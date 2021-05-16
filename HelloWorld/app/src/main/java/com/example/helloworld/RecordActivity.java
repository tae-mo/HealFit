package com.example.helloworld;

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

        /***********************************LOG 불러오기*************************************/
        log_list = new ArrayList<>();
        log_sp = getSharedPreferences("RecLog", MODE_PRIVATE);
        //log_sp.edit().clear().apply();

        Map<String, ?>  totalRec = log_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            if(day_rec.getDate().equals(today)){
                log_list.add(day_rec);
            }
        }
        Collections.sort(log_list);

        /*********************************수행한 운동 불러오기***********************************/
        rec_list = new ArrayList<>();
        rec_table = (TableLayout)findViewById(R.id.rec_table);
        plan_sp = getSharedPreferences("RecFile", MODE_PRIVATE);

        totalRec = plan_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            rec_list.add(day_rec);
        }
        Collections.sort(rec_list);

        modify_list(rec_list, rec_row, rec_table, 20);

        /*********************************계획한 운동 불러오기***********************************/
        plan_list = new ArrayList<>();
        plan_table = (TableLayout)findViewById(R.id.plan_table);
        plan_sp = getSharedPreferences("RecPlan", MODE_PRIVATE);
        plan_cnt_sp = getSharedPreferences("RecPlanCnt", MODE_PRIVATE);

        //plan_sp.edit().clear().apply();
        //plan_cnt_sp.edit().clear().apply();

        String exist = plan_cnt_sp.getString(today, "");
        SharedPreferences.Editor editor = plan_cnt_sp.edit();

        if(!exist.equals("")){
            String[] temp = exist.split("/");
            complete_plan_cnt = Integer.parseInt(temp[0]);
            plan_cnt = Integer.parseInt(temp[1]);
        }

        totalRec = plan_sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_plan = gson.fromJson(entry.getValue().toString(), Rec.class);
            if(day_plan.getDate().equals(today)){
                plan_list.add(day_plan);
            }
        }

        ArrayList<Rec> remove_items = new ArrayList<>();
        for(Rec rec_item : rec_list){
            if(rec_item.getDate().equals(today)){
                for(Rec plan_item : plan_list){
                    if(rec_item.getWorkout().equals(plan_item.getWorkout())){
                        if(rec_item.getVolume() >= plan_item.getVolume()){
                            remove_items.add(plan_item);

                            if(!add_item(log_list, plan_item)){
                                log_list.add(plan_item);
                            }
                            complete_plan_cnt++;
                        }
                    }
                }
            }
        }
        for(int i = 0; i < remove_items.size(); i++){
            plan_list.remove(remove_items.get(i));
        }
        if(!remove_items.isEmpty()){
            Toast plan_removed = Toast.makeText(this.getApplicationContext(), remove_items.size() +"개의 완료된 Plan이 제거됩니다.", Toast.LENGTH_SHORT);
            plan_removed.show();
        }
        modify_list(plan_list, plan_row, plan_table, 20);

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

    public void modify_list(ArrayList<Rec> arr_list, ArrayList<TableRow> arr_row, TableLayout table, int space){
        table.removeAllViews();
        arr_row.clear();

        TableRow tr = new TableRow(this);
        tr.setGravity(Gravity.CENTER);
        tr.setLayoutParams(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        for(String s : col_format){
            TextView text = new TextView(this);
            text.setText(String.format("%-20s", s));
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

            date.setText(String.format("%-"+space+"s", arr_list.get(i).getDate()));
            workout.setText(String.format("%-"+space+"s", arr_list.get(i).getWorkout()));
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
    public void display_logs(View view){
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.log_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Workout Logs");
        builder.setView(dialogView);
        TableLayout log_table = dialogView.findViewById(R.id.log_table);
        modify_list(log_list, log_row, log_table, 10);
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
                    modify_list(plan_list, plan_row, plan_table, 20);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void save_state(){
        plan_sp.edit().clear().apply();
        SharedPreferences.Editor editor_plan = plan_sp.edit();

        for(Rec plan_item : plan_list){
            editor_plan.putString(today+plan_item.getWorkout(), gson.toJson(plan_item, Rec.class));
        }
        editor_plan.apply();

        log_sp.edit().clear().apply();
        SharedPreferences.Editor editor_log = log_sp.edit();

        for(Rec log_item : log_list){
            editor_log.putString(today+log_item.getWorkout(), gson.toJson(log_item, Rec.class));
        }
        editor_log.apply();
    }
    @Override
    protected void onStop() {
        super.onStop();
        save_state();
    }

}
























































