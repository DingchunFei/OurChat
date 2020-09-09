package com.example.gotsaintwho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.gotsaintwho.pojo.Moment;
import com.example.gotsaintwho.pojo.User;
import com.example.gotsaintwho.utils.HttpUtil;
import com.example.gotsaintwho.utils.ParamUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class AddMomentActivity extends AppCompatActivity {
    private static final String TAG = "AddMomentActivity";

    private EditText momentEditText;
    private ImageView momentImage;
    private ImageView momentButton;
    private Button momentSubmit;

    public static final int NO_SELECTION = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private static Integer isChanged = NO_SELECTION;
    public String imagePath;
    private Uri imageUri;
    String imageId;

    private User user;
    String momentEditTextStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_moment);
        //TODO 获取从user中传来的user信息
        user = (User) getIntent().getSerializableExtra("user_info");
        momentEditText = findViewById(R.id.moment_edit_text);
        momentImage = findViewById(R.id.moment_image);
        momentButton = findViewById(R.id.moment_add_img_button);
        momentSubmit = findViewById(R.id.moment_submit_button);

        momentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //表示是添加朋友圈类型
                showPopupMenu(view);
            }
        });
        momentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(momentEditText.getText().toString()==null || momentEditText.getText().toString().equals("")){
                    Toast.makeText(AddMomentActivity.this, "Fill something before submit", Toast.LENGTH_SHORT).show();
                }else{
                    if(isChanged != NO_SELECTION){
                        //说明editText填满了，准备发送http请求
                        Moment moment = new Moment();
                        moment.setUserId(user.getUserId());
                        moment.setUsername(user.getUsername());
                        moment.setMomentContent(momentEditText.getText().toString());

                        try {
                            if(isChanged == TAKE_PHOTO){
                                //将图片上传到服务器
                                HttpUtil.uploadMoment(moment,AddMomentActivity.this.getExternalCacheDir()+"/moment.jpg", ParamUtil.URI+"moment/uploadImg");
                            }else if(isChanged == CHOOSE_PHOTO){
                                HttpUtil.uploadMoment(moment,imagePath, ParamUtil.URI+"moment/uploadImg");
                            }
                            isChanged = NO_SELECTION;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(AddMomentActivity.this,"Nothing has been changed",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(AddMomentActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.avatar_popup_menu, popupMenu.getMenu());
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //为照片设置一个ID
                imageId = UUID.randomUUID().toString();
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
        File outputImage = new File(AddMomentActivity.this.getExternalCacheDir(), "moment.jpg");
        Log.e(TAG, "path: "+outputImage.getPath());
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        } if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(AddMomentActivity.this, "com.example.gotsaintwho.fileprovider", outputImage);
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
        if (ContextCompat.checkSelfPermission(AddMomentActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager. PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddMomentActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
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
                    Toast.makeText(AddMomentActivity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
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
        if (DocumentsContract.isDocumentUri(AddMomentActivity.this, uri)) { // 如果是document类型的Uri，则通过document id处理
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
        Cursor cursor = AddMomentActivity.this.getContentResolver().query(uri, null, selection, null, null);
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
            momentImage.setImageBitmap(bitmap);
        } else {
            Toast.makeText(AddMomentActivity.this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //相机拍完后返回照片的回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == AddMomentActivity.this.RESULT_OK) {
                    try {
                        //表示图像被改变过了，需要予以提交
                        isChanged = TAKE_PHOTO;

                        //让图片区域变得Visible
                        momentImage.setVisibility(View.VISIBLE);
                        momentButton.setVisibility(View.GONE);

                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(AddMomentActivity.this.getContentResolver().openInputStream(imageUri));
                        momentImage.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == AddMomentActivity.this.RESULT_OK) { // 判断手机系统版本号
                    //表示图像被改变过了，需要予以提交
                    isChanged = CHOOSE_PHOTO;

                    //让图片区域变得Visible
                    momentImage.setVisibility(View.VISIBLE);
                    momentButton.setVisibility(View.GONE);

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