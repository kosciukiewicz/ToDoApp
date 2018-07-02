package com.witold.todoapp.view.single_sub_list_fragment;

import com.witold.todoapp.model.entities.Task;

public class ShowTaskInformationDialogEvent {
    private Task task;

    public ShowTaskInformationDialogEvent(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
