package com.elitedarkkaiser.nubialy.xposed;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import com.elitedarkkaiser.nubialy.SurfaceStyle;
import com.elitedarkkaiser.nubialy.StatusBarStyle;
import de.robv.android.xposed.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;
/** Scoped view transforms only. No tile state, lock-screen security, text or layout model writes. */
final class SurfaceBackend {
    private static final String WIDE="com.zte.qs.tileimpl.QSTileViewWide",CIRCLE="com.zte.qs.tileimpl.QSTileViewCircle",LOCK="cn.nubia.lockscreen.NubiaKeyguardStatusView";
    private static final Map<ViewGroup,Boolean> roots=new WeakHashMap<>();
    private static final Map<View,State> states=new WeakHashMap<>();
    private static final List<XC_MethodHook.Unhook> hooks=new ArrayList<>();
    private static boolean attempted,ready,active,writing;private static volatile boolean failed;private static long generation;
    private static SurfaceStyle style=SurfaceStyle.STOCK;private static int captured,restored;private static String message="Surface observers unavailable.";
    private record Target(ViewGroup owner,int group){}
    private record Frame(long generation,State state){}
    private static final class State {
        final WeakReference<ViewGroup> owner;final int group,scale,opacity,position;final boolean hide;
        float x,y,a,tx,ty,r;int visibility;
        State(View v,Target target){owner=new WeakReference<>(target.owner());group=target.group();int appearance=style.appearance(group);scale=StatusBarStyle.indicatorScale(appearance);opacity=StatusBarStyle.indicatorOpacity(appearance);position=style.position(group);hide=style.hide(group);
            x=v.getScaleX();y=v.getScaleY();a=v.getAlpha();tx=v.getTranslationX();ty=v.getTranslationY();r=v.getRotation();visibility=v.getVisibility();}
    }
    static synchronized void install(ClassLoader loader){
        if(attempted)return;attempted=true;List<XC_MethodHook.Unhook> observers=new ArrayList<>();
        String stage="class lookup";
        try{
            Class<?> wide=XposedHelpers.findClass(WIDE,loader),circle=XposedHelpers.findClass(CIRCLE,loader),lock=XposedHelpers.findClass(LOCK,loader);
            stage="circle background getter";
            if(circle.getMethod("getColorBackgroundDrawable").getReturnType()!=android.graphics.drawable.GradientDrawable.class)throw new IllegalStateException("Circle background signature");
            stage="tile label descriptors";
            for(Class<?> tile:new Class<?>[]{wide,circle})if(!tile.getSuperclass().getName().equals("com.zte.qs.tileimpl.QSTileViewBase")||!hasTextGetter(tile,"getLabel")||!hasTextGetter(tile,"getSecondaryLabel"))throw new IllegalStateException("Tile signatures");
            stage="keyguard fields";
            if(lock.getSuperclass()!=GridLayout.class||lock.getDeclaredField("mClockView").getType()!=TextClock.class||!TextView.class.isAssignableFrom(lock.getDeclaredField("mDateView").getType())||lock.getDeclaredField("mDarkAmount").getType()!=float.class||lock.getDeclaredField("mPulsing").getType()!=boolean.class)throw new IllegalStateException("Keyguard signatures");
            stage="view lifecycle hooks";
            observers.add(XposedBridge.hookMethod(View.class.getDeclaredMethod("onAttachedToWindow"),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(ready&&!p.hasThrowable()&&main()){try{target((View)p.thisObject);if(active)track((View)p.thisObject);}catch(Throwable unavailable){if(active)fail(unavailable);}}}}));
            observers.add(XposedBridge.hookMethod(View.class.getDeclaredMethod("onDetachedFromWindow"),new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(!active||!main())return;View view=(View)p.thisObject;restore(view);if(view instanceof ViewGroup root){QsShapeBackend.release(root);for(View v:new ArrayList<>(states.keySet()))if(states.get(v).owner.get()==root)restore(v);roots.remove(root);}}}));
            stage="keyguard pulsing hook";
            observers.add(XposedBridge.hookMethod(lock.getDeclaredMethod("setPulsing",boolean.class),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(active&&main())refresh();}}));
            ready=true;message="Surface observers ready.";
        }catch(Throwable unavailable){for(var h:observers)try{h.unhook();}catch(Throwable ignored){}message="Surface source unavailable at "+stage+" ("+unavailable.getClass().getSimpleName()+").";}
    }
    private static boolean hasTextGetter(Class<?> owner,String name){
        // NX809J publishes both View bridge and TextView getter descriptors. Android's
        // name-only getMethod lookup may return the bridge; require the actual descriptor.
        for(Method method:owner.getMethods())if(method.getName().equals(name)&&method.getParameterCount()==0&&method.getReturnType()==TextView.class)return true;
        return false;
    }
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    private static Target target(View view){
        if(!(view instanceof TextView))return null;View current=view;
        for(int depth=0;depth<24&&current!=null;depth++){
            String name=current.getClass().getName();
            if(current instanceof ViewGroup root&&(name.equals(WIDE)||name.equals(CIRCLE)||name.equals(LOCK))){
                roots.put(root,true);
                if(name.equals(LOCK)){
                    if(!Boolean.FALSE.equals(XposedHelpers.getObjectField(root,"mPulsing"))||((Number)XposedHelpers.getObjectField(root,"mDarkAmount")).floatValue()!=0)return null;
                    if(XposedHelpers.getObjectField(root,"mClockView")==view)return new Target(root,2);
                    if(XposedHelpers.getObjectField(root,"mDateView")==view)return new Target(root,3);
                }else{
                    if(XposedHelpers.callMethod(root,"getLabel")==view)return new Target(root,0);
                    if(XposedHelpers.callMethod(root,"getSecondaryLabel")==view)return new Target(root,1);
                }return null;
            }
            ViewParent parent=current.getParent();current=parent instanceof View v?v:null;
        }return null;
    }
    static void begin(SurfaceStyle selection){
        if(selection==null||!selection.valid()||!main())throw new IllegalArgumentException();if(!end())throw new IllegalStateException("Surface restoration incomplete");style=selection;captured=restored=0;if(!style.any()){message="Stock Quick Settings and lock-screen text appearance.";return;}
        if(!ready||failed)throw new IllegalStateException(message);
        XC_MethodHook callback=new XC_MethodHook(){
            @Override protected void beforeHookedMethod(MethodHookParam p){if(!active||writing)return;State state=states.get(p.thisObject);if(state==null)return;if(!main()){failed=true;return;}p.setObjectExtra("nubia-ly:surface",new Frame(generation,state));}
            @Override protected void afterHookedMethod(MethodHookParam p){Object extra=p.getObjectExtra("nubia-ly:surface");if(!(extra instanceof Frame frame)||!active||writing||frame.generation()!=generation||states.get(p.thisObject)!=frame.state())return;View v=(View)p.thisObject;State s=frame.state();
                try{if(p.hasThrowable())throw new IllegalStateException();switch(((Method)p.method).getName()){case "setScaleX"->s.x=v.getScaleX();case "setScaleY"->s.y=v.getScaleY();case "setAlpha"->s.a=v.getAlpha();case "setTranslationX"->s.tx=v.getTranslationX();case "setTranslationY"->s.ty=v.getTranslationY();case "setRotation"->s.r=v.getRotation();case "setVisibility"->s.visibility=v.getVisibility();}
                    Target t=target(v);if(t==null||t.owner()!=s.owner.get()||t.group()!=s.group)restore(v);else paint(v,s);
                }catch(Throwable unavailable){fail(unavailable);}
            }
        };
        try{for(String property:new String[]{"setScaleX","setScaleY","setAlpha","setTranslationX","setTranslationY","setRotation"})hooks.add(XposedHelpers.findAndHookMethod(View.class,property,float.class,callback));hooks.add(XposedHelpers.findAndHookMethod(View.class,"setVisibility",int.class,callback));QsShapeBackend.begin(style.tileShape());active=true;refresh();if(failed||!QsShapeBackend.healthy())throw new IllegalStateException("Surface apply failed");message="Quick Settings and lock-screen styling active.";}
        catch(Throwable unavailable){end();throw new IllegalStateException("Surface activation unavailable",unavailable);}
    }
    private static void fail(Throwable failure){failed=true;message="Surface callback failed ("+failure.getClass().getSimpleName()+"); restoration required.";}
    private static void track(View view){
        if(!active||writing||failed||!main()||!view.isAttachedToWindow())return;
        try{Target target=target(view);State s=states.get(view);if(target!=null)QsShapeBackend.track(target.owner());if(target==null){if(s!=null)restore(view);return;}
            if(s!=null&&(s.owner.get()!=target.owner()||s.group!=target.group())){if(!restore(view))return;s=null;}
            if(s==null){if(style.appearance(target.group())==0&&style.position(target.group())==0&&!style.hide(target.group()))return;s=new State(view,target);states.put(view,s);captured++;}paint(view,s);
        }catch(Throwable unavailable){fail(unavailable);}
    }
    private static void paint(View v,State s){writing=true;try{
        if(s.scale!=100){v.setScaleX(s.x*s.scale/100f);v.setScaleY(s.y*s.scale/100f);}if(s.opacity!=100)v.setAlpha(s.a*s.opacity/100f);
        if(s.position!=0){float density=v.getResources().getDisplayMetrics().density;if(!Float.isFinite(density)||density<=0||!Float.isFinite(s.tx)||!Float.isFinite(s.ty)||!Float.isFinite(s.r))throw new IllegalStateException();if(StatusBarStyle.horizontal(s.position)!=0)v.setTranslationX(s.tx+StatusBarStyle.horizontal(s.position)*density);if(StatusBarStyle.vertical(s.position)!=0)v.setTranslationY(s.ty+StatusBarStyle.vertical(s.position)*density);if(StatusBarStyle.rotation(s.position)!=0)v.setRotation(s.r+StatusBarStyle.rotation(s.position));}if(s.hide)v.setVisibility(View.GONE);
    }finally{writing=false;}}
    private static boolean near(float a,float b){return Float.isFinite(a)&&Float.isFinite(b)&&Math.abs(a-b)<0.00001f;}
    private static boolean restore(View v){State s=states.get(v);if(s==null)return true;writing=true;try{
        if(s.scale!=100){v.setScaleX(s.x);v.setScaleY(s.y);if(!near(v.getScaleX(),s.x)||!near(v.getScaleY(),s.y))throw new IllegalStateException();}
        if(s.opacity!=100){v.setAlpha(s.a);if(!near(v.getAlpha(),s.a))throw new IllegalStateException();}
        if(StatusBarStyle.horizontal(s.position)!=0){v.setTranslationX(s.tx);if(!near(v.getTranslationX(),s.tx))throw new IllegalStateException();}
        if(StatusBarStyle.vertical(s.position)!=0){v.setTranslationY(s.ty);if(!near(v.getTranslationY(),s.ty))throw new IllegalStateException();}
        if(StatusBarStyle.rotation(s.position)!=0){v.setRotation(s.r);if(!near(v.getRotation(),s.r))throw new IllegalStateException();}
        if(s.hide){v.setVisibility(s.visibility);if(v.getVisibility()!=s.visibility)throw new IllegalStateException();}states.remove(v);restored++;return true;
    }catch(Throwable unavailable){failed=true;message="Surface restoration incomplete. Disable nubia-ly in Vector and reboot.";return false;}finally{writing=false;}}
    static void refresh(){if(!active||failed||!main())return;for(View v:new ArrayList<>(states.keySet()))track(v);for(ViewGroup root:new ArrayList<>(roots.keySet()))if(root.isAttachedToWindow()){QsShapeBackend.track(root);ArrayDeque<View> queue=new ArrayDeque<>();queue.add(root);int count=0;while(!queue.isEmpty()&&count++<128){View v=queue.removeFirst();track(v);if(v instanceof ViewGroup group)for(int i=0;i<group.getChildCount()&&queue.size()<128;i++)queue.add(group.getChildAt(i));}}}
    static boolean end(){generation++;active=false;for(View v:new ArrayList<>(states.keySet()))restore(v);for(var h:hooks)try{h.unhook();}catch(Throwable unavailable){failed=true;}hooks.clear();boolean shapeRestored=QsShapeBackend.end();return !failed&&states.isEmpty()&&shapeRestored;}
    static boolean healthy(){return !failed&&QsShapeBackend.healthy();}
    static String status(){return message+" Surface captures: "+captured+"; restored: "+restored+"; remaining: "+states.size()+". "+QsShapeBackend.status();}
}
