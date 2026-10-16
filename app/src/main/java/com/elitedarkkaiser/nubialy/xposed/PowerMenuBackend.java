package com.elitedarkkaiser.nubialy.xposed;
import android.app.Dialog;
import android.graphics.*;
import android.os.Looper;
import android.view.*;
import android.widget.RelativeLayout;
import com.elitedarkkaiser.nubialy.NotificationEffects;
import de.robv.android.xposed.*;
import java.util.*;
/** Decorates only the two verified vendor action containers, before their children draw. */
final class PowerMenuBackend {
 private static final String OWNER="com.zte.feature.globalaction.MfvGlobalActionsDialog$MFVActionsDialog";
 private static final Map<Dialog,Boolean> dialogs=new WeakHashMap<>();private static XC_MethodHook.Unhook hook;private static boolean ready,active,failed;private static int style,draws;
 static void install(ClassLoader loader){try{Class<?> owner=XposedHelpers.findClass(OWNER,loader);if(owner.getSuperclass()!=Dialog.class||owner.getDeclaredField("mContentView").getType()!=ViewGroup.class||owner.getDeclaredField("mPowerContent").getType()!=RelativeLayout.class||owner.getDeclaredField("mResetContent").getType()!=RelativeLayout.class)throw new IllegalStateException();
  XposedBridge.hookMethod(owner.getDeclaredMethod("initView"),new XC_MethodHook(){@Override protected void afterHookedMethod(MethodHookParam p){if(!p.hasThrowable()&&main()){dialogs.put((Dialog)p.thisObject,true);invalidate();}}});ready=true;
 }catch(Throwable unavailable){ready=false;}}
 private static boolean main(){return Looper.myLooper()==Looper.getMainLooper();}
 private static boolean eligible(ViewGroup view){if(!view.isAttachedToWindow()||view.getClass()!=RelativeLayout.class)return false;for(Dialog dialog:new ArrayList<>(dialogs.keySet())){if(!dialog.isShowing())continue;Object root=XposedHelpers.getObjectField(dialog,"mContentView");if(!(root instanceof ViewGroup parent))continue;for(String name:new String[]{"mPowerContent","mResetContent"})if(XposedHelpers.getObjectField(dialog,name)==view){for(View current=view;current!=null;current=current.getParent() instanceof View next?next:null)if(current==parent)return true;}}return false;}
 static void begin(int value){if(value<0||value>5||!main())throw new IllegalArgumentException();if(!end())throw new IllegalStateException();style=value;draws=0;if(style==0)return;if(!ready||failed)throw new IllegalStateException("Power-menu renderer unavailable");try{hook=XposedBridge.hookMethod(ViewGroup.class.getDeclaredMethod("dispatchDraw",Canvas.class),new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(!active||failed||!main()||!(p.thisObject instanceof ViewGroup view))return;try{if(!eligible(view))return;float density=view.getResources().getDisplayMetrics().density;RectF bounds=new RectF(0,0,view.getWidth(),view.getHeight());float[] corners=new float[8];Arrays.fill(corners,Math.min(bounds.width(),bounds.height())/2);if(NotificationEffects.draw(style,(Canvas)p.args[0],bounds,corners,density,()->active&&!failed&&eligible(view),()->{}))draws++;}catch(Throwable unavailable){failed=true;view.invalidate();}}});active=true;invalidate();}catch(Throwable unavailable){end();throw new IllegalStateException("Power-menu styling unavailable",unavailable);}}
 private static void invalidate(){for(Dialog d:new ArrayList<>(dialogs.keySet()))for(String name:new String[]{"mPowerContent","mResetContent"})try{if(XposedHelpers.getObjectField(d,name) instanceof View v)v.invalidate();}catch(Throwable unavailable){failed=true;}}
 static boolean end(){active=false;invalidate();if(hook!=null)try{hook.unhook();hook=null;}catch(Throwable unavailable){failed=true;}return hook==null;}
 static boolean healthy(){return !failed;}
 static String status(){return "Power menu style: "+style+"; action draws: "+draws+(ready?".":"; source unavailable.");}
}
