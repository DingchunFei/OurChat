package com.example.got_saint_who.utils;


import com.example.got_saint_who.dialogue.DialogueMsgDTO;
import com.example.got_saint_who.pojo.User;
import com.google.gson.Gson;

public class JsonUtil {

    private static Gson gson = new Gson();

    public static String user2Json(User user){
        return gson.toJson(user);
    }

    public static User json2User(String jsonStr){
        return gson.fromJson(jsonStr,User.class);
    }

    public static String dialogueMsg2Json(DialogueMsgDTO dialogueMsgDTO) {return gson.toJson(dialogueMsgDTO);}

    public static DialogueMsgDTO json2dialogueMsg(String jsonStr){
        return gson.fromJson(jsonStr, DialogueMsgDTO.class);
    }

}
