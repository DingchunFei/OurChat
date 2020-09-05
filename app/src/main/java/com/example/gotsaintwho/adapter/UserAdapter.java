package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.pojo.User;

import java.util.List;

//TODO 等网络图像功能实现好了来处理
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<User> mUserList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item_user_image;
        LinearLayout item_username;
        TextView image_already_added;
        TextView item_user_button;

        public ViewHolder(View view) {
            super(view);
            item_user_image = view.findViewById(R.id.item_user_image);
            item_username = view.findViewById(R.id.item_username);
            image_already_added = view.findViewById(R.id.image_already_added);
            item_user_button = view.findViewById(R.id.item_user_button);
        }
    }

    public UserAdapter(List<User> userList) {
        mUserList = userList;
    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder holder, int position) {
        User user = mUserList.get(position);
/*        if(user)

        if (msg.getType() == Msg.TYPE_RECEIVED) { // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SENT) { // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }*/
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }
}
