package com.example.gotsaintwho;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gotsaintwho.adapter.ChatItemAdapter;
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
    ListView userListView;
    Button findUserButton;
    List<User> userList = null;
    List<User> userListFromDb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        usernameEditText = findViewById(R.id.usernameEditText);
        userListView = findViewById(R.id.userListView);
        findUserButton = findViewById(R.id.findUserButton);

        //先从数据库里查出所有用户
        userListFromDb = DBUtil.findAllUser();

     /*   UserAdapter userAdapter = new UserAdapter(FindUserActivity.this, R.layout.item_user_list, userList);
        userListView.setAdapter(userAdapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = userList.get(i);
                //TODO 还要防止这个用户是已添加好友和寄几
                //点击后把这个用户存起来，表示好友添加成功
                DBUtil.saveUser(user);
            }
        });


        findUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                if(username ==null || username.equals("")){
                    Toast.makeText(FindUserActivity.this, "Please enter the username", Toast.LENGTH_SHORT).show();
                    return;
                }
                //封装一个要查找的user对象
                User user = new User();
                user.setUsername(username);

                //向服务器寻找
                HttpUtil.sendRequestWithHttpURLConnection("user/findUserByUsername", JsonUtil.user2Json(user), new HttpCallbackListener() {

                    @Override
                    public void onFinish(String response) {
                        userList = JsonUtil.json2UserList(response);
                        Log.e(TAG, "userList==================> "+ userList );
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(FindUserActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    */
    }
}