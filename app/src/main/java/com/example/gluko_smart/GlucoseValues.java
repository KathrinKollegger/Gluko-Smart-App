package com.example.gluko_smart;

public class GlucoseValues {


    private float bzWert;
    private String vorNachMahlzeit;
    private String timestamp;
    //private String date;

    public GlucoseValues(){}


    public GlucoseValues(float bzWert, String vorNachMahlzeit, String timestamp) {

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

    public String getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }



}
