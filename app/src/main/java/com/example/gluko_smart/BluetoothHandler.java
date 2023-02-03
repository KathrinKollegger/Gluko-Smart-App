package com.example.gluko_smart;

import static android.content.Context.BLUETOOTH_SERVICE;
import static com.example.gluko_smart.GlobalVariable.CLIENT_CHARACTERISTIC_CONFIG;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_MEASUREMENT;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_MEASUREMENT_CONTEXT;
import static com.example.gluko_smart.GlobalVariable.GLUCOSE_SERVICE_UUID;
import static com.example.gluko_smart.GlobalVariable.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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

    //um empfangenen Wert aus onCharacterisitcChanged zu speichern
    public List<Object> dataList = new ArrayList<>();

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
                    //leDeviceListAdapter.addDevice(device);
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

                //Get the Glucose Service
                BluetoothGattService glucoseService =
                        gatt.getService(GLUCOSE_SERVICE_UUID);
                if (glucoseService != null) {
                    Log.d("GlucoseService", "discovered"); // er geht da rein
                    List<BluetoothGattCharacteristic> allCharacterisitksGluco = glucoseService.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : allCharacterisitksGluco) {
                        Log.d("Characteristic", "UUID: " + characteristic.getUuid());
                    }


                    if (allCharacterisitksGluco.isEmpty()) {
                        Log.d("Characterisitcs", "Keine characterisitcs for this service" + GLUCOSE_SERVICE_UUID);
                    }

                } else {
                    Log.d("Glucoservice", "notFound");
                    return;
                }

                //Get the Glucose_Measurement and Glucose_Measurement_Context characteristics
                BluetoothGattCharacteristic glucoCharakterMeasurement = glucoseService.getCharacteristic(GLUCOSE_MEASUREMENT);

                //Enable notifications for the characteristics because they are only notify
                gatt.setCharacteristicNotification(glucoCharakterMeasurement, true);

                //ERROR MEDISANA MEDITOUCH 2
                //gatt.readCharacteristic(glucoCharakterMeasurement);

                //Descriptor glucoseMeasurementDescriptor
                BluetoothGattDescriptor glucoseMeasurementDescriptor = glucoCharakterMeasurement.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                if (glucoseMeasurementDescriptor != null) {
                    glucoseMeasurementDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(glucoseMeasurementDescriptor);
                    gatt.readDescriptor(glucoseMeasurementDescriptor);
                    gatt.notify();
                    Log.d("Descriptor", "Reading/Writing descriptor for characteristic: " + glucoCharakterMeasurement.getUuid());

                }else{
                    Log.d("Descriptor", "Descriptor not found for characteristic: " + glucoCharakterMeasurement.getUuid());
                }

            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("onCharaRead","entered");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic
                characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            //um zu schauen welche UUID die methode bekommt --> bekommt immer nur 1
            Log.d("onCharacteristicChanged", " In Methode empfangende UUID: " + characteristic.getUuid().toString());

            //check if the characteristic that has changed is the one you are interested in
            //da geht er rein und gibt des erste log richtig aus
            if (characteristic.getUuid().equals(GLUCOSE_MEASUREMENT)) {
                byte[] dataMeasurement = characteristic.getValue();
                //Methode processData aufrufen
                processData(characteristic, dataMeasurement);

                    //GATT Server sagen, dass ich den  ersten wert empfangen habe
                    //Hier kann man dem GATT Server signalisieren, dass man
                    // bereit für die nächste Characteristic ist und den Descriptior der nächsten aufrufen!
                    BluetoothGattCharacteristic glucoCharakterContext = gatt.getService(GLUCOSE_SERVICE_UUID).
                            getCharacteristic(GLUCOSE_MEASUREMENT_CONTEXT);

                    gatt.setCharacteristicNotification(glucoCharakterContext, true);

                    //Descriptor glucoseMeasurementContextDescriptor
                    BluetoothGattDescriptor glucoseMeasurementContextDescriptor = glucoCharakterContext.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                    if (glucoseMeasurementContextDescriptor != null) {
                        glucoseMeasurementContextDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(glucoseMeasurementContextDescriptor);
                        gatt.readDescriptor(glucoseMeasurementContextDescriptor);

                        // Log.d("Descriptor", "Reading/Writing descriptor for characteristic: " + glucoCharakterContext.getUuid());
                    } else {
                        Log.d("Descriptor", "Descriptor not found for characteristic: " + glucoCharakterContext.getUuid());
                    }

            } else {
                Log.d("onCharacteristicChanged", "GLUCOSE_MEASUREMENT nicht gefunden");
            }

            //müssen wir aber bekommen, damit Gerät OK zurückliefert
            //bekommen es müssen es aba nd auswerten --> nur wenn wir vor/nach essen wollen
            if (characteristic.getUuid().equals(GLUCOSE_MEASUREMENT_CONTEXT)) {

                byte[] dataMeasurementContext = characteristic.getValue();
                Log.d("onCharacteristicChanged", "GLUCOSE_MEASUREMENT_CONTEXT wurde gefunden");

            } else {
                Log.d("onCharacteristicChanged", "GLUCOSE_MEASUREMENT_CONTEXT nicht gefunden");
            }
        }
    };



            public void processData(BluetoothGattCharacteristic characteristic, byte[] dataMeasurement){
                //process the data here
                //Print the array in the log
                //D/DatenMeasurement: [31, 1, 0, -23, 7, 1, 21, 18, 22, 0, 0, 0, 46, -64, 17, 0, 0]
                //Log.d("DatenMeasurement", Arrays.toString(dataMeasurement));
                //Log.d("onCharacteristicChanged", "GLUCOSE_MEASUREMENT has changed");

                //extract the data from the characteristic
                //https://stackoverflow.com/questions/45568958/bluetooth-low-energy-glucose-gatt-profile-measurement-parsing-value
                boolean timeOffsetPresent = (dataMeasurement[0] & 0x01) > 0;
                boolean typeAndLocationPresent = (dataMeasurement[0] & 0x02) > 0;
                String concentrationUnit = (dataMeasurement[0] & 0x04) > 0 ? "mol/l" : "kg/L";
                boolean sensorStatusAnnunciationPresent = (dataMeasurement[0] & 0x08) > 0;
                boolean contextInfoFollows = (dataMeasurement[0] & 0x10) > 0;

                //seqNumber des Eintrags im Gerätetagebuch
                int seqNum = (int) (dataMeasurement[1] & 255);
                seqNum |= (int) (dataMeasurement[2] & 255) << 8;

                //1 mol/L = 1000 mmol/L. --> um mol/L in mmol/L umzurechnen
                float glucose = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 12);

                //wenn das Gerät in mol/liter liefert und wir in mmol/liter umrechnen diese variable verwenden
                float glucoseMMOL = glucose * 1000;
                //wenn das Gerät in kg/l liefert und wir in mg/dl umrechnen diese variable verwenden
                float glucoseMGDL= glucose*100000;

                //glucoseMGDL in int parsen!! um es übergebne zu Können --> Regina Kathrin
                // ListView/Adapter mit Einträgen erstellen wo dann jeder einzelne exportiert werden kann und Button Erstellen--> Regina Kathrin
                // Timestamp richtig---> Regina Kathrin
                // Kontrollmeachnismus doppelte einträge wenn von Gerät gesendet !! --> Regina Kathrin

                int year1 = dataMeasurement[3] & 255;
                int year2 = dataMeasurement[4] & 255;
                int year = year1 | (year2 << 8);
                String yearStr = String.format("%04d", year);
                String month = String.format("%02d", dataMeasurement[5]);
                String day = String.format("%02d", dataMeasurement[6]);
                String hour = String.format("%02d", dataMeasurement[7]);
                String min = String.format("%02d", dataMeasurement[8]);
                String sec = String.format("%02d", dataMeasurement[9]);

                String timestamp = yearStr + "-" + month + "-" + day + "T" + hour + ":" + min + ":" + sec;


                //erhaltene Werte in dataList Objekt speichern
                dataList.add(new Object[]{seqNum, glucose, glucoseMMOL,glucoseMGDL,year,month,day,hour,min,sec});

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = currentUser.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://gluko-smart-default-rtdb.europe-west1.firebasedatabase.app");
                DatabaseReference myRef = database.getReference("users/" +userId).child(GlucoseValues.class.getSimpleName());;

                /*Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                calendar.set(Calendar.SECOND, sec);*/

                //Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.hasChild((timestamp))) {
                            myRef.child((timestamp)).child("bzWert").setValue(glucoseMGDL);
                            myRef.child((timestamp)).child("timestamp").setValue(timestamp);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle errors
                    }
                });


                //Werte die oben sind werden in log ausgegeben --> es wird nur der aktuellste Wert ausgegeben in mol/L
                Log.d("Werte von GLUCOSE_MEASUREMENT", " seqNr: "
                        + seqNum + " Datum: " + yearStr + " " + month + " " + day + " "
                        + hour + " " + min + " " + sec +
                        " glucose: " + glucose + " Einheit: "
                        + concentrationUnit + " Wert in mg/dl: " + glucoseMGDL);

                //Hier müsste man nun irgendwie durch jede sequenz (vlt mit Hilfe der seqNr) auslesen und so nicht nur den letzten Eintrag des Geräts auslesen und ausgeben.
                //Dann diese Werte zwischenspeichern und dann in die Firebase schreiben; zuvor überprüfen ob die Werte vorhanden sind und wenn ja diese dann nicht
                //in die DB schreiben
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
