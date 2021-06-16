package com.example.tcm.entity;

import java.io.Serializable;

public class RecommendDoctor implements Serializable {

    private Integer uid;
    private String name;
    private String type;
    private Integer answerNumber;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(Integer answerNumber) {
        this.answerNumber = answerNumber;
    }

    @Override
    public String toString() {
        return "RecommendDoctorBo{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", answerNumber=" + answerNumber +
                '}';
    }
}
