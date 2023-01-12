package com.example.gluko_smart;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class BleScanner(TextView tv_pariedDev, ScanFilter) implements BluetoothAdapter.LeScanCallback {

    public BleScanner BleScanner;

    private int deviceCounter = 1;
    private Map<Integer, BluetoothDevice> devices = new HashMap<>();


    public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                return;
            }*/


            String textDevName = String.format("%d - %s, %s\r\n", deviceCounter, device.getName(), device.getAddress());
            //tv_pairedDev.setText(tv_pairedDev.getText().toString().concat(textDevName));

            devices.put(deviceCounter, device);
            deviceCounter++;

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(GereateKoppelnActivity.this, "Scan Failed", Toast.LENGTH_SHORT).show();
            tv_pairedDev.setText("No suitable dvices can be found!");


        }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

    }
}

