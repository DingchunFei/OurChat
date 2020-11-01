package com.example.gotsaintwho.utils;

import android.nfc.Tag;
import android.nfc.tech.TagTechnology;
import android.util.Log;

import com.example.gotsaintwho.pojo.AllUserIdDTO;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.Like;
import com.example.gotsaintwho.pojo.LikeDTO;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.MomentIds;
import com.example.gotsaintwho.pojo.Reply;
import com.example.gotsaintwho.pojo.ReplyDTO;
import com.example.gotsaintwho.pojo.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    private static final String TAG = "JsonUtil";

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

        //遍历JsonArray
        for (JsonElement userElement : jsonArray) {
            //使用GSON，直接转成Bean对象
            userList.add(gson.fromJson(userElement, User.class));
        }
        return userList;
    }

    public static List<Moment> json2MomentList(String jsonStr){
        ArrayList<Moment> MomentList = new ArrayList<>();
        //Json的解析类对象
        JsonParser parser = new JsonParser();
        //将JSON的String 转成一个JsonArray对象
        JsonArray jsonArray = parser.parse(jsonStr).getAsJsonArray();

        //遍历JsonArray
        for (JsonElement momentElement : jsonArray) {
            //使用GSON，直接转成Bean对象
            MomentList.add(gson.fromJson(momentElement, Moment.class));
        }
        return MomentList;
    }

    public static String userId2JsonStr(AllUserIdDTO allUserId){
        String jsonStr = gson.toJson(allUserId, AllUserIdDTO.class);
        return jsonStr;
    }

    public static ReplyDTO json2ReplyDTO(String json){
        Gson gson = new Gson();
        ReplyDTO replyDTO =  gson.fromJson(json, ReplyDTO.class);
        return replyDTO;
    }

    public static String momentIds2Json(MomentIds momentIds){
        Gson gson = new Gson();
        return gson.toJson(momentIds);
    }

    public static LikeDTO json2LikeDTO(String json){
        Gson gson = new Gson();
        LikeDTO likeDTO = gson.fromJson(json, LikeDTO.class);
        return likeDTO;
    }

    public static Like json2Like(String json){
        Gson gson = new Gson();
        Like like = gson.fromJson(json, Like.class);
        return like;
    }

    public static String like2Json(Like like){
        Gson gson = new Gson();
        String json = gson.toJson(like);
        return json;
    }

/*    public static String json2MomentImgId(String jsonStr){
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            String img_id = jsonObject.getString("img_id");
            Log.e(TAG, img_id );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }*/

}
