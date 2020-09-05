package com.example.gotsaintwho.dialogue;

import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.utils.DBUtil;
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
                Map<String, List<Msg>> msgListMap = DialogueActivity.msgListMap;
                //目标list
                List<Msg> msgs = msgListMap.get(dialogueMsgDTO.getUserId());

                if(msgs == null){
                    //还未为该用户创建chatList,创建完并放入Map
                    msgs = new LinkedList<>();
                    msgListMap.put(dialogueMsgDTO.getUserId(),msgs);
                }

                //把服务器发来的数据放到userId与TargetUserId的聊天内容
                msgs.add(msg);

                //数据存入sqlite
                MsgDBPojo msgDBPojo = new MsgDBPojo(msg.getContent(), Msg.TYPE_RECEIVED,dialogueMsgDTO.getUserId());
                //存入数据库中
                DBUtil.saveMsgDBPojo(msgDBPojo);

                //判断当前窗口是否就是消息来源的TargetUserId
                if(DialogueActivity.currentTargetUserId.equals(dialogueMsgDTO.getUserId())){
                    //如果是，则要刷新Adapter
                    Message message = new Message();
                    message.what = DialogueActivity.UPDATE_ADAPTER;
                    //通过DialogueActivity提供的Handler来实现异步数据传递！
                    DialogueActivity.handler.sendMessage(message); // 将Message对象发送出去
                }
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
