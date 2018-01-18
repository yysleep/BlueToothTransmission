package com.test.yysleep.bluttoothtransmission.manager;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.model.FileInfo;
import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.connect.SendDataThread;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class BlueToothConnectManager extends Thread {

    private final static String TAG = "BlueToothConnectManager";
    private final static long FINISH = 1;
    private final BluetoothSocket mSocket;
    private Handler mHandler;

    public BlueToothConnectManager(Handler handler) {
        BluetoothSocket tmp = null;
        mHandler = handler;
        try {
            tmp = BluetoothSys.getInstance().getDevice().createRfcommSocketToServiceRecord(BluetoothConstant.MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mSocket.close();
            LogUtil.d(TAG, "[cancel] over");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        if (mSocket == null) {
            return;
        }
        new SendDataThread(mSocket, mHandler).start();
    }

}
