package com.example.gotsaintwho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gotsaintwho.adapter.ChatItemAdapter;
import com.example.gotsaintwho.adapter.UserAdapter;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.fragment.ChatListFragment;
import com.example.gotsaintwho.pojo.ChatItem;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class FindUserActivity extends AppCompatActivity {

    private static final String TAG = "FindUserActivity";
    EditText usernameEditText;
    RecyclerView userRecyclerView;
    Button findUserButton;
    List<User> userList = new ArrayList<>();
    UserAdapter userAdapter;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        usernameEditText = findViewById(R.id.usernameEditText);
        userRecyclerView = findViewById(R.id.user_recycler_view);
        findUserButton = findViewById(R.id.findUserButton);

        //先从数据库里查出所有用户
        //userListFromDb = DBUtil.findAllUser();
        //设定cyclerView的layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(layoutManager);

        //从Intent中拿出user
        currentUser = (User) getIntent().getSerializableExtra("user_info");

        userAdapter = new UserAdapter(userList);
        userRecyclerView.setAdapter(userAdapter);

        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                if(username ==null || username.equals("")){
                    Toast.makeText(FindUserActivity.this, "Please enter the username", Toast.LENGTH_SHORT).show();
                    return;
                }
                //封装一个要查找的user对象发送给服务器
                User user = new User();
                user.setUsername(username);

                //向服务器寻找匹配的用户
                HttpUtil.sendRequestWithHttpURLConnection("user/findUserByUsername", JsonUtil.user2Json(user), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        List<User> users= JsonUtil.json2UserList(response);
                        //数据改变后，刷新页面
                        userList.clear();
                        for (User user:users){
                            if(user.getUserId().equals(currentUser.getUserId())) continue;
                            //防止把当前用户放到待加好友中！
                            userList.add(user);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userAdapter.notifyDataSetChanged();
                            }
                        });
                        Log.e(TAG, "userList==================> "+ userList);
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FindUserActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
