package com.example.got_saint_who.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 20:32
 * @description:
 */

public class LikeDTO {
    private Map<Integer, List<Like>> likeMap;

    public Map<Integer, List<Like>> getLikeMap() {
        if(likeMap == null)
            return new HashMap<>();
        return likeMap;
    }

    public void setLikeMap(Map<Integer, List<Like>> likeMap) {
        this.likeMap = likeMap;
    }
}
