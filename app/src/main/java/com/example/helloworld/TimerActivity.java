package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.icu.text.AlphabeticIndex;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class TimerActivity extends AppCompatActivity {
    static final String SET_COUNT = "SET_COUNT";

    Gson gson;

    Rec Rec;
    ArrayList<Rec> did_list;

    Integer weight = 0, volume = 0, count = 0;
    String workout;

    Handler handler;
    Button start;
    TextView rest_time, set_time, time, timer_status, set_num, set_cnt, vol_cnt, date, did_workout;
    EditText input_workout, input_weight;

    int user_def_rest, user_def_set, user_def_num, cur_num;
    boolean set_timer = true, rest_timer = true;//타이머 상태

    public static final int INIT = 0;//처음
    public static final int RUN = 1;//실행중
    public static final int PAUSE = 2;//일시정지

    public static int status = INIT;

    String today;


    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //SharedPreferences setting = getSharedPreferences("RecFile", MODE_PRIVATE);
        //setting.edit().clear().apply();

        gson = new GsonBuilder().create();
        Rec = new Rec();
        handler = new Handler();
        did_list = new ArrayList<>();

        start = findViewById(R.id.start_btn);

        time = findViewById(R.id.time);
        date = findViewById(R.id.date);
        rest_time = findViewById(R.id.rest_time);
        set_time = findViewById(R.id.set_time);
        set_num = findViewById(R.id.set_num);
        set_cnt = findViewById(R.id.set_cnt);
        vol_cnt = findViewById(R.id.vol_cnt);
        timer_status = findViewById(R.id.timer_status);
        did_workout = findViewById(R.id.did_workout);

        input_workout = findViewById(R.id.input_workout);
        input_weight = findViewById(R.id.input_weight);

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
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int value = reps.getValue();
                volume += weight * value;

                did_workout.setText(workout);
                set_cnt.setText((count+1) + " set");    // 휴식 시간에는 set가 카운팅 되기 전이기 때문에, 나타낼 떄는 1 더한값을 나타냄
                vol_cnt.setText(volume.toString() + " kg");
            }
        });
        AlertDialog alt = builder.create();
        alt.show();
    }


    public Runnable runnable = new Runnable() {
        @SuppressLint("SetTextI18n")
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
                @SuppressLint("DefaultLocale") String new_time = String.format("%02d", new_h) + ":" + String.format("%02d", new_m) + ":" + String.format("%02d", new_s);
                time.setText(new_time);

                if (new_s == 0 && new_m == 0 && new_h == 0) {
                    handler.removeCallbacks(this);
                    if (set_timer) set_timer = false;
                    else {//완전 싹 다 끝났을 때
                        set_timer = true;
                        cur_num++;
                        count++;
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

                            save_workout();

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

    @SuppressLint("SetTextI18n")
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

                did_workout.setText("None");
                set_cnt.setText("0 set");
                vol_cnt.setText("0 kg");

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

        @SuppressLint("DefaultLocale") String user_def_time = String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);

        time.setText(user_def_time);
    }
    private void rest_time_setter(){
        int h = (int)(user_def_rest / 3600);
        int m = (int)((user_def_rest - (3600*h)) / 60);
        int s = (int)((user_def_rest - (60*m) - (3600*h)));

        @SuppressLint("DefaultLocale") String user_def_time = String.format("%02d", h) + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);

        time.setText(user_def_time);
    }

    @SuppressLint("NonConstantResourceId")
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

    @SuppressLint("NonConstantResourceId")
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

    @SuppressLint("SetTextI18n")
    public void save_workout(){
        Rec.setVolume(volume);
        Rec.setDate(today);
        Rec.setWorkout(workout);
        Rec.setSets(count);

        String rec_obj;

        SharedPreferences recFileSharedPreference = getSharedPreferences("RecFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = recFileSharedPreference.edit();
        String keyStr = today + workout;


        if( !isExistCorrespondData(recFileSharedPreference, keyStr) ){
            rec_obj =  gson.toJson(Rec, Rec.class);

            did_workout.setText(Rec.getWorkout());
            set_cnt.setText(Rec.getSets().toString() + " set");
            vol_cnt.setText(Rec.getVolume().toString() + " kg");
        } else{
            String value = recFileSharedPreference.getString(keyStr, "");
            Rec prevRec = gson.fromJson(value, Rec.class);

            did_workout.setText(prevRec.getWorkout());
            set_cnt.setText(prevRec.getSets().toString() + "+" + count + " set");
            vol_cnt.setText(prevRec.getVolume().toString() + "+" + volume + " kg");

            prevRec.sets += count;
            prevRec.volume += volume;

            rec_obj = gson.toJson(prevRec, Rec.class);
        }
        editor.putString(keyStr, rec_obj);
        editor.apply();

        volume = 0;
        workout = "";
        count = 0;

        // 타이머 수행 완료 시점에 RecordActivity 메서드 호출
        ((RecordActivity)RecordActivity.context).initRecordLogData();
        ((RecordActivity)RecordActivity.context).initRecordPlanData();
        ((RecordActivity)RecordActivity.context).save_state();

        //RecordActivity 내 메서드 호출 후 SP가 정상적으로 처리 되었는지 체크
        checkSharedPreferenceStatus(keyStr,rec_obj);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(volume != 0 && !"".equals(workout) && count != 0) {
            save_workout();
        }
    }

    @SuppressLint("SetTextI18n")
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

    private void checkSharedPreferenceStatus(String keyStr, String rec_obj) {
        SharedPreferences recLogSharedPreference = getSharedPreferences("RecLog", MODE_PRIVATE);
        SharedPreferences recPlanSharedPreference = getSharedPreferences("RecPlan", MODE_PRIVATE);
        SharedPreferences.Editor planEditor = recPlanSharedPreference.edit();
        SharedPreferences.Editor logEditor = recLogSharedPreference.edit();

        if( isExistCorrespondData(recPlanSharedPreference, keyStr) ) {
            planEditor.clear();
            planEditor.apply();
        }

        if ( !isExistCorrespondData(recLogSharedPreference, keyStr)) {
            logEditor.putString(keyStr,rec_obj);
            logEditor.apply();
        }
    }

    private Boolean isExistCorrespondData(SharedPreferences sharedPreferences, String keyStr) {

        String value = sharedPreferences.getString(keyStr, "");
        Map<String, ?> totalValue = sharedPreferences.getAll();
        boolean result = false;

        if ((!"".equals(value) && value != null) && totalValue.containsKey(keyStr)) {
            result = true;
        }
        return result;
    }
}

























































