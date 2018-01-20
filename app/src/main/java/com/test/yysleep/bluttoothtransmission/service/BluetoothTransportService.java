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
        mNtfSys = NotificationSys.getInstance();

        mManager = new BlueToothConnectManager(mHandler);
        mManager.executeAccept();
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
                mManager.executeConnect();
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.cancelAccept();
            mManager.cancelSend();
        }
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
            switch (msg.what) {
                case BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION:
                    ToastUtil.toast("发送完毕");
                    service.mNtfSys.notifyNotification(service, "发送完毕", "发送完毕");
                    break;

                case BluetoothConstant.MESSAGE_FINISH_ACCEPT_NOTIFICATION:
                    ToastUtil.toast("接收完毕");
                    service.mNtfSys.notifyNotification(service, "接收完毕", "接收完毕");
                    break;

                case BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION:
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_SEND, (String) msg.obj);
                    break;

                case BluetoothConstant.MESSAGE_UPDATE_ACCEPT_NOTIFICATION:
                    service.mNtfSys.notifyNotification(service, NotificationSys.TITLE_ACCEPT, (String) msg.obj);
                    break;

                case BluetoothConstant.MESSAGE_BLUETOOTH_SERVER_SOCKET_FAILED:
                    if (service.mManager != null) {
                        service.mManager.cancelAccept();
                        service.mManager.executeAccept();
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
