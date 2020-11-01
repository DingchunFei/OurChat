package com.example.gotsaintwho.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Like;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.Reply;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.ParamUtil;
import com.example.gotsaintwho.view.LikeListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MomentExpandableAdapter extends BaseExpandableListAdapter {
    private List<Moment> mMomentList;
    private Map<Integer, List<Like>> likeListMap;
    private Map<Integer, Boolean> hadLiked = new HashMap<>();//key: position, value: 是否点赞
    private Map<Integer, List<Reply>> replyListMap;
    private User me;
    private Context context;

    public MomentExpandableAdapter(Context context,
                                   List<Moment> mMomentList,
                                   Map<Integer, List<Like>> likeListMap,
                                   Map<Integer, List<Reply>> replyListMap){
        this.context = context;
        this.mMomentList = mMomentList;
        this.likeListMap = likeListMap;
        this.replyListMap = replyListMap;
    }

    @Override
    public int getGroupCount() {
        return mMomentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Moment moment = mMomentList.get(groupPosition);
        //moment为空或reply为空
        if(moment == null || replyListMap.get(moment.getMomentId()) == null)
            return 0;
        else
            return replyListMap.get(moment.getMomentId()).size();
    }

    @Override
    public Moment getGroup(int groupPosition) {
        return mMomentList.get(groupPosition);
    }

    @Override
    public List<Reply> getChild(int groupPosition, int childPosition) {
        Moment moment = mMomentList.get(groupPosition);
        if(moment == null)
            return null;
        return replyListMap.get(moment.getMomentId());
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder groupHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.moment_item, parent, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        }else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        Moment moment = mMomentList.get(groupPosition);

        groupHolder.moment_username.setText(moment.getUsername());
        groupHolder.moment_content.setText(moment.getMomentContent());

        String avatarUri = ParamUtil.IMAGE_URI + moment.getUserId()+".jpg";
        Glide.with(MyApplication.getContext()).load(avatarUri).into(groupHolder.moment_avatar);

        String imgUri = ParamUtil.IMAGE_URI + moment.getMomentImage();
        Glide.with(MyApplication.getContext()).load(imgUri).into(groupHolder.moment_image);

        if(moment.getLocation()!=null){
            //有位置信息
            groupHolder.moment_location.setVisibility(View.VISIBLE);
            groupHolder.moment_location.setText(moment.getLocation());
        }

        System.out.print("======onBindViewHolder====== ");

        List<Like> likeList = likeListMap.get(Integer.valueOf(moment.getMomentId()));

        if (likeList != null){
            List<Like> cur = groupHolder.likeListView.getList();
            System.out.println(groupPosition+" Cur like: "+cur.toString()+ " : " + likeList.toString());
            cur.clear();
            groupHolder.likeListView.setText("");
            cur.addAll(likeList);
            groupHolder.likeListView.setList(cur);
            System.out.println(groupPosition+" likeListView: "+groupHolder.likeListView.getList().toString());
        }

        //用户是否已点赞
        hadLiked.put(groupPosition, false);
        if(likeList != null){
            for(Like like: likeList){
                if(like.getUserId() == Integer.valueOf(me.getUserId())){
                    hadLiked.put(groupPosition, true);
                    break;
                }
            }
        }
        //根据是否点赞改变按钮颜色
        if(hadLiked.get(groupPosition)){
            groupHolder.btn_like_comment.setColorFilter(Color.parseColor("#FF5C5C"));
        }
        else {
            groupHolder.btn_like_comment.setColorFilter(Color.parseColor("#aaaaaa"));
        }

        //对item控件进行点击事件的监听并回调给自定义的监听
        if (mOnItemClickListener != null) {
            groupHolder.btn_like_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(hadLiked.get(groupPosition)){//已点赞,则取消点赞
                        hadLiked.put(groupPosition, false);
                        groupHolder.btn_like_comment.setColorFilter(Color.parseColor("#aaaaaa"));
                        mOnItemClickListener.doUnlike(view, groupPosition);
                    }
                    else {
                        hadLiked.put(groupPosition, true);
                        groupHolder.btn_like_comment.setColorFilter(Color.parseColor("#FF5C5C"));
                        mOnItemClickListener.doLike(view, groupPosition);
                    }
                }
            });
        }

//        点击添加评论按钮的监听事件
        groupHolder.btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("=============add comment======== ");
                System.out.println(groupHolder.likeListView.getText());
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder childHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.reply_item,parent, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        }
        else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        Moment moment = mMomentList.get(groupPosition);
        List<Reply> replyList = replyListMap.get(moment.getMomentId());
        Reply reply = replyList.get(childPosition);
        if(replyList == null || replyList.size() <= 0){
            childHolder.replyUserName.setText("");
            childHolder.replyContent.setText("");
        }
        else {
            childHolder.replyUserName.setText(reply.getCommenterName()+": ");
            childHolder.replyContent.setText(reply.getReplyContent());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupHolder{
        ImageView moment_avatar;
        ImageView moment_image;
        TextView moment_username;
        TextView moment_content;
        TextView moment_location;
        ImageView btn_add_comment;//添加评论按钮
        ImageView btn_like_comment;//点赞按钮
        LikeListView likeListView;//点赞列表
        public GroupHolder(View view) {
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

    private class ChildHolder{
        private TextView replyUserName;
        private TextView replyContent;
        public ChildHolder(View view){
            replyUserName = view.findViewById(R.id.reply_item_user_name);
            replyContent = view.findViewById(R.id.reply_item_content);
        }
    }

    //item内的点击事件
    private MomentAdapter.OnItemClickListener mOnItemClickListener;
    //在里面实现具体的点击响应事件，同时传入两个参数：view和postion
    public interface OnItemClickListener{
        void doLike(View view, int position);
        void doUnlike(View view, int position);

//        void onItemClick(View view, LikeListView likeListView, int position);

//        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(MomentAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
