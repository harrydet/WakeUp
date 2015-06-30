package com.example.harry.wakeup.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.harry.wakeup.R;
import com.example.harry.wakeup.TaskList;
import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.List;

public class ListTaskListAdapter extends BaseAdapter implements View.OnClickListener{

    private Activity activity;
    private List<TaskList> taskLists;
    private LayoutInflater mInflater;
    private DatabaseHelper dbHelper;

    private AdapterCallback mAdapterCallback;

    private SharedPreferences settings;

    public ListTaskListAdapter(Activity activity, List<TaskList> taskLists, Fragment fragment) {
        super();
        this.activity = activity;
        this.taskLists = taskLists;
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.dbHelper = new DatabaseHelper(this.activity.getApplicationContext());

        try {
            this.mAdapterCallback = ((AdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }

        settings = PreferenceManager
                .getDefaultSharedPreferences(this.activity.getApplicationContext());
    }

    @Override
    public int getCount() {
        return taskLists.size();
    }

    @Override
    public Object getItem(int position) {
        return taskLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tasklist_item_view, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.list_item_title);
        title.setText(taskLists.get(position).getListName());

        TextView subText = (TextView) convertView.findViewById(R.id.list_item_subtitle);
        subText.setText(taskLists.get(position).getListSubText());
        subText.setTextColor(Color.GRAY);

        Button deleteButton = (Button) convertView.findViewById(R.id.delete_button);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_button:
                deleteItem((Integer) v.getTag());
                break;
            default:
                break;
        }
    }

    private void deleteItem(int position){
        int displayedTaskList = settings.getInt("tasklist_id_to_display", -1);
        if(taskLists.get(position).getId() == displayedTaskList){
            settings.edit().remove("tasklist_id_to_display").apply();
            mAdapterCallback.onMethodCallback();
        }
        dbHelper.deleteTaskList(taskLists.get(position).getId(), true);
        taskLists.remove(position);
        notifyDataSetChanged();
        if(taskLists.size() == 0){
            mAdapterCallback.onMethodCallback();
        }
    }

    public void updateDataset(List<TaskList> taskLists){
        this.taskLists = taskLists;
        notifyDataSetChanged();
    }

    public interface AdapterCallback {
        void onMethodCallback();
    }
}
