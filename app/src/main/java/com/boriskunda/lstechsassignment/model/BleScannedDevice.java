package com.boriskunda.lstechsassignment.model;

public class BleScannedDevice {

    private String deviceName;
    private String address;

    public BleScannedDevice (String iDeviceName, String iAddress) {
        deviceName = iDeviceName;
        address = iAddress;
    }

    public String getDeviceName () {
        return deviceName;
    }

    public String getAddress () {
        return address;
    }

}
