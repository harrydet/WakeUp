package com.example.harry.wakeup.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;


import android.widget.ToggleButton;

import com.example.harry.wakeup.Alarm;
import com.example.harry.wakeup.AlarmReceiver;
import com.example.harry.wakeup.R;
import com.example.harry.wakeup.RingtonePlayingService;
import com.example.harry.wakeup.TaskList;
import com.example.harry.wakeup.helpers.DatabaseHelper;

import org.w3c.dom.Text;


public class AlarmListAdapter  extends BaseAdapter implements View.OnClickListener {

    List<Alarm> alarms;
    Activity activity;
    LayoutInflater mInflater;
    ToggleButton alarmToggle;
    private PendingIntent pendingIntent;
    AlarmManager alarmManager;
    DatabaseHelper dbHelper;
    Button deleteAlarm;
    private SharedPreferences settings;

    private AdapterCallback mAdapterCallback;

    public AlarmListAdapter(Activity activity, List<Alarm> alarms, Fragment fragment){
        settings = PreferenceManager
                .getDefaultSharedPreferences(activity.getApplicationContext());
        this.alarms = alarms;
        this.activity = activity;
        this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        dbHelper = new DatabaseHelper(activity);

        try {
            this.mAdapterCallback = ((AdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View V = convertView;
        if(V == null) {
            V = mInflater.inflate(R.layout.alarm_list_item, parent, false);
        }

        TextView time =  (TextView)V.findViewById(R.id.time_text);
        int hour = alarms.get(position).getTime()/100;
        int minute = alarms.get(position).getTime()%100;
        if(minute < 10 && hour < 10){
          time.setText("0" + hour + ":0" + minute);
        } else if(hour < 10){
            time.setText("0" + hour + ":" + minute);
        } else if(minute < 10){
            time.setText(hour + ":0" + minute);
        } else {
            time.setText(hour + ":" + minute);
        }

        TextView alarmSubText = (TextView)V.findViewById(R.id.time_sub_text);
        TaskList alarmTaskList = alarms.get(position).getTaskList();
        if(alarmTaskList == null){
            alarmSubText.setText("No list assigned...");
            alarmSubText.setTextColor(activity.getResources().getColor(R.color.faintTextColor));
            alarmSubText.setTextSize(20);
        } else {
            alarmSubText.setText(alarmTaskList.getListName());
        }


        alarmToggle = (ToggleButton) V.findViewById(R.id.alarm_status_button);
        alarmToggle.setChecked(alarms.get(position).getStatus());
        alarmToggle.setTag(position);
        alarmToggle.setOnClickListener(this);

        deleteAlarm = (Button)V.findViewById(R.id.alarm_delete_button);
        deleteAlarm.setOnClickListener(this);
        deleteAlarm.setTag(position);

        return V;
    }

    public void updateDataset(List<Alarm> alarms){
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.alarm_delete_button){
            deleteItem((Integer) v.getTag());
            if(alarms.size() == 0){
                mAdapterCallback.onMethodCallback();
            }
        } else {
            boolean on = ((ToggleButton) v).isChecked();
            if(on && alarms.get((Integer) v.getTag()).getTaskList() != null){
                alarms.get((Integer) v.getTag()).setStatus(true);
                dbHelper.updateAlarm(alarms.get((Integer) v.getTag()));
                Calendar calendar = Calendar.getInstance();
                int hour = alarms.get((Integer) v.getTag()).getTime()/100;
                int minute = alarms.get((Integer) v.getTag()).getTime()%100;
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                alarms.get((Integer) v.getTag()).engage(calendar, alarmManager, activity);

            } else if (!on && alarms.get((Integer) v.getTag()).getTaskList() != null){
                alarms.get((Integer) v.getTag()).setStatus(false);
                dbHelper.updateAlarm(alarms.get((Integer) v.getTag()));
                Intent myIntent = new Intent(activity, AlarmReceiver.class);
                alarmManager.cancel(PendingIntent.getBroadcast(activity, alarms.get((Integer) v.getTag()).getId(), myIntent, 0));

                Intent stopIntent = new Intent(activity, RingtonePlayingService.class);
                activity.stopService(stopIntent);
            }
        }
    }

    private void deleteItem(int position){
        dbHelper.deleteAlarm(alarms.get(position).getId());
        alarms.remove(position);
        notifyDataSetChanged();
    }

    public interface AdapterCallback {
        void onMethodCallback();
    }
}
