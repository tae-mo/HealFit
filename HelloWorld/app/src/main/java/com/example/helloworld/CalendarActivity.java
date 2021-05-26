package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    final Context context = this;

    Gson gson;
    SharedPreferences cal_sp, prog_sp, kcal_sp;

    Integer save_weight = 0, picked_weight = 0;
    double save_kcal = 0;


    CalendarView calendar;
    TextView date, weight, kcal, progress;
    Button enter_weight;

    private String today;
    private String picked_date;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Date d = new Date();
        today = dateFormat.format(d);

        gson = new GsonBuilder().create();

        cal_sp = getSharedPreferences("RecCal", MODE_PRIVATE);
        prog_sp = getSharedPreferences("RecProgress", MODE_PRIVATE);
        kcal_sp = getSharedPreferences("RecKcal", MODE_PRIVATE);

        calendar = findViewById(R.id.calendar);

        date = findViewById(R.id.date);
        weight = findViewById(R.id.weight);
        kcal = findViewById(R.id.kcal);
        progress = findViewById(R.id.progress);

        enter_weight = findViewById(R.id.enter_weight);

        calculate_progress();

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Setting Date Text
                String temp_month = ""+(++month);
                String temp_year = ""+year;
                String temp_day = ""+dayOfMonth;
                if(month < 10){
                    temp_month = "0"+temp_month;
                }
                if(dayOfMonth < 10){
                    temp_day = "0"+temp_day;
                }
                picked_date = String.format("%s/%s/%s", temp_year, temp_month, temp_day);
                if(!today.equals(picked_date)){
                    enter_weight.setEnabled(false);
                    enter_weight.setText("NOT TODAY");
                }
                else{
                    enter_weight.setEnabled(true);
                    enter_weight.setText("ENTER WEIGHT");
                }

                date.setText("날짜 : " + picked_date);

                // Load saved body weight
                String exist = cal_sp.getString(picked_date, "");
                if(!exist.equals("")){
                    picked_weight = Integer.parseInt(exist);
                    weight.setText(exist + " kg");
                }
                else{
                    weight.setText("0 kg");
                }

                // Load saved progress
                exist = prog_sp.getString(picked_date, "");
                if(!exist.equals("")){
                    double picked_prog = Double.parseDouble(exist);
                    progress.setText(String.format("%.1f", picked_prog) + " %");
                }
                else{
                    progress.setText("0 %");
                }

                // Load saved kcal
                exist = kcal_sp.getString(picked_date, "");
                if(!exist.equals("") && !exist.equals("0.0") && !picked_date.equals(today)){
                    kcal.setText(String.format("%.1f", Double.parseDouble(exist)) + " kcal");
                }
                else{
                    if(picked_date.equals(today)){ // 없는데 오늘이다? --> 계산 해야됨
                        int total_set = 0;
                        SharedPreferences rec_sp = getSharedPreferences("RecFile", MODE_PRIVATE);

                        Map<String, ?> totalRec = rec_sp.getAll();
                        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
                            Rec rec = gson.fromJson(entry.getValue().toString(), Rec.class);
                            if(rec.getDate().equals(today)){
                                total_set += rec.getSets();
                            }
                        }

                        if(picked_weight == 0){ // 오늘 체중 입력 안한 경우
                            Toast no_weight = Toast.makeText(context, "체중값이 없으면 칼로리를 계산할 수 없습니다", Toast.LENGTH_SHORT);
                            no_weight.show();
                        }
                        else{   // 체중 입력 돼있는 경우
                            save_kcal = calc_kcal(picked_weight, total_set);
                            kcal.setText(String.format("%.1f", save_kcal) + " kcal");
                        }
                    }
                    else{
                        kcal.setText("0 kcal");
                    }
                }
            }
        });
    }
    private double calc_kcal(int weight, int sets){
        int METs = 6;
        return METs * 3.5 * weight * 0.005 * (sets * 4);
    }

    public void set_weight(View view){
        EditText input_weight = new EditText(this);
        input_weight.setInputType(InputType.TYPE_CLASS_NUMBER);
        input_weight.setGravity(Gravity.CENTER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("오늘의 몸무게");
        builder.setView(input_weight);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editable value = input_weight.getText();
                if(!value.toString().equals("")) {
                    save_weight = Integer.parseInt(value.toString());
                    weight.setText(save_weight + " kg");
                    weight_saver();
                }
            }
        });
        AlertDialog alt = builder.create();
        alt.show();
    }

    public void weight_saver(){
        if(save_weight == 0){
            String exist = cal_sp.getString(today, "");
            if(!exist.equals("")){
                save_weight = Integer.parseInt(exist);
            }
        }
        cal_sp.edit().putString(today, save_weight.toString()).apply();
    }
    public void kcal_saver(){
        if(save_kcal == 0){
            String exist = kcal_sp.getString(today, "");
            if(!exist.equals("")){
                save_kcal = Double.parseDouble(exist);
            }
        }
        kcal_sp.edit().putString(today, ((Double)save_kcal).toString()).apply();
    }
    public void calculate_progress(){
        SharedPreferences plan_cnt_sp = getSharedPreferences("RecPlanCnt", MODE_PRIVATE);
        SharedPreferences progress_sp = getSharedPreferences("RecProgress", MODE_PRIVATE);
        String exist = plan_cnt_sp.getString(today,"");
        if(exist.equals("")){
            exist = "0/0";
        }

        String[] temp = exist.split("/");
        int complete_plan_cnt = Integer.parseInt(temp[0]);
        int plan_cnt = Integer.parseInt(temp[1]);
        double temp_prog = 0;

        if(complete_plan_cnt != 0){
            temp_prog = ((double)complete_plan_cnt / (double)plan_cnt) * 100;
        }

        progress_sp.edit().putString(today, Double.toString(temp_prog)).apply(); // progress 저장
    }
    @Override
    protected void onStop() {
        super.onStop();
        weight_saver();
        kcal_saver();
    }
}





































































