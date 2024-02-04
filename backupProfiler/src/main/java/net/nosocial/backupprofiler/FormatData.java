package net.nosocial.backupprofiler;

import java.io.File;

/**
 * @author ikh
 * @since 10/18/19
 */
public class FormatData {
    public static String humanReadableByteCount(long bytes) {
        return humanReadableByteCount(bytes, true);
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%d %s", Math.round(bytes / Math.pow(unit, exp)), pre);
    }

    public static String formatFileName(File fileOrDir) {
        String path = fileOrDir.getPath();
        return path + (fileOrDir.isDirectory() && !path.endsWith(File.separator) ? File.separatorChar : "");
    }
}
