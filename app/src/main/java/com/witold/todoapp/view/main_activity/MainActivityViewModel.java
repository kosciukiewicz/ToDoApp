package com.witold.todoapp.view.main_activity;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.TaskListDao;
import com.witold.todoapp.model.entities.TaskList;
import com.witold.todoapp.utils.ActionLiveData;
import com.witold.todoapp.view.add_new_list_fragment.TaskListEditedEvent;
import com.witold.todoapp.view.task_list_fragment.AddNewTaskToSublistEvent;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivityViewModel extends AndroidViewModel {
    MutableLiveData<List<TaskList>> taskListMutableLiveData;
    public ActionLiveData<TaskListEditedEvent> taskListEditedEventActionLiveData;
    public ActionLiveData<AddNewTaskToSublistEvent> addNewTaskToSublistEventActionLiveData;

    private TaskListDao taskListDao;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        this.taskListMutableLiveData = new MutableLiveData<>();
        this.taskListEditedEventActionLiveData = new ActionLiveData<>();
        this.addNewTaskToSublistEventActionLiveData = new ActionLiveData<>();
        this.taskListDao = AppDatabase.getDatabaseInstance(application).taskListDao();
    }

    public void getAllTaskLists() {
        this.taskListDao.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<TaskList>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<TaskList> taskLists) {
                        taskListMutableLiveData.setValue(taskLists);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void removeTaskList(TaskList taskList) {
        Completable.fromAction(() -> {
            taskListDao.deleteTaskList(taskList);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Timber.d("List added");
                        taskListEditedEventActionLiveData.postValue(new TaskListEditedEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}
