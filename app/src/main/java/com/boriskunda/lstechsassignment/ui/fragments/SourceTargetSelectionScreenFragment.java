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


public class SourceTargetSelectionScreenFragment extends Fragment implements View.OnClickListener {

    private LsViewModel mLsViewModel;
    private ImageView mSourceIv, mTargetIv;
    private TextView mSourceImageLabelTv, mTargetImageLabelTv;

    public SourceTargetSelectionScreenFragment () {
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_source_target_selection_screen, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLsViewModel = new ViewModelProvider(getActivity()).get(LsViewModel.class);

        mSourceIv = view.findViewById(R.id.source_iv);
        mTargetIv = view.findViewById(R.id.target_iv);
        mSourceImageLabelTv = view.findViewById(R.id.source_tv);
        mTargetImageLabelTv = view.findViewById(R.id.target_tv);

        mSourceIv.setOnClickListener(this);
        mTargetIv.setOnClickListener(this);
        mSourceImageLabelTv.setOnClickListener(this);
        mTargetImageLabelTv.setOnClickListener(this);

    }

    @Override
    public void onClick (View v) {

        switch (v.getId()) {
            case R.id.source_iv:
            case R.id.source_tv:
                mLsViewModel.openSelectedTargetDeviceScreen();
                break;
            //---
            case R.id.target_iv:
            case R.id.target_tv:
                mLsViewModel.openTargetDeviceScreen();
                break;

        }

    }

}