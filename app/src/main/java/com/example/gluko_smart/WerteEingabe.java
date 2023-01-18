package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatSpinner;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WerteEingabe extends Activity {


    Button button_homeVerlauf;
    AppCompatSpinner ed_infoessen;
    private String selectedEssen;
    TextView textView_date;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werte_eingabe);

        textView_date = findViewById(R.id.textView_date);

        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy / hh:mm a" );
        String dateTime = simpleDateFormat.format(calender.getTime());
        textView_date.setText(dateTime);


        final TextView textViewDate = findViewById(R.id.textView_date);
        final EditText edit_bzWert = findViewById(R.id.edit_bzWert);
        final Spinner edit_infoessen = findViewById(R.id.ed_infoessen);
        Button button_speichern1 = findViewById(R.id.button_speichern1);
        DAOGlucoseValue daoGlucoseValue = new DAOGlucoseValue();

        button_speichern1.setOnClickListener(v-> {

            GlucoseValues glucoseValues = new GlucoseValues(edit_bzWert.getText().toString(), edit_infoessen.getSelectedItem().toString(), textViewDate.getText().toString());
            daoGlucoseValue.add(glucoseValues).addOnSuccessListener(suc->{
                Toast.makeText(this, "Blutzuckerwert wurde gespeichert", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(er->{

                Toast.makeText(this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });


        button_homeVerlauf = (Button) findViewById(R.id.button_homeVerlauf);
        ed_infoessen = (AppCompatSpinner) findViewById(R.id.ed_infoessen);

        button_homeVerlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(WerteEingabe.this, Home.class);
                startActivity(intent7);
            }
        });

        setupSpinner();
    }
    private void setupSpinner() {
        List<String> categories = new ArrayList<String>();
        categories.add("Vor dem Essen");
        categories.add("Nach dem Essen");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ed_infoessen.setAdapter(dataAdapter);

        ed_infoessen.setSelection(0);
        selectedEssen="Vor dem Essen";
        ed_infoessen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Methode wenn Geschlecht gewählt wurde. Änderung des Begriffs in der View.
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedEssen=adapterView.getItemAtPosition(position).toString();
            }
            /**
             * Methode wenn kein Geschlecht gewählt wurde.
             * Die View bleibt so wie sie ist.
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }





}