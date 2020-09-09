package com.example.gotsaintwho;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.adapter.MomentAdapter;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.example.gotsaintwho.pojo.AllUserIdDTO;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.JsonUtil;
import com.example.gotsaintwho.utils.ParamUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.gotsaintwho.utils.HttpUtil.sendRequestWithHttpURLConnection;


public class MomentActivity extends AppCompatActivity {
    private static final String TAG = "MomentActivity";

    private List<Moment> moments = new ArrayList<>();
    RecyclerView momentRecyclerView;
    FloatingActionButton fab;
    MomentAdapter momentAdapter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        user = (User) getIntent().getSerializableExtra("user_info");

        fab = findViewById(R.id.moment_floating_button);
        momentRecyclerView = findViewById(R.id.moment_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MomentActivity.this);
        momentRecyclerView.setLayoutManager(layoutManager);

        momentAdapter = new MomentAdapter(moments);
        momentRecyclerView.setAdapter(momentAdapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //pyq每条的分割线
        momentRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView momentBgView = findViewById(R.id.moment_bg_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("Moment");
        //朋友圈背景
        Glide.with(this).load(ParamUtil.IMAGE_URI+"bg.jpg").into(momentBgView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登录成功后的页面跳转
                Intent intent = new Intent(MomentActivity.this, AddMomentActivity.class);
                intent.putExtra("user_info",user);
                startActivity(intent);
            }
        });

/*        //TODO 这里需要从sqlite找出该用户好友,并包装成JsonStr
        String jsonStr = "{\n" +
                "    \"friendsIdList\":[1,2,3,4,5]\n" +
                "}";*/
        List<String> allUserId = getAllUserId();
        String jsonStr = JsonUtil.userId2JsonStr(new AllUserIdDTO(allUserId));
        Log.e(TAG, "=========================>"+jsonStr );

        sendRequestWithHttpURLConnection("moment", jsonStr, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到moments
                moments.clear();
                moments.addAll(JsonUtil.json2MomentList(response));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(MomentActivity.this, "pyq!!!", Toast.LENGTH_SHORT).show();
                        momentAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private List<String> getAllUserId(){
        List<String> allUserId = DBUtil.findAllUserId();
        boolean isExist = false;
        for(String id: allUserId){
            if(user.getUserId().equals(id)){
                isExist = true;
            }
        }
        if(!isExist){
            //不存在的话把自己放进去，不然自己的pyq就没了
            allUserId.add(user.getUserId());
        }
        return allUserId;
    }

}