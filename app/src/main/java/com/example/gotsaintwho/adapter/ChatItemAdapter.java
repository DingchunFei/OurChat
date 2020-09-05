package com.example.gotsaintwho.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.ChatItem;

import java.util.List;

public class ChatItemAdapter extends ArrayAdapter<ChatItem> {

    private int resourceId;

    public ChatItemAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<ChatItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatItem chatItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView targetUserImage = view.findViewById(R.id.item_target_user_image);
        TextView targetUsername = view.findViewById(R.id.item_target_username);
        TextView lastChat = view.findViewById(R.id.item_last_chat);
        targetUserImage.setImageResource(chatItem.getImageId());
        //用Glide变成一个圆形图片
        Glide.with(getContext()).load(chatItem.getImageId()).into(targetUserImage);
        targetUsername.setText(chatItem.getTargetUserName());
        lastChat.setText(chatItem.getLastChat());
        return view;
    }
}
