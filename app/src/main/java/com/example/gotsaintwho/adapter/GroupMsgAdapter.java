package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Msg;

import java.util.List;

public class GroupMsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;

    public GroupMsgAdapter(List<Msg> msgList) {
        mMsgList = msgList;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item, parent, false);
        return new MsgAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MsgAdapter.ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED) { // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
        } else if (msg.getType() == Msg.TYPE_SENT) { // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}