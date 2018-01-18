package com.test.yysleep.bluttoothtransmission.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class ToastUtil {

    private static volatile Toast toast;

    @SuppressLint("ShowToast")
    public static void toast(Context context, String content) {
        if (toast == null) {
            synchronized (ToastUtil.class) {
                if (toast == null) {
                    toast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
                }
            }
        }
        toast.setText(content);
        toast.show();
    }
}
