package com.example.gluko_smart;

import java.time.LocalDateTime;

public class GlucoseValues {


    private float bzWert;
    private String vorNachMahlzeit;
    private LocalDateTime timestamp;
    private String user;
    //private String date;

    public GlucoseValues(){}


    public GlucoseValues(float bzWert, String vorNachMahlzeit, LocalDateTime timestamp, String user) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.timestamp = timestamp;
        this.user = user;
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

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp){
        this.timestamp = timestamp;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){

        this.user = user;
    }


}
