package com.example.harry.wakeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.session.MediaController;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.software.shell.fab.ActionButton;
import com.software.shell.fab.FloatingActionButton;

import java.util.Calendar;


public class AlarmActivity extends Activity implements View.OnClickListener {

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private static AlarmActivity inst;
    private TextView alarmTextView;
    private Ringtone ringtone;
    private ActionButton fab;

    public static AlarmActivity instance() {

        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setAlarmText(extras.getString("msg"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmTextView = (TextView) findViewById(R.id.alarmText);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Button silence = (Button) findViewById(R.id.silence_button);
        silence.setOnClickListener(this);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            setAlarmText(extras.getString("msg"));
        }
    }

    public void silence() {
        Intent stopIntent = new Intent(this, RingtonePlayingService.class);
        this.stopService(stopIntent);
    }

    public void setAlarmText(String alarmText) {
        alarmTextView.setText(alarmText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.silence_button:
                silence();
                break;
            default:
                break;
        }
    }

}
