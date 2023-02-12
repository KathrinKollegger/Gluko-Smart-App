package com.example.gluko_smart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
                fireToFhir.execute(glucoseValue);
            }

        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlucoseValues glucoseValue = storedGlucoValues.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Sind Sie sicher, dass Sie diesen Blutzuckerwert löschen möchten?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                database.getReference("users")
                                        .child(userId)
                                        .child("GlucoseValues")
                                        .child(glucoseValue.getKey())
                                        .removeValue();
                                storedGlucoValues.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(parent.getContext(), "Der Blutzuckerwert wurde gelöscht!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return convertView;

    }
}
