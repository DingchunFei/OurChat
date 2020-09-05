package com.example.gotsaintwho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DialogueActivity extends AppCompatActivity {

    public static final int UPDATE_ADAPTER = 1;
    //private List<Msg> msgList = new ArrayList<>();
    //第一个参数是TargetUserId,第二个参数是userId与TargetUserId的聊天内容
    public static Map<String, List<Msg>> msgListMap = new ConcurrentHashMap<>();
    private static List<Msg> msgs;

    private EditText inputText;
    private Button send;
    public static RecyclerView msgRecyclerView;
    public static String currentTargetUserId;
    public static MsgAdapter adapter;
    private User user = null;
    private User targetUser = null;

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

        //设置toolbar的内容
        getSupportActionBar().setTitle(targetUser.getUsername());

        //initMsgs(); // 初始化消息数据
        inputText = findViewById(R.id.input_text);
        send = findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        //设置当前的目标用户
        currentTargetUserId = targetUser.getUserId();
        //获取与targetUser的聊天全部内容
        msgs = msgListMap.get(targetUser.getUserId());
        if(msgs == null){   //说明这个用户是第一次聊天
            msgs = new LinkedList<>();
            //看看数据库里有没有之前的聊天记录
            List<MsgDBPojo> allMsgDBPojo = DBUtil.findAllMsgDBPojoByTargetUserId(currentTargetUserId);
            for (MsgDBPojo msgDBPojo:allMsgDBPojo){
                msgs.add(new Msg(msgDBPojo.getContent(),msgDBPojo.getType()));
            }
            msgListMap.put(targetUser.getUserId(),msgs);
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
                    MsgDBPojo msgDBPojo = new MsgDBPojo(msg.getContent(), Msg.TYPE_SENT, currentTargetUserId);
                    //存入数据库中
                    //SQLiteDatabase db = DBUtil.getDb(DialogueActivity.this);
                    DBUtil.saveMsgDBPojo(msgDBPojo);

                    //向服务器发送数据
                    sendMsg2Server(user, msg, targetUser.getUserId());
                }
            }
        });
    }

    //通过这个方法让其他方法可以更新Adapter
    public static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_ADAPTER: // 服务器发来新的消息，因此要刷新adapter
                    adapter.notifyItemInserted(msgs.size() - 1); // 当有新消息时， 刷新RecyclerView中的显示
                    msgRecyclerView.scrollToPosition(msgs.size() - 1); // 将 RecyclerView定位到最后一行
                default:
                    break;
            }
        }
    };

    private void sendMsg2Server(final User user, final Msg msg, final String targetUserId){
        DialogueMsgDTO dialogueMsgDTO = new DialogueMsgDTO(user.getUserId(), targetUserId, msg.getContent());
        //将用户发出的消息发送到服务器！
        DialogueQueue.sendDialogue(dialogueMsgDTO);
    }

/*    private void initMsgs() {
        Msg msg1 = new Msg("Hello guy.", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello. Who is that?", Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }*/
}