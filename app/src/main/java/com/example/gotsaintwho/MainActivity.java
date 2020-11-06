package com.example.gotsaintwho;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.gotsaintwho.fragment.ChatListFragment;
import com.example.gotsaintwho.fragment.MeFragment;
import com.example.gotsaintwho.fragment.MultifunctionFragment;
import com.example.gotsaintwho.pojo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    List<Fragment> fragments;
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        fragments = new ArrayList<>();
        fragments.add(new ChatListFragment());
        fragments.add(new MultifunctionFragment());
        fragments.add(new MeFragment());
        BtmNavViewAdapter btmNavViewAdapter = new BtmNavViewAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(btmNavViewAdapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item1:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item2:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item3:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        //这个是监听pager滑动时候，下面的buttom对应图标也要跟着切换
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if(menuItem == null){
                    //初始化
                    menuItem = bottomNavigationView.getMenu().getItem(0);
                }
                //将上次的选择设为false，等待下次的选择
                menuItem.setChecked(false);
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }
        });
    }

    //创建右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //从Intent中拿到user用来传给下一个Activity
        User user = (User) getIntent().getSerializableExtra("user_info");
        Intent intent;

        switch (item.getItemId()) {
            case R.id.add_friend_item:
                intent = new Intent(MainActivity.this, FindUserActivity.class);
                intent.putExtra("user_info",user);
                startActivity(intent);
                break;

            case R.id.add_group_item:
                intent = new Intent(MainActivity.this, AddGroupActivity.class);
                intent.putExtra("user_info",user);
                startActivity(intent);
                break;
            default:
        }
        return true;
    }

    private class BtmNavViewAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public BtmNavViewAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}