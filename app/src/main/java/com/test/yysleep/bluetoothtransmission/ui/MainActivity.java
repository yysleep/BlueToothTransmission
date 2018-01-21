package com.test.yysleep.bluetoothtransmission.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.test.yysleep.bluetoothtransmission.R;
import com.test.yysleep.bluetoothtransmission.tool.sys.PermissionSys;
import com.test.yysleep.bluetoothtransmission.ui.base.BaseActivity;
import com.test.yysleep.bluetoothtransmission.ui.bluetooth.BlueToothActivity;
import com.test.yysleep.bluetoothtransmission.util.FileLogUtil;
import com.test.yysleep.bluetoothtransmission.util.LogUtil;
import com.test.yysleep.bluetoothtransmission.util.ToastUtil;


/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public String getToolBarTitle() {
        return "BluetoothTransmission";
    }

    private void init() {
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
                LogUtil.d(TAG, "[onMainClick] test");
                ToastUtil.toast("onMainClick");
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
            startActivity(new Intent(this, WaringActivity.class));
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
