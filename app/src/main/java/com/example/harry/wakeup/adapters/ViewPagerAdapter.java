package com.example.harry.wakeup.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.harry.wakeup.AlarmFragment;
import com.example.harry.wakeup.ListTaskListFragment;
import com.example.harry.wakeup.TodayListFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    // Tab Titles
    private String tabtitles[] = new String[]{"Today's List", "Alarms", "Tasks"};
    Context context;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TodayListFragment.newInstance("0", "Today's List");
            case 1:
                return AlarmFragment.newInstance("1", "Alarms");
            case 2:
                return ListTaskListFragment.newInstance("2", "Tasks");


            default:
                return null;

        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}