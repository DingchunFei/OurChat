package com.example.gotsaintwho.adapter;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Like;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.Reply;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.ParamUtil;
import com.example.gotsaintwho.view.LikeListView;

import java.util.*;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    private List<Moment> mMomentList;
    private Map<Integer, List<Like>> likeListMap;
    private Map<Integer, Boolean> hadLiked = new HashMap<>();//key: position, value: 是否点赞
    private Map<Integer, List<Reply>> replyListMap;
    private User me;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moment_avatar;
        ImageView moment_image;
        TextView moment_username;
        TextView moment_content;
        TextView moment_location;
        ImageView btn_add_comment;//添加评论按钮
        ImageView btn_like_comment;//点赞按钮
        LikeListView likeListView;//点赞列表

        public ViewHolder(View view) {
            super(view);
            moment_avatar = view.findViewById(R.id.moment_avatar);
            moment_image = view.findViewById(R.id.moment_image);
            moment_username = view.findViewById(R.id.moment_username);
            moment_content = view.findViewById(R.id.moment_content);
            moment_location = view.findViewById(R.id.moment_location);
            btn_add_comment = view.findViewById(R.id.btn_add_moment_comment);//添加评论按钮
            btn_like_comment = view.findViewById(R.id.btn_like_comment);//点赞按钮
            likeListView = view.findViewById(R.id.like_list_view);//点赞列表
        }
    }

    public MomentAdapter(List<Moment> msgList, User me, Map<Integer, List<Like>> likeListMap) {
        mMomentList = msgList;
        this.me = me;
        this.likeListMap = likeListMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Moment moment = mMomentList.get(position);

        holder.moment_username.setText(moment.getUsername());
        holder.moment_content.setText(moment.getMomentContent());

        String avatarUri = ParamUtil.IMAGE_URI + moment.getUserId()+".jpg";
        Glide.with(MyApplication.getContext()).load(avatarUri).into(holder.moment_avatar);

        String imgUri = ParamUtil.IMAGE_URI + moment.getMomentImage();
        Glide.with(MyApplication.getContext()).load(imgUri).into(holder.moment_image);

        if(moment.getLocation()!=null){
            //有位置信息
            holder.moment_location.setVisibility(View.VISIBLE);
            holder.moment_location.setText(moment.getLocation());
        }

        System.out.print("======onBindViewHolder====== ");

        List<Like> likeList = likeListMap.get(Integer.valueOf(moment.getMomentId()));

        if (likeList != null){
            List<Like> cur = holder.likeListView.getList();
            System.out.println(position+" Cur like: "+cur.toString()+ " : " + likeList.toString());
            cur.clear();
            holder.likeListView.setText("");
            cur.addAll(likeList);
            holder.likeListView.setList(cur);
            System.out.println(position+" likeListView: "+holder.likeListView.getList().toString());
        }

        //用户是否已点赞
        hadLiked.put(position, false);
        if(likeList != null){
            for(Like like: likeList){
                if(like.getUserId() == Integer.valueOf(me.getUserId())){
                    hadLiked.put(position, true);
                    break;
                }
            }
        }
        //根据是否点赞改变按钮颜色
        if(hadLiked.get(position)){
            holder.btn_like_comment.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        else {
            holder.btn_like_comment.setColorFilter(Color.parseColor("#aaaaaa"));
        }

        //对item控件进行点击事件的监听并回调给自定义的监听
        if (mOnItemClickListener != null) {
            holder.btn_like_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hadLiked.get(position)){//已点赞,则取消点赞
                        hadLiked.put(position, false);
                        holder.btn_like_comment.setColorFilter(Color.parseColor("#aaaaaa"));
                        mOnItemClickListener.doUnlike(view, position);
                    }
                    else {
                        hadLiked.put(position, true);
                        holder.btn_like_comment.setColorFilter(Color.parseColor("#FF5C5C"));
                        mOnItemClickListener.doLike(view, position);
                    }
                }
            });
        }

//        点击添加评论按钮的监听事件
        holder.btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("=============add comment======== ");
                System.out.println(holder.likeListView.getText());
            }
        });

        //点赞按钮的监听事件
//        holder.btn_like_comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.print("=============like the moment ");
//
//                System.out.println(" From: "+me.getUsername());
//
//                List<User> list = holder.likeListView.getList();
//                if(list == null){
//                    list = new ArrayList<>();
//                }
//                list.add(me);
//                holder.likeListView.setList(list);
//                holder.likeListView.notifyDataSetChanged();
//                System.out.println(" List: "+list);
//            }
//        });
    }

    //item内的点击事件
    private OnItemClickListener mOnItemClickListener;
    //在里面实现具体的点击响应事件，同时传入两个参数：view和postion
    public interface OnItemClickListener{
        void doLike(View view, int position);
        void doUnlike(View view, int position);

//        void onItemClick(View view, LikeListView likeListView, int position);

//        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mMomentList.size();
    }
}