package com.example.gotsaintwho.dialogue;

import android.util.Log;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 这个线程专门用来向服务器发送数据！
 */
public class SenderThread extends Thread {
    private Socket socket;
    private static final String TAG = "SenderThread";
    public SenderThread(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        //alway checking whether there are new drawing message sent from current user's whiteboard
        while (true) {
            //发现发送队列不为空，向服务器发送数据
            if (DialogueQueue.getSenderQueue().size() != 0) {
                //fetch the first element
                String msg = DialogueQueue.getSenderQueue().remove(0);
                PrintWriter out = null;
                try {
                    //send the data to server
                    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.println(msg);
                    out.flush();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {  //if there is no new msg, sleep for a while
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
