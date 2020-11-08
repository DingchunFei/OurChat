package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;

import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> mUserList;
    public static final String TAG = "UserAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView item_user_image;
        TextView item_username;
        ImageView image_already_added;
        ImageView image_to_be_added;

        public ViewHolder(View view) {
            super(view);
            item_user_image = view.findViewById(R.id.item_user_image);
            item_username = view.findViewById(R.id.item_username);
            image_already_added = view.findViewById(R.id.image_already_added);
            image_to_be_added = view.findViewById(R.id.image_to_be_added);
        }
    }

    public UserAdapter(List<User> userList) {
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

        //从数据库中找到所有用户,便于之后标识出已添加的用户
        List<User> userListFromDb = DBUtil.findAllUser();
        for (User userInDB:userListFromDb){
            if(userInDB.getUserId().equals(user.getUserId())){
                //表示这个用户已经是好友
                holder.image_to_be_added.setVisibility(View.GONE);
                holder.image_already_added.setVisibility(View.VISIBLE);
                //为按钮添加点击事件
                holder.image_already_added.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    //提示该用户已被添加
                    Toast.makeText(MyApplication.getContext(),"This user has already been added",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        }
        //表示这个用户还不是好友
        holder.image_to_be_added.setVisibility(View.VISIBLE);
        holder.image_already_added.setVisibility(View.GONE);
        holder.image_to_be_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //用户加为好友
                DBUtil.saveUser(user);
                Toast.makeText(MyApplication.getContext(),"Adding successfully",Toast.LENGTH_SHORT).show();
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
