package com.elitedarkkaiser.nubialy.xposed;
import android.os.Looper;
import de.robv.android.xposed.*;
import java.lang.reflect.Method;
/** Suppresses new automatic heads-up presentations; user-pinned presentations stay native. */
final class NotificationBehaviorBackend {
 private static Method show;private static XC_MethodHook.Unhook hook;private static boolean failed;
 static void install(ClassLoader loader){try{Class<?> owner=XposedHelpers.findClass("com.android.systemui.statusbar.notification.headsup.HeadsUpManagerImpl",loader),entry=XposedHelpers.findClass("com.android.systemui.statusbar.notification.collection.NotificationEntry",loader);show=owner.getDeclaredMethod("showNotification",entry,boolean.class);if(show.getReturnType()!=void.class)show=null;}catch(Throwable unavailable){show=null;}}
 static void begin(int value){if(value<0||value>1||Looper.myLooper()!=Looper.getMainLooper())throw new IllegalArgumentException();if(!end())throw new IllegalStateException();if(value==0)return;if(show==null||failed)throw new IllegalStateException("Heads-up control unavailable");hook=XposedBridge.hookMethod(show,new XC_MethodHook(){@Override protected void beforeHookedMethod(MethodHookParam p){if(!failed&&Looper.myLooper()==Looper.getMainLooper()&&Boolean.FALSE.equals(p.args[1]))p.setResult(null);}});}
 static boolean end(){if(hook!=null)try{hook.unhook();hook=null;}catch(Throwable unavailable){failed=true;}return hook==null;}
 static boolean healthy(){return !failed;}
 static String status(){return "Automatic heads-up suppression: "+(hook!=null?"enabled":"off")+".";}
}
