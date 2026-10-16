"""Execute the production brightness hooks against exact vendor ownership doubles."""
from pathlib import Path
import ast,subprocess,tempfile
root=Path(__file__).resolve().parents[1]
m=ast.parse((root/'tests/test_statusbar_backend.py').read_text())
base=ast.literal_eval(next(n.value for n in m.body if isinstance(n,ast.Assign) and any(isinstance(t,ast.Name) and t.id=='sources' for t in n.targets)))
sources={k:v for k,v in base.items() if k.startswith('de/robv/android/xposed/')}
sources['de/robv/android/xposed/XC_MethodHook.java']=sources['de/robv/android/xposed/XC_MethodHook.java'].replace('public Object thisObject;','public boolean skip;public void setResult(Object value){skip=true;}public Object thisObject;')
sources['de/robv/android/xposed/XposedBridge.java']=sources['de/robv/android/xposed/XposedBridge.java'].replace('public static Map<Member,','public static boolean bypass;public static Object invokeOriginalMethod(Member method,Object owner,Object[] args)throws Throwable{boolean old=bypass;bypass=true;try{return ((Method)method).invoke(owner,args);}catch(InvocationTargetException e){throw e.getCause();}finally{bypass=old;}}public static Map<Member,').replace('original.run();Collections.reverse','if(!p.skip)original.run();Collections.reverse')
sources['de/robv/android/xposed/XposedHelpers.java']=sources['de/robv/android/xposed/XposedHelpers.java'].replace('o.getClass().getDeclaredMethods()','o.getClass().getMethods()')
sources.update({
'android/R.java':'package android;public class R {public static class id {public static final int background=1,progress=2;}}',
'android/os/Looper.java':'package android.os;public class Looper {static final Looper MAIN=new Looper();public static boolean off;public static Looper getMainLooper(){return MAIN;}public static Looper myLooper(){return off?null:MAIN;}}',
'android/util/DisplayMetrics.java':'package android.util;public class DisplayMetrics {public float density=2;}',
'android/content/res/Resources.java':'package android.content.res;public class Resources {public android.util.DisplayMetrics getDisplayMetrics(){return new android.util.DisplayMetrics();}}',
'android/graphics/Canvas.java':'package android.graphics;public class Canvas {public int draws;public boolean failOnce;public Runnable during;}',
'android/view/ViewParent.java':'package android.view;public interface ViewParent {}',
'android/view/View.java':'package android.view;public class View implements ViewParent {public ViewParent parent;public int invalidations;public boolean attached=true,failInvalidate;public ViewParent getParent(){return parent;}public boolean isAttachedToWindow(){return attached;}public void invalidate(){invalidations++;if(failInvalidate)throw new IllegalStateException();}public android.content.res.Resources getResources(){return new android.content.res.Resources();}}',
'android/graphics/drawable/Drawable.java':'package android.graphics.drawable;public class Drawable {}',
'android/graphics/drawable/GradientDrawable.java':'''package android.graphics.drawable;import de.robv.android.xposed.XposedBridge;public class GradientDrawable extends Drawable {public static final int RECTANGLE=0;public int shape;public int getShape(){return shape;}public void draw(android.graphics.Canvas c){Runnable nativeDraw=()->{if(c.failOnce){c.failOnce=false;throw new IllegalStateException();}c.draws++;if(c.during!=null)c.during.run();};if(XposedBridge.bypass)nativeDraw.run();else XposedBridge.run(this,GradientDrawable.class,"draw",new Class<?>[]{android.graphics.Canvas.class},new Object[]{c},nativeDraw);}}''',
'android/graphics/drawable/ClipDrawable.java':'package android.graphics.drawable;public class ClipDrawable extends Drawable {public Drawable fill=new GradientDrawable();public Drawable getDrawable(){return fill;}}',
'android/graphics/drawable/LayerDrawable.java':'package android.graphics.drawable;public class LayerDrawable extends Drawable {public Drawable track=new GradientDrawable(),progress=new ClipDrawable();public int count=2;public int getNumberOfLayers(){return count;}public Drawable findDrawableByLayerId(int id){return id==1?track:progress;}}',
'android/widget/SeekBar.java':'''package android.widget;import android.graphics.drawable.*;import de.robv.android.xposed.XposedBridge;public class SeekBar extends android.view.View {public Drawable progress=new LayerDrawable();public Drawable getProgressDrawable(){return progress;}public void onDraw(android.graphics.Canvas c){XposedBridge.run(this,SeekBar.class,"onDraw",new Class<?>[]{android.graphics.Canvas.class},new Object[]{c},()->{if(progress instanceof LayerDrawable l){((GradientDrawable)l.track).draw(c);((GradientDrawable)((ClipDrawable)l.progress).fill).draw(c);}});}}''',
'com/android/systemui/settings/brightness/ToggleSeekBar.java':'package com.android.systemui.settings.brightness;public class ToggleSeekBar extends android.widget.SeekBar {}',
'com/zte/controlcenter/view/ToggleSliderView.java':'package com.zte.controlcenter.view;public class ToggleSliderView extends android.view.View {public com.android.systemui.settings.brightness.ToggleSeekBar slider=new com.android.systemui.settings.brightness.ToggleSeekBar();public ToggleSliderView(){slider.parent=this;}public com.android.systemui.settings.brightness.ToggleSeekBar getSlider(){return slider;}}',
'com/elitedarkkaiser/nubialy/QsOutlineRenderer.java':'package com.elitedarkkaiser.nubialy;public class QsOutlineRenderer {@FunctionalInterface public interface NativeDraw{void draw()throws Throwable;}}',
'com/elitedarkkaiser/nubialy/BrightnessEffects.java':'package com.elitedarkkaiser.nubialy;public class BrightnessEffects {public static int decorated;public static boolean draw(int style,android.graphics.Canvas c,android.graphics.drawable.GradientDrawable d,float density,java.util.function.BooleanSupplier allowed,QsOutlineRenderer.NativeDraw draw)throws Throwable{if(!allowed.getAsBoolean())return false;draw.draw();if(!allowed.getAsBoolean())return false;decorated++;return true;}}',
'com/elitedarkkaiser/nubialy/xposed/BrightnessTest.java':'''package com.elitedarkkaiser.nubialy.xposed;import android.graphics.*;import android.graphics.drawable.*;import com.elitedarkkaiser.nubialy.BrightnessEffects;public class BrightnessTest {
static void check(boolean b){if(!b)throw new AssertionError(BrightnessBackend.status());}
public static void main(String[] args){BrightnessBackend.install(BrightnessTest.class.getClassLoader());var owner=new com.zte.controlcenter.view.ToggleSliderView();var mirror=new com.zte.controlcenter.view.ToggleSliderView();var unrelated=new com.android.systemui.settings.brightness.ToggleSeekBar();
BrightnessBackend.observe(owner.slider);BrightnessBackend.observe(mirror.slider);for(int style=1;style<=3;style++){int previous=owner.slider.invalidations;BrightnessBackend.begin(style);check(owner.slider.invalidations>previous);BrightnessEffects.decorated=0;owner.slider.onDraw(new Canvas());mirror.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==4);unrelated.onDraw(new Canvas());new android.widget.SeekBar().onDraw(new Canvas());check(BrightnessEffects.decorated==4);
var stale=(LayerDrawable)owner.slider.progress;owner.slider.progress=new LayerDrawable();((GradientDrawable)stale.track).draw(new Canvas());check(BrightnessEffects.decorated==4);owner.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==6);
owner.slider.attached=false;owner.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==6);owner.slider.attached=true;android.os.Looper.off=true;owner.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==6);android.os.Looper.off=false;
var saved=owner.slider;owner.slider=new com.android.systemui.settings.brightness.ToggleSeekBar();saved.onDraw(new Canvas());check(BrightnessEffects.decorated==6);owner.slider=saved;
owner.slider.progress=new LayerDrawable();((LayerDrawable)owner.slider.progress).count=3;owner.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==6);owner.slider.progress=new LayerDrawable();
int beforeRestore=owner.slider.invalidations;check(BrightnessBackend.end());check(owner.slider.invalidations>beforeRestore);check(de.robv.android.xposed.XposedBridge.hooks.isEmpty());owner.slider.onDraw(new Canvas());check(BrightnessEffects.decorated==6);}
BrightnessBackend.begin(1);Canvas c=new Canvas();c.during=()->BrightnessBackend.end();owner.slider.onDraw(c);check(de.robv.android.xposed.XposedBridge.hooks.isEmpty());check(BrightnessBackend.healthy());
BrightnessBackend.begin(1);c=new Canvas();c.failOnce=true;owner.slider.onDraw(c);check(!BrightnessBackend.healthy());check(BrightnessBackend.end());check(de.robv.android.xposed.XposedBridge.hooks.isEmpty());System.out.println("Brightness ownership, mirror, replacement, detach, thread, unknown layers, revocation, failure and cleanup passed");}}
'''
})
with tempfile.TemporaryDirectory(prefix='nubialy-brightness-') as folder:
 folder=Path(folder)
 for name,content in sources.items():
  p=folder/name;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(content)
 files=[str(p) for p in folder.rglob('*.java')]+[str(root/'app/src/main/java/com/elitedarkkaiser/nubialy/xposed/BrightnessBackend.java')]
 subprocess.run(['java','com.sun.tools.javac.Main','-d',str(folder/'classes'),*files],check=True)
 subprocess.run(['java','-cp',str(folder/'classes'),'com.elitedarkkaiser.nubialy.xposed.BrightnessTest'],check=True)
