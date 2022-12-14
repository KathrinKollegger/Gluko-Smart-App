package com.example.gluko_smart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button button_gereateKoppeln;
    Button button_verlauf;
    Button button_werteEinfuegen;
    Button button_persoenliches;
    Button button_userManual;
    Button button_abmelden;
    TextView textView_userName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button_gereateKoppeln = (Button) findViewById(R.id.button_gereateKoppeln);
        button_verlauf = (Button) findViewById(R.id.button_verlauf);
        button_werteEinfuegen = (Button) findViewById(R.id.button_werteEinfuegen);
        button_persoenliches = (Button) findViewById(R.id.button_persoenliches);
        button_userManual = (Button) findViewById(R.id.button_userManual);
        button_abmelden = (Button) findViewById(R.id.button_abmelden);
        textView_userName = (TextView) findViewById(R.id.tv_UserName);
        textView_userName.setText(getIntent().getStringExtra("UserName"));

        mAuth= FirebaseAuth.getInstance();

        button_gereateKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, GereateKoppeln2.class);
                startActivity(intent);
            }
        });

        button_verlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Home.this, Verlauf.class);
                startActivity(intent1);
            }
        });

        button_werteEinfuegen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Home.this, WerteEinfuegen.class);
                startActivity(intent2);
            }
        });

        button_persoenliches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(Home.this, Persoenliches.class);
                startActivity(intent3);
            }
        });

        button_userManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(Home.this, UserManual.class);
                startActivity(intent4);
            }
        });

        //abmelden mit signout() Methode
        button_abmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent5 = new Intent(Home.this, RegisterActivity.class);
                startActivity(intent5);
                finish();
                Toast.makeText(Home.this, "Abmelden erfolreich!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}