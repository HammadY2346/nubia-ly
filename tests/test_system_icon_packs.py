"""Execute pack resource aliases, current drawable state, scoping and revocation against host doubles."""
from pathlib import Path
import ast,subprocess,tempfile
root=Path(__file__).resolve().parents[1]
p=root/'tests/test_personalization_controls.py';ns={'__file__':str(p)}
t=ast.parse(p.read_text());exec(compile(ast.Module(body=[n for n in t.body if not isinstance(n,ast.With)],type_ignores=[]),'doubles','exec'),ns)
sources={k:v for k,v in ns['sources'].items() if not k.endswith('/ControlsTest.java')}
sources['android/graphics/drawable/Drawable.java']=sources['android/graphics/drawable/Drawable.java'].replace('public int level;','public static class ConstantState {}public ConstantState cs=new ConstantState();public ConstantState getConstantState(){return cs;}public boolean setState(int[] n){return true;}public int level;')
sources['android/content/res/Resources.java']='''package android.content.res;import android.graphics.drawable.Drawable;import de.robv.android.xposed.XposedBridge;public class Resources {public static class Theme {}public String getResourcePackageName(int id){return id==90?"third.party":"com.android.systemui";}public String getResourceEntryName(int id){return switch(id){case 1,90->"qs_flashlight_icon_on";case 2->"qs_bluetooth_icon_on";case 3->"ic_no_internet_wifi_signal_3";default->"unknown";};}public Drawable getDrawableForDensity(int id,int density,Theme theme){return (Drawable)XposedBridge.call(this,Resources.class,"getDrawableForDensity",new Class<?>[]{int.class,int.class,Theme.class},new Object[]{id,density,theme},()->new Drawable());}}'''
sources['com/android/systemui/qs/tileimpl/QSIconViewImpl.java']='package com.android.systemui.qs.tileimpl;public class QSIconViewImpl extends android.view.ViewGroup {}'
sources['com/android/systemui/statusbar/phone/NotificationIconContainer.java']='package com.android.systemui.statusbar.phone;public class NotificationIconContainer extends android.view.ViewGroup {}'
sources['com/elitedarkkaiser/nubialy/xposed/PackTest.java']='''package com.elitedarkkaiser.nubialy.xposed;import android.widget.ImageView;import android.graphics.Canvas;import android.content.res.Resources;import com.elitedarkkaiser.nubialy.SystemIconPackPolicy;public class PackTest {static void check(boolean v){if(!v)throw new AssertionError(SystemIconPackBackend.status());}static int draw(ImageView v){Canvas c=new Canvas();v.onDraw(c);check(c.saves==0);return c.draws;}public static void main(String[] args){
check(SystemIconPackPolicy.key("qs_flashlight_icon_on").equals("flashlight"));check(SystemIconPackPolicy.key("ic_no_internet_wifi_signal_3")==null);check(SystemIconPackPolicy.key("qs_bluetooth_icon_search")==null);check(SystemIconPackPolicy.key("unrelated_flashlight_icon")==null);
SystemIconPackBackend.install(PackTest.class.getClassLoader());Resources res=new Resources();var status=new com.android.systemui.statusbar.phone.PhoneStatusBarView();ImageView image=new ImageView();image.parent=status;image.drawable=res.getDrawableForDensity(1,0,null);var original=image.drawable;
SystemIconPackBackend.begin(1);check(draw(image)==1);check(image.drawable==original);image.drawable=res.getDrawableForDensity(2,0,null);check(draw(image)==1);image.drawable=res.getDrawableForDensity(3,0,null);check(draw(image)==100);image.drawable=res.getDrawableForDensity(90,0,null);check(draw(image)==100);
image.drawable=original;image.noTint=true;check(draw(image)==100);image.noTint=false;
var qs=new com.android.systemui.qs.tileimpl.QSIconViewImpl();image.parent=qs;SystemIconPackBackend.observe(image);check(draw(image)==1);image.parent=null;SystemIconPackBackend.observe(image);check(draw(image)==100);
var notifications=new com.android.systemui.statusbar.phone.NotificationIconContainer();notifications.parent=status;image.parent=notifications;SystemIconPackBackend.observe(image);check(draw(image)==100);
image.parent=status;SystemIconPackBackend.observe(image);image.attached=false;check(draw(image)==100);image.attached=true;android.os.Looper.off=true;check(draw(image)==100);android.os.Looper.off=false;
var clone=new android.graphics.drawable.Drawable();clone.cs=original.cs;image.drawable=clone;check(draw(image)==1);check(image.drawable==clone);check(SystemIconPackBackend.end());check(draw(image)==100&&image.drawable==clone);SystemIconPackBackend.begin(0);check(draw(image)==100);check(SystemIconPackBackend.healthy());System.out.println("Icon packs: resource provenance, state changes, warning fallback, QS/status/notification scope, cloned resources, native tint, detach/thread guards and immediate restoration passed");}}
'''
with tempfile.TemporaryDirectory(prefix='nubialy-icon-packs-') as work:
 folder=Path(work)
 for name,content in sources.items():
  f=folder/name;f.parent.mkdir(parents=True,exist_ok=True);f.write_text(content)
 files=[str(f) for f in folder.rglob('*.java')]+[str(root/'app/src/main/java/com/elitedarkkaiser/nubialy'/n) for n in ['ConnectivityResourcePolicy.java','SystemIconPackPolicy.java','xposed/SystemIconPackBackend.java']]
 subprocess.run(['java','com.sun.tools.javac.Main','-d',str(folder/'classes'),*files],check=True)
 subprocess.run(['java','-cp',str(folder/'classes'),'com.elitedarkkaiser.nubialy.xposed.PackTest'],check=True)
