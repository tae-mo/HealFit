package com.example.healfit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProgressActivity extends AppCompatActivity {
    Integer star = 0;

    int complete_plan_cnt = 0, plan_cnt = 0;
    double progress = 0;
    String[] nickname = {"헬린이", "헬스 보이/걸", "몸짱"};
    String[] msg = {"시작이 반입니다!", "노력하고 계시군요!", "절반정도 왔어요!", "좀만 더 힘내세요!", "목표 달성!"};

    SharedPreferences plan_cnt_sp, progress_sp, star_sp, modified;

    TextView message, star_cnt;
    LinearLayout level1, level2, level3, level4;
    ArrayList<LinearLayout> turtle_list;
    ImageView normal_star, shine_star;

    private String today;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Date d = new Date();
        today = dateFormat.format(d);

        message = findViewById(R.id.message);
        star_cnt = findViewById(R.id.star_cnt);

        level1 = findViewById(R.id.level1);
        level2 = findViewById(R.id.level2);
        level3 = findViewById(R.id.level3);
        level4 = findViewById(R.id.level4);

        turtle_list = new ArrayList<>();
        turtle_list.add(level1);
        turtle_list.add(level2);
        turtle_list.add(level3);
        turtle_list.add(level4);

        level1.setVisibility(View.INVISIBLE);
        level2.setVisibility(View.INVISIBLE);
        level3.setVisibility(View.INVISIBLE);
        level4.setVisibility(View.INVISIBLE);

        normal_star = findViewById(R.id.normal_star);
        shine_star = findViewById(R.id.shine_star);
        shine_star.setVisibility(View.INVISIBLE);

        //여기서 부터 shared를 짤 계획
        plan_cnt_sp = getSharedPreferences("RecPlanCnt",MODE_PRIVATE);
        progress_sp = getSharedPreferences("RecProgress", MODE_PRIVATE);
        star_sp = getSharedPreferences("RecStar",MODE_PRIVATE);
        modified = getSharedPreferences("Modified", MODE_PRIVATE);

        calculate_progress();   // progress 계산 & 저장
        get_star();

        set_message();
        set_nickname();

        star_cnt.setText("모은 별의 개수 : " + star+ " 개");
    }
    public void set_nickname(){
        //star 처리 부분
        for(LinearLayout layout : turtle_list){
            TextView name = (TextView)layout.getChildAt(0);

            if(star >= 0 && star < 11){
                name.setText(nickname[0]);
            }
            else if(star >= 11 && star < 31){
                name.setText(nickname[1]);
            }
            else{
                name.setText(nickname[2]);
            }
        }
    }

    public void set_message(){
        boolean already = modified.getBoolean(today, false);
        //character 부분
        if(progress < 100 && already){
            star--;
            star_sp.edit().putString("user", star.toString()).apply();
            modified.edit().putBoolean(today, false).apply();
        }

        if(progress >= 0 && progress < 25)
        {
            turtle_list.get(0).setVisibility(View.VISIBLE);
            message.setText(msg[0]);

        }
        else if(progress >= 25 && progress < 50)
        {
            turtle_list.get(1).setVisibility(View.VISIBLE);
            message.setText(msg[1]);
        }
        else if(progress >= 50 && progress < 75)
        {
            turtle_list.get(2).setVisibility(View.VISIBLE);
            message.setText(msg[2]);
        }
        else if(progress >= 75 && progress < 100)
        {
            turtle_list.get(3).setVisibility(View.VISIBLE);
            message.setText(msg[3]);
        }
        else if(progress >= 100)
        {
            if(!already) {
                star++;
                star_sp.edit().putString("user", star.toString()).apply();
                modified.edit().putBoolean(today, true).apply();
            }
            message.setText(msg[4]);
            shine_star.setVisibility(View.VISIBLE);
        }
    }

    public void calculate_progress(){
        String exist = plan_cnt_sp.getString(today,"");
        if(exist.equals("")){
            exist = "0/0";
        }

        String[] temp = exist.split("/");
        complete_plan_cnt = Integer.parseInt(temp[0]);
        plan_cnt = Integer.parseInt(temp[1]);

        if(complete_plan_cnt != 0){
            progress = ((double)complete_plan_cnt / (double)plan_cnt) * 100;
        }

        progress_sp.edit().putString(today, Double.toString(progress)).apply(); // progress 저장
    }

    public void get_star(){
        //star_sp.edit().clear().apply();
        //modified.edit().clear().apply();

        String exist = star_sp.getString("user", "");
        if(exist.equals("")){
            star_sp.edit().putString("user", "0").apply();
        }
        else{
            star = Integer.parseInt(exist); // 별 개수 가져옴
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
}
