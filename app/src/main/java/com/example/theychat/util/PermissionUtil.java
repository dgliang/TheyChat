package com.example.theychat.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    private final static String TAG = "PermissionUtil";

    // 检查某个权限。true 表示已启用该权限，false 表示未启用该权限
    public static boolean checkPermission(Activity act, String permission, int requestCode) {
        return checkPermission(act, new String[]{permission}, requestCode);
    }

    // 检查多个权限。返回true表示已完全启用权限，返回false表示未完全启用权限
    public static boolean checkPermission(Activity act, String[] permissions, int requestCode) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int check = PackageManager.PERMISSION_GRANTED;

            for (String permission : permissions) {
                check = ContextCompat.checkSelfPermission(act, permission);
                // 有权限没开启
                if (check != PackageManager.PERMISSION_GRANTED) {
                    break;
                }
            }
            if (check != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(act, permissions, requestCode);
                result = false;
            }
        }
        return result;
    }

    // 检查权限结果数组，true 表示都已经获得授权。false 表示至少有一个未获得授权
    public static boolean checkGrant(int[] grantResults) {
        boolean result = true;
        if (grantResults != null) {
            for (int grant : grantResults) {
                // 未获得授权
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
        }
        return result;
    }
}
