package com.test.yysleep.bluetoothtransmission.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.test.yysleep.bluetoothtransmission.R;
import com.test.yysleep.bluetoothtransmission.util.LogUtil;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public abstract class BaseActivity extends AppCompatActivity {

    private final static String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        int id = getToolbarId();
        if (id > 0) {
            Toolbar toolbar = findViewById(id);
            if (toolbar == null)
                return;
                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayShowTitleEnabled(false);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
                String title = getToolBarTitle();
                if (title != null) {
                    toolbar.setTitle(title);
                }
                toolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public int getToolbarId() {
        return R.id.toolbar;
    }

    public abstract String getToolBarTitle();

}
