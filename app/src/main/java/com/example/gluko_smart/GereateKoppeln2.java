package com.example.gluko_smart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GereateKoppeln2 extends AppCompatActivity {

    Button button_wertebekommen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereate_koppeln2);

        button_wertebekommen = (Button) findViewById(R.id.button_wertebekommen);


        button_wertebekommen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent13 = new Intent(GereateKoppeln2.this, GereateKoppeln.class);
                startActivity(intent13);
            }
        });
    }
}