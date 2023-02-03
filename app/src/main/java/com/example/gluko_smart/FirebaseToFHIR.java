package com.example.gluko_smart;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//In dieser Klasse wird eine Instanz der Firebase Datenbank erzeugt und auf den entsprechenden Datensatz zugegriffen.
// Wenn der Datensatz verfügbar ist, wird der Glukosewert ausgelesen und in eine Instanz der Klasse BloodGlucose gesetzt.
// Anschließend wird die toXml() Methode aufgerufen, um das FHIR XML Dokument zu generieren, welches dann weiter verarbeitet werden kann.

public class FirebaseToFHIR {
    private DatabaseReference mDatabase;

    public FirebaseToFHIR() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static BloodGlucose convertToFhir(String time, String unit, int value) {
        BloodGlucose bloodGlucose = new BloodGlucose();
        bloodGlucose.setDateTime(time);
        bloodGlucose.setValue(value);
        bloodGlucose.setUnit(unit);
        return bloodGlucose;

        /*mDatabase.child(glucoseValueKey).addValueEventListener(new ValueEventListener() {
        @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String glucoseValue = dataSnapshot.getValue(String.class);
                BloodGlucose bloodGlucose = new BloodGlucose();
                bloodGlucose.setValue(glucoseValue);
                try {
                    String fhirXml = bloodGlucose.toXml();
                    // Do something with the generated FHIR XML, for example write it to a file
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });*/
    }
}