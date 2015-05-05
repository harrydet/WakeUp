package com.example.harry.wakeup;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Harry on 3/4/2015.
 */
public class AlarmService extends IntentService {
    private NotificationManager alarmNotificationManager;
    private DatabaseHelper dbHelper;
    private int taskListId;

       public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        taskListId = intent.getIntExtra("tasklist_id", -1);
        sendNotification("Wake Up !!!");
    }

    public void sendNotification(String msg) {
        dbHelper = new DatabaseHelper(this);

        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mainScreeIntent = new Intent(this, MainScreen.class);
        mainScreeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, mainScreeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
//                this).setContentTitle("Alarm").setSmallIcon(R.drawable.ic_alarm_clock).setContentText(msg).setAutoCancel(true);

        RemoteViews expandedView = new RemoteViews(
                getApplicationContext().getPackageName(), R.layout.custom_notification_layout);

        expandedView.setTextViewText(R.id.notification_title, "Wake up!");
        expandedView.setTextViewText(R.id.notification_subtitle, "Here are your tasks for today: ");
        expandedView.setTextViewText(R.id.notification_colored_text, "Colored Text!");

        List<Task> tasks = dbHelper.getTasksByTaskList(dbHelper.getTaskList(taskListId));
        String taskMessage = "\n" + "\n";
        for(int i = 0; i < tasks.size(); i++){
            taskMessage += (i + 1) + ". " + tasks.get(i).getName() + "\n\n";
        }

        expandedView.setTextViewText(R.id.notification_tasks_text, taskMessage);

        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra("silence", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1337, myIntent, 0);

        Notification notification = new NotificationCompat.Builder(
                getApplicationContext())
                .setSmallIcon(R.drawable.ic_alarm_clock)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setContentTitle("Notification!!!")
                .setContentText(msg)
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_silence, "Silence", pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle())
                .build();
        notification.bigContentView = expandedView;

//        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, notification);
        Log.d("AlarmService", "Notification Sent");
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}
