package com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;


import com.test.yysleep.bluttoothtransmission.Constant;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class BlueToothPairThread extends Thread {

    private static final String TAG = "BlueToothPairThread";
    private BluetoothDevice mDevice;
    private boolean mIsConnect;
    private BluetoothAdapter mAdapter;

    public BlueToothPairThread(BluetoothDevice device, boolean isConnect) {
        mDevice = device;
        mIsConnect = isConnect;
    }

    @Override
    public void run() {
        try {
            LogUtil.d(TAG, "[BlueToothPairThread][run] 开始目标设备的连接/断开 " + mDevice.getAddress());
            String task = "createBond";
            if (!mIsConnect) {
                task = "removeBond";
            }
            // createBond or removeBond
            Method createBondMethod = BluetoothDevice.class.getMethod(task);
            boolean returnValue = (Boolean) createBondMethod.invoke(mDevice);

            LogUtil.d(TAG, "[BlueToothPairThread][run]  returnValue = " + returnValue);

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d(TAG, "[BlueToothPairThread][run] 连接/断开 异常");
        }

    }

}
