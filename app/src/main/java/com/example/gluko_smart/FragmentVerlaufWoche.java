package com.example.gluko_smart;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Zeigt Gesamtverlauf - alle Werte von User
public class FragmentVerlaufWoche extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ListView listviewValues;

    private final List<GlucoseValues> storedGlucoValues = new ArrayList<GlucoseValues>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verlauf_woche, container, false);
        listviewValues = view.findViewById(R.id.listValues);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        return view;
    }

    public void onStart() {
        super.onStart();

        setValueDisplayData("Exportiere oder lösche einen Messwert");

        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).child("GlucoseValues")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                            for (DataSnapshot child : children) {

                                //Alle Einträge werden direkt in GlucoseValue-Objekte gespeichert
                                GlucoseValues retrivedValue = child.getValue(GlucoseValues.class);
                                //Danach in Liste 'storedGlucoValues aufgenommen'
                                storedGlucoValues.add(retrivedValue);
                                Log.i("onDataChanged", "Wert:" + retrivedValue.getBzWert()+retrivedValue.getKey());
                            }
                            //Liste wird nach Zeitpunkt der Messung sortiert
                            Collections.sort(storedGlucoValues);
                            GlucoseValueAdapter adapter = new GlucoseValueAdapter(storedGlucoValues);
                            listviewValues.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // handle error
                    }
                });
    }

    public void setValueDisplayData(String data) {
        if (getActivity() != null) {
            ((Verlauf) getActivity()).updateValueDisplay(data);
        }
    }

}
