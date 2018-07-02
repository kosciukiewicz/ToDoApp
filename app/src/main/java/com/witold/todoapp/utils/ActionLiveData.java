package com.witold.todoapp.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ActionLiveData<T> extends MutableLiveData<T> {

    @MainThread
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
        super.observe(owner, new Observer<T>() {
            @Override
            public void onChanged(@Nullable T data) {
                if (data == null) return;
                observer.onChanged(data);
                // We set the value to null straight after emitting the change to the observer
                setValue(null);
            }
        });
    }

    @MainThread
    public void sendAction(T data) {
        setValue(data);
    }
}
