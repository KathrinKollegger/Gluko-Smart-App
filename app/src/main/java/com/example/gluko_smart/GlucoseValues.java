package com.example.gluko_smart;

public class GlucoseValues {


    private String bzWert;
    private String vorNachMahlzeit;
    private String date;

    public GlucoseValues(){}


    public GlucoseValues(String bzWert, String vorNachMahlzeit, String date) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.date = date;
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

    public String getDate(){

        return date;
    }

    public void setDate(String date){

        this.date = date;
    }


}
