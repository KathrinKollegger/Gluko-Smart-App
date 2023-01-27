package com.example.gluko_smart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FragmentVerlaufWoche extends Fragment {
    private LineChart chart;
    private ArrayList<Entry> data = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
    private DatabaseReference myRef = database.getReference("GlucoseValues");

    public FragmentVerlaufWoche() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_woche, container, false);
        chart = view.findViewById(R.id.linechartWoche);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    float x = child.child("bzWert").getValue(Float.class);
                    float y = child.child("bzWert").getValue(Float.class);
                    data.add(new Entry(x, y));
                }

                LineDataSet dataSet = new LineDataSet(data, "GlucoseValues");
                LineData lineData = new LineData(dataSet);
                chart.setData(lineData);

                chart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TestChart", "Failed to read value.", error.toException());
            }
        });
        return view;
    }
}
