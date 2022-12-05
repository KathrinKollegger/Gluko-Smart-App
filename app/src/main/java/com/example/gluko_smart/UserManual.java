package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserManual extends Activity {


    Button button_homeUser;
    Button button_weiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanual);

        button_homeUser = (Button) findViewById(R.id.button_homeUser);
        button_weiter = (Button) findViewById(R.id.button_weiter);

        button_homeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent14 = new Intent(UserManual.this, Home.class);
                startActivity(intent14);
            }
        });

        button_weiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent15 = new Intent(UserManual.this, UserManual2.class);
                startActivity(intent15);
            }
        });
    }}