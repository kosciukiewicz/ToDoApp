package com.witold.todoapp.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = SubList.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = TaskList.class,
        parentColumns = "id",
        childColumns = "taskListId",
        onDelete = ForeignKey.CASCADE))
public class SubList implements Serializable {
    public static final String TABLE_NAME = "sub_lists";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subListId")
    private int subListId;
    private int index;
    private int taskListId;

    public int getSubListId() {
        return subListId;
    }

    public void setSubListId(int subListId) {
        this.subListId = subListId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }
}
