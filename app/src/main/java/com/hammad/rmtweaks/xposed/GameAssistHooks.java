package com.hammad.rmtweaks.xposed;

import android.content.ContentResolver;
import android.content.Context;
import android.os.BaseBundle;
import android.provider.Settings;

import com.hammad.rmtweaks.Features;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;

/** cn.nubia.gameassist — Game Assist overlay, plugins, performance modes, Super Resolution. */
final class GameAssistHooks {
    private GameAssistHooks() {}

    private static final String PMC = "cn.nubia.gameassist.performance.PerformanceModeController";
    private static final String SRVC = "cn.nubia.plugin.superresolution.SuperResolutionViewController";
    private static final String SR_TILE = "cn.nubia.gameassist.plugin.tiles.SuperResolutionTile";
    private static final String BIABLO_TILE = "cn.nubia.gameassist.plugin.tiles.BiabloTile";
    private static final String PLUGIN_CONFIG = "cn.nubia.gameassist.plugin.config.PluginConfig";

    static void install(ClassLoader cl) {
        unfreeze(cl);
        superResolution(cl);
        dialogs(cl);
    }

    /** Every plugin / overlay feature enabled for every package. */
    private static void unfreeze(ClassLoader cl) {
        Class<?> pc = H.cls(cl, PLUGIN_CONFIG);
        if (pc != null) {
            // isPluginEnable(Context, String)            -> firmware 11.0.11: k(Context, String)
            H.constant(pc, "isPluginEnable", boolean.class, H.types(Context.class, String.class), true, () -> Cfg.on(Features.GAME_UNFREEZE));
            // isPluginEnable(Context, String, String)    -> l(Context, String, String)
            H.constant(pc, "isPluginEnable", boolean.class, H.types(Context.class, String.class, String.class), true, () -> Cfg.on(Features.GAME_UNFREEZE));
        }
        // Bundle flags exchanged between Game Assist and its plugins.
        XC_MethodHook bundleHook = new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                if (!Cfg.on(Features.GAME_UNFREEZE)) return;
                Object k = p.args[0];
                if ("is_need_show_linkview".equals(k) || "isMacroEnable".equals(k) || "isPackageEnable".equals(k)) p.setResult(true);
            }
        };
        H.hook(H.find(BaseBundle.class, "getBoolean", String.class), bundleHook);
        H.hook(H.find(BaseBundle.class, "getBoolean", String.class, boolean.class), bundleHook);
        Lx.i("GameAssist: unfreeze hooks ready");
    }

    /** Super Resolution + Diablo coexistence, no auto-disable. */
    private static void superResolution(ClassLoader cl) {
        Class<?> pmc = H.cls(cl, PMC);
        Class<?> sr = H.cls(cl, SRVC);
        if (pmc != null) {
            // getPerformanceMode(String): inside the SR tile click, report 3 instead of 5/1 so the tile does not refuse.
            H.hook(H.find(pmc, "getPerformanceMode", String.class), new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam p) {
                    if (!Cfg.on(Features.GAME_SR_COEXIST)) return;
                    Object r = p.getResult();
                    if (r instanceof Integer && ((Integer) r == 5 || (Integer) r == 1) && H.calledFrom(SR_TILE, null)) {
                        Lx.i("SR: getPerformanceMode " + r + " -> 3 (coexist)");
                        p.setResult(3);
                    }
                }
            });
            // setBiabloModeEnable(String, boolean, boolean) -> C0(...) : block the automatic (false,false) shutdown.
            final Method setBiablo = H.resolveAny(pmc, void.class, H.types(String.class, boolean.class, boolean.class), "setBiabloModeEnable", "C0");
            H.hook(setBiablo, new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) throws Throwable {
                    boolean enable = Boolean.TRUE.equals(p.args[1]);
                    boolean dialog = Boolean.TRUE.equals(p.args[2]);
                    if (!enable && !dialog && Cfg.on(Features.GAME_SR_COEXIST) && H.calledFrom(PMC, null) && !H.calledFrom(BIABLO_TILE, null)) {
                        Lx.i("SR: blocked automatic Diablo shutdown for " + p.args[0]);
                        p.setResult(null);
                        return;
                    }
                    if (enable && dialog && Cfg.on(Features.GAME_NO_DIALOGS)) {
                        Lx.i("Diablo: enabling without confirmation dialog for " + p.args[0]);
                        setBiablo.invoke(p.thisObject, p.args[0], true, false);
                        p.setResult(null);
                    }
                }
            });
        }
        if (sr != null) {
            // updateEnableSwitchPkg(String, boolean) -> L(String, boolean): block auto-off coming from the controller itself.
            H.hook(H.resolveAny(sr, void.class, H.types(String.class, boolean.class), "updateEnableSwitchPkg", "L"), new XC_MethodHook() {
                @Override protected void beforeHookedMethod(MethodHookParam p) {
                    if (!Cfg.on(Features.GAME_SR_COEXIST)) return;
                    if (Boolean.FALSE.equals(p.args[1]) && H.calledFrom(SRVC, null) && !H.calledFrom(SR_TILE, null)
                            && !H.calledFrom("cn.nubia.plugin.superresolution.SuperResolutionSettingWindowManager", null)) {
                        Lx.i("SR: blocked automatic Super Resolution off for " + p.args[0]);
                        p.setResult(null);
                    }
                }
            });
            // isSuperResolutionOn(String) -> w(String): Diablo tile / controller must not see SR as on.
            H.hook(H.resolveAny(sr, boolean.class, H.types(String.class), "isSuperResolutionEnabledForPkg", "isOpenSuperResolution", "w"), new XC_MethodHook() {
                @Override protected void afterHookedMethod(MethodHookParam p) {
                    if (!Cfg.on(Features.GAME_SR_COEXIST)) return;
                    if (Boolean.TRUE.equals(p.getResult()) && (H.calledFrom(BIABLO_TILE, null) || H.calledFrom(PMC, null))) {
                        p.setResult(false);
                    }
                }
            });
        }
        // low_power gate inside the SR tile click
        H.hook(H.find(Settings.Global.class, "getInt", ContentResolver.class, String.class, int.class), new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                if ("low_power".equals(p.args[1]) && Cfg.on(Features.GAME_SR_COEXIST) && H.calledFrom(SR_TILE, null)) p.setResult(0);
            }
        });
        Lx.i("GameAssist: super-resolution hooks ready");
    }

    /** Diablo confirmation dialog: run the confirm action straight away. */
    private static void dialogs(ClassLoader cl) {
        Class<?> pmc = H.cls(cl, PMC);
        if (pmc == null) return;
        H.hook(H.resolveAny(pmc, void.class, H.types(Runnable.class), "showBiabloModeDialog", "F0"), new XC_MethodHook() {
            @Override protected void beforeHookedMethod(MethodHookParam p) {
                if (!Cfg.on(Features.GAME_NO_DIALOGS)) return;
                try { ((Runnable) p.args[0]).run(); } catch (Throwable t) { Lx.e("Diablo confirm runnable", t); }
                p.setResult(null);
            }
        });
    }
}
