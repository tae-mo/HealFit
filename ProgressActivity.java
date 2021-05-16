package com.example.helloworld;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

public class ProgressActivity extends AppCompatActivity {
    TextView textView;
    Integer star = 0;
    Integer length = 0;
    Gson gson;
    int complete_plan_cnt = 0, plan_cnt = 0;
    double today_goal;
    ArrayList<String> rec_list;
    //ArrayList<Rec> rec_list;
    String comment="";
    SharedPreferences temp_sp;
    TextView text1,text2,text3,text4,goal_text;
    ImageView Level_1,Level_2,Level_3,Level_4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        Level_1 = (ImageView)findViewById(R.id.Level1_char);
        Level_2 = (ImageView)findViewById(R.id.Level2_char);
        Level_3 = (ImageView)findViewById(R.id.Level3_char);
        Level_4 = (ImageView)findViewById(R.id.Level4_char);

        text1 = (TextView)findViewById(R.id.Level1_text);
        text2 = (TextView)findViewById(R.id.Level2_text);
        text3 = (TextView)findViewById(R.id.Level3_text);
        text4 = (TextView)findViewById(R.id.Level4_text);
        goal_text = (TextView)findViewById(R.id.goal_text);
        //여기서 부터 shared를 짤 계획

        gson = new GsonBuilder().create();
        temp_sp = getSharedPreferences("RecPlanCnt",MODE_PRIVATE);

        Map<String, ?> totalRec = temp_sp.getAll();
        rec_list = new ArrayList<String>();


        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            String day_rec = entry.getValue().toString();
            rec_list.add(day_rec);
        }
        length = rec_list.size()-1;
        String[] temp = rec_list.get(length).split("/");
        complete_plan_cnt = Integer.parseInt(temp[0]);
        plan_cnt = Integer.parseInt(temp[1]);
        if(plan_cnt == 0)
            today_goal = 0;
        else today_goal = ((double)complete_plan_cnt / (double)plan_cnt) * 100;
        //today_goal = rec_list.get(0).getProgression_state();


        //새롭게 짜야한다.
        /*
        gson = new GsonBuilder().create();
        temp_sp = getSharedPreferences("RecPlanCnt", MODE_PRIVATE);
        Map<String, ?> totalRec = temp_sp.getAll();

        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            String day_rec = gson.fromJson(entry.getValue().toString(), String.class);
            rec_list.add(day_rec);
        }

        length = rec_list.size()-1;
        String[] temp = rec_list.get(length).split("/");
        today_goal = Integer.parseInt(temp[0]) / Integer.parseInt(temp[1]);
        */

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
        else if(today_goal == 100)
        {
            goal_text.append("열심히 응원하던 거북이는 쉬러 갔습니다!");
        }

        //star를 기록하는 부분이 필요


    }
}