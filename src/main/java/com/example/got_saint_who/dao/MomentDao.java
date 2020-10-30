package com.example.got_saint_who.dao;

import com.example.got_saint_who.pojo.Moment;
import com.example.got_saint_who.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public interface MomentDao extends JpaRepository<Moment,Integer>, JpaSpecificationExecutor<User> {

    @Query(value="select * from tb_moment where user_id in (:ids)",nativeQuery = true)
    public List<Object []> queryByIds(List<Integer> ids);

}
