package com.witold.todoapp.view.main_activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.witold.todoapp.R;
import com.witold.todoapp.model.entities.Task;
import com.witold.todoapp.model.entities.TaskInformation;
import com.witold.todoapp.view.add_new_list_fragment.AddNewListFragment;
import com.witold.todoapp.model.entities.TaskList;
import com.witold.todoapp.utils.KeyboardHider;
import com.witold.todoapp.utils.ViewUtils;
import com.witold.todoapp.view.add_new_list_fragment.AddNewListFragmentViewModel;
import com.witold.todoapp.view.add_new_list_fragment.TaskListEditedEvent;
import com.witold.todoapp.view.add_new_task_fragment.AddNewTaskFragment;
import com.witold.todoapp.view.task_list_fragment.SingleTaskListFragment;
import com.witold.todoapp.view.task_list_fragment.SingleTaskListViewModel;

import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout fragmentContainer;
    private AddNewListFragment addNewListFragment;
    private SingleTaskListFragment singleTaskListFragment;
    private AddNewTaskFragment addNewTaskFragment;
    private MainActivityViewModel mainActivityViewModel;
    private AddNewListFragmentViewModel addNewListFragmentViewModel;
    private SingleTaskListViewModel singleTaskListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.setUpHidingKeyboard(findViewById(android.R.id.content), this);
        this.mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        this.addNewListFragmentViewModel = ViewModelProviders.of(this).get(AddNewListFragmentViewModel.class);
        this.singleTaskListViewModel = ViewModelProviders.of(this).get(SingleTaskListViewModel.class);
        this.observeViewModel();
        this.mainActivityViewModel.getAllTaskLists();
        this.initializeNavigationDrawer();
        this.initializeComponents();
        this.initializeFragments();
        if(getIntent().getExtras() != null){
            Timber.d(((TaskInformation)getIntent().getExtras().getBundle("bundle").getSerializable("task")).getTaskList().getId() + "");
            TaskInformation taskInformation = (TaskInformation) getIntent().getExtras().getBundle("bundle").getSerializable("task");
            attachListFragment(taskInformation.getTaskList().getId(), taskInformation.getTask().getSubListId(), taskInformation.getTask().getId());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar slider_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardHider.checkIfEditTextAndHideKeyboard(ev, this);
        return super.dispatchTouchEvent(ev);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view slider_item clicks here.
        if (item.getGroupId() == R.id.group1) {
            attachListFragment(item.getItemId(), -1, -1);
        }
        if (item.getItemId() == R.id.add_new_list_form) {
            this.attachFragment(addNewListFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void attachListFragment(int listId, int subListId, int taskId){
        Bundle bundle = new Bundle();
        bundle.putInt("listId", listId);
        bundle.putInt("subListId", subListId);
        bundle.putInt("taskId", taskId);
        singleTaskListFragment.setArguments(bundle);
        this.attachFragment(singleTaskListFragment);
    }

    private void initializeComponents() {
        this.fragmentContainer = findViewById(R.id.frame_layout_fragment_container);
    }

    private void initializeFragments() {
        this.addNewListFragment = new AddNewListFragment();
        this.singleTaskListFragment = new SingleTaskListFragment();
        this.addNewTaskFragment = new AddNewTaskFragment();
    }

    private void initializeNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void attachFragment(Fragment fragment) {
        if (fragment.isAdded()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.replace(R.id.frame_layout_fragment_container, fragment).commit();

        }else{
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_fragment_container, fragment).addToBackStack(fragment.getClass().getName()).commit();
        }
    }


    private void observeViewModel() {
        this.mainActivityViewModel.taskListMutableLiveData.observe(this, new Observer<List<TaskList>>() {
            @Override
            public void onChanged(@Nullable List<TaskList> taskLists) {
                setTaskListList(taskLists);
                if(getIntent().getExtras() == null){
                    if(taskLists.size() > 0){
                        Bundle bundle = new Bundle();
                        bundle.putInt("listId", taskLists.get(taskLists.size() - 1).getId());
                        singleTaskListFragment.setArguments(bundle);
                        attachFragment(singleTaskListFragment);
                    }else {
                        attachFragment(addNewListFragment);
                    }                }

            }
        });

        this.addNewListFragmentViewModel.newTaskListAddedEventMutableLiveData.observe(this, new Observer<TaskListEditedEvent>() {
            @Override
            public void onChanged(@Nullable TaskListEditedEvent taskListEditedEvent) {
                mainActivityViewModel.getAllTaskLists();
            }
        });

        this.mainActivityViewModel.taskListEditedEventActionLiveData.observe(this, new Observer<TaskListEditedEvent>() {
            @Override
            public void onChanged(@Nullable TaskListEditedEvent taskListEditedEvent) {
                mainActivityViewModel.getAllTaskLists();
                attachFragment(addNewListFragment);
            }
        });

        this.mainActivityViewModel.addNewTaskToSublistEventActionLiveData.observe(this, addNewTaskToSublistEvent -> {
            Bundle bundle = new Bundle();
            bundle.putInt("subListId", addNewTaskToSublistEvent.getSubList().getSubListId());
            addNewTaskFragment.setArguments(bundle);
            attachFragment(addNewTaskFragment);
        });

    }

    private void setTaskListList(List<TaskList> taskListList) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.group1);
        for (int i = 0; i < taskListList.size(); i++) {
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_check_white_24dp).mutate();
            Timber.d(taskListList.get(i).getColor() + "");
            drawable.setColorFilter(taskListList.get(i).getColor(), PorterDuff.Mode.MULTIPLY);
            menu.add(R.id.group1, taskListList.get(i).getId(), Menu.NONE, taskListList.get(i).getName()).setIcon(drawable);
        }
    }
}
