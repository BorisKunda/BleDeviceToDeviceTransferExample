package com.boriskunda.lstechsassignment;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //constants:
    private final static int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private final String locPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    //private ScanFilter mScanFilter;
    private ScanSettings mScanSettings;
    private ExecutorService bleScanExecutor;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setBluetoothComponents();

        if (isLocPermissionGranted()) {
            startBleScan();
        } else {
            requestLocPermission();
        }

    }

    @Override
    protected void onResume () {
        super.onResume();

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                requestEnableBluetooth();
            }
        }

    }

    /**
     * BLE logic
     */
    private void setBluetoothComponents () {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        //mScanFilter = new ScanFilter.Builder().build();

        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.MATCH_MODE_STICKY).build();
        bleScanExecutor = Executors.newSingleThreadExecutor();
    }

    private void requestEnableBluetooth () {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
        }
    }

    private void startBleScan () {

        ScanCallback scanCallback = new ScanCallback() {

            @Override
            public void onScanResult (int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);

                String scanResultName = result.getDevice().getName();

                if (scanResultName != null && scanResultName.equals("Galaxy S7")) {
                    Log.d("debug", "onScanResult: ");
                }

                //BOND_NONE = 10;
                //BOND_BONDING = 11;
                //BOND_BONDED = 12;

                // Log.i("ScanCallback", " --------------------------------------------- ");
                // Log.i("ScanCallback", " Name:" + result.getDevice().getName() + " ");
                // Log.i("ScanCallback", " Bond:" + result.getDevice().getBondState() + " ");
                // //  Log.i("ScanCallback", " result:" + result.getDevice().getUuids() + " ");
                // Log.i("ScanCallback", " --------------------------------------------- ");
            }
        };

        bleScanExecutor.execute(() -> mBluetoothLeScanner.startScan(null, mScanSettings, scanCallback));

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

            if (permissions[ 0 ].equals(PackageManager.PERMISSION_DENIED)) {
                requestLocPermission();
            } else {
                startBleScan();
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

}
