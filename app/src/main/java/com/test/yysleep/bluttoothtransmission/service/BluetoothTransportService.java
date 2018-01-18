package com.test.yysleep.bluttoothtransmission.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.tool.sys.NotificationSys;
import com.test.yysleep.bluttoothtransmission.manager.BlueToothConnectManager;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.accept.AcceptDataThread;
import com.test.yysleep.bluttoothtransmission.util.ToastUtil;

import java.lang.ref.WeakReference;

/**
 * Created by YySleep on 2018/1/18.
 *
 * @author YySleep
 */

public class BluetoothTransportService extends Service {

    private final static String TAG = "BluetoothTransportService";
    private Handler mHandler;
    NotificationSys mNtfSys;
    private BlueToothConnectManager mManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new TransportHandler(this);
        new AcceptDataThread(mHandler).start();
        mNtfSys = NotificationSys.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null)
            return START_STICKY;

        String extra = intent.getStringExtra(BluetoothConstant.EXTRA_TRANSPORT_SEND_SERVICE);

        if (extra == null)
            return START_STICKY;

        switch (extra) {
            case BluetoothConstant.EXTRA_TRANSPORT_SEND_SERVICE:
                if (mManager != null) {
                    mManager.cancel();
                    mManager = null;
                }
                mManager = new BlueToothConnectManager(mHandler);
                mManager.execute();
                break;
        }
        return START_STICKY;
    }

    private static class TransportHandler extends Handler {
        private final WeakReference<BluetoothTransportService> w;

        private TransportHandler(BluetoothTransportService service) {
            w = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BluetoothTransportService service = w.get();
            if (service == null)
                return;
            String title = null;
            String content = null;
            switch (msg.what) {

                case BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION:
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_SEND, (int) msg.obj + "%");
                    break;

                case BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION:
                    ToastUtil.toast(service, "发送完毕");
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_SEND_FINISH, "发送完毕");
                    break;

                case BluetoothConstant.MESSAGE_UPDATE_ACCEPT_NOTIFICATION:
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_ACCEPT, (int) msg.obj + "%");

                    break;

                case BluetoothConstant.MESSAGE_FINISH_ACCEPT_NOTIFICATION:
                    ToastUtil.toast(service, "接收完毕");
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_ACCEPT_FINISH, "接收完毕");
                    break;

                default:
                    break;
            }

        }
    }
}
