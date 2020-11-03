package com.example.gotsaintwho.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_MSGDBPOJO = "create table msgdbpojo ( id integer primary key autoincrement, content text, type integer, targetUserId text)";
    public static final String CREATE_USER = "create table user ( user_id text primary key, username text, gender text)";
    public static final String CREATE_GROUP = "create table group_chat ( group_id integer primary key, user_ids text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); mContext = context;
//        context.deleteDatabase("OurChat.db");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("on create database", "in database helper!!!!!!!!!!");
        db.execSQL(CREATE_MSGDBPOJO);
        db.execSQL(CREATE_USER);
        db.execSQL(CREATE_GROUP);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists msgdbpojo");
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists group_chat");

        onCreate(db);
    }
}