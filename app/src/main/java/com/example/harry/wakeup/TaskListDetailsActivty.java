package com.example.harry.wakeup;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.List;


public class TaskListDetailsActivty extends ActionBarActivity {

    private DatabaseHelper dbHelper;
    private TaskList taskList;
    private List<Task> tasks;
    private LinearLayout parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        Intent intent = getIntent();
        int id = intent.getIntExtra("tasklist_id", -1);

        dbHelper = new DatabaseHelper(getApplicationContext());
        taskList = dbHelper.getTaskList(id);


        parent = (LinearLayout) findViewById(R.id.detail_parent_layout);

        TextView listViewTitle = (TextView) findViewById(R.id.task_list_title);
        listViewTitle.setText(taskList.getListName());

        TextView listViewSubTitle = (TextView) findViewById(R.id.task_list_subtitle);
        listViewSubTitle.setText(taskList.getListSubText());

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        tasks = dbHelper.getTasksByTaskList(taskList);
        Log.e("Total tasks 123", Integer.toString(tasks.size()));
        for(int i = 0; i < tasks.size(); i++){
            EditText et = new EditText(this);
            et.setLayoutParams(lparams);
            et.setText(tasks.get(i).getName());
            parent.addView(et);
        }
    }



}
