package com.example.helloworld;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class RecordActivity extends AppCompatActivity {
    Gson gson;
    String rec_obj;
    ArrayList<Rec> rec_list;
    SharedPreferences sp;

    TableLayout tableLayout;
    Button record;
    ArrayList<TableRow> rec_row = new ArrayList<TableRow>();
    ArrayList<String> col_format = new ArrayList<>(Arrays.asList("날짜", "운동", "세트", "볼륨"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        gson = new GsonBuilder().create();
        sp = getSharedPreferences("RecFile", MODE_PRIVATE);

        rec_list = new ArrayList<Rec>();
        Map<String, ?> totalRec = sp.getAll();
        for(Map.Entry<String, ?> entry : totalRec.entrySet()){
            Rec day_rec = gson.fromJson(entry.getValue().toString(), Rec.class);
            rec_list.add(day_rec);
        }
        Collections.sort(rec_list);

        tableLayout = (TableLayout)findViewById(R.id.rec_table);
        record = (Button)findViewById(R.id.rec_btn);

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
        rec_row.add(tr);

        // ArrayList에 테이블값 추가 --> sharedPreferencesInstance.getAll().size()를 100대신에 넣어야댐
        for(int i = 0; i < rec_list.size(); i++) {
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

            date.setText(String.format("%-20s", rec_list.get(i).getDate()));
            workout.setText(String.format("%-20s", rec_list.get(i).getWorkout()));
            sets.setText(String.format("%-20s", rec_list.get(i).getSets().toString() + " sets"));
            volume.setText(String.format("%-20s", rec_list.get(i).getVolume().toString() + " kg"));

            tr.addView(date);
            tr.addView(workout);
            tr.addView(sets);
            tr.addView(volume);

            rec_row.add(tr);
        }

        for(TableRow recorded : rec_row){
            tableLayout.addView(recorded);
        }
    }
}





































