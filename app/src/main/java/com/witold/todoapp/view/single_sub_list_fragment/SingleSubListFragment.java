package com.witold.todoapp.view.single_sub_list_fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.view.task_list_fragment.SingleTaskListViewModel;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSubListFragment extends Fragment implements ViewPagerLifecycle, TaskListAdapter.TaskClickListener {
    private SingleSubListViewModel singleSubListViewModel;
    private SingleTaskListViewModel singleTaskListViewModel;
    private int subListId;
    private RecyclerView recyclerView;
    private int taskToShowId = -1;
    public SingleSubListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_sub_list, container, false);
        initializeComponents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.singleSubListViewModel = ViewModelProviders.of(this).get(SingleSubListViewModel.class);
        this.singleTaskListViewModel = ViewModelProviders.of(getActivity()).get(SingleTaskListViewModel.class);
        this.observeViewModel();

    }

    @Override
    public void onResume() {
        super.onResume();
        this.subListId = getArguments().getInt("subListId");
        Timber.d(subListId + " RESUME ");
        this.singleSubListViewModel.getAllTasksBySublist(this.subListId);
    }


    @Override
    public void onViewPagerResume() {
    }

    @Override
    public void showTaskDialog(int taskId) {
        this.taskToShowId = taskId;

    }

    @Override
    public void showTaskInformation(Task task) {
        singleTaskListViewModel.showTaskInformationDialogActionLiveData.postValue(new ShowTaskInformationDialogEvent(task));
    }

    private void initializeComponents(View view) {
        this.recyclerView = view.findViewById(R.id.recycler_view_task_list);
    }

    private void observeViewModel() {
        this.singleSubListViewModel.mutableLiveData.observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                initializeList(tasks);
            }
        });
    }

    private void initializeList(@Nullable List<Task> tasks) {
        TaskListAdapter taskListAdapter = new TaskListAdapter(tasks, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taskListAdapter);
    }

    public static SingleSubListFragment newInstance(SubList subList) {
        SingleSubListFragment singleTaskListFragment = new SingleSubListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("subListId", subList.getSubListId());
        singleTaskListFragment.setArguments(bundle);
        return singleTaskListFragment;
    }

}
