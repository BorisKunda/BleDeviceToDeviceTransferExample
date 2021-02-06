package com.boriskunda.lstechsassignment.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.vm.LsViewModel;


public class BleScannedDevicesListFragment extends Fragment {

    private LsViewModel mLsViewModel;

    public BleScannedDevicesListFragment () {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ble_scanned_devices_list, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLsViewModel = new ViewModelProvider(getActivity()).get(LsViewModel.class);

        performScan();

    }

    private void performScan () {
        mLsViewModel.scanForBleDevices();
    }

}