package com.test.yysleep.bluttoothtransmission.tool;

import android.bluetooth.BluetoothDevice;


import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.lang.reflect.Method;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class BlueToothConnectThread extends Thread {

    private static final String TAG = "BlueToothConnectThread";
    private BluetoothDevice mDevice;
    private boolean mIsConnect;

    public BlueToothConnectThread(BluetoothDevice device, boolean isConnect) {
        mDevice = device;
        mIsConnect = isConnect;
    }

    @Override
    public void run() {
        try {
            LogUtil.d(TAG, "[BlueToothConnectThread][run] 开始目标设备的连接/断开 " + mDevice.getAddress());
            String task = "createBond";
            if (!mIsConnect) {
                task = "removeBond";
            }
            // createBond or removeBond
            Method createBondMethod = BluetoothDevice.class.getMethod(task);
            boolean returnValue = (Boolean) createBondMethod.invoke(mDevice);

            LogUtil.d(TAG, "[BlueToothConnectThread][run]  returnValue = " + returnValue);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "[BlueToothConnectThread][run] 连接/断开 异常");
        }

    }

}
