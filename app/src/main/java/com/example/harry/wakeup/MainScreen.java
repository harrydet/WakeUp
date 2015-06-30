package com.example.harry.wakeup;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.harry.wakeup.adapters.ViewPagerAdapter;
import com.software.shell.fab.ActionButton;


public class MainScreen extends FragmentActivity implements ActionBar.TabListener, ListTaskListFragment.Callbacks, View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static MainScreen inst;
    ActionBar actionbar;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager viewPager;
    ViewPagerAdapter mViewPagerAdapter;
    private AlarmManager alarmManager;

    private ActionButton fab;

    private int currentTab;

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
        currentTab = 1;

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentTab = position;
                if (currentTab == 0) {
                    fab.playHideAnimation();
                    fab.hide();
                } else if (currentTab == 1) {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_clock));
                    if (fab.isHidden()) {
                        fab.playShowAnimation();
                        fab.show();
                    }
                } else {
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                    if (fab.isHidden()) {
                        fab.playShowAnimation();
                        fab.show();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab = (ActionButton) findViewById(R.id.fab_activity_action_button);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_alarm_clock));
        fab.setShowAnimation(ActionButton.Animations.SCALE_UP);
        fab.setHideAnimation(ActionButton.Animations.SCALE_DOWN);

        fab.setOnClickListener(this);


    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        currentTab = tab.getPosition();
        fab.playShowAnimation();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        fab.playHideAnimation();
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
        currentTab = tab.getPosition();
    }

    public AlarmManager getAlarmManager() {
        return alarmManager;
    }

    @Override
    public void onListItemSelected(int id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_activity_action_button:
                if (currentTab == 1) {
                    Intent intent = new Intent(this, NewAlarmActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (currentTab == 2) {
                    Intent intent = new Intent(this, NewTaskListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            fab.playShowAnimation();
        } else {
            fab.playHideAnimation();
        }
        super.onWindowFocusChanged(hasFocus);
    }
}
