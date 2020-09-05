package com.example.gotsaintwho.callbackListener;

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
