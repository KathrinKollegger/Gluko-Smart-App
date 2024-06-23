package com.example.gluko_smart;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.example.gluko_smart.GlobalVariable.CLIENT_CHARACTERISTIC_CONFIG;
import static com.example.gluko_smart.GlobalVariable.FIREBASE_DB_INSTANCE;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_MEASUREMENT;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_MEASUREMENT_CONTEXT;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_SERVICE_UUID;
import static com.example.gluko_smart.GlobalVariable.TAG_BLE;
import static com.example.gluko_smart.GlobalVariable.TAG_FIREBASE;
import static com.example.gluko_smart.GlobalVariable.TAG_GATT_SERVICES;
import static com.example.gluko_smart.GlobalVariable.TAG_GLUCOSE_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BluetoothHandler {
    //Handles the BLE Connection with Glucose-Meter

    private Context context;
    private BluetoothAdapter mBtAdapter;
    private BluetoothLeScanner bleScanner;
    private Handler handler;
    private DeviceHandler deviceHandler;
    public LeDeviceListAdapter leDeviceListAdapter;

    //public API for Bluetooth GATT-Profile
    private BluetoothGatt mbluetoothGatt;
    private BluetoothGattService glucoseService;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    //saves received values from onCharacteristicChanged
    public List<Object> dataList = new ArrayList<>();

    public BluetoothHandler(Context context, DeviceHandler devHandler) {
        //Initialize Bluetooth adapter of Smartphone and BLE Scanner
        this.context = context;
        this.deviceHandler = devHandler;
        BluetoothManager mbtManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = mbtManager.getAdapter();
        bleScanner = mBtAdapter.getBluetoothLeScanner();
        handler = new Handler();
    }

    //Start scanning for devices
    @SuppressLint("MissingPermission")
    public void startScanning() {

        //ScanFilter Implementation - filter on Devices providing Glucose Service
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter1 = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(GLUCOSE_SERVICE_UUID))
                .build();
        scanFilters.add(scanFilter1);

        //ScanSettings Implementation - set ScanMode to Low Latency
        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        //Adapter for List of BLE Devices
        leDeviceListAdapter = new LeDeviceListAdapter(context);

        Log.i(TAG_BLE, "Scanning for Devices");

        //starts actual BLE-Scan for nearby devices
        bleScanner.startScan(scanFilters, scanSettings, bleScanCallback);
    }

    //Stop scanning for devices
    @SuppressLint("MissingPermission")
    public void stopScanning() {
        bleScanner.stopScan(bleScanCallback);
    }

    //connect to device
    @SuppressLint("MissingPermission")
    public void connectToDevice(BluetoothDevice device) {
        if (device == null) {
            Log.w(TAG_BLE, "Device not found.  Unable to connect.");
            return;
        }
        Log.i(TAG_BLE, "Trying to create a new connection.");
        mbluetoothGatt = device.connectGatt(context, false, mGattCallback);

        Toast.makeText(context, R.string.ConnectionInProgress, Toast.LENGTH_SHORT).show();
    }

    //break BLE-Connection
    @SuppressLint("MissingPermission")
    public void disconnect(BluetoothGatt mBluetoothGatt) {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            //mBluetoothGatt = null;
        }
    }

    //Scan Results is handled in following ScanCallback
    private final ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            BluetoothDevice device = result.getDevice();

            //checking for duplicate devices
            if (!deviceHandler.getDevices().containsValue(device)) {
                deviceHandler.addDevice(device);

                if (!leDeviceListAdapter.getDevices().contains(device)) {
                    leDeviceListAdapter.addDevice(device);
                    leDeviceListAdapter.notifyDataSetChanged();
                    Log.i(TAG_BLE, "Device added to DevHandler and list Adapter");
                }
            }
        }
    };

    //Connection with GATT-Server (Glucose Meter) is handled ind BluetoothGattCallback
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        //Client (App) connects to GATT-Server and discovers available Services
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == STATE_CONNECTED) { gatt.discoverServices(); }

            if (newState == STATE_CONNECTING) { gatt.discoverServices(); }

            if (newState == STATE_DISCONNECTED) { gatt.disconnect(); }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {

                //Get all gatt-Services
                List<BluetoothGattService> gattServices;
                gattServices = gatt.getServices();

                //Create Log message for each GATT-Service
                for (BluetoothGattService gattService : gattServices) {
                    String uuid = gattService.getUuid().toString();
                    Log.i(TAG_GATT_SERVICES, "Service UUID: " + uuid);
                }

                //Get the Glucose Service
                glucoseService =
                        gatt.getService(GLUCOSE_SERVICE_UUID);

                //Glucose Service found
                if (glucoseService != null) {

                    Log.i(TAG_GATT_SERVICES, "Glucose Service discovered");

                    //Get Characteristics from Glucose Service
                    List<BluetoothGattCharacteristic> allGlucoCharacteristics = glucoseService.getCharacteristics();

                    //writes all Characteristic UUIDs of Glucose Service into Log
                    for (BluetoothGattCharacteristic characteristic : allGlucoCharacteristics) {
                        Log.d(TAG_GLUCOSE_SERVICE, "Characteristic UUID: " + characteristic.getUuid());
                        Log.d(TAG_GLUCOSE_SERVICE, "Characteristic Properties: " + characteristic.getProperties());
                    }

                    if (allGlucoCharacteristics.isEmpty()) {
                        Log.d(TAG_GLUCOSE_SERVICE, "No characterisitcs for service: " + GLUCOSE_SERVICE_UUID);
                    }

                } else {
                    Log.i(TAG_GATT_SERVICES, "Glucose Service notFound");
                    return;
                }

                //Deal with Characteristics of Glucose Service

                //Get the Glucose_Measurement characteristic
                BluetoothGattCharacteristic glucoCharacterMeasurement =
                        glucoseService.getCharacteristic(GLUCOSE_MEASUREMENT);

                // Get the Glucose_Measurement_Context characteristic
                BluetoothGattCharacteristic glucoCharacterContext =
                        glucoseService.getCharacteristic(GLUCOSE_MEASUREMENT_CONTEXT);


                //Enable notification for the characteristic because it is being read via notify -> triggers onCharacteristicChanged
                gatt.setCharacteristicNotification(glucoCharacterMeasurement, true);
                gatt.setCharacteristicNotification(glucoCharacterContext, true);

                //Descriptor glucoseMeasurementDescriptor
                BluetoothGattDescriptor glucoseMeasurementDescriptor =
                        glucoCharacterMeasurement.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);

                if (glucoseMeasurementDescriptor != null) {
                    glucoseMeasurementDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(glucoseMeasurementDescriptor);
                    gatt.readDescriptor(glucoseMeasurementDescriptor);
                    Log.d(TAG_GLUCOSE_SERVICE, "Reading/Writing descriptor for characteristic: " + glucoCharacterMeasurement.getUuid());

                } else {
                    Log.d(TAG_GLUCOSE_SERVICE, "Descriptor not found for characteristic: " + glucoCharacterMeasurement.getUuid());
                }
                //Descriptor glucoseMeasurementContextDescriptor
                BluetoothGattDescriptor measurementContextDescriptor =
                        glucoCharacterContext.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);

                //Toast to confirm successful Connection
                handler.postDelayed(() -> Toast.makeText(context, R.string.ConnectionSuccessful, Toast.LENGTH_SHORT).show(), 5000);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG_GLUCOSE_SERVICE, "onCharaRead entered");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG_GLUCOSE_SERVICE, "Descriptor write successful for " + descriptor.getCharacteristic().getUuid());
                if (descriptor.getCharacteristic().getUuid().equals(GLUCOSE_MEASUREMENT)) {
                    // Now write the descriptor for the next characteristic
                    BluetoothGattCharacteristic glucoCharacterContext =
                            glucoseService.getCharacteristic(GLUCOSE_MEASUREMENT_CONTEXT);
                    BluetoothGattDescriptor measurementContextDescriptor =
                            glucoCharacterContext.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                    if (measurementContextDescriptor != null) {
                        measurementContextDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        if (!gatt.writeDescriptor(measurementContextDescriptor)) {
                            Log.e(TAG_GLUCOSE_SERVICE, "Failed to write descriptor for Glucose Measurement Context");
                        } else {
                            Log.d(TAG_GLUCOSE_SERVICE, "Writing descriptor for Glucose Measurement Context: " + glucoCharacterContext.getUuid());
                        }
                    }
                }
            } else {
                Log.e(TAG_GLUCOSE_SERVICE, "Failed to write descriptor for " + descriptor.getCharacteristic().getUuid());
            }

        }


        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            //checking the Character UUID received by the method --> only first CharacterUUID
            Log.d("CharacteristicChanged", "Characteristic UUID: " + characteristic.getUuid().toString());

            //check if the characteristic that has changed is Glucose Measurement
            if (characteristic.getUuid().equals(GLUCOSE_MEASUREMENT)) {

                //store value of Characteristic onto Byte
                byte[] dataMeasurement = characteristic.getValue();
                Log.d("CharacteristicChanged", "Data Measurement: " + Arrays.toString(dataMeasurement));
                processData(characteristic, dataMeasurement);

                //client must receive this Characteristic for Confirmation of Data transfer on Glucose Meter
                //pre and post food intake would be handled in Glucose Measurement Context

            } else {
                Log.d(TAG_GATT_SERVICES, "GLUCOSE_MEASUREMENT nicht gefunden");
            }


            if (characteristic.getUuid().equals(GLUCOSE_MEASUREMENT_CONTEXT)) {

                byte[] dataMeasurementContext = characteristic.getValue();
                Log.d("CharacteristicChanged", "Characteristic UUID" + characteristic.getUuid().toString());
                Log.d("CharacteristicChanged", "Data Measurement Context: " + Arrays.toString(dataMeasurementContext));

            } else {
                Log.d("onCharacteristicChanged", "GLUCOSE_MEASUREMENT_CONTEXT not found");
            }
        }

    };

    /**
     * processes the BLE Measurement data from the Measurement Characteristic
     * Prints array in the log
     * <p>
     * Example of dataMeasurement: [31, 1, 0, -23, 7, 1, 21, 18, 22, 0, 0, 0, 46, -64, 17, 0, 0]
     *
     *
     * @param characteristic  the changed BLE-Service Characteristic
     * @param dataMeasurement the value of the changed BLE-Characteristic as byte
     *
     *Source: https://stackoverflow.com/questions/45568958/bluetooth-low-energy-glucose-gatt-profile-measurement-parsing-value
     */
    @SuppressLint("DefaultLocale")
    public void processData(BluetoothGattCharacteristic characteristic, byte[] dataMeasurement) {

        /*boolean timeOffsetPresent = (dataMeasurement[0] & 0x01) > 0;
        boolean typeAndLocationPresent = (dataMeasurement[0] & 0x02) > 0;
        boolean sensorStatusAnnunciationPresent = (dataMeasurement[0] & 0x08) > 0;*/
        boolean contextInfoFollows = (dataMeasurement[0] & 0x10) == 0x10;
        String concentrationUnit = (dataMeasurement[0] & 0x04) > 0 ? "mol/l" : "kg/L";

        if (contextInfoFollows) {
            Log.i("BLE", "Context information will follow this measurement.");
        } else {
            Log.i("BLE", "No context information follows this measurement.");
        }

        //seqNumber of Entry in Device Diary
        int seqNum = (int) (dataMeasurement[1] & 255);
        seqNum |= (int) (dataMeasurement[2] & 255) << 8;

        //Blood Glucose Value as float
        //1 mol/L = 1000 mmol/L. --> convert mol/L to mmol/L
        float glucose = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 12);

        //Glucose Value in mmol/L as float
        float glucoseMMOL = glucose * 1000;

        //Glucose Value in mg/dl as float
        float glucoseMGDL = glucose * 100000;

        //Handling Date and Time of Measurement
        int year1 = dataMeasurement[3] & 255;
        int year2 = dataMeasurement[4] & 255;
        int year = year1 | (year2 << 8);

        String yearStr = String.format("%04d", year);
        String month = String.format("%02d", dataMeasurement[5]);
        String day = String.format("%02d", dataMeasurement[6]);
        String hour = String.format("%02d", dataMeasurement[7]);
        String min = String.format("%02d", dataMeasurement[8]);
        String sec = String.format("%02d", dataMeasurement[9]);

        //Date of Measurement in ISO-Timestamp Format
        String timestamp = yearStr + "-" + month + "-" + day + "T" + hour + ":" + min + ":" + sec;

        //store received Entries in dataList Object
        dataList.add(new Object[]{seqNum, glucose, glucoseMMOL, glucoseMGDL, year, month, day, hour, min, sec});

        //get Firebase Instance of current User
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance(FIREBASE_DB_INSTANCE);
        DatabaseReference myRef = database.getReference("users/" + userId).child("GlucoseValues");

        //Create Database Entries for received Measurements
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild((timestamp))) {
                    myRef.child(timestamp).child("bzWert").setValue(glucoseMGDL);
                    myRef.child(timestamp).child("timestamp").setValue(timestamp);
                    myRef.child(timestamp).child("key").setValue(myRef.push().getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG_FIREBASE,"ListenerFSVE failed: " + databaseError.toString());
            }
        });

        //CONTROL
        //Latest Measurement Values are being wrote into Log Message
        Log.d("Values of GLUCOSE_MEASUREMENT", " seqNr: "
                + seqNum + " Datum: " + timestamp +
                " glucose: " + glucose + " Einheit: "
                + concentrationUnit + " Wert in mg/dl: " + glucoseMGDL);
    }

    //Inner Class for List Adapter for found BLE Devices
    public static class LeDeviceListAdapter extends BaseAdapter {
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

            //Show Device Name(s) in Device List
            BluetoothDevice device = mLeDevices.get(i);
            @SuppressLint("MissingPermission") final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText(R.string.unknownDevice);
            }
            return view;
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }
    }

    public BluetoothGatt getmbluetoothGatt() {
        return mbluetoothGatt;
    }

    public BluetoothAdapter getmBtAdapter() {
        return mBtAdapter;
    }

    public LeDeviceListAdapter getLeDeviceListAdapter() {
        return leDeviceListAdapter;
    }

    public BluetoothGattCallback getmGattCallback() {
        return mGattCallback;
    }

    @SuppressLint("MissingPermission")
    public List<BluetoothDevice> getPairedDevices() {
        //Return a list of paired devices
        return (List<BluetoothDevice>) mBtAdapter.getBondedDevices();
    }


}
