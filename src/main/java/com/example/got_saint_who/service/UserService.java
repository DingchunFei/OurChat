package com.example.got_saint_who.service;

import com.example.got_saint_who.dao.UserDao;
import com.example.got_saint_who.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 增加
     * @param user
     */
    public User add(User user) {
        int count = userDao.countByUsername(user.getUsername());
        if(count>0) return user;    //说明用户已经创建！
        User userWithId = userDao.save(user);
        return userWithId;
    }


    public User login(String username, String password) {
        return userDao.findByUsernameAndPassword(username,password);
    }

    public List<User> findByUsernameLike(String username) {
        List<User> userList = userDao.findByUsernameContaining(username);
        System.out.println("---------------------> "+ userList);

        if(userList!=null){
            for (User user : userList){
                System.out.println("+++++++++++++++++++> "+ user);
                user.setPassword(null);
            }
        }
        return userList;
    }

    public User findUserById(Integer userId) {
        return userDao.findById(userId).get();
    }
}
