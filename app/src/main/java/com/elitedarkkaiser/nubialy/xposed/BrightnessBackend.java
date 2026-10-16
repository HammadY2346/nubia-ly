package com.elitedarkkaiser.nubialy.xposed;

import android.graphics.Canvas;
import android.graphics.drawable.*;
import android.os.Looper;
import android.view.*;
import android.widget.SeekBar;
import com.elitedarkkaiser.nubialy.BrightnessEffects;
import de.robv.android.xposed.*;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.*;

/** Exact NX809J ToggleSliderView ownership, native LayerDrawable/ClipDrawable progress retained. */
final class BrightnessBackend {
    private static final String OWNER="com.zte.controlcenter.view.ToggleSliderView",SLIDER="com.android.systemui.settings.brightness.ToggleSeekBar";
    private record Entry(WeakReference<SeekBar> slider,WeakReference<Drawable> root){}
    private static final Map<GradientDrawable,Entry> leaves=new WeakHashMap<>();
    private static final List<XC_MethodHook.Unhook> hooks=new ArrayList<>();
    private static ClassLoader loader;private static boolean active,failed;private static long generation;private static int selection;
    static void install(ClassLoader value){loader=value;}
    private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
    static void begin(int style){
        if(style<0||style>3||!main())throw new IllegalArgumentException();if(!end())throw new IllegalStateException("Brightness cleanup incomplete");selection=style;if(style==0)return;
        final long ticket=++generation;
        try{
            Class<?> slider=XposedHelpers.findClass(SLIDER,loader),owner=XposedHelpers.findClass(OWNER,loader);
            if(slider.getSuperclass()!=SeekBar.class||owner.getMethod("getSlider").getReturnType()!=slider)throw new IllegalStateException("Brightness signatures");
            Method draw=null;for(Class<?> type=slider;type!=null;type=type.getSuperclass())try{draw=type.getDeclaredMethod("onDraw",Canvas.class);break;}catch(NoSuchMethodException absent){}
            if(draw==null)throw new IllegalStateException("Brightness draw unavailable");
            hooks.add(XposedBridge.hookMethod(draw,new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){
                if(!active||failed||!main()||!p.thisObject.getClass().getName().equals(SLIDER))return;
                try{track((SeekBar)p.thisObject);}catch(Throwable unavailable){failed=true;invalidate();}
            }}));
            hooks.add(XposedHelpers.findAndHookMethod(GradientDrawable.class,"draw",Canvas.class,new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){
                if(!active||failed||!main()||!(p.thisObject instanceof GradientDrawable d))return;Entry entry=leaves.get(d);if(!eligible(entry,d))return;
                SeekBar view=entry.slider().get();
                try{if(BrightnessEffects.draw(style,(Canvas)p.args[0],d,view.getResources().getDisplayMetrics().density,()->active&&!failed&&generation==ticket&&eligible(entry,d),()->XposedBridge.invokeOriginalMethod(p.method,p.thisObject,p.args)))p.setResult(null);}
                catch(Throwable unavailable){failed=true;invalidate();}
            }}));active=true;
        }catch(Throwable unavailable){failed=true;end();throw new IllegalStateException("Brightness styling unavailable",unavailable);}
    }
    private static boolean owned(SeekBar slider){
        if(slider==null||!slider.isAttachedToWindow()||!slider.getClass().getName().equals(SLIDER))return false;
        ViewParent parent=slider.getParent();for(int depth=0;depth<8&&parent instanceof View view;depth++,parent=view.getParent()){
            if(view.getClass().getName().equals(OWNER))return XposedHelpers.callMethod(view,"getSlider")==slider;
        }return false;
    }
    private static GradientDrawable[] layers(Drawable root){
        if(!(root instanceof LayerDrawable layer)||layer.getNumberOfLayers()!=2)return null;
        Drawable background=layer.findDrawableByLayerId(android.R.id.background),progress=layer.findDrawableByLayerId(android.R.id.progress);
        if(!(background instanceof GradientDrawable track)||!(progress instanceof ClipDrawable clip)||!(clip.getDrawable() instanceof GradientDrawable fill)||track==fill)return null;
        if(track.getShape()!=GradientDrawable.RECTANGLE||fill.getShape()!=GradientDrawable.RECTANGLE)return null;
        return new GradientDrawable[]{track,fill};
    }
    private static boolean eligible(Entry entry,GradientDrawable drawable){
        if(entry==null)return false;try{SeekBar slider=entry.slider().get();Drawable root=entry.root().get();if(!owned(slider)||root==null||slider.getProgressDrawable()!=root)return false;GradientDrawable[] parts=layers(root);return parts!=null&&(parts[0]==drawable||parts[1]==drawable);}catch(Throwable unavailable){return false;}
    }
    private static void track(SeekBar slider){
        leaves.entrySet().removeIf(e->e.getValue().slider().get()==null||e.getValue().slider().get()==slider);
        if(!owned(slider))return;Drawable root=slider.getProgressDrawable();GradientDrawable[] parts=layers(root);if(parts==null)return;
        Entry entry=new Entry(new WeakReference<>(slider),new WeakReference<>(root));for(GradientDrawable part:parts){Entry other=leaves.get(part);if(other!=null&&other.slider().get()!=slider)throw new IllegalStateException("Shared brightness drawable");leaves.put(part,entry);}
    }
    private static void invalidate(){Set<SeekBar> views=new HashSet<>();for(Entry entry:new ArrayList<>(leaves.values())){SeekBar view=entry.slider().get();if(view!=null&&views.add(view))try{view.invalidate();}catch(Throwable unavailable){failed=true;}}}
    static boolean end(){generation++;active=false;invalidate();leaves.clear();boolean removed=true;for(var hook:new ArrayList<>(hooks))try{hook.unhook();hooks.remove(hook);}catch(Throwable unavailable){removed=false;failed=true;}return removed;}
    static boolean healthy(){return !failed;}
    static String status(){return "Brightness style: "+selection+"; tracked layers: "+leaves.size()+(failed?"; styling stopped.":".");}
}
