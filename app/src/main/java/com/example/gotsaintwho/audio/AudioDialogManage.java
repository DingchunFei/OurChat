package com.example.gotsaintwho.audio;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gotsaintwho.R;

/**
 * 录制语音弹窗管理类
 */
public class AudioDialogManage {
    private Dialog mDialog;

    private LinearLayout mBackground;

    //录音时长
    private Chronometer mTime;
    //录音提示文字
    private TextView mLabel;

    private Context mContext;
    public long recordingTime = 0;


    public AudioDialogManage(Context context) {
        this.mContext = context;
    }

    /**
     * 默认的对话框的显示
     */
    public void recorderingDialog() {
        mDialog = new Dialog(mContext, R.style.Translucent);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(
                R.layout.dialogue_audio_record, null);
        mDialog.setContentView(view);
        mTime = mDialog.findViewById(R.id.recorder_dialog_time_tv);
        mLabel = mDialog.findViewById(R.id.recorder_dialog_label);
        mBackground= mDialog.findViewById(R.id.recorder_dialog_bg);
        mDialog.show();
    }
    public void cancelMessageDialog() {
        mLabel.setText("Release finger , Cancel messages");
    }
    public void stopCancelMessageDialog() {
        mLabel.setText("Release finger , Send messages");
    }

    public void tooShortDialog() {
        mLabel.setText("Speek too short");
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void startTiming(){
        mTime.setBase(SystemClock.elapsedRealtime());
        mTime.start();
    }


    public void stopTiming(){
        mTime.stop();
        recordingTime = SystemClock.elapsedRealtime()- mTime.getBase();
    }

    public long getRecordingTime() {
        return recordingTime;
    }


    public Chronometer getTime() {
        return this.mTime;
    }



}

