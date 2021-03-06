package com.test.yysleep.bluetoothtransmission;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.test.yysleep.bluetoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluetoothtransmission.util.ToastUtil;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class BlueToothTransmissionApplication extends Application {

    private static Handler sHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothSys.getInstance().init();
        ToastUtil.init(this);

        sHandler = new Handler(Looper.getMainLooper());
    }

    public static Handler getsHandler() {
        return sHandler;
    }

}
