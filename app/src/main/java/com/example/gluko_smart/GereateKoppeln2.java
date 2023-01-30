package com.example.gluko_smart;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

public class GereateKoppeln2 extends AppCompatActivity {

    Button button_wertebekommen;
    Button button_homeKoppeln;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereate_koppeln2);

        button_wertebekommen = (Button) findViewById(R.id.button_wertebekommen);
        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);


        button_wertebekommen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GereateKoppeln2.this);
                //.fromHtml ist zwar eine veraltete Methode aber einzige Möglichkeit des einfach und schön auszugeben
                builder.setMessage(Html.fromHtml("<b>Wichtiger Hinweis</b><br><br>" +
                                "1.)  Klicke auf der nächsten Seite auf <b>Scan starten</b>.<br> <br>" +
                                "2.)  Klicke bei deinem Glucosegerät noch einmal auf den Pfeil. Das Bluetoothsymbol blinkt nun.<br><br> " +
                                "3.)  Klicke auf den Gerätenamen wenn dieser angezeigt wird, um deine <b>gespeicherten</b> Blutzuckereinträge zu bekommen!"))
                        .setCancelable(false)
                        .setPositiveButton("Verstanden", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User bestätigt den Text
                                Intent intent13 = new Intent(GereateKoppeln2.this, GereateKoppelnActivity.class);
                                startActivity(intent13);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        button_homeKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent14 = new Intent(GereateKoppeln2.this, Home.class);
                startActivity(intent14);
            }
        });
    }
}