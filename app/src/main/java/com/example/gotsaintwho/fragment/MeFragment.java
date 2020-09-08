package com.example.gotsaintwho.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.gotsaintwho.R;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.DBUtil;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeFragment extends Fragment {

    private static final String TAG = "MeFragment";

    public static final int NO_SELECTION = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    public String imagePath;

    private CircleImageView imageView;
    private TextView usernameTextView;
    private User user;
    private Uri imageUri;
    private Button submitBtn;
    private static Integer isChanged = NO_SELECTION;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        user = (User) getActivity().getIntent().getSerializableExtra("user_info");

        imageView = view.findViewById(R.id.me_avatar);
        usernameTextView = view.findViewById(R.id.me_username);

        //从网络读取图片作为头像
        String uri = ParamUtil.IMAGE_URI + user.getUserId() + ".jpg";

        Glide.with(getContext()).load(uri).into(imageView);

        usernameTextView.setText(user.getUsername());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(imageView);
            }
        });

        submitBtn = view.findViewById(R.id.submit_change);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChanged != NO_SELECTION){
                    //表示图像被修改过了，需要提交
                    try {
                        if(isChanged == TAKE_PHOTO){
                            //将图片上传到服务器
                            HttpUtil.uploadFile(user.getUserId(),getActivity().getExternalCacheDir()+"/avatar.jpg", ParamUtil.URI+"user/uploadImg");
                        }else if(isChanged == CHOOSE_PHOTO){
                            HttpUtil.uploadFile(user.getUserId(),imagePath, ParamUtil.URI+"user/uploadImg");

                        }
                        isChanged = NO_SELECTION;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getActivity(),"Nothing has been changed",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.avatar_popup_menu, popupMenu.getMenu());
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getTitle().toString().equals("Open Album")){
                    //点击了打开相册
                    checkOpenAlbum();
                }else if(item.getTitle().toString().equals("Use Camera")){
                    //点击了打开相机
                    openCamera();
                }
                Log.e(TAG, item.getTitle().toString() );
                return true;
            }
        });
/*        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });*/
    }


    private void openCamera(){
        File outputImage = new File(getActivity().getExternalCacheDir(), "avatar.jpg");
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

    private void checkOpenAlbum(){
        //打开相册需要申请危险权限
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager. PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
        } else {
            openAlbum();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //判断用户是否授权打开相册
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager. PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                } break;
            default:
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) { // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        //将ImagePath保存起来以便上传到服务器
        this.imagePath = imagePath;
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null; // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore. Images.Media.DATA));
            }
            cursor.close();
        } return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //相机拍完后返回照片的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == getActivity().RESULT_OK) {
                    try {
                        //表示图像被改变过了，需要予以提交
                        isChanged = TAKE_PHOTO;
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == getActivity().RESULT_OK) { // 判断手机系统版本号
                    //表示图像被改变过了，需要予以提交
                    isChanged = CHOOSE_PHOTO;
                    if (Build.VERSION.SDK_INT >= 19) { // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else { // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }
}
