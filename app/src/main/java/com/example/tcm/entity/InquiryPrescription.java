package com.example.tcm.entity;

import java.io.Serializable;

public class InquiryPrescription implements Serializable {

    private Integer ipid;
    private Integer idid;
    private String content;
    private Integer doctorId;
    private String doctor;

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getIpid() {
        return ipid;
    }

    public void setIpid(Integer ipid) {
        this.ipid = ipid;
    }

    public Integer getIdid() {
        return idid;
    }

    public void setIdid(Integer idid) {
        this.idid = idid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "InquiryPrescription{" +
                "ipid=" + ipid +
                ", idid=" + idid +
                ", content='" + content + '\'' +
                ", doctorId=" + doctorId +
                ", doctor='" + doctor + '\'' +
                '}';
    }
}
