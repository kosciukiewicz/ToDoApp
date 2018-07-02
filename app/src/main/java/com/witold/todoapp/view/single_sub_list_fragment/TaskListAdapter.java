package com.witold.todoapp.view.single_sub_list_fragment;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private SimpleDateFormat simpleDateFormat;
    private TaskClickListener taskClickListener;

    public TaskListAdapter(List<Task> taskList, TaskClickListener taskClickListener) {
        super();
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        this.tasks = taskList;
        this.taskClickListener = taskClickListener;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textViewName;
        TextView textViewDate;
        Task task;

        public TaskViewHolder(View itemView) {
            super(itemView);
            this.linearLayout = itemView.findViewById(R.id.linear_layout_single_task_holder);
            this.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskClickListener.showTaskInformation(task);
                }
            });
            this.textViewName = itemView.findViewById(R.id.textViewTaskName);
            this.textViewDate = itemView.findViewById(R.id.textViewTaskDate);
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_single_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = this.tasks.get(position);
        holder.textViewName.setText(task.getName());
        holder.task = task;
        if (task.getAlertDateTime() != null && task.getAlertDateTime() > 0){
            holder.textViewDate.setText(simpleDateFormat.format(new Date(task.getAlertDateTime())));
        }else {
            holder.textViewDate.setText(R.string.no_date);
        }

        if(task.getPriority()){
            holder.linearLayout.setBackgroundColor(Color.parseColor("#FFF8E1"));
        }

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    interface TaskClickListener{
        public void showTaskInformation(Task task);
    }
}
