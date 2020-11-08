package com.example.gotsaintwho.pojo;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {
    private final String groupId;
    private String ownerId;
    private String groupName;
    private List<User> usersInGroup;

    public Group(String groupId, String ownerId, String groupName, List<User> usersInGroup) {
        this.groupId = groupId;
        this.ownerId = ownerId;
        this.groupName = groupName;
        this.usersInGroup = usersInGroup;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<User> getUsersInGroup() {
        return usersInGroup;
    }

    public void setUsersInGroup(List<User> usersInGroup) {
        this.usersInGroup = usersInGroup;
    }

    @NonNull
    @Override
    public String toString() {
        String groupUsers = "";
        for (User user: this.usersInGroup) {
            groupUsers += user.getUsername();
        }

        return "Group{" +
                "group id='" + groupId + '\'' +
                "owner id=" + this.ownerId +
                ", group name='" + groupName + '\'' +
                groupUsers +
                '}';
    }
}
