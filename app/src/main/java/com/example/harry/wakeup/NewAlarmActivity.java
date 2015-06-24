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

import java.util.Calendar;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class NewAlarmActivity extends ActionBarActivity implements View.OnClickListener{

    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private DatabaseHelper dbHelper;
    private Button taskListButton;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        settings = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= Build.VERSION_CODES.KITKAT){
            alarmTimePicker.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        taskListButton = (Button) findViewById(R.id.taskListButton);
        taskListButton.setOnClickListener(this);
        taskListButton.setTag(-1);

        dbHelper = new DatabaseHelper(this);
    }

    private void setButtonHandling(){
        long id = createRecord();
        if(id == -1) {
            Toast.makeText(getApplicationContext(), "Need to select a tasklist...", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("AlarmActivity", "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            Intent myIntent = new Intent(NewAlarmActivity.this, AlarmReceiver.class);
            Log.e("PUT", Integer.toString(dbHelper.getAlarm(id).getTaskList().getId()));
            myIntent.putExtra("tasklist_id", dbHelper.getAlarm(id).getTaskList().getId());
            settings.edit().putLong("ringing_alarm_id", dbHelper.getAlarm(id).getId()).apply();
            pendingIntent = PendingIntent.getBroadcast(NewAlarmActivity.this, (int) id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.taskListButton:
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
                        taskListButton.setTextColor(getResources().getColor(R.color.textColorPrimary));
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
        }
    }

    public long createRecord(){
        int hour = alarmTimePicker.getCurrentHour();
        int minute = alarmTimePicker.getCurrentMinute();
        int combined = hour*100 + minute;
        Alarm alarm = new Alarm();
        alarm.setStatus(true);
        alarm.setTime(combined);
        if(taskListButton.getTag() == -1){
            return -1;
        } else {
            alarm.setTaskList(dbHelper.getTaskList((int)taskListButton.getTag()));
            return dbHelper.createAlarm(alarm);
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
