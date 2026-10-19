package com.hammad.rmtweaks;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Custom charging-animation frames stored under files/charging/custom/NNN.ext */
public final class Frames {
    private Frames() {}

    public static File dir(Context c) {
        File d = new File(c.getFilesDir(), "charging/custom");
        //noinspection ResultOfMethodCallIgnored
        d.mkdirs();
        return d;
    }

    public static File[] list(Context c) {
        File[] f = dir(c).listFiles((d, n) -> n.matches("\\d{3}\\.(png|webp|jpg|jpeg)"));
        if (f == null) return new File[0];
        Arrays.sort(f);
        return f;
    }

    public static int count(Context c) { return list(c).length; }

    public static File frame(Context c, int index) {
        File[] f = list(c);
        if (f.length == 0) return null;
        return f[Math.floorMod(index, f.length)];
    }

    /** Copy every image in the chosen folder (sorted by name) into our store. Returns how many were imported. */
    public static int importFolder(Context c, Uri tree) {
        DocumentFile root = DocumentFile.fromTreeUri(c, tree);
        if (root == null) return 0;
        List<DocumentFile> files = new ArrayList<>();
        for (DocumentFile f : root.listFiles()) {
            String n = f.getName();
            if (f.isFile() && n != null && n.toLowerCase(Locale.ROOT).matches(".*\\.(png|webp|jpg|jpeg)")) files.add(f);
        }
        files.sort((a, b) -> String.valueOf(a.getName()).compareToIgnoreCase(String.valueOf(b.getName())));
        for (File old : list(c)) //noinspection ResultOfMethodCallIgnored
            old.delete();
        int n = 0;
        for (DocumentFile f : files) {
            String name = String.valueOf(f.getName());
            String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            if (ext.equals("jpeg")) ext = "jpg";
            File out = new File(dir(c), String.format(Locale.ROOT, "%03d.%s", n, ext));
            try (InputStream in = c.getContentResolver().openInputStream(f.getUri()); FileOutputStream o = new FileOutputStream(out)) {
                if (in == null) continue;
                byte[] buf = new byte[65536];
                int r;
                while ((r = in.read(buf)) > 0) o.write(buf, 0, r);
                n++;
            } catch (Exception ignored) { }
            if (n >= 400) break;
        }
        return n;
    }
}
