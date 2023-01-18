package com.example.gluko_smart;

public class GlucoseValues {


    private String bzWert;
    private String vorNachMahlzeit;

    public GlucoseValues(){}


    public GlucoseValues(String bzWert, String vorNachMahlzeit) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
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
}
