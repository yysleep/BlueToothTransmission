package com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class BlueToothAcceptFileThread extends Thread {

    private final static String TAG = "BlueToothAcceptFileThread";
    private final BluetoothServerSocket mmServerSocket;
    private List<String> mFiles;

    public BlueToothAcceptFileThread(Handler handler) {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = BluetoothSys.getInstance().getBlueToothAdapter().listenUsingRfcommWithServiceRecord(Constant.PACKAGE_NAME, BluetoothConstant.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmServerSocket = tmp;
    }

    public void run() {
        LogUtil.d(TAG, "[run] mmServerSocket = " + mmServerSocket);
        if (mmServerSocket == null)
            return;

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                LogUtil.d(TAG, "[run] 开始一轮新的等待");
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            acceptFile(socket);

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel(BluetoothSocket socket) {
        try {
            if (socket != null) {
                mmServerSocket.close();
            }
            LogUtil.d(TAG, "[cancel] over");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptFile(BluetoothSocket socket) {
        if (socket == null)
            return;
        // Todo
        mFiles = new ArrayList<>();
        mFiles.add(Constant.PATH);

        LogUtil.d(TAG, "[acceptFile] 开始接受文件");
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        for (String path : mFiles) {

            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(socket.getInputStream());
                buffOut = new BufferedOutputStream(new FileOutputStream(path));
                while ((len = buffIn.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, len);
                    buffOut.flush();
                }
                Thread.sleep(500);
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

        LogUtil.d(TAG, "[acceptFile] 文件接受完毕");
        File file = new File(Constant.PATH);
        LogUtil.d(TAG, "[acceptFile] file.exists : " + file.exists() + " --- " + file.length());
    }
}
