package com.witold.todoapp.view.slider_activity.view_pager;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.witold.todoapp.R;

import java.util.List;

import static com.witold.todoapp.view.slider_activity.SliderActivity.FIRST_PAGE;
import static com.witold.todoapp.view.slider_activity.SliderActivity.PAGES;


public class CustomPagerAdapter extends FragmentPagerAdapter implements ViewPager.PageTransformer {
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
    private List<SliderElement> sliderElementList;
    private Activity mContext;
    private FragmentManager mFragmentManager;
    private float mScale;

    public CustomPagerAdapter(Activity context, FragmentManager fragmentManager, List<SliderElement> sliderElementList) {
        super(fragmentManager);
        this.mFragmentManager = fragmentManager;
        this.mContext = context;
        this.sliderElementList = sliderElementList;
    }

    @Override
    public Fragment getItem(int position) {
        // make the first mViewPager bigger than others
        if (position == FIRST_PAGE)
            mScale = BIG_SCALE;
        else
            mScale = SMALL_SCALE;

        return CustomFragment.newInstance(mContext, this.sliderElementList.get(position), mScale);
    }

    @Override
    public int getCount() {
        return sliderElementList.size();
    }

    @Override
    public void transformPage(View page, float position) {
        CustomLinearLayout myLinearLayout = (CustomLinearLayout) page.findViewById(R.id.item_root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;
        myLinearLayout.setScaleBoth(scale);
    }
}
