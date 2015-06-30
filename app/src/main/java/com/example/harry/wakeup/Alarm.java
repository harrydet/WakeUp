package com.example.harry.wakeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.Calendar;


public class Alarm {
    private int id;
    private boolean status;
    private int time;
    private TaskList taskList;
    private PendingIntent pendingIntent;

    public TaskList getTaskList() {
        return taskList;
    }

    public void setTaskList(TaskList taskList) {
        this.taskList = taskList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void engage(Calendar c, AlarmManager alarmManager, Activity activity) {
        Intent myIntent = new Intent(activity, AlarmReceiver.class);
        myIntent.putExtra("alarm_id", this.id);
        myIntent.putExtra("tasklist_id", this.taskList.getId());
        this.pendingIntent = PendingIntent.getBroadcast(activity, this.id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
