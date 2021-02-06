package com.boriskunda.lstechsassignment.repo;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.boriskunda.lstechsassignment.model.BleScannedDevice;
import com.boriskunda.lstechsassignment.utils.LsConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LsRepository {

    private static LsRepository singleRepoInstance;
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
    private final Application mApplication;
    private final ExecutorService mExecutorService;


    private MutableLiveData<BleScannedDevice> scannedDeviceMld = new MutableLiveData<>();

    private LsRepository (Application application) {
        mApplication = application;
        setBluetoothComponents();
        mExecutorService = Executors.newSingleThreadExecutor();
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


    public void scanForBleDevicesFilteredByUuid () {

        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

        mScanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(LsConstants.TARGET_UUID)).build();

        if (mScanCallback == null) {
            mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult (int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    scannedDeviceMld.postValue(new BleScannedDevice(result.getDevice().getName(), result.getDevice().getAddress()));

                    Log.i("BLE SCAN STATUS:", " scanning ");
                    Log.i("BLE SCAN RESULT:", " name:" + result.getDevice().getName());
                    stopBleScan();
                    //todo timeout
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

            mExecutorService.execute(() -> mBluetoothLeScanner.startScan(new ArrayList<>(Collections.singletonList(mScanFilter)), mScanSettings, mScanCallback));

        }


    }

    private void stopBleScan () {

        if (mBluetoothLeScanner != null && isScanning && mScanCallback != null) {
            isScanning = false;
            mBluetoothLeScanner.stopScan(mScanCallback);
            mExecutorService.shutdownNow();
        }

    }

    /**
     * getters
     */

    public MutableLiveData<BleScannedDevice> getScannedDeviceMld () {
        return scannedDeviceMld;
    }

}
