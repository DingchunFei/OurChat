package com.example.got_saint_who.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 19:40
 * @description:
 */
@Entity
@Table(name = "tb_like")
public class Like implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId;
    private Integer momentId;
    private Integer userId;
    private String userName;

    public Like() {}

    @Override
    public String toString() {
        return "Like{" +
                "likeId=" + likeId +
                ", momentId=" + momentId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }

    public Integer getLikeId() {
        return likeId;
    }

    public void setLikeId(Integer likeId) {
        this.likeId = likeId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
