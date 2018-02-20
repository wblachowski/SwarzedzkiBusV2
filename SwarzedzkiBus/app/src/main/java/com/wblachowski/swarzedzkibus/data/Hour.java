package com.wblachowski.swarzedzkibus.data;

import java.util.ArrayList;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class Hour {
    int hour;
    ArrayList<Integer> minutes;
    ArrayList<String> remarks;

    public Hour(int hour, ArrayList<Integer>minutes, ArrayList<String> remarks){
        this.hour=hour;
        this.minutes=minutes;
        this.remarks=remarks;
    }

    public void addToMinutes(int minute){
        minutes.add(minute);
    }

    public void addToRemarks(String remark){
        remarks.add(remark);
    }

    public ArrayList<Integer> getMinutes() {
        return minutes;
    }

    public ArrayList<String> getRemarks() {
        return remarks;
    }

    public int getHour() {
        return hour;
    }
}
