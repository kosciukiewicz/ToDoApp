package com.witold.todoapp.view.app_widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.witold.todoapp.R;
import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.TaskDao;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class TaskViewFactory implements RemoteViewsService.RemoteViewsFactory {
    private final static int STATUS_LOADING = 1;
    private final static int STATUS_FETCHED = 2;
    private final static int STATUS_CHANGED = 3;
    private int status = STATUS_CHANGED;
    private Context mContext;
    private List<TaskInformation> list;
    private Boolean dataFetched = false;
    private int count = 0;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    public TaskViewFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        TaskDao taskDao = AppDatabase.getDatabaseInstance(mContext).taskDao();
        taskDao.getTaskThreeFutureTasks(Calendar.getInstance().getTime().getTime()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TaskInformation>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        status = STATUS_LOADING;
                    }

                    @Override
                    public void onSuccess(List<TaskInformation> tasks) {
                        list = tasks;
                        count = tasks.size();
                        status = STATUS_FETCHED;
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(new ComponentName(mContext, ToDoAppWidget.class)), R.id.list_view_widget_task_holder);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    @Override
    public void onDataSetChanged() {
        TaskDao taskDao = AppDatabase.getDatabaseInstance(mContext).taskDao();
        this.list = taskDao.getTaskThreeFutureTasks(Calendar.getInstance().getTime().getTime()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).blockingGet();
        Timber.d(list.size() + "");
    }


    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position < list.size()) {
            TaskInformation task = this.list.get(position);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_single_widget_task);
            this.setView(rv, task);
            Intent fillInIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("task", task);
            fillInIntent.putExtra("bundle", bundle);
            rv.setOnClickFillInIntent(R.id.linear_layout_widget_task_holder, fillInIntent);
            return rv;
        }
        return null;
    }

    private void setView(RemoteViews view, TaskInformation taskInformation){
        view.setTextViewText(R.id.text_view_widget_task_title, taskInformation.getTask().getName());
        view.setTextViewText(R.id.text_view_widget_list_title, taskInformation.getTaskList().getName());
        view.setTextViewText(R.id.text_view_widget_sublist_title, "Podlista " + taskInformation.getSubList().getIndex());
        view.setTextViewText(R.id.text_view_widget_time, timeFormat.format(new Date(taskInformation.getTask().getAlertDateTime())));
        view.setTextViewText(R.id.text_view_widget_date, dateFormat.format(new Date(taskInformation.getTask().getAlertDateTime())));
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
