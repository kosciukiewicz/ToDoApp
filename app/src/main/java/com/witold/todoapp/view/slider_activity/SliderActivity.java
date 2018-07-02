package com.witold.todoapp.view.slider_activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.witold.todoapp.R;
import com.witold.todoapp.view.main_activity.MainActivity;
import com.witold.todoapp.view.slider_activity.view_pager.CustomPagerAdapter;
import com.witold.todoapp.view.slider_activity.view_pager.SliderElement;

import java.util.Arrays;

public class SliderActivity extends AppCompatActivity {
    private Button buttonShowMainActivity;
    private ViewPager viewPager;
    public CustomPagerAdapter mAdapter;
    public final static int PAGES = 5;
    public final static int FIRST_PAGE = 0  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        initializeComponents();
        initializeButtonShowMainActivity();
        initializeViewPager();
    }

    private void initializeComponents(){
        this.buttonShowMainActivity = findViewById(R.id.button_show_main_activity);
        this.viewPager = findViewById(R.id.view_pager_slider);
    }

    private void initializeViewPager(){
        mAdapter = new CustomPagerAdapter(this, this.getSupportFragmentManager(), Arrays.asList(
                new SliderElement(R.drawable.slider_element_5, "Zarządzaj swoimi listami!"),
                new SliderElement(R.drawable.slider_element_1, "Dodawaj nowe listy!"),
                new SliderElement(R.drawable.slider_element_4, "Wybieraj ich kolor"),
                new SliderElement(R.drawable.slider_element_2, "Dodawaj nowe zadania"),
                new SliderElement(R.drawable.slider_element_3, "Konroluj swoje listy zadań")
                ));
        viewPager.setAdapter(mAdapter);
        viewPager.setPageTransformer(false, mAdapter);
        viewPager.setCurrentItem(FIRST_PAGE);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(-300);
    }

    private void initializeButtonShowMainActivity(){
        this.buttonShowMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainActivity();
            }
        });
    }

    private void showMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
