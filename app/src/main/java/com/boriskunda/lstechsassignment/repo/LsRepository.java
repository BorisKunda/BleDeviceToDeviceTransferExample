package com.boriskunda.lstechsassignment.repo;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LsRepository {

    private static LsRepository singleRepoInstance;
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

    private LsRepository (Application application) {
    }

    synchronized public static LsRepository getSingleRepoInstance (Application iApplication) {

        if (singleRepoInstance == null) {
            singleRepoInstance = new LsRepository(iApplication);
        }

        return singleRepoInstance;
    }

    public void scanBle () {

    }



}
