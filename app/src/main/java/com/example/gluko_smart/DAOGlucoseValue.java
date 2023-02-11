package com.example.gluko_smart;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DAOGlucoseValue {

    private DatabaseReference databaseReference;

    public DAOGlucoseValue(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = db.getReference("users/" +userId).child(GlucoseValues.class.getSimpleName());
    }

    public Task<Void> add(GlucoseValues glucoseValues) {

            String key = databaseReference.push().getKey();
            glucoseValues.setKey(key);
            return databaseReference.child(key).setValue(glucoseValues);
    }




}
