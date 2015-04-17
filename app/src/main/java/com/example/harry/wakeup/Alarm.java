package com.example.harry.wakeup;

import android.app.PendingIntent;

/**
 * Created by Harry on 4/16/2015.
 */
public class Alarm {
    private int id;
    private boolean status;
    private int time;
    private PendingIntent pendingIntent;

    public PendingIntent getPendingIntent() {
        return pendingIntent;
    }

    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
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
}
