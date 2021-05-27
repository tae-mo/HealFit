package com.example.healfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        SharedPreferences sp1, sp2, sp3, sp4, sp5, sp6, sp7, sp8, sp9;
        sp1 = getSharedPreferences("RecCal", MODE_PRIVATE);
        sp2 = getSharedPreferences("RecFile", MODE_PRIVATE);
        sp3 = getSharedPreferences("RecKcal", MODE_PRIVATE);
        sp4 = getSharedPreferences("RecLog", MODE_PRIVATE);
        sp5 = getSharedPreferences("RecPlan", MODE_PRIVATE);
        sp6 = getSharedPreferences("RecPlanCnt", MODE_PRIVATE);
        sp7= getSharedPreferences("RecProgress", MODE_PRIVATE);
        sp8 = getSharedPreferences("RecStar", MODE_PRIVATE);
        sp9 = getSharedPreferences("TimerRemoved", MODE_PRIVATE);


        sp1.edit().clear().apply();
        sp2.edit().clear().apply();
        sp3.edit().clear().apply();
        sp4.edit().clear().apply();
        sp5.edit().clear().apply();
        sp6.edit().clear().apply();
        sp7.edit().clear().apply();
        sp8.edit().clear().apply();
        sp9.edit().clear().apply();
        */

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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