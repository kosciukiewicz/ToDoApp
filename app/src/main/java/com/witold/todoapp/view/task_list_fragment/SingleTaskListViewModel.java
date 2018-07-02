package com.witold.todoapp.view.task_list_fragment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.witold.todoapp.model.AppDatabase;
import com.witold.todoapp.model.dao.SubListDao;
import com.witold.todoapp.model.dao.TaskDao;
import com.witold.todoapp.model.dao.TaskListDao;
import com.witold.todoapp.model.entities.ListToSend;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.model.entities.TaskList;
import com.witold.todoapp.utils.ActionLiveData;
import com.witold.todoapp.utils.TaskNotificationScheduler;
import com.witold.todoapp.view.add_new_task_fragment.NewTaskAddedEvent;
import com.witold.todoapp.view.single_sub_list_fragment.ShowTaskInformationDialogEvent;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class SingleTaskListViewModel extends AndroidViewModel {
    private SubListDao subListDao;
    private TaskListDao taskListDao;
    private TaskDao taskDao;
    MutableLiveData<TaskList> listMutableLiveData;
    MutableLiveData<List<SubList>> subListMutableLiveData;
    ActionLiveData<SubListListEditedEvent> subListAddedEventMutableLiveData;
    ActionLiveData<NewTaskAddedEvent> taskAddedActionLiveData;
    ActionLiveData<List<ListToSend>> listToSendActionLiveData;
    public ActionLiveData<ShowTaskInformationDialogEvent> showTaskInformationDialogActionLiveData;

    AppDatabase appDatabase;

    public SingleTaskListViewModel(@NonNull Application application) {
        super(application);
        this.appDatabase = AppDatabase.getDatabaseInstance(application);
        this.subListDao = appDatabase.subListDao();
        this.taskListDao = appDatabase.taskListDao();
        this.taskDao = appDatabase.taskDao();
        this.listMutableLiveData = new MutableLiveData<>();
        this.subListMutableLiveData = new MutableLiveData<>();
        this.subListAddedEventMutableLiveData = new ActionLiveData<>();
        this.showTaskInformationDialogActionLiveData = new ActionLiveData<>();
        this.taskAddedActionLiveData = new ActionLiveData<>();
        this.listToSendActionLiveData = new ActionLiveData<>();
    }

    public void getTaskListInformation(int taskListId) {
        this.taskListDao.getTaskListById(taskListId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TaskList>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(TaskList taskList) {
                        listMutableLiveData.setValue(taskList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void getAllSubLists(int taskListId) {
        this.subListDao.getAllSubListsByTaskListId(taskListId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<SubList>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<SubList> subLists) {
                        subListMutableLiveData.setValue(subLists);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void addNewSublist(int taskListId) {
        Completable.fromAction(() -> {
            appDatabase.runInTransaction(() -> {
                int count = this.subListDao.getAllSubListsByTaskListId(taskListId).blockingGet().size();
                SubList subList = new SubList();
                subList.setIndex(count + 1);
                subList.setTaskListId((int) taskListId);
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
                        subListAddedEventMutableLiveData.postValue(new SubListListEditedEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void removeSubList(SubList subList) {
        Completable.fromAction(() -> {
            subListDao.deleteSubList(subList);
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
                        subListAddedEventMutableLiveData.postValue(new SubListListEditedEvent());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void getTaskInformationToShowOnDialog(int taksId){
        taskDao.getTaskInformationByTaskId((long)taksId) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TaskInformation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(TaskInformation taskInformation) {
                        showTaskInformationDialogActionLiveData.postValue(new ShowTaskInformationDialogEvent(taskInformation.getTask()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void addNewTask(String name, String note, Date alertDate, boolean priority, int subListId) {
        Task task = new Task();
        task.setName(name);
        task.setNote(note);
        task.setAlertDateTime(alertDate == null ? 0 : alertDate.getTime());
        task.setPriority(priority);
        task.setSubListId(subListId);
        Single.fromCallable(() -> {
            long taskId = taskDao.insertTask(task);
            return taskDao.getTaskInformationByTaskId(taskId).blockingGet();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<TaskInformation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(TaskInformation taskInformation) {
                        Timber.d("TaskAdded");
                        if(taskInformation.getTask().getAlertDateTime() != null){
                            TaskNotificationScheduler.scheduleTaskNotification(getApplication(), taskInformation);
                        }
                        taskAddedActionLiveData.postValue(new NewTaskAddedEvent(subListId));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void removeTask(Task task) {
        Completable.fromAction(() -> {
            taskDao.deleteTask(task);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        taskAddedActionLiveData.postValue(new NewTaskAddedEvent(task.getSubListId()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }
                });
    }

    public void sendListAsMail(int listId){
        subListDao.getAllListsToSendByTaskListId(listId) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<ListToSend>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<ListToSend> listToSends) {
                        listToSendActionLiveData.postValue(listToSends);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
}
