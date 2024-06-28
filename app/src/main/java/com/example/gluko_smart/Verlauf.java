package com.example.gluko_smart;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Verlauf extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Home button
    Button button_homeVerlauf;
    // Buttons to switch between graph and list view
    Button fragmentAlleWerteBtn;
    Button fragmentTagBtn;
    // Button to export the data as a PDF
    Button pdfExportBtn;

    // Text view to display dynamic content
    TextView valueDisplay;

    //show daily values as default view when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        // Set the value display to the default text
        valueDisplay = (TextView) findViewById(R.id.tv_valueDisplayNew);
        valueDisplay.setText("Wähle einen Verlauf");
        valueDisplay.setVisibility(View.VISIBLE);

        button_homeVerlauf = findViewById(R.id.button_homeVerlauf);
        fragmentAlleWerteBtn = findViewById(R.id.fragmentAlleWertebtn);
        fragmentTagBtn = findViewById(R.id.fragmentTagbtn);
        pdfExportBtn = findViewById(R.id.button_exportPdf);

        // Set the default fragment to the daily view
        replaceFragment(new FragementVerlauf());

        button_homeVerlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(Verlauf.this, Home.class);
                startActivity(intent7);
            }
        });

        // Switch to general view when fragmentWocheBtn button is clicked
        fragmentAlleWerteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragmentVerlaufGesamt());

            }
        });

        // Switch to linceChart view when daily button is clicked
        fragmentTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragementVerlauf());
            }
        });

        // Set the onClick listener for the PDF export button
        pdfExportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Verlauf.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Verlauf.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    exportPdf();
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        valueDisplay.setText(R.string.chooseCourse);
    }

    private void exportPdf() {
        //Get an instance of the FirebaseAuth and the FirebaseDatabase

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app").getReference();

        String userId = mAuth.getCurrentUser().getUid();// Setze hier die Benutzer-ID

        mDatabase.child("users").child(userId).child("GlucoseValues").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<GlucoseValues> glucoseValuesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GlucoseValues value = snapshot.getValue(GlucoseValues.class);
                    glucoseValuesList.add(value);
                }

                PDFExport pdfExport = new PDFExport(Verlauf.this);
                pdfExport.createPdf(glucoseValuesList, mAuth.getCurrentUser().getEmail());
                Toast.makeText(Verlauf.this, "PDF generiert und in den Dokumenten abgelegt", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Verlauf.this, "Fehler beim Abrufen der Daten", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Replace the fragment in the frame layout with the given fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Update the text in the value display
    public void updateValueDisplay(String data) {
        valueDisplay.setText(data);
    }

}
