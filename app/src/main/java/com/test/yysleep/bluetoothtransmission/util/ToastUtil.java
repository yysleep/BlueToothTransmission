package com.test.yysleep.bluetoothtransmission.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.test.yysleep.bluetoothtransmission.BlueToothTransmissionApplication;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class ToastUtil {

    private static volatile Toast sToast;

    @SuppressLint("ShowToast")
    public static void toast(final String content) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sToast.setText(content);
            sToast.show();
        } else {
            BlueToothTransmissionApplication.getsHandler().post(new Runnable() {
                @Override
                public void run() {
                    sToast.setText(content);
                    sToast.show();
                }
            });
        }
    }

    @SuppressLint("ShowToast")
    public static void init(Context context) {
        if (sToast == null) {
            synchronized (ToastUtil.class) {
                if (sToast == null) {
                    sToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                }
            }
        }
    }

}
