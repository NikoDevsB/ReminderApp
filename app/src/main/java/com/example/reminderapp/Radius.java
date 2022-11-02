package com.example.reminderapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Radius extends AppCompatActivity {

    SeekBar seekBar;
    TextView value,prog;
    SharedPreferences shrd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius);
        getSupportActionBar().hide();


        seekBar=findViewById(R.id.seekbar);
        seekBar.setProgress(1);
        seekBar.setMax(50);
        shrd =  getSharedPreferences("savedRadius", Activity.MODE_PRIVATE);

        value=findViewById(R.id.progress);
        String radiusValue = shrd.getString("radiusValue","1");
        value.setText(radiusValue+"/"+"50"+" Km");
        int progressValue = Integer.parseInt(radiusValue);
        seekBar.setProgress(progressValue);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            // increment or decrement on process changed

            // increase the textsize
            // with the value of progress
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value.setText(progress+"/"+"50"+"Km");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user touches the SeekBar
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // This method will automatically
                // called when the user
                // stops touching the SeekBar
            }
        });
    }
    //Save Radius function. gets the value from seek bar progress
    public void saveRadius(View view){
        SharedPreferences.Editor editor = shrd.edit();
        String value = String.valueOf(seekBar.getProgress());

        editor.putString("radiusValue",value);

        editor.apply();
        Intent intent = new Intent(Radius.this,mainApp.class);
        startActivity(intent);

    }

    public void closeWindow(View view){
        Intent intent = new Intent(Radius.this,Settings.class);
        startActivity(intent);
    }

}