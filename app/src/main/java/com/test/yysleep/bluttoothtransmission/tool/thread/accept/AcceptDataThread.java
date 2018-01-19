package com.test.yysleep.bluttoothtransmission.tool.thread.accept;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.constant.Constant;
import com.test.yysleep.bluttoothtransmission.model.FileInfo;
import com.test.yysleep.bluttoothtransmission.tool.sys.BluetoothSys;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;
import com.test.yysleep.bluttoothtransmission.util.ToastUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private BluetoothSocket mSocket;
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

        mSocket = null;
        // Keep listening until exception occurs or a socket is returned

        try {
            while (true) {

                if (isInterrupted()) {
                    throw new InterruptedException();

                }
                try {
                    LogUtil.d(TAG, "[run] 开始一轮新的等待");
                    mSocket = mServerSocket.accept();
                } catch (IOException e) {
                    ToastUtil.toast("蓝牙通道异常关闭");
                    break;
                }

                BluetoothSys.getInstance().clearAcceptFiles();
                acceptDataInfo(mSocket);
                acceptFile(mSocket);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will cancel the listening socket, and cause the thread to finish
     */
    public void cancel() {
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        interrupt();

        LogUtil.d(TAG, "[cancel] close mServerSocket");
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

            String[] array = s.trim().split(" ");
            if (array.length > 0) {
                ToastUtil.toast("开始解析传输文件" + array[0]);
            }
            BluetoothSys.getInstance().clearAcceptFiles();
            List<FileInfo> files = BluetoothSys.getInstance().getAcceptFiles();
            for (int i = 0; i < array.length; i = i + 2) {
                FileInfo info = new FileInfo();
                info.setPath(array[i]);
                info.setLength(Long.valueOf(array[i + 1]));
                files.add(info);
                LogUtil.d(TAG, "[acceptDataInfo] path = " + array[i]);
            }
            replyState(out, BluetoothConstant.FLAG_ACCEPT_FILE_INFO_SUCCESS);

        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.toast("蓝牙通道出现异常");
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
        if (socket == null) {
            ToastUtil.toast("蓝牙通道异常关闭，结束文件传输");
            return;
        }

        List<FileInfo> list = BluetoothSys.getInstance().getAcceptFiles();

        LogUtil.d(TAG, "[acceptFile] 开始接收文件");
        BufferedInputStream buffIn = null;
        BufferedOutputStream buffOut = null;
        int fileNum = 1;
        for (FileInfo info : list) {
            try {
                long fileSize = info.getLength();
                long acceptSize = 0;
                byte[] bytes = new byte[1024];
                int len = 0;
                buffIn = new BufferedInputStream(socket.getInputStream());
                buffOut = new BufferedOutputStream(new FileOutputStream(info.getPath()));
                ToastUtil.toast("开始接收第 " + fileNum + " 个文件");
                while ((len = buffIn.read(bytes)) != -1) {
                    buffOut.write(bytes, 0, len);
                    if (fileSize >= 0) {
                        acceptSize = len + acceptSize;

                        sendMessage(BluetoothConstant.MESSAGE_UPDATE_ACCEPT_NOTIFICATION, "第" + fileNum + "个文件 ：" + (int) ((float) 100 * acceptSize / fileSize) + "%");
                    }
                    LogUtil.d(TAG, "[acceptFile] fileSize");
                    buffOut.flush();
                    if (acceptSize >= fileSize)
                        break;
                }
                fileNum++;
                buffOut.close();

                buffOut = new BufferedOutputStream(socket.getOutputStream());
                replyState(buffOut, BluetoothConstant.FLAG_ACCEPT_FILE_SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtil.toast("蓝牙通道出现异常");
                if (buffIn != null) {
                    try {
                        buffIn.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(BluetoothConstant.MESSAGE_FINISH_ACCEPT_NOTIFICATION, 1000);
        LogUtil.d(TAG, "[acceptFile] 文件接收完毕");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        File file = new File(list.get(0).getPath());
        boolean fileExists = file.exists();
        LogUtil.d(TAG, "[acceptFile] file.exists : " + fileExists + " --- " + file.length());
        BluetoothSys.getInstance().clearAcceptFiles();
    }

    private void replyState(OutputStream out, String flag) throws IOException {
        byte[] b = flag.getBytes();
        out.write(b);
        out.flush();
    }

    private void sendMessage(int what, String content) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = content;
        LogUtil.d(TAG, "[sendMessage] progress = " + msg.obj);
        mHandler.sendMessage(msg);
    }


}
