package com.witold.todoapp.view.task_list_fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskInformationDialog extends DialogFragment {
    private TaskInformationListener taskInformationListener;
    private Task task;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.task_information_dialog, null);
        this.task = (Task) getArguments().getSerializable("task");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setNote(view, task.getNote());
        setDeadline(view, task.getAlertDateTime());
        setPriority(view, task.getPriority());
        builder.setView(view);
        builder.setNegativeButton("Zamknij", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        builder.setNeutralButton("Usuń", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                taskInformationListener.removeTask(task);
                dismiss();
            }
        });
        return builder.setTitle(task.getName()).create();
    }

    private void setNote(View view, String note){
        if(note!=null && !note.equals("") ){
            ((TextView)view.findViewById(R.id.text_view_task_dialog_note)).setText(note);
        }else{
            ((TextView)view.findViewById(R.id.text_view_task_dialog_note)).setText("Brak notatki");
        }
    }

    private void setDeadline(View view, Long deadline){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd", Locale.getDefault());
        if(deadline!=null && deadline != 0 ){
            ((TextView)view.findViewById(R.id.text_view_task_dialog_deadline)).setText(simpleDateFormat.format(new Date(deadline)));
        }else{
            ((TextView)view.findViewById(R.id.text_view_task_dialog_deadline)).setText("Brak terminu");
        }
    }


    private void setPriority(View view, boolean priority){
        if(priority ){
            ((TextView)view.findViewById(R.id.text_view_task_dialog_priority)).setText("Ważne!");
        }else{
            ((TextView)view.findViewById(R.id.text_view_task_dialog_priority)).setText("Zwykły");
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.taskInformationListener = (TaskInformationListener)getParentFragment();
    }

    interface TaskInformationListener {
        public void removeTask(Task task);
    }
}
