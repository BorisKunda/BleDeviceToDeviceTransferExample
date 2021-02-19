package com.boriskunda.lstechsassignment.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.vm.LsViewModel;


public class SourceFragment extends Fragment {

    private TextView mTargetDeviceNameTv;
    private TextView mTargetDeviceAddressTv;
    private ImageView mConnectTargetDeviceIv;
    private ImageView mReadTargetDevice;

    public SourceFragment () {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selected_target_device_screen, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTargetDeviceNameTv = view.findViewById(R.id.selected_target_device_name_tv);
        mTargetDeviceAddressTv = view.findViewById(R.id.selected_target_device_address_tv);
        mConnectTargetDeviceIv = view.findViewById(R.id.connect_target_device_iv);
        mReadTargetDevice = view.findViewById(R.id.read_target_device_iv);

        LsViewModel lsViewModel = new ViewModelProvider(getActivity()).get(LsViewModel.class);

        lsViewModel.scanForBleDevices();

        lsViewModel.getScannedDeviceLd().observe(getViewLifecycleOwner(), iBleScannedDevice -> {

            mTargetDeviceNameTv.setText(iBleScannedDevice.getDeviceName());
            mTargetDeviceAddressTv.setText(iBleScannedDevice.getAddress());

        });

        mConnectTargetDeviceIv.setOnClickListener(v -> lsViewModel.connectToBleTargetDevice());
        mReadTargetDevice.setOnClickListener(v -> lsViewModel.readBleTarget());

    }

}