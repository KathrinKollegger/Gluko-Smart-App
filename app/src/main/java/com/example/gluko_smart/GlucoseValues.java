package com.example.gluko_smart;

/**
 * A class representing glucose values, with a blood sugar level, timestamp, and optionally a meal time.
 * Implements the Comparable interface to allow sorting by timestamp.
 */
public class GlucoseValues implements Comparable<GlucoseValues> {


    private int bzWert;
    private String vorNachMahlzeit;
    private String timestamp;
    private String key;

    //Default constructor for GlucoseValues.
    public GlucoseValues() {
    }

    //Constructor ohne vorNachMahlzeit
    public GlucoseValues(int bzWert, String timestamp) {

        this.bzWert = bzWert;
        this.timestamp = timestamp;
    }

    //Constructor mit vorNachMahlzeit
    public GlucoseValues(int bzWert, String vorNachMahlzeit, String timestamp) {

        this.bzWert = bzWert;
        this.vorNachMahlzeit = vorNachMahlzeit;
        this.timestamp = timestamp;
    }

    public int getBzWert() {
        return bzWert;
    }

    public void setBzWert(int bzWert) {
        this.bzWert = bzWert;
    }

    public String getVorNachMahlzeit() {
        return vorNachMahlzeit;
    }

    public void setVorNachMahlzeit(String vorNachMahlzeit) {
        this.vorNachMahlzeit = vorNachMahlzeit;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    //used to compare two objects of the GlucoseValues class based on their timestamp variable.
    @Override
    public int compareTo(GlucoseValues gv) {
        return timestamp.compareTo(gv.getTimestamp());
    }
}
