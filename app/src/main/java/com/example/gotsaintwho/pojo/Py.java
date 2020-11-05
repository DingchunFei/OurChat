package com.example.gotsaintwho.pojo;

/**
 * @author: Kandoka
 * @createTime: 2020/10/30 19:44
 * @description:
 */

public class Py {
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
