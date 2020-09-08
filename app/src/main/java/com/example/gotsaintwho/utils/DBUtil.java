package com.example.gotsaintwho.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class DBUtil {

    private static SQLiteDatabase db = null;

    static{
        db = new MyDatabaseHelper(MyApplication.getContext(), "OurChat.db", null, 5).getWritableDatabase();
    }

    public static boolean existUser(String userId){
        Cursor cursor = db.query("user", new String[]{"user_id"}, "user_id=?", new String[]{userId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex ("user_id"));
                if(id!=null ) {
                    cursor.close();
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        //不存在
        return false;
    }

    public static List<User> findAllUser(){
        List<User> userList = new ArrayList<>();
        Cursor cursor = db.query("user", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String userId = cursor.getString(cursor.getColumnIndex ("user_id"));
                String username = cursor.getString(cursor.getColumnIndex ("username"));
                String gender = cursor.getString(cursor.getColumnIndex("gender"));
                User user = new User();
                user.setUserId(userId);
                user.setUsername(username);
                user.setGender(gender);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public static void saveUser(User user) {
        ContentValues values = new ContentValues(); // 开始组装第一条数据 values.put("name", "The Da Vinci Code"); values.put("author", "Dan Brown"); values.put("pages", 454);
        values.put("user_id", user.getUserId());
        values.put("username", user.getUsername());
        values.put("gender", user.getGender());
        db.insert("user", null, values); // 插入数据
    }

    public static void deleteAllUser(){
        db.delete("user",null,null);
    }

    public static void deleteUser(String userId){
        db.delete("user","user_id=?",new String[]{userId});
    }

    public static void deleteMsgDBPojo(String userId){
        db.delete("msgdbpojo","targetUserId=?",new String[]{userId});
    }

    public static MsgDBPojo findLastMsgDBPojoByTargetUserId(String targetUserId){
        MsgDBPojo msgDBPojo = null;
        Cursor cursor = db.query("msgdbpojo", new String[]{"id","content","type"}, "targetUserId=?", new String[]{targetUserId}, null, null, "id desc");
        if (cursor.moveToFirst()) {
            String content = cursor.getString(cursor.getColumnIndex("content"));
            Integer type = cursor.getInt(cursor.getColumnIndex("type"));
            msgDBPojo = new MsgDBPojo(content,type,targetUserId);
        }
        cursor.close();
        return msgDBPojo;
    }

    public static List<MsgDBPojo> findAllMsgDBPojoByTargetUserId(String targetUserId){
        List<MsgDBPojo> msgDBPojoList = new ArrayList<>();
        Cursor cursor = db.query("msgdbpojo", new String[]{"id","content","type"}, "targetUserId=?", new String[]{targetUserId}, null, null, "id desc");
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex ("content"));
                int type = cursor.getInt(cursor.getColumnIndex ("type"));
                MsgDBPojo msgDBPojo = new MsgDBPojo(content,type,targetUserId);
                msgDBPojoList.add(msgDBPojo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return msgDBPojoList;
    }

    public static void saveMsgDBPojo(MsgDBPojo msgDBPojo) {
        ContentValues values = new ContentValues(); // 开始组装第一条数据 values.put("name", "The Da Vinci Code"); values.put("author", "Dan Brown"); values.put("pages", 454);
        values.put("content", msgDBPojo.getContent());
        values.put("type", msgDBPojo.getType());
        values.put("targetUserId", msgDBPojo.getTargetUserId());
        db.insert("msgdbpojo", null, values); // 插入数据
    }
}
