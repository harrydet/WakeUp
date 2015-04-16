package com.example.harry.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.Calendar;


public class NewAlarmActivity extends ActionBarActivity implements View.OnClickListener{

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private Button setButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        setButton = (Button) findViewById(R.id.setButton);
        setButton.setOnClickListener(this);

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setButton:
                createRecord();
                Log.d("AlarmActivity", "Alarm On");
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
                Intent myIntent = new Intent(NewAlarmActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(NewAlarmActivity.this, 0, myIntent, 0);
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void createRecord(){
        int hour = alarmTimePicker.getCurrentHour();
        int minute = alarmTimePicker.getCurrentMinute();
        int combined = hour*100 + minute;
        Alarm alarm = new Alarm();
        alarm.setStatus(true);
        alarm.setTime(combined);
        dbHelper.createAlarm(alarm);
    }
}
