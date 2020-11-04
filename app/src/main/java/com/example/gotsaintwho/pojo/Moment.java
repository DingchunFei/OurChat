package com.example.gotsaintwho.pojo;

public class Moment {

    private String momentId;
    private String userId;        //user image
    private String username;          //user name
    private String momentImage;         //moment image
    private String momentContent;       //moment content
    private String location;            //location of the sender

    public Moment() {}

    public Moment(String momentId, String userId, String username, String momentImage, String momentContent) {
        this.momentId = momentId;
        this.userId = userId;
        this.username = username;
        this.momentImage = momentImage;
        this.momentContent = momentContent;
    }

    public Moment(String momentId, String userId, String username, String momentImage, String momentContent, String location) {
        this.momentId = momentId;
        this.userId = userId;
        this.username = username;
        this.momentImage = momentImage;
        this.momentContent = momentContent;
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    @Override
    public String toString() {
        return "Moment{" +
                "momentId='" + momentId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", momentImage='" + momentImage + '\'' +
                ", momentContent='" + momentContent + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMomentImage() {
        return momentImage;
    }

    public void setMomentImage(String momentImage) {
        this.momentImage = momentImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMomentContent() {
        return momentContent;
    }

    public void setMomentContent(String momentContent) {
        this.momentContent = momentContent;
    }
}
