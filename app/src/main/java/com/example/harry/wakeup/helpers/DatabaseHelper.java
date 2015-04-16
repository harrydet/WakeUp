package com.example.harry.wakeup.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.harry.wakeup.Alarm;
import com.example.harry.wakeup.Task;
import com.example.harry.wakeup.TaskList;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contactsManager";
    private static final String TABLE_TASK = "task";
    private static final String TABLE_TASKLIST = "tasklist";
    private static final String TABLE_TASK_TASKLIST = "task_tasklist";
    private static final String TABLE_ALARM = "alarm";
    private static final String TABLE_ALARM_TASKLIST = "alarm_tasklist";

    private static final String KEY_ID = "id";

    private static final String KEY_TASK_NAME = "name";
    private static final String KEY_TASK_DESCRIPTION = "description";

    private static final String KEY_TASKLIST_NAME = "name";
    private static final String KEY_TASKLIST_SUBTEXT = "subtext";

    private static final String KEY_TASK_ID = "task_id";
    private static final String KEY_TASKLIST_ID = "tasklist_id";
    private static final String KEY_ALARM_ID = "alarm_id";

    private static final String KEY_ALARM_DATE = "date";
    private static final String KEY_ALARM_STATUS = "satus";


    //Tasklist tables
    private static final String CREATE_TABLE_TASK = "CREATE TABLE "
            + TABLE_TASK + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TASK_NAME
            + " TEXT,"  + KEY_TASK_DESCRIPTION
            + " TEXT" + ")";

    private static final String CREATE_TABLE_TASKLIST = "CREATE TABLE " + TABLE_TASKLIST
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TASKLIST_NAME + " TEXT," + KEY_TASKLIST_SUBTEXT
            + " TEXT" + ")";

    private static final String CREATE_TABLE_TASK_TASKLIST = "CREATE TABLE "
            + TABLE_TASK_TASKLIST + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
            +  KEY_TASK_ID + " INTEGER REFERENCES task(id) ON DELETE CASCADE ON UPDATE CASCADE, " + KEY_TASKLIST_ID + " INTEGER REFERENCES tasklist(id) ON DELETE CASCADE ON UPDATE CASCADE"+ ")";

    //Alarm tables
    private static final String CREATE_TABLE_ALARM = "CREATE TABLE "
            + TABLE_ALARM + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ALARM_DATE
            + " INT,"  + KEY_ALARM_STATUS
            + " INT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_TASKLIST);
        db.execSQL(CREATE_TABLE_TASK_TASKLIST);
        db.execSQL(CREATE_TABLE_ALARM);
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK_TASKLIST);

        // create new tables
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        Log.e("Executed: ", "PRAGMA");
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    public long createTaskList(TaskList taskList, long[] task_ids) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKLIST_NAME, taskList.getListName());
        values.put(KEY_TASKLIST_SUBTEXT, taskList.getListSubText());

        // insert row
        long tasklist_id = db.insert(TABLE_TASKLIST, null, values);

        // assigning tags to todo
        for (long task_id : task_ids) {
            createTaskTaskList(tasklist_id, task_id);
        }

        return tasklist_id;
    }

    public long createAlarm(Alarm alarm){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(alarm.getStatus()){
            values.put(KEY_ALARM_STATUS, 1);
        } else {
            values.put(KEY_ALARM_STATUS, 0);
        }
        values.put(KEY_ALARM_DATE, alarm.getTime());

        long alarm_id = db.insert(TABLE_ALARM, null, values);
        return alarm_id;
    }

    public Alarm getAlarm(long alarm_id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ALARM + " WHERE "
                + KEY_ID + " = " + alarm_id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Alarm al = new Alarm();
        al.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        int status = c.getInt(c.getColumnIndex(KEY_ALARM_STATUS));
        if(status == 0){
            al.setStatus(false);
        } else {
            al.setStatus(true);
        }
        al.setTime(c.getInt(c.getColumnIndex(KEY_ALARM_DATE)));

        return al;
    }

    public List<Alarm> getAllAlarms(){
        List<Alarm> alarms = new ArrayList<Alarm>();
        String selectQuery = "SELECT  * FROM " + TABLE_ALARM;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Alarm al = new Alarm();
                al.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                int status = c.getInt(c.getColumnIndex(KEY_ALARM_STATUS));
                if(status == 0){
                    al.setStatus(false);
                } else {
                    al.setStatus(true);
                }
                al.setTime(c.getInt(c.getColumnIndex(KEY_ALARM_DATE)));

                // adding to todo list
                alarms.add(al);
            } while (c.moveToNext());
        }

        return alarms;
    }


    public TaskList getTaskList(long tasklist_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_TASKLIST + " WHERE "
                + KEY_ID + " = " + tasklist_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        TaskList tl = new TaskList();
        tl.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        tl.setListName((c.getString(c.getColumnIndex(KEY_TASKLIST_NAME))));
        tl.setListSubText(c.getString(c.getColumnIndex(KEY_TASKLIST_SUBTEXT)));

        return tl;
    }

    public List<TaskList> getAllTaskLists() {
        List<TaskList> taskLists = new ArrayList<TaskList>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASKLIST;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                TaskList tl = new TaskList();
                tl.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                tl.setListName((c.getString(c.getColumnIndex(KEY_TASKLIST_NAME))));
                tl.setListSubText(c.getString(c.getColumnIndex(KEY_TASKLIST_SUBTEXT)));

                // adding to todo list
                taskLists.add(tl);
            } while (c.moveToNext());
        }

        return taskLists;
    }

    public List<TaskList> getAllTaskListsByTask(String task_name) {
        List<TaskList> taskLists = new ArrayList<TaskList>();

        String selectQuery = "SELECT  * FROM " + TABLE_TASKLIST + " td, "
                + TABLE_TASK + " tg, " + TABLE_TASK_TASKLIST + " tt WHERE tg."
                + KEY_TASK_NAME + " = '" + task_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_TASK_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_TASKLIST_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                TaskList td = new TaskList();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setListName((c.getString(c.getColumnIndex(KEY_TASKLIST_NAME))));
                td.setListSubText(c.getString(c.getColumnIndex(KEY_TASKLIST_SUBTEXT)));

                // adding to todo list
                taskLists.add(td);
            } while (c.moveToNext());
        }

        return taskLists;
    }

    public int updateTaskList(TaskList taskList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKLIST_NAME, taskList.getListName());
        values.put(KEY_TASKLIST_SUBTEXT, taskList.getListSubText());

        // updating row
        return db.update(TABLE_TASKLIST, values, KEY_ID + " = ?",
                new String[] { String.valueOf(taskList.getId()) });
    }

    public void deleteTaskList(long tasklist_id, boolean should_delete_all_tasks) {
        SQLiteDatabase db = this.getWritableDatabase();

        if(should_delete_all_tasks){
            List<Task> allTasks = getTasksByTaskList(getTaskList(tasklist_id));

            // delete all tasklists
            for (Task task : allTasks) {
                // delete tasklist
                deleteTask(task);
            }
        }

        db.delete(TABLE_TASKLIST, KEY_ID + " = ?",
                new String[] { String.valueOf(tasklist_id) });
    }

    public long createTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_NAME, task.getName());
        values.put(KEY_TASK_DESCRIPTION, task.getDescription());

        // insert row
        long task_id = db.insert(TABLE_TASK, null, values);

        return task_id;
    }

    public List<Task> getAllTasks() {
        List<Task> tags = new ArrayList<Task>();
        String selectQuery = "SELECT  * FROM " + TABLE_TASK;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Task t = new Task();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setName(c.getString(c.getColumnIndex(KEY_TASK_NAME)));

                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        return tags;
    }

    public List<Task> getTasksByTaskList(TaskList taskList){
        List<Task> tasks = new ArrayList<Task>();

        String selectQuery = "SELECT  o.* FROM " + TABLE_TASK_TASKLIST + " AS uo INNER JOIN "
                + TABLE_TASK + " AS o ON uo." + KEY_TASK_ID + " = o."
                + KEY_ID + " WHERE uo." + KEY_TASKLIST_ID + " = " +  taskList.getId();

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Task td = new Task();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setName((c.getString(c.getColumnIndex(KEY_TASK_NAME))));
                td.setDescription(c.getString(c.getColumnIndex(KEY_TASK_DESCRIPTION)));

                tasks.add(td);
            } while (c.moveToNext());
        }

        return tasks;

    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_NAME, task.getName());

        // updating row
        return db.update(TABLE_TASK, values, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
    }

    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        // now delete the task
        db.delete(TABLE_TASK, KEY_ID + " = ?",
                new String[] { String.valueOf(task.getId()) });
    }

    public int deleteAllTasks(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK_TASKLIST, "1", null);
        return db.delete(TABLE_TASK, "1", null);
    }

    public long createTaskTaskList(long tasklist_id, long task_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASKLIST_ID, tasklist_id);
        values.put(KEY_TASK_ID, task_id);

        long id = db.insert(TABLE_TASK_TASKLIST, null, values);

        return id;
    }

    public int updateTaskListTsk(long id, long task_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TASK_ID, task_id);

        // updating row
        return db.update(TABLE_TASKLIST, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


}