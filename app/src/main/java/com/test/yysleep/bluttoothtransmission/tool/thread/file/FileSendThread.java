package com.test.yysleep.bluttoothtransmission.tool.thread.file;

import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.os.Handler;

import com.test.yysleep.bluttoothtransmission.Constant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 *
 * @author yysleep
 */

public class FileSendThread extends Thread {

    private BluetoothSocket mSocket;

    private List<String> mFiles;
    private Handler mHandler;

    public FileSendThread(BluetoothSocket socket, List<String> files, Handler handler) {
        mSocket = socket;
        mFiles = files;

        // todo
        mFiles = new ArrayList<>();
        mFiles.add(Constant.PATH);
    }

    @Override
    public void run() {
        sendFile();
    }

    private void sendFile() {
        if (mSocket == null || mFiles == null || mFiles.size() == 0)
            return;
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
                mHandler.obtainMessage(200).sendToTarget();
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
    }

}
