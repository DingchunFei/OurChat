package com.example.gotsaintwho.utils;

import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

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

    public static List<User> json2UserList(String jsonStr){
        ArrayList<User> userList = new ArrayList<>();

        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(jsonStr).getAsJsonArray();

        //加强for循环遍历JsonArray
        for (JsonElement userElement : jsonArray) {
            //使用GSON，直接转成Bean对象
            userList.add(gson.fromJson(userElement, User.class));
        }
        return userList;
    }
}
