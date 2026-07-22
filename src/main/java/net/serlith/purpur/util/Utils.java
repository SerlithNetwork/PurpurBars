package net.serlith.purpur.util;

import org.bukkit.NamespacedKey;
import org.jspecify.annotations.Nullable;

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

    public static @Nullable NamespacedKey keyOrNullFromString(String fullKey) {
        String[] splits = fullKey.split(":");
        if (splits.length != 2) {
            return null;
        }

        String namespace = splits[0];
        String key = splits[1];
        if (namespace.isBlank() || key.isBlank()) {
            return null;
        }

        return new NamespacedKey(namespace, key);
    }

}
