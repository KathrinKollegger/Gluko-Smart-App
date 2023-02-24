package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class UserManual extends Activity {


    Button button_homeUser;
    Button button_weiter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanual);

        button_homeUser = findViewById(R.id.button_homeUser);
        button_weiter = findViewById(R.id.button_weiter);

        button_homeUser.setOnClickListener(v -> {
            Intent intent14 = new Intent(UserManual.this, Home.class);
            startActivity(intent14);
        });

        button_weiter.setOnClickListener(v -> {
            Intent intent15 = new Intent(UserManual.this, UserManual2.class);
            startActivity(intent15);
        });
    }}