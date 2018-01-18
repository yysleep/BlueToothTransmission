package com.test.yysleep.bluttoothtransmission;

import android.app.Application;

import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;

/**
 * Created by YySleep on 2018/1/17.
 * @author YySleep
 */

public class BlueToothTransmissionApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothSys.getInstance().init();
    }
}
