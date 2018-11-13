package samuli.androidbeacons;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    private static final ScanSettings SCAN_SETTINGS =
            new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0)
                    .build();

    private static final Handler handler = new Handler(Looper.getMainLooper());

    // The Eddystone Service UUID, 0xFEAA.
    private static final ParcelUuid EDDYSTONE_SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    private static final byte UID_FRAME_TYPE = 0x00;

    private double maxDistanceToBeacon = 0.1; //metriä

    private TextView textview;
    private TextView textview2;
    private TextView textview3;

    private Button buttonIncrease;
    private Button buttonDecrease;
    private Button buttonClear;

    private CheckBox sendToDatabeseCheckbox;

    private TimeMeasure timeMeasure;

    private BluetoothLeScanner scanner;
    private List<ScanFilter> scanFilters;
    private ScanCallback scanCallback;
    private HashMap<String, Beacon> deviceToBeaconMap = new HashMap<>();

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        context = getApplicationContext();

        textview = findViewById(R.id.textView);
        textview2 = findViewById(R.id.textView2);
        textview3 = findViewById(R.id.textView3);

        textview.setText("Displaying beacons which distance is less than " + Double.toString(maxDistanceToBeacon) + "m");
        textview3.setMovementMethod(new ScrollingMovementMethod());

        timeMeasure = new TimeMeasure();

        buttonIncrease = findViewById(R.id.button1);
        buttonDecrease = findViewById(R.id.button2);
        buttonClear = findViewById(R.id.buttonClear);

        sendToDatabeseCheckbox = findViewById(R.id.sendToDatabaseCheckbox);

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxDistanceToBeacon += 0.1;
                String maxDistance = new DecimalFormat("0.0#").format(maxDistanceToBeacon);
                textview.setText("Displaying beacons which distance is less than " + maxDistance + "m");
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxDistanceToBeacon > 0.1) {
                    maxDistanceToBeacon -= 0.1;
                    String maxDistance = new DecimalFormat("0.0#").format(maxDistanceToBeacon);
                    textview.setText("Displaying beacons which distance is less than " + maxDistance + "m");
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview3.setText("");
            }
        });

        sendToDatabeseCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                              @Override
                                                              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                  DatabaseSender.sendingEnabled = isChecked;
                                                              }
                                                          }
        );

        init();
        scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder().setServiceUuid(EDDYSTONE_SERVICE_UUID).build());
        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                ScanRecord scanRecord = result.getScanRecord();
                if (scanRecord == null) {
                    return;
                }

                String deviceAddress = result.getDevice().getAddress();
                Beacon beacon;
                if (!deviceToBeaconMap.containsKey(deviceAddress)) {
                    beacon = new Beacon(deviceAddress, result.getRssi());
                    deviceToBeaconMap.put(deviceAddress, beacon);
                } else {
                    deviceToBeaconMap.get(deviceAddress).rssi = result.getRssi();
                }

                byte[] serviceData = scanRecord.getServiceData(EDDYSTONE_SERVICE_UUID);
                validateServiceData(deviceAddress, serviceData);
            }

            @Override
            public void onScanFailed(int errorCode) {
                System.out.println("Scan failed error code: " + errorCode);
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanner != null) {
            scanner.stopScan(scanCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeCallbacksAndMessages(null);

        if (scanner != null) {
            scanner.startScan(scanFilters, SCAN_SETTINGS, scanCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                init();
            } else {
                this.finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("PERMISSION_REQUEST_COARSE_LOCATION granted");
                }
            }
        }
    }

    // Checks the frame type and hands off the service data to the validation module.
    private void validateServiceData(String deviceAddress, byte[] serviceData) {
        Beacon beacon = deviceToBeaconMap.get(deviceAddress);
        if (serviceData == null) {
            String err = "Null Eddystone service data";
            beacon.nullServiceData = err;
            return;
        }

        Log.v("TAG", deviceAddress + " " + Utils.toHexString(serviceData));
        switch (serviceData[0]) {
            case UID_FRAME_TYPE:
                UidValidator.validate(deviceAddress, serviceData, beacon);
                break;
            default:
                String err = String.format("Invalid frame type byte %02X", serviceData[0]);
                beacon.invalidFrameType = err;
                break;
        }
        checkBeacons();
    }

    private double distanceFromRssi(int rssi, int txPower0m) {
        int pathLoss = txPower0m - rssi;
        return Math.pow(10, (pathLoss - 41) / 20.0);
    }

    private void checkBeacons() {
        textview2.setText("");
        for (Map.Entry<String, Beacon> entry : deviceToBeaconMap.entrySet()) {
            Beacon beacon = entry.getValue();
            double distanceToBeacon = distanceFromRssi(beacon.rssi, beacon.txPower);
            timeMeasure.setCurrentBeacon(beacon);
            //if (distanceToBeacon < beacon.deviceMaxDistance) { //for manually adding beacon distances to beaconDistanceMap
            if (distanceToBeacon < maxDistanceToBeacon) { //for choosing distance with buttons
                if (!timeMeasure.beaconTimeMeasureMapContains(beacon)) {
                    timeMeasure.startMeasuringTime(beacon);
                } else {
                    timeMeasure.setBeaconLastSeen(beacon);
                    textview2.append("Measuring beacon: " + beacon.deviceName + ", time: " + timeMeasure.getCurrentTimeInSeconds() + "s \n");
                }
            } else {
                if (timeMeasure.beaconTimeMeasureMapContains(beacon)) {
                    timeMeasure.checkIfStop();
                    textview2.append("Connection lost to: " + timeMeasure.getBeaconName() + ", time to reconnect: " + TimeUnit.MILLISECONDS.toSeconds(OwnBeacons.connectionLostTime) + "\n");
                    if (timeMeasure.isMeasureStopped() && timeMeasure.isMeasureDataValid()) {
                        textview3.append("Measured beacon: " + timeMeasure.getBeaconName() + ",  time: " + timeMeasure.getMeasuredTimeInSeconds() + "s \n");
                    }
                }
            }
        }
    }

    // Attempts to create the scanner.
    private void init() {
        // New Android M+ permission check requirement.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs coarse location access");
                builder.setMessage("Please grant coarse location access so this app can scan for beacons");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
        BluetoothManager manager = (BluetoothManager) this.getApplicationContext()
                .getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = manager.getAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            scanner = btAdapter.getBluetoothLeScanner();
        }
    }
}
