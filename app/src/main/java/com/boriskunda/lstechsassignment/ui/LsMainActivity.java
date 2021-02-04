package com.boriskunda.lstechsassignment.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.ui.fragments.BleScannedDevicesListFragment;
import com.boriskunda.lstechsassignment.ui.fragments.SelectedTargetDeviceScreenFragment;
import com.boriskunda.lstechsassignment.ui.fragments.SourceTargetSelectionScreenFragment;
import com.boriskunda.lstechsassignment.ui.fragments.TargetDeviceScreenFragment;
import com.boriskunda.lstechsassignment.vm.LsViewModel;

public class LsMainActivity extends AppCompatActivity {

    private BleScannedDevicesListFragment mBleScannedDevicesListFragment;//yellow
    private SelectedTargetDeviceScreenFragment mSelectedTargetDeviceScreenFragment;//green
    private SourceTargetSelectionScreenFragment mSourceTargetSelectionScreenFragment;//blue
    private TargetDeviceScreenFragment mTargetDeviceScreenFragment;//red
    private FragmentManager mFragmentManager;
    private LsViewModel mLsViewModel;

    @Override

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragments();
        initVM();
        observeLiveData(mLsViewModel);


    }

    private void initVM () {
        mLsViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(LsViewModel.class);
    }

    private void observeLiveData (LsViewModel iLsViewModel) {

        iLsViewModel.getOpenBleScannedDevicesListSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mBleScannedDevicesListFragment).commit());

        iLsViewModel.getOpenSelectedTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mSelectedTargetDeviceScreenFragment).commit());

        iLsViewModel.getOpenSourceTargetSelectionScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mSourceTargetSelectionScreenFragment).commit());

        iLsViewModel.getOpenTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mTargetDeviceScreenFragment).commit());

    }

    private void setFragments () {

        mFragmentManager = getSupportFragmentManager();

        mBleScannedDevicesListFragment = new BleScannedDevicesListFragment();
        mSelectedTargetDeviceScreenFragment = new SelectedTargetDeviceScreenFragment();
        mSourceTargetSelectionScreenFragment = new SourceTargetSelectionScreenFragment();
        mTargetDeviceScreenFragment = new TargetDeviceScreenFragment();

    }

}