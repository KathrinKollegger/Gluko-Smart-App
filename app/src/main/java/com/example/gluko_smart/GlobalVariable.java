package com.example.gluko_smart;

import java.util.UUID;

public class GlobalVariable {

    public static final String TAG_BLE = "BLE_Connection";
    public static final String TAG_GATT_SERVICES = "BLE_Services";
    public static final String TAG_GLUCOSE_SERVICE = "BLE_Services";
    public static final String TAG_FIREBASE = "FIREBASE_DB";
    public static final String TAG_FHIR = "FHIR_SERVER";

    //Glucose Meter Device information
    //private static final String MEDITOUCH2_DEVICE_ADRESS = "F4:04:4C:0E:7C:0D";

    public static  final UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_SERVICE_UUID = UUID.fromString("00001808-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_MEASUREMENT = UUID.fromString("00002a18-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_MEASUREMENT_CONTEXT = UUID.fromString("00002a34-0000-1000-8000-00805f9b34fb");
    public static final UUID GLUCOSE_FEATURE = UUID.fromString("00002a51-0000-1000-8000-00805f9b34fb");

    // Not supported from Medisana Meditouch 2 Device
    public static final UUID RECORD_ACCESS_CONTROL_POINT = UUID.fromString("00002a52-0000-1000-8000-00805f9b34fb"); //Unterstützt MediSana Gerät nicht

    //Permission Request Codes
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_DISCOVER_BT = 1;
    public static final int REQUEST_SCAN_BT = 2;
    public static final int REQUEST_LOCATION = 3;
    public static final int REQUEST_CONNECT_BT = 4;
    public static final int REQUEST_BT_PERMISSIONS = 5;
    public static final long SCAN_INTERVAL = 5000;

    public static final String FIREBASE_DB_INSTANCE = "https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app";

}
