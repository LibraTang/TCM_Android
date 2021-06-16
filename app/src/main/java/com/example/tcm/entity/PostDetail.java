package com.example.tcm.entity;

import java.io.Serializable;

/**
 * 帖子详情
 */
public class PostDetail implements Serializable {

    private Long pid;
    private Integer uid;
    private String name;
    private String content;
    private String title;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PostDetailBo{" +
                "pid=" + pid +
                ", uid=" + uid +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
