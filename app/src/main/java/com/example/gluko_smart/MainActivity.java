package com.example.gluko_smart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    public final int LOAD_TIME = 3000;
    boolean ok = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        while(ok) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                startApplication();
                ok= false;


            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
                ok=true;
            }
        }



        //if (ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT) ==
        // PackageManager.PERMISSION_GRANTED) {

        // } else {
        // requestPermissions(new String[]{ Manifest.permission.BLUETOOTH_CONNECT}, 10);
        //}
    }


        //Starts RegisterActivity
        public void startApplication(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }, LOAD_TIME);
    }

}