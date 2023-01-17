package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import static com.example.gluko_smart.GlobalVariable.*;

import java.util.HashMap;
import java.util.Map;

public class DeviceHandler {

    private Map<Integer, String> devicesDescription = new HashMap<>();
    private Map<Integer, BluetoothDevice> devices = new HashMap<>();
    private int deviceCounter = 1;



    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device) {

        devices.put(deviceCounter, device);
        devicesDescription.put(deviceCounter, device.getName());
        deviceCounter++;
    }

    public void removeDevice(int deviceId) {
        //Remove a device from the list of devices
        devices.remove(deviceId);
    }
    public void clearDevices() {
        //Remove a device from the list of devices
        devices.clear();
        devicesDescription.clear();
    }

    public void addDeviceDescription(int deviceId, String description) {
        //Add a description to a device
        devicesDescription.put(deviceId, description);
    }

    public Map<Integer, BluetoothDevice> getDevices() {
        //Return the list of devices
        return devices;
    }


    public Map<Integer, String> getDevicesDescription() {
        //Return the list of devices descriptions
        return devicesDescription;
    }
}


