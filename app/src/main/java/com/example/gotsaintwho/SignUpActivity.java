package com.example.gotsaintwho;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.JsonUtil;

public class SignUpActivity extends AppCompatActivity {

    private Button back;
    private Button signup;

    private EditText accountEdit;
    private EditText passwordEdit;

    private Switch aSwitch;
    private String gender = "male";
    private User userAfterSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        back = findViewById(R.id.back);
        signup = findViewById(R.id.signUp);
        aSwitch = findViewById(R.id.gender_switch);
        accountEdit = findViewById(R.id.account);
        passwordEdit = findViewById(R.id.password);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {   //表示开启，Female
                    gender = "female";
                }else{
                    gender = "male";
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                //这里是登录
                User user = new User(account, password,gender);
                String jsonStr = JsonUtil.user2Json(user);
                HttpUtil.sendRequestWithHttpURLConnection("user", jsonStr, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        userAfterSignup = JsonUtil.json2User(response);
                        if(userAfterSignup.getUserId() != null){
                            Toast.makeText(SignUpActivity.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "This username has been used", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(SignUpActivity.this, "fail to connect to server", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
