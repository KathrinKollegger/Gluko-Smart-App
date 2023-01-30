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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


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
                            entries.add(new Entry(((Long) timestamp) / 1000f / 60f / 60f, ((Double) glucoseValue).floatValue()));
                        }
                        LineDataSet dataSet = new LineDataSet(entries, "Tages-Blutzuckerwerte");
                        dataSet.setColor(Color.BLUE);
                        dataSet.setLineWidth(2f);
                        LineData lineData = new LineData(dataSet);

                        //Design X-Achse
                        XAxis xAxis = chart.getXAxis();
                        xAxis.setEnabled(true);
                        xAxis.isDrawLabelsEnabled();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setLabelRotationAngle(-90);
                        xAxis.setTextSize(12f);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1f);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setAxisLineColor(Color.GRAY);
                        xAxis.setAxisLineWidth(1f);
                        xAxis.setAvoidFirstLastClipping(true);
                        float xMin = Collections.min(entries, new EntryXComparator()).getX();
                        float xMax = Collections.max(entries, new EntryXComparator()).getX();
                        xAxis.setAxisMinimum(xMin);
                        xAxis.setAxisMaximum(xMax);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                return formatter.format(new Date((long)(value * 1000f * 60f * 60f)));
                            }
                        });

                        //Design Y-Achse
                        YAxis yAxis = chart.getAxisRight();
                        yAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                        yAxis.setTextColor(Color.GRAY);
                        yAxis.setDrawGridLines(true);
                        yAxis.setGranularityEnabled(true);
                        yAxis.setAxisMinimum(0f);
                        yAxis.setAxisMaximum(300f);
                        yAxis.setYOffset(-9f);

                        YAxis rightAxis = chart.getAxisRight();
                        rightAxis.setEnabled(false);


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
 //if  max stundenzahl < 24 design x-Achse von 0 bis Max-Wert

