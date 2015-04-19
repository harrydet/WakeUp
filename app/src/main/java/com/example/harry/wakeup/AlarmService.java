package com.example.harry.wakeup;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Harry on 3/4/2015.
 */
public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;

       public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification("Wake Up !!!");
    }

    public void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("alarm_text", msg);
        editor.commit();
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.drawable.ic_alarm_clock).setContentText(msg).setAutoCancel(true);

        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alarmNotificationBuilder.build());
        Log.d("AlarmService", "Notification Sent");
    }

}
