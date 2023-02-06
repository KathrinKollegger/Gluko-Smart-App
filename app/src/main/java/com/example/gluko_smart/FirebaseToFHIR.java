package com.example.gluko_smart;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//In dieser Klasse wird eine Instanz der Firebase Datenbank erzeugt und auf den entsprechenden Datensatz zugegriffen.
// Wenn der Datensatz verfügbar ist, wird der Glukosewert ausgelesen und in eine Instanz der Klasse BloodGlucose gesetzt.
// Anschließend wird die toXml() Methode aufgerufen, um das FHIR XML Dokument zu generieren, welches dann weiter verarbeitet werden kann.

public class FirebaseToFHIR {

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser.getUid();
    DatabaseReference database = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").
    getReference("users").child(userId).child("GlucoseValues");

   /* database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {

                        // Get the values for each child node
                        int bzWert = childSnapshot.child("bzWert").getValue(Integer.class);
                        String timestamp = childSnapshot.child("timestamp").getValue(String.class);

                        // Create a new GlucoseValues object with the retrieved values
                        GlucoseValues glucoseValue = new GlucoseValues(bzWert, timestamp);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });*/



    public static BloodGlucoseToXML convertToFhir(String time, String unit, int value) {

        BloodGlucoseToXML bloodGlucoseToXML = new BloodGlucoseToXML();
        bloodGlucoseToXML.setDateTime(time);
        bloodGlucoseToXML.setValue(value);
        bloodGlucoseToXML.setUnit(unit);

        GlucoseValues glucoValueForFHIR = new GlucoseValues(90,"test");

        return bloodGlucoseToXML;



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