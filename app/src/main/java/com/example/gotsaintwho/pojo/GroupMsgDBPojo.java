package com.example.gotsaintwho.pojo;

public class GroupMsgDBPojo  {

    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;

    private String content;
    private Integer type;
    private String targetGroupId;
    private String sentUser;

    public GroupMsgDBPojo() {
    }

    public GroupMsgDBPojo(String content, Integer type, String targetGroupId, String sentUser) {
        this.content = content;
        this.type = type;
        this.targetGroupId = targetGroupId;
        this.sentUser = sentUser;
    }

    public String getSentUser() {
        return sentUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String gettargetGroupId() {
        return targetGroupId;
    }

    public void settargetGroupId(String targetGroupId) {
        this.targetGroupId = targetGroupId;
    }
}
