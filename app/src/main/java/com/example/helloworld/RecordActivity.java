package com.example.helloworld;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.TelephonyNetworkSpecifier;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class RecordActivity extends AppCompatActivity {
    Gson gson;
    ArrayList<Rec> rec_list;
    ArrayList<Rec> plan_list;
    ArrayList<Rec> log_list;
    SharedPreferences sp;

    TableLayout rec_table, plan_table, log_table;
    Button add_plan;
    Button get_logs;
    LinearLayout listView;

    TableRow tableRow;

    ArrayList<TableRow> rec_row = new ArrayList<>();
    ArrayList<TableRow> plan_row = new ArrayList<>();
    ArrayList<TableRow> log_row = new ArrayList<>();

    ArrayList<String> col_format = new ArrayList<>(Arrays.asList("날짜", "운동", "세트", "볼륨"));

    private String today;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        add_plan = (Button)findViewById(R.id.add_plan);
        get_logs = (Button)findViewById(R.id.get_logs);
        listView = findViewById(R.id.listView);

        Date d = new Date();
        today = dateFormat.format(d);

        gson = new GsonBuilder().create();

        /*********************************수행한 운동 불러오기***********************************/

        rec_list = new ArrayList<>();
        rec_table = (TableLayout)findViewById(R.id.rec_table);
        sp = getSharedPreferences("RecFile", MODE_PRIVATE);

        Map<String, ?>  totalRec = sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            rec_list.add(day_rec);
        }
        Collections.sort(rec_list);

        modify_list(rec_list, rec_row, rec_table);

        /*********************************계획한 운동 불러오기***********************************/
        plan_list = new ArrayList<>();
        plan_table = (TableLayout)findViewById(R.id.plan_table);
        sp = getSharedPreferences("RecPlan", MODE_PRIVATE);

        totalRec = sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_plan = gson.fromJson(entry.getValue().toString(), Rec.class);
            plan_list.add(day_plan);
        }

        int remove_cnt = 0;
        for(Rec rec_item : rec_list){
            if(rec_item.getDate().equals(today)){
                for(Rec plan_item : plan_list){
                    if(rec_item.getWorkout().equals(plan_item.getWorkout())){
                        if(rec_item.getVolume() >= plan_item.getVolume()){
                            plan_list.remove(plan_item);
                            remove_cnt++;
                        }
                    }
                }
            }
        }
        if(remove_cnt > 0){
            Toast plan_removed = Toast.makeText(this.getApplicationContext(), remove_cnt+"개의 완료된 Plan이 제거됩니다.", Toast.LENGTH_SHORT);
            plan_removed.show();
        }

        modify_list(plan_list, plan_row, plan_table);
    }

    public void adding_plan(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.record_plan_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("운동 계획 추가");
        builder.setView(dialogView);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText edit_workout = (EditText)dialogView.findViewById(R.id.plan_workout);
                EditText edit_sets = (EditText)dialogView.findViewById(R.id.plan_sets);
                EditText edit_weight = (EditText)dialogView.findViewById(R.id.plan_weight);
                EditText edit_reps = (EditText)dialogView.findViewById(R.id.plan_reps);

                String editWorkoutStr = edit_workout.getText().toString();
                String editSetsStr = edit_sets.getText().toString();
                String editWeightStr = edit_weight.getText().toString();
                String editRepsStr = edit_reps.getText().toString();

                if(isEditValExist(editWorkoutStr, editSetsStr, editWeightStr, editRepsStr)){
                    String input_workout = editWorkoutStr;
                    Integer input_sets = Integer.parseInt(editSetsStr);
                    Integer input_weight = Integer.parseInt(editWeightStr);
                    Integer input_reps = Integer.parseInt(editRepsStr);

                    Rec input_rec = new Rec(today, input_workout, input_weight*input_reps*input_sets, input_sets);

                    boolean already = false;
                    for(Rec plan_item : plan_list){
                        if(plan_item.getWorkout().equals(input_rec.getWorkout())){
                            already = true;
                            if(plan_item.getVolume() < input_rec.getVolume()){
                                plan_item.setVolume(input_rec.getVolume());
                            }
                        }
                    }
                    if(!already){
                        plan_list.add(input_rec);
                    }
                    modify_list(plan_list, plan_row, plan_table);
                }
            }
            private boolean isEditValExist(String editWorkoutStr, String editSetsStr, String editWeightStr, String editRepsStr) {
                return !"".equals(editWorkoutStr) && !"0".equals(editSetsStr) && !"0".equals(editWeightStr) && !"0".equals(editRepsStr);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void get_logs(View view) {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.record_logs, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        log_list = new ArrayList<>();

        builder.setTitle("내 운동기록");

        sp = getSharedPreferences("RecLog", MODE_PRIVATE);

        Map<String, ?>  logsData = sp.getAll();

        Set<? extends Map.Entry<String, ?>> allLogs = logsData.entrySet();

        if (log_list != null) {
            for (Map.Entry<String, ?> log : allLogs) {
                String logValStr = log.getValue().toString();
                Rec log_value = gson.fromJson(logValStr, Rec.class);
                log_list.add(log_value);
            }
        }

        log_table = (TableLayout)dialogView.findViewById(R.id.log_table);

        if (dialogView.getParent() != null ){
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }

        builder.setView(dialogView);

        modify_list(log_list, log_row, log_table);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                log_table.removeAllViews();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void modify_list(ArrayList<Rec> arr_list, ArrayList<TableRow> arr_row, TableLayout table){
        if (table != null) {
            table.removeAllViews();
        }
        arr_row.clear();
        tableRow = setTableRowLayout();

        if (arr_list == log_list && ( log_list == null || log_list.size() == 0 )){
            log_table.setGravity(Gravity.CENTER);
            TableRow tableRow = setTableRowLayout();
            TextView emptyMessage;
            emptyMessage = new TextView(this);
            emptyMessage.setText("로그 데이터가 존재하지 않습니다.");
            tableRow.setGravity(Gravity.CENTER);
            tableRow.addView(emptyMessage);
            arr_row.add(tableRow);

            for(TableRow recorded : arr_row) {
                table.addView(recorded);
            }
            return;
        }

        for(String s : col_format){
            TextView text = new TextView(this);
            text.setText(String.format("%-20s", s));
            tableRow.addView(text);
        }
        arr_row.add(tableRow);
        // ArrayList에 테이블값 추가
        for (int i = 0; i < arr_list.size(); i++) {
            tableRow = setTableRowLayout();

            TextView date, workout, sets, volume;
            date = new TextView(this);
            workout = new TextView(this);
            sets = new TextView(this);
            volume = new TextView(this);

            date.setText(String.format("%-20s", arr_list.get(i).getDate()));
            workout.setText(String.format("%-20s", arr_list.get(i).getWorkout()));
            sets.setText(String.format("%-20s", arr_list.get(i).getSets().toString() + " sets"));
            volume.setText(String.format("%-20s", arr_list.get(i).getVolume().toString() + " kg"));

            tableRow.addView(date);
            tableRow.addView(workout);
            tableRow.addView(sets);
            tableRow.addView(volume);

            arr_row.add(tableRow);
        }


        for(TableRow recorded : arr_row){
            table.addView(recorded);
        }
    }

    private TableRow setTableRowLayout() {
        tableRow = new TableRow(this);
        tableRow.setGravity(Gravity.CENTER);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return tableRow;
    }

    public void save_state(){
        sp.edit().clear().apply();
        SharedPreferences.Editor editor = sp.edit();

        for(Rec plan_item : plan_list){
            editor.putString(today+plan_item.getWorkout(), gson.toJson(plan_item, Rec.class));
        }
        editor.apply();
    }
    @Override
    protected void onStop() {
        super.onStop();
        save_state();
    }

}
























































