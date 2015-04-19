package com.example.harry.wakeup;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.sql.Time;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;


public class AlarmDetailsActivity extends ActionBarActivity implements View.OnClickListener{

    private TimePicker timePicker;
    private Button taskListButton, modifyButton;
    private DatabaseHelper dbHelper;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_details);

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("alarm_id", -1);

        alarm = dbHelper.getAlarm(id);

        timePicker = (TimePicker) findViewById(R.id.details_time_picker);
        taskListButton = (Button) findViewById(R.id.details_tasklist_button);
        modifyButton = (Button) findViewById(R.id.details_submit_button);

        taskListButton.setOnClickListener(this);
        taskListButton.setText(alarm.getTaskList().getListName());
        modifyButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.details_tasklist_button:
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
                        alarm.setTaskList(taskLists.get(position));
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setContentView(baseView);
                materialDialog.show();
                break;
            case R.id.details_submit_button:
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                int combined = hour*100 + minute;
                alarm.setStatus(false);
                alarm.setTime(combined);
                dbHelper.updateAlarm(alarm);
                finish();
                break;
            default:
                break;
        }
    }
}
