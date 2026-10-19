package com.hammad.rmtweaks.xposed;

import com.hammad.rmtweaks.Features;

import de.robv.android.xposed.XC_MethodHook;

/** com.zte.beautify (Theme Store) — present the phone as a collaboration edition. */
final class ThemeHooks {
    private ThemeHooks() {}

    private static String[] variant() {
        int i = Cfg.choice(Features.THEME_VARIANT);
        if (i <= 0 || i >= Features.THEME_VARIANTS.length) return null;
        return Features.THEME_VARIANTS[i];
    }

    static void install(ClassLoader cl) {
        Class<?> u = H.cls(cl, "com.zte.beautify.view.common.tools.Utilities");
        if (u == null) return;
        H.hook(H.find(u, "getThemeResourceFeature"), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                String[] v = variant();
                if (v != null) p.setResult(v[1]);
            }
        });
        H.hook(H.find(u, "getStringSystemProperties", String.class), new XC_MethodHook() {
            @Override protected void afterHookedMethod(MethodHookParam p) {
                String[] v = variant();
                if (v != null && "persist.vendor.custom.variant.id".equals(p.args[0])) p.setResult(v[2]);
            }
        });
        Lx.i("Theme: variant hooks ready (" + (variant() == null ? "off" : variant()[0]) + ")");
    }
}
