package com.example.gluko_smart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserManual extends Activity {


    Button button_homeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanual);

        button_homeUser = (Button) findViewById(R.id.button_homeUser);

        button_homeUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent9 = new Intent(UserManual.this, Home.class);
                startActivity(intent9);
            }
        });
    }}