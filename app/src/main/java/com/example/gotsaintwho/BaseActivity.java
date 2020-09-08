package com.example.gotsaintwho;

import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotsaintwho.fragment.ChatListFragment;
import com.example.gotsaintwho.pojo.Msg;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseActivity  extends AppCompatActivity {
    //第一个参数是TargetUserId,第二个参数是userId与TargetUserId的聊天内容
    //public static Map<String, List<Msg>> msgListMap = new ConcurrentHashMap<>();
    public static Map<String, List<Msg>> msgListMap = new ConcurrentHashMap<>();

    public static String currentTargetUserId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
