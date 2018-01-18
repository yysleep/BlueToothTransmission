package com.test.yysleep.bluttoothtransmission.tool.sys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by YySleep on 2018/1/18.
 *
 * @author YySleep
 */

public class BluetoothSys {

    private final static String TAG = "BluetoothSys";
    private static volatile BluetoothSys instance;
    private BluetoothAdapter mBlueAdapter;
    private BluetoothDevice mDevice;

    private BluetoothSys() {
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothSys getInstance() {
        if (instance == null) {
            synchronized (BluetoothSys.class) {
                if (instance == null) {
                    instance = new BluetoothSys();
                }
            }
        }
        return instance;
    }

    public void init() {

    }

    public BluetoothAdapter getBlueToothAdapter() {
        return mBlueAdapter;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        this.mDevice = device;
    }
}
