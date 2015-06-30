package com.example.harry.wakeup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.List;


public class TaskListDetailsActivty extends ActionBarActivity implements View.OnClickListener {

    EditText[] editTexts;
    TextView listViewTitle;
    TextView listViewSubTitle;
    private DatabaseHelper dbHelper;
    private TaskList taskList;
    private List<Task> tasks;
    private LinearLayout parent;
    private int totalTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        Intent intent = getIntent();
        int id = intent.getIntExtra("tasklist_id", -1);

        dbHelper = new DatabaseHelper(getApplicationContext());
        taskList = dbHelper.getTaskList(id);


        parent = (LinearLayout) findViewById(R.id.inner_detail_layout);

        listViewTitle = (TextView) findViewById(R.id.task_list_title);
        listViewTitle.setText(taskList.getListName());

        listViewSubTitle = (TextView) findViewById(R.id.task_list_subtitle);
        listViewSubTitle.setText(taskList.getListSubText());

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tasks = dbHelper.getTasksByTaskList(taskList);
        totalTasks = tasks.size();
        editTexts = new EditText[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            EditText et = new EditText(this);
            et.setLayoutParams(lparams);
            et.setText(tasks.get(i).getName());
            parent.addView(et);
            editTexts[i] = et;
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion <= Build.VERSION_CODES.KITKAT) {
                et.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;

        }
    }

    private void modifyTasklist() {
        for (int i = 0; i < totalTasks; i++) {
            Task t = tasks.get(i);
            t.setName(editTexts[i].getText().toString());
            dbHelper.updateTask(t);
        }
    }

    private boolean fieldsValid() {
        boolean valid = true;
        if (!listViewTitle.getText().toString().isEmpty() && !listViewSubTitle.getText().toString().isEmpty()
                && listViewTitle.getText().toString().matches(".*\\w.*") && listViewSubTitle.getText().toString().matches(".*\\w.*")) {
            for (int i = 0; i < totalTasks; i++) {
                if (editTexts[i].getText().toString().isEmpty() || !editTexts[i].getText().toString().matches(".*\\w.*")) {
                    valid = false;
                }
            }
        } else {
            valid = false;
        }
        return valid;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                this.finish();
                break;
            case R.id.action_done:
                if (fieldsValid()) {
                    modifyTasklist();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Some of the input fields are empty", Toast.LENGTH_LONG).show();
                }
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
