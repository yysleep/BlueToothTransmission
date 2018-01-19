package com.test.yysleep.bluttoothtransmission.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.test.yysleep.bluttoothtransmission.R;
import com.test.yysleep.bluttoothtransmission.tool.sys.PermissionSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;
import com.test.yysleep.bluttoothtransmission.util.ToastUtil;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class GuideActivity extends AppCompatActivity {

    private static final String TAG = "GuideActivity";
    private static final int PERMISSION_REQUEST_CODE = 111;
    PermissionSys permissionSys;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        init();
        testAndRun01();
    }

    private void init() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void testAndRun01() {
        permissionSys = PermissionSys.getInstance();
        permissionSys.checkPermission(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE});
        permissionSys.requestPermission(this, PERMISSION_REQUEST_CODE);
        if (permissionSys.testDone()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length == 0 || permissions.length == 0) {
                    ToastUtil.toast("权限被拒，请去设置权限");
                    break;
                }
                for (int i = 0; i < permissions.length; i++) {
                    LogUtil.d(TAG, "permissions = " + permissions[i] + "grantResults = " + grantResults[i]);
                    if (grantResults[i] == 0) {
                        permissionSys.removePermission(permissions[i]);
                    }
                }
                break;

            default:
                break;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
