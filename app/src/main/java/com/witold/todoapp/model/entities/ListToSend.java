package com.witold.todoapp.model.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class ListToSend {
    @Embedded
    private SubList subList;

    @Relation(parentColumn = "subListId", entityColumn = "fkSubListId", entity = Task.class)
    private List<Task> taskList;

    public SubList getSubList() {
        return subList;
    }

    public void setSubList(SubList subList) {
        this.subList = subList;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }
}
