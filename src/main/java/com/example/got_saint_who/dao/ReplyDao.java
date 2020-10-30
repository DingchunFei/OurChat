package com.example.got_saint_who.dao;

import com.example.got_saint_who.pojo.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReplyDao extends JpaRepository<Reply,Integer>, JpaSpecificationExecutor<Reply> {
    public List<Reply> findByMomentId(Integer momentId);
}
