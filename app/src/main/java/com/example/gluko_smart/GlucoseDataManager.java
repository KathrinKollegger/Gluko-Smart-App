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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        long startTime;
        long endTime;

        switch (viewMode) {
            case "day":
                startTime = getStartOfDayInMillis();
                endTime = getEndOfDayInMillis();
                break;
            case "week":
                cal.add(Calendar.DAY_OF_YEAR, -6); // Die letzten 6 Tage + heute
                startTime = cal.getTimeInMillis();
                endTime = getEndOfDayInMillis();
                break;
            case "month":
                cal.add(Calendar.DAY_OF_YEAR, -29); // Die letzten 29 Tage + heute
                startTime = cal.getTimeInMillis();
                endTime = getEndOfDayInMillis();
                break;
            default:
                throw new IllegalArgumentException("Unsupported view mode: " + viewMode);
        }

        // Load aggregated data in LineChart
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .orderByChild("timestamp").startAt(sdf.format(new Date(startTime))).endAt(sdf.format(new Date(endTime)))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<GlucoseValues> values = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GlucoseValues value = snapshot.getValue(GlucoseValues.class);
                            if (value != null) {
                                values.add(value);
                            }
                        }
                        List<Entry> aggregatedEntries = aggregateData(values, viewMode);
                        callback.accept(aggregatedEntries);
                    }
        /*
        // Load normal data in LineChart
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
                                    int bzWert = value.getBzWert();
                                    Log.i("GlucoseDataManager", "Timestamp: " + value.getTimestamp() + ", BZ-Wert: " + bzWert);
                                    entries.add(new Entry(date.getTime(), bzWert));
                                } catch (Exception e) {
                                    Log.e("GlucoseDataManager", "Error parsing date: " + value.getTimestamp(), e);
                                }
                            }
                        }
                        Collections.sort(entries, (e1, e2) -> Float.compare(e1.getX(), e2.getX()));
                        callback.accept(entries);

                    }*/

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("GlucoseDataManager", "Error loading data: " + databaseError.getMessage());
                    }
                });
    }

    private List<Entry> aggregateData(List<GlucoseValues> values, String viewMode) {
        Map<Long, List<Float>> aggregatedMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        for (GlucoseValues value : values) {
            try {
                Date date = sdf.parse(value.getTimestamp());
                calendar.setTime(date);

                long key;
                if (viewMode.equals("week")) {
                    // Aggregation per Halbtag
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    if (hour < 12) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                    } else {
                        calendar.set(Calendar.HOUR_OF_DAY, 12);
                    }
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    key = calendar.getTimeInMillis();
                } else if (viewMode.equals("month")) {
                    // Aggregation per Tag
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    key = calendar.getTimeInMillis();
                } else {
                    // Für die Tagesansicht keine Aggregation
                    key = date.getTime();
                }

                if (!aggregatedMap.containsKey(key)) {
                    aggregatedMap.put(key, new ArrayList<>());
                }
                aggregatedMap.get(key).add((float) value.getBzWert());
            } catch (Exception e) {
                Log.e("GlucoseDataManager", "Error parsing date: " + value.getTimestamp(), e);
            }
        }

        List<Entry> aggregatedEntries = new ArrayList<>();
        for (Map.Entry<Long, List<Float>> entry : aggregatedMap.entrySet()) {
            long timestamp = entry.getKey();
            List<Float> glucoseValues = entry.getValue();
            float average = 0;
            for (float val : glucoseValues) {
                average += val;
            }
            average /= glucoseValues.size();
            aggregatedEntries.add(new Entry(timestamp, average));
        }

        Collections.sort(aggregatedEntries, (e1, e2) -> Float.compare(e1.getX(), e2.getX()));
        return aggregatedEntries;
    }


    private long getStartOfDayInMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getEndOfDayInMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTimeInMillis();
    }
}
