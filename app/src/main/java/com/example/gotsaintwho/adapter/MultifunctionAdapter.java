package com.example.gotsaintwho.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.Multifunction;

import java.util.List;

public class MultifunctionAdapter extends RecyclerView.Adapter<MultifunctionAdapter.ViewHolder> {
    private List<Multifunction> mMultifunctionList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View multifunctionView;
        ImageView multifunctionIcon;
        TextView multifunctionName;

        public ViewHolder(View view) {
            super(view);
            multifunctionView = view;
            multifunctionIcon = view.findViewById(R.id.multifunction_icon);
            multifunctionName = view.findViewById(R.id.multifunction_name);
        }
    }

    public MultifunctionAdapter(List<Multifunction> MultifunctionList) {
        mMultifunctionList = MultifunctionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multifunction, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.multifunctionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Multifunction multifunction = mMultifunctionList.get(position);
                //执行对应的封装好的点击事件
                multifunction.getViewOnClickListener().execute();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Multifunction multifunction = mMultifunctionList.get(position);
        holder.multifunctionIcon.setImageResource(multifunction.getIcon());
        holder.multifunctionName.setText(multifunction.getName());
    }

    @Override
    public int getItemCount() {
        return mMultifunctionList.size();
    }

}
