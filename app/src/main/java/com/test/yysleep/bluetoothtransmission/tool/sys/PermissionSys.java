package com.test.yysleep.bluetoothtransmission.tool.sys;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.test.yysleep.bluetoothtransmission.util.FileLogUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class PermissionSys {

    private volatile static PermissionSys instance;

    private PermissionSys() {

    }

    private static Set<String> sSet;

    public static PermissionSys getInstance() {
        if (instance == null) {
            synchronized (PermissionSys.class) {
                if (instance == null) {
                    instance = new PermissionSys();
                    sSet = new HashSet<>();
                }
            }
        }
        return instance;
    }

    public void checkPermission(Context context, String[] permissions) {
        if (permissions == null)
            return;

        for (String permission : permissions) {
            boolean havePermission = ContextCompat.checkSelfPermission(context.getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED;
            if (!havePermission) {
                sSet.add(permission);
            }
        }
    }

    public void requestPermission(Activity activity, int requestCode) {
        if (sSet.isEmpty()) {
            FileLogUtil.init();
            return;
        }
        String[] permissions = getPermissions();
        if (permissions == null || permissions.length == 0)
            return;
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    private String[] getPermissions() {
        if (sSet.isEmpty())
            return null;
        return sSet.toArray(new String[sSet.size()]);
    }

    public void removePermission(String permission) {
        if (sSet.isEmpty())
            return;
        if (sSet.contains(permission)) {
            sSet.remove(permission);
        }
    }

    public boolean testDone() {
        return sSet.isEmpty();
    }
}
