package com.example.gotsaintwho.pojo;

import java.io.Serializable;

public class GroupDialogueMsgDTO implements Serializable {

    private String userId;
    private String targetUserId;
    private String message;

    public GroupDialogueMsgDTO(String userId, String targetUserId, String groupId, String groupName, String groupMemberIds, String message) {
        this.userId = userId;
        this.targetUserId = targetUserId;

        this.message = "group:" + groupId + "," + groupName + "/" + "members:" +  groupMemberIds + "/" + message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String gettargetUserId() {
        return targetUserId;
    }

    public void settargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
