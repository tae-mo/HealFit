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
    public Rec(String d, String w, Integer v, Integer s) {
        date = d;
        workout = w;
        volume = v;
        sets = s;
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

    public void setDate(String nDate){
        this.date = nDate;
    }
    public void setWorkout(String nWorkout){
        this.workout = nWorkout;
    }
    public void setVolume(Integer nVol){
        this.volume = nVol;
    }
    public void setSets(Integer nSets){
        this.sets = nSets;
    }

    @Override
    public int compareTo(Rec rec) {
        return this.date.compareTo(rec.date);
    }
}
