package com.boriskunda.lstechsassignment.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.boriskunda.lstechsassignment.model.BleScannedDevice;
import com.boriskunda.lstechsassignment.repo.LsRepository;
import com.boriskunda.lstechsassignment.utils.SingleLiveEvent;

public class LsViewModel extends AndroidViewModel {

    private SingleLiveEvent<Boolean> openSourceTargetSelectionScreenSle = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> openSelectedTargetDeviceScreenSle = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> openTargetDeviceScreenSle = new SingleLiveEvent<>();
    private LsRepository lsRepository;

    public LsViewModel (@NonNull Application application) {
        super(application);
        lsRepository = LsRepository.getSingleRepoInstance(application);
    }


    /**
     * screens navigation methods
     */

    public void openSourceTargetSelectionScreen () {
        openSourceTargetSelectionScreenSle.call();
    }

    public void openSelectedTargetDeviceScreen () {
        openSelectedTargetDeviceScreenSle.call();
    }

    public void openTargetDeviceScreen () {
        openTargetDeviceScreenSle.call();
    }

    public LiveData<BleScannedDevice> getScannedDeviceLd () {
        return lsRepository.getScannedDeviceMld();
    }

    /**
     * getters
     */

    public SingleLiveEvent<Boolean> getOpenSourceTargetSelectionScreenSle () {
        return openSourceTargetSelectionScreenSle;
    }

    public SingleLiveEvent<Boolean> getOpenSelectedTargetDeviceScreenSle () {
        return openSelectedTargetDeviceScreenSle;
    }

    public SingleLiveEvent<Boolean> getOpenTargetDeviceScreenSle () {
        return openTargetDeviceScreenSle;
    }

    /**
     * BLE central logic
     **/

    public void connectToBleTargetDevice () {
        lsRepository.connectToBleTarget();
    }

    public void scanForBleDevices () {
        lsRepository.scanForBleDevicesFilteredByUuid();
    }

    /**
     * BLE peripheral logic
     **/
    public void advertiseBleData () {
        lsRepository.startBleAdvertising();
    }

}
