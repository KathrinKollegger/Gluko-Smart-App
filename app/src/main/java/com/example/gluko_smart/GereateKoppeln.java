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


        //adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bt is available or not and update status
       if(bluetoothCheck(mBtAdapter)==true) {
           switch_Bt.setChecked(true);
       };

        //on switch clickListener
        switch_Bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(GereateKoppeln.this, "Turning on Bluetooth...", Toast.LENGTH_SHORT).show();
                    //BT Disabled
                    if (!mBtAdapter.isEnabled()) {
                        //Intent to turn on Bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                            return;
                        }
                        startActivityForResult(intent, REQUEST_ENABLE_BT);
                    } else {
                        Toast.makeText(GereateKoppeln.this, "Bluetooth turned ON", Toast.LENGTH_SHORT).show();
                        bluetoothCheck(mBtAdapter);
                    }
                }
                else {
                    Toast.makeText(GereateKoppeln.this, "Bluetooth turned OFF!", Toast.LENGTH_SHORT).show();
                    mBtAdapter.disable();
                    bluetoothCheck(mBtAdapter);

                }
                if (mBtAdapter == null && !mBtAdapter.isEnabled()) {
                    tv_BtStatus.setText("Bluetooth is not available");
                    img_BtIcon.setImageResource(R.drawable.ic_action_off);
                } else {
                    tv_BtStatus.setText("Bluetooth is available");
                    img_BtIcon.setImageResource(R.drawable.ic_action_on);
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
public boolean bluetoothCheck (BluetoothAdapter mBtAdapter){

    if (mBtAdapter !=null && mBtAdapter.isEnabled()) {
        tv_BtStatus.setText("Bluetooth is available and turned on");
        img_BtIcon.setImageResource(R.drawable.ic_action_on);
        return true;
    } else {
        tv_BtStatus.setText("Bluetooth is not available or turned of");
        img_BtIcon.setImageResource(R.drawable.ic_action_off);

    }
    return false;
    }
}
