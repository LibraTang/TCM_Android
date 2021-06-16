package com.example.tcm.entity;

import java.io.Serializable;

public class InquiryDetail implements Serializable {

    private Integer idid;
    private Integer uid;
    private String name;
    private String content;
    private String type;
    private Integer doctorId;
    private String doctor;

    public Integer getIdid() {
        return idid;
    }

    public void setIdid(Integer idid) {
        this.idid = idid;
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

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InquiryDetail{" +
                "idid=" + idid +
                ", uid=" + uid +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                ", doctorId=" + doctorId +
                ", doctor='" + doctor + '\'' +
                '}';
    }
}
