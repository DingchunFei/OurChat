package com.example.gotsaintwho;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * manage recording audio layout
 */
public class AudioDialogManage {
    private Dialog mDialog;
    //麦克风及删除图标
    public ImageView mIcon;
    //录音时长
    private TextView mTime;
    //录音提示文字
    private TextView mLabel;
    //音量分贝
    public ImageView ivVoice;
    private Context mContext;


    public AudioDialogManage(Context context) {
        this.mContext = context;
    }

    /**
     * show default recording audio dialog
     */
    public void showRecorderingDialog() {
        mDialog = new Dialog(mContext, R.style.Translucent);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(
                R.layout.dialogue_audio_record, null);
        mDialog.setContentView(view);
        mIcon = mDialog.findViewById(R.id.recorder_dialog_icon);
        mTime = mDialog.findViewById(R.id.recorder_dialog_time_tv);
        mLabel = mDialog.findViewById(R.id.recorder_dialog_label);
        ivVoice = mDialog.findViewById(R.id.recorder_dialog_ivVoice);
        mDialog.show();
    }

    //Control ImageView and TextView base on the state
    /**
     * recording state
     */
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.mipmap.icon_microphone);
            mLabel.setBackgroundColor(Color.parseColor("#00000000"));
            mLabel.setText("手指松开 开始发送");
        }
    }

    /**
     * cancel recording state
     */
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.GONE);

            mIcon.setImageResource(R.mipmap.icon_rubbish_bin);
            mLabel.setBackgroundColor(Color.parseColor("#AF2831"));
            mLabel.setText("手指上滑 取消发送");
        }
    }

    /**
     * Record too short
     */
    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mTime.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            ivVoice.setVisibility(View.GONE);

            mIcon.setImageResource(R.mipmap.icon_punctuation_mark);
            mLabel.setBackgroundColor(Color.parseColor("#00000000"));
            mLabel.setText("说话时间太短");
        }
    }

    /**
     * close dialog
     */
    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * show time
     * @param time
     */
    public void updateCurTime(String time) {
        if (mDialog != null && mDialog.isShowing()) {
            mTime.setText(time);
        }
    }


}

