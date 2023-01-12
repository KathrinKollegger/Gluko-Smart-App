package com.example.gluko_smart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GereateKoppeln2 extends AppCompatActivity {

    Button button_wertebekommen;
    Button button_homeKoppeln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereate_koppeln2);

        button_wertebekommen = (Button) findViewById(R.id.button_wertebekommen);
        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);


        button_wertebekommen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent13 = new Intent(GereateKoppeln2.this, GereateKoppelnActivity.class);
                startActivity(intent13);
            }
        });


        button_homeKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent14 = new Intent(GereateKoppeln2.this, Home.class);
                startActivity(intent14);
            }
        });
    }
}