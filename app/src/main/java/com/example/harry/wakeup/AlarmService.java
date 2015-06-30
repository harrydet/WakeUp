package com.example.harry.wakeup;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.List;

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
        String taskMessage = "\n";
        for (int i = 0; i < tasks.size(); i++) {
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
                .setContentTitle("It's time to wake up!")
                .setContentText("Expand this notification for your tasks.")
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("Today's list").bigText(taskMessage))
                .build();
        //notification.bigContentView = expandedView;

//        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, notification);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        settings.edit().putInt("tasklist_id_to_display", taskListId).apply();
    }


}
