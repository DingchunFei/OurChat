package com.example.gotsaintwho.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotsaintwho.BaseActivity;
import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.adapter.ChatItemAdapter;
import com.example.gotsaintwho.pojo.ChatItem;
import com.example.gotsaintwho.pojo.Group;
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
    private static ChatItemAdapter adapter = null;

    private MsgReceiver msgReceiver;

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

    //定义一个广播接收器用来接收新消息
    class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "new msg received", Toast.LENGTH_SHORT).show();
            //一旦接收到消息传递的广播，就刷新页面！
            initView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //刷新页面
        initView();

        /**
         * resume后开始监听广播
         */
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.gotsaintwho.ReceiveMsg");
        msgReceiver = new MsgReceiver();
        getActivity().registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(msgReceiver!= null){
            //退出后就取消对广播的监听
            getActivity().unregisterReceiver(msgReceiver);
        }
    }

    private void initView(){
        //从数据库中读取所有好友
        initChatItem(user.getUserId());
        // read every group from database
        initChatItemGroup(user);

        adapter = new ChatItemAdapter(getActivity(), R.layout.item_chat_list, chatItemsList);
        listView.setAdapter(adapter);

        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu arg0, View arg1,
                                            ContextMenu.ContextMenuInfo arg2) {
                //arg0.setHeaderTitle("Delete user");
                arg0.add(0, 0, 0, "Delete content");
                arg0.add(0, 1, 0, "Delete user");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatItem chatItem = chatItemsList.get(i);
                //登录成功后的页面跳转
                Intent intent = new Intent(getActivity(), DialogueActivity.class);
                User targetUser = new User();
                targetUser.setUserId(chatItem.getTargetUserId());
                targetUser.setUsername(chatItem.getTargetUsername());
                intent.putExtra("user_info", user);
                intent.putExtra("target_user_info",targetUser);
                startActivity(intent);
            }
        });
    }

    private void initChatItem(String currentUserId){
        //清空，不然chatItemsList东西会多出来
        chatItemsList.clear();

        List<User> userList = DBUtil.findAllUser();

        for(User user: userList){
            if(!user.getUserId().equals(currentUserId)){
                //判断一下，防止把自己加进去
                //查询最后一条聊天记录
                MsgDBPojo lastMsgDBPojo = DBUtil.findLastMsgDBPojoByTargetUserId(user.getUserId());
                String lastChat = null;
                if(lastMsgDBPojo!=null){
                    lastChat = lastMsgDBPojo.getContent();
                }

                ChatItem chatItem =  new ChatItem(user.getUsername(),user.getUserId(),lastChat);

                chatItemsList.add(chatItem);
            }
        }
    }

    private void initChatItemGroup(User user){
        List<Group> groupList = DBUtil.findAllGroups(user);

        // test
        for (Group group: groupList) {
            Log.d("chat list fragment", group.toString());
        }


    }

    //设置菜单内容和事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int pos = (int)info.id;
        ChatItem chatItem = chatItemsList.get(pos);
        switch(item.getItemId()){
            case 0:
                //Toast.makeText(getActivity(), "点击了 delete content", Toast.LENGTH_SHORT).show();
                //删除缓存的聊天内容
                BaseActivity.msgListMap.remove(chatItem.getTargetUserId());
                //删除用户聊天记录
                DBUtil.deleteMsgDBPojo(chatItem.getTargetUserId());
                //将最后的聊天内容删除
                chatItem.setLastChat(null);
                //更新listview的数据
                adapter.notifyDataSetChanged();
                return true;
            case 1:
                //Toast.makeText(getActivity(), "点击了 delete user", Toast.LENGTH_SHORT).show();
                //移除list的某项数据，注意remove()里的数据只能是int，这里用了强制转换，将long转换成int
                chatItemsList.remove((int)info.id);

                //从msgListMap也要删除,不然插入数据库会出错!
                BaseActivity.msgListMap.remove(chatItem.getTargetUserId());

                //删除该用户
                DBUtil.deleteUser(chatItem.getTargetUserId());
                //删除用户聊天记录
                DBUtil.deleteMsgDBPojo(chatItem.getTargetUserId());
                //更新listview的数据
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }


}
