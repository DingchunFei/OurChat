package com.example.got_saint_who.dialogue;

import com.example.got_saint_who.utils.JsonUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class DialogueServer implements Runnable{
    //private List<Socket> sockets = new LinkedList<>();
    private HashMap<String,Socket> map = new HashMap<>();   //K:userId   V:ip

    @Override
    public void run() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(8077);
            System.out.println("Server is listening the port : 8077") ;

            while(true)  {
                Socket socket = ss.accept() ;
                //sockets.add(socket) ;
                //新用户加入
                String ip = socket.getInetAddress().getHostAddress() ;
                System.out.println("A new socket with ip:"+ip) ;
                Thread thread = new Thread(new ServerRunner(socket)) ;
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ServerRunner implements Runnable  {
        private Socket currentSocket ;   //当前socket

        public ServerRunner (Socket currentSocket)  {
            this.currentSocket = currentSocket ;
        }

        public void run()  {
            String ip = currentSocket.getInetAddress().getHostAddress() ;
            BufferedReader br = null ;
            try  {
                br = new BufferedReader(new InputStreamReader(currentSocket.getInputStream())) ;
                String str = null ;
                while((str = br.readLine()) != null)  {
                    System.out.print("New data is coming from "+ ip +" ===> "+str);
                    DialogueMsgDTO dialogueMsgDTO = JsonUtil.json2dialogueMsg(str);
                    if(dialogueMsgDTO.getUserId()!=null && dialogueMsgDTO.getMessage()==null && dialogueMsgDTO.getTargetUserId()==null){
                        //说明这是一个刚登录时的请求，用来让服务器将userId和Socket关联起来！
                        map.put(dialogueMsgDTO.getUserId(),currentSocket);  //.getInetAddress().getHostAddress()
                    }else{
                        //这是一个普通的Dialogue信息
                        Socket targetUserSocket = map.get(dialogueMsgDTO.getTargetUserId());
                        if(targetUserSocket==null){
                            //对方IP没找到，说明没连接到服务器，不处理
                            continue;
                        }
                        else{
                            //向TargetUser发送消息
                            System.out.println("to "+ targetUserSocket.getInetAddress().getHostAddress() +" ===> "+str);
                            sendDialogue(dialogueMsgDTO,targetUserSocket);
                        }
                    }
                }
            }catch(IOException e)  {
                e.printStackTrace();
            }
        }

        //向TargetUserId所在的Socket发送消息
        private void sendDialogue(DialogueMsgDTO dialogueMsgDTO, Socket targetSocket){
            BufferedReader br = null ;
            try  {
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(targetSocket.getOutputStream())) ;
                pw.println(JsonUtil.dialogueMsg2Json(dialogueMsgDTO)) ;
                pw.flush();
            }catch(IOException e)  {
                e.printStackTrace();
            }
        }

    }



}

