package com.example.gluko_smart;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GereateKoppeln extends Activity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    Button button_homeKoppeln, button_getDevices;
    Switch switch_Bt;
    ImageView img_BtIcon;
    TextView tv_pairedDev, tv_BtStatus;

    BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereatekoppeln);

        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);
        button_getDevices = (Button) findViewById(R.id.button_getBtDevices);
        switch_Bt = (Switch) findViewById(R.id.switch_BT);
        img_BtIcon = (ImageView) findViewById(R.id.iv_bluetooth);
        tv_pairedDev = (TextView) findViewById(R.id.tv_pairedDevsTV);
        tv_BtStatus = (TextView) findViewById(R.id.tv_statusBluetooth);

        //BTadapter of Smartphone
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bt is available or not and update status
       if(bluetoothCheck(mBtAdapter)==true) {
           switch_Bt.setChecked(true);
           switch_Bt.setText("ON");
       }
       else {
           switch_Bt.setText("OFF");
       }

        //on switch clickListener
        switch_Bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_Bt.setText("ON");
                    Toast.makeText(GereateKoppeln.this, "Bluetooth wird aktiviert...", Toast.LENGTH_SHORT).show();
                    //BT is disabled otherwise switch (isChecked) = false
                    if (!mBtAdapter.isEnabled()) {
                        //Intent to turn on Bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                            return;
                        }
                        startActivityForResult(intent, REQUEST_ENABLE_BT);


                        //if switch is unchecked but bluetooth is already enabled
                    } else {
                        Toast.makeText(GereateKoppeln.this, "Bluetooth wurde aktiviert", Toast.LENGTH_SHORT).show();
                        bluetoothCheck(mBtAdapter);
                    }
                }
                else {
                    Toast.makeText(GereateKoppeln.this, "Bluetooth wurde deaktiviert!", Toast.LENGTH_SHORT).show();
                    switch_Bt.setText("OFF");
                    mBtAdapter.disable();
                    img_BtIcon.setImageResource(R.drawable.ic_action_off);
                    tv_BtStatus.setText("Bluetooth nicht verfügbar oder deaktiviert");


                }
            }
        });

        ;

        button_getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





        button_homeKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(GereateKoppeln.this, Home.class);
                startActivity(intent6);
            }
        });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth is on
                    bluetoothCheck(mBtAdapter);
                    Toast.makeText(this, "Bluetooth wurde aktiviert", Toast.LENGTH_SHORT).show();
                }
                else {
                    //user denied/cancelled turn on
                    Toast.makeText(this, "Bluetooth Aktivierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    switch_Bt.setChecked(false);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean bluetoothCheck (BluetoothAdapter mBtAdapter){
        if (mBtAdapter !=null && mBtAdapter.isEnabled()) {
        tv_BtStatus.setText("Bluetooth ist verfügbar und aktiviert");
        img_BtIcon.setImageResource(R.drawable.ic_action_on);
        return true;
        } else {
        tv_BtStatus.setText("Bluetooth nicht verfügbar oder deaktiviert");
        img_BtIcon.setImageResource(R.drawable.ic_action_off);
        }
        return false;
    }
}
