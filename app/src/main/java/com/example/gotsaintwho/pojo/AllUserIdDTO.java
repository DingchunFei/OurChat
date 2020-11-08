package com.example.gotsaintwho.pojo;

import java.util.List;

public class AllUserIdDTO {
    public List<String> friendsIdList;

    public List<String> getFriendsIdList() {
        return friendsIdList;
    }

    public void setFriendsIdList(List<String> friendsIdList) {
        this.friendsIdList = friendsIdList;
    }

    public AllUserIdDTO(List<String> friendsIdList) {
        this.friendsIdList = friendsIdList;
    }

    public AllUserIdDTO() { }
}
