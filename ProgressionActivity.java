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
    Integer star = 0,today_goal= 0;
    Integer length = 0;
    Gson gson;
    ArrayList<Rec> rec_list;
    String comment="";

    SharedPreferences sp;
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
        sp = getSharedPreferences("RecFile",MODE_PRIVATE);
        //계산이 안된상태라면 가져와서 계산을 한뒤 진행

        //만약 계산이 되었다면 그냥 이렇게 가져온다.
        //today_goal = sp.getInt("progression_state",0);
        //textView.setText(today_goal);


        Map<String, ?> totalRec = sp.getAll();
        rec_list = new ArrayList<Rec>();


        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            rec_list.add(day_rec);
        }
        length = rec_list.size()-1;
        today_goal = rec_list.get(0).getProgression_state();


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
    }
}
