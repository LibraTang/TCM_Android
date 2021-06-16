package com.example.tcm.entity;

import java.io.Serializable;

public class PostComment implements Serializable {

    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 用户昵称
     */
    private String name;
    /**
     * 评论内容
     */
    private String detail;

    public PostComment() {}

    public PostComment(Integer uid, String name, String detail) {
        this.uid = uid;
        this.name = name;
        this.detail = detail;
    }

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "PostComment{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
