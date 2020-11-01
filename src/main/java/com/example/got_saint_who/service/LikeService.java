package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.LikeDao;
import com.example.got_saint_who.pojo.Like;
import com.example.got_saint_who.pojo.LikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: Kandoka
 * @createTime: 2020/10/31 10:41
 * @description:
 */
@Service
public class LikeService {
    @Autowired
    private LikeDao likeDao;

    //查找所有moment的点赞信息
    public LikeDTO findAllLikes(List<Integer> momentIds){
        LikeDTO likeDTO = new LikeDTO();
        Map<Integer, List<Like>> likeListMap = likeDTO.getLikeMap();
        //根据momentId列表查找所有点赞，加入到map中
        for(Integer momentId: momentIds){
            List<Like> likeList = likeDao.findByMomentId(momentId);
            likeListMap.put(momentId, likeList);
        }
        likeDTO.setLikeMap(likeListMap);
        return likeDTO;
    }

    public Like addLike(Like like){
        return likeDao.save(like);
    }

    public Like deleteLike(Like like){
        likeDao.delete(like);
        return like;
    }
}
