package com.example.gotsaintwho;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 录制语音弹窗管理类
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
     * 默认的对话框的显示
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


}

