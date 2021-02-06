package com.boriskunda.lstechsassignment.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.ui.fragments.SelectedTargetDeviceScreenFragment;
import com.boriskunda.lstechsassignment.ui.fragments.SourceTargetSelectionScreenFragment;
import com.boriskunda.lstechsassignment.ui.fragments.TargetDeviceScreenFragment;
import com.boriskunda.lstechsassignment.utils.LsConstants;
import com.boriskunda.lstechsassignment.vm.LsViewModel;

public class LsMainActivity extends AppCompatActivity {

    private SelectedTargetDeviceScreenFragment mSelectedTargetDeviceScreenFragment;
    private SourceTargetSelectionScreenFragment mSourceTargetSelectionScreenFragment;
    private TargetDeviceScreenFragment mTargetDeviceScreenFragment;
    private FragmentManager mFragmentManager;
    private LsViewModel mLsViewModel;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFragments();
        initVM();
        observeLiveData(mLsViewModel);

        mLsViewModel.openSourceTargetSelectionScreen();
    }

    private void initVM () {
        mLsViewModel = new ViewModelProvider(this).get(LsViewModel.class);
    }

    private void observeLiveData (LsViewModel iLsViewModel) {

        iLsViewModel.getOpenSourceTargetSelectionScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mSourceTargetSelectionScreenFragment).commit());

        iLsViewModel.getOpenSelectedTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mSelectedTargetDeviceScreenFragment).addToBackStack(LsConstants.MAIN_FRAGMENT_STACK).commit());

        iLsViewModel.getOpenTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mTargetDeviceScreenFragment).addToBackStack(LsConstants.MAIN_FRAGMENT_STACK).commit());

    }

    private void setFragments () {

        mFragmentManager = getSupportFragmentManager();

        mSelectedTargetDeviceScreenFragment = new SelectedTargetDeviceScreenFragment();
        mSourceTargetSelectionScreenFragment = new SourceTargetSelectionScreenFragment();
        mTargetDeviceScreenFragment = new TargetDeviceScreenFragment();

    }

}