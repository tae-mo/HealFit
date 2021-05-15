package com.example.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

// 매일 특정 시간 운동 Record 데이터 삭제 스케줄러 Receiver
public class SchedularReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        System.out.println(":::::::::::::::::::::::스케줄러 Run:::::::::::::::::::::::");

        SharedPreferences sp = context.getSharedPreferences("RecFile", MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

        System.out.println(":::::::::::::::::::::::RecFile 삭제 완료:::::::::::::::::::::::");
    }
}