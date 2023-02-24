package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WerteEingabe extends Activity {


    Button button_homeVerlauf;
    AppCompatSpinner ed_infoessen;
    private String selectedEssen;
    TextView textView_date;
    FirebaseUser user;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werte_eingabe);

        textView_date = findViewById(R.id.textView_date);
        user = FirebaseAuth.getInstance().getCurrentUser();

        //YYYY-MM-DDTHH:MM:SS
        Calendar calender = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd. MMMM yyyy / hh:mm a" );
        String dateTime = simpleDateFormat.format(calender.getTime());
        textView_date.setText(dateTime);
        //Vielleicht sollte Datum und Zeit manuell eingegeben werden bevor Wert hinzugefügt wird


        //final TextView textViewDate = findViewById(R.id.textView_date);
        final EditText edit_bzWert = findViewById(R.id.edit_bzWert);
        final Spinner edit_infoessen = findViewById(R.id.ed_infoessen);


        Button button_speichern1 = findViewById(R.id.button_speichern1);
        GlucoseValueDAO glucoseValueDAO = new GlucoseValueDAO();

        button_speichern1.setOnClickListener(v-> {
            String bzWertInput = edit_bzWert.getText().toString();

            //Nur gültige Werte: 14 < Wert < 200
            if(bzWertInput.length() < 2 ||
                    bzWertInput.contains(".") ||
                    Float.parseFloat(bzWertInput) > 250 ||
                    Float.parseFloat(bzWertInput) < 15){

                Toast.makeText(this, "Bitte geben Sie einen gültigen BZ Wert in der Einheit mg/dl ein!", Toast.LENGTH_SHORT).show();

            }else{

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Möchten Sie den Wert "+bzWertInput+" mg/dl wirklich speichern?")
                        .setPositiveButton("Ja", (dialog, id) -> {
                            //Sets Timestamp in ISO-Format for DB entry
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                            LocalDateTime loDatetime = LocalDateTime.now();
                            String dateForDB = loDatetime.format(formatter).substring(0,19);

                            GlucoseValues glucoseValue = new GlucoseValues(Integer.parseInt(bzWertInput),
                                    edit_infoessen.getSelectedItem().toString(), dateForDB);
                            glucoseValueDAO.add(glucoseValue)
                                    .addOnSuccessListener(suc->{
                                Toast.makeText(this, "Blutzuckerwert: "+bzWertInput+" mg/dl wurde gespeichert", Toast.LENGTH_SHORT).show();
                            })
                                    .addOnFailureListener(er->{
                                Toast.makeText(this, ""+er.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        })
                        .setNegativeButton("Nein", (dialog, id) -> {
                            dialog.cancel();
                        });
                AlertDialog alert = builder.create();
                alert.show();
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
        //selectedEssen="Vor dem Essen";
        ed_infoessen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Methode wenn Geschlecht gewählt wurde. Änderung des Begriffs in der View.
             */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedEssen=adapterView.getItemAtPosition(position).toString();
            }
            /**
             * Methode wenn keine Auswahl getroffen wurde.
             * Die View bleibt so wie sie ist.
             */
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}