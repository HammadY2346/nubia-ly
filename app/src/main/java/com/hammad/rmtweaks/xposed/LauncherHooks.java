package com.hammad.rmtweaks.xposed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hammad.rmtweaks.Features;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * com.zte.mifavor.launcher — config flags + horizontal paged app drawer.
 *
 * Paged drawer approach: the stock drawer is a vertical GridLayoutManager over a flat list of icon
 * items. We swap in the launcher's own GridLayoutManager in HORIZONTAL orientation (span = rows),
 * remap adapter positions so pages fill row by row, pad the last page, and snap to page edges.
 */
final class LauncherHooks {
    private LauncherHooks() {}

    private static final String RV = "androidx.recyclerview.widget.RecyclerView";
    private static final String GLM = "androidx.recyclerview.widget.GridLayoutManager";
    private static final String AARV = "com.android.launcher3.allapps.AllAppsRecyclerView";
    private static final String SEARCH_RV = "com.android.launcher3.allapps.SearchRecyclerView";
    private static final String MIFAVOR_CONTEXT = "com.android.launcher3.C1308v4";       // MifavorContext (obfuscated name, fw 11.0.11)
    private static final String RESOURCE_CONTEXT = "com.android.launcher3.resource.Q";   // ResourceContext

    /** Per-drawer paging state. */
    static final class State {
        final WeakReference<View> recycler;
        int cols = 5, rows = 6;
        Object adapter;
        int[] iconPositions = new int[0];   // visual index -> original adapter position
        int cachedCount = -1;
        boolean snapping;
        State(View rv) { recycler = new WeakReference<>(rv); }
        int page() { return cols * rows; }
        int padded() { int k = iconPositions.length; int p = page(); return p == 0 ? k : ((k + p - 1) / p) * p; }
        /** GLM (column-major) position -> visual (row-major, paged) index. */
        int visual(int pos) {
            int c = pos / rows, r = pos % rows;
            int page = c / cols, cp = c % cols;
            return page * page() + r * cols + cp;
        }
    }

    private static final Map<Object, State> byRecycler = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Object, State> byAdapter = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Set<Class<?>> hookedAdapters = new HashSet<>();
    private static Method origItemType, origItemCount;

    static void install(ClassLoader cl) {
        configFlags(cl);
        pagedDrawer(cl);
    }

    // ------------------------------------------------------------------ config flags

    private static Integer override(String key) {
        switch (key) {
            case "config_support_rotation":
            case "config_rotation_default_value":
                return Cfg.on(Features.LAUNCHER_ROTATION) ? 1 : null;
            case "config_num_columns": { int i = Cfg.choice(Features.LAUNCHER_COLS); return i > 0 && i < Features.GRID_COLS.length ? Integer.valueOf(Features.GRID_COLS[i]) : null; }
            case "config_num_rows": { int i = Cfg.choice(Features.LAUNCHER_ROWS); return i > 0 && i < Features.GRID_ROWS.length ? Integer.valueOf(Features.GRID_ROWS[i]) : null; }
            case "config_num_hotseat_icons": { int i = Cfg.choice(Features.LAUNCHER_HOTSEAT); return i > 0 && i < Features.HOTSEAT.length ? Integer.valueOf(Features.HOTSEAT[i]) : null; }
            case "config_show_hotseat_text": return Cfg.on(Features.LAUNCHER_HOTSEAT_TEXT) ? 1 : null;
            case "config_two_line_title": return Cfg.on(Features.LAUNCHER_TWO_LINE) ? 1 : null;
            case "config_circle_scroll_status":
            case "config_circle_scroll_status_internal":
            case "config_show_circle_scroll_switch":
                return Cfg.on(Features.LAUNCHER_CIRCLE) ? 1 : null;
            case "config_show_fast_scroller_bar": return Cfg.on(Features.LAUNCHER_NO_FASTSCROLL) ? 0 : null;
            default: return null;
        }
    }

    private static void configFlags(ClassLoader cl) {
        XC_MethodHook cb = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                Object key = p.args.length >= 2 ? p.args[1] : p.args[0];
                if (!(key instanceof String)) return;
                Integer v = override((String) key);
                if (v != null) { p.setResult(v); }
            }
        };
        Class<?> mc = H.cls(cl, MIFAVOR_CONTEXT);
        if (mc != null) H.hook(H.findBySig(mc, int.class, Context.class, String.class, int.class), cb);
        Class<?> rc = H.cls(cl, RESOURCE_CONTEXT);
        if (rc != null) H.hook(H.find(rc, "o", String.class), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                Integer v = override(String.valueOf(p.args[0]));
                if (v != null) p.setResult(v);
            }
        });
        Lx.i("Launcher: config-flag hooks ready");
    }

    // ------------------------------------------------------------------ paged drawer

    private static void pagedDrawer(final ClassLoader cl) {
        final Class<?> rv = H.cls(cl, RV);
        final Class<?> glm = H.cls(cl, GLM);
        final Class<?> aarv = H.cls(cl, AARV);
        if (rv == null || glm == null || aarv == null) return;
        final Class<?> searchRv = XposedHelpers.findClassIfExists(SEARCH_RV, cl);
        final Class<?> lmClass = XposedHelpers.findClass(RV + "$LayoutManager", cl);
        final Class<?> adapterClass = XposedHelpers.findClass(RV + "$Adapter", cl);
        final Class<?> vhClass = XposedHelpers.findClass(RV + "$ViewHolder", cl);
        final Constructor<?> glmCtor;
        try { glmCtor = glm.getConstructor(Context.class, int.class, int.class, boolean.class); }
        catch (NoSuchMethodException e) { Lx.e("GridLayoutManager ctor", e); return; }

        // 1. swap the layout manager
        H.hook(H.find(rv, "setLayoutManager", lmClass), new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                try {
                    View self = (View) p.thisObject;
                    if (!aarv.isInstance(self) || (searchRv != null && searchRv.isInstance(self))) return;
                    Object lm = p.args[0];
                    if (!Cfg.on(Features.LAUNCHER_PAGED)) { byRecycler.remove(self); return; }
                    if (lm == null || !glm.isInstance(lm)) return;
                    int orientation = (Integer) XposedHelpers.callMethod(lm, "getOrientation");
                    if (orientation != 1 /* VERTICAL */) return;   // already ours
                    int cols = (Integer) XposedHelpers.callMethod(lm, "getSpanCount");
                    State st = new State(self);
                    st.cols = Math.max(1, cols);
                    int ri = Cfg.choice(Features.LAUNCHER_PAGED_ROWS);
                    st.rows = (ri > 0 && ri < Features.PAGED_ROWS.length) ? Integer.parseInt(Features.PAGED_ROWS[ri]) : 6;
                    Object ours = glmCtor.newInstance(self.getContext(), st.rows, 0 /* HORIZONTAL */, false);
                    p.args[0] = ours;
                    byRecycler.put(self, st);
                    Lx.i("Drawer: paged layout " + st.cols + "x" + st.rows + " installed");
                } catch (Throwable t) { Lx.e("Drawer: setLayoutManager", t); }
            }
        });

        // 2. remember the adapter and hook its class once
        H.hook(H.find(rv, "setAdapter", adapterClass), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                try {
                    State st = byRecycler.get(p.thisObject);
                    Object ad = p.args[0];
                    if (st == null || ad == null) return;
                    st.adapter = ad;
                    st.cachedCount = -1;
                    byAdapter.put(ad, st);
                    hookAdapterClass(ad.getClass(), vhClass);
                } catch (Throwable t) { Lx.e("Drawer: setAdapter", t); }
            }
        });

        // 3. page snapping: fling + settle
        H.hook(H.find(rv, "fling", int.class, int.class), new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                State st = byRecycler.get(p.thisObject);
                if (st == null) return;
                try {
                    View self = (View) p.thisObject;
                    int vx = (Integer) p.args[0];
                    int pageW = pageWidth(self, st);
                    if (pageW <= 0) return;
                    int offset = (Integer) XposedHelpers.callMethod(self, "computeHorizontalScrollOffset");
                    int range = (Integer) XposedHelpers.callMethod(self, "computeHorizontalScrollRange");
                    int maxPage = Math.max(0, (range - 1) / pageW);
                    int cur = Math.round((float) offset / pageW);
                    int target = cur;
                    if (vx > 600) target = (int) Math.floor((float) offset / pageW) + 1;
                    else if (vx < -600) target = (int) Math.ceil((float) offset / pageW) - 1;
                    target = Math.max(0, Math.min(maxPage, target));
                    st.snapping = true;
                    XposedHelpers.callMethod(self, "smoothScrollBy", target * pageW - offset, 0);
                    p.setResult(true);
                } catch (Throwable t) { Lx.e("Drawer: fling", t); }
            }
        });
        H.hook(H.find(aarv, "onScrollStateChanged", int.class), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                State st = byRecycler.get(p.thisObject);
                if (st == null) return;
                int state = (Integer) p.args[0];
                if (state != 0 /* IDLE */) return;
                try {
                    View self = (View) p.thisObject;
                    int pageW = pageWidth(self, st);
                    if (pageW <= 0) return;
                    int offset = (Integer) XposedHelpers.callMethod(self, "computeHorizontalScrollOffset");
                    int target = Math.round((float) offset / pageW) * pageW;
                    int dx = target - offset;
                    if (Math.abs(dx) > 2 && !st.snapping) {
                        st.snapping = true;
                        XposedHelpers.callMethod(self, "smoothScrollBy", dx, 0);
                    } else {
                        st.snapping = false;
                    }
                } catch (Throwable t) { Lx.e("Drawer: settle", t); }
            }
        });
        Lx.i("Launcher: paged drawer hooks ready");
    }

    /** Page width = cols * cell width, so snapping and cell sizing never drift apart. */
    private static int pageWidth(View rv, State st) {
        int w = rv.getWidth() - rv.getPaddingLeft() - rv.getPaddingRight();
        if (w <= 0) return 0;
        return st.cols * (w / st.cols);
    }

    /** Hook getItemCount / getItemViewType / onBindViewHolder on the concrete adapter class (once per class). */
    private static synchronized void hookAdapterClass(Class<?> adapterCls, Class<?> vhClass) {
        if (hookedAdapters.contains(adapterCls)) return;
        hookedAdapters.add(adapterCls);
        Method count = H.find(adapterCls, "getItemCount");
        Method type = H.find(adapterCls, "getItemViewType", int.class);
        Method bind = null;
        for (Class<?> k = adapterCls; k != null && bind == null; k = k.getSuperclass()) {
            Method best = null; int score = -1;
            for (Method m : k.getDeclaredMethods()) {
                Class<?>[] pt = m.getParameterTypes();
                if (m.isBridge() || m.isSynthetic() || pt.length != 2 || pt[1] != int.class || m.getReturnType() != void.class) continue;
                if (!vhClass.isAssignableFrom(pt[0])) continue;
                int sc = m.getName().equals("onBindViewHolder") ? 3 : (!java.lang.reflect.Modifier.isPrivate(m.getModifiers()) && m.getName().length() <= 2 ? 2 : 0);
                if (sc > score) { score = sc; best = m; }
            }
            if (best != null && score > 0) { best.setAccessible(true); bind = best; }
        }
        origItemCount = count; origItemType = type;
        if (count == null || type == null || bind == null) { Lx.w("Drawer: adapter methods not found on " + adapterCls.getName()); return; }

        H.hook(count, new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                State st = byAdapter.get(p.thisObject);
                if (st == null || !Cfg.on(Features.LAUNCHER_PAGED)) return;
                refresh(st, p.thisObject, (Integer) p.getResult());
                p.setResult(st.padded());
            }
        });
        H.hook(type, new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                State st = byAdapter.get(p.thisObject);
                if (st == null || !Cfg.on(Features.LAUNCHER_PAGED)) return;
                int pos = (Integer) p.args[0];
                int v = st.visual(pos);
                if (v >= st.iconPositions.length) { p.setResult(2); return; }   // padding cell -> icon type, hidden at bind
                p.args[0] = st.iconPositions[v];
            }
        });
        H.hook(bind, new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                State st = byAdapter.get(p.thisObject);
                if (st == null || !Cfg.on(Features.LAUNCHER_PAGED)) return;
                int pos = (Integer) p.args[1];
                View item = (View) XposedHelpers.getObjectField(p.args[0], "itemView");
                View rv = st.recycler.get();
                if (rv != null && item.getLayoutParams() != null) {
                    int w = rv.getWidth() - rv.getPaddingLeft() - rv.getPaddingRight();
                    if (w <= 0) w = rv.getResources().getDisplayMetrics().widthPixels;
                    ViewGroup.LayoutParams lp = item.getLayoutParams();
                    lp.width = Math.max(1, w / st.cols);
                }
                int v = st.visual(pos);
                if (v >= st.iconPositions.length) { item.setVisibility(View.INVISIBLE); p.setResult(null); return; }
                p.args[1] = st.iconPositions[v];
            }
            @Override protected void afterHookedMethod(MethodHookParam p) {
                State st = byAdapter.get(p.thisObject);
                if (st == null || !Cfg.on(Features.LAUNCHER_PAGED)) return;
                try { ((View) XposedHelpers.getObjectField(p.args[0], "itemView")).setVisibility(View.VISIBLE); } catch (Throwable ignored) { }
            }
        });
        Lx.i("Drawer: adapter " + adapterCls.getName() + " hooked (bind=" + bind.getName() + ")");
    }

    /** Rebuild the icon index when the underlying list size changes. */
    private static void refresh(State st, Object adapter, int realCount) {
        if (st.cachedCount == realCount) return;
        try {
            int[] tmp = new int[realCount];
            int n = 0;
            for (int i = 0; i < realCount; i++) {
                int t = (Integer) XposedBridge.invokeOriginalMethod(origItemType, adapter, new Object[]{i});
                if (t == 2) tmp[n++] = i;
            }
            int[] out = new int[n];
            System.arraycopy(tmp, 0, out, 0, n);
            st.iconPositions = out;
            st.cachedCount = realCount;
            Lx.i("Drawer: " + n + " icons of " + realCount + " items, " + ((n + st.page() - 1) / Math.max(1, st.page())) + " pages");
        } catch (Throwable t) { Lx.e("Drawer: refresh", t); }
    }
}
