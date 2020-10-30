package com.example.got_saint_who.pojo;

import javax.persistence.*;

@Entity
@Table(name = "tb_moment")
public class Moment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer momentId;
    private Integer userId;             //user Id
    private String username;          //user name
    private String momentImage;         //moment image
    private String momentContent;       //moment content
    private String location;  //location of the sender

    @Override
    public String toString() {
        return "Moment{" +
                "momentId=" + momentId +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", momentImage='" + momentImage + '\'' +
                ", momentContent='" + momentContent + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public Moment() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Moment(String username, String momentContent) {
        this.username = username;
        this.momentContent = momentContent;
    }

    public Moment(String momentImage, String username, String momentContent) {
        this.momentImage = momentImage;
        this.username = username;
        this.momentContent = momentContent;
    }


    public Moment(Integer userId, String username, String momentImage, String momentContent) {
        this.userId = userId;
        this.username = username;
        this.momentImage = momentImage;
        this.momentContent = momentContent;
    }

    public Integer getMomentId() {
        return momentId;
    }

    public void setMomentId(Integer momentId) {
        this.momentId = momentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
