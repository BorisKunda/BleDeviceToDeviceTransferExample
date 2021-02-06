package com.boriskunda.lstechsassignment.repo;

import android.Manifest;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.boriskunda.lstechsassignment.model.BleScannedDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private Application mApplication;
    private MutableLiveData<List<BleScannedDevice>> scannedDeviceListMld = new MutableLiveData<>();

    private LsRepository (Application application) {
        mApplication = application;
        setBluetoothComponents();

    }

    synchronized public static LsRepository getSingleRepoInstance (Application iApplication) {

        if (singleRepoInstance == null) {
            singleRepoInstance = new LsRepository(iApplication);
        }

        return singleRepoInstance;
    }


    private void setBluetoothComponents () {

        mBluetoothManager = (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);

        if (mBluetoothManager != null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

    }


    public void scanBle () {


        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.MATCH_MODE_STICKY).build();

    //    mScanFilter = new ScanFilter.Builder().setDeviceName("Galaxy S7").build();//todo make it work for any device

        if (mScanCallback == null) {
            mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult (int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    Log.i("BLE SCAN STATUS:", " scanning ");
                    Log.i("BLE SCAN RESULT:", " " + result.getDevice().getName());

                //    mSelectedBluetoothDevice = result.getDevice();

                }
                //-----------

                @Override
                public void onScanFailed (int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.e("onScanFailed", " ErrorCode: " + errorCode);
                }
            };
        }


        if (!isScanning) {
            isScanning = true;
            mBluetoothLeScanner.startScan(new ArrayList<>(Collections.singletonList(null)), mScanSettings, mScanCallback);
        }


    }

    private void stopBleScan () {

        if (mBluetoothLeScanner != null && isScanning && mScanCallback != null) {
            isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }

}
