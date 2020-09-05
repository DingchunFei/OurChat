package com.example.gotsaintwho.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.adapter.ChatItemAdapter;
import com.example.gotsaintwho.pojo.ChatItem;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.MsgDBPojo;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private List<ChatItem> chatItemsList = new ArrayList<>();
    User user = null; //当前登录用户的信息
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        listView = view.findViewById(R.id.list_view);

        //从MainActivity获取登录的用户信息
        user = (User) getActivity().getIntent().getSerializableExtra("user_info");
        initView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView(){
        //从数据库中读取所有好友
        initChatItem(user.getUserId());
        ChatItemAdapter adapter = new ChatItemAdapter(getActivity(), R.layout.item_chat_list, chatItemsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatItem chatItem = chatItemsList.get(i);
                //登录成功后的页面跳转
                Intent intent = new Intent(getActivity(), DialogueActivity.class);
                User targetUser = new User();
                targetUser.setUserId(chatItem.getTargetUserId());
                targetUser.setUsername(chatItem.getTargetUsername());
                intent.putExtra("user_info", ChatListFragment.this.user);
                intent.putExtra("target_user_info",targetUser);
                startActivity(intent);
            }
        });
    }

    private void initChatItem(String currentUserId){
        //清空，不然chatItemsList东西会多出来
        chatItemsList = new ArrayList<>();

        //SQLiteDatabase db= DBUtil.getDb(getActivity());
        List<User> userList = DBUtil.findAllUser();

/*        //从sqlite中查询出所有的好友，然后填充ChatList
        List<User> userList = DataSupport.findAll(User.class);*/
        for(User user: userList){
            if(!user.getUserId().equals(currentUserId)){
                //判断一下，防止把自己加进去
                //查询最后一条聊天记录
                MsgDBPojo lastMsgDBPojo = DBUtil.findLastMsgDBPojoByTargetUserId(user.getUserId());
                String lastChat = null;
                if(lastMsgDBPojo!=null){
                    lastChat = lastMsgDBPojo.getContent();
                }

                //TODO 此处用户头像硬编码
                ChatItem chatItem = null;
                if(user.getUserId().equals("1"))
                    chatItem = new ChatItem(user.getUsername(),user.getUserId(),lastChat,R.drawable.avatar_1);
                else if(user.getUserId().equals("2"))
                    chatItem = new ChatItem(user.getUsername(),user.getUserId(),lastChat,R.drawable.avatar_2);
                else if(user.getUserId().equals("3"))
                    chatItem = new ChatItem(user.getUsername(),user.getUserId(),lastChat,R.drawable.avatar_3);
                chatItemsList.add(chatItem);
            }
        }
    }

}
