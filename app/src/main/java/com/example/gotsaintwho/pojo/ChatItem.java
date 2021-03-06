package com.example.gotsaintwho.pojo;

/**
 * 聊天列表内的每一项
 */
public class ChatItem {

    private String targetUsername;
    private String targetUserId;
    private String lastChat;
    private int imageId;
    private Group targetGroup;
    private boolean isGroupChat = false;

    public Group getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(Group targetGroup) {
        this.targetGroup = targetGroup;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public ChatItem(String targetUsername, String targetUserId, String lastChat, int imageId) {
        this.targetUsername = targetUsername;
        this.targetUserId = targetUserId;
        this.lastChat = lastChat;
        this.imageId = imageId;
    }

    public ChatItem(String targetUserName, String targetUserId, int imageId) {
        this.targetUsername = targetUserName;
        this.targetUserId = targetUserId;
        this.imageId = imageId;
    }

    public ChatItem(String targetUsername, String targetUserId, String lastChat) {
        this.targetUsername = targetUsername;
        this.targetUserId = targetUserId;
        this.lastChat = lastChat;
    }

    public ChatItem(Group targetGroup, String lastChat) {
        this.targetGroup = targetGroup;
        this.lastChat = lastChat;
        this.isGroupChat = true;
    }

    public String getTargetUserName() {
        return targetUsername;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUsername = targetUserName;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getLastChat() {
        return lastChat;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }
}
