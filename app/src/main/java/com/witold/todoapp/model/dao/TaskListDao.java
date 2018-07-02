package com.witold.todoapp.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.witold.todoapp.model.entities.TaskList;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface TaskListDao {

    @Query("SELECT * FROM " + TaskList.TABLE_NAME)
    Single<List<TaskList>> getAllTasks();

    @Query("SELECT * FROM " + TaskList.TABLE_NAME + " WHERE id=:taskListId")
    Single<TaskList> getTaskListById(int taskListId);

    @Insert
    public abstract Long insertList(TaskList taskList);

    @Delete
    public abstract void deleteTaskList(TaskList taskList);
}
