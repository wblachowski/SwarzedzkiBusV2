package com.wblachowski.swarzedzkibus.data;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class Stop {
    String id;
    String busNr;
    String name;
    String direction;

    public Stop(String id, String busNr, String name, String direction){
        this.id=id;
        this.busNr=busNr;
        this.name=name;
        this.direction=direction;
    }

    public String getId(){
        return id;
    }

    public String getBusNr() { return busNr; }

    public String getName() {
        return name;
    }

    public String getDirection() { return direction; }
}
