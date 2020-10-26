package com.example.gotsaintwho.audio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotsaintwho.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class AudioListFragment extends Fragment {

    private ConstraintLayout musicPlayerLayout;
    private BottomSheetBehavior musicPlayerLayoutBehavior;
    public AudioListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicPlayerLayout = view.findViewById(R.id.music_player);
        musicPlayerLayoutBehavior = BottomSheetBehavior.from(musicPlayerLayout);
        //add callback
        musicPlayerLayoutBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //musicPlayerLayout always show up
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    musicPlayerLayoutBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //not used
            }
        });

    }
}