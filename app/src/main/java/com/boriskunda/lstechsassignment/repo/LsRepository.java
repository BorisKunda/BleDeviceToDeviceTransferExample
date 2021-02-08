package com.boriskunda.lstechsassignment.repo;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
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
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattCallback mBluetoothGattCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanSettings mScanSettings;
    private ScanCallback mScanCallback;
    private ScanFilter mScanFilter;
    private BluetoothDevice mSelectedBluetoothDevice;
    private final Application mApplication;
    //private final ExecutorService mExecutorService;
    private ParcelUuid mBatteryServiceParcelUuid;
    private ParcelUuid mBatteryLevelCharParcelUuid;
    private UUID mBatteryServiceUuid;
    private UUID mBatteryLevelCharUuid;
    private String TAG = "BLE";


    private MutableLiveData<BleScannedDevice> scannedDeviceMld = new MutableLiveData<>();

    private LsRepository (Application application) {
        mApplication = application;
        setBluetoothComponents();
        //mExecutorService = Executors.newCachedThreadPool();
    }

    synchronized public static LsRepository getSingleRepoInstance (Application iApplication) {

        if (singleRepoInstance == null) {
            singleRepoInstance = new LsRepository(iApplication);
        }

        return singleRepoInstance;
    }


    private void setBluetoothComponents () {

        mBatteryServiceParcelUuid = new ParcelUuid(UUID.fromString(LsConstants.BATTERY_SERVICE_UUID));
        mBatteryLevelCharParcelUuid = new ParcelUuid(UUID.fromString(LsConstants.BATTERY_LEVEL_UUID));

        mBatteryServiceUuid = UUID.fromString(LsConstants.BATTERY_SERVICE_UUID);
        mBatteryLevelCharUuid = UUID.fromString(LsConstants.BATTERY_LEVEL_UUID);

        /*
          public static final String BATTERY_LEVEL_UUID = "00002A19-0000-1000-8000-00805f9b34fb";
    private static final String BATTERY_SERVICE_UUID
         */

        BluetoothManager bluetoothManager = (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (mBluetoothAdapter != null) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }

    }

    /**
     * central logic
     */

    public void scanForBleDevicesFilteredByUuid () {

        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

        mScanFilter = new ScanFilter.Builder().setServiceUuid(mBatteryServiceParcelUuid).build();

        if (mScanCallback == null) {
            mScanCallback = new ScanCallback() {

                @Override
                public void onScanResult (int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    scannedDeviceMld.setValue(new BleScannedDevice(result.getDevice().getName(), result.getDevice().getAddress()));

                    Log.i(TAG, " scanning ");
                    Log.i(TAG, " name:" + result.getDevice().getName());

                    mSelectedBluetoothDevice = result.getDevice();

                    stopBleScan();

                    //      mSelectedBluetoothDevice.connectGatt(mApplication, true, mBluetoothGattCallback);

                }


                @Override
                public void onScanFailed (int errorCode) {
                    super.onScanFailed(errorCode);
                    Log.i(TAG, " onScanFailed ErrorCode: " + errorCode);
                }

            };
        }


        if (!isScanning) {
            isScanning = true;

            //mExecutorService.execute(() -> {
            mBluetoothLeScanner.startScan(new ArrayList<>(Collections.singletonList(mScanFilter)), mScanSettings, mScanCallback);
            //  });

        }


    }

    private void stopBleScan () {

        if (mBluetoothLeScanner != null && isScanning && mScanCallback != null) {
            isScanning = false;
            Log.i(TAG, "stopBleScan: ");
            mBluetoothLeScanner.stopScan(mScanCallback);
            //mExecutorService.shutdownNow();
        }

    }

    /**
     * peripheral logic
     */

    //    public void beginBleAdvertising () {
    //
    //        AdvertiseSettings settings = new AdvertiseSettings.Builder()
    //                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
    //                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
    //                .setConnectable(true)
    //                .build();
    //
    //        AdvertiseData data = new AdvertiseData.Builder()
    //                .setIncludeDeviceName(true)
    //                .addServiceUuid(mParcelUuid)
    //                .addServiceData(mParcelUuid, "Data".getBytes(Charset.forName("UTF-8")))
    //                .build();
    //
    //        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
    //
    //            @Override
    //            public void onStartSuccess (AdvertiseSettings settingsInEffect) {
    //                super.onStartSuccess(settingsInEffect);
    //            }
    //
    //            @Override
    //            public void onStartFailure (int errorCode) {
    //                Log.i(TAG, "Advertising onStartFailure: " + errorCode);
    //                super.onStartFailure(errorCode);
    //            }
    //        };

    //mBluetoothLeAdvertiser.startAdvertising(settings, data, advertisingCallback);

    //    }

    /**
     * ble center -> peripheral connection flow
     */
    public void connectToBleTarget () {

        mBluetoothGattCallback = new BluetoothGattCallback() {

            /**
             *********************BluetoothGattCallback*********************
             */

            @Override
            public void onConnectionStateChange (BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.i(TAG, "onConnectionStateChange: status -- " + status);

                if (status == BluetoothGatt.GATT_SUCCESS) {

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.i(TAG, "onConnectionStateChange: Successfully connected ");

                        //---------------------------

                        gatt.discoverServices();
                        gatt.requestMtu(LsConstants.GATT_MAX_MTU_SIZE);

                        //---------------------------

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        Log.i(TAG, "onConnectionStateChange: Successfully disconnected ");
                        gatt.close();
                    }

                } else {
                    Log.i(TAG, "onConnectionStateChange: error" + status);
                    gatt.close();
                }

            }

            //--------------------------------------------------------------//

            @Override
            public void onPhyUpdate (BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                Log.i(TAG, "onPhyUpdate: status -- " + status);
            }

            @Override
            public void onPhyRead (BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyRead(gatt, txPhy, rxPhy, status);
                Log.i(TAG, "onPhyRead: status -- " + status);
            }

            @Override
            public void onServicesDiscovered (BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                Log.i(TAG, "onServicesDiscovered: status -- " + status);
                List<BluetoothGattService> servicesList = gatt.getServices();

                if (!servicesList.isEmpty()) {
                    for (BluetoothGattService bgs : servicesList) {
                        Log.i("Service", "---" + bgs.getUuid());
                    }
                }

            }

            @Override
            public void onCharacteristicRead (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
                Log.i(TAG, "onCharacteristicRead: status -- " + status);
            }

            @Override
            public void onCharacteristicWrite (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                Log.i(TAG, "onCharacteristicWrite: status -- " + status);
            }

            @Override
            public void onCharacteristicChanged (BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.i(TAG, "onCharacteristicChanged: ");
            }

            @Override
            public void onDescriptorRead (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorRead: status -- " + status);
            }

            @Override
            public void onDescriptorWrite (BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
                Log.i(TAG, "onDescriptorWrite: status -- " + status);
            }

            @Override
            public void onReliableWriteCompleted (BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
                Log.i(TAG, "onReliableWriteCompleted: status -- " + status);
            }

            @Override
            public void onReadRemoteRssi (BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                Log.i(TAG, "onReadRemoteRssi: status -- " + status);
            }

            @Override
            public void onMtuChanged (BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
                Log.i(TAG, "onMtuChanged: status -- " + status);
            }


            /**
             *********************BluetoothGattCallback*********************
             */

        };

        mSelectedBluetoothDevice.connectGatt(mApplication, false, mBluetoothGattCallback);
    }

    /**
     * getters
     */
    public MutableLiveData<BleScannedDevice> getScannedDeviceMld () {
        return scannedDeviceMld;
    }

}
