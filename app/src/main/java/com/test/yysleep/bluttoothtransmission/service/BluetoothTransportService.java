package com.test.yysleep.bluttoothtransmission.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.test.yysleep.bluttoothtransmission.R;
import com.test.yysleep.bluttoothtransmission.constant.BluetoothConstant;
import com.test.yysleep.bluttoothtransmission.sys.NotificationSys;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.BlueToothAcceptFileThread;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.BlueToothSendFileThread;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new TransportHandler(this);
        new BlueToothAcceptFileThread(mHandler).start();
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
                new BlueToothSendFileThread(mHandler).start();
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
            String content = (String) msg.obj;
            switch (msg.what) {

                case BluetoothConstant.MESSAGE_UPDATE_SEND_NOTIFICATION:
                    title = NotificationSys.TITLE_SEND;
                    break;

                case BluetoothConstant.MESSAGE_FINISH_SEND_NOTIFICATION:
                    title = NotificationSys.TITLE_FINISH;
                    break;

                case BluetoothConstant.MESSAGE_UPDATE_ACCEPT_NOTIFICATION:
                    title = NotificationSys.TITLE_ACCEPT;
                    break;

                case BluetoothConstant.MESSAGE_FINISH_ACCEPT_NOTIFICATION:
                    title = NotificationSys.TITLE_FINISH;
                    break;

                default:
                    break;
            }
            if (title != null) {
                service.mNtfSys.notifyNotification(service, title, content);
            }
        }
    }
}
