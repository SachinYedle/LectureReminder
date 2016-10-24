package com.example.sachin.lecturereminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by Sachin on 10/21/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public static final int MESSAGE_NOTIFICATION_ID = 435345;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context,"Alarm Received",Toast.LENGTH_SHORT).show();
        createNotification(context,intent);
    }
    private void createNotification(Context context, Intent intent) {

        String title = "Class Name " + intent.getStringExtra("name");
        String topic = "Topic " + intent.getStringExtra("topic");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(topic)
                .setDefaults(Notification.DEFAULT_SOUND);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());

    }
}
