/*
package com.example.gotsaintwho.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public class ContactListFragment extends Fragment {

    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    private static final String TAG = "ContactListFragment";
    Button button1,button2,button3,button4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

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

        button4 = view.findViewById(R.id.upload_image);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //向服务器发送图片
                    Log.e(TAG, getActivity().getExternalCacheDir()+"/output_image.jpg");
                    //TODO 这里的用户id是硬编码
                    HttpUtil.uploadFile("1",getActivity().getExternalCacheDir()+"/output_image.jpg", ParamUtil.URI+"user/uploadImg");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.e(TAG, "image uploaded =====================>" + getActivity().getExternalCacheDir());
            }
        });

        Button takePhoto = view.findViewById(R.id.take_photo);
        picture = view.findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 创建File对象，用于存储拍照后的图片
            File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
            Log.e(TAG, "path: "+outputImage.getPath());
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            } if (Build.VERSION.SDK_INT >= 24) {
                imageUri = FileProvider.getUriForFile(getActivity(), "com.example.gotsaintwho.fileprovider", outputImage);
            } else {
                imageUri = Uri.fromFile(outputImage);
            }
            // 启动相机程序
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, TAKE_PHOTO);
        }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default:
                    break;
        }
    }
}*/
