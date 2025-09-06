package net.serlith.purpur.util;

public class Utils {

    private static final char[] SIZES = { 'B', 'K', 'M', 'G', 'T', 'P', 'E' };

    public static String formatBytes(long bytes) {
        String value;
        if (bytes < 1024) {
            value = "%dB".formatted(bytes);
        } else {
            var z = (63 - Long.numberOfLeadingZeros(bytes)) / 10;
            if (z > 2) {
                value = "%.1f%c".formatted(((float) bytes) / (1L << (z * 10)), SIZES[z]);
            } else {
                value = "%d%c".formatted(bytes / (1L << (z * 10)), SIZES[z]);
            }
        }
        return value;
    }

}
