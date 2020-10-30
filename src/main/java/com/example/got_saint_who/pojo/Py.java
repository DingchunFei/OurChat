package com.example.got_saint_who.pojo;

import javax.persistence.*;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 19:44
 * @description:
 */

@Entity
@Table(name = "tb_py")
public class Py {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer pyIdFirst;
    private Integer pyIdSecond;

    public Py() {}

    @Override
    public String toString() {
        return "Py{" +
                "id=" + id +
                ", pyIdFirst=" + pyIdFirst +
                ", pyIdSecond=" + pyIdSecond +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPyIdFirst() {
        return pyIdFirst;
    }

    public void setPyIdFirst(Integer pyIdFirst) {
        this.pyIdFirst = pyIdFirst;
    }

    public Integer getPyIdSecond() {
        return pyIdSecond;
    }

    public void setPyIdSecond(Integer pyIdSecond) {
        this.pyIdSecond = pyIdSecond;
    }
}
