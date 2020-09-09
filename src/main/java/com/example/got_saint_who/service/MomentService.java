package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.MomentDao;
import com.example.got_saint_who.pojo.Friends;
import com.example.got_saint_who.pojo.Moment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.LinkedList;
import java.util.List;

@Service
public class MomentService {

    @Autowired
    private MomentDao momentDao;

    public void save(Moment moment){
        momentDao.save(moment);
    }

    public List<Moment> queryByIds(List<Integer> ids){
        List<Object[]> objects = momentDao.queryByIds(ids);
        LinkedList<Moment> moments = new LinkedList<>();
        for (Object objectArray[] :objects){
            Moment moment = new Moment();
            if(objectArray.length>= 1 && objectArray[1]!=null){
                moment.setUserId((Integer) objectArray[1]);
            }
            if(objectArray.length>= 2 && objectArray[2]!=null){
                moment.setUsername(objectArray[2].toString());
            }
            if(objectArray.length>= 3 && objectArray[3]!=null){
                moment.setMomentImage(objectArray[3].toString());
            }
            if(objectArray.length>= 4 && objectArray[4]!=null){
                moment.setMomentContent(objectArray[4].toString());
            }
            if(objectArray.length>= 5 && objectArray[5]!=null){
                moment.setLocation(objectArray[5].toString());
            }
            //用push才能看到最新的
            moments.push(moment);
        }

        return moments;
    }

}
