package com.example.android.camera2basic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by way on 16/2/28.
 */
public class OsUtil {
    public final static String[] sRequiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static boolean sIsAtLeastM;
    private static Hashtable<String, Integer> sPermissions = new Hashtable<String, Integer>();

    static {
        sIsAtLeastM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * @return True if the version of Android that we're running on is at least M
     * (API level 23).
     */
    public static boolean isAtLeastM() {
        return sIsAtLeastM;
    }

    /**
     * Does the app have all the specified permissions
     */
    public static boolean hasPermissions(final String[] permissions) {
        for (final String permission : permissions) {
            if (!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermission(final String permission) {
        if (OsUtil.isAtLeastM()) {
            // It is safe to cache the PERMISSION_GRANTED result as the process gets killed if the
            // user revokes the permission setting. However, PERMISSION_DENIED should not be
            // cached as the process does not get killed if the user enables the permission setting.
            if (!sPermissions.containsKey(permission)
                    || sPermissions.get(permission) == PackageManager.PERMISSION_DENIED) {
                final Context context = App.getContext();
                final int permissionState = context.checkSelfPermission(permission);
                sPermissions.put(permission, permissionState);
            }
            return sPermissions.get(permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static String[] getMissingPermissions(final String[] permissions) {
        final ArrayList<String> missingList = new ArrayList<String>();
        for (final String permission : permissions) {
            if (!hasPermission(permission)) {
                missingList.add(permission);
            }
        }

        final String[] missingArray = new String[missingList.size()];
        missingList.toArray(missingArray);
        return missingArray;
    }

    /**
     * Does the app have the minimum set of permissions required to operate.
     */
    public static boolean hasRequiredPermissions() {
        return hasPermissions(sRequiredPermissions);
    }

    public static String[] getMissingRequiredPermissions() {
        return getMissingPermissions(sRequiredPermissions);
    }

    public static boolean redirectToPermissionCheckIfNeeded(final Activity activity) {
        if (!sIsAtLeastM || OsUtil.hasRequiredPermissions()) {
            return false;
        }

        final Intent intent = new Intent(activity, PermissionCheckActivity.class);
        activity.startActivity(intent);
        activity.finish();
        return true;
    }

}
