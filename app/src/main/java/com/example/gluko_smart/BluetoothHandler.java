package com.example.gluko_smart;

import static android.content.Context.BLUETOOTH_SERVICE;

import static com.example.gluko_smart.GlobalVariable.*;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BluetoothHandler {
    private static final String TAG_NOCONNECT = "Verbindung nicht möglich";
    private static final String TAG_CONNECTING = "Verbindugsaufbau";
    private Context context;
    private BluetoothAdapter mBtAdapter;
    private BluetoothLeScanner bleScanner;
    private Handler handler;
    private DeviceHandler deviceHandler;
    public LeDeviceListAdapter leDeviceListAdapter ;
    private BluetoothGatt mbluetoothGatt;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public BluetoothHandler(Context context, DeviceHandler devHandler) {
        //Initialize BTadapter of Smartphone and BLEscanner
        this.context = context;
        BluetoothManager mbtManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mbtManager.getAdapter();
        bleScanner = mBtAdapter.getBluetoothLeScanner();
        handler = new Handler();
        this.deviceHandler = devHandler;

    }

    @SuppressLint("MissingPermission")
    public void startScanning() {
        //Start scanning for devices
        //ScanFilter Implementation
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter1 = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(GLUCOSE_SERVICE_UUID))
                .build();
        scanFilters.add(scanFilter1);

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        //bleScanner.startScan(bleScanCallback);
        Log.i(TAG,"Scan has actually started!");
        leDeviceListAdapter = new LeDeviceListAdapter(context);
        bleScanner.startScan(scanFilters, scanSettings, bleScanCallback);
    }

    @SuppressLint("MissingPermission")
    public void stopScanning() {
        //Stop scanning for devices
        bleScanner.stopScan(bleScanCallback);
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> getPairedDevices() {
        //Return a list of paired devices
        return (List<BluetoothDevice>) mBtAdapter.getBondedDevices();
    }

    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device) {
        if (device == null) {
            Log.w(TAG_NOCONNECT, "Device not found.  Unable to connect.");
            return;
        }

        //connect to device
        mbluetoothGatt = device.connectGatt(context, false, mGattCallback);
        Log.d(TAG_CONNECTING, "Trying to create a new connection.");
        Toast.makeText(context, "Verbindung wird hergestellt", Toast.LENGTH_SHORT).show();

    }

    public BluetoothGattCallback getmGattCallback() {
        return mGattCallback;
    }

    public BluetoothGatt getmbluetoothGatt() {
        return mbluetoothGatt;
    }

    @SuppressLint("MissingPermission")
    public void disconnect(BluetoothGatt mBluetoothGatt) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }




    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();

            //checking for duplicate devices
            if (!deviceHandler.getDevices().containsValue(device)) {
                deviceHandler.addDevice(device);

                if (!leDeviceListAdapter.getDevices().contains(device)){
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                    leDeviceListAdapter.addDevice(device);
                    Log.i(TAG,"Dev added to leDevAdapter");
                }
                /*//Variante ohne Klick auf Device
                device.connectGatt(context,true, mGattCallback);*/
            }
        }

        ;
    };

    //Wird dann für die Connection benötigt
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {

                gatt.discoverServices();


            }
            if (newState == BluetoothProfile.STATE_CONNECTING) {
                gatt.discoverServices();
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.disconnect();
            }

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                //All Services
                List<BluetoothGattService> gattServices;
                gattServices = gatt.getServices();
                for (BluetoothGattService gattService : gattServices) {
                    String uuid = gattService.getUuid().toString();
                    Log.d("Services", "UUID: " + uuid);
                }

                //Glucose-Service discovered
                BluetoothGattService glucoseService =
                        gatt.getService(GLUCOSE_SERVICE_UUID);
                if (glucoseService != null) {
                    Log.d("GlucsoeService", "discovered");

                } else {
                    Log.d("Glucoservice", "notFound");
                }
                //CUSTOM
                //readCharacteristics(glucoseService);

                List<BluetoothGattCharacteristic> glucoCharakters;
                glucoCharakters = glucoseService.getCharacteristics();


                for (BluetoothGattCharacteristic characteristic : glucoCharakters) {
                    if (GLUCOSE_MEASUREMENT.equals(characteristic.getUuid())) {


                    if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        // characteristic is readable
                        gatt.readCharacteristic(characteristic);
                        Log.i("CharacterProps"+characteristic.getUuid(),"="+characteristic.getProperties());
                        }
                    }
                }
                  /*  String uuid = gattCharacteristic.getUuid().toString();
                    byte[] value = gattCharacteristic.getValue();
                    BluetoothGattDescriptor descriptor = gattCharacteristic.getDescriptor(gattCharacteristic.getUuid());
                    Log.d("GlucoCharakters", "UUID: " + uuid);
                    //Log.d("Descriptor", "UUID: " + descriptor);
                    Log.d("ValueInByte",":" + Arrays.toString(value));
                    //STOPPED HERE
                    //Get the GLUCOSE Measurement Charactersitics
                    BluetoothGattCharacteristic GLUCOSE_MEASUREMENT_Characteristic =
                            gatt.getService(GLUCOSE_SERVICE_UUID).getCharacteristic(GLUCOSE_MEASUREMENT);

                    gatt.readCharacteristic(GLUCOSE_MEASUREMENT_Characteristic);*/



            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("OnCharacterRead","entered"+characteristic.getUuid().toString());

               /* byte [] charakterValue = characteristic.getValue();
                Log.d("CharakterValue", "is" + Arrays.toString(charakterValue));*/

                if (GLUCOSE_MEASUREMENT.equals(characteristic.getUuid())) {
                    Log.i("GlucoMeasureTrue","entered");
                    byte[] glucoseValueBytes = characteristic.getValue();
                    int flags = glucoseValueBytes[0];
                    boolean timeOffsetPresent = (flags & 0x01) > 0;
                    boolean typeAndSampleLocationPresent = (flags & 0x02) > 0;
                    //is true if mol/L and false if kg/L
                    boolean concentrationUnits = (flags & 0x03) > 0; // 0 = kg/L, 1 = mol/L

                    int offset = 1;
                    if (timeOffsetPresent) {
                        // Time offset is a 2-byte value
                        int timeOffset = (glucoseValueBytes[offset] & 0xff)
                                + ((glucoseValueBytes[offset + 1] & 0xff) << 8);
                        offset += 2;
                    }

                    // Glucose concentration is a 2 or 3 byte value
                    int glucoseConcentration = (glucoseValueBytes[offset] & 0xff)
                            + ((glucoseValueBytes[offset + 1] & 0xff) << 8);
                    if (glucoseValueBytes.length > offset + 2) {
                        glucoseConcentration += (glucoseValueBytes[offset + 2] & 0xff) << 16;
                    }
                    offset += 2;

                    if (typeAndSampleLocationPresent) {
                        int typeAndSampleLocation = glucoseValueBytes[offset] & 0xff;
                        offset += 1;
                    }

                    if (concentrationUnits) {
                        // concentration is in mol/L
                    } else {
                        // concentration is in kg/L
                    }

                    Log.d(TAG, "Glucose concentration: " + glucoseConcentration);
                }

                /*if (GLUCOSE_MEASUREMENT.equals(characteristic.getUuid())) {
                    byte[] glucoseValue = characteristic.getValue();
                    Log.d("CharakterValue:",Arrays.toString(glucoseValue));
                    int offset = 0;

                    // Extract the flags field
                    int flags = glucoseValue[offset] & 0xFF;
                    offset++;

                    // Extract the sequence number
                    int sequenceNumber = (glucoseValue[offset] & 0xFF) << 8 | (glucoseValue[offset + 1] & 0xFF);
                    offset += 2;

                    // Extract the timestamp
                    long timestamp = ((glucoseValue[offset] & 0xFF) << 24) | ((glucoseValue[offset + 1] & 0xFF) << 16) |
                            ((glucoseValue[offset + 2] & 0xFF) << 8) | (glucoseValue[offset + 3] & 0xFF);
                    offset += 4;

                    // Extract the glucose concentration
                    int glucoseConcentration = (glucoseValue[offset] & 0xFF) << 8 | (glucoseValue[offset + 1] & 0xFF);
                    offset += 2;

                    // Extract the glucose concentration units
                    int glucoseConcentrationUnits = (glucoseValue[offset] & 0xFF);
                    offset++;

                    Log.d("Glucose Measurement:", "Sequence Number: " + sequenceNumber + ", Timestamp: " + timestamp +
                            ", Glucose Concentration: " + glucoseConcentration + " " + glucoseConcentrationUnits + " mg/dL");
                }*/

            }
            gatt.readCharacteristic(characteristic);
        }
    };



    @SuppressLint("MissingPermission")
    private void readCharacteristics(BluetoothGattService glucoService) {
        List<BluetoothGattCharacteristic> glucoServiceCharakters = glucoService.getCharacteristics();
        for (BluetoothGattCharacteristic characteristic : glucoServiceCharakters) {
            mbluetoothGatt.readCharacteristic(characteristic);

        }
    }

    private void broadcastUpdate(String intentAction) {
        final Intent intent = new Intent(intentAction);

    }

    public double parseGlucoseValue ( byte[] glucoseValue){
                // extract glucose measurement flags
                int offset = 0;
                int flags = glucoseValue[offset] & 0xFF;
                boolean timeOffsetPresent = (flags & 0x01) > 0;
                boolean typeAndLocationPresent = (flags & 0x02) > 0;
                boolean sensorStatusAnnunciationPresent = (flags & 0x04) > 0;
                boolean contextInfoFollows = (flags & 0x08) > 0;

                // extract glucose concentration
                offset++;
                int concentration = (glucoseValue[offset] & 0xFF)
                        | ((glucoseValue[offset + 1] & 0xFF) << 8);
                double glucose = concentration * 0.1;

                // extract glucose measurement date and time
                offset += 2;
                if (timeOffsetPresent) {
                    // time offset is present, extract it
                    int timeOffset = (glucoseValue[offset] & 0xFF)
                            | ((glucoseValue[offset + 1] & 0xFF) << 8);
                }

                // extract type and location
                offset += 2;
                if (typeAndLocationPresent) {
                    // type and location are present, extract them
                    int typeAndLocation = glucoseValue[offset] & 0xFF;
                }

                // extract sensor status annunciation
                offset++;
                if (sensorStatusAnnunciationPresent) {
                    // sensor status annunciation is present, extract it
                    int sensorStatusAnnunciation = (glucoseValue[offset] & 0xFF)
                            | ((glucoseValue[offset + 1] & 0xFF) << 8);
                }
                return glucose;

            }
            public BluetoothAdapter getmBtAdapter () {
                return mBtAdapter;
            }

            public LeDeviceListAdapter getLeDeviceListAdapter () {
                return leDeviceListAdapter;
            }

            public class LeDeviceListAdapter extends BaseAdapter {
                private ArrayList<BluetoothDevice> mLeDevices;
                private LayoutInflater mInflator;

                public LeDeviceListAdapter(Context context) {
                    super();
                    mLeDevices = new ArrayList<BluetoothDevice>();
                    mInflator = LayoutInflater.from(context);
                }

                public void addDevice(BluetoothDevice device) {
                    if (!mLeDevices.contains(device)) {
                        mLeDevices.add(device);
                    }
                }

                public ArrayList getDevices() {
                    return mLeDevices;
                }

                public void clear() {
                    mLeDevices.clear();
                }

                @Override
                public int getCount() {
                    return mLeDevices.size();
                }

                @Override
                public Object getItem(int i) {
                    return mLeDevices.get(i);
                }

                @Override
                public long getItemId(int i) {
                    return i;
                }

                @Override
                public View getView(int i, View view, ViewGroup viewGroup) {
                    //Inflate the layout for the listview and set the device name as the text
                    GereateKoppelnActivity.ViewHolder viewHolder;
                    if (view == null) {
                        view = mInflator.inflate(R.layout.listitem_device, null);
                        viewHolder = new GereateKoppelnActivity.ViewHolder();
                        viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                        viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                        view.setTag(viewHolder);
                    } else {
                        viewHolder = (GereateKoppelnActivity.ViewHolder) view.getTag();
                    }


                    BluetoothDevice device = mLeDevices.get(i);
                    @SuppressLint("MissingPermission") final String deviceName = device.getName();
                    if (deviceName != null && deviceName.length() > 0)
                        viewHolder.deviceName.setText(deviceName);
                    else
                        viewHolder.deviceName.setText("Unknown device");

                    return view;
                }

                public BluetoothDevice getDevice(int position) {
                    return mLeDevices.get(position);
                }
            }



    }
