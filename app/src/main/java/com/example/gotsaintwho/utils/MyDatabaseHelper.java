package com.example.gotsaintwho.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_MSGDBPOJO = "create table msgdbpojo ( id integer primary key autoincrement, content text, type integer, targetUserId text)";
    public static final String CREATE_USER = "create table user ( user_id text primary key, username text, gender text)";

    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version); mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSGDBPOJO);
        db.execSQL(CREATE_USER);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists msgdbpojo");
        db.execSQL("drop table if exists user");
        onCreate(db);
    }
}