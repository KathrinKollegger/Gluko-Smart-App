package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GereateKoppeln extends Activity {

    Button button_homeKoppeln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereatekoppeln);

        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);

        button_homeKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(GereateKoppeln.this, Home.class);
                startActivity(intent6);
            }
        });
}}
