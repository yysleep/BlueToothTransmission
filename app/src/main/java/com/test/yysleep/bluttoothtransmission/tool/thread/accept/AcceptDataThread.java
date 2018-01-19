package com.test.yysleep.bluttoothtransmission.tool.thread.accept;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.model.FileInfo;
import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/18.
 *
 * @author yysleep
 */

public class AcceptDataThread extends Thread {

    private final static String TAG = "AcceptDataThread";
    private final static long FINISH = 1;
    private final BluetoothServerSocket mServerSocket;
    private Handler mHandler;

    public AcceptDataThread(Handler handler) {
        // Use a temporary object that is later assigned to mServerSocket,
        // because mServerSocket is final
        BluetoothServerSocket tmp = null;
        mHandler = handler;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = BluetoothSys.getInstance().getBlueToothAdapter().listenUsingRfcommWithServiceRecord(Constant.PACKAGE_NAME, BluetoothConstant.MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = tmp;
    }

    public void run() {
        LogUtil.d(TAG, "[run] mServerSocket = " + mServerSocket);
        if (mServerSocket == null)
            return;

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                LogUtil.d(TAG, "[run] 开始一轮新的等待");
                socket = mServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (BluetoothSys.getInstance().getTransportFiles().size() == 0) {
                acceptDataInfo(socket);
            } else {
                acceptFile(socket);
            }

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
                mServerSocket.close();
            }
            LogUtil.d(TAG, "[cancel] over");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptDataInfo(BluetoothSocket socket) {
        byte[] srcBytes = new byte[1024];
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(socket.getInputStream());
            out = new BufferedOutputStream(socket.getOutputStream());
            int len = in.read(srcBytes);
            if (len <= 0)
                return;

            byte[] destBytes = new byte[len];
            System.arraycopy(srcBytes, 0,
                    destBytes, 0,
                    len);
            String s = new String(destBytes);

            Gson g = new Gson();
            String[] array = s.trim().split(" ");
            BluetoothSys.getInstance().clearTransportFiles();
            List<FileInfo> files = BluetoothSys.getInstance().getTransportFiles();
            for (int i = 0; i < array.length; i = i + 2) {
                FileInfo info = new FileInfo();
                info.setPath(array[i]);
                info.setLength(Long.valueOf(array[i + 1]));
                files.add(info);
                LogUtil.d(TAG, "[acceptDataInfo] path = " + array[i]);
            }
            replyState(out);

        } catch (IOException e) {
            e.printStackTrace();

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    private void acceptFile(BluetoothSocket socket) {
        if (socket == null)
            return;

        List<FileInfo> list = BluetoothSys.getInstance().getTransportFiles();

        LogUtil.d(TAG, "[acceptFile] 开始接受文件");
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        for (FileInfo info : list) {
            try {
                long fileSize = info.getLength();
                long acceptSize = 0;
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(socket.getInputStream());
                buffOut = new BufferedOutputStream(new FileOutputStream(info.getPath()));
                while ((len = buffIn.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, len);
                    if (fileSize >= 0) {
                        acceptSize = len + acceptSize;
                        sendMessage(BluetoothConstant.MESSAGE_UPDATE_ACCEPT_NOTIFICATION, acceptSize, fileSize);
                    }
                    LogUtil.d(TAG, "[acceptFile] fileSize");
                    buffOut.flush();
                    if (acceptSize >= fileSize)
                        break;
                }
                buffOut.flush();
                buffOut = new BufferedOutputStream(socket.getOutputStream());
                replyState(buffOut);
                mHandler.sendEmptyMessageDelayed(BluetoothConstant.MESSAGE_FINISH_ACCEPT_NOTIFICATION, 1000);
            } catch (IOException e) {
                e.printStackTrace();

                if (buffOut != null) {
                    try {
                        buffOut.close();
                    } catch (IOException e0) {
                        e0.printStackTrace();
                    }
                }
            } finally {

                if (buffIn != null) {
                    try {
                        buffIn.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        LogUtil.d(TAG, "[acceptFile] 文件接受完毕");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File(list.get(0).getPath());
        boolean fileExists = file.exists();
        LogUtil.d(TAG, "[acceptFile] file.exists : " + fileExists + " --- " + file.length());
        BluetoothSys.getInstance().clearTransportFiles();
    }

    private void replyState(OutputStream out) throws IOException {
        byte[] b = new byte[1];
        b[0] = 1;
        out.write(b);
        out.flush();
    }

    private void sendMessage(int what, long acceptSize, long fileSize) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = (int) ((float) 100 * acceptSize / fileSize);
        LogUtil.d(TAG, "[sendMessage] progress = " + msg.obj);
        mHandler.sendMessage(msg);
    }


}
