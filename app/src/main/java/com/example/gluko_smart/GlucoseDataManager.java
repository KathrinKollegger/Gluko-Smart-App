package com.example.gluko_smart;

import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class GlucoseDataManager {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    public GlucoseDataManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();
    }

    public void loadData(String viewMode, Consumer<List<Entry>> callback) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        switch (viewMode) {
            case "day":
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                break;
            case "week":
                cal.add(Calendar.DAY_OF_YEAR, -7);
                break;
            case "month":
                cal.add(Calendar.MONTH, -1);
                break;
        }

        long startTime = cal.getTimeInMillis();
        cal.add(Calendar.DATE, viewMode.equals("day") ? 1 : (viewMode.equals("week") ? 7 : 30));
        long endTime = cal.getTimeInMillis() - 1; // End time just before the next period starts

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .orderByChild("timestamp").startAt(sdf.format(new Date(startTime))).endAt(sdf.format(new Date(endTime)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Entry> entries = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GlucoseValues value = snapshot.getValue(GlucoseValues.class);
                            if (value != null) {
                                try {
                                    Date date = sdf.parse(value.getTimestamp());
                                    entries.add(new Entry(date.getTime(), value.getBzWert()));
                                } catch (Exception e) {
                                    Log.e("GlucoseDataManager", "Error parsing date: " + value.getTimestamp(), e);
                                }
                            }
                        }
                        Collections.sort(entries, (e1, e2) -> Float.compare(e1.getX(), e2.getX()));
                        callback.accept(entries);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("GlucoseDataManager", "Error loading data: " + databaseError.getMessage());
                    }
                });
    }
}
