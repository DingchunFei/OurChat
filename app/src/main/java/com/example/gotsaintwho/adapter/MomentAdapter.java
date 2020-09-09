package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.Msg;
import com.example.gotsaintwho.utils.ParamUtil;

import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {
    private List<Moment> mMomentList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moment_avatar;
        ImageView moment_image;
        TextView moment_username;
        TextView moment_content;
        TextView moment_location;

        public ViewHolder(View view) {
            super(view);
            moment_avatar = view.findViewById(R.id.moment_avatar);
            moment_image = view.findViewById(R.id.moment_image);
            moment_username = view.findViewById(R.id.moment_username);
            moment_content = view.findViewById(R.id.moment_content);
            moment_location = view.findViewById(R.id.moment_location);
        }
    }

    public MomentAdapter(List<Moment> msgList) {
        mMomentList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.moment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
    }

    @Override
    public int getItemCount() {
        return mMomentList.size();
    }
}