package com.test.yysleep.bluttoothtransmission.tool.thread.file;

import android.bluetooth.BluetoothSocket;
import android.os.Environment;

import com.test.yysleep.bluttoothtransmission.Constant;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 *
 * @author yysleep
 */

public class FileAcceptThread extends Thread {

    private final static String TAG = "FileAcceptThread";
    private BluetoothSocket mSocket;
    private List<String> mFiles;

    public FileAcceptThread(BluetoothSocket socket, List<String> files) {
        mSocket = socket;

        mFiles = files;

        // todo
        mFiles = new ArrayList<>();
        mFiles.add(Constant.PATH);

    }

    @Override
    public void run() {
        acceptFile();
        File file = new File(Constant.PATH);
        LogUtil.d(TAG, "[run] file.exists : " + file.exists() + " --- " + file.length());
    }

    private void acceptFile() {
        if (mSocket == null || mFiles == null || mFiles.size() == 0)
            return;
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        for (String path : mFiles) {

            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(mSocket.getInputStream());
                buffOut = new BufferedOutputStream(new FileOutputStream(path));
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
    }

}
