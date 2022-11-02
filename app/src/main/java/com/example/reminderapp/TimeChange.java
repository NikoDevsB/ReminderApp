package com.example.reminderapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.base.MoreObjects;

import java.sql.Time;
import java.util.Calendar;
import java.util.Locale;

public class TimeChange extends AppCompatActivity {

    int hour,minute;
    TextView timeReceivedStart,timeReceivedEnd;
    SharedPreferences shrd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_change);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        View startTimeBtn = findViewById(R.id.start_time_btn);
        View endTimeBtn = findViewById(R.id.end_time_btn);
        timeReceivedStart = findViewById(R.id.time_received_start);
        timeReceivedEnd = findViewById(R.id.time_received_end);
        shrd =  getSharedPreferences("savedTime", Context.MODE_PRIVATE);

        String timeStart = shrd.getString("timeStart","");
        timeReceivedStart.setText(timeStart);
        String timeEnd = shrd.getString("timeEnd","");
        timeReceivedEnd.setText(timeEnd);

    }

    //Opening the Clock and using the picked time by users (for start time)
    public void openStartClock(View view){
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour =selectedHour;
                minute= selectedMinute;
                timeReceivedStart.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));

            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,onTimeSetListener,hour,minute,true);
        timePickerDialog.show();

    }

    //Opening the Clock and using the picked time by users (for end time)
    public void openEndClock(View view){

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour =selectedHour;
                minute= selectedMinute;
                timeReceivedEnd.setText(String.format(Locale.getDefault(),"%02d:%02d",hour,minute));

            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,onTimeSetListener,hour,minute,true);
        timePickerDialog.show();

    }

    //Save Time function. gets the time Information from "Saved Time" file
    public void saveTime(View view) {
        SharedPreferences.Editor editor = shrd.edit();
        editor.putString("timeStart",timeReceivedStart.getText().toString());
        editor.putString("timeEnd",timeReceivedEnd.getText().toString());

        editor.apply();
        Intent intent = new Intent(TimeChange.this,mainApp.class);
        startActivity(intent);

    }

    public void closeWindow(View view){
        Intent intent = new Intent(TimeChange.this,Settings.class);
        startActivity(intent);
    }


}

