package com.example.helloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SchedularReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent){
        System.out.println(":::::::::::::::::::::::스케쥴러 동작 확인:::::::::::::::::::::::::");
    }
}