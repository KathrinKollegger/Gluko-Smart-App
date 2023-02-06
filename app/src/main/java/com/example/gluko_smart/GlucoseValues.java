package com.example.gluko_smart;

public class GlucoseValues {


    private int bzWert;
    private String vorNachMahlzeit;
    private String timestamp;
    //private String date;

    public GlucoseValues() {

    }

    //Constructor ohne vorNachMahlzeit
    public GlucoseValues(int bzWert, String timestamp){

        this.bzWert=bzWert;
        this.timestamp=timestamp;
    }

    //Constructor mit vorNachMahlzeit
    public GlucoseValues(int bzWert, String vorNachMahlzeit, String timestamp) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.timestamp = timestamp;
    }

    public int getBzWert(){

        return bzWert;
    }

    public void setBzWert(int bzWert){

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
