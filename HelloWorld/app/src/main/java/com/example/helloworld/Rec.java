package com.example.helloworld;

import java.util.ArrayList;

public class Rec implements Comparable<Rec>{
    String date;
    String workout;
    Integer volume;
    Integer sets;


    public Rec() {
        date = "";
        workout = "";
        volume = 0;
        sets = 0;
    }

    public String getDate(){
        return this.date;
    }
    public String getWorkout(){
        return this.workout;
    }
    public Integer getVolume(){
        return this.volume;
    }
    public Integer getSets(){
        return this.sets;
    }

    @Override
    public int compareTo(Rec rec) {
        return this.date.compareTo(rec.date);
    }
}
