package com.example.gotsaintwho.dialogue;

import android.util.Log;

import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.GroupDialogueMsgDTO;
import com.example.gotsaintwho.utils.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class DialogueQueue {

    private static List<String> senderQueue = new LinkedList<>();
    private static List<DialogueMsgDTO> receiverQueue = new LinkedList<>();
    private static final String IS_GROUP = "1";
    private static final String NOT_GROUP = "0";

    public static void sendDialogue(DialogueMsgDTO dialogueMsgDTO){
        System.out.println("消息放入发送队列");
        String str = JsonUtil.dialogueMsg2Json(dialogueMsgDTO);
        senderQueue.add(str);
    }

    public static void sendGroupDialogue(GroupDialogueMsgDTO groupDialogueMsgDTO){
        System.out.println("群消息放入发送队列");
        String str = JsonUtil.dialogueMsg2Json(groupDialogueMsgDTO);
        senderQueue.add(str);
    }

    public static List<String> getSenderQueue() {
        return senderQueue;
    }

    public static List<DialogueMsgDTO> getReceiverQueue() {
        return receiverQueue;
    }

}
