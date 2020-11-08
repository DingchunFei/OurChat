package com.example.gotsaintwho;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gotsaintwho.audio.AudioListFragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class VoiceMemoActivity extends AppCompatActivity {
    private static final String TAG = "VoiceMemoActivity";
    private ImageButton listBtn;
    private ImageButton recordBtn;
    private boolean isRecording = false;
    private TextView filenameText;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String readExternalStorage =Manifest.permission.READ_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;

    public MediaRecorder mediaRecorder;
    private String recordPath;
    private Chronometer timer;

    public VoiceMemoActivity() {
        mediaRecorder = new MediaRecorder();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_memo);
        recordBtn = findViewById(R.id.record_btn);
        timer = findViewById(R.id.record_timer);
        filenameText = findViewById(R.id.record_filename);
        listBtn = findViewById(R.id.record_list_btn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                FragmentManager fragmentManager  = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ();
                AudioListFragment  audioListFragment = new AudioListFragment();
                fragmentTransaction.add (R.id.listContainer,audioListFragment);
                fragmentTransaction.commit ();
            }
        });
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (isRecording) {
                    //Stop Recording
                    stopRecording();
                    recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_stopped, null));
                } else {
                    if (checkRecordPermissions()) {
                        if(checkWriteExternalStorage()){
                            if(checkReadExternalStorage()){
                                startRecording();
                                recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_recording, null));
                            }
                        }
                    }
                }
            }
        });

    }//end onCreate

    @Override
    protected void onStop() {
        super.onStop();
        stopRecording();
    }

    private boolean checkRecordPermissions() {
        if(ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }
    private boolean checkWriteExternalStorage() {
        if(ActivityCompat.checkSelfPermission(this, writeExternalStorage) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{writeExternalStorage}, PERMISSION_CODE);
            return false;
        }
    }
    private boolean checkReadExternalStorage() {
        if(ActivityCompat.checkSelfPermission(this, readExternalStorage) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{readExternalStorage}, PERMISSION_CODE);
            return false;
        }
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
//        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
        Date now = new Date();
        recordPath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Music/NewRecording_" + formatter.format(now) + ".mp3";
        filenameText.setText("Recording, File Name : " + recordPath);

        try {
            isRecording = true;
            this.mediaRecorder.reset();
            this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            this.mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            this.mediaRecorder.setOutputFile(recordPath);
            this.mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            this.mediaRecorder.prepare();
            this.mediaRecorder.start();
            //emulator can not get audio input from laptop
            Log.d("media recorder", "record to " + recordPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        //stop recording
        if (isRecording) {
            timer.stop();
            filenameText.setText("Recording Stopped, File Saved : " + recordPath);
            this.mediaRecorder.stop();
            isRecording = false;
            Log.d("media recorder", "stop record ");
        }
    }
}
