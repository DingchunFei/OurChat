package com.example.got_saint_who.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 17:40
 * @description:
 */
@Entity
@Table(name = "tb_reply")
public class Reply implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer replyId;
    private Integer momentId;
    private Integer commenterId;
    private String commenterName;
    private String replyContent;
    private Integer replyToId;
    private String replyToName;

    public Reply(){}

    @Override
    public String toString() {
        return "Reply{" +
                "replyId=" + replyId +
                ", momentId=" + momentId +
                ", commenterId=" + commenterId +
                ", commenterName='" + commenterName + '\'' +
                ", replyName='" + replyContent + '\'' +
                ", replyToId=" + replyToId +
                ", replyToName='" + replyToName + '\'' +
                '}';
    }

    public Integer getReplyId() {
        return replyId;
    }

    public void setReplyId(Integer replyId) {
        this.replyId = replyId;
    }

    public Integer getMomentId() {
        return momentId;
    }

    public void setMomentId(Integer momentId) {
        this.momentId = momentId;
    }

    public Integer getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(Integer commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyName) {
        this.replyContent = replyName;
    }

    public Integer getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(Integer replyToId) {
        this.replyToId = replyToId;
    }

    public String getReplyToName() {
        return replyToName;
    }

    public void setReplyToName(String replyToName) {
        this.replyToName = replyToName;
    }
}
