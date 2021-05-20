package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void timerClick(View view){
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }
    public void recordClick(View view){
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }
    public void progressClick(View view){
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }
    public void calClick(View view){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}