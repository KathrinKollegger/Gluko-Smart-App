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

import androidx.appcompat.widget.AppCompatSpinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class WerteEingabe extends Activity {

    Button button_homeVerlauf;
    AppCompatSpinner ed_infoessen;
    private String selectedEssen;
    private EditText eingabe_Blutzuckerwert;
    private Button button_speichern1;
    private DatabaseReference rootDatabaseref;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werte_eingabe);

        eingabe_Blutzuckerwert = findViewById(R.id.eingabe_Blutzuckerwert);
        button_speichern1 = findViewById(R.id.button_speichern1);

        rootDatabaseref = FirebaseDatabase.getInstance().getReference().child("Wert");

        button_speichern1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = eingabe_Blutzuckerwert.getText().toString();

                rootDatabaseref.setValue(data);
            }
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