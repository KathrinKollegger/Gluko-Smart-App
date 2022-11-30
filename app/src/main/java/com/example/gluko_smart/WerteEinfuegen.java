package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WerteEinfuegen extends Activity {

    Button button_homeWerte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_werteeinfuegen);


        button_homeWerte = (Button) findViewById(R.id.button_homeWerte);

        button_homeWerte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent8 = new Intent(WerteEinfuegen.this, Home.class);
                startActivity(intent8);
            }
        });
    }}