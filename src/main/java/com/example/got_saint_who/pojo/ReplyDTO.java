package com.example.got_saint_who.pojo;

import java.util.List;
import java.util.Map;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 17:40
 * @description:
 */

public class ReplyDTO {
    private Map<Integer, List<Reply>> replyMap;

    public Map<Integer, List<Reply>> getReplyMap() {
        return replyMap;
    }

    public void setReplyMap(Map<Integer, List<Reply>> replyMap) {
        this.replyMap = replyMap;
    }
}
