package com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Message;

import com.test.yysleep.bluttoothtransmission.Constant;
import com.test.yysleep.bluttoothtransmission.tool.thread.file.FileSendThread;
import com.test.yysleep.bluttoothtransmission.ui.bluetooth.BlueToothActivity;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class BlueToothConnectThread extends Thread {

    private final static String TAG = "BlueToothConnectThread";
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothHandler mHandler;
    private List<String> mFiles;

    public BlueToothConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
        // Use a temporary object that is later assigned to mSocket,
        // because mSocket is final
        BluetoothSocket tmp = null;
        mDevice = device;
        mBluetoothAdapter = adapter;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(Constant.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;
    }

    public void run() {
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
            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(new FileInputStream(file));
                buffOut = new BufferedOutputStream(mSocket.getOutputStream());
                while ((len = buffIn.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, len);
                    buffOut.flush();
                }
            } catch (IOException e) {
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

    public static class BluetoothHandler extends android.os.Handler {
        public final static int SEND_FINISH = 300;
        private final WeakReference<BlueToothConnectThread> w;

        public BluetoothHandler(BlueToothConnectThread t) {
            w = new WeakReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            BlueToothConnectThread t = w.get();
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
