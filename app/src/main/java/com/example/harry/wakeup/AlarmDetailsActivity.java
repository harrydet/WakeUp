package com.example.harry.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class AlarmDetailsActivity extends ActionBarActivity implements View.OnClickListener{

    private TimePicker timePicker;
    private Button taskListButton;
    private DatabaseHelper dbHelper;
    private Alarm alarm;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("alarm_id", -1);

        alarm = dbHelper.getAlarm(id);

        timePicker = (TimePicker) findViewById(R.id.details_time_picker);
        timePicker.setCurrentHour(alarm.getTime()/100);
        timePicker.setCurrentMinute(alarm.getTime()%100);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= Build.VERSION_CODES.KITKAT){
            timePicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        taskListButton = (Button) findViewById(R.id.details_tasklist_button);

        taskListButton.setOnClickListener(this);
        TaskList alarmTaskList = alarm.getTaskList();
        if(alarmTaskList != null) {
            taskListButton.setText(alarm.getTaskList().getListName());
            taskListButton.setTag(alarm.getTaskList().getId());
        } else {
            taskListButton.setText("Pick a list of tasks...");
            taskListButton.setTag(-1);
        }
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_tasklist_button:
                final MaterialDialog materialDialog = new MaterialDialog(this);
                materialDialog.setTitle("Pick a list of tasks");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
                final List<TaskList> taskLists = dbHelper.getAllTaskLists();
                for(int i = 0; i < taskLists.size(); i++){
                    arrayAdapter.add(taskLists.get(i).getListName());
                }

                View baseView = LayoutInflater.from(this).inflate(R.layout.tasklist_listview, null);
                ListView listView = (ListView) baseView.findViewById(R.id.dialog_list_view);

                listView.setAdapter(arrayAdapter);
                listView.setDividerHeight(0);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        taskListButton.setTag(taskLists.get(position).getId());
                        taskListButton.setText(taskLists.get(position).getListName());
                        alarm.setTaskList(taskLists.get(position));
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton("Cancel", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });

                materialDialog.setCanceledOnTouchOutside(true);
                materialDialog.setContentView(baseView);
                materialDialog.show();
                break;
            default:
                break;
        }
    }

    private void setButtonHandling(){
        long id = modifyRecord();
        if(id == -1){
            Toast.makeText(getApplicationContext(), "Need to select a tasklist...", Toast.LENGTH_SHORT).show();
        } else {
            id = alarm.getId();
            Intent myIntent = new Intent(this, AlarmReceiver.class);
            alarmManager.cancel(PendingIntent.getBroadcast(this, alarm.getId(), myIntent, 0));
            Intent stopIntent = new Intent(this, RingtonePlayingService.class);
            stopService(stopIntent);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            Intent myAlarmIntent = new Intent(this, AlarmReceiver.class);
            Log.e("PUT", Integer.toString(dbHelper.getAlarm(id).getTaskList().getId()));
            myAlarmIntent.putExtra("tasklist_id", dbHelper.getAlarm(id).getTaskList().getId());
            settings.edit().putLong("ringing_alarm_id", id).apply();
            pendingIntent = PendingIntent.getBroadcast(this, (int) id, myAlarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 86400000,  pendingIntent);
            finish();
        }

    }

    private long modifyRecord(){
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();
        int combined = hour*100 + minute;
        alarm.setStatus(true);
        alarm.setTime(combined);
        if(taskListButton.getTag() == -1){
            return -1;
        } else {
            alarm.setTaskList(dbHelper.getTaskList((int)taskListButton.getTag()));
            return dbHelper.updateAlarm(alarm);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                this.finish();
                break;
            case R.id.action_done:
                setButtonHandling();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.accept_icon, menu);
        return true;
    }
}
