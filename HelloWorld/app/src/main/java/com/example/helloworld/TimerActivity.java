package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimerActivity extends AppCompatActivity {
    static final String SET_COUNT = "SET_COUNT";

    Gson gson;

    Rec Rec;
    Integer weight = 0, volume = 0, count = 0;
    String workout;

    Handler handler;
    Button start, stop;
    TextView rest_time, set_time, time, timer_status, set_num, set_cnt, vol_cnt, date;
    EditText input_workout, input_weight;

    long baseTime, pauseTime;
    int user_def_rest, user_def_set, user_def_num, cur_num;
    boolean set_timer = true, rest_timer = true;//타이머 상태

    public static final int INIT = 0;//처음
    public static final int RUN = 1;//실행중
    public static final int PAUSE = 2;//일시정지

    public static int status = INIT;

    //TimeHandler timer ;

    String today;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //SharedPreferences setting = getSharedPreferences("RecFile", MODE_PRIVATE);
        //setting.edit().clear().apply();

        gson = new GsonBuilder().create();
        Rec = new Rec();
        handler = new Handler();
        //timer = new TimeHandler();

        start = (Button)findViewById(R.id.start_btn);

        time = (TextView)findViewById(R.id.time);
        date = (TextView)findViewById(R.id.date);
        rest_time = (TextView)findViewById(R.id.rest_time);
        set_time = (TextView)findViewById(R.id.set_time);
        set_num = (TextView)findViewById(R.id.set_num);
        set_cnt = (TextView)findViewById(R.id.set_cnt);
        vol_cnt = (TextView)findViewById(R.id.vol_cnt);
        timer_status = (TextView)findViewById(R.id.timer_status);

        input_workout = (EditText)findViewById(R.id.input_workout);
        input_weight = (EditText)findViewById(R.id.input_weight);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Date d = new Date();
        today = dateFormat.format(d);
        date.setText(today);
    }


    public void ask_reps(){
        NumberPicker reps = new NumberPicker(this);
        reps.setMinValue(0);
        reps.setMaxValue(30);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("몇 개 수행하셨나요?");
        builder.setView(reps);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int value = reps.getValue();
                volume += weight * value;
                vol_cnt.setText(volume.toString() + " kg");
            }
        });
        AlertDialog alt = builder.create();
        alt.show();
    }


    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (status == RUN || status == INIT) {
                String[] hms_str = ((String) time.getText()).split(":");
                int[] hms = {0, 0, 0};

                for (int i = 0; i < hms_str.length; i++) {//hms를 hms_str에 넣어줌
                    hms[i] = Integer.parseInt(hms_str[i]);
                }

                int new_s = 0, new_m = 0, new_h = 0;
                new_s = hms[2] - 1;//초 단위
                if (new_s == -1) { //
                    new_s = 59;
                    new_m = hms[1] - 1;
                    if (new_m == -1) {
                        new_m = 59;
                        new_h = hms[0] - 1;
                        if (new_h == -1) {
                            new_s = 0;
                            new_m = 0;
                            new_h = 0;
                        }
                    }

                }
                String new_time = String.format("%02d", new_h) + ":" + String.format("%02d", new_m) + ":" + String.format("%02d", new_s);
                time.setText(new_time);

                if (new_s == 0 && new_m == 0 && new_h == 0) {
                    handler.removeCallbacks(this);
                    if (set_timer) set_timer = false;
                    else {//완전 싹 다 끝났을 때
                        set_timer = true;
                        cur_num++;
                        count++;
                        set_cnt.setText(Integer.toString(count) + " set");
                        if (cur_num == user_def_num + 1) {  // 사용자가 정의한 세트수 만큼 수행했다면
                            status = INIT;
                            start.setText("시작");
                            timer_status.setText("타이머를 설정해주세요");
                            user_def_num = 0;
                            user_def_rest = 0;
                            user_def_set = 0;
                            input_weight.setText("");
                            input_workout.setText("");
                            input_weight.setEnabled(true);
                            input_workout.setEnabled(true);
                            return;
                        }
                        set_time_setter();
                        timer_status.setText(cur_num + "번째 세트 진행중");
                    }
                    if (!set_timer) {
                        rest_time_setter();
                        timer_status.setText(cur_num + "번째 휴식 진행중");
                        ask_reps();
                    }
                }

                handler.postDelayed(this, 1000);//1초에 한번씩 run()함수를 호출한다
            }
            else if (status == PAUSE){
                handler.postDelayed(this, 1);
            }
        }
    };

    public void start_pause(View view){
        if (status == INIT) {
            cur_num = 1;

            if (user_def_num == 0 || user_def_rest == 0 || user_def_set == 0 || input_workout.getText().toString().length() == 0 || input_weight.getText().toString().length() == 0) {
                Toast zero_time = Toast.makeText(this.getApplicationContext(), "설정값들을 모두 설정해 주세요", Toast.LENGTH_SHORT);
                zero_time.show();
            } else {//타이머 준비
                status = RUN;
                start.setText("일시정지");

                workout = input_workout.getText().toString();
                weight = Integer.parseInt(input_weight.getText().toString());

                set_time.setText("0");
                rest_time.setText("0");
                set_num.setText("0");
                input_weight.setEnabled(false);
                input_workout.setEnabled(false);

                set_timer = true;
                rest_timer = true;

                set_time_setter();
                timer_status.setText(cur_num + "번째 세트 진행중");

                handler.postDelayed(runnable, 1000);//타이머 시작
                //start.setEnabled(false);
            }
        }
        else if(status == RUN) {
            status = PAUSE;
            start.setText("시작");
        }
        else {
            status = RUN;
            start.setText("일시정지");
        }
    }

    private void set_time_setter(){
        int h = (int)(user_def_set / 3600);
        int m = (int)((user_def_set - (3600*h)) / 60);
        int s = (int)((user_def_set - (60*m) - (3600*h)));

        String user_def_time = String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);

        time.setText(user_def_time);
    }
    private void rest_time_setter(){
        int h = (int)(user_def_rest / 3600);
        int m = (int)((user_def_rest - (3600*h)) / 60);
        int s = (int)((user_def_rest - (60*m) - (3600*h)));

        String user_def_time = String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);

        time.setText(user_def_time);
    }

    public void control_time(View view){
        int cur_set_time = Integer.parseInt((String) set_time.getText());
        int cur_rest_time = Integer.parseInt((String) rest_time.getText());

        switch(view.getId()){
            case R.id.increase_set:
                set_time.setText((String)Integer.toString(cur_set_time + 10));
                user_def_set = cur_set_time + 10;
                break;
            case R.id.decrease_set:
                if(cur_set_time == 0){
                    Toast minus_time = Toast.makeText(this.getApplicationContext(), "0 보다 작을 수 없습니다", Toast.LENGTH_SHORT);
                    minus_time.show();
                    break;
                }
                set_time.setText((String)Integer.toString(cur_set_time - 10));
                user_def_set = cur_set_time - 10;
                break;
            case R.id.increase_rest:
                rest_time.setText((String)Integer.toString(cur_rest_time + 10));
                user_def_rest = cur_rest_time + 10;
                break;
            case R.id.decrease_rest:
                if(cur_rest_time == 0){
                    Toast minus_time = Toast.makeText(this.getApplicationContext(), "0 보다 작을 수 없습니다", Toast.LENGTH_SHORT);
                    minus_time.show();
                    break;
                }
                rest_time.setText((String)Integer.toString(cur_rest_time - 10));
                user_def_rest = cur_rest_time - 10;
                break;
        }
    }

    public void control_set(View view){
        int cur_set_num = Integer.parseInt((String) set_num.getText());
        switch(view.getId()){
            case R.id.increase_num:
                set_num.setText((String)Integer.toString(cur_set_num + 1));
                user_def_num = cur_set_num + 1;
                break;
            case R.id.decrease_num:
                if(cur_set_num == 0){
                    Toast minus_num = Toast.makeText(this.getApplicationContext(), "0 보다 작을 수 없습니다", Toast.LENGTH_SHORT);
                    minus_num.show();
                    break;
                }
                set_num.setText((String)Integer.toString(cur_set_num - 1));
                user_def_num = cur_set_num - 1;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(volume != 0 && workout != "" && count != 0) {
            Rec.volume = volume;
            Rec.date = today;
            Rec.workout = workout;
            Rec.sets = count;

            String rec_obj;

            SharedPreferences sharedPreferences = getSharedPreferences("RecFile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String exist = sharedPreferences.getString(today+workout, "");

            if(exist == ""){
                rec_obj =  gson.toJson(Rec, Rec.class);
            }
            else{
                Rec prevRec = gson.fromJson(exist, Rec.class);
                prevRec.sets += count;
                prevRec.volume += volume;

                rec_obj = gson.toJson(prevRec, Rec.class);
            }
            editor.putString(today+workout, rec_obj);
            editor.apply();
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt(SET_COUNT);
        set_cnt.setText(""+count);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SET_COUNT, count);
        super.onSaveInstanceState(outState);
    }



}

























































