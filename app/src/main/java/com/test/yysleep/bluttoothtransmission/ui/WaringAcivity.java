package com.test.yysleep.bluttoothtransmission.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.test.yysleep.bluttoothtransmission.R;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class WaringAcivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waring);
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
