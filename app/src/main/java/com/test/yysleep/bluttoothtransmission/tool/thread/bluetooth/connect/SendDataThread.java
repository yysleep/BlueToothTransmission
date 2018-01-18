package com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.connect;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.model.FileInfo;
import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 *
 * @author yysleep
 */

public class SendDataThread extends Thread {

    private final static String TAG = "SendDataThread";
    private final static long FINISH = 1;
    private final BluetoothSocket mSocket;
    private Handler mHandler;

    public SendDataThread(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        mHandler = handler;
    }

    @Override
    public void run() {
        BluetoothSys.getInstance().getBlueToothAdapter().cancelDiscovery();
        LogUtil.d(TAG, "[run] mSocket = " + mSocket);
        try {
            mSocket.connect();
            sendFileInfo();
            transportFile();
            mHandler.sendEmptyMessageDelayed(BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION, 1000);
        } catch (IOException connectException) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendFileInfo() {
        if (mSocket == null) {
            return;
        }
        LogUtil.d(TAG, "[transportFile] 开始发送文件信息");
        BufferedOutputStream buffOut = null;
        // Todo
        List<String> mCheckedFilePaths = new ArrayList<>();
        mCheckedFilePaths.add(Constant.PATH);

        BluetoothSys.getInstance().clearTransportFiles();
        List<FileInfo> mFileList = BluetoothSys.getInstance().getTransportFiles();

        StringBuilder builder = new StringBuilder();
        for (String path : mCheckedFilePaths) {
            File file = new File(path);
            if (file.exists()) {
                long length = file.length();
                builder.append(path).append(" ").append(length).append(" ");
                FileInfo info = new FileInfo();
                info.setLength(length);
                info.setPath(path);
                mFileList.add(info);
            }
        }
        String s = builder.toString();
        byte[] bytes = s.getBytes();
        if (bytes.length == 0) {
            return;
        }
        try {
            buffOut = new BufferedOutputStream(mSocket.getOutputStream());
            buffOut.write(bytes, 0, bytes.length);
            buffOut.flush();
            LogUtil.d(TAG, "[transportFile] 发送文件信息结束");

            mSocket.connect();
            /*InputStream inputStream = new BufferedInputStream(mSocket.getInputStream());
            int l = inputStream.read(new byte[100]);*/
            LogUtil.d(TAG, "[transportFile] 收到回传消息 ");


        } catch (IOException exception) {
            exception.printStackTrace();
            if (buffOut != null) {
                try {
                    buffOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void transportFile() {
        if (mSocket == null) {
            return;
        }

        List<FileInfo> mFileList = BluetoothSys.getInstance().getTransportFiles();

        LogUtil.d(TAG, "[transportFile] 开始发送文件");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        for (FileInfo info : mFileList) {
            File file = new File(info.getPath());
            if (!file.exists()) {
                continue;
            }
            long fileSize = file.length();
            LogUtil.d(TAG, "[transportFile] file.length" + file.length() + " --- " + fileSize);
            long sendSize = 0;
            try {
                byte[] bytes = new byte[1024];
                int len = 0;
                in = new BufferedInputStream(new FileInputStream(file));
                out = new BufferedOutputStream(mSocket.getOutputStream());
                while ((len = in.read(bytes)) != -1) {
                    out.write(bytes, 0, len);
                    out.flush();
                    if (fileSize > 0) {
                        sendSize = len + sendSize;
                        sendMessage(BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION, sendSize, fileSize);
                    }
                    if (sendSize >= fileSize)
                        break;
                }

                mSocket.connect();
                LogUtil.d(TAG, "[transportFile] 继续下个文件");

            } catch (IOException e) {
                e.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        LogUtil.d(TAG, "[transportFile] 发送文件结束");
    }

    private void sendMessage(int what, long sendSize, long fileSize) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = (int) ((float) 100 * sendSize / fileSize);
        mHandler.sendMessage(msg);
    }
}
