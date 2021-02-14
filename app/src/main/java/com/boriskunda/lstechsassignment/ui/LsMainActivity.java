package com.boriskunda.lstechsassignment.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.boriskunda.lstechsassignment.R;
import com.boriskunda.lstechsassignment.ui.fragments.SourceFragment;
import com.boriskunda.lstechsassignment.ui.fragments.SourceTargetSelectionScreenFragment;
import com.boriskunda.lstechsassignment.ui.fragments.TargetFragment;
import com.boriskunda.lstechsassignment.utils.LsConstants;
import com.boriskunda.lstechsassignment.vm.LsViewModel;

public class LsMainActivity extends AppCompatActivity {

    private SourceFragment mSourceFragment;
    private SourceTargetSelectionScreenFragment mSourceTargetSelectionScreenFragment;
    private TargetFragment mTargetFragment;
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

        iLsViewModel.getOpenSelectedTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mSourceFragment).addToBackStack(LsConstants.MAIN_FRAGMENT_STACK).commit());

        iLsViewModel.getOpenTargetDeviceScreenSle().observe(this, iBoolean -> mFragmentManager.beginTransaction().replace(R.id.fr_container_ll, mTargetFragment).addToBackStack(LsConstants.MAIN_FRAGMENT_STACK).commit());

    }

    private void setFragments () {

        mFragmentManager = getSupportFragmentManager();

        mSourceFragment = new SourceFragment();
        mSourceTargetSelectionScreenFragment = new SourceTargetSelectionScreenFragment();
        mTargetFragment = new TargetFragment();

    }

}