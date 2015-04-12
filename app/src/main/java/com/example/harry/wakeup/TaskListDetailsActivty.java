package com.example.harry.wakeup;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import org.w3c.dom.Text;

import java.util.List;


public class TaskListDetailsActivty extends ActionBarActivity implements View.OnClickListener{

    private DatabaseHelper dbHelper;
    private TaskList taskList;
    private List<Task> tasks;
    private LinearLayout parent;
    private Button modifyButton;
    EditText [] editTexts;

    TextView listViewTitle;
    TextView listViewSubTitle;

    private int totalTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

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
        Log.e("Total tasks 123", Integer.toString(tasks.size()));
        totalTasks = tasks.size();
        editTexts = new EditText[tasks.size()];
        for(int i = 0; i < tasks.size(); i++){
            EditText et = new EditText(this);
            et.setLayoutParams(lparams);
            et.setText(tasks.get(i).getName());
            parent.addView(et);
            editTexts[i] = et;
        }

        modifyButton = (Button) findViewById(R.id.modifyButton);
        modifyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.modifyButton:
                if(fieldsValid()) {
                    modifyTasklist();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Some of the input fields are empty", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;

        }
    }

    private void modifyTasklist(){
        for(int i = 0; i < totalTasks; i++){
            Task t = tasks.get(i);
            t.setName(editTexts[i].getText().toString());
            dbHelper.updateTask(t);
        }
    }

    private boolean fieldsValid(){
        boolean valid = true;
        if (!listViewTitle.getText().toString().isEmpty() && !listViewSubTitle.getText().toString().isEmpty()
                && listViewTitle.getText().toString().matches(".*\\w.*") && listViewSubTitle.getText().toString().matches(".*\\w.*")){
            for(int i = 0; i < totalTasks; i++){
                if(editTexts[i].getText().toString().isEmpty() || !editTexts[i].getText().toString().matches(".*\\w.*")){
                    valid = false;
                }
            }
        } else {
            valid = false;
        }
        return valid;
    }
}
