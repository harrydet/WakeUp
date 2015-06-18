package com.example.harry.wakeup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
    private Button setButton;
    private DatabaseHelper dbHelper;
    private Button taskListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        setButton = (Button) findViewById(R.id.setButton);
        setButton.setOnClickListener(this);

        taskListButton = (Button) findViewById(R.id.taskListButton);
        taskListButton.setOnClickListener(this);
        taskListButton.setTag(-1);

        dbHelper = new DatabaseHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setButton:
                long id = createRecord(pendingIntent);
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
                    pendingIntent = PendingIntent.getBroadcast(NewAlarmActivity.this, (int) id, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
                    finish();
                }
                break;
            case R.id.taskListButton:
                final MaterialDialog materialDialog = new MaterialDialog(this);
                materialDialog.setTitle("Pick a list of tasks");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
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
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setContentView(baseView);
                materialDialog.show();
        }
    }

    public long createRecord(PendingIntent pendingIntent){
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }
}
