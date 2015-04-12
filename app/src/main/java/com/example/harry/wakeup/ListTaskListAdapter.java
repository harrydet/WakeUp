package com.example.harry.wakeup;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.harry.wakeup.helpers.DatabaseHelper;

import java.util.List;

/**
 * Created by Harry on 4/9/2015.
 */
public class ListTaskListAdapter extends BaseAdapter implements View.OnClickListener{

    private Activity activity;
    private List<TaskList> taskLists;
    private LayoutInflater mInflater;
    private DatabaseHelper dbHelper;

    private AdapterCallback mAdapterCallback;

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

        Button deleteButton = (Button) convertView.findViewById(R.id.delete_button);
        deleteButton.setTag(position);
        deleteButton.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete_button:;
                deleteItem((Integer) v.getTag());
                break;
            default:
                break;
        }
    }

    private void deleteItem(int position){
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

    public static interface AdapterCallback {
        void onMethodCallback();
    }
}
