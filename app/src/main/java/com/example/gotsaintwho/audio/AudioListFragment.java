package com.example.gotsaintwho.audio;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gotsaintwho.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;

public class AudioListFragment extends Fragment implements AudioAdapter.onItemListClick {

    private ConstraintLayout musicPlayerLayout;
    private BottomSheetBehavior musicPlayerLayoutBehavior;
    private RecyclerView audioList;
    private File[] allAudioFiles;
    //MediaPlayer
    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;
    private File fileToPlay = null;

    //assign adapter to recycle view
    private AudioAdapter audioAdapter;

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
        //musicPlayerLayout
        musicPlayerLayout = view.findViewById(R.id.music_player);
        musicPlayerLayoutBehavior = BottomSheetBehavior.from(musicPlayerLayout);

        //audioList
        audioList = view.findViewById(R.id.audio_list_view);
        //File
        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();

//        String path = getActivity().getFilesDir().getPath();
        File directory = new File(path);
        allAudioFiles = directory.listFiles();

        audioAdapter = new AudioAdapter(allAudioFiles,this);
        //set recycle view
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioAdapter);


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

    @Override
    public void onClickListener(File file, int position) {
        Log.d("PLAY_LOG", "File Playing" + file.getName());
        if(isPlaying){
            stopAudio();
            isPlaying=false;
        } else {
            isPlaying=true;
            fileToPlay = file;
            playAudio(fileToPlay);
        }
    }

    private void playAudio(File fileToPlay) {
        isPlaying = true;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopAudio(){
        isPlaying = false;

    }
}