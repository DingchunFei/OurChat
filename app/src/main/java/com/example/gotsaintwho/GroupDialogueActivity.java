package com.example.gotsaintwho;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gotsaintwho.adapter.MsgAdapter;
import com.example.gotsaintwho.audio.AudioDialogManage;
import com.example.gotsaintwho.dialogue.DialogueQueue;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Group;
import com.example.gotsaintwho.pojo.GroupDialogueMsgDTO;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;

import java.util.LinkedList;
import java.util.List;


public class GroupDialogueActivity extends BaseActivity {

    private static List<Msg> msgs;
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private User user = null;
    private Group targetGroup = null;
    private MsgReceiver msgReceiver;

    //audio dialog
    private ImageView callAudio;
    private Button recordAudio;
    private AudioDialogManage audioDialogManage;
    private static final int DISTANCE_Y_CANCEL = 50;
    private Chronometer getTime;
    private long recordingTime;
    boolean wantToCancel = false;

    //定义一个广播接收器用来接收新消息
    class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //一旦接收到消息传递的广播，就刷新页面！
            adapter.notifyItemInserted(msgs.size() - 1); // 当有新消息时， 刷新RecyclerView中的显示
            msgRecyclerView.scrollToPosition(msgs.size() - 1); // 将 RecyclerView定位到最后一行
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * 因为这个Activity类型是SingleTask.防止从Notification进入后，重复两次DialogueActivity
         * 但是如果不在这个方法里刷新DialgoueActivity的内容的话，从Notification点入后msg内容没
         * 有更新。因此这个方法里要刷新一下！
         */
        super.onNewIntent(intent);
        adapter.notifyItemInserted(msgs.size() - 1);
        msgRecyclerView.scrollToPosition(msgs.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置为null
        currentTargetGroupId = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * resume后开始监听广播
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.gotsaintwho.ReceiveMsg");
        msgReceiver = new MsgReceiver();
        registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(msgReceiver!= null){
            //退出后就取消对广播的监听
            unregisterReceiver(msgReceiver);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user_info");
        targetGroup = (Group) intent.getSerializableExtra("target_group_info");

        // set toobar's group name
        getSupportActionBar().setTitle(targetGroup.getGroupName());

        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        //audio dialog
        callAudio =  findViewById(R.id.btn_call_audio);
        recordAudio = findViewById(R.id.btn_record);

        // set current group id
        currentTargetGroupId = targetGroup.getGroupId();
        //获取与targetGroup的聊天全部内容
        msgs = groupMsgListMap.get(currentTargetGroupId);
        if(msgs == null){   //说明这个用户是第一次聊天
            msgs = new LinkedList<>();
            /*//看看数据库里有没有之前的聊天记录    !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            List<MsgDBPojo> allMsgDBPojo = DBUtil.findAllMsgDBPojoByTargetUserId(currentTargetGroupId);
            for (MsgDBPojo msgDBPojo:allMsgDBPojo){
                msgs.add(new Msg(msgDBPojo.getContent(),msgDBPojo.getType()));
            }
            msgListMap.put(targetUser.getUserId(),msgs);*/
        }

        adapter = new MsgAdapter(msgs);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    final Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgs.add(msg);
                    adapter.notifyItemInserted(msgs.size() - 1); // 当有新消息时， 刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgs.size() - 1); // 将 RecyclerView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容

                    //向数据库存入新数据
                    DBUtil.saveMsgDBPojo(new MsgDBPojo(msg.getContent(), Msg.TYPE_SENT, currentTargetGroupId));

                    //向服务器发送数据
                    sendMsg2Server(user, msg, targetGroup.getGroupId());
                }
            }
        });

        //callAudio button
        callAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callAudio.setText("r");
                if(recordAudio.isShown()){
                    recordAudio.setVisibility(View.GONE);
                    inputText.setVisibility(View.VISIBLE);
                }else{
                    inputText.setVisibility(View.GONE);
                    recordAudio.setVisibility(View.VISIBLE);
                }
            }
        });

        recordAudio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showRecorderingDialog();
                return false;
            }
        });

        recordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        showRecorderingDialog();
                        recordAudio.setText("正在录音,松手发送");
                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (wantToCancel(x, y)) {
                            audioDialogManage.cancelMessageDialog();
                            recordAudio.setText("取消发送");
                            wantToCancel = true;

                        } else {
                            audioDialogManage.stopCancelMessageDialog();
                            recordAudio.setText("正在录音,松手发送");
                            wantToCancel = false;
                        }
                        break;


                    case MotionEvent.ACTION_UP:

                        if (wantToCancel = false && lessThen1s() == true){
                            //more up don't want cancel and record less than 1s
                            audioDialogManage.tooShortDialog();
                            recordAudio.setText("按下录音");
                            //cancel recording
                        }

                        if(wantToCancel = false && lessThen1s() == false) {
                            //more up don't want cancel and record more than 1s
                            cancelShowRecorderingDialog();
                            audioDialogManage.stopTiming();
                            recordAudio.setText("按下录音");
                            //stop recording and sent
                        }

                        if(wantToCancel = true){
                            //more up and to cancel
                            cancelShowRecorderingDialog();
                            audioDialogManage.cancelMessageDialog();
                            recordAudio.setText("按下录音");
                            //cancel recording
                        }
                        if(lessThen1s() == true){
                            //record less than 1s
                            audioDialogManage.tooShortDialog();
                            recordAudio.setText("按下录音");
                            //cancel recording
                        }
                        break;

                }
                return false;
            }
        });



        //end onCreate
    }


    protected void showRecorderingDialog(){
        audioDialogManage = new AudioDialogManage(this);
        audioDialogManage.recorderingDialog();
        audioDialogManage.startTiming();


    }
    protected void cancelShowRecorderingDialog(){
        audioDialogManage.dismissDialog();
    }

    private boolean wantToCancel(int x, int y) {
        // 判断手指的滑动是否超出范围
        if (x < 0 || x > recordAudio.getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > recordAudio.getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }


    private boolean lessThen1s(){
        recordingTime = audioDialogManage.getRecordingTime();
        if (recordingTime < 1000){
            return true;
        }else{
            return false;
        }
    }



    private void sendMsg2Server(final User user, final Msg msg, final String targetGroupId){
        GroupDialogueMsgDTO groupDialogueMsgDTO = new GroupDialogueMsgDTO(user.getUserId(), targetGroupId, msg.getContent());
        //将用户发出的消息发送到服务器！
        DialogueQueue.sendGroupDialogue(groupDialogueMsgDTO);
    }

}