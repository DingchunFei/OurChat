package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Reply;
import com.example.gotsaintwho.pojo.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private List<Reply> replyList;
    private User me;

    public ReplyAdapter(List<Reply> replyList, User me){
        this.replyList = replyList;
        this.me = me;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView userNameView;
        TextView contentView;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameView = itemView.findViewById(R.id.reply_item_user_name);
            contentView = itemView.findViewById(R.id.reply_item_content);
        }
    }

    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ReplyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplyAdapter.ViewHolder holder, int position) {
        Reply reply = replyList.get(position);
        holder.userNameView.setText(reply.getCommenterName());
        holder.contentView.setText(reply.getReplyContent());
    }

    @Override
    public int getItemCount() {
        if(replyList == null)
            return 0;
        return replyList.size();
    }
}
