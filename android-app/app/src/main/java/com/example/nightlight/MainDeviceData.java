package com.example.nightlight;

import java.io.Serializable;

public class MainDeviceData implements Serializable {
    String mac;
    String nick;
    String type;

    public MainDeviceData(String mac, String nick, String type){
        this.mac = mac;
        this.nick = nick;
        this.type = type;
    }

    public String getMac(){
        return this.mac;
    }

    public String getNick(){
        return this.nick;
    }

    public String getType(){
        return  this.type;
    }
}
