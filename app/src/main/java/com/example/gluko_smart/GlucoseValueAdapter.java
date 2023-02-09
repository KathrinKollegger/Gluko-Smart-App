package com.example.gluko_smart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GlucoseValueAdapter extends BaseAdapter {
    private List<GlucoseValues> storedGlucoValues;

    public GlucoseValueAdapter(List<GlucoseValues> storedGlucoValues) {
        this.storedGlucoValues=storedGlucoValues;
    }

    @Override
    public int getCount() {
        return storedGlucoValues.size();
    }

    @Override
    public Object getItem(int position) {
        return storedGlucoValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_list_item_view, parent, false);
        }

        GlucoseValues glucoseValue = storedGlucoValues.get(position);
        String timestamp = glucoseValue.getTimestamp();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat niceDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String niceDate = niceDateFormat.format(date);

        TextView valueTextView = convertView.findViewById(R.id.textGlucoseValue);
        valueTextView.setText(String.valueOf(glucoseValue.getBzWert())+" mg/dl");
        TextView timeTextView = convertView.findViewById(R.id.textTimestamp);
        timeTextView.setText(niceDate);

        Button buttonExport = convertView.findViewById(R.id.buttonExport);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        buttonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Export hier einfügen
                FirebaseToFHIR fireToFhir = new FirebaseToFHIR();
                fireToFhir.createObservation(glucoseValue);

            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                    final int positionToDelete = position;
                    GlucoseValues glucoseValue = storedGlucoValues.get(positionToDelete);

                    String time = glucoseValue.getTimestamp();
                    FirebaseDatabase db = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
                    FirebaseDatabase.getInstance().getReference().child("GlucoseValues").child(time).removeValue();
                    storedGlucoValues.remove(positionToDelete);
                    notifyDataSetChanged();
                    Toast.makeText(parent.getContext(), "Blutzucker Wert wurde aus Wochenansicht, sowie aus der Datenbank gelöscht!", Toast.LENGTH_SHORT).show();

            }
            });

        return convertView;

    }
}
