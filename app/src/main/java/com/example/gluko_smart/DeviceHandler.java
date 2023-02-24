package com.example.gluko_smart;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import java.util.HashMap;
import java.util.Map;

public class DeviceHandler {
    //Handles Maps of found BLE-Glucose Devices

    private Map<Integer, String> devicesDescription = new HashMap<>();
    private Map<Integer, BluetoothDevice> devices = new HashMap<>();
    private int deviceCounter = 1;

    //Adds Device to Hash-Maps (List of devices)
    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device) {

        devices.put(deviceCounter, device);
        devicesDescription.put(deviceCounter, device.getName());
        deviceCounter++;
    }

    public void clearDevices() {
        //Remove a device from the list of devices
        devices.clear();
        devicesDescription.clear();
    }

    public Map<Integer, BluetoothDevice> getDevices() {
        //Return the list of devices
        return devices;
    }

    public Map<Integer, String> getDevicesDescription() {
        //Return the list of devices descriptions
        return devicesDescription;
    }

    public void removeDevice(int deviceId) {
        //Remove a device from the list of devices
        devices.remove(deviceId);
    }

    public void addDeviceDescription(int deviceId, String description) {
        //Add a description to a device
        devicesDescription.put(deviceId, description);
    }

}


