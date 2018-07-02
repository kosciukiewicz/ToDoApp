package com.witold.todoapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.witold.todoapp.job_service.TaskNotificationBroadcastReceiver;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;

import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class TaskNotificationScheduler {

    public static void scheduleTaskNotification(Context context, TaskInformation taskInformation) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, TaskNotificationBroadcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", taskInformation);
        intent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskInformation.getTask().getAlertDateTime().intValue(),  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, taskInformation.getTask().getAlertDateTime(), pendingIntent);
    }

    public static void cancelAll(Context context){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, TaskNotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmMgr.cancel(pendingIntent);
    }

}
