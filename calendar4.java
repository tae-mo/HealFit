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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {
    SharedPreferences sp;

    Integer save_weight = 0, save_progress = 0, save_kcal = 0;

    CalendarView calendar;
    TextView date, weight, kcal, progress;
    Button enter_weight;

    private static String today = getToday();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


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

                int[] weight_kacal_arr = checkDataExist(picked_date);

                if (weight_kacal_arr[0]==-1){
                    weight.setText("0 kg");
                }else {
                    weight.setText(weight_kacal_arr[0]+"kg");
                }

                if (weight_kacal_arr[1]==-1){
                    kcal.setText("0 kcal");
                }else{
                    kcal.setText(weight_kacal_arr[1]+"kcal");
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
                    Double volume = getVolume(save_weight);
                    int kk = getKcal(save_weight);
                    kcal.setText(kk+"kcal");
                    saveData(save_weight, kk);
                }
            }
        });
        AlertDialog alt = builder.create();
        alt.show();
    }

    private static String getToday(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        return simpleDate.format(cal.getTime());
    }

    private static Double getVolume(int volume){
        /*
         *
         * logic here
         * 구현 getKcal() 함수와 비슷하게 volume의 입력값에 따라 범위 지정 및 인덱스를 잡기.
         * */
        return 0.0;
    }

    private void saveData(int weight, int kcal){
        SharedPreferences sharedPreferences = getSharedPreferences("weightFile", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String weight_key = today+"_weight";
        String kcal_key = today+"_kcal";
        editor.putInt(weight_key, weight);
        editor.putInt(kcal_key, kcal);
        editor.apply();
    }

    private static int getKcal(int weight){
        if((weight/10)<=12){
            return kcalArr[(weight/10)-2];
        }else {
            return kcalArr[kcalArr.length-1];
        }
    }

    private int[] checkDataExist(String date){
        SharedPreferences sp = getSharedPreferences("weightFile", MODE_PRIVATE);
        String weight_key = date+"_weight";
        String kcal_key = date+"_kcal";
        int w = sp.getInt(weight_key,-1);
        int k = sp.getInt(kcal_key,-1);
        return new int[]{w,k};
    }

    static Double[] volumeArr = {0.46, 0.87, 1.28, 1.69, 2.1, 2.51, 2.92, 3.33, 3.74, 4.15, 4.56, 4.97, 5.38, 5.79, 6.2, 6.61, 7.02, 7.43, 7.84, 8.25,
            8.66, 9.07, 9.48, 9.89, 10.3, 10.71, 11.12, 11.53, 11.94, 12.35, 12.76, 13.17, 13.58, 13.99, 14.4, 14.81, 15.22, 15.63, 16.04, 16.45,
    };

    static int[] kcalArr = {13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 53};
}