package com.example.gluko_smart;
import java.util.Map;

public class FirebaseToHL7v2 {

   public static String convert(Map<String, Object>data){
       StringBuilder sb= new StringBuilder();

       //Hier könnten die Daten aus der Firebase-Datenbank extrahiert
       // und in ein HL7v2-Format umgewandelt werden
       //je nachdem welchenm Datentyp wir haben und brauchen
        String patientSVNR = (String) data.get("patient_id");

        //.....

       //HL7v2-Meldung zusammenbauen
       //diese Meldung können wir dann durch Klasse HL7v2Message
       // in ein Dateiformat exportieren
       //Komponenten HL7 müss ma schauen

       sb.append("MSH|^~\\&|"); // MSH-Segment
       sb.append("...");
       sb.append("PID|"); // PID-Segment
       sb.append(patientSVNR + "|");
       // weitere dann hier dazu
       //wegen Datetyp müss ma no schauen

       return sb.toString();

   }



}
