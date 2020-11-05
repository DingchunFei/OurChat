package com.example.gotsaintwho.dialogue;

import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.utils.JsonUtil;

import java.util.LinkedList;
import java.util.List;

public class DialogueQueue {

    private static List<String> senderQueue = new LinkedList<>();
    private static List<DialogueMsgDTO> receiverQueue = new LinkedList<>();

    public static void sendDialogue(DialogueMsgDTO dialogueMsgDTO){
        System.out.println("消息放入发送队列");
        String str = JsonUtil.dialogueMsg2Json(dialogueMsgDTO);
        senderQueue.add(str);
    }

    public static List<String> getSenderQueue() {
        return senderQueue;
    }

    public static List<DialogueMsgDTO> getReceiverQueue() {
        return receiverQueue;
    }

}
