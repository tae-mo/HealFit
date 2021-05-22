package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {
    TextView textView;
    Integer star = 0;
    Integer length = 0;
    Gson gson;
    int complete_plan_cnt = 0, plan_cnt = 0;
    double today_goal;
    ArrayList <String> rec_list;
    private String today;
    private String yesterday;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    String comment="";
    SharedPreferences temp_sp;
    SharedPreferences progress_sp;
    SharedPreferences star_sp;
    TextView text1,text2,text3,text4,goal_text;
    TextView starcnt;
    ImageView Level_1,Level_2,Level_3,Level_4;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        Date d = new Date();
        Date dDate = new Date();
        dDate = new Date(dDate.getTime()+(1000*60*60*24*-1));

        yesterday = dateFormat.format(dDate);
        today = dateFormat.format(d);
        Level_1 = (ImageView)findViewById(R.id.Level1_char);
        Level_2 = (ImageView)findViewById(R.id.Level2_char);
        Level_3 = (ImageView)findViewById(R.id.Level3_char);
        Level_4 = (ImageView)findViewById(R.id.Level4_char);

        text1 = (TextView)findViewById(R.id.Level1_text);
        text2 = (TextView)findViewById(R.id.Level2_text);
        text3 = (TextView)findViewById(R.id.Level3_text);
        text4 = (TextView)findViewById(R.id.Level4_text);
        goal_text = (TextView)findViewById(R.id.goal_text);
        starcnt = (TextView) findViewById(R.id.star_cnt);
        //여기서 부터 shared를 짤 계획

        gson = new GsonBuilder().create();
        temp_sp = getSharedPreferences("RecPlanCnt",MODE_PRIVATE);
        progress_sp = getSharedPreferences("RecProgress", MODE_PRIVATE);
        star_sp = getSharedPreferences("RecStar",MODE_PRIVATE);
        String totalRec = temp_sp.getString(today,"");
        SharedPreferences.Editor editor_star = star_sp.edit();
        SharedPreferences.Editor editor = progress_sp.edit();

        if(totalRec.equals("")){
            totalRec="0/0";
        }
        String[] temp = totalRec.split("/");
        complete_plan_cnt = Integer.parseInt(temp[0]);
        plan_cnt = Integer.parseInt(temp[1]);
        if(plan_cnt == 0)
            today_goal = 0;
        else today_goal = ((double)complete_plan_cnt / (double)plan_cnt) * 100;
        String temp_today_goal = Double.toString(today_goal);
        editor.putString(today, temp_today_goal);
        editor.apply();
        //String test = progress_sp.getString(today,"");
        Map<String, ?> star_list = star_sp.getAll();
        if(star_list.size() < 2)
        {
            String temp_star = star_sp.getString(today,"");
            if(temp_star.equals("") && today_goal >= 100.0){
                star = 0;
                star++;
                temp_star = Integer.toString(star);
                editor_star.putString(today,temp_star);
                editor_star.apply();
            }
            else if(!temp_star.equals("") && today_goal >= 100){
                //string to int 필요
                star = Integer.parseInt(temp_star);
                star = 0;
                star++;
                temp_star = Integer.toString(star);
                editor_star.putString(today,temp_star);
                editor_star.apply();
            }
            else{
                star = 0;
                temp_star = Integer.toString(star);
                editor_star.putString(today,temp_star);
                editor_star.apply();
            }
        }
        else{
            if(today_goal >= 100)
            {
                String temp_star = star_sp.getString(yesterday,"");
                star = Integer.parseInt(temp_star);
                star++;
                temp_star = Integer.toString(star);
                editor_star.putString(today,temp_star);
                editor_star.apply();
            }
            else{
                String temp_star = star_sp.getString(yesterday,"");
                star = Integer.parseInt(temp_star);
                editor_star.putString(today,temp_star);
                editor_star.apply();
            }
        }


        //star 처리 부분
        if(star >= 0)
        {
            text1.append("헬린이");
            text2.append("헬린이");
            text3.append("헬린이");
            text4.append("헬린이");
        }
        if(star >= 11 && star <= 30)
        {
            text1.append("헬스 보이/걸");
            text2.append("헬스 보이/걸");
            text3.append("헬스 보이/걸");
            text4.append("헬스 보이/걸");
        }

        if(star >= 31 && star <= 50)
        {
            text1.append("몸짱");
            text2.append("몸짱");
            text3.append("몸짱");
            text4.append("몸짱");
        }

        //character 부분
        if(today_goal == 0)
        {
            Level_1.setVisibility(View.VISIBLE);
            text1.setVisibility(View.VISIBLE);
            goal_text.append("시작이 반입니다!");
        }
        else if(today_goal >= 25 && today_goal <= 49)
        {
            Level_2.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            goal_text.append("열심히 하고 계시군요 힘내세요!");
        }
        else if(today_goal >= 50 && today_goal <= 74)
        {
            Level_3.setVisibility(View.VISIBLE);
            text3.setVisibility(View.VISIBLE);
            goal_text.append("목표의 절반정도 도달했습니다.!");
        }
        else if(today_goal >= 75 && today_goal <= 99)
        {
            Level_4.setVisibility(View.VISIBLE);
            text4.setVisibility(View.VISIBLE);
            goal_text.append("최종 목표까지 얼마 안남았습니다!");
        }
        else if(today_goal >= 100)
        {
            goal_text.append("목표에 달성했습니다!⭐️");
        }

        //star를 기록하는 부분이
        //현재 문제점 필요한건 별이 create 될때 증가하기때문에
        //리스트의 사이즈가 하나이면 해결하는건 쉬운데
        //리스트가 2개일 경우 사이즈를 가져와서 정렬을 한뒤 전에꺼를 가져와야한다.
        //1.리스트의 개수 확인 1개일 경우 star = 0 거기서++
        //2. 2개이상일 경우 star = 가장최신의날 전에날 개수 가져오기 거기서 ++ -> 저장


        //1. 시작 아무런 기록이 없다 오늘 + 별의 개수 기록
        //2. 전날의 기록에서 현재 today goal이 100이다. 별의 개수를 더한다.
        starcnt.append(star+ "개");
    }
}
