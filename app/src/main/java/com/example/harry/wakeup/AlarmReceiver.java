package com.example.harry.wakeup;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.Calendar;

/**
 * Created by Harry on 3/4/2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {

        settings = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());

        calendar = Calendar.getInstance();
        dbHelper = new DatabaseHelper(context);

        if(intent.getIntExtra("silence", -1) != -1){
            Intent stopIntent = new Intent(context, RingtonePlayingService.class);
            context.stopService(stopIntent);
        }

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        long alarmId = settings.getLong("ringing_alarm_id", -2);
        Alarm alarm = dbHelper.getAlarm(settings.getLong("ringing_alarm_id", -2));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int combinedHour = hour*100 + minute;
        if(alarm != null && combinedHour <= alarm.getTime()) {
            Intent startIntent = new Intent(context, RingtonePlayingService.class);
            startIntent.putExtra("ringtone-uri", alarmUri);
            context.startService(startIntent);

            Log.e("From Receiver: ", Integer.toString(intent.getIntExtra("tasklist_id", -1)));

            ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
            startWakefulService(context, (intent.setComponent(comp)));
            setResultCode(Activity.RESULT_OK);
        }
    }


}
