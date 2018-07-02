package com.witold.todoapp.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.model.entities.TaskList;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM " + Task.TABLE_NAME)
    Single<List<Task>> getAllTasks();

    @Query("SELECT * FROM " + Task.TABLE_NAME + " WHERE fkSubListId=:subListId")
    Single<List<Task>> getTasksBySubListId(int subListId);

    @Query("SELECT subList.*, taskList.*, task.* FROM " + Task.TABLE_NAME + " AS task " +
            "JOIN " + SubList.TABLE_NAME + " AS subList ON subList.subListId = task.fkSubListId " +
            "JOIN " + TaskList.TABLE_NAME + " AS taskList ON subList.taskListId = taskList.id " +
            "WHERE alertDateTime IS NOT NULL AND task.alertDateTime > :timeInMilis ORDER BY alertDateTime ASC LIMIT 3")
    Single<List<TaskInformation>> getTaskThreeFutureTasks(Long timeInMilis);

    @Query("SELECT subList.*, taskList.*, task.* FROM " + Task.TABLE_NAME + " AS task " +
            "JOIN " + SubList.TABLE_NAME + " AS subList ON subList.subListId = task.fkSubListId " +
            "JOIN " + TaskList.TABLE_NAME + " AS taskList ON subList.taskListId = taskList.id " +
            "WHERE task.taskId=:taskId ")
    Single<TaskInformation> getTaskInformationByTaskId(Long taskId);

    @Insert
    public abstract Long insertTask(Task task);

    @Delete
    public abstract void deleteTask(Task task);
}
