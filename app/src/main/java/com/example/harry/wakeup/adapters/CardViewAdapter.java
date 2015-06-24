package com.example.harry.wakeup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harry.wakeup.R;
import com.example.harry.wakeup.Task;

import java.util.List;

/**
 * Created by Harry on 6/21/2015.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder>{

    private List<Task> tasks;

    public CardViewAdapter(List<Task> tasks){
        this.tasks = tasks;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.infoText.setText((position + 1) + ". " + task.getName());
    }

    @Override
    public int getItemCount() {
        if(tasks == null)
            return 0;
        return tasks.size();
    }

    public void updateDataset(List<Task> tasks){
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        private TextView infoText;

        public CardViewHolder(View itemView) {
            super(itemView);

            infoText = (TextView) itemView.findViewById(R.id.info_text);
        }

    }

}
