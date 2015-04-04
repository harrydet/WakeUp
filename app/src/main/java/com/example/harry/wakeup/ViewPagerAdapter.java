package com.example.harry.wakeup;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    // Tab Titles
    private String tabtitles[] = new String[]{"Alarms", "Tasks"};
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
                return AlarmFragment.newInstance("0", "Alarms");

            case 1:
                return TaskListFragment.newInstance("1", "Tasks");
            default:
                return null;

        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}