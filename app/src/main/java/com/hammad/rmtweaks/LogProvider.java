package com.hammad.rmtweaks;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;

/**
 * Receives log lines from hooked processes: resolver.call(URI, "log", line, null).
 * Only packages in the module scope (plus ourselves) are accepted; everything else is dropped.
 */
public final class LogProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hammad.rmtweaks.log";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_LOG = "log";

    @Override public boolean onCreate() { return true; }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        Context c = getContext();
        if (c == null || arg == null) return null;
        if (!METHOD_LOG.equals(method)) return null;
        String caller = callerPackage(c);
        if (caller == null) return null;
        if (arg.length() > 4000) arg = arg.substring(0, 4000);
        LogStore.append(c, LogStore.stamp() + " [" + caller + "] " + arg.replace('\n', ' '));
        return Bundle.EMPTY;
    }

    private String callerPackage(Context c) {
        int uid = Binder.getCallingUid();
        PackageManager pm = c.getPackageManager();
        String[] pkgs = pm.getPackagesForUid(uid);
        if (pkgs == null) return null;
        String[] scope = c.getResources().getStringArray(R.array.xposed_scope);
        for (String p : pkgs) {
            if (p.equals(c.getPackageName())) return p;
            for (String s : scope) if (s.equals(p)) return p;
        }
        return null;
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
