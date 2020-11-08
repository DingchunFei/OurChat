package com.example.gotsaintwho.adapter;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.AddGroupActivity;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;

import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public static final String TAG = "GroupAdapter";

    private List<User> mUserList;

    public GroupAdapter(List<User> userList) {
        mUserList = userList;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserAdapter.ViewHolder holder, int position) {
        final User user = mUserList.get(position);

        //更具URI读取对应用户图片
        String Uri = ParamUtil.IMAGE_URI + user.getUserId()+".jpg";
        Glide.with(MyApplication.getContext()).load(Uri).into(holder.item_user_image);

        holder.item_username.setText(user.getUsername());

        // find every user already in the group, and exclude them
        List<User> usersInGroup = AddGroupActivity.CURRENT_USERS_IN_GROUP;

        for (User userInGroup:usersInGroup){
            if(userInGroup.getUserId().equals(user.getUserId())){
                // user already in group
                holder.image_to_be_added.setVisibility(View.GONE);
                holder.image_already_added.setVisibility(View.VISIBLE);
                //为按钮添加点击事件
                holder.image_already_added.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //提示该用户已被添加
                        Toast.makeText(MyApplication.getContext(),"This user has already been added in the group",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }
        // user not in group yet
        holder.image_to_be_added.setVisibility(View.VISIBLE);
        holder.image_already_added.setVisibility(View.GONE);
        holder.image_to_be_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add user in group
                // DBUtil.saveUser(user);
                AddGroupActivity.CURRENT_USERS_IN_GROUP.add(user);
                Toast.makeText(MyApplication.getContext(),"Added successfully in group",Toast.LENGTH_SHORT).show();
                holder.image_to_be_added.setVisibility(View.GONE);
                holder.image_already_added.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
