package com.example.gluko_smart;

import java.time.LocalDateTime;

public class GlucoseValues {


    private String bzWert;
    private String vorNachMahlzeit;
    private LocalDateTime timestamp;
    private String user;
    //private String date;

    public GlucoseValues(){}


    public GlucoseValues(String bzWert, String vorNachMahlzeit, LocalDateTime timestamp, String user) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.timestamp = timestamp;
        this.user = user;
        //this.date = date;
    }

    public String getBzWert(){

        return bzWert;
    }

    public void setBzWert(String bzWert){

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

   /* public String getDate(){

        return date;
    }

    public void setDate(String date){

        this.date = date;
    }*/


}
