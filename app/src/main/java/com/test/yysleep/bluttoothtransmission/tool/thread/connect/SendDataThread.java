package com.test.yysleep.bluttoothtransmission.tool.thread.connect;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

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
import java.io.OutputStream;
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
        LogUtil.d(TAG, "[sendFileInfo] 开始发送文件信息");
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        // Todo
        List<String> mCheckedFilePaths = new ArrayList<>();
        mCheckedFilePaths.add(Constant.PATH_01);
        mCheckedFilePaths.add(Constant.PATH_02);
        mCheckedFilePaths.add(Constant.PATH_03);
        mCheckedFilePaths.add(Constant.PATH_04);
        mCheckedFilePaths.add(Constant.PATH_05);

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
            out = new BufferedOutputStream(mSocket.getOutputStream());
            out.write(bytes, 0, bytes.length);
            out.flush();
            LogUtil.d(TAG, "[sendFileInfo] 发送文件信息结束");

            in = new BufferedInputStream(mSocket.getInputStream());
            boolean acceptFileInfoState = acceptState(in, BluetoothConstant.FLAG_ACCEPT_FILE_INFO_SUCCESS);
            LogUtil.d(TAG, "[sendFileInfo] 发送文件信息结束 acceptFileInfoState = " + acceptFileInfoState);


        } catch (IOException exception) {
            exception.printStackTrace();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
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
        int fileNum = 1;
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
                        sendMessage(BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION, "第" + fileNum + "个文件 ：" + (int) ((float) 100 * sendSize / fileSize) + "%");
                    }
                    if (sendSize >= fileSize)
                        break;
                }
                fileNum++;
                in.close();


                in = new BufferedInputStream(mSocket.getInputStream());
                boolean acceptFileState = acceptState(in, BluetoothConstant.FLAG_ACCEPT_FILE_SUCCESS);
                LogUtil.d(TAG, "[transportFile] 继续下个文件 acceptFileState = " + acceptFileState);

            } catch (IOException e) {
                e.printStackTrace();
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION, 1000);
        LogUtil.d(TAG, "[transportFile] 发送文件结束");
    }

    private void sendMessage(int what, String content) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = content;
        mHandler.sendMessage(msg);
    }

    private boolean acceptState(InputStream in, String flag) throws IOException {
        byte[] b = new byte[flag.getBytes().length];
        int len = in.read(b);
        return flag.equals(new String(b));
    }
}
