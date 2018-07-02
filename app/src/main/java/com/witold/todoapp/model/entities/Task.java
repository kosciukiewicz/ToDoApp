package com.witold.todoapp.model.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = Task.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = SubList.class,
                parentColumns = "subListId",
                childColumns = "fkSubListId",
                onDelete = ForeignKey.CASCADE))
public class Task implements Serializable {
    public static final String TABLE_NAME = "tasks";

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "taskId")
    private int id;
    @ColumnInfo(name = "taskName")
    private String name;
    private String note;
    private Long alertDateTime;
    private Boolean priority;
    @ColumnInfo(name = "fkSubListId")
    private int subListId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getAlertDateTime() {
        return alertDateTime;
    }

    public void setAlertDateTime(Long alertDateTime) {
        this.alertDateTime = alertDateTime;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public int getSubListId() {
        return subListId;
    }

    public void setSubListId(int subListId) {
        this.subListId = subListId;
    }
}
