package com.example.gotsaintwho;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gotsaintwho.adapter.MsgAdapter;
import com.example.gotsaintwho.dialogue.DialogueQueue;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;

import java.util.LinkedList;
import java.util.List;


public class DialogueActivity extends BaseActivity {
    private static List<Msg> msgs;

    private EditText inputText;
    private Button send;
    private Button callAudio;
    private Button recordAudio;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private User user = null;
    private User targetUser = null;

    private MsgReceiver msgReceiver;

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
        user = (User) intent.getSerializableExtra("user_info");
        targetUser = (User) intent.getSerializableExtra("target_user_info");

        //设置toolbar的内容,变成Target user的id
        getSupportActionBar().setTitle(targetUser.getUsername());

        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        callAudio =  findViewById(R.id.btn_call_audio);
        recordAudio = findViewById(R.id.btn_record);
        inputText = findViewById(R.id.input_text);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
        }

        //callAudio button
         callAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //callAudio.setText("r");
                if(recordAudio.isShown()){
                    System.out.println("recordBtn is show, hide it");
                    recordAudio.setVisibility(View.GONE);
                    inputText.setVisibility(View.VISIBLE);
                }else{
                    System.out.println("inputText is show, hide it");
                    inputText.setVisibility(View.GONE);
                    recordAudio.setVisibility(View.VISIBLE);
                }
            }
        });

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
                    DBUtil.saveMsgDBPojo(new MsgDBPojo(msg.getContent(), Msg.TYPE_SENT, currentTargetUserId));

                    //向服务器发送数据
                    sendMsg2Server(user, msg, targetUser.getUserId());
                }
            }
        });
    }

    private void sendMsg2Server(final User user, final Msg msg, final String targetUserId){
        DialogueMsgDTO dialogueMsgDTO = new DialogueMsgDTO(user.getUserId(), targetUserId, msg.getContent());
        //将用户发出的消息发送到服务器！
        DialogueQueue.sendDialogue(dialogueMsgDTO);
    }
}