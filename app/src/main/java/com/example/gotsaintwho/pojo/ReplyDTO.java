package com.example.gotsaintwho.pojo;

import java.util.*;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 17:40
 * @description:
 */

public class ReplyDTO {
    private Map<Integer, List<Reply>> replyMap;

    public Map<Integer, List<Reply>> getReplyMap() {
        if(replyMap == null)
            return new HashMap<>();
        return replyMap;
    }

    public void setReplyMap(Map<Integer, List<Reply>> replyMap) {
        this.replyMap = replyMap;
    }
}
