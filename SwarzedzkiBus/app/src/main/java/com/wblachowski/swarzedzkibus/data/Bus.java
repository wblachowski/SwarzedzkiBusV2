package com.wblachowski.swarzedzkibus.data;

/**
 * Created by wblachowski on 2/18/2018.
 */

public class Bus {
    String nr;
    String from;
    String to;
    public Bus(String nr,String from,String to){
        this.nr=nr;
        this.from=from;
        this.to=to;
    }

    public String getNr(){
        return nr;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
