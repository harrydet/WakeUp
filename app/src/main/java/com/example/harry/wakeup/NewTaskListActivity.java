package com.example.harry.wakeup;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.harry.wakeup.helpers.DatabaseHelper;


public class NewTaskListActivity extends ActionBarActivity implements View.OnClickListener{

    private static int TOTAL_EDIT_TEXT = 5;
    private int TOTAL_VISIBLE;

    private Button moreTasksButton;
    private Button submitButton;
    private EditText[] textFields;
    private EditText titleText;
    private EditText subtitleText;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_list);
        dbHelper = new DatabaseHelper(getApplicationContext());

        moreTasksButton = (Button) findViewById(R.id.moreTasksButton);
        moreTasksButton.setOnClickListener(this);
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        titleText = (EditText) findViewById(R.id.titleText);
        subtitleText = (EditText) findViewById(R.id.subtitleTextEdit);

        textFields = new EditText[TOTAL_EDIT_TEXT];
        textFields[0] = (EditText) findViewById(R.id.editText1);
        textFields[1] = (EditText) findViewById(R.id.editText2);
        textFields[2] = (EditText) findViewById(R.id.editText3);
        textFields[3] = (EditText) findViewById(R.id.editText4);
        textFields[4] = (EditText) findViewById(R.id.editText5);

        TOTAL_VISIBLE = 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.moreTasksButton:
                showNextEditText();
                break;
            case R.id.submitButton:
                saveListItem();
                finish();
                break;
            default:
                break;
        }
    }

    private void showNextEditText(){
        int i = 0;
        while(i < 5 && textFields[i].isShown()){
            i++;
        }
        if(i < 5){
            textFields[i].setVisibility(View.VISIBLE);
            TOTAL_VISIBLE++;
        }
    }

    private void saveListItem(){
        long[] taskIds = new long[TOTAL_VISIBLE];
        for(int i = 0; i < TOTAL_VISIBLE; i++){
            Task t = new Task();
            t.setName(textFields[i].getText().toString());
            t.setDescription("Sample Description");
            taskIds[i] = dbHelper.createTask(t);
        }

        TaskList tl = new TaskList();
        tl.setListName(titleText.getText().toString());
        tl.setListSubText(subtitleText.getText().toString());
        Log.e("Subtext", subtitleText.getText().toString());
        dbHelper.createTaskList(tl, taskIds);
        dbHelper.closeDB();
    }
}
