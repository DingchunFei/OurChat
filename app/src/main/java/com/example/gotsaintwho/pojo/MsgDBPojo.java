package com.example.gotsaintwho.pojo;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class MsgDBPojo  extends DataSupport {

    @Column(ignore = true)  //在存数据库的时候忽略该字段
    public static final int TYPE_RECEIVED = 0;
    @Column(ignore = true)  //在存数据库的时候忽略该字段
    public static final int TYPE_SENT = 1;

    private String content;
    private Integer type;
    private String targetUserId;

    public MsgDBPojo() {
    }

    public MsgDBPojo(String content, Integer type, String targetUserId) {
        this.content = content;
        this.type = type;
        this.targetUserId = targetUserId;
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

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }
}
