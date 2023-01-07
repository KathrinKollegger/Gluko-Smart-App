package com.example.gluko_smart;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GereateKoppeln extends Activity {

    //Global Variables
    private static final String TAG = "BLE_Connection";

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int REQUEST_SCAN_BT = 2;
    private static final int REQUEST_LOCATION =3;
    private static final long SCAN_INTERVAL = 5000;

    //Views UI
    Button button_homeKoppeln, button_getDevices, button_btSync;
    Switch switch_Bt;
    ImageView img_BtIcon;
    TextView tv_pairedDev, tv_BtStatus;
    ProgressBar pb_BleScan;

    private BluetoothAdapter mBtAdapter;
    private BluetoothLeScanner bleScanner;
    private Handler handler = new Handler();

    private int deviceCounter = 1;
    private Map<Integer, String> devicesDescription = new HashMap<>();
    private Map<Integer, BluetoothDevice> devices = new HashMap<>();

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                return;
            }

            // if (device.getAddress().equals("F4:04:4C:0E:7C:0D")) {}
            String text = String.format("%d - %s, %s\r\n", deviceCounter, device.getName(), device.getAddress());
            tv_pairedDev.setText(tv_pairedDev.getText().toString().concat(text));

            devices.put(deviceCounter, device);
            deviceCounter++;

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(GereateKoppeln.this, "Scan Failed", Toast.LENGTH_SHORT).show();
            
        }
    };

    //Decleration of ScanFilterArray
    private ArrayList<ScanFilter> scanFilters;
    private ScanSettings scanSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereatekoppeln);

        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);
        button_getDevices = (Button) findViewById(R.id.button_getBtDevices);
        button_btSync = (Button) findViewById(R.id.button_sync);
        switch_Bt = (Switch) findViewById(R.id.switch_BT);
        img_BtIcon = (ImageView) findViewById(R.id.iv_bluetooth);
        tv_pairedDev = (TextView) findViewById(R.id.tv_pairedDevsTV);
        tv_BtStatus = (TextView) findViewById(R.id.tv_statusBluetooth);
        pb_BleScan = (ProgressBar) findViewById(R.id.progressBarBLEscan);

        tv_pairedDev.setMovementMethod(new ScrollingMovementMethod());

        //Initialize BTadapter of Smartphone and BLEscanner
        BluetoothManager mbtManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mbtManager.getAdapter();
        bleScanner = mBtAdapter.getBluetoothLeScanner();

        //Permission Management ala https://draeger-it.blog/android-app-programmierung-bluetooth-low-energy-connection-ble/
        //Requests Bluetooth, BTAdmin, CoarseLocation Permission
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PackageManager.PERMISSION_GRANTED);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            }

        //check if bt is available or not and update status
        if (bluetoothCheck(mBtAdapter) == true) {
            switch_Bt.setChecked(true);
            switch_Bt.setText("ON");
        } else {
            switch_Bt.setText("OFF");
        }

        //Bluetooth switch Listener
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

                    //BTAdapter already enabled
                    } else {
                        Toast.makeText(GereateKoppeln.this, "Bluetooth wurde aktiviert", Toast.LENGTH_SHORT).show();
                        bluetoothCheck(mBtAdapter);
                    }
                } else {
                    Toast.makeText(GereateKoppeln.this, "Bluetooth wurde deaktiviert!", Toast.LENGTH_SHORT).show();
                    switch_Bt.setText("OFF");
                    mBtAdapter.disable();
                    img_BtIcon.setImageResource(R.drawable.ic_action_off);
                    tv_BtStatus.setText("Bluetooth nicht verfügbar oder deaktiviert");
                }
            }
        });

        //STOPPED HERE - SF for bt dev

        String devAdress = "F4:04:4C:0E:7C:0D";

        scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).build();

        ScanFilter scanFilter1 = new ScanFilter.Builder()
                .setDeviceAddress(devAdress)
                .build();
        try {
            scanFilters.add(scanFilter1);
        } catch (NullPointerException e) {
            Toast.makeText(this, "ScanFilter - NullpointerEx", Toast.LENGTH_SHORT).show();
        }
        button_getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBtAdapter.isEnabled()) {
                    //               tv_pairedDev.setText("Paired Devices");
                    if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(GereateKoppeln.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                        return;

                        //Scan is starting
                    } else {

                        resetFoundDevices();
                        Log.i(TAG, "suche nach BLE Devices - Start");
                        button_getDevices.setEnabled(false);
                        pb_BleScan.setVisibility(View.VISIBLE);

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN},REQUEST_SCAN_BT);
                                    return;
                                }

                                try {
                                    //bleScanner.startScan(bleScanCallback);
                                    bleScanner.startScan(scanFilters,scanSettings,bleScanCallback);
                                } catch (NullPointerException e) {
                                    tv_pairedDev.setText("NoProperDev");
                                }

                            }
                        });

                        //handler stops scan after predefined Scan_Interval (5000 ms)
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG,"Suche nach BLE Devices > ende");
                                button_getDevices.setEnabled(true);
                                AsyncTask.execute(new Runnable() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void run() {
                                        bleScanner.stopScan(bleScanCallback);
                                    }
                                });
                            }
                        }, SCAN_INTERVAL);


          /*            Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
                        for (BluetoothDevice device: devices) {
                            tv_pairedDev.append ("\nDevice"+device.getName()+ "," + device);
          */              }
                    } else {
                    //bluetooth is off
                    Toast.makeText(GereateKoppeln.this, "Turn on Bluetooth first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Falsche Bluetooth Funktion bis jetzt
        button_btSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(GereateKoppeln.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_DISCOVER_BT);
                    return;
                }
                if (!mBtAdapter.isDiscovering()) {
                    Toast.makeText(GereateKoppeln.this, "Starting to sync", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
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

    public void scanForDevicesByAddress (final String [] deviceAddresses) {

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


    //Part of Permission Management
    private boolean hasRequiredPermissions() {
        boolean hasBluetoothPermission = hasPermission(Manifest.permission.BLUETOOTH);
        boolean hasBluetoothAdminPermission = hasPermission(Manifest.permission.BLUETOOTH_ADMIN);
        boolean hasLocationPermission = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasBluetoothPermission && hasBluetoothAdminPermission && hasLocationPermission;
    }


    private boolean hasPermission(String permission){
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }


    private void resetFoundDevices() {
        tv_pairedDev.setText("");
        devices.clear();
        deviceCounter = 0;
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
