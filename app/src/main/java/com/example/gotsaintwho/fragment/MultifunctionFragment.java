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

import com.example.gotsaintwho.DialogueActivity;
import com.example.gotsaintwho.MomentActivity;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.User;

public class MultifunctionFragment extends Fragment {

    private User user = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_multifunction, container, false);

        Button button = view.findViewById(R.id.moment_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = (User) getActivity().getIntent().getSerializableExtra("user_info");
                Intent intent = new Intent(getActivity(), MomentActivity.class);
                intent.putExtra("user_info", user);
                startActivity(intent);
            }
        });
        return view;
    }
}
