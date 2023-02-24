package com.example.gluko_smart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * The Home activity is the main activity of the app that is displayed when the user logs in.
 *  It contains buttons that allow the user to navigate to different parts of the app, as well as
 *  a TextView that displays the user's name.
 */
public class Home extends AppCompatActivity {

    Button button_gereateKoppeln;
    Button button_verlauf;
    Button button_werteEinfuegen;
    Button button_userManual;
    Button button_abmelden;
    TextView textView_userName;
    private FirebaseAuth mAuth;

    //Called when the activity is resumed. Retrieves the username from SharedPreferences
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs",MODE_PRIVATE);
        textView_userName.setText(sharedPreferences.getString("username",""));
    }

    //called when the activity is created. Initializes the buttons and TextView, retrieves username from SharedPreference
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        button_gereateKoppeln = (Button) findViewById(R.id.button_gereateKoppeln);
        button_verlauf = (Button) findViewById(R.id.button_verlauf);
        button_werteEinfuegen = (Button) findViewById(R.id.button_werteEinfuegen);
        button_userManual = (Button) findViewById(R.id.button_userManual);
        button_abmelden = (Button) findViewById(R.id.button_abmelden);
        textView_userName = (TextView) findViewById(R.id.tv_UserName);

        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs",MODE_PRIVATE);
        textView_userName.setText(sharedPreferences.getString("username",""));

        mAuth= FirebaseAuth.getInstance();

        //Sets onClickListener for Buttons
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

        button_userManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(Home.this, UserManual.class);
                startActivity(intent4);
            }
        });

        //sign out firebase user
        button_abmelden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
               Intent intent5 = new Intent(Home.this, RegisterActivity.class);
               startActivity(intent5);
               Toast.makeText(Home.this, getString(R.string.SignOutSuccess), Toast.LENGTH_SHORT).show();
                //Also closes Stacktrace, so user cannot re-enter via return button
               finishAffinity();
            }
        });
    }
}