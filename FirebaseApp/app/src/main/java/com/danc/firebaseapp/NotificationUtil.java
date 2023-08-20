package com.danc.firebaseapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtil {

    private Context context;
    private String title;
    private String body;
    private int PENDING_INTENT_REQUEST_CODE = 0;
    private int NOTIFICATION_ID = 0;

    public NotificationUtil(Context context, String title, String body) {
        this.context = context;
        this.title = title;
        this.body = body;
    }

    public Notification getNotification(){

        //Click Action
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, PENDING_INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context, Common.NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        return notiBuilder.build();
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String name = Common.NOTIFICATION_CHANNEL_NAME;
            String description = Common.NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(Common.NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            createNotificationManager(channel);
        }
    }

    private void createNotificationManager(NotificationChannel channel){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notiManager = context.getSystemService(NotificationManager.class);
            notiManager.createNotificationChannel(channel);
            notiManager.notify(NOTIFICATION_ID++, getNotification());
        }
    }
}
