package org.openlmis.core.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Utility class to handle PendingIntent flags compatibility
 * for Android 12+ (API 31+)
 */
public class PendingIntentCompat {
    
    /**
     * Get immutable flag for PendingIntent
     * Compatible with all Android versions
     */
    public static int getImmutableFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.FLAG_IMMUTABLE;
        }
        return 0;
    }
    
    /**
     * Get update current flag with immutable flag for Android 12+
     */
    public static int getUpdateCurrentFlag() {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return flags;
    }
    
    /**
     * Create a broadcast PendingIntent with proper flags
     */
    public static PendingIntent getBroadcast(Context context, int requestCode, Intent intent, int flags) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Ensure FLAG_IMMUTABLE is set for Android 12+
            if ((flags & PendingIntent.FLAG_IMMUTABLE) == 0 && (flags & PendingIntent.FLAG_MUTABLE) == 0) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
        }
        return PendingIntent.getBroadcast(context, requestCode, intent, flags);
    }
}
