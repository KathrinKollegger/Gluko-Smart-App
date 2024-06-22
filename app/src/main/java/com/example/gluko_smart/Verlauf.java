package com.example.gluko_smart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Verlauf extends AppCompatActivity {

    // Home button
    Button button_homeVerlauf;
    // Buttons to switch between graph and list view
    Button fragmentAlleWerteBtn;
    Button fragmentTagBtn;
    //But

    // Text view to display dynamic content
    TextView valueDisplay;

    //show daily values as default view when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        // Set the value display to the default text
        valueDisplay = (TextView) findViewById(R.id.tv_valueDisplayNew);
        valueDisplay.setText("Wähle einen Verlauf");
        valueDisplay.setVisibility(View.VISIBLE);

        button_homeVerlauf = findViewById(R.id.button_homeVerlauf);
        fragmentAlleWerteBtn = findViewById(R.id.fragmentAlleWertebtn);
        fragmentTagBtn = findViewById(R.id.fragmentTagbtn);

        // Set the default fragment to the daily view
        replaceFragment(new FragementVerlauf());

        button_homeVerlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(Verlauf.this, Home.class);
                startActivity(intent7);
            }
        });

        // Switch to general view when fragmentWocheBtn button is clicked
        fragmentAlleWerteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragmentVerlaufGesamt());

            }
        });

        // Switch to daily view when daily button is clicked
        fragmentTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragementVerlauf());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        valueDisplay.setText(R.string.chooseCourse);
    }

    // Replace the fragment in the frame layout with the given fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    // Update the text in the value display
    public void updateValueDisplay(String data) {
        valueDisplay.setText(data);
    }

}
