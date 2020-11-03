package com.example.gotsaintwho.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.MomentActivity;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.TrackActivity;
import com.example.gotsaintwho.adapter.MultifunctionAdapter;
import com.example.gotsaintwho.callbackListener.ViewOnClickListener;
import com.example.gotsaintwho.pojo.Multifunction;
import com.example.gotsaintwho.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class MultifunctionFragment extends Fragment {

    private List<Multifunction> multifunctionList = new ArrayList<>();
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multifunction, container, false);

        user = (User) getActivity().getIntent().getSerializableExtra("user_info");
        //初始化多功能列表
        initMultifunction();
        RecyclerView recyclerView = view.findViewById(R.id.multifunction_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //加一条横线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        MultifunctionAdapter adapter = new MultifunctionAdapter(multifunctionList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initMultifunction(){
        //第一个是朋友圈
        multifunctionList.add(new Multifunction("Moment", R.drawable.plane, new ViewOnClickListener() {
            @Override
            public void execute() {
                Intent intent = new Intent(getActivity(), MomentActivity.class);
                intent.putExtra("user_info", user);
                startActivity(intent);
            }
        }));

        // 第二个是足迹
        multifunctionList.add(new Multifunction("Track", R.drawable.location, new ViewOnClickListener() {
            @Override
            public void execute() {
                Intent intent = new Intent(getActivity(), TrackActivity.class);
                intent.putExtra("user_info", user);
                startActivity(intent);
            }
        }));
    }
}
