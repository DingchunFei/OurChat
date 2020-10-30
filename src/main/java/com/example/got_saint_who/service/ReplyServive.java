package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.MomentDao;
import com.example.got_saint_who.dao.PyDao;
import com.example.got_saint_who.dao.ReplyDao;
import com.example.got_saint_who.dao.UserDao;
import com.example.got_saint_who.pojo.Py;
import com.example.got_saint_who.pojo.Reply;
import com.example.got_saint_who.pojo.ReplyDTO;
import com.example.got_saint_who.pojo.User;
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

    public ReplyDTO findAllReplies(User user){
        ReplyDTO replyDTO = new ReplyDTO();
        Map<Integer, List<Reply>> replyMap = new HashMap<>();
        //�õ����ѵ�id
        List<Py> pyList = pyDao.findByPyIdFirst(user.getUserId());
        //�����˵�id�б�
        List<Integer> ids = new ArrayList<>();
        //�����Լ�
        ids.add(user.getUserId());
        //��������
        for(Py py: pyList){
            ids.add(py.getPyIdSecond());
        }
        //���������˵�moment
        List<Object []> momentList = momentDao.queryByIds(ids);
        for (Object objectArray[] :momentList){
            if(objectArray.length>= 1 && objectArray[0]!=null){
                Integer momentId = (Integer) objectArray[0];
                //keyΪmoment��id, valueΪ��moment�µ����лظ�
                replyMap.put(momentId, findReplies(momentId));
            }
        }
        replyDTO.setReplyMap(replyMap);
        return replyDTO;
    }

    public List<Reply> findReplies(Integer momentId){
        return replyDao.findByMomentId(momentId);
    }

    public void save(Reply reply){
        replyDao.save(reply);
    }

    public void delete(Reply reply){
        replyDao.delete(reply);
    }
}
