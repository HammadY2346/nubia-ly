package com.hammad.rmtweaks.xposed;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.ArrayDeque;

import de.robv.android.xposed.XposedBridge;

/**
 * Logging from inside hooked processes. Every line goes to the LSPosed log and, once the host
 * app has a Context, to the module's own log (shown in the app's Logs page).
 */
public final class Lx {
    private Lx() {}
    private static final Uri LOG_URI = Uri.parse("content://com.hammad.rmtweaks.log");
    private static final ArrayDeque<String> queue = new ArrayDeque<>();
    private static volatile Context ctx;
    private static Handler io;
    private static String tag = "?";

    static void init(String packageName) { tag = packageName; }

    static void attach(Context c) {
        if (c == null) return;
        ctx = c.getApplicationContext() != null ? c.getApplicationContext() : c;
        synchronized (queue) {
            if (io == null) {
                HandlerThread t = new HandlerThread("rmtweaks-log");
                t.start();
                io = new Handler(t.getLooper());
            }
        }
        flush();
    }

    public static void i(String msg) { log("I", msg); }
    public static void w(String msg) { log("W", msg); }
    public static void e(String msg, Throwable t) { log("E", msg + (t == null ? "" : " :: " + t)); }

    private static void log(String level, String msg) {
        String line = level + " " + msg;
        try { XposedBridge.log("RMTweaks[" + tag + "] " + line); } catch (Throwable ignored) { }
        synchronized (queue) {
            if (queue.size() > 400) queue.pollFirst();
            queue.addLast(line);
        }
        if (ctx != null) flush();
    }

    private static void flush() {
        final Context c = ctx;
        final Handler h = io;
        if (c == null || h == null) return;
        h.post(() -> {
            String line;
            while (true) {
                synchronized (queue) { line = queue.pollFirst(); }
                if (line == null) break;
                try {
                    c.getContentResolver().call(LOG_URI, "log", line, null);
                } catch (Throwable t) {
                    // provider not reachable (module app not installed / frozen) — drop silently
                    break;
                }
            }
        });
    }
}
