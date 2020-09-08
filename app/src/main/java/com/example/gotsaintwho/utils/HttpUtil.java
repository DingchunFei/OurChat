package com.example.gotsaintwho.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.gotsaintwho.MyApplication;
import com.example.gotsaintwho.callbackListener.HttpCallbackListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

import static android.net.sip.SipErrorCode.TIME_OUT;
import static android.provider.Telephony.Mms.Part.CHARSET;

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    public static void sendRequestWithHttpURLConnection(final String uriStr, final String jsonStr, final HttpCallbackListener listener) { // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    String urlStr = ParamUtil.URI + uriStr;
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    // 设置contentType
                    connection.setRequestProperty("Content-Type", "application/json");
                    DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                    os.writeBytes(jsonStr);
                    os.flush();
                    os.close();
                    //连接
                    connection.connect();
                    //得到响应码
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 下面对获取到的输入流进行读取
                        InputStream in = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        if (listener != null) {
                            listener.onFinish(response.toString());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) { // 回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void uploadFile(String userId, String path, String url) throws Exception {
        File file = new File(path);
        if (file.exists() && file.length() > 0) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("user_id",userId);
            params.put("img", file);
            // 上传文件
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {
                    // 上传成功后要做的工作
                    Toast.makeText(MyApplication.getContext(), "change has been made", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    // 上传失败后要做到工作
                    Toast.makeText(MyApplication.getContext(), "change image error", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(MyApplication.getContext(), "image has not been taken", Toast.LENGTH_LONG).show();
        }

    }

}
