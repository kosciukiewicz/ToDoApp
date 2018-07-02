package com.witold.todoapp.model.entities;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.io.Serializable;

public class TaskInformation implements Serializable{
    @Embedded
    private SubList subList;
    @Embedded
    private TaskList taskList;
    @Embedded
    private Task task;

    public SubList getSubList() {
        return subList;
    }

    public void setSubList(SubList subList) {
        this.subList = subList;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
