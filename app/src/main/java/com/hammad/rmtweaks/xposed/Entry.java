package com.hammad.rmtweaks.xposed;

import android.app.Application;
import android.content.Context;

import com.hammad.rmtweaks.Features;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/** Module entry point (assets/xposed_init). Dispatches to one backend per host package. */
public final class Entry implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    public static String modulePath;

    @Override public void initZygote(StartupParam param) { modulePath = param.modulePath; }

    @Override public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lp) {
        final String pkg = lp.packageName;
        if (pkg == null || !pkg.equals(lp.processName)) return;   // main process only
        Lx.init(pkg);

        if (Features.PKG.equals(pkg)) {
            try {
                XposedHelpers.findAndHookMethod("com.hammad.rmtweaks.ModuleStatus", lp.classLoader, "isActive",
                        de.robv.android.xposed.XC_MethodReplacement.returnConstant(true));
            } catch (Throwable t) { Lx.e("self status hook", t); }
            return;
        }

        // Give the logger a Context as soon as the host Application exists.
        try {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam p) {
                    try { Lx.attach((Context) p.args[0]); } catch (Throwable ignored) { }
                }
            });
        } catch (Throwable t) { Lx.e("Application.attach hook", t); }

        long t0 = System.currentTimeMillis();
        try {
            switch (pkg) {
                case "cn.nubia.gameassist":      GameAssistHooks.install(lp.classLoader); break;
                case "cn.nubia.gamelauncher":    GameLauncherHooks.install(lp.classLoader); break;
                case "cn.nubia.gamehelpmodule":  GameHelperHooks.install(lp.classLoader); break;
                case "cn.nubia.gamelab":         GameLabHooks.install(lp.classLoader); break;
                case "cn.nubia.gamehighlights":  GameHighlightsHooks.install(lp.classLoader); break;
                case "com.zte.beautify":         ThemeHooks.install(lp.classLoader); break;
                case "com.zte.fingerprints":     FingerprintHooks.installThemeApp(lp.classLoader); break;
                case "com.fingerprint.sensorservice": FingerprintHooks.installSensorService(lp.classLoader); break;
                case "com.zte.mifavor.launcher": LauncherHooks.install(lp.classLoader); break;
                case "com.android.systemui":     SystemUiHooks.install(lp.classLoader); break;
                default: return;
            }
            Lx.i("hooks installed in " + (System.currentTimeMillis() - t0) + " ms; settings=" + Cfg.dump());
        } catch (Throwable t) {
            Lx.e("backend install crashed for " + pkg, t);
        }
    }
}
