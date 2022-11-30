package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Persoenliches extends Activity {


    Button button_homePersoenliches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persoenliches);



        button_homePersoenliches = (Button) findViewById(R.id.button_homePersoenliches);

        button_homePersoenliches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent(Persoenliches.this, Home.class);
                startActivity(intent9);
            }
        });
    }}