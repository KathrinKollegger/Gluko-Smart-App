package com.example.gluko_smart;

import static com.example.gluko_smart.GlobalVariable.*;
import static com.example.gluko_smart.BluetoothHandler.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Map;

public class GereateKoppelnActivity extends Activity implements BluetoothAdapter.LeScanCallback {

    //Views UI
    private Button button_homeKoppeln, button_getDevices, button_btSync;
    private Switch switch_Bt;
    private ImageView img_BtIcon;
    private TextView tv_BtStatus;
    private ListView tv_pairedDev;
    private ProgressBar pb_BleScan;

    private Handler handler = new Handler();
    private final Handler textUpdater = new Handler();

    private DeviceHandler devHandler;
    private BluetoothHandler btHandler;

    private static final String[] BLE_LEGACY_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onResume() {
        super.onResume();
        btHandler.disconnect(btHandler.getmbluetoothGatt());
    }

    @Override
    protected void onStart() {
        super.onStart();
        btHandler.disconnect(btHandler.getmbluetoothGatt());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gereatekoppeln);

        button_homeKoppeln = (Button) findViewById(R.id.button_homeKoppeln);
        button_getDevices = (Button) findViewById(R.id.button_getBtDevices);
        //button_btSync = (Button) findViewById(R.id.button_sync);
        switch_Bt = (Switch) findViewById(R.id.switch_BT);
        img_BtIcon = (ImageView) findViewById(R.id.iv_bluetooth);
        tv_pairedDev = (ListView) findViewById(R.id.tv_pairedDevsTV);
        //tv_pairedDev.setMovementMethod(new ScrollingMovementMethod());
        tv_BtStatus = (TextView) findViewById(R.id.tv_statusBluetooth);
        pb_BleScan = (ProgressBar) findViewById(R.id.progressBarBLEscan);

        devHandler = new DeviceHandler();
        btHandler = new BluetoothHandler(this, devHandler);
        tv_pairedDev.setAdapter(btHandler.getLeDeviceListAdapter());

        tv_pairedDev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = btHandler.getLeDeviceListAdapter().getDevice(position);
                btHandler.connectToDevice(device);

            }
        });

        //Requests Permissions depending on SDK Version if PermissionRequest is needed
        if (!hasRequiredPermissions()) {

            if (Build.VERSION.SDK_INT <= 30) {
                requestPermissions(BLE_LEGACY_PERMISSIONS, REQUEST_BT_PERMISSIONS);

            } else if (Build.VERSION.SDK_INT > 30) {
                requestPermissions(ANDROID_12_BLE_PERMISSIONS, REQUEST_BT_PERMISSIONS);
            }
        }

        //check if bt is available or not and updates switch state and text
        if (bluetoothCheck(btHandler.getmBtAdapter()) == true) {
            switch_Bt.setChecked(true);
            switch_Bt.setText("ON");
        } else {
            switch_Bt.setText("OFF");
        }

        //Bluetooth-Switch Listener (FINISHED)
        switch_Bt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_Bt.setText("ON");
                    Toast.makeText(GereateKoppelnActivity.this, "Bluetooth wird aktiviert...", Toast.LENGTH_SHORT).show();
                    //BT is disabled otherwise switch (isChecked) = false
                    if (!btHandler.getmBtAdapter().isEnabled()) {

                        //Intent to turn on Bluetooth
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                        if (Build.VERSION.SDK_INT <= 30 && ActivityCompat.checkSelfPermission(GereateKoppelnActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
                            return;
                        } else if (Build.VERSION.SDK_INT > 30 && ActivityCompat.checkSelfPermission(GereateKoppelnActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                            return;
                        }
                        startActivityForResult(intent, REQUEST_ENABLE_BT);

                        //BTAdapter already enabled
                    } else {
                        Toast.makeText(GereateKoppelnActivity.this, "Bluetooth wurde aktiviert", Toast.LENGTH_SHORT).show();
                        bluetoothCheck(btHandler.getmBtAdapter());
                    }

                } else {
                    Toast.makeText(GereateKoppelnActivity.this, "Bluetooth wurde deaktiviert!", Toast.LENGTH_SHORT).show();
                    switch_Bt.setText("OFF");
                    btHandler.getmBtAdapter().disable();
                    img_BtIcon.setImageResource(R.drawable.ic_action_off);
                    tv_BtStatus.setText("Bluetooth nicht verfügbar oder deaktiviert");
                }
            }
        });

        button_getDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btHandler.getmBtAdapter().isEnabled()) {

                    if ((Build.VERSION.SDK_INT > 30) && ActivityCompat.checkSelfPermission(GereateKoppelnActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_SCAN_BT);

                    } else if (ActivityCompat.checkSelfPermission(GereateKoppelnActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                        //here was just Bluetooth
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, REQUEST_SCAN_BT);

                        //Scan is starting
                    } else {

                        Log.i(TAG, "suche nach BLE Devices - Start");
                        button_getDevices.setEnabled(false);
                        pb_BleScan.setVisibility(View.VISIBLE);
                        resetFoundDevices();

                        //Haltet DevicesTextView up to date
                        Runnable texUpdaterRunnable = new Runnable() {
                            @Override
                            public void run() {

                                //Strukturierte Ausgabe - lineBreak
                                StringBuilder sb = new StringBuilder();
                                for (Map.Entry<Integer, String> entry : devHandler.getDevicesDescription().entrySet()) {
                                    sb.append(entry.getValue());
                                    sb.append("\n");
                                }
                                tv_pairedDev.setAdapter(btHandler.getLeDeviceListAdapter());
                                //tv_pairedDev.setText(sb.toString());
                                handler.postDelayed(this, 1000);
                            }
                        };

                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //Relative Position of nearby devices
                                if ((Build.VERSION.SDK_INT > 30) && ActivityCompat.checkSelfPermission(GereateKoppelnActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CONNECT_BT);
                                    return;
                                }

                                try {
                                    btHandler.startScanning();
                                    Log.i(TAG, "HandlerMethod startScaning");

                                } catch (NullPointerException e) {
                                    Log.i(TAG, "Scan funktioniert nicht richtig");
                                }
                                textUpdater.post(texUpdaterRunnable);
                            }
                        });


                        //handler stops scan after predefined Scan_Interval (5000 ms)
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.i(TAG, "Suche nach BLE Devices > ende");
                                button_getDevices.setEnabled(true);
                                AsyncTask.execute(new Runnable() {
                                    @SuppressLint("MissingPermission")
                                    @Override
                                    public void run() {
                                        btHandler.stopScanning();
                                        //bleScanner.stopScan(bleScanCallback);
                                        pb_BleScan.setVisibility(View.INVISIBLE);

                                    }
                                });
                            }
                        }, SCAN_INTERVAL);
                    }

                } else {
                    //bluetooth is off
                    Toast.makeText(GereateKoppelnActivity.this, "Turn on Bluetooth first", Toast.LENGTH_SHORT).show();
                }
                //tv_pairedDev.setText(devHandler.getDevicesDescription().toString());
            }
        });

        button_homeKoppeln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent6 = new Intent(GereateKoppelnActivity.this, Home.class);
                startActivity(intent6);

                btHandler.disconnect(btHandler.getmbluetoothGatt());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    //bluetooth is on
                    bluetoothCheck(btHandler.getmBtAdapter());
                    Toast.makeText(this, "Bluetooth wurde aktiviert", Toast.LENGTH_SHORT).show();
                } else {
                    //user denied/cancelled turn on
                    Toast.makeText(this, "Bluetooth Aktivierung fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    switch_Bt.setChecked(false);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_SCAN_BT) {
            // Request for Storage permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start AppPreview Activity.
                if (bluetoothCheck(btHandler.getmBtAdapter()) == true) {
                    switch_Bt.setChecked(true);
                    switch_Bt.setText("ON");
                } else {
                    switch_Bt.setText("OFF");
                }

            } else {
                // Permission request was denied.
                Toast.makeText(this, "Bluetooh Scan Perm notwendig", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Part of Permission Management - First Perm Check
    private boolean hasRequiredPermissions() {
        boolean hasBluetoothPermission = hasPermission(Manifest.permission.BLUETOOTH);
        boolean hasBluetoothAdminPermission = hasPermission(Manifest.permission.BLUETOOTH_ADMIN);
        boolean hasFineLocationPermission = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean hasLocationPermissionBackground = hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        boolean hasCoarseLoactionPermission = hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        boolean hasBluetoothScanPermission = hasPermission(Manifest.permission.BLUETOOTH_SCAN);
        boolean hasBluetoothConnectPermission = hasPermission(Manifest.permission.BLUETOOTH_CONNECT);

        //true if all permissions are granted, false otherwise
        return (hasBluetoothPermission &&
                hasBluetoothAdminPermission &&
                hasFineLocationPermission &&
                hasLocationPermissionBackground) ||
                (hasFineLocationPermission &&
                        hasBluetoothScanPermission &&
                        hasBluetoothConnectPermission);
    }

    //checks for single perission if granted
    private boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;

    }

    //clears DeviceList and related View
    private void resetFoundDevices() {
        //tv_pairedDev.clear;
        devHandler.clearDevices();

    }

    //checks if BT is turned on and updates related Views
    public boolean bluetoothCheck(BluetoothAdapter mBtAdapter) {
        if (mBtAdapter != null && mBtAdapter.isEnabled()) {
            tv_BtStatus.setText("Bluetooth ist verfügbar und aktiviert");
            img_BtIcon.setImageResource(R.drawable.ic_action_on);
            return true;
        } else {
            tv_BtStatus.setText("Bluetooth nicht verfügbar oder deaktiviert");
            img_BtIcon.setImageResource(R.drawable.ic_action_off);

        }
        return false;
    }

    public static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        //EMPTY
    }

}
