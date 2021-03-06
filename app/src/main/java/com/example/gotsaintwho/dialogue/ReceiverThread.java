package com.example.gotsaintwho.dialogue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.example.gotsaintwho.AddGroupActivity;
import com.example.gotsaintwho.BaseActivity;
import com.example.gotsaintwho.DialogueActivity;
//import com.example.gotsaintwho.GlideApp;
import com.example.gotsaintwho.LoginActivity;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.fragment.ChatListFragment;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Group;
import com.example.gotsaintwho.pojo.GroupMsgDBPojo;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.JsonUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 这个线程专门用来处理服务器发发来的数据！
 */
public class ReceiverThread extends Thread {
    private Socket socket;

    private User user;
    private Group group;

    public ReceiverThread(Socket socket) {
        this.socket = socket;
    }

    private static final String TAG = "ReceiverThread";

    private void sendNotification(String userId, String username, String content){
        //用户点击后直接跳转到聊天窗
        Intent intent = new Intent(MyApplication.getContext(), DialogueActivity.class);
        //放入发送消息放的信息
        intent.putExtra("target_user_info",user);
        //放入自己的信息
        intent.putExtra("user_info",MyApplication.getUser());
        //最后flag要LAG_UPDATE_CURRENT不然收不到参数
        PendingIntent pi = PendingIntent.getActivity(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /**
         * 发送Notification
         */
        RemoteViews remoteViews = new RemoteViews(MyApplication.getContext().getPackageName(), R.layout.notification_new_message);

        remoteViews.setImageViewResource(R.id.notification_icon, R.mipmap.ic_launcher);

        remoteViews.setTextViewText(R.id.notification_headline, username);
        remoteViews.setTextViewText(R.id.notification_short_message, content);

        // build notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MyApplication.getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(remoteViews)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pi)
                        .setAutoCancel(true);

        final Notification notification = mBuilder.build();

        // set big content view for newer androids
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = remoteViews;
        }

        NotificationTarget notificationTarget = new NotificationTarget(
                MyApplication.getContext(),
                R.id.notification_icon,
                remoteViews,
                notification,
                1);
        //从网络读取图片作为头像
        String imgUri = ParamUtil.IMAGE_URI + userId + ".jpg";

        //GlideApp.with(MyApplication.getContext()).asBitmap().load(imgUri).into(notificationTarget);

        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notification);
    }

    DialogueMsgDTO dialogueMsgDTO;

    public void run() {
        BufferedReader br = null;
        try {
            //read the data from socket
            // 怎么做到只给目标用户发消息？
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = null;
            while ((str = br.readLine()) != null) {

                dialogueMsgDTO = JsonUtil.json2dialogueMsg(str);
                System.out.println(dialogueMsgDTO);
                Log.d("original message", str);
                Log.d("receiver thread", dialogueMsgDTO.getMessage());
                String message = dialogueMsgDTO.getMessage();

                // it's a single chat
                if (message.startsWith("group:-1")) {
                    //将发来的数据放到数据区
                    Msg msg = new Msg(message.substring(9), Msg.TYPE_RECEIVED);
                    //第一个参数是TargetUserId,第二个参数是userId与TargetUserId的聊天内容
                    Map<String, List<Msg>> msgListMap = BaseActivity.msgListMap;

                    //从聊天好友列表中拿到对应用户的聊天list
                    List<Msg> msgs = msgListMap.get(dialogueMsgDTO.getUserId());
                    if(msgs == null){
                        //说明还未为该用户创建chatList,创建完并放入Map
                        msgs = new LinkedList<>();
                        msgListMap.put(dialogueMsgDTO.getUserId(),msgs);
                    }

                    //把服务器发来的数据放到userId与TargetUserId的聊天内容
                    msgs.add(msg);
                    //数据存入sqlite
                    MsgDBPojo msgDBPojo = new MsgDBPojo(msg.getContent(), Msg.TYPE_RECEIVED,dialogueMsgDTO.getUserId());
                    //存入数据库中
                    DBUtil.saveMsgDBPojo(msgDBPojo);

                    //把这个用户信息查询出来
                    user = DBUtil.findUserById(dialogueMsgDTO.getUserId());
                    if(user.getUsername()==null){
                        //说明数据库中不存在这个用户，从服务器查询对方好友信息
                        HttpUtil.sendRequestWithHttpURLConnection("user/findUserById", JsonUtil.user2Json(user), new HttpCallbackListener() {
                            @Override
                            public void onFinish(String response) {
                                user = JsonUtil.json2User(response);
                                //往sqlite中存一份对方用户
                                DBUtil.saveUser(user);

                                /**
                                 * 收到新消息后广播
                                 */
                                Intent intent = new Intent("com.example.gotsaintwho.ReceiveMsg");
                                MyApplication.getContext().sendBroadcast(intent);
                                //发送通知
                                sendNotification(user.getUserId(),user.getUsername(),dialogueMsgDTO.getMessage());
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
                         * 收到新消息后广播
                         */
                        Intent intent = new Intent("com.example.gotsaintwho.ReceiveMsg");
                        MyApplication.getContext().sendBroadcast(intent);

                        //发送通知
                        sendNotification(user.getUserId(),user.getUsername(),dialogueMsgDTO.getMessage());
                    }
                }
                //****** group chat *********
                else {
                    int ind_1_slash = message.indexOf("/");
                    int ind_2_slash = message.indexOf("/", message.indexOf("/") + 1);
                    int ind_1_comma = message.indexOf(",");
                    int ind_1_colon = message.indexOf(":");
                    String messageContent = message.substring(ind_2_slash + 1);

                    String groupId = message.substring(ind_1_colon + 1, ind_1_comma);
                    String groupName = message.substring(ind_1_comma + 1, ind_1_slash);
                    String groupMembers = message.substring(ind_1_slash + 1, ind_2_slash).split(":")[1];
                    String[] userIdsInGroup = groupMembers.split(",");

                    final String senderUserId = dialogueMsgDTO.getUserId();
//                    Log.d("original message", message);
//                    Log.d("sender user id", senderUserId);
                    final String[] senderUserName = {""};
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpUtil.sendRequestWithHttpURLConnectionSync("user/findUserById", JsonUtil.user2Json(new User(senderUserId)), new HttpCallbackListener() {
                                @Override
                                public void onFinish(String response) {
                                    user = JsonUtil.json2User(response);
                                    senderUserName[0] = user.getUsername();
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
                        }
                    });

                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //将发来的数据放到数据区
                    Msg msg = new Msg(senderUserName[0] + ": " + messageContent, Msg.TYPE_RECEIVED);
                    Map<String, List<Msg>> groupMsgListMap = BaseActivity.groupMsgListMap;

                    //从聊天好友列表中拿到对应用户的聊天list
                    List<Msg> msgs = groupMsgListMap.get(groupId);
                    if(msgs == null){
                        //说明还未为该用户创建chatList,创建完并放入Map
                        msgs = new LinkedList<>();
                        groupMsgListMap.put(groupId, msgs);
                    }

                    //把服务器发来的数据放到userId与TargetUserId的聊天内容
                    msgs.add(msg);
                    //数据存入sqlite
                    GroupMsgDBPojo groupMsgDBPojo = new GroupMsgDBPojo(msg.getContent(), Msg.TYPE_RECEIVED, groupId, dialogueMsgDTO.getUserId());
                    //存入数据库中
                    DBUtil.saveGroupMsgDBPojo(groupMsgDBPojo);

                    // find the group
                    this.group = DBUtil.findGroupbyId(groupId);
                    List<User> usersInGroup= new ArrayList<>();

                    if(this.group == null){
                        // create the group if it doesn't exist
                        // find every user in the group
                        for (final String userId: userIdsInGroup) {
                            user = DBUtil.findUserById(userId);

                            if (user.getUsername() == null) {
                                //说明数据库中不存在这个用户，从服务器查询对方好友信息
                                Thread requestThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        HttpUtil.sendRequestWithHttpURLConnectionSync("user/findUserById", JsonUtil.user2Json(user), new HttpCallbackListener() {
                                            @Override
                                            public void onFinish(String response) {
                                                user = JsonUtil.json2User(response);
                                                //往sqlite中存一份对方用户
                                                // skip the current user
                                                if (!userId.equals(dialogueMsgDTO.getTargetUserId())) {
                                                    DBUtil.saveUser(user);
                                                    Log.d("find non user cur", userId);
                                                }
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
                                    }
                                });

                                requestThread.start();
                                try {
                                    requestThread.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            usersInGroup.add(user);
                        }

                        // save the new group in database
                        DBUtil.addUsersInGroup(Integer.parseInt(groupId), groupName, usersInGroup.get(0), usersInGroup);
                        /**
                         * 收到新消息后广播
                         */
                        Intent intent = new Intent("com.example.gotsaintwho.ReceiveGroupMsg");
                        intent.putExtra("group_id", groupId);
                        MyApplication.getContext().sendBroadcast(intent);
                        //发送通知
                        sendNotification(user.getUserId(),user.getUsername(),dialogueMsgDTO.getMessage());
                    }else{
                        Intent intent = new Intent("com.example.gotsaintwho.ReceiveGroupMsg");
                        intent.putExtra("group_id", groupId);
                        MyApplication.getContext().sendBroadcast(intent);
                    }
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
