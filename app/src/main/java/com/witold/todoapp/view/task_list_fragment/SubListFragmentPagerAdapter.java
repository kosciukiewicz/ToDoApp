package com.witold.todoapp.view.task_list_fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.witold.todoapp.model.entities.SubList;
import com.witold.todoapp.view.single_sub_list_fragment.SingleSubListFragment;

import java.util.List;

import timber.log.Timber;

public class SubListFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private List<SubList> subLists;

    public SubListFragmentPagerAdapter(FragmentManager fm, Context context, List<SubList> subLists) {
        super(fm);
        this.context = context;
        this.subLists = subLists;
    }

    @Override
    public Fragment getItem(int position) {
        Timber.d(subLists.get(position).getSubListId() + "");
        return SingleSubListFragment.newInstance(subLists.get(position));
    }

    @Override
    public int getCount() {
        return subLists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Podlista " + subLists.get(position).getIndex();
    }

    public void removeTabPage(int position) {
        if (!subLists.isEmpty() && position<subLists.size()) {
            subLists.remove(position);
            notifyDataSetChanged();
        }
    }

    public void addTabPage(SubList subList) {
        subLists.add(subList);
        notifyDataSetChanged();
    }
}
