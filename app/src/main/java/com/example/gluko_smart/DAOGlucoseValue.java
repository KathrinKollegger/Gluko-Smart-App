package com.example.gluko_smart;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOGlucoseValue {

    private DatabaseReference databaseReference;

    public DAOGlucoseValue(){

        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = db.getReference(GlucoseValues.class.getSimpleName());
    }

    public Task<Void> add(GlucoseValues glucoseValues){

        if (glucoseValues == null){
            throw new RuntimeException();
        } //throw exception

       return databaseReference.push().setValue(glucoseValues);


    }




}
