package com.example.gotsaintwho.dialogue;

import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.ParamUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class DialogueClient {

    public static void connect2DialogueServer(final User user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //连接到服务器
                try {
                    //Socket socket = new Socket("10.0.2.2", 8077);
                    //Socket socket = new Socket("192.168.0.40", 8077);
                    Socket socket = new Socket(ParamUtil.IP, 8077);
                    System.out.println("=======> 已连接上服务器");

                    //用来向服务器发送的数据
                    SenderThread senderThread = new SenderThread(socket);
                    senderThread.start();

                    //用来接收服务器发来的数据
                    ReceiverThread receiverThread  = new ReceiverThread(socket) ;
                    receiverThread.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
