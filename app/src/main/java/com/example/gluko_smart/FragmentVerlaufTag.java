package com.example.gluko_smart;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class FragmentVerlaufTag extends Fragment {
    private LineChart chart;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final List<GlucoseValues> storedGlucoValues = new ArrayList<GlucoseValues>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_tag, container, false);
        chart = view.findViewById(R.id.linechartTag);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Calendar calStar = Calendar.getInstance();
        calStar.set(Calendar.HOUR_OF_DAY, 0);
        calStar.set(Calendar.MINUTE, 0);
        calStar.set(Calendar.SECOND, 0);

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(Calendar.HOUR_OF_DAY, 23);
        calEnd.set(Calendar.MINUTE, 59);
        calEnd.set(Calendar.SECOND, 59);

        long startOfToday = calStar.getTimeInMillis();
        long endOfToday = calEnd.getTimeInMillis();




        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .addValueEventListener(new ValueEventListener() {

                    /**
                     * Will be invoked any time the data on the database changes.
                     * Will also be invoked as soon as we connect the Listener, to get initial shanpshot of stored Data of DB-Reference
                     * @param dataSnapshot
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children) {
                            GlucoseValues retrivedValue = child.getValue(GlucoseValues.class);
                            storedGlucoValues.add(retrivedValue);
                            //Log.i("onDataChanged", "Wert:" + retrivedValue.getBzWert());
                        }

                        List<Entry> entries = new ArrayList<>();
                        for (GlucoseValues glucoValue : storedGlucoValues) {
                            String timestamp = glucoValue.getTimestamp();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Date date = null;
                            try {
                                date = simpleDateFormat.parse(timestamp);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (date == null) {
                                continue;
                            }
                            long timeInMillis = date.getTime();

                            if (timeInMillis >= startOfToday && timeInMillis <= endOfToday) {
                                entries.add(new Entry((float) timeInMillis, (float) glucoValue.getBzWert()));
                                Log.i("Entries", "added: Time=" + timeInMillis + "Bz=" + glucoValue.getBzWert());
                            }
                        }

                       /* List<Entry> entries = new ArrayList<>();
                        for (DataSnapshot glucoseValuesSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) glucoseValuesSnapshot.getValue();
                            String timestamp = (String) map.get("timestamp");

                            //Änderung auf instanceof String wegen neuem Timestamp
                            if (!(timestamp instanceof String)) {
                                Log.e("FragmentVerlaufWoche", "timestamp is not a String: " + timestamp);
                                continue;
                            }

                            Long glucoseValue;
                            glucoseValue = (Long) map.get("bzWert");

                            if (!(glucoseValue instanceof Long)) {
                                Log.e("FragmentVerlaufWoche", "glucoseValue is not a Double: " + glucoseValue);
                                continue;
                            }
                            double doubleGlucoValue = glucoseValue * 1.0;


                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                            Calendar cal = Calendar.getInstance();
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);

                            try {
                                Date date = simpleDateFormat.parse(timestamp);
                                long time = date.getTime();//Time in Milliseconds
                                long startOfToday = cal.getTimeInMillis();

                                if (time >= startOfToday && time < startOfToday + 24 * 60 * 60 * 1000) {
                                    entries.add(new Entry((time - startOfToday) / 1000f / 60f / 60f, storedGlucoValues.));
                                }

                            } catch (ParseException e) {
                                Log.e("TimeParsingError","unsuccessfull SDF");
                            }
                        }*/

                        LineDataSet dataSet = new LineDataSet(entries, "Tages-Blutzuckerwerte");
                        dataSet.setColor(Color.BLUE);
                        dataSet.setValueTextSize(10f);
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setLineWidth(5f);
                        LineData lineData = new LineData(dataSet);

                        //Design X-Achse
                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new HourAxisValueFormatter());
                        xAxis.setAxisMinimum((float) startOfToday);
                        xAxis.setAxisMaximum((float) endOfToday);
                        xAxis.setEnabled(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setLabelCount(12);

                        //xAxis.isDrawLabelsEnabled();
                        xAxis.setDrawLabels(true);
                        chart.setData(lineData);
                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e("ERROR", "something went wrong");
                        // handle error
                    }
                });
    }
}

