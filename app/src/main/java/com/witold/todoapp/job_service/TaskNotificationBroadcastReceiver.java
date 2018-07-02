package com.witold.todoapp.job_service;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.utils.NotificationHelper;
import com.witold.todoapp.view.app_widget.ToDoAppWidget;

import timber.log.Timber;

public class TaskNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        try {
            TaskInformation task = (TaskInformation) intent.getBundleExtra("bundle").getSerializable("task");
            notificationHelper.pushNotification("Masz zadanie do wykonania!", task.getTask().getName(), task);
            updateWidget(context);
        }catch (Exception e){
            Timber.e(e);
            notificationHelper.pushNotification("Masz zadanie do wykonania!", e.getMessage(), null);
        }
    }

    private void updateWidget(Context context){
        Intent intent = new Intent(context, ToDoAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, ToDoAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
}
