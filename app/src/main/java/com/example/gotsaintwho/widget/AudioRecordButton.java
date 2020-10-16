package com.example.gotsaintwho.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.example.gotsaintwho.AudioDialogManage;
import com.example.gotsaintwho.AudioManage;
import com.example.gotsaintwho.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;


public class AudioRecordButton extends AppCompatButton implements AudioManage.AudioStateListener {
    /**
     * Three state of button
     */
    private static final int STATE_NORMAL = 1;           //default state
    private static final int STATE_RECORDERING = 2;      //recording state
    private static final int STATE_WANT_TO_CALCEL = 3;   //cancel recording state

    private int mCurState = STATE_NORMAL;    // current state of button, default state
    private boolean isRecordering = false;   // whether is recording
    private boolean mReady;    // whether trigger onLongClick
    private static final int DISTANCE_Y_CANCEL = 50; //y location of the figure action, decide cancel or keep recording

    // Audio Dialog class
    private AudioDialogManage audioDialogManage;
    // Audio class
    private AudioManage mAudioManage;
    // Timing recording
    private int mTime;

    private int[] arrayImageId = new int[] {R.mipmap.icon_voice_1, R.mipmap.icon_voice_2, R.mipmap.icon_voice_3
            , R.mipmap.icon_voice_4, R.mipmap.icon_voice_5, R.mipmap.icon_voice_6};

    /**
     * 正常录音完成后的回调接口
     */
    public interface AudioFinishRecorderListener{
        void onFinish(int seconds, String FilePath);
    }

    private AudioFinishRecorderListener mListener;

    /**
     * 添加监听完成后回调接口的方法
     * @param listener
     */
    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        this.mListener = listener;
    }

    //constructor
    public AudioRecordButton(@NonNull Context context) {
        super(context);
    }

    public AudioRecordButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        //实例化对话框管理器
        audioDialogManage = new AudioDialogManage(getContext());

        //音频文件保存路径
        String dir = Environment.getExternalStorageDirectory()
                + "/deepreality/VoiceCache";
        //获取音频管理器
        mAudioManage = AudioManage.getInstance(dir);
        //监听准备完成接口
        mAudioManage.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                // 真正显示应该在audio end prepared以后
                //开始录音
                mAudioManage.prepareAudio();
                return false;
            }
        });

        mAudioManage.setOnAudioStatusUpdateListener(new AudioManage.OnAudioStatusUpdateListener() {

            //录音中....db为声音分贝，time为录音时长
            @Override
            public void onUpdate(double db, long time) {
                //根据分贝值来设置录音时音量图标的上下波动
                //audioDialogManage.mIcon.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                int imageIndex = 0;
                if (db > 50) {
                    imageIndex = ((int)db - 50) / 10;
                }
                if (imageIndex >= 5) {
                    imageIndex = 5;
                }
                audioDialogManage.ivVoice.setImageResource(arrayImageId[imageIndex]);
            }
        });
    }




    /*
     * Base on onTouchEvent (ACTION_DOWN, ACTION_MOVE,ACTION_UP ), to trigger audio state (recording or cancel)
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();   //get current action
        int x = (int) event.getX();       //get action location
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //press button, keep recording
                changeState(STATE_RECORDERING);
                break;

            case MotionEvent.ACTION_MOVE:
                // start recording, base on action, determine cancel or keep
                // recording.
                if (isRecordering) {
                    // isRecordering = true
                    // determine cancel or not
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CALCEL);
                    } else {
                        //keep recording
                        changeState(STATE_RECORDERING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                //not press button
                if (!mReady) {   //didn't trigger onLongClick
                    reset();
                    return super.onTouchEvent(event);
                }

                if (!isRecordering || mTime < 900) {
                    //start recording but time too short
                    audioDialogManage.tooShort(); //change dialog layout
                    mAudioManage.cancel(); //release audio, cancel file
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);// 延迟，1.3秒以后关闭“时间过短对话框”
                }

        }
        return super.onTouchEvent(event);
    }

    // determine cancel recording or not
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }
    // not press recording button, recover state
    private void reset() {
        isRecordering = false;
        mReady = false;  //didn't trigger onLongClick
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    /**
     * base on the onTouchEvent, change the dialog state ()
     * @param state
     */
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.send_speech_btn_normal_style);
                    setText("按住说话");
                    break;

                case STATE_RECORDERING:
                    setBackgroundResource(R.drawable.send_speech_btn_pres_style);
                    setText("松开发送");
                    if (isRecordering) {
                        // 更新Dialog.recording()
                        audioDialogManage.recording();
                    }
                    break;

                case STATE_WANT_TO_CALCEL:
                    setBackgroundResource(R.drawable.send_speech_btn_pres_style);
                    setText("取消发送");
                    // 更新Dialog.wantCancel()
                    audioDialogManage.wantToCancel();
                    break;
            }
        }
    }

    /*
     * 实现“准备完毕”接口
     */
    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    /**
     * 接收子线程数据，并用此数据配合主线程更新UI
     * Handler运行在主线程（UI线程）中，它与子线程通过Message对象传递数据。
     * Handler接受子线程传过来的(子线程用sedMessage()方法传弟)Message对象，把这些消息放入主线程队列中，配合主线程进行更新UI。
     */

    private static final int MSG_AUDIO_PREPARED = 0x110;   //准备完全
    private static final int MSG_CURRENT_TIME = 0x111;     //当前语音时长
    private static final int MSG_DIALOG_DISMISS = 0x112;    //销毁对话框
    private static final int MSG_COUNT_DOWN_DONE = 0x113;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:        //216:mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
                    audioDialogManage.showRecorderingDialog(); //display dialog
                    isRecordering = true;
                    //already recording, start a thread to get volumn and timing
                    new Thread(mUpdateCurTimeRunnable).start();
                    break;

                case MSG_CURRENT_TIME:          //265:mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                    audioDialogManage.updateCurTime(String.valueOf(mTime / 1000)); //dialog show time
                    break;

                case MSG_DIALOG_DISMISS:         //125:mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);
                    audioDialogManage.dismissDialog(); //close dialog
                    break;

                //stop recording
                case MSG_COUNT_DOWN_DONE:
                    mAudioManage.release(); // recording stop

                    // 正常录制结束，回调录音时间和录音文件完整路径——在播放的时候需要使用
                    if(mListener!=null){
                        //record finish, callback recording time and save path
                        mListener.onFinish(mTime /1000, mAudioManage.getCurrentFilePath());
                    }
                    audioDialogManage.dismissDialog();
                    reset();
                    break;
            }
        }
    };

    /**
     * 更新当前录音时长的runnable
     */
    //already recording, new thread to get volumn and timing
    private Runnable mUpdateCurTimeRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecordering) {
                try {
                    Thread.sleep(100);
                    mTime += 100;
                    mHandler.sendEmptyMessage(MSG_CURRENT_TIME);

                    if(mTime == 60 * 1000){
                        mHandler.sendEmptyMessage(MSG_COUNT_DOWN_DONE);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };
}
