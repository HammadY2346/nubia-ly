package com.elitedarkkaiser.nubialy.xposed;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import com.elitedarkkaiser.nubialy.SystemIconPacks;
import com.elitedarkkaiser.nubialy.SystemIconPackPolicy;
import de.robv.android.xposed.*;
import java.util.*;

/** Draw-only, scoped replacement. Native drawables and their state are never mutated. */
final class SystemIconPackBackend {
    private record Source(Drawable drawable,String key){}
    private record Art(String key,Drawable drawable){}
    private static final Map<Drawable,String> drawableKeys=new WeakHashMap<>();
    private static final Map<Drawable.ConstantState,String> stateKeys=new WeakHashMap<>();
    private static final Map<ImageView,Source> sources=new WeakHashMap<>();
    private static final Map<ImageView,Art> artwork=new WeakHashMap<>();
    private static final Map<ImageView,Boolean> targets=new WeakHashMap<>();
    private static final Map<Integer,String> resourceKeys=new HashMap<>();
    private static final List<XC_MethodHook.Unhook> draws=new ArrayList<>();
    private static java.lang.reflect.Method drawMethod;
    private static int pack,drawCount;private static boolean ready,active;
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    private static String resourceKey(Resources res,int id){
        // Memoize only System UI's stable resource table, never IDs from arbitrary app Resources.
        try{String pkg=res.getResourcePackageName(id);if(!pkg.equals("com.android.systemui")&&!pkg.equals("android"))return null;
            if(resourceKeys.containsKey(id))return resourceKeys.get(id);
            String key=SystemIconPackPolicy.key(res.getResourceEntryName(id));if(resourceKeys.size()<4096)resourceKeys.put(id,key);return key;
        }catch(Throwable unavailable){return null;}
    }
    private static void remember(Drawable d,String key){if(d==null||key==null)return;drawableKeys.put(d,key);Drawable.ConstantState cs=d.getConstantState();if(cs!=null)stateKeys.put(cs,key);}
    private static String key(Drawable d){if(d==null)return null;String k=drawableKeys.get(d);return k!=null?k:stateKeys.get(d.getConstantState());}
    private static boolean scope(ImageView image){
        try{String id=image.getResources().getResourceEntryName(image.getId());if(id.equals("wifi_signal")||id.equals("mobile_signal"))return false;}catch(Throwable absent){}
        View v=image;boolean qs=false;for(int i=0;i<18&&v!=null;i++){
        String name=v.getClass().getName();if(name.equals("com.android.systemui.statusbar.phone.NotificationIconContainer"))return false;
        if(name.equals("com.android.systemui.qs.tileimpl.QSIconViewImpl"))qs=true;
        if(name.equals("com.android.systemui.statusbar.phone.PhoneStatusBarView"))return true;
        if(qs)return true;
        v=v.getParent() instanceof View parent?parent:null;
    }return false;}
    static void observe(View view){if(!main()||!(view instanceof ImageView image))return;targets.remove(image);sources.remove(image);artwork.remove(image);if(scope(image)){targets.put(image,true);if(active)image.invalidate();}}
    static void install(ClassLoader loader){try{
        XposedBridge.hookMethod(Resources.class.getDeclaredMethod("getDrawableForDensity",int.class,int.class,Resources.Theme.class),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(main()&&!p.hasThrowable()&&p.getResult() instanceof Drawable d)remember(d,resourceKey((Resources)p.thisObject,(Integer)p.args[0]));}});
        XposedBridge.hookMethod(ImageView.class.getDeclaredMethod("setImageResource",int.class),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(!main()||p.hasThrowable())return;ImageView image=(ImageView)p.thisObject;String k=resourceKey(image.getResources(),(Integer)p.args[0]);if(k!=null){remember(image.getDrawable(),k);sources.put(image,new Source(image.getDrawable(),k));}}});
        drawMethod=ImageView.class.getDeclaredMethod("onDraw",Canvas.class);ready=true;
    }catch(Throwable unavailable){ready=false;}}
    static void begin(int value){if(!main()||!SystemIconPacks.valid(value))throw new IllegalArgumentException();end();pack=value;drawCount=0;if(value==0)return;if(!ready)throw new IllegalStateException("Icon pack observer unavailable");
        draws.add(XposedBridge.hookMethod(drawMethod,new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){
            if(!active||!main())return;ImageView image=(ImageView)p.thisObject;if(!image.isAttachedToWindow())return;
            Boolean target=targets.get(image);if(target==null){target=scope(image);targets.put(image,target);}if(!target)return;
            Drawable nativeIcon=image.getDrawable();if(nativeIcon==null)return;
            Source source=sources.get(image);if(source==null||source.drawable()!=nativeIcon){String k=key(nativeIcon);source=new Source(nativeIcon,k);sources.put(image,source);}if(source.key()==null)return;
            try{Art cached=artwork.get(image);if(cached==null||!cached.key().equals(source.key())){Drawable d=SystemIconPacks.artwork(pack,source.key());if(d==null)return;cached=new Art(source.key(),d);artwork.put(image,cached);}
                Drawable d=cached.drawable();d.setColorFilter(null);if(image.getImageTintList()!=null)d.setTint(image.getImageTintList().getColorForState(image.getDrawableState(),image.getImageTintList().getDefaultColor()));else{ColorFilter filter=image.getColorFilter();if(filter==null)filter=nativeIcon.getColorFilter();if(filter==null)return;d.setTintList(null);d.setColorFilter(filter);}
                Rect bounds=nativeIcon.getBounds();if(bounds.isEmpty())return;d.setBounds(bounds);d.setAlpha(image.getImageAlpha());d.setState(image.getDrawableState());d.setLayoutDirection(image.getLayoutDirection());
                Canvas canvas=(Canvas)p.args[0];int save=canvas.save();try{canvas.translate(image.getPaddingLeft(),image.getPaddingTop());canvas.concat(image.getImageMatrix());d.draw(canvas);}finally{canvas.restoreToCount(save);}drawCount++;p.setResult(null);
            }catch(Throwable unsupported){artwork.remove(image);/* Isolated artwork failure keeps the native icon. */}
        }}));active=true;invalidate();
    }
    private static void invalidate(){for(ImageView v:new ArrayList<>(targets.keySet()))if(Boolean.TRUE.equals(targets.get(v)))v.invalidate();}
    static boolean end(){active=false;for(var h:draws)h.unhook();draws.clear();artwork.clear();invalidate();return true;}
    static boolean healthy(){return true;}
    static String status(){return "Icon pack: "+SystemIconPacks.NAMES[pack]+"; decorated draws: "+drawCount+"; scoped targets: "+targets.values().stream().filter(Boolean.TRUE::equals).count()+".";}
}
