package com.test.yysleep.bluttoothtransmission.tool.sys;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.test.yysleep.bluttoothtransmission.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

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
    private List<FileInfo> mAcceptFiles;
    private List<String> mSendFilePaths;

    private BluetoothSys() {
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        mAcceptFiles = new ArrayList<>();
        mSendFilePaths = new ArrayList<>();
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

    public List<FileInfo> getAcceptFiles() {
        return mAcceptFiles;
    }

    public void clearAcceptFiles() {
        if (mAcceptFiles != null)
            mAcceptFiles.clear();
    }

    public List<String> getSendFilePaths() {
        return mSendFilePaths;
    }

    public void clearSendFilePaths() {
        if (mSendFilePaths != null)
            mSendFilePaths.clear();
    }
}
