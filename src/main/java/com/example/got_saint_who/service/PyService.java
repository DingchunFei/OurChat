package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.MomentDao;
import com.example.got_saint_who.dao.PyDao;
import com.example.got_saint_who.pojo.Py;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 19:47
 * @description:
 */

@Service
public class PyService {

    @Autowired
    private PyDao pyDao;

    public List<Py> findAllFriendById(Integer userId){
        System.out.println("===========PyService=====findAllFriendById====== "+userId);
        return pyDao.findByPyIdFirst(userId);
    }

    public Py save(Py py){
        return pyDao.save(py);
    }

    public void delete(Py py){
        pyDao.delete(py);
    }
}
