package com.example.reminderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderapp.DataModel.Note;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private ArrayList<Reminder> remindersList;
    private RecyclerViewClickListener listener;
    private setRefreshListener refreshListener;
    private List<Note> noteList;
    private ArrayList testList;
    SQL sql;


    public Adapter(ArrayList testList, ArrayList<Reminder> remindersList, RecyclerViewClickListener listener, List<Note> noteList) {
        this.testList = testList;
        this.noteList = noteList;
        this.remindersList = remindersList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView reminderText;
        private View completeReminder,editReminder;



        public MyViewHolder(final View view) {
            super(view);
            reminderText = view.findViewById(R.id.reminder_text);
            completeReminder = view.findViewById(R.id.complete_reminder);
            editReminder = view.findViewById(R.id.edit_reminder);
            completeReminder.setOnClickListener(this);
            editReminder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }


    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, int position) {
        Reminder rm= remindersList.get(position);
        holder.reminderText.setText(rm.getFreeText());
        String id = remindersList.get(position).getId();
        holder.itemView.setTag(id);

    }


    @Override
    public int getItemCount() {
        return remindersList.size();
    }
//    @Override
//    public int getItemCount() {
//        return remindersList.size();
//    }

    public interface RecyclerViewClickListener {
        void onClick(View v, int position);
    }

    public interface setRefreshListener {
        void refresh();
    }

}
