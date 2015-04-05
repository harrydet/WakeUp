package com.example.harry.wakeup;

import java.util.Locale;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainScreen extends FragmentActivity implements ActionBar.TabListener {

    ActionBar actionbar;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static MainScreen inst;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager viewPager;
    ViewPagerAdapter mViewPagerAdapter;
    private AlarmManager alarmManager;

    public static MainScreen instance() {

        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Locate the viewpager in activity_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Set the ViewPagerAdapter into ViewPager
        viewPager.setAdapter(mViewPagerAdapter);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        viewPager.setCurrentItem(1);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    public AlarmManager getAlarmManager(){
        return alarmManager;
    }
}
