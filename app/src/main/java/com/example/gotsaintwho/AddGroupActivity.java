package com.example.gotsaintwho;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gotsaintwho.adapter.GroupAdapter;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {
    private static final String TAG = "AddGroupActivity";
    public static final List<User> CURRENT_USERS_IN_GROUP = new ArrayList<>();
    static int nextGroup = 1;
    EditText usernameEditText;
    RecyclerView userRecyclerView;
    Button findUserButton;
    Button createGroupButton;
    List<User> userList = new ArrayList<>();
    GroupAdapter groupAdapter;
    User currentUser;

    public static int getNextGroup() {
        return nextGroup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        AddGroupActivity.nextGroup = DBUtil.getMaxGroupId() + 1;

        usernameEditText = findViewById(R.id.usernameEditText);
        userRecyclerView = findViewById(R.id.user_recycler_view);
        findUserButton = findViewById(R.id.findUserButton);
        createGroupButton = findViewById(R.id.doneButton);

        //设定cyclerView的layoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userRecyclerView.setLayoutManager(layoutManager);

        //从Intent中拿出user
        currentUser = (User) getIntent().getSerializableExtra("user_info");

        groupAdapter = new GroupAdapter(userList);
        userRecyclerView.setAdapter(groupAdapter);

        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                if (username.equals("")) {
                    Toast.makeText(AddGroupActivity.this, "Please enter the username", Toast.LENGTH_SHORT).show();
                    return;
                }
                //封装一个要查找的user对象发送给服务器
                User user = new User();
                user.setUsername(username);

                //向服务器寻找匹配的用户
                HttpUtil.sendRequestWithHttpURLConnection("user/findUserByUsername", JsonUtil.user2Json(user), new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        List<User> users = JsonUtil.json2UserList(response);
                        //数据改变后，刷新页面
                        userList.clear();
                        for (User user : users) {
                            if (user.getUserId().equals(currentUser.getUserId())) continue;
                            //防止把当前用户放到待加好友中！
                            userList.add(user);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                groupAdapter.notifyDataSetChanged();
                            }
                        });
                        Log.d(TAG, "userList==================> " + userList);
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddGroupActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Log.d("create group", "clicked");
                // must add at least one user
                if (CURRENT_USERS_IN_GROUP.size() < 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddGroupActivity.this, "Please add at least 1 user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    DBUtil.addUsersInGroup(AddGroupActivity.nextGroup, CURRENT_USERS_IN_GROUP);
                }


            }
        });
    }
}