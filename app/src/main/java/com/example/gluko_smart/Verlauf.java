package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Verlauf extends AppCompatActivity {

    Button button_homeVerlauf;
    Button fragmentWocheBtn;
    Button fragmentTagBtn;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verlauf);

        LineChart lineChartWoche = findViewById(R.id.linechartWoche);
        LineChart lineChartTag = findViewById(R.id.linechartTag);

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

        fragmentWocheBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){


                replaceFragment(new FragmentVerlaufWoche());
            }


        });

        fragmentTagBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                replaceFragment(new FragmentVerlaufTag());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

}
