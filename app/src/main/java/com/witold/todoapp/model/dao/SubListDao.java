package com.witold.todoapp.model.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.witold.todoapp.model.entities.ListToSend;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.TaskList;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface SubListDao {

    @Query("SELECT * FROM " + SubList.TABLE_NAME)
    Single<List<SubList>> getAllSubLists();

    @Query("SELECT * FROM " + SubList.TABLE_NAME + " WHERE taskListId=:taskListId")
    Single<List<SubList>> getAllSubListsByTaskListId(int taskListId);

    @Query("SELECT * FROM " + SubList.TABLE_NAME + " WHERE taskListId=:taskListId")
    Single<List<ListToSend>> getAllListsToSendByTaskListId(int taskListId);

    @Insert
    public abstract Long insertList(SubList subList);

    @Delete
    public abstract void deleteSubList(SubList subList);

}
