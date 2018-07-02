package com.witold.todoapp.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.view.main_activity.MainActivity;

public class NotificationHelper extends ContextWrapper {
    private NotificationManager notifManager;
    public static final String CHANNEL_ONE_ID = "firstChannel";
    public static final String CHANNEL_ONE_NAME = "Channel One";
    public static final String CHANNEL_TWO_ID = "com.jessicathornsby.myapplication.TWO";
    public static final String CHANNEL_TWO_NAME = "Channel Two";

//Create your notification channels//

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                CHANNEL_ONE_NAME, notifManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_TWO_ID,
                CHANNEL_TWO_NAME, notifManager.IMPORTANCE_DEFAULT);
        notificationChannel2.enableLights(false);
        notificationChannel2.enableVibration(true);
        notificationChannel2.setLightColor(Color.RED);
        notificationChannel2.setShowBadge(false);
        getManager().createNotificationChannel(notificationChannel2);
    }

//Create the notification that’ll be posted to Channel One//

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_check_white_24dp)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getCompatNotification1(String title, String body) {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ONE_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_check_white_24dp)
                .setAutoCancel(true);
    }

//Create the notification that’ll be posted to Channel Two//

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getNotification2(String title, String body) {
        return new Notification.Builder(getApplicationContext(), CHANNEL_TWO_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_check_white_24dp)
                .setAutoCancel(true);
    }

    public void notify(int id, Notification.Builder notification, TaskInformation task) {
        Intent startActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        startActivityIntent.putExtra("bundle", bundle);
        startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(startActivityPendingIntent);
        getManager().notify(id, notification.build());
    }

    public void notify(int id, NotificationCompat.Builder notification, TaskInformation task) {
        Intent startActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        startActivityIntent.putExtra("bundle", bundle);
        startActivityIntent.addFlags(
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(startActivityPendingIntent);
        getManager().notify(id, notification.build());
    }

    public void pushNotification(String title, String Body, TaskInformation task){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notify(1, getNotification1(title, Body), task);
        }else{
            notify(1, getCompatNotification1(title, Body), task);
        }
    }
//Send your notifications to the NotificationManager system service//

    private NotificationManager getManager() {
        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notifManager;
    }
}
