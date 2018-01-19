package com.test.yysleep.bluttoothtransmission.manager;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.tool.thread.accept.AcceptDataThread;
import com.test.yysleep.bluttoothtransmission.tool.thread.send.SendDataThread;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.IOException;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class BlueToothConnectManager extends Thread {

    private final static String TAG = "BlueToothConnectManager";
    private final static long FINISH = 1;
    private Handler mHandler;
    private AcceptDataThread mAcceptThread;
    private SendDataThread mSendThread;

    public BlueToothConnectManager(Handler handler) {
        mHandler = handler;
    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancelAccept() {
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }
    }

    public void cancelSend() {
        if (mSendThread != null) {
            mSendThread.cancel();
        }
    }

    public void executeConnect() {
        //cancelSend();
        BluetoothSocket mSocket = null;
        try {
            mSocket = BluetoothSys.getInstance().getDevice().createRfcommSocketToServiceRecord(BluetoothConstant.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mSocket == null) {
            LogUtil.e(TAG, "[executeConnect] failed , socket is null");
            return;
        }
        mSendThread = new SendDataThread(mSocket, mHandler);
        mSendThread.start();
    }

    public void executeAccept() {
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptDataThread(mHandler);
            mAcceptThread.start();
        }
    }

}
