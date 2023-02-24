package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//Pre-Information Activity for manuel value Input
public class WerteEinfuegen extends Activity {

    Button button_homeWerte;
    Button button_WerteEingabe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werteeinfuegen);


        button_homeWerte = (Button) findViewById(R.id.button_homeWerte);
        button_WerteEingabe = (Button) findViewById(R.id.button_WerteEingabe);

        // takes the user back to the home screen
        button_homeWerte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent8 = new Intent(WerteEinfuegen.this, Home.class);
                startActivity(intent8);
            }
        });

        // takes the user to the WerteEingabe activity, where they can manually input glucose values
        button_WerteEingabe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent11 = new Intent(WerteEinfuegen.this, WerteEingabe.class);
                startActivity(intent11);
            }
        });

    }
}