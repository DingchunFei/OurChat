package com.example.gotsaintwho;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.gotsaintwho.pojo.Like;
import com.example.gotsaintwho.pojo.LikeDTO;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.MomentIds;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.Reply;
import com.example.gotsaintwho.pojo.ReplyDTO;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.JsonUtil;
import com.example.gotsaintwho.utils.ParamUtil;
import com.example.gotsaintwho.view.LikeListView;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.gotsaintwho.utils.HttpUtil.sendRequestWithHttpURLConnection;


public class MomentActivity extends AppCompatActivity {
    private static final String TAG = "MomentActivity";

    private List<Moment> moments = new ArrayList<>();
    private Map<Integer, List<Like>> likeListMap = new HashMap<>();//所有pyq的点赞列表
    RecyclerView momentRecyclerView;
    FloatingActionButton fab;
    MomentAdapter momentAdapter;
    SwipeRefreshLayout swipeRefresh;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        user = (User) getIntent().getSerializableExtra("user_info");

        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMoment();
            }
        });

        fab = findViewById(R.id.moment_floating_button);
        momentRecyclerView = findViewById(R.id.moment_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MomentActivity.this);
        momentRecyclerView.setLayoutManager(layoutManager);

        System.out.print("========MomentActivity onCreate========= ");
        momentAdapter = new MomentAdapter(moments, user, likeListMap);
        momentRecyclerView.setAdapter(momentAdapter);
        momentAdapter.setOnItemClickListener(new MomentAdapter.OnItemClickListener(){
            @Override
            public void doLike(View view, int position) {
                System.out.println("======doLike========= " + position+ " ");
                addLike(position);
            }

            @Override
            public void doUnlike(View view, int position) {
                System.out.println("======doUnlike========= " + position+ " ");
                deleteLike(position);
            }

        });


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
/*        //朋友圈背景
        Glide.with(this).load(ParamUtil.IMAGE_URI+"bg.jpg").into(momentBgView);*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登录成功后的页面跳转
                Intent intent = new Intent(MomentActivity.this, AddMomentActivity.class);
                intent.putExtra("user_info",user);
                startActivity(intent);
            }
        });
    }

    //每次start刷新界面
    @Override
    protected void onStart() {
        super.onStart();
        refreshMoment();
    }

    private void refreshMoment(){
        //从数据库找出所有的好友id并转化为Json格式
        List<String> allUserId = getAllUserId();
        String jsonStr = JsonUtil.userId2JsonStr(new AllUserIdDTO(allUserId));
        System.out.print("==========MomentActivity=====refreshMoment====== ");
        System.out.println(jsonStr);
        sendRequestWithHttpURLConnection("moment", jsonStr, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到moments
                moments.clear();
                moments.addAll(JsonUtil.json2MomentList(response));
//                System.out.println(moments.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        momentAdapter.notifyDataSetChanged();
                        getAllLikeList();
                        getAllReplyList();
                        swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
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

    private void getAllReplyList(){
        //获得所有pyq的评论
        MomentIds momentIds = new MomentIds();
        List<Integer> momentIdList = new ArrayList<>();
        for(Moment moment: moments){
            momentIdList.add(Integer.valueOf(moment.getMomentId()));
        }
        momentIds.setMomentIds(momentIdList);

        String json = JsonUtil.momentIds2Json(momentIds);

        sendRequestWithHttpURLConnection("reply/findAllReplies", json, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到replies
                System.out.print("===========reply/findAllReplies============");
                ReplyDTO replyDTO = JsonUtil.json2ReplyDTO(response);
                System.out.println(replyDTO.getReplyMap().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        momentAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
            }
        });
    }

    //刷新所有点赞列表
    private void getAllLikeList(){
        MomentIds momentIds = new MomentIds();
        List<Integer> momentIdList = new ArrayList<>();
        for(Moment moment: moments){
            momentIdList.add(Integer.valueOf(moment.getMomentId()));
        }
        momentIds.setMomentIds(momentIdList);

        String json = JsonUtil.momentIds2Json(momentIds);

        sendRequestWithHttpURLConnection("like/findAllLikes", json, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到likes
                System.out.print("===========like/findAllLikes============");
                LikeDTO likeDTO = JsonUtil.json2LikeDTO(response);
                System.out.println(likeDTO.getLikeMap().toString());
                likeListMap.clear();
                likeListMap.putAll(likeDTO.getLikeMap());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        momentAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
            }
        });
    }

    //点赞
    private void addLike(final int position){
        Moment moment = moments.get(position);
        Like like = new Like();
        Integer momentID = Integer.valueOf(moment.getMomentId());
        //Like对象赋值
        like.setMomentId(momentID);
        like.setUserId(Integer.valueOf(user.getUserId()));
        like.setUserName(user.getUsername());

        System.out.print("===========like/addLike============");
        String json = JsonUtil.like2Json(like);

        System.out.println(json);

        //网络请求
        sendRequestWithHttpURLConnection("like/addLike", json, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到like
                Like like = JsonUtil.json2Like(response);
                //更新adapter中的map
                List<Like> likeList = likeListMap.get(like.getMomentId());
                likeList.add(like);
                likeListMap.put(like.getMomentId(), likeList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        momentAdapter.notifyItemChanged(position);
                        swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
            }
        });
    }

    //取消点赞
    private void deleteLike(final int position){
        Moment moment = moments.get(position);
        Integer momentID = Integer.valueOf(moment.getMomentId());
        //Like对象赋值
        List<Like> likeList = likeListMap.get(momentID);
        Like like = null;
        for(Like cur: likeList){
            if(cur.getUserId() == Integer.valueOf(user.getUserId())){
                like = cur;
            }
        }

        if(like == null)
            return;

        System.out.print("===========like/deleteLike============");
        String json = JsonUtil.like2Json(like);

        System.out.println(json);

        //网络请求
        sendRequestWithHttpURLConnection("like/deleteLike", json, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                //收到like
                Like like = JsonUtil.json2Like(response);
                //更新adapter中的map
                List<Like> likeList = likeListMap.get(like.getMomentId());
                for(Like cur: likeList){
                    if(cur.getUserId() == like.getUserId()){
                        likeList.remove(cur);
                        break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        momentAdapter.notifyItemChanged(position);
                        swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                swipeRefresh.setRefreshing(false);  //表示刷新事件结束，并隐藏刷新进度条
            }
        });
    }
}