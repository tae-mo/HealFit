package com.taemo.healfit;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    Gson gson;

    ArrayList<Rec> rec_list;
    ArrayList<Rec> plan_list;
    ArrayList<Rec> log_list;
    ArrayList<String> over_ten_workout;

    SharedPreferences plan_sp, plan_cnt_sp, log_sp;

    TableLayout rec_table, plan_table;
    Button add_plan, show_log;
    TextView complete_cnt;

    ArrayList<TableRow> rec_row = new ArrayList<>();
    ArrayList<TableRow> plan_row = new ArrayList<>();
    ArrayList<TableRow> log_row = new ArrayList<>();

    ArrayList<String> col_format = new ArrayList<>(Arrays.asList("날짜", "운동", "세트", "볼륨"));

    int complete_plan_cnt = 0, plan_cnt = 0;

    private String today;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        add_plan = findViewById(R.id.add_plan);
        show_log = findViewById(R.id.show_log);
        complete_cnt = findViewById(R.id.complete_cnt);

        Date d = new Date();
        today = dateFormat.format(d);

        gson = new GsonBuilder().create();
        over_ten_workout = new ArrayList<>();
        /***********************************LOG 불러오기*************************************/
        load_log();

        /*********************************수행한 운동 불러오기***********************************/
        load_RecFile();

        /*********************************계획한 운동 불러오기***********************************/
        load_plan();
    }

    public void load_log(){
        log_list = new ArrayList<>();
        log_sp = getSharedPreferences("RecLog", MODE_PRIVATE);

        Map<String, ?>  totalRec = log_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            if(day_rec.getDate().equals(today)){
                log_list.add(day_rec);
            }
        }

        SharedPreferences removed_sp = getSharedPreferences("TimerRemoved", MODE_PRIVATE);
        totalRec = removed_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            if(day_rec.getDate().equals(today)){
                log_list.add(day_rec);
            }
        }
        removed_sp.edit().clear().apply();
        Collections.sort(log_list);
    }
    public void load_RecFile(){
        rec_list = new ArrayList<>();
        rec_table = (TableLayout)findViewById(R.id.rec_table);
        plan_sp = getSharedPreferences("RecFile", MODE_PRIVATE);

        Map<String, ?>  totalRec = plan_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            rec_list.add(day_rec);
        }
        Collections.sort(rec_list);

        modify_list(rec_list, rec_row, rec_table, 18);
    }
    public void load_plan(){
        plan_list = new ArrayList<>();
        plan_table = (TableLayout)findViewById(R.id.plan_table);
        plan_sp = getSharedPreferences("RecPlan", MODE_PRIVATE);
        plan_cnt_sp = getSharedPreferences("RecPlanCnt", MODE_PRIVATE);

        String exist = plan_cnt_sp.getString(today, "");
        SharedPreferences.Editor editor = plan_cnt_sp.edit();

        if(!exist.equals("")){
            String[] temp = exist.split("/");
            complete_plan_cnt = Integer.parseInt(temp[0]);
            plan_cnt = Integer.parseInt(temp[1]);
        }

        Map<String, ?> totalRec = plan_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_plan = gson.fromJson(entry.getValue().toString(), Rec.class);
            if(day_plan.getDate().equals(today)){
                plan_list.add(day_plan);
            }
        }

        modify_list(plan_list, plan_row, plan_table, 18);

        editor.putString(today, complete_plan_cnt+ "/" + plan_cnt);
        editor.apply();

        complete_cnt.setText("( " + complete_plan_cnt + " / " + plan_cnt + " )");
    }

    public boolean add_item(ArrayList<Rec> base, Rec compare){
        boolean already = false;
        for(Rec item : base){
            if(item.getWorkout().equals(compare.getWorkout())){
                already = true;
                item.setSets(item.getSets() + compare.getSets());
                item.setVolume(item.getVolume() + compare.getVolume());
            }
        }
        return already;
    }

    @SuppressLint("SetTextI18n")
    public void modify_list(ArrayList<Rec> arr_list, ArrayList<TableRow> arr_row, TableLayout table, int space){
        table.removeAllViews();
        arr_row.clear();

        TableRow tr = new TableRow(this);
        tr.setBackgroundResource(R.color.dark_blue);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        for(String s : col_format){
            TextView text = new TextView(this);
            text.setBackgroundResource(R.color.grey);
            text.setGravity(Gravity.CENTER);;
            text.setText(String.format("%-"+space+"s", s));
            tr.addView(text);
        }
        arr_row.add(tr);

        // ArrayList에 테이블값 추가
        for(int i = 0; i < arr_list.size(); i++) {
            tr = new TableRow(this);
            tr.setGravity(Gravity.CENTER);
            tr.setLayoutParams(new TableRow.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            TextView date, workout, sets, volume;
            date = new TextView(this);
            workout = new TextView(this);
            sets = new TextView(this);
            volume =  new TextView(this);

            workout.setOnClickListener(new View.OnClickListener() {     // WorkOut을 나타내는 TextView에만 클릭 리스너 등록
                @Override
                public void onClick(View v) {
                    textClick(v);       // 클릭 리스너는 textClick이라는 함수를 호출함
                }
            });

            date.setBackgroundResource(R.color.white);
            workout.setBackgroundResource(R.color.white);
            sets.setBackgroundResource(R.color.white);
            volume.setBackgroundResource(R.color.white);

            date.setText(String.format("%-"+space+"s", arr_list.get(i).getDate()));
            if(arr_list.get(i).getWorkout().length() <= 10){    // 문자열의 길이가 10 이하인 경우
                workout.setText(String.format("%-"+space+"s", arr_list.get(i).getWorkout()));
            }
            else{   // 문자열의 길이가 10을 초과하는 경우
                if(!over_ten_workout.contains(arr_list.get(i).getWorkout())){   // over_ten_workout이라는 ArrayList에 길이가 10 초과인 문자열을 넣음 (이미 존재하면 안넣음)
                    over_ten_workout.add(arr_list.get(i).getWorkout());
                }
                workout.setText(String.format("%-"+space+"s", arr_list.get(i).getWorkout()).substring(0, 7) + "...");   // 문자열의 인덱스 0~6까지 출력후, 뒤에 "..."를 붙임
            }
            sets.setText(String.format("%-"+space+"s", arr_list.get(i).getSets().toString() + " sets"));
            volume.setText(String.format("%-"+space+"s", arr_list.get(i).getVolume().toString() + " kg"));

            tr.addView(date);
            tr.addView(workout);
            tr.addView(sets);
            tr.addView(volume);

            arr_row.add(tr);
        }

        for(TableRow recorded : arr_row){
            table.addView(recorded);
        }
    }
    public void textClick(View v){      // textClick에서는 클릭된 TextView에 "..."이 포함된 경우, over_ten_workout에서 "..."부분이 제외된 부분을 포함하는 string을 찾아 토스트 출력
        TextView clicked_view = (TextView)v;
        String text = clicked_view.getText().toString();

        if(text.contains("...")){
            for(String entry : over_ten_workout){
                if(entry.contains(text.replace("...", ""))){
                    Toast show_text = Toast.makeText(this, entry, Toast.LENGTH_SHORT);
                    show_text.show();
                }
            }
        }
    }
    public void display_logs(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.log_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Workout Logs");
        builder.setView(dialogView);

        TableLayout log_table = dialogView.findViewById(R.id.log_table);
        log_table.setGravity(Gravity.CENTER);
        modify_list(log_list, log_row, log_table, 17);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void adding_plan(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.record_plan_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Enter Plan Info");
        builder.setView(dialogView);
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit_workout = (EditText)dialogView.findViewById(R.id.plan_workout);
                EditText edit_sets = (EditText)dialogView.findViewById(R.id.plan_sets);
                EditText edit_weight = (EditText)dialogView.findViewById(R.id.plan_weight);
                EditText edit_reps = (EditText)dialogView.findViewById(R.id.plan_reps);

                if(!edit_workout.getText().toString().equals("") && !edit_sets.getText().toString().equals("0") && !edit_weight.getText().toString().equals("0") && !edit_reps.getText().toString().equals("0")){
                    String input_workout = edit_workout.getText().toString();
                    Integer input_sets = Integer.parseInt(edit_sets.getText().toString());
                    Integer input_weight = Integer.parseInt(edit_weight.getText().toString());
                    Integer input_reps = Integer.parseInt(edit_reps.getText().toString());

                    Rec input_rec = new Rec(today, input_workout, input_weight*input_reps*input_sets, input_sets);

                    if(!add_item(plan_list, input_rec)){
                        plan_list.add(input_rec);

                        SharedPreferences.Editor editor = plan_cnt_sp.edit();
                        plan_cnt++;
                        complete_cnt.setText("( " + complete_plan_cnt + " / " + plan_cnt + " )");
                        editor.putString(today, complete_plan_cnt+ "/" + plan_cnt);
                        editor.apply();
                    }
                    modify_list(plan_list, plan_row, plan_table, 18);
                    save_plan();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void save_plan(){
        plan_sp.edit().clear().apply();
        SharedPreferences.Editor editor_plan = plan_sp.edit();

        for(Rec plan_item : plan_list){
            editor_plan.putString(today+plan_item.getWorkout(), gson.toJson(plan_item, Rec.class));
        }
        editor_plan.apply();
    }
    public void save_log(){
        log_sp.edit().clear().apply();
        SharedPreferences.Editor editor_log = log_sp.edit();

        for(Rec log_item : log_list){
            editor_log.putString(today+log_item.getWorkout(), gson.toJson(log_item, Rec.class));
        }
        editor_log.apply();
    }
    public void save_state(){
        save_plan();
        save_log();
    }
    @Override
    protected void onStop() {
        super.onStop();
        save_state();
    }

}
