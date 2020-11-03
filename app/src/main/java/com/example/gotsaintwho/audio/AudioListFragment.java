package com.example.gotsaintwho.audio;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

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
    //public MediaPlayer mediaPlayer =null;
    private boolean isPlaying = false;
    private File fileToPlay = null;
    public MediaPlayer mediaPlayer;

    //assign adapter to recycle view
    private AudioAdapter audioAdapter;

    //UI Elements
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;

    //music player UI
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    private Drawable pauseButtonImage;
    private Drawable playButtonImage;

    public AudioListFragment() {
        mediaPlayer = new MediaPlayer();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pauseButtonImage = getActivity().getResources().getDrawable(R.drawable.player_pause_btn, null);
        playButtonImage= getActivity().getResources().getDrawable(R.drawable.player_play_btn, null);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAudio();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        musicPlayerLayout = view.findViewById(R.id.music_player);
        musicPlayerLayoutBehavior = BottomSheetBehavior.from(musicPlayerLayout);
        //player
        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);
        audioList = view.findViewById(R.id.audio_list_view);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
        Log.d("Audio List Fragement","File Path" + path);
        File directory = new File(path);
        allAudioFiles = directory.listFiles();
        audioAdapter = new AudioAdapter(allAudioFiles,this);
        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioAdapter);
        playerSeekbar = view.findViewById(R.id.player_seekbar);
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

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check any audio play
                if (isPlaying) {
                    pauseAudio();
                } else if (fileToPlay != null) {
                    resumeAudio();
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });
    }//onViewCreated end

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if (isPlaying) {
            stopAudio();
        }
        playAudio(fileToPlay);
    }

    private void pauseAudio() {
        if (isPlaying) {
            isPlaying = false;
            mediaPlayer.pause();
            playBtn.setImageDrawable(playButtonImage);
            seekbarHandler.removeCallbacks(updateSeekbar);
        }
    }

    private void resumeAudio() {
        if (!isPlaying) {
            isPlaying = true;
            mediaPlayer.start();
            playBtn.setImageDrawable(pauseButtonImage);
            updateRunnable();
            seekbarHandler.postDelayed(updateSeekbar, 0);
        }
    }

    private void playAudio(File fileToPlay) {
        musicPlayerLayoutBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.reset();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    playerSeekbar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    isPlaying = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopAudio();
                    playerHeader.setText("Finished");
                }
            });
            mediaPlayer.setOnErrorListener (new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("AudioListFragment","media play error");
                    return false;
                }
            });
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(playButtonImage);
        playerFilename.setText(fileToPlay.getName());
        playerHeader.setText("Playing");
        seekbarHandler =  new Handler(Looper.myLooper());
        //real time progress of audio
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    private void stopAudio() {
        if (isPlaying) {
            playBtn.setImageDrawable(pauseButtonImage);
            playerHeader.setText("Stopping");
            mediaPlayer.stop();
            isPlaying = false;
        }
    }
}