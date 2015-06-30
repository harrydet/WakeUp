package com.example.harry.wakeup;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harry.wakeup.helpers.DatabaseHelper;


public class NewTaskListActivity extends ActionBarActivity implements View.OnClickListener {

    private static int TOTAL_EDIT_TEXT = 5;
    private int TOTAL_VISIBLE;

    private Button moreTasksButton;
    private EditText[] textFields;
    private EditText titleText;
    private EditText subtitleText;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        dbHelper = new DatabaseHelper(getApplicationContext());

        moreTasksButton = (Button) findViewById(R.id.moreTasksButton);
        moreTasksButton.setOnClickListener(this);

        titleText = (EditText) findViewById(R.id.titleText);

        subtitleText = (EditText) findViewById(R.id.subtitleTextEdit);

        textFields = new EditText[TOTAL_EDIT_TEXT];
        textFields[0] = (EditText) findViewById(R.id.editText1);
        textFields[1] = (EditText) findViewById(R.id.editText2);
        textFields[2] = (EditText) findViewById(R.id.editText3);
        textFields[3] = (EditText) findViewById(R.id.editText4);
        textFields[4] = (EditText) findViewById(R.id.editText5);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion <= Build.VERSION_CODES.KITKAT) {
            for (EditText textField : textFields) {
                textField.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            titleText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            subtitleText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        TOTAL_VISIBLE = 1;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.moreTasksButton:
                showNextEditText();
                break;
            default:
                break;
        }
    }

    private void showNextEditText() {
        int i = 0;
        while (i < 5 && textFields[i].isShown()) {
            i++;
        }
        if (i < 5) {
            textFields[i].setVisibility(View.VISIBLE);
            TOTAL_VISIBLE++;
        }
    }

    private void saveListItem() {
        long[] taskIds = new long[TOTAL_VISIBLE];
        for (int i = 0; i < TOTAL_VISIBLE; i++) {
            Task t = new Task();
            t.setName(textFields[i].getText().toString());
            t.setDescription("Sample Description");
            taskIds[i] = dbHelper.createTask(t);
        }

        TaskList tl = new TaskList();
        tl.setListName(titleText.getText().toString());
        tl.setListSubText(subtitleText.getText().toString());
        dbHelper.createTaskList(tl, taskIds);
        dbHelper.closeDB();
    }

    private boolean fieldsValid() {
        boolean valid = true;
        if (!titleText.getText().toString().isEmpty() && !subtitleText.getText().toString().isEmpty()
                && titleText.getText().toString().matches(".*\\w.*") && subtitleText.getText().toString().matches(".*\\w.*")) {
            for (int i = 0; i < TOTAL_VISIBLE; i++) {
                if (textFields[i].getText().toString().isEmpty() && !textFields[i].getText().toString().matches(".*\\w.*")) {
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
                    saveListItem();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Some of the input fields are empty", Toast.LENGTH_LONG).show();
                }

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
