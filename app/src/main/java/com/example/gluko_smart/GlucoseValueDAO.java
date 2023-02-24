package com.example.gluko_smart;

import static com.example.gluko_smart.GlobalVariable.FIREBASE_DB_INSTANCE;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 This class represents a DAO (Data Access Object) for GlucoseValues in Firebase Realtime Database.
 It provides methods to add GlucoseValues to the database.
 */
public class GlucoseValueDAO {

    private final DatabaseReference databaseReference;

    //Constructs a new GlucoseValueDAO instance
    public GlucoseValueDAO(){

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance(FIREBASE_DB_INSTANCE);
        databaseReference = db.getReference("users/" +userId).child(GlucoseValues.class.getSimpleName());
    }


    //Adds a new GlucoseValues object to the database.
    public Task<Void> add(GlucoseValues glucoseValues) {

            String key = databaseReference.push().getKey();
            glucoseValues.setKey(key);
            String timestamp = glucoseValues.getTimestamp();
            return databaseReference.child(timestamp).setValue(glucoseValues);
    }
}
