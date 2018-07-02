package com.witold.todoapp.view.task_list_fragment;

import com.witold.todoapp.model.entities.SubList;

public class AddNewTaskToSublistEvent {
    private SubList subList;

    public AddNewTaskToSublistEvent(SubList subList) {
        this.subList = subList;
    }

    public SubList getSubList() {
        return subList;
    }

    public void setSubList(SubList subList) {
        this.subList = subList;
    }
}
