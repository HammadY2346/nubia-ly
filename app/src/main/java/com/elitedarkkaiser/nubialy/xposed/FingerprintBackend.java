package com.elitedarkkaiser.nubialy.xposed;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import de.robv.android.xposed.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
/** Appearance of the vendor's sensorView background only; never hide its touch view/window. */
final class FingerprintBackend {
    private static final String OWNER="com.zte.feature.fingerprintlcd.FloatWindowSensorView",FEATURE="com.zte.feature.fingerprintlcd.FingerprintUnderLcdFeature";
    private static final Map<View,WeakReference<Object>> owners=new WeakHashMap<>();
    private static final Map<ImageView,State> states=new WeakHashMap<>();
    private static final List<XC_MethodHook.Unhook> hooks=new ArrayList<>();
    private static boolean attempted,ready,active,writing,failed;private static int style,captured,restored;private static boolean hidden;private static long generation;
    private static final class State {final WeakReference<View> owner;Drawable stock,custom;State(View owner,ImageView image){this.owner=new WeakReference<>(owner);stock=image.getBackground();}}
    private record Frame(long generation,State state){}
    static void install(ClassLoader loader){if(attempted)return;attempted=true;List<XC_MethodHook.Unhook> pending=new ArrayList<>();try{
        Class<?> owner=XposedHelpers.findClass(OWNER,loader),feature=XposedHelpers.findClass(FEATURE,loader);
        if(owner.getSuperclass()!=View.class||owner.getDeclaredField("sensorView").getType()!=ImageView.class||owner.getDeclaredField("enrollState").getType()!=boolean.class||feature.getDeclaredMethod("isSensorUIKeyguard").getReturnType()!=boolean.class||feature.getDeclaredMethod("isSensorUIAODStatus").getReturnType()!=boolean.class||feature.getDeclaredMethod("getKeyguardColor").getReturnType()!=int.class)throw new IllegalStateException();
        XC_MethodHook observer=new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(p.hasThrowable()||!main())return;try{View root=(View)p.thisObject;for(Object arg:p.args)if(arg!=null&&arg.getClass().getName().equals(FEATURE))owners.put(root,new WeakReference<>(arg));if(active)refresh();}catch(Throwable failure){if(active)failed=true;}}};
        for(Method m:owner.getDeclaredMethods())if(Set.of("initSensorView","setSensorViewByStatus","setSensorViewIconForRedmagic","setSensorViewVisible","setEnrollState").contains(m.getName()))pending.add(XposedBridge.hookMethod(m,observer));
        // Sensor image is in a separate window. Its owner is not an ancestor.
        pending.add(XposedBridge.hookMethod(View.class.getDeclaredMethod("onDetachedFromWindow"),new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(main()&&p.thisObject instanceof ImageView image)restore(image);}}));
        ready=true;
    }catch(Throwable failure){for(var h:pending)try{h.unhook();}catch(Throwable ignored){}ready=false;}}
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    private static boolean eligible(View owner){WeakReference<Object> ref=owners.get(owner);Object feature=ref==null?null:ref.get();return feature!=null&&Boolean.FALSE.equals(XposedHelpers.getObjectField(owner,"enrollState"))&&Boolean.TRUE.equals(XposedHelpers.callMethod(feature,"isSensorUIKeyguard"))&&Boolean.FALSE.equals(XposedHelpers.callMethod(feature,"isSensorUIAODStatus"));}
    static void begin(int selection,boolean hide){if(selection<0||selection>4||!main())throw new IllegalArgumentException();if(!end())throw new IllegalStateException("Fingerprint restoration incomplete");style=selection;hidden=hide;captured=restored=0;if(style==0&&!hidden)return;if(!ready||failed)throw new IllegalStateException("Fingerprint source unavailable");
        XC_MethodHook background=new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(!active||writing)return;State state=states.get(p.thisObject);if(state!=null)p.setObjectExtra("nubia-ly:fingerprint",new Frame(generation,state));}
            @Override protected void afterHookedMethod(MethodHookParam p){Object extra=p.getObjectExtra("nubia-ly:fingerprint");if(!(extra instanceof Frame frame)||!active||writing||frame.generation()!=generation||states.get(p.thisObject)!=frame.state())return;try{if(p.hasThrowable()||!main())throw new IllegalStateException();ImageView image=(ImageView)p.thisObject;frame.state().stock=image.getBackground();View owner=frame.state().owner.get();if(owner==null||!eligible(owner))restore(image);else paint(image,frame.state());}catch(Throwable failure){failed=true;}}
        };
        try{hooks.add(XposedHelpers.findAndHookMethod(View.class,"setBackground",Drawable.class,background));active=true;refresh();if(failed)throw new IllegalStateException();}catch(Throwable failure){end();throw new IllegalStateException("Fingerprint appearance unavailable",failure);}}
    private static void background(ImageView image,Drawable value){int l=image.getPaddingLeft(),t=image.getPaddingTop(),r=image.getPaddingRight(),b=image.getPaddingBottom();image.setBackground(value);image.setPadding(l,t,r,b);}
    private static void paint(ImageView image,State state){writing=true;try{Drawable selected=hidden?null:state.custom;background(image,selected);if(image.getBackground()!=selected)throw new IllegalStateException("Fingerprint background apply incomplete");}finally{writing=false;}}
    static void refresh(){if(!active||failed||!main())return;try{
        for(ImageView image:new ArrayList<>(states.keySet())){View owner=states.get(image).owner.get();if(owner==null||!image.isAttachedToWindow()||!eligible(owner)||XposedHelpers.getObjectField(owner,"sensorView")!=image)restore(image);}
        for(View owner:new ArrayList<>(owners.keySet())){Object value=XposedHelpers.getObjectField(owner,"sensorView");if(!(value instanceof ImageView image)||!image.isAttachedToWindow()||!eligible(owner))continue;State state=states.get(image);if(state!=null&&state.owner.get()!=owner)throw new IllegalStateException("Shared sensor image");if(state==null){state=new State(owner,image);states.put(image,state);captured++;}Object feature=owners.get(owner).get();int color=((Number)XposedHelpers.callMethod(feature,"getKeyguardColor")).intValue();state.custom=style==0?state.stock:new FingerprintGlyphDrawable(style,color);paint(image,state);}
    }catch(Throwable failure){failed=true;}}
    private static boolean restore(ImageView image){State state=states.get(image);if(state==null)return true;writing=true;try{background(image,state.stock);if(image.getBackground()!=state.stock)throw new IllegalStateException();states.remove(image);restored++;return true;}catch(Throwable failure){failed=true;return false;}finally{writing=false;}}
    static boolean end(){generation++;active=false;for(ImageView image:new ArrayList<>(states.keySet()))restore(image);for(var hook:hooks)try{hook.unhook();}catch(Throwable failure){failed=true;}hooks.clear();return !failed&&states.isEmpty();}
    static boolean healthy(){return !failed;}
    static String status(){return "Fingerprint: "+(hidden?"Hidden":FingerprintGlyphDrawable.NAMES[style])+"; captured: "+captured+"; restored: "+restored+"; remaining: "+states.size()+(failed?"; restoration required.":".");}
}
