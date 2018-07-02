package com.witold.todoapp.view.slider_activity.view_pager;

import java.io.Serializable;

public class SliderElement implements Serializable{
    private int resourceId;
    private String description;

    public SliderElement(int resourceId, String description) {
        this.resourceId = resourceId;
        this.description = description;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
