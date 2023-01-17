package com.example.gluko_smart;

public class GlobalVariable {

    public static final String TAG = "BLE_Connection";

    //Glucose Meter Device information
    //private static final String MEDITOUCH_DEVICE_ADRESS = "F4:04:4C:0E:7C:0D";

    public static final String GLUCOSE_SERVICE_UUID = "00001808-0000-1000-8000-00805f9b34fb";
    byte [] serviceData = new byte[] {
            0x18
    };

    public static final int REQUEST_BT_PERMISSIONS = 5;
    public static final int REQUEST_ENABLE_BT = 0;
    public static final int REQUEST_DISCOVER_BT = 1;
    public static final int REQUEST_SCAN_BT = 2;
    public static final int REQUEST_LOCATION =3;
    public static final int REQUEST_CONNECT_BT = 4;
    public static final long SCAN_INTERVAL = 5000;



}
