package com.example.gotsaintwho.dialogue;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.gotsaintwho.BaseActivity;
import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.LoginActivity;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.fragment.ChatListFragment;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个线程专门用来处理服务器发发来的数据！
 */
public class ReceiverThread extends Thread {
    private Socket socket;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
    }

    private static final String TAG = "ReceiverThread";
    public void run() {
        BufferedReader br = null;
        try {
            //read the data from socket
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = null;
            while ((str = br.readLine()) != null) {
                DialogueMsgDTO dialogueMsgDTO = JsonUtil.json2dialogueMsg(str);
                System.out.println(dialogueMsgDTO);
                //将发来的数据放到数据区
                Msg msg = new Msg(dialogueMsgDTO.getMessage(), Msg.TYPE_RECEIVED);
                //第一个参数是TargetUserId,第二个参数是userId与TargetUserId的聊天内容
                Map<String, List<Msg>> msgListMap = BaseActivity.msgListMap;
                //目标list
                List<Msg> msgs = msgListMap.get(dialogueMsgDTO.getUserId());

                if(msgs == null){
                    //还未为该用户创建chatList,创建完并放入Map
                    msgs = new LinkedList<>();
                    msgListMap.put(dialogueMsgDTO.getUserId(),msgs);
                    //查看sqlite中是否有该用户，即是否是好友
                    if(!DBUtil.existUser(dialogueMsgDTO.getUserId())){
                        //不是好友，得到对方user id
                        String userId = dialogueMsgDTO.getUserId();
                        User user = new User();
                        user.setUserId(userId);
                        HttpUtil.sendRequestWithHttpURLConnection("user/findUserById", JsonUtil.user2Json(user), new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                User userWithusername = JsonUtil.json2User(response);
                                //往sqlite中存一份对方用户
                                DBUtil.saveUser(userWithusername);
                                /**
                                 * 收到新消息后发送广播
                                 */
                                Intent intent = new Intent("com.example.gotsaintwho.ReceiveMsg");
                                MyApplication.getContext().sendBroadcast(intent);
                            }

                            @Override
                            public void onError(Exception e) {
/*                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MyApplication.getContext(),"Server error",Toast.LENGTH_SHORT).show();
                                    }
                                });*/
                            }
                        });
                    }else{
                        /**
                         * 收到新消息后发送广播
                         */
                        Intent intent = new Intent("com.example.gotsaintwho.ReceiveMsg");
                        MyApplication.getContext().sendBroadcast(intent);
                    }
                }else{
                    /**
                     * 收到新消息后发送广播
                     */
                    Intent intent = new Intent("com.example.gotsaintwho.ReceiveMsg");
                    MyApplication.getContext().sendBroadcast(intent);
                }


                //把服务器发来的数据放到userId与TargetUserId的聊天内容
                msgs.add(msg);

                //数据存入sqlite
                MsgDBPojo msgDBPojo = new MsgDBPojo(msg.getContent(), Msg.TYPE_RECEIVED,dialogueMsgDTO.getUserId());
                //存入数据库中
                DBUtil.saveMsgDBPojo(msgDBPojo);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
