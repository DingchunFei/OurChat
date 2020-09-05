package com.example.gotsaintwho.pojo;

import java.io.Serializable;

public class DialogueMsgDTO implements Serializable {

    private String userId;
    private String targetUserId;
    private String message;

    public DialogueMsgDTO(String userId) {
        this.userId = userId;
    }

    public DialogueMsgDTO(String userId, String targetUserId, String message) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.message = message;
    }

    public DialogueMsgDTO() {
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
