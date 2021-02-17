package com.boriskunda.lstechsassignment.repo;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
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
import java.util.List;
import java.util.UUID;

public class LsRepository {

    private static LsRepository singleRepoInstance;
    private boolean isScanning = false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private final Application mApplication;
    private ParcelUuid mServiceParcelUuid;
    private ParcelUuid mImageCharacterParcelUuid;
    private UUID mServiceUuid;
    private UUID mImageCharacteristicUuid;
    //peripheral
    private BluetoothGattService mService;
    private BluetoothGattCharacteristic mImageCharacteristic;
    private BluetoothGattServer mGattServer;
    private BluetoothGattServerCallback mBluetoothGattServerCallback;
    //central
    private BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothDevice mSelectedBluetoothDevice;
    private ScanSettings mScanSettings;
    private ScanCallback mScanCallback;
    private ScanFilter mScanFilter;


    private MutableLiveData<BleScannedDevice> scannedDeviceMld = new MutableLiveData<>();

    public MutableLiveData<BleScannedDevice> getScannedDeviceMld () {
        return scannedDeviceMld;
    }

    private LsRepository (Application application) {
        mApplication = application;
        initComponents();
    }

    synchronized public static LsRepository getSingleRepoInstance (Application iApplication) {

        if (singleRepoInstance == null) {
            singleRepoInstance = new LsRepository(iApplication);
        }

        return singleRepoInstance;
    }

    private void initComponents () {

        mServiceParcelUuid = new ParcelUuid(UUID.fromString(LsConstants.TARGET_SERVICE_UUID));
        mImageCharacterParcelUuid = new ParcelUuid(UUID.fromString(LsConstants.TARGET_IMAGE_CHARACTERISTIC_UUID));

        mServiceUuid = UUID.fromString(LsConstants.TARGET_SERVICE_UUID);
        mImageCharacteristicUuid = UUID.fromString(LsConstants.TARGET_IMAGE_CHARACTERISTIC_UUID);

        mBluetoothManager = (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();

    }

    /***********************************************************************************************
     central logic
     ************************************************************************************************/

    public void scanForBleDevicesFilteredByUuid () {

        mBluetoothGattCallback = new BluetoothGattCallback() {

            /**SOURCE CONNECTION CHANGE******************************************************************/

            @Override
            public void onConnectionStateChange (BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("SOURCE", "STATE_GATT_SUCCESS");

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i("SOURCE", "STATE_CONNECTED");
                        gatt.discoverServices();
                        gatt.requestMtu(LsConstants.GATT_MAX_MTU_SIZE);

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i("SOURCE", "STATE_DISCONNECTED");
                        gatt.close();
                    }

                } else {
                    Log.e("SOURCE", "STATE_ERROR" + status);
                    gatt.close();
                }

            }


            @Override
            public void onServicesDiscovered (BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("SOURCE", "SERVICE DISCOVERY_SUCCESS");

                    List<BluetoothGattService> servicesList = gatt.getServices();

                    if (!servicesList.isEmpty()) {
                        for (BluetoothGattService bgs : servicesList) {
                            Log.i("SOURCE", "---service uuid---" + bgs.getUuid());
                        }
                    }

                } else {
                    Log.i("SOURCE", "SERVICE DISCOVERY_FAILED");
                }

            }

            @Override
            public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("SOURCE", "CHARACTERISTIC WRITE SUCCESS");
                } else {
                    Log.i("SOURCE", "CHARACTERISTIC WRITE FAILURE");
                }

            }

            @Override
            public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.i("SOURCE", "CHARACTERISTIC CHANGED");
            }

            @Override
            public void onMtuChanged (BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("SOURCE", "MTU CHANGE SUCCESS");
                } else {
                    Log.i("SOURCE", "MTU CHANGE FAILURE");
                }

            }

        };

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

        mScanFilter = new ScanFilter.Builder().setServiceUuid(mServiceParcelUuid).build();

        if (mScanCallback == null) {

            /**SCAN******************************************************************/

            mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult (int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    scannedDeviceMld.setValue(new BleScannedDevice(result.getDevice().getName(), result.getDevice().getAddress()));

                    mSelectedBluetoothDevice = result.getDevice();

                    Log.i("SOURCE", "---name---" + result.getDevice().getName());

                    stopBleScan();

                }


                @Override
                public void onScanFailed (int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.i("SOURCE", "SCAN FAILURE ---error code---" + errorCode);
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
            Log.i("SOURCE", "stopBleScan: ");
            mBluetoothLeScanner.stopScan(mScanCallback);
        }

    }

    public void connectToBleTarget () {
        mSelectedBluetoothDevice.connectGatt(mApplication, false, mBluetoothGattCallback);
    }


    /***********************************************************************************************
     peripheral logic
     ************************************************************************************************/

    public void setPeripheral () {

        mBluetoothGattServerCallback = new BluetoothGattServerCallback() {

            /**TARGET CONNECTION CHANGE******************************************************************/

            @Override
            public void onConnectionStateChange (BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("TARGET", "STATE_GATT_SUCCESS");

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i("TARGET", "STATE_CONNECTED");

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i("TARGET", "STATE_DISCONNECTED");
                    }

                } else {
                    Log.e("TARGET", "STATE_ERROR" + status);
                }

            }

            @Override
            public void onCharacteristicReadRequest (BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            }

            @Override
            public void onCharacteristicWriteRequest (BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
                Log.i("TARGET", "WRITE REQUEST");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value);
            }

            @Override
            public void onExecuteWrite (BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
                Log.i("TARGET", "EXECUTE WRITE REQUEST---status---" + execute);
            }
        };

        mGattServer = mBluetoothManager.openGattServer(mApplication, mBluetoothGattServerCallback);

        // create the Service
        mService = new BluetoothGattService(mServiceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        mImageCharacteristic = new BluetoothGattCharacteristic(mImageCharacteristicUuid,
                (BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY),
                BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);

        //todo use byte buffer for it
        mImageCharacteristic.setValue(new byte[]{1,3,5});

        // add the Characteristic to the Service
        mService.addCharacteristic(mImageCharacteristic);

        mGattServer.addService(mService);

    }

    public void startBleAdvertising () {

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(mServiceParcelUuid)
                .build();

        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {

            @Override
            public void onStartSuccess (AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i("TARGET", "AD SUCCESS");
            }

            @Override
            public void onStartFailure (int errorCode) {
                super.onStartFailure(errorCode);
                Log.i("TARGET", "AD FAILURE---errorCode---" + errorCode);

            }
        };

        mBluetoothAdapter.getBluetoothLeAdvertiser().startAdvertising(settings, data, advertisingCallback);

    }


}
