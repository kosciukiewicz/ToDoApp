package com.witold.todoapp.model;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.witold.todoapp.model.dao.SubListDao;
import com.witold.todoapp.model.dao.TaskDao;
import com.witold.todoapp.model.dao.TaskListDao;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskList;

@Database(entities = {TaskList.class, SubList.class, Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskListDao taskListDao();
    public abstract SubListDao subListDao();
    public abstract TaskDao taskDao();

    public static AppDatabase sInstance;

    // Get a database instance
    public static synchronized AppDatabase getDatabaseInstance(Context context) {
        if (sInstance == null) {
            sInstance = create(context);
        }
        return sInstance;
    }

    // Create the database
    static AppDatabase create(Context context) {
        RoomDatabase.Builder<AppDatabase> builder = Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class,
                TaskList.TABLE_NAME);
        return builder.build();
    }
}
