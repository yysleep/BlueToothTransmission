package com.test.yysleep.bluetoothtransmission.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.test.yysleep.bluetoothtransmission.R;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class WaringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_waring);
        super.onCreate(savedInstanceState);
    }

    public void onWaringClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return super.onKeyDown(keyCode, event);
    }
}
