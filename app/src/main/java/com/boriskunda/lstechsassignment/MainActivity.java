package com.boriskunda.lstechsassignment;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {


    private final static int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private final String locPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    private boolean isScanning = false;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanSettings mScanSettings;
    private ScanCallback mScanCallback;
    private ScanFilter mScanFilter;
    private BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothDevice mSelectedBluetoothDevice;
    private TextView deviceNameTv;
    private Button mStartBtn, mStopBtn;
    private ImageView mConnectBleBtnIv;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        setBluetoothComponents();

        /**
         * Click listeners
         */

        mStartBtn.setOnClickListener(v -> {

            if (isLocPermissionGranted()) {
                startBleScan();
            } else {
                requestLocPermission();
            }

        });

        mStopBtn.setOnClickListener(v -> {
            stopBleScan();
            deviceNameTv.setText("");
        });

        mConnectBleBtnIv.setOnClickListener(v -> {

            if (isScanning) {
                stopBleScan();
            }

            connectToBleDevice(mSelectedBluetoothDevice);

        });

    }

    /**
     * BLE logic
     */
    private void setBluetoothComponents () {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

    }

    private void requestEnableBluetooth () {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    private void startBleScan () {

        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.MATCH_MODE_STICKY).build();

        mScanFilter = new ScanFilter.Builder().setDeviceName("Galaxy S7").build();//todo make it work for any device

        if (mScanCallback == null) {
            mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult (int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    Log.i("BLE SCAN STATUS:", " scanning ");
                    Log.i("BLE SCAN RESULT:", " " + result.getDevice().getName());

                    mSelectedBluetoothDevice = result.getDevice();

                    deviceNameTv.setText(result.getDevice().getName());

                }
            };
        }


        if (!isScanning) {
            isScanning = true;
            mBluetoothLeScanner.startScan(new ArrayList<>(Collections.singletonList(mScanFilter)), mScanSettings, mScanCallback);
        }

    }

    private void stopBleScan () {

        if (mBluetoothLeScanner != null && isScanning && mScanCallback != null) {
            isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }

    private void connectToBleDevice (BluetoothDevice iBluetoothDevice) {

        if (mBluetoothGattCallback == null) {

            mBluetoothGattCallback = new BluetoothGattCallback() {

                @Override
                public void onConnectionStateChange (BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);

                    mBluetoothGatt = gatt;

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            Log.i("BluetoothGattCallback", "Successfully connected to Galaxy S7");
                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            Log.i("BluetoothGattCallback", "Disconnected from Galaxy S7");
                            gatt.close();
                        }
                    } else {
                        Log.w("BluetoothGattCallback", "Oops! Something went wrong.");
                    }

                }
            };

        }

        if (iBluetoothDevice != null) {
            iBluetoothDevice.connectGatt(this, false, mBluetoothGattCallback);
        }

    }


    /**
     * Permissions logic
     */
    private boolean isLocPermissionGranted () {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocPermission () {
        ActivityCompat.requestPermissions(this, new String[]{ locPermission }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length != 0) {

                if (grantResults[ 0 ] == PackageManager.PERMISSION_DENIED) {
                    requestLocPermission();
                } else {
                    startBleScan();
                }

            }

        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENABLE_BLUETOOTH_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                requestEnableBluetooth();
            }
        }

    }

    private void initUi () {
        mStartBtn = findViewById(R.id.startBtn);
        mStopBtn = findViewById(R.id.stopBtn);
        mConnectBleBtnIv = findViewById(R.id.bleBtnIv);
        deviceNameTv = findViewById(R.id.device_name_tv);
    }

    /**
     * Lifecycle methods
     */

    @Override
    protected void onResume () {
        super.onResume();

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                requestEnableBluetooth();
            }
        }

    }

    @Override
    protected void onStop () {
        super.onStop();
        stopBleScan();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        //clean resources
        mScanCallback = null;
        mBluetoothLeScanner = null;
    }

}
