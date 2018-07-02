package com.witold.todoapp.view.single_sub_list_fragment;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.ComponentName;
import android.support.annotation.NonNull;

import com.witold.todoapp.R;
import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.TaskDao;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.view.app_widget.ToDoAppWidget;
import com.witold.todoapp.view.task_list_fragment.SubListListEditedEvent;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SingleSubListViewModel extends AndroidViewModel {
    private TaskDao taskDao;
    MutableLiveData<List<Task>> mutableLiveData;

    public SingleSubListViewModel(@NonNull Application application) {
        super(application);
        this.taskDao = AppDatabase.getDatabaseInstance(application).taskDao();
        this.mutableLiveData = new MutableLiveData<>();
    }

    public void getAllTasksBySublist(int subListId) {
        this.taskDao.getTasksBySubListId(subListId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Task>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Task> subLists) {
                        mutableLiveData.setValue(subLists);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
