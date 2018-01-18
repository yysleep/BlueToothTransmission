package com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class BlueToothSendFileThread extends Thread {

    private final static String TAG = "BlueToothSendFileThread";
    private final static long FINISH = 1;
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private List<String> mFiles;


    public BlueToothSendFileThread(Handler handler) {
        // Use a temporary object that is later assigned to mSocket,
        // because mSocket is final
        BluetoothSocket tmp = null;
        mDevice = BluetoothSys.getInstance().getDevice();
        mBluetoothAdapter = BluetoothSys.getInstance().getBlueToothAdapter();
        mHandler = handler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mDevice.createRfcommSocketToServiceRecord(BluetoothConstant.MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }

    public void run() {
        if (mDevice == null || mSocket == null) {
            return;
        }
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
        LogUtil.d(TAG, "[run] mSocket = " + mSocket);
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
            sendFile();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mSocket);
        cancel();
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

    private void sendFile() {
        if (mSocket == null) {
            return;
        }
        // Todo
        mFiles = new ArrayList<>();
        mFiles.add(Constant.PATH);

        LogUtil.d(TAG, "[sendFile] 开始发送文件");
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        for (String path : mFiles) {
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }
            long fileSize = file.length();
            LogUtil.d(TAG, "[sendFile] file.length" + file.length() + " --- " + fileSize);
            long sendSize = 0;
            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(new FileInputStream(file));
                buffOut = new BufferedOutputStream(mSocket.getOutputStream());
                while ((len = buffIn.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, len);
                    if (fileSize > 0) {
                        sendSize = len + sendSize;
                        sendMessage(BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION, sendSize, fileSize);
                    }
                    buffOut.flush();
                }
                Thread.sleep(500);
                sendMessage(BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION, FINISH, FINISH);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (buffIn != null) {
                    try {
                        buffIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (buffOut != null) {
                    try {
                        buffOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        LogUtil.d(TAG, "[sendFile] 发送文件结束");
    }

    private void sendMessage(int what, long sendSize, long fileSize) {
        Message msg = Message.obtain();
        msg.what = what;
        switch (what) {
            case BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION:
                msg.obj = (int) ((float) 100 * sendSize / fileSize);
                break;

            case BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION:
                msg.obj = 100;

            default:
                msg.obj = 100;
                break;
        }
        mHandler.sendMessage(msg);
    }

    public static class BluetoothHandler extends Handler {
        public final static int SEND_FINISH = 300;
        private final WeakReference<BlueToothSendFileThread> w;

        public BluetoothHandler(BlueToothSendFileThread t) {
            w = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            BlueToothSendFileThread t = w.get();
            if (t == null)
                return;
            switch (msg.what) {
                case SEND_FINISH:
                    t.cancel();
                    break;
            }
        }
    }
}
