package com.example.gotsaintwho;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import static com.example.gotsaintwho.utils.ParamUtil.IMAGE_URI;

public class MomentActivity extends AppCompatActivity {

    RecyclerView momentRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);

        momentRecyclerView = findViewById(R.id.moment_recycler_view);
        //从Intent中取出Fruit name和image
        Intent intent = getIntent();
        Toolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView momentBgView = findViewById(R.id.moment_bg_view);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("Moment");
        Glide.with(this).load(IMAGE_URI+"bg.jpg").into(momentBgView);

    }
}