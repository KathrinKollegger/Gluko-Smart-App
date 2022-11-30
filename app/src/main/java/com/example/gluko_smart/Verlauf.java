package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Verlauf extends Activity {

    Button button_homeVerlauf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        button_homeVerlauf = (Button) findViewById(R.id.button_homeVerlauf);

        button_homeVerlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(Verlauf.this, Home.class);
                startActivity(intent7);
            }
        });
    }}
