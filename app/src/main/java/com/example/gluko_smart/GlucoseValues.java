package com.example.gluko_smart;

import java.time.LocalDateTime;

public class GlucoseValues {


    private float bzWert;
    private String vorNachMahlzeit;
    private long timestamp;
    //private String date;

    public GlucoseValues(){}


    public GlucoseValues(float bzWert, String vorNachMahlzeit, long timestamp) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.timestamp = timestamp;
        //this.date = date;
    }

    public float getBzWert(){

        return bzWert;
    }

    public void setBzWert(float bzWert){

        this.bzWert = bzWert;
    }

    public String getVorNachMahlzeit(){

        return vorNachMahlzeit;
    }

    public void setVorNachMahlzeit(String vorNachMahlzeit){

        this.vorNachMahlzeit = vorNachMahlzeit;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(long timestamp){
        this.timestamp = timestamp;
    }



}
