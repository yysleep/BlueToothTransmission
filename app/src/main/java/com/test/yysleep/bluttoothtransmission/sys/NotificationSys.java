package com.test.yysleep.bluttoothtransmission.sys;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class NotificationSys {

    public static final int NOTIFICATION_ID = NotificationSys.class.hashCode();

    private NotificationSys() {
    }

    private static volatile NotificationSys instance;

    private static SparseArray<NotificationCompat.Builder> mArray;
    public static final String CHANNEL_ID_01 = "channel01";
    public static final String CHANNEL_ID_02 = "channel02";

    public static NotificationSys getInstance() {
        if (instance == null) {
            synchronized (NotificationSys.class) {
                if (instance == null) {
                    instance = new NotificationSys();
                    mArray = new SparseArray<>();
                }
            }
        }
        return instance;
    }

    public Notification createNotification(Context context, int id, String channelId, String title, int icon, String content) {
        if (mArray.get(id) != null) {
            return notifyNotification(context, id, channelId, title, icon, content);
        }
        if (context == null || channelId == null || title == null || icon <= 0 || content == null)
            return null;

        context = context.getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setSmallIcon(icon)
                .setContentText(content);
        Notification notification = builder.build();
        managerNotify(context, notification, id);
        mArray.put(id, builder);
        return notification;
    }

    private void managerNotify(Context context, Notification n, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(id, n);
        }
    }

    public Notification notifyNotification(Context context, int id, String channelId, String title, int icon, String content) {
        if (context == null || channelId == null)
            return null;

        if (mArray.get(id) == null) {
            return createNotification(context, id, channelId, title, icon, content);
        }
        context = context.getApplicationContext();

        NotificationCompat.Builder builder = mArray.get(id);
        if (title != null) {
            builder.setContentTitle(title);
        }
        if (icon > 0) {
            builder.setSmallIcon(icon);
        }
        if (content != null) {
            builder.setContentText(content);
        }
        Notification n = builder.build();
        managerNotify(context, n, id);

        return n;
    }

    public void cancelAllNotification(Context context) {
        if (mArray.size() == 0)
            return;
        context = context.getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
            mArray.clear();
        }
    }

    public void cancelNotification(Context context, int id) {
        if (mArray.size() == 0 || mArray.get(id) == null) {
            return;
        }
        context = context.getApplicationContext();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(id);
            mArray.remove(id);
        }
    }

}
