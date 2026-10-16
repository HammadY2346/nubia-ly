package com.elitedarkkaiser.nubialy.xposed;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.*;
import android.widget.ImageView;
import com.elitedarkkaiser.nubialy.ConnectivityStyles;
import de.robv.android.xposed.*;
import java.util.*;
/** Native state remains authoritative; only ordinary connected glyphs in PhoneStatusBarView are redrawn. */
final class ConnectivityBackend {
 private record ResourceState(Drawable drawable,int level){}
 private record ArtworkState(boolean wifi,int style,int level,Drawable drawable){}
 private static final Map<ImageView,ArtworkState> artwork=new WeakHashMap<>();
 private static final Map<ImageView,ResourceState> resources=new WeakHashMap<>();
 private static final Map<ImageView,Boolean> observed=new WeakHashMap<>();
 private static final List<XC_MethodHook.Unhook> hooks=new ArrayList<>();
 private static int wifi,signal,draws;private static boolean active,ready,failed;
 private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
 private static String id(View v){try{return v.getResources().getResourcePackageName(v.getId()).equals("com.android.systemui")?v.getResources().getResourceEntryName(v.getId()):"";}catch(Throwable absent){return "";}}
 private static int category(ImageView v){String id=id(v);if(!id.equals("wifi_signal")&&!id.equals("mobile_signal"))return 0;boolean owner=false;View current=v;
  for(int i=0;i<16&&current!=null;i++){String name=current.getClass().getName();if(name.equals(id.equals("wifi_signal")?"com.android.systemui.statusbar.pipeline.wifi.ui.view.ModernStatusBarWifiView":"com.android.systemui.statusbar.pipeline.mobile.ui.view.ModernStatusBarMobileView"))owner=true;
   if(name.equals("com.android.systemui.statusbar.phone.PhoneStatusBarView"))return owner?(id.equals("wifi_signal")?1:2):0;current=current.getParent() instanceof View next?next:null;}return 0;}
 static void observe(View view){if(main()&&view instanceof ImageView image&&category(image)!=0){observed.put(image,true);if(active)image.invalidate();}}
 static void install(ClassLoader loader){try{
  XposedHelpers.findClass("com.android.systemui.statusbar.pipeline.wifi.ui.view.ModernStatusBarWifiView",loader);XposedHelpers.findClass("com.android.systemui.statusbar.pipeline.mobile.ui.view.ModernStatusBarMobileView",loader);
  XposedBridge.hookMethod(ImageView.class.getDeclaredMethod("setImageResource",int.class),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(!main()||p.hasThrowable())return;ImageView v=(ImageView)p.thisObject;String target=id(v);if(!target.equals("wifi_signal")&&!target.equals("mobile_signal"))return;resources.remove(v);try{String name=v.getResources().getResourceEntryName((Integer)p.args[0]);if(target.equals("wifi_signal")&&name.matches("(?:intl_)?ic_wifi_(?:[4-7]_)?signal_[0-4]"))resources.put(v,new ResourceState(v.getDrawable(),name.charAt(name.length()-1)-'0'));else if(target.equals("mobile_signal")&&name.matches("(?:sp_signal_cellular|statusbar_signal)_[0-5]"))resources.put(v,new ResourceState(v.getDrawable(),Math.round((name.charAt(name.length()-1)-'0')*4f/5f)));}catch(Throwable absent){} }});
  ready=true;
 }catch(Throwable unavailable){ready=false;}}
 static void begin(int wifiStyle,int signalStyle){if(!main()||wifiStyle<0||wifiStyle>8||signalStyle<0||signalStyle>8)throw new IllegalArgumentException();if(!end())throw new IllegalStateException();wifi=wifiStyle;signal=signalStyle;draws=0;if(wifi==0&&signal==0)return;if(!ready||failed)throw new IllegalStateException("Signal styling unavailable");
  try{hooks.add(XposedBridge.hookMethod(ImageView.class.getDeclaredMethod("onDraw",Canvas.class),new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(!active||failed||!main())return;ImageView v=(ImageView)p.thisObject;int kind=category(v),style=kind==1?wifi:kind==2?signal:0;if(style==0||!v.isAttachedToWindow())return;observed.put(v,true);Drawable nativeIcon=v.getDrawable();if(nativeIcon==null)return;
   try{int level;Integer color=null;if(kind==1){ResourceState state=resources.get(v);if(state==null||state.drawable()!=nativeIcon)return;level=state.level();}else if(!nativeIcon.getClass().getName().equals("com.android.settingslib.graph.SignalDrawable")){ResourceState state=resources.get(v);if(state==null||state.drawable()!=nativeIcon)return;level=state.level();}else{int packed=nativeIcon.getLevel();if((packed>>>16)!=0)return;int count=(packed>>>8)&255,raw=packed&255;if(count<2||count>6||raw>=count)return;level=Math.round(raw*4f/(count-1));Object paint=XposedHelpers.getObjectField(nativeIcon,"mForegroundPaint");if(paint instanceof Paint foreground)color=foreground.getColor();}
    ArtworkState cached=artwork.get(v);if(cached==null||cached.wifi()!=(kind==1)||cached.style()!=style||cached.level()!=level){Drawable loaded=ConnectivityStyles.artwork(kind==1,style,level);if(loaded==null)return;cached=new ArtworkState(kind==1,style,level,loaded);artwork.put(v,cached);}Drawable art=cached.drawable();if(v.getImageTintList()!=null)color=v.getImageTintList().getColorForState(v.getDrawableState(),v.getImageTintList().getDefaultColor());if(color==null)return;
    art.setTint(color);art.setAlpha(v.getImageAlpha());art.setLayoutDirection(v.getLayoutDirection());Rect bounds=nativeIcon.getBounds();if(bounds.isEmpty())return;art.setBounds(bounds);Canvas c=(Canvas)p.args[0];int save=c.save();try{c.translate(v.getPaddingLeft(),v.getPaddingTop());c.concat(v.getImageMatrix());art.draw(c);}finally{c.restoreToCount(save);}draws++;p.setResult(null);
   }catch(Throwable unavailable){failed=true;v.invalidate();}
  }}));active=true;invalidate();}catch(Throwable unavailable){end();throw new IllegalStateException("Signal styling unavailable",unavailable);}}
 private static void invalidate(){for(ImageView v:new ArrayList<>(observed.keySet()))v.invalidate();}
 static boolean end(){active=false;invalidate();artwork.clear();for(var h:hooks)try{h.unhook();}catch(Throwable unavailable){failed=true;}hooks.clear();return !failed;}
 static boolean healthy(){return !failed;}
 static String status(){return "Connectivity styles: Wi-Fi "+wifi+", mobile "+signal+"; glyph draws: "+draws+(ready?".":"; source unavailable.");}
}
