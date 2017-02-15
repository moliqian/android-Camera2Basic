package com.example.android.camera2basic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


/**
 * Activity to check if the user has required permissions. If not, it will try to prompt the user
 * to grant permissions. However, the OS may not actually prompt the user if the user had
 * previously checked the "Never ask again" checkbox while denying the required permissions.
 */
public class PermissionCheckActivity extends Activity {
    private static final String TAG = "PermissionCheckActivity";
    private static final int REQUIRED_PERMISSIONS_REQUEST_CODE = 1;
    private static final String PACKAGE_URI_PREFIX = "package:";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showRequestPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_title)
                .setCancelable(false)
                .setMessage(R.string.required_permissions_all)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), R.string.permission_refused, Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tryRequestPermission();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (OsUtil.hasRequiredPermissions()) {
            redirect();
        } else {
            tryRequestPermission();
        }
    }


    private void tryRequestPermission() {
        final String[] missingPermissions = OsUtil.getMissingRequiredPermissions();
        if (missingPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, missingPermissions, REQUIRED_PERMISSIONS_REQUEST_CODE);
        }else {
            redirect();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode, @NonNull final String permissions[], @NonNull final int[] grantResults) {
        if (requestCode == REQUIRED_PERMISSIONS_REQUEST_CODE) {
            // We do not use grantResults as some of the granted permissions might have been
            // revoked while the permissions dialog box was being shown for the missing permissions.
            if (OsUtil.hasRequiredPermissions()) {
                redirect();
            } else {
                final int length = grantResults.length;
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            //Show permission explanation dialog...
                            finish();
                            return;
                        } else {
                            //Never ask again selected, or device policy prohibits the app from having that permission.
                            //So, disable that feature, or fall back to another situation...
                            gotoSettings();
                            return;
                        }
                    }
                }
            }
        }
    }
    private Dialog mDialog;
    private void gotoSettings() {
        if(mDialog != null && mDialog.isShowing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionCheckActivity.this).setTitle(R.string.permission_title)
                .setCancelable(false)
                .setMessage(R.string.enable_permission_procedure)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse(PACKAGE_URI_PREFIX + getPackageName()));
                        startActivity(intent);
                    }
                });
        mDialog = builder.create();
        mDialog.show();
    }

    private void redirect() {
        Intent i = new Intent(this, CameraActivity.class);
        startActivity(i);
        finish();
    }

}
