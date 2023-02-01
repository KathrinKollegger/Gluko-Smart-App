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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
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
import java.util.concurrent.TimeUnit;


public class FragmentVerlaufTag extends Fragment {
    private LineChart chart;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Entry> entries = new ArrayList<>();
                        for (DataSnapshot glucoseValuesSnapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) glucoseValuesSnapshot.getValue();
                            Object timestamp = map.get("timestamp");
                            if (!(timestamp instanceof Long)) {
                                Log.e("FragmentVerlaufWoche", "timestamp is not a Long: " + timestamp);
                                continue;
                            }
                            Object glucoseValue = map.get("bzWert");
                            if (!(glucoseValue instanceof Double)) {
                                Log.e("FragmentVerlaufWoche", "glucoseValue is not a Double: " + glucoseValue);
                                continue;
                            }

                            long time = (long) timestamp;
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(time);
                            cal.set(Calendar.HOUR_OF_DAY, 0);
                            cal.set(Calendar.MINUTE, 0);
                            cal.set(Calendar.SECOND, 0);
                            cal.set(Calendar.MILLISECOND, 0);
                            long startOfToday = cal.getTimeInMillis();
                            if (time >= startOfToday && time < startOfToday + 24 * 60 * 60 * 1000) {
                                entries.add(new Entry((time - startOfToday) / 1000f / 60f / 60f, ((Double) glucoseValue).floatValue()));
                            }
                        }
                        LineDataSet dataSet = new LineDataSet(entries, "Tages-Blutzuckerwerte");
                        dataSet.setColor(Color.BLUE);
                        dataSet.setLineWidth(2f);
                        LineData lineData = new LineData(dataSet);

                        //Design X-Achse
                        XAxis xAxis = chart.getXAxis();
                        xAxis.setValueFormatter(new HourAxisValueFormatter());
                        xAxis.setEnabled(true);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.isDrawLabelsEnabled();
                        xAxis.setAxisMinimum(0);
                        xAxis.setAxisMaximum(24);


                        chart.setData(lineData);
                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // handle error
                    }
                });
    }
}

