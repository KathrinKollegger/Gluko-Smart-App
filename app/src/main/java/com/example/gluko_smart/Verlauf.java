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

        fragmentWocheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                replaceFragment(new FragmentVerlaufGesamt());

            }
        });

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
        valueDisplay.setText("Wähle einen Verlauf");
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void updateValueDisplay(String data) {

        valueDisplay.setText(data);
    }

}
