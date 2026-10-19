package com.hammad.rmtweaks.xposed;

import android.content.ContentResolver;
import android.content.Context;

import com.hammad.rmtweaks.Features;

import de.robv.android.xposed.XC_MethodHook;

/** cn.nubia.gamelauncher — Game Space, trigger-key (TGK) mapping. */
final class GameLauncherHooks {
    private GameLauncherHooks() {}

    private static final String GFAV = "cn.nubia.gamelauncher.gamecontrolpanel.GameFunctionAllocationView";

    static void install(ClassLoader cl) {
        Class<?> tgk = H.cls(cl, "cn.nubia.tgk.TgkHelper");
        if (tgk != null) {
            H.constant(tgk, "disableTgkFunction", boolean.class, H.types(String.class), false, () -> Cfg.on(Features.GAME_TGK_ALL));
            H.constant(tgk, "getTgkDisableOpt", int.class, H.types(ContentResolver.class, String.class), 0, () -> Cfg.on(Features.GAME_TGK_ALL));
        }
        Class<?> pc = H.cls(cl, "cn.nubia.gamelauncher.gamecontrolpanel.config.PluginConfig");
        if (pc != null) {
            H.hook(H.find(pc, "getBlackList", Context.class, String.class), new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam p) {
                    if (Cfg.on(Features.GAME_TGK_ALL)) p.setResult(new String[0]);
                }
            });
        }
        Class<?> view = H.cls(cl, GFAV);
        if (view != null) {
            H.hook(H.find(view, "disableRedMagicTime"), new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    if (!Cfg.on(Features.GAME_KEEP_RMTIME)) return;
                    // let the user's own tap (inner-class click listener) through; block automatic resets
                    if (!H.calledFromPrefix(GFAV + "$")) {
                        Lx.i("GameSpace: blocked automatic disableRedMagicTime");
                        p.setResult(null);
                    }
                }
            });
        }
        Lx.i("GameLauncher: hooks ready");
    }
}
