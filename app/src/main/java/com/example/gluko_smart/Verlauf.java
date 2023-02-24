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

    Button button_homeVerlauf;
    Button fragmentWocheBtn;
    Button fragmentTagBtn;
    // Text view to display dynamic content
    TextView valueDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        valueDisplay = (TextView) findViewById(R.id.tv_valueDisplayNew);
        valueDisplay.setText("Wähle einen Verlauf");
        valueDisplay.setVisibility(View.VISIBLE);

        button_homeVerlauf = findViewById(R.id.button_homeVerlauf);
        fragmentWocheBtn = findViewById(R.id.fragmentWochebtn);
        fragmentTagBtn = findViewById(R.id.fragmentTagbtn);

        button_homeVerlauf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent7 = new Intent(Verlauf.this, Home.class);
                startActivity(intent7);
            }
        });

        // Switch to general view when fragmentWocheBtn button is clicked
        fragmentWocheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragmentVerlaufGesamt());

            }
        });

        // Switch to daily view when daily button is clicked
        fragmentTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragmentVerlaufTag());
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
