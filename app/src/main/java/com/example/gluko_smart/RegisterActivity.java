package com.example.gluko_smart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    TextView alreadyHaveaccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //alreadyHaveaccount = findViewById(R.id.alreadyHaveaccount);
        alreadyHaveaccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View v){
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });
    }
}


