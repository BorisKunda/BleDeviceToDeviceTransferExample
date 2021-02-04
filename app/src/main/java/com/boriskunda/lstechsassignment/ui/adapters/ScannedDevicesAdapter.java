package com.boriskunda.lstechsassignment.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.model.BleScannedDevice;

import java.util.ArrayList;
import java.util.List;

public class ScannedDevicesAdapter extends RecyclerView.Adapter<ScannedDevicesAdapter.ScannedDevicesVh> {

    private List<BleScannedDevice> mScannedDevicesList = new ArrayList<>();

    public ScannedDevicesAdapter (List<BleScannedDevice> iScannedDevicesList) {
        mScannedDevicesList = iScannedDevicesList;
    }

    @NonNull
    @Override
    public ScannedDevicesVh onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        return new ScannedDevicesVh(LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_scanned_devices_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder (@NonNull ScannedDevicesVh holder, int position) {

        BleScannedDevice bleScannedDevice = mScannedDevicesList.get(position);
        holder.scannedDeviceNameTv.setText(bleScannedDevice.getDeviceName());

    }

    @Override
    public int getItemCount () {
        return mScannedDevicesList.size();
    }


    class ScannedDevicesVh extends RecyclerView.ViewHolder {

        TextView scannedDeviceNameTv;

        public ScannedDevicesVh (@NonNull View itemView) {
            super(itemView);
            scannedDeviceNameTv = itemView.findViewById(R.id.scanned_device_name_tv);
        }

    }

}
