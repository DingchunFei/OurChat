package com.example.gotsaintwho.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

public class MeFragment extends Fragment {

    Button button1,button2,button3;
    private static final String TAG = "MeFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        button1 = view.findViewById(R.id.create_db);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SQLiteDatabase db = DBUtil.getDb(getActivity());
                User user1 = new User("1","admin",null,"male");
                DBUtil.saveUser(user1);
                User user2 = new User("2","cena",null,"female");
                DBUtil.saveUser(user2);
                User user3 = new User("3","fifi",null,"male");
                DBUtil.saveUser(user3);
            }
        });
        button2 = view.findViewById(R.id.query_data);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SQLiteDatabase db = DBUtil.getDb(getActivity());
                List<User> userList = DBUtil.findAllUser();
                for (User user: userList){
                    Log.e(TAG, "=====================>" + user.toString());
                }
            }
        });

        button3 = view.findViewById(R.id.delete_data);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SQLiteDatabase db = DBUtil.getDb(getActivity());
                DBUtil.deleteAllUser();
                Log.e(TAG, "data deleted =====================>");
            }
        });
        return view;
    }
}
