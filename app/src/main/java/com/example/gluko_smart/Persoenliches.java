package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatSpinner;

import java.util.ArrayList;
import java.util.List;

public class Persoenliches extends Activity {


    Button button_homePersoenliches;
    AppCompatSpinner genderSpinner;
    EditText et_username,et_age,et_height,et_weight;
    private String selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persoenliches);



        button_homePersoenliches = (Button) findViewById(R.id.button_homePersoenliches);
        genderSpinner=findViewById(R.id.ed_infogender);

        button_homePersoenliches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent(Persoenliches.this, Home.class);
                startActivity(intent9);
            }
        });

        setupSpinner();
    }

    private void setupSpinner() {
        List<String> categories = new ArrayList<String>();
        categories.add("Männlich");
        categories.add("Weiblich");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(dataAdapter);

        genderSpinner.setSelection(0);
        selectedGender="Männlich";
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Methode wenn Geschlecht gewählt wurde. Änderung des Begriffs in der View.
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedGender=adapterView.getItemAtPosition(position).toString();
            }
            /**
             * Methode wenn kein Geschlecht gewählt wurde.
             * Die View bleibt so wie sie ist.
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }}