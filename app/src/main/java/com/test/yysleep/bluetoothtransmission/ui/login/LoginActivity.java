package com.test.yysleep.bluetoothtransmission.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.test.yysleep.bluetoothtransmission.R;
import com.test.yysleep.bluetoothtransmission.ui.MainActivity;
import com.test.yysleep.bluetoothtransmission.ui.base.BaseActivity;
import com.test.yysleep.bluetoothtransmission.util.LogUtil;

/**
 * 登录界面
 * @author yysleep
 */
public class LoginActivity extends BaseActivity {

    private final static String TAG = "LoginActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getToolBarTitle() {
        return "登录";
    }

    public void onClickLogin(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "[onStart]");
    }
}
