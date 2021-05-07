package com.example.activity_progression;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Integer star = 0,today_goal= 75;
    String comment="";


    TextView text1,text2,text3,text4,goal_text;
    ImageView Level_1,Level_2,Level_3,Level_4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Level_1 = (ImageView)findViewById(R.id.Level1_char);
        Level_2 = (ImageView)findViewById(R.id.Level2_char);
        Level_3 = (ImageView)findViewById(R.id.Level3_char);
        Level_4 = (ImageView)findViewById(R.id.Level4_char);

        text1 = (TextView)findViewById(R.id.Level1_text);
        text2 = (TextView)findViewById(R.id.Level2_text);
        text3 = (TextView)findViewById(R.id.Level3_text);
        text4 = (TextView)findViewById(R.id.Level4_text);
        goal_text = (TextView)findViewById(R.id.goal_text);


        if(today_goal == 0)
        {
            Level_1.setVisibility(View.VISIBLE);
            text1.setVisibility(View.VISIBLE);
            goal_text.append("시작이 반입니다!");
        }
        if(today_goal == 25)
        {
            Level_2.setVisibility(View.VISIBLE);
            text2.setVisibility(View.VISIBLE);
            goal_text.append("열심히 하고 계시군요 힘내세!");
        }
        if(today_goal == 50)
        {
            Level_3.setVisibility(View.VISIBLE);
            text3.setVisibility(View.VISIBLE);
            goal_text.append("목표의 절반정도 도달했습니다.!");
        }
        if(today_goal == 75)
        {
            Level_4.setVisibility(View.VISIBLE);
            text4.setVisibility(View.VISIBLE);
            goal_text.append("최종 목표까지 얼마 안남았습니다!");
        }

    }
}