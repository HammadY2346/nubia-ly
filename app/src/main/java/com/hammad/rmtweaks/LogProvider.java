package com.hammad.rmtweaks;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Receives log lines from hooked processes: resolver.call(URI, "log", line, null).
 * Only packages in the module scope (plus ourselves) are accepted; everything else is dropped.
 */
public final class LogProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hammad.rmtweaks.log";
    public static final Uri URI = Uri.parse("content://" + AUTHORITY);
    public static final String METHOD_LOG = "log";
    public static final String METHOD_FRAME_COUNT = "frame_count";

    @Override public boolean onCreate() { return true; }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        Context c = getContext();
        if (c == null) return null;
        String caller = callerPackage(c);
        if (caller == null) return null;
        if (METHOD_FRAME_COUNT.equals(method)) {
            Bundle b = new Bundle();
            b.putInt("count", Frames.count(c));
            return b;
        }
        if (!METHOD_LOG.equals(method) || arg == null) return null;
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

    /** content://authority/charging/<index> -> one custom frame (index wraps). */
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Context c = getContext();
        if (c == null || callerPackage(c) == null) throw new FileNotFoundException("not allowed");
        java.util.List<String> seg = uri.getPathSegments();
        if (seg.size() == 2 && "charging".equals(seg.get(0))) {
            int idx;
            try { idx = Integer.parseInt(seg.get(1)); } catch (NumberFormatException e) { throw new FileNotFoundException(uri.toString()); }
            File f = Frames.frame(c, idx);
            if (f == null) throw new FileNotFoundException("no frames");
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        }
        throw new FileNotFoundException(uri.toString());
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) { return null; }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }
}
