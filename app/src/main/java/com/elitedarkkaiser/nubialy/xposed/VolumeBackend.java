package com.elitedarkkaiser.nubialy.xposed;
import android.graphics.*;
import android.os.Looper;
import android.view.*;
import android.widget.SeekBar;
import com.elitedarkkaiser.nubialy.VolumeEffects;
import de.robv.android.xposed.*;
import java.lang.ref.WeakReference;
import java.util.*;
/** Inspected volume-button dialog rows only; no audio API, level, listener or native field writes. */
final class VolumeBackend {
    private static final String DIALOG="com.zte.feature.volume.MfvVolumeDialogImpl",BASE="com.android.systemui.volume.VolumeDialogImpl",ROW=BASE+"$VolumeRow",SLIDER="com.zte.mifavor.widget.ColorSeekBarZTE";
    private record Entry(WeakReference<Object> dialog,WeakReference<Object> row){}
    private static final Map<Object,Boolean> dialogs=new WeakHashMap<>();
    private static final Map<SeekBar,Entry> rows=new WeakHashMap<>();
    private static final List<XC_MethodHook.Unhook> observers=new ArrayList<>();
    private static XC_MethodHook.Unhook drawHook;private static Class<?> sliderClass;private static boolean ready,active,failed;private static long generation;private static int selection,draws;private static String message="not initialized";
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    static void install(ClassLoader loader){
        try{Class<?> dialog=XposedHelpers.findClass(DIALOG,loader),base=XposedHelpers.findClass(BASE,loader),row=XposedHelpers.findClass(ROW,loader);sliderClass=XposedHelpers.findClass(SLIDER,loader);
            if(dialog.getSuperclass()!=base||sliderClass.getSuperclass()!=SeekBar.class||row.getDeclaredField("slider").getType()!=SeekBar.class||row.getDeclaredField("view").getType()!=View.class||base.getDeclaredField("mRows").getType()!=List.class)throw new IllegalStateException("Volume signatures");
            for(String field:new String[]{"mLeftPadding","mTopPadding","mLength","mTrackSize"})if(sliderClass.getDeclaredField(field).getType()!=int.class)throw new IllegalStateException("Volume geometry");
            if(sliderClass.getDeclaredField("mRadii").getType()!=float[].class)throw new IllegalStateException("Volume radii");
            XC_MethodHook discover=new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(!p.hasThrowable()&&main()&&p.thisObject.getClass().getName().equals(DIALOG))try{dialogs.put(p.thisObject,true);scan(p.thisObject);if(active)invalidate();}catch(Throwable unavailable){if(active)failed=true;}}};
            observers.add(XposedBridge.hookMethod(dialog.getDeclaredMethod("initRow",row,int.class,int.class,int.class,boolean.class,boolean.class),discover));
            observers.add(XposedBridge.hookMethod(dialog.getDeclaredMethod("showH",int.class,boolean.class,int.class),discover));
            ready=true;message="ready";
        }catch(Throwable unavailable){for(var hook:observers)try{hook.unhook();}catch(Throwable ignored){}observers.clear();message="source unavailable: "+unavailable.getClass().getSimpleName();}
    }
    private static void scan(Object dialog){Object value=XposedHelpers.getObjectField(dialog,"mRows");if(!(value instanceof List<?> list)||list.size()>32)return;
        rows.entrySet().removeIf(e->e.getValue().dialog().get()==dialog);
        for(Object row:list){if(row==null||!row.getClass().getName().equals(ROW))continue;Object candidate=XposedHelpers.getObjectField(row,"slider");if(candidate instanceof SeekBar slider&&slider.getClass().getName().equals(SLIDER))rows.put(slider,new Entry(new WeakReference<>(dialog),new WeakReference<>(row)));}
    }
    private static boolean eligible(SeekBar slider,Entry entry){
        if(slider==null||entry==null||!slider.isAttachedToWindow())return false;Object dialog=entry.dialog().get(),row=entry.row().get();if(dialog==null||row==null||!dialog.getClass().getName().equals(DIALOG))return false;
        try{Object list=XposedHelpers.getObjectField(dialog,"mRows");if(!(list instanceof List<?> values)||values.size()>32||values.stream().noneMatch(v->v==row)||XposedHelpers.getObjectField(row,"slider")!=slider)return false;
            Object root=XposedHelpers.getObjectField(row,"view");View current=slider;for(int depth=0;depth<12&&current!=null;depth++){if(current==root)return true;ViewParent parent=current.getParent();current=parent instanceof View view?view:null;}return false;
        }catch(Throwable unavailable){return false;}
    }
    static void begin(int style){if(style<0||style>6||!main())throw new IllegalArgumentException();if(!end())throw new IllegalStateException("Volume hook cleanup incomplete");selection=style;draws=0;if(style==0)return;if(!ready||failed)throw new IllegalStateException(message);final long ticket=++generation;
        try{drawHook=XposedBridge.hookMethod(sliderClass.getDeclaredMethod("onDraw",Canvas.class),new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){
            if(!active||failed||!main()||!(p.thisObject instanceof SeekBar slider))return;Entry entry=rows.get(slider);if(!eligible(slider,entry))return;
            try{float left=((Number)XposedHelpers.getObjectField(slider,"mLeftPadding")).floatValue(),top=((Number)XposedHelpers.getObjectField(slider,"mTopPadding")).floatValue(),width=((Number)XposedHelpers.getObjectField(slider,"mLength")).floatValue(),height=((Number)XposedHelpers.getObjectField(slider,"mTrackSize")).floatValue();
                if(!Float.isFinite(left)||!Float.isFinite(top)||!Float.isFinite(width)||!Float.isFinite(height)||width<=0||height<=0)return;
                float[] radii=(float[])XposedHelpers.getObjectField(slider,"mRadii");if(VolumeEffects.draw(style,(Canvas)p.args[0],new RectF(left,top,left+width,top+height),radii,slider.getResources().getDisplayMetrics().density,()->active&&!failed&&generation==ticket&&eligible(slider,entry),()->XposedBridge.invokeOriginalMethod(p.method,p.thisObject,p.args))){draws++;p.setResult(null);}
            }catch(Throwable unavailable){failed=true;invalidate();}
        }});active=true;refresh();invalidate();}catch(Throwable unavailable){failed=true;end();throw new IllegalStateException("Volume draw hook unavailable",unavailable);}
    }
    static void refresh(){if(!active||failed||!main())return;try{for(Object dialog:new ArrayList<>(dialogs.keySet()))scan(dialog);}catch(Throwable unavailable){failed=true;}}
    private static void invalidate(){for(SeekBar slider:new ArrayList<>(rows.keySet()))try{if(slider.isAttachedToWindow())slider.invalidate();}catch(Throwable unavailable){failed=true;}}
    static boolean end(){generation++;active=false;invalidate();if(drawHook!=null)try{drawHook.unhook();drawHook=null;}catch(Throwable unavailable){failed=true;}return drawHook==null;}
    static boolean healthy(){return !failed;}
    static String status(){return "Volume style: "+selection+"; rows: "+rows.size()+"; decorated draws: "+draws+"; "+message+(failed?"; styling stopped.":".");}
}
