package com.example.got_saint_who.dao;

import com.example.got_saint_who.pojo.Like;
import com.example.got_saint_who.pojo.Moment;
import com.example.got_saint_who.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author: Kandoka
 * @createTime: 2020/10/31 10:41
 * @description:
 */

public interface LikeDao extends JpaRepository<Like,Integer>, JpaSpecificationExecutor<Like> {
    public List<Like> findByMomentId(Integer momentId);
}
