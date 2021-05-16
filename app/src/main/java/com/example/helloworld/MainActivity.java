package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 매일 특정 시간 운동 Record 데이터 삭제 스케줄러 ( 매일 자정 )
        this.initializeRecordsSchedular();

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

    // 매일 특정 시간 운동 Record 데이터 삭제 스케줄러 ( 매일 자정 )
    public void initializeRecordsSchedular() {
        Intent intent = new Intent(this, SchedularReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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