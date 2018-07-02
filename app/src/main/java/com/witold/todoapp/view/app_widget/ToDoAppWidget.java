package com.witold.todoapp.view.app_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.witold.todoapp.R;
import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.TaskDao;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.view.main_activity.MainActivity;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Implementation of App Widget functionality.
 */
public class ToDoAppWidget extends AppWidgetProvider {

    public static void updateWidgets(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), AppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, AppWidgetProvider.class));

        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.layout_app_widget
            );
            Intent intent = new Intent(context, ToDoAppWidgetService.class);
            views.setRemoteAdapter(R.id.list_view_widget_task_holder, intent);
            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.list_view_widget_task_holder, startActivityPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String strAction = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(strAction)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisWidget = new ComponentName(context, ToDoAppWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_widget_task_holder);
        }
    }
}

