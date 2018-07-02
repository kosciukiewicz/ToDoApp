package com.witold.todoapp.model.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = TaskList.TABLE_NAME)
public class TaskList implements Serializable{
    public static final String TABLE_NAME = "lists";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int color;

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
