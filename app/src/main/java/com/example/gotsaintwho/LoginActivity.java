package com.example.gotsaintwho;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.dialogue.DialogueClient;
import com.example.gotsaintwho.dialogue.DialogueQueue;
import com.example.gotsaintwho.pojo.DialogueMsgDTO;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.JsonUtil;

import static com.example.gotsaintwho.utils.HttpUtil.sendRequestWithHttpURLConnection;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button signup;
    User userAfterLogin;
    String account;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        rememberPass = findViewById(R.id.remember_pass);
        boolean isRemember = pref.getBoolean("remember_password", false);

        if (isRemember) {
            // 将账号和密码都设置到文本框中
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            accountEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        signup = findViewById(R.id.signUp);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login = findViewById(R.id.login);
        //登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = accountEdit.getText().toString();
                password = passwordEdit.getText().toString(); // 如果账号是admin且密码是123456，就认为登录成功
                //这里是登录
                User user = new User(account, password);
                String jsonStr = JsonUtil.user2Json(user);
                //sendRequestWithHttpURLConnection("user/login", jsonStr, new HttpCallbackListener() {
                sendRequestWithHttpURLConnection("user/login", jsonStr, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        userAfterLogin = JsonUtil.json2User(response);
                        //登录成功
                        if (userAfterLogin != null && userAfterLogin.getUserId() != null) {
                            editor = pref.edit();
                            if (rememberPass.isChecked()) {   //检查复选框是否选中
                                editor.putBoolean("remember_password", true);
                                editor.putString("account", account);
                                editor.putString("password", password);
                            } else {
                                editor.clear();
                            }
                            editor.apply();
                            //起一个线程链接服务器，开始循环DialogueQueue,并发送一个ip + id绑定
                            DialogueClient.connect2DialogueServer(userAfterLogin);

                            DialogueQueue.sendDialogue(new DialogueMsgDTO(userAfterLogin.getUserId()));

                            //登录成功后的页面跳转
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user_info", userAfterLogin);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "account or password is invalid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "fail to connect to server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}
