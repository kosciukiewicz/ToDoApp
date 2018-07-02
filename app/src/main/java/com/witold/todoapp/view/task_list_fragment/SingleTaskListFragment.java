package com.witold.todoapp.view.task_list_fragment;


import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.ListToSend;
import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskList;
import com.witold.todoapp.view.add_new_task_fragment.NewTaskAddedEvent;
import com.witold.todoapp.view.app_widget.ToDoAppWidget;
import com.witold.todoapp.view.main_activity.MainActivityViewModel;
import com.witold.todoapp.view.single_sub_list_fragment.ShowTaskInformationDialogEvent;
import com.witold.todoapp.view.single_sub_list_fragment.ViewPagerLifecycle;

import java.util.List;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleTaskListFragment extends Fragment implements TaskInformationDialog.TaskInformationListener {
    private SingleTaskListViewModel singleTaskListViewModel;
    private MainActivityViewModel mainActivityViewModel;
    private ViewPager viewPagerSubLists;
    private FloatingActionButton floatingActionButtonAddNewTask;
    private TabLayout tabLayoutSubLists;
    private SubListFragmentPagerAdapter subListFragmentPagerAdapter;
    private TaskList taskList;
    private List<SubList> subLists;
    private int subListToShowId;
    private int taskDialogToShowId;
    private TaskInformationDialog taskInformationDialog;

    public SingleTaskListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_task_list, container, false);
        this.initializeComponents(view);
        this.taskInformationDialog = new TaskInformationDialog();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int listId = getArguments().getInt("listId");
        this.singleTaskListViewModel = ViewModelProviders.of(getActivity()).get(SingleTaskListViewModel.class);
        this.mainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        this.subListToShowId = getArguments().getInt("subListId", -1);
        this.taskDialogToShowId = getArguments().getInt("taskId", -1);
        this.observeViewModel();
        this.singleTaskListViewModel.getTaskListInformation(listId);
        this.singleTaskListViewModel.getAllSubLists(listId);
        this.viewPagerSubLists.setOffscreenPageLimit(1);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.single_task_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_sub_list:
                if(subLists.size() < 5){
                    addNewSubList();
                }else{
                    Toast.makeText(getContext(), "Nie można dodać więcej podlist", Toast.LENGTH_SHORT).show();
                }                return true;
            case R.id.send_task_list:
                singleTaskListViewModel.sendListAsMail(this.taskList.getId());
                return true;
            case R.id.remove_task_list:
                this.mainActivityViewModel.removeTaskList(this.taskList);
                return true;
            case R.id.remove_sub_list:
                if(subLists.size() > 1){
                    this.singleTaskListViewModel.removeSubList(this.subLists.get(viewPagerSubLists.getCurrentItem()));
                }else{
                    Toast.makeText(getContext(), "Brak podlist do usunęcia", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void observeViewModel(){
        singleTaskListViewModel.listMutableLiveData.observe(this, taskList -> {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                this.taskList = taskList;
                actionBar.setTitle(taskList.getName());
            }
        });

        singleTaskListViewModel.subListMutableLiveData.observe(this, subLists -> {
            this.subLists = subLists;
            initializeViewPager(subLists);
            if(subListToShowId != -1 &&  taskDialogToShowId != -1){
                int id = subListToShowId;
                int taskId = taskDialogToShowId;
                singleTaskListViewModel.getTaskInformationToShowOnDialog(taskId);
                viewPagerSubLists.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        viewPagerSubLists.setCurrentItem(findSublistById(id), false);
                        ((ViewPagerLifecycle)subListFragmentPagerAdapter.getItem(viewPagerSubLists.getCurrentItem())).showTaskDialog(taskId);
                    }
                }, 100);
            }
        });

        singleTaskListViewModel.subListAddedEventMutableLiveData.observe(this, subListAddedEvent -> {
            this.singleTaskListViewModel.getAllSubLists(this.taskList.getId());
        });

        singleTaskListViewModel.taskAddedActionLiveData.observe(this, new Observer<NewTaskAddedEvent>() {
            @Override
            public void onChanged(@Nullable NewTaskAddedEvent newTaskAddedEvent) {
                initializeViewPager(subLists);
                viewPagerSubLists.setCurrentItem(findSublistById(newTaskAddedEvent.getSubListId()));
                updateWidget();
            }
        });

        singleTaskListViewModel.showTaskInformationDialogActionLiveData.observe(this, new Observer<ShowTaskInformationDialogEvent>() {
            @Override
            public void onChanged(@Nullable ShowTaskInformationDialogEvent showTaskInformationDialogEvent) {
                showTaskInformation(showTaskInformationDialogEvent.getTask());
            }
        });

        singleTaskListViewModel.listToSendActionLiveData.observe(this, new Observer<List<ListToSend>>() {
            @Override
            public void onChanged(@Nullable List<ListToSend> listToSends) {
                sendListAsEmail(listToSends);
            }
        });
    }

    private void sendListAsEmail(List<ListToSend> listToSends){
        StringBuilder stringBuilder = new StringBuilder(this.taskList.getName() + "\n");
        stringBuilder.append("\n");
        for(int i = 0 ; i < listToSends.size(); i++){
            stringBuilder.append("Podlista " + listToSends.get(i).getSubList().getIndex() + ": \n");

            for(int j = 0 ; j < listToSends.get(i).getTaskList().size(); j++){
                stringBuilder.append(listToSends.get(i).getTaskList().get(j).getName() + "\n");
            }

            stringBuilder.append("\n");

        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","kosciukiewicz.w@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Lista zadań z aplikacji ToDoApp");
        emailIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void showTaskInformation(Task task) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        taskInformationDialog.setArguments(bundle);
        if(!taskInformationDialog.isAdded()){
            taskInformationDialog.show(getChildFragmentManager(), "taskInfoDialog");
        }
    }

    private void updateWidget(){
        Intent intent = new Intent(getActivity(), ToDoAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getContext(), ToDoAppWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
    }

    private int findSublistById(int sublistId){
        int i = 0;
        for(; i < subLists.size() && sublistId != subLists.get(i).getSubListId(); i++){
            Timber.d(subLists.get(i).getSubListId() + " " + sublistId);
        }
        return i;
    }

    private void initializeComponents(View view) {
        this.viewPagerSubLists = view.findViewById(R.id.viewpager_sub_lists_holder);
        this.tabLayoutSubLists = view.findViewById(R.id.tab_layout_sub_lists);
        this.floatingActionButtonAddNewTask = view.findViewById(R.id.floating_action_button_add_new_task);
        this.floatingActionButtonAddNewTask.setOnClickListener(v -> mainActivityViewModel.addNewTaskToSublistEventActionLiveData.postValue(new AddNewTaskToSublistEvent(subLists.get(viewPagerSubLists.getCurrentItem()))));
    }

    private void initializeViewPager(List<SubList> lists) {
        if (lists.size()==1){
            this.tabLayoutSubLists.setVisibility(View.GONE);
        }else{
            this.tabLayoutSubLists.setVisibility(View.VISIBLE);
        }
        subListFragmentPagerAdapter = new SubListFragmentPagerAdapter(getFragmentManager(), getContext(), lists);
        this.viewPagerSubLists.setAdapter(null);
        this.viewPagerSubLists.setAdapter(subListFragmentPagerAdapter);
        this.tabLayoutSubLists.setupWithViewPager(this.viewPagerSubLists);
        this.setTabsContextMenu();
    }

    private void addNewSubList(){
      this.singleTaskListViewModel.addNewSublist(taskList.getId());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        Timber.d("Menu");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void setTabsContextMenu(){
    }

    @Override
    public void removeTask(Task task) {
        this.singleTaskListViewModel.removeTask(task);
    }
}
