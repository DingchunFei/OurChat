package com.example.got_saint_who.dao;

import com.example.got_saint_who.pojo.Py;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PyDao extends JpaRepository<Py,Integer>, JpaSpecificationExecutor<Py> {

    public List<Py> findByPyIdFirst(Integer pyIdFirst);

}
