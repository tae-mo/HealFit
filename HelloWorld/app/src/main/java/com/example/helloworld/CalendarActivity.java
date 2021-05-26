package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    SharedPreferences sp;

    Integer save_weight = 0, save_progress = 0, save_kcal = 0;

    CalendarView calendar;
    TextView date, weight, kcal, progress;
    Button enter_weight;

    private String today;
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
        sp = getSharedPreferences("RecCal", MODE_PRIVATE);

        calendar = (CalendarView)findViewById(R.id.calendar);

        date = (TextView)findViewById(R.id.date);
        weight = (TextView)findViewById(R.id.weight);
        kcal = (TextView)findViewById(R.id.kcal);
        progress = (TextView)findViewById(R.id.progress);

        enter_weight = (Button)findViewById(R.id.enter_weight);

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
                String picked_date = String.format("%s/%s/%s", temp_year, temp_month, temp_day);
                if(!today.equals(picked_date)){
                    enter_weight.setEnabled(false);
                    enter_weight.setText("NOT TODAY");
                }
                else{
                    enter_weight.setEnabled(true);
                    enter_weight.setText("ENTER WEIGHT");

                    if(save_weight != 0){
                        weight.setText(save_weight.toString());
                    }
                }
                date.setText(picked_date);

                // Load saved data
                String exist = sp.getString(picked_date, "");
                if(!exist.equals("")){
                    weight.setText(exist+" kg");
                }
                else{
                    weight.setText("0 kg");
                }
            }
        });
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
                }
            }
        });
        AlertDialog alt = builder.create();
        alt.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(save_weight == 0){
            String exist = sp.getString(today, "");
            if(!exist.equals("")){
                save_weight = Integer.parseInt(exist);
            }
        }
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(today, save_weight.toString());
        editor.apply();
    }
}







































































