package com.example.harry.wakeup;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Harry on 3/4/2015.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if(AlarmActivity.instance() == null){
            Log.d("Err:(", "Activity is null");
        } else {
            Log.d("No Err :D", "Activity is gucci");
        }
        AlarmActivity inst = AlarmActivity.instance();
        inst.setAlarmText("Wakey Wakey Sunshine");

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alarmUri == null){
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        startIntent.putExtra("ringtone-uri", alarmUri);
        context.startService(startIntent);

        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }


}
