package com.example.gotsaintwho;

import android.app.Application;
import android.content.Context;

import com.example.gotsaintwho.pojo.User;

public class MyApplication extends Application {
    private static Context context;
    //记录当前用户信息
    private static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MyApplication.user = user;
    }
}