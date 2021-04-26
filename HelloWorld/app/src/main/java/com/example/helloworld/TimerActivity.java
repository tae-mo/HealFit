package com.example.helloworld;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Timer");
    }

    public void start_pause(View view){

    }
    public void control_time(View view){
        TextView set_time = (TextView)findViewById(R.id.set_time);
        TextView rest_time = (TextView)findViewById(R.id.rest_time);
        int cur_set_time = Integer.parseInt((String) set_time.getText());
        int cur_rest_time = Integer.parseInt((String) rest_time.getText());

        switch(view.getId()){
            case R.id.increase_set:
                set_time.setText((String)Integer.toString(cur_set_time + 10));
                break;
            case R.id.decrease_set:
                if(cur_set_time == 0){
                    Toast minus_time = Toast.makeText(this.getApplicationContext(), "0 보다 작을 수 없습니다", Toast.LENGTH_SHORT);
                    minus_time.show();
                    break;
                }
                set_time.setText((String)Integer.toString(cur_set_time - 10));
                break;
            case R.id.increase_rest:
                rest_time.setText((String)Integer.toString(cur_rest_time + 10));
                break;
            case R.id.decrease_rest:
                if(cur_rest_time == 0){
                    Toast minus_time = Toast.makeText(this.getApplicationContext(), "0 보다 작을 수 없습니다", Toast.LENGTH_SHORT);
                    minus_time.show();
                    break;
                }
                rest_time.setText((String)Integer.toString(cur_rest_time - 10));
                break;
        }
    }
}