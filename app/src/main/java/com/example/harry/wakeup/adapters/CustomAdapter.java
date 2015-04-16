package com.example.harry.wakeup.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.harry.wakeup.R;
import com.example.harry.wakeup.TaskList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harry on 13/03/2015.
 */
public class CustomAdapter  extends BaseAdapter {

    List<TaskList> items;
    Activity activity;
    LayoutInflater mInflater;

    public CustomAdapter(Activity activity, List<TaskList> items){
        this.items = items;
        this.activity = activity;
        this.mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View V = convertView;
        if(V == null) {
            V = mInflater.inflate(R.layout.custom_list_view_layout, parent, false);
        }

        TextView title =  (TextView)V.findViewById(R.id.list_item_title);

        title.setText(items.get(position).toString());

        return V;
    }
}
