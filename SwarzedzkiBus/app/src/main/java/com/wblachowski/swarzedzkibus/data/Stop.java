package com.wblachowski.swarzedzkibus.data;

/**
 * Created by wblachowski on 2/19/2018.
 */

public class Stop {
    String id;
    String name;
    public Stop(String id, String name){
        this.id=id;
        this.name=name;
    }

    public String getId(){
        return id;
    }

    public String getName() {
        return name;
    }
}
