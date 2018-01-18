package com.test.yysleep.bluttoothtransmission.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.test.yysleep.bluttoothtransmission.R;
import com.test.yysleep.bluttoothtransmission.sys.PermissionSys;
import com.test.yysleep.bluttoothtransmission.ui.bluetooth.BlueToothActivity;
import com.test.yysleep.bluttoothtransmission.util.FileLogUtil;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;


/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {
        RelativeLayout l;
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissionRequest();
    }

    public void onMainClick(View v) {
        switch (v.getId()) {

            case R.id.main_goto_bluetooth_btn:
                startActivity(new Intent(MainActivity.this, BlueToothActivity.class));
                break;

            case R.id.main_test_service_btn:

                break;

            default:
                break;
        }
    }

    private void permissionRequest() {
        PermissionSys permissionSys = PermissionSys.getInstance();
        permissionSys.checkPermission(this, null);
        permissionSys.requestPermission(this, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isFinish = false;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length == 0 || permissions.length == 0) {
                    Toast.makeText(this, "权限被拒，请去设置权限", Toast.LENGTH_SHORT).show();
                    break;
                }
                for (int i = 0; i < permissions.length; i++) {
                    LogUtil.d(TAG, "permissions = " + permissions[i] + "grantResults = " + grantResults[i]);
                    if (grantResults[i] != 0) {
                        isFinish = true;
                    }
                }
                break;

            default:
                break;
        }
        if (isFinish) {
            startActivity(new Intent(this, WaringAcivity.class));
            finish();
        } else {
            FileLogUtil.init();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
