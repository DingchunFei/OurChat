package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.MomentDao;
import com.example.got_saint_who.dao.PyDao;
import com.example.got_saint_who.dao.ReplyDao;
import com.example.got_saint_who.dao.UserDao;
import com.example.got_saint_who.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 20:22
 * @description:
 */

@Service
public class ReplyServive {
    @Autowired
    ReplyDao replyDao;

    @Autowired
    PyDao pyDao;

    @Autowired
    UserDao userDao;

    @Autowired
    MomentDao momentDao;

    public ReplyDTO findAllReplies(MomentIds momentIds){
        ReplyDTO replyDTO = new ReplyDTO();
        Map<Integer, List<Reply>> replyMap = new HashMap<>();
        //查找所有moment的reply
        for(Integer momentId: momentIds.getMomentIds()){
            replyMap.put(momentId, replyDao.findByMomentId(momentId));
        }
        replyDTO.setReplyMap(replyMap);
        return replyDTO;
    }

    public List<Reply> findReplies(Integer momentId){
        return replyDao.findByMomentId(momentId);
    }

    public Reply save(Reply reply){
        return replyDao.save(reply);
    }

    public void delete(Reply reply){
        replyDao.delete(reply);
    }
}
