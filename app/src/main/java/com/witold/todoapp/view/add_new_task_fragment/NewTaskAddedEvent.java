package com.witold.todoapp.view.add_new_task_fragment;

public class NewTaskAddedEvent {
    private int subListId;

    public NewTaskAddedEvent(int subListId) {
        this.subListId = subListId;
    }

    public int getSubListId() {
        return subListId;
    }

    public void setSubListId(int subListId) {
        this.subListId = subListId;
    }
}
