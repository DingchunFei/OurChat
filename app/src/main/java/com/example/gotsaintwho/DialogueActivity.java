package com.example.gotsaintwho;

import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Dialog;
import android.widget.Toast;


import com.example.gotsaintwho.adapter.MsgAdapter;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.AudioDialogManage;
import com.example.gotsaintwho.widget.AudioRecordButton;

import java.util.List;


public class DialogueActivity extends BaseActivity {
    private static List<Msg> msgs;

    private EditText inputText;
    private Button send;
    private ImageView callAudio;
    private Button recordAudio;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private User user = null;
    private User targetUser = null;

    private MsgReceiver msgReceiver;
    private float y ;
    private TextView mStateTV;
    private ImageView mStateIV;
    private Dialog recordDialog;
    private static View view;
    private AudioDialogManage audioDialogManage;

    //定义一个广播接收器用来接收新消息
    class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //onReceive() used to capture the message
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
        currentTargetUserId = null;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogue);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
       /* user = (User) intent.getSerializableExtra("user_info");
        targetUser = (User) intent.getSerializableExtra("target_user_info");*/

        //设置toolbar的内容,变成Target user的id
        /*getSupportActionBar().setTitle(targetUser.getUsername());*/

        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        callAudio =  findViewById(R.id.btn_call_audio);
        recordAudio = findViewById(R.id.btn_record);
        inputText = findViewById(R.id.input_text);





        /*LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        //设置当前的目标用户
        currentTargetUserId = targetUser.getUserId();
        //获取与targetUser的聊天全部内容
        msgs = msgListMap.get(currentTargetUserId);
        if(msgs == null){   //说明这个用户是第一次聊天
            msgs = new LinkedList<>();
            //看看数据库里有没有之前的聊天记录
            List<MsgDBPojo> allMsgDBPojo = DBUtil.findAllMsgDBPojoByTargetUserId(currentTargetUserId);
            for (MsgDBPojo msgDBPojo:allMsgDBPojo){
                msgs.add(new Msg(msgDBPojo.getContent(),msgDBPojo.getType()));
            }
            msgListMap.put(targetUser.getUserId(),msgs);
        }*/

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

        recordAudio.setOnClickListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(int seconds, String FilePath) {

            }

            @Override
            public void onClick(View v) {
                showRecorderingDialog();
            }
        });




      /* ((AudioRecordButton)recordAudio).setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
           @Override
           public void onFinish(int seconds, String FilePath) {
               Toast.makeText(mContext, "发送成功！", Toast.LENGTH_SHORT).show();
               tb_audioRecorder = new Tb_AudioRecorder(FilePath, seconds);
               tbAudioRecorderList.add(tb_audioRecorder);
               booleanList.add(false);
               audioListAdapter.notifyDataSetChanged();
           }
        });*/







        /*((RecordButton) recordAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                LogUtil.d("录音结束回调");
                File file = new File(audioPath);
                if (file.exists()) {
                    //sendAudioMessage(audioPath,time);
                }
            }
        });*/




         /*recordAudio.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //if user touch the recordAudio button display the dialogue_audio_record layout
                y =event.getY();

                // the y value less than 0. The audio message will be cancel
                if(mStateTV!=null && mStateIV!=null &&y<0){
                    mStateTV.setText("release fingue to cancel the audio message");
                    mStateIV.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_cancel));
                }else if(mStateTV != null){
                    mStateTV.setText("手指上滑,取消发送");
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Start
                        break;
                    case MotionEvent.ACTION_UP:
                        // End
                        break;
                }
                return false;
            }
        });*/


        /*((RecordButton) recordAudio).setOnFinishedRecordListener(new RecordButton.OnFinishedRecordListener() {
            @Override
            public void onFinishedRecord(String audioPath, int time) {
                System.out.println("Record button relased");
                File file = new File(audioPath);
                if (file.exists()) {
                    // TODO: send audio code here in the future
                    //sendAudioMessage(audioPath,time);
                }
            }
        });*/


        /*
        adapter = new MsgAdapter(msgs);
        msgRecyclerView.setAdapter(adapter);*/
        /*send.setOnClickListener(new View.OnClickListener() {
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
                    DBUtil.saveMsgDBPojo(new MsgDBPojo(msg.getContent(), Msg.TYPE_SENT, currentTargetUserId));

                    //向服务器发送数据
                    sendMsg2Server(user, msg, targetUser.getUserId());
                }
            }
        });*/
    }

   /*private void sendMsg2Server(final User user, final Msg msg, final String targetUserId){
        DialogueMsgDTO dialogueMsgDTO = new DialogueMsgDTO(user.getUserId(), targetUserId, msg.getContent());
        //将用户发出的消息发送到服务器！
        DialogueQueue.sendDialogue(dialogueMsgDTO);
    }*/

    protected void showRecorderingDialog(){
        AudioDialogManage audioDialogManage = new AudioDialogManage(this);
        audioDialogManage.showRecorderingDialog();
    }




}