package com.example.got_saint_who.dao;

import com.example.got_saint_who.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserDao extends JpaRepository<User,Integer>, JpaSpecificationExecutor<User> {
    public int countByUsername(String userName);
    public User findByUsernameAndPassword(String username, String password);

    //根据用户名查询模糊用户
    public List<User> findByUsernameContaining(String username);

    public List<User> findAll();
}
