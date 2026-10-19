package com.hammad.rmtweaks.xposed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.hammad.rmtweaks.Features;

import java.io.InputStream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/** com.android.systemui — charging animation set / custom frames / play on every screen-on. */
final class SystemUiHooks {
    private SystemUiHooks() {}

    private static final String CHARGE_UTILS = "com.zte.feature.charging.ChargeUtils";
    private static final String CHARGING_FEATURE = "com.zte.feature.charging.ChargingFeature";
    private static final String OPERATOR_UTILS = "com.zte.customize.OperatorUtils";
    private static final Uri PROVIDER = Uri.parse("content://com.hammad.rmtweaks.log");

    private static int customCount = -1;
    private static long customCountAt;

    static void install(ClassLoader cl) {
        chargingSet(cl);
        everyScreenOn(cl);
    }

    private static int set() { return Cfg.choice(Features.CHARGE_SET); }

    /** Number of imported frames, cached for 5 s (asked from the module app through its provider). */
    private static int customFrames(Context c) {
        long now = System.currentTimeMillis();
        if (customCount >= 0 && now - customCountAt < 5000) return customCount;
        try {
            Bundle b = c.getContentResolver().call(PROVIDER, "frame_count", null, null);
            customCount = b == null ? 0 : b.getInt("count", 0);
        } catch (Throwable t) { customCount = 0; Lx.w("charging: frame count unavailable " + t); }
        customCountAt = now;
        return customCount;
    }

    private static void chargingSet(ClassLoader cl) {
        final Class<?> cu = H.cls(cl, CHARGE_UTILS);
        if (cu == null) return;

        // Wukong 3-stage collab animation = firmware's IP_MK_CN variant flag (static, read at class init).
        if (set() == 1) {
            Class<?> ou = H.cls(cl, OPERATOR_UTILS);
            if (ou != null) {
                try { XposedHelpers.setStaticBooleanField(ou, "IS_IP_MK_CN", true); Lx.i("charging: IS_IP_MK_CN forced on"); }
                catch (Throwable t) { Lx.e("charging: cannot set IS_IP_MK_CN", t); }
            }
        }

        // getCurrentDrawable(plugType, usingTheme, isRedMagic, fast) -> choose the frame table
        H.hook(H.find(cu, "getCurrentDrawable", int.class, boolean.class, boolean.class, boolean.class), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                int s = set();
                if (s <= 1) return;
                boolean fast = Boolean.TRUE.equals(p.args[3]);
                String field = null;
                switch (s) {
                    case 2: field = fast ? "RedMagic_Fast_Drawable" : "RedMagic_Drawable"; break;
                    case 3: field = fast ? "RedMagic_Wireless_Fast_Drawable" : "RedMagic_Wireless_Drawable"; break;
                    case 4: field = fast ? "Fast_Drawable" : "Drawable"; break;
                    case 5: {
                        Context c = de.robv.android.xposed.AndroidAppHelper.currentApplication();
                        int n = c == null ? 0 : customFrames(c);
                        if (n <= 0) { Lx.w("charging: custom set chosen but no frames imported"); return; }
                        p.setResult(new int[n]);   // length drives the frame count; values unused (we serve the bitmaps)
                        return;
                    }
                }
                try {
                    Object arr = XposedHelpers.getStaticObjectField(cu, field);
                    if (arr instanceof int[]) p.setResult(arr);
                } catch (Throwable t) { Lx.e("charging: table " + field, t); }
            }
        });

        // getChargeBitmapByIndex(i, res, configCustom, table, options, fast, plugType) -> serve imported frames
        H.hook(H.find(cu, "getChargeBitmapByIndex", int.class, android.content.res.Resources.class, boolean.class, int[].class, BitmapFactory.Options.class, boolean.class, int.class), new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                if (set() != 5) return;
                Context c = de.robv.android.xposed.AndroidAppHelper.currentApplication();
                if (c == null) return;
                int i = (Integer) p.args[0];
                try (InputStream in = c.getContentResolver().openInputStream(Uri.withAppendedPath(PROVIDER, "charging/" + i))) {
                    if (in == null) return;
                    Bitmap b = BitmapFactory.decodeStream(in, null, (BitmapFactory.Options) p.args[4]);
                    if (b != null) p.setResult(b);
                } catch (Throwable t) {
                    Lx.w("charging: frame " + i + " failed: " + t);
                }
            }
        });
        // Custom frames carry no middle/bottom layers.
        XC_MethodHook noLayers = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) { if (set() == 5) p.setResult(null); }
        };
        H.hook(H.find(cu, "getCurrentMiddleDrawable", boolean.class), noLayers);
        Lx.i("SystemUI: charging hooks ready (set=" + set() + ")");
    }

    private static void everyScreenOn(ClassLoader cl) {
        Class<?> cf = H.cls(cl, CHARGING_FEATURE);
        if (cf == null) return;
        H.hook(H.find(cf, "onFinishedWakingUp"), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                if (!Cfg.on(Features.CHARGE_EVERY_LOCK)) return;
                try {
                    Object f = p.thisObject;
                    boolean charging = Boolean.TRUE.equals(XposedHelpers.callMethod(f, "isCharging"));
                    boolean showing = Boolean.TRUE.equals(XposedHelpers.callMethod(f, "isKeyguardViewShowing"));
                    boolean anim = Boolean.TRUE.equals(XposedHelpers.callMethod(f, "isAnimationShowing"));
                    if (charging && showing && !anim) {
                        XposedHelpers.callMethod(f, "startChargingAnimation", 300L);
                        Lx.i("charging: replayed on wake-up");
                    }
                } catch (Throwable t) { Lx.e("charging: wake-up replay", t); }
            }
        });
    }
}
