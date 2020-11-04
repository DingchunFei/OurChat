package com.example.gotsaintwho.pojo;

import java.io.Serializable;

public class GroupDialogueMsgDTO implements Serializable {

    private String userId;
    private String targetGroupId;
    private String message;

    public GroupDialogueMsgDTO(String userId, String targetGroupId, String message) {
        this.userId = userId;
        this.targetGroupId = targetGroupId;
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetGroupId() {
        return targetGroupId;
    }

    public void setTargetGroupId(String targetGroupId) {
        this.targetGroupId = targetGroupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
