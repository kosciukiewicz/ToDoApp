package com.witold.todoapp.view.add_new_list_fragment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.SubListDao;
import com.witold.todoapp.model.dao.TaskListDao;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.TaskList;
import com.witold.todoapp.utils.ActionLiveData;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AddNewListFragmentViewModel extends AndroidViewModel {
    private TaskListDao taskListDao;
    private SubListDao subListDao;
    private AppDatabase appDatabase;
    public ActionLiveData<TaskListEditedEvent> newTaskListAddedEventMutableLiveData;

    public AddNewListFragmentViewModel(@NonNull Application application) {
        super(application);
        this.appDatabase = AppDatabase.getDatabaseInstance(application);
        this.taskListDao = appDatabase.taskListDao();
        this.subListDao = appDatabase.subListDao();
        this.newTaskListAddedEventMutableLiveData = new ActionLiveData<>();
    }

    public void addNewList(String listName, int color) {
        TaskList taskList = new TaskList();
        taskList.setName(listName);
        taskList.setColor(color);
        Completable.fromAction(() -> {
            appDatabase.runInTransaction(() -> {
                long listId = taskListDao.insertList(taskList);
                SubList subList = new SubList();
                subList.setIndex(1);
                subList.setTaskListId((int) listId);
                subListDao.insertList(subList);
            });
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
                        newTaskListAddedEventMutableLiveData.postValue(new TaskListEditedEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }
}
