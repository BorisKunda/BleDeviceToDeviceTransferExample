package com.boriskunda.lstechsassignment.vm;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.boriskunda.lstechsassignment.repo.LsRepository;
import com.boriskunda.lstechsassignment.utils.SingleLiveEvent;

public class LsViewModel extends AndroidViewModel {

    private SingleLiveEvent<Boolean> openSourceTargetSelectionScreenSle = new SingleLiveEvent<>();
    private SingleLiveEvent<Boolean> openBleScannedDevicesListSle = new SingleLiveEvent<>();
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

    public void openBleScannedDevicesList () {
        openBleScannedDevicesListSle.call();
    }

    public void openSelectedTargetDeviceScreen () {
        openSelectedTargetDeviceScreenSle.call();
    }

    public void openTargetDeviceScreen () {
        openTargetDeviceScreenSle.call();
    }

    /**
     * getters
     */
    public SingleLiveEvent<Boolean> getOpenSourceTargetSelectionScreenSle () {
        return openSourceTargetSelectionScreenSle;
    }

    public SingleLiveEvent<Boolean> getOpenBleScannedDevicesListSle () {
        return openBleScannedDevicesListSle;
    }

    public SingleLiveEvent<Boolean> getOpenSelectedTargetDeviceScreenSle () {
        return openSelectedTargetDeviceScreenSle;
    }

    public SingleLiveEvent<Boolean> getOpenTargetDeviceScreenSle () {
        return openTargetDeviceScreenSle;
    }

    /**
     * BLE logic
     **/
    public void scanForBleDevices () {
        lsRepository.scanBle();
    }

}
