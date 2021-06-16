package com.example.tcm.entity;

import java.io.Serializable;

public class UserAcc implements Serializable {

    private Integer uid;
    private String name;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserAcc{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                '}';
    }
}
