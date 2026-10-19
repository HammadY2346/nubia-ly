package com.hammad.rmtweaks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/** World-readable settings file (LSPosed redirects it so hooked apps can read it via XSharedPreferences). */
public final class Prefs {
    private Prefs() {}
    private static SharedPreferences sp;
    private static boolean worldReadable;

    @SuppressLint("WorldReadableFiles")
    public static synchronized SharedPreferences get(Context c) {
        if (sp == null) {
            Context app = c.getApplicationContext();
            try {
                //noinspection deprecation
                sp = app.getSharedPreferences(Features.PREFS, Context.MODE_WORLD_READABLE);
                worldReadable = true;
            } catch (SecurityException e) {
                sp = app.getSharedPreferences(Features.PREFS, Context.MODE_PRIVATE);
                worldReadable = false;
            }
        }
        return sp;
    }

    public static boolean isWorldReadable(Context c) { get(c); return worldReadable; }

    public static boolean on(Context c, String key) { return get(c).getBoolean(key, false); }
    public static int choice(Context c, String key) { return get(c).getInt(key, 0); }
    public static void set(Context c, String key, boolean v) { get(c).edit().putBoolean(key, v).commit(); }
    public static void set(Context c, String key, int v) { get(c).edit().putInt(key, v).commit(); }

    public static int enabledCount(Context c, Features.Section s) {
        int n = 0;
        for (Features.Feature f : s.features) {
            if (f.type == Features.Type.SWITCH ? on(c, f.key) : choice(c, f.key) > 0) n++;
        }
        return n;
    }
}
