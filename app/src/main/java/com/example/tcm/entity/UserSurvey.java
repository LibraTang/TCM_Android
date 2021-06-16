package com.example.tcm.entity;

import java.io.Serializable;
import java.util.Date;

public class UserSurvey implements Serializable {
    /**
     * 主键
     */
    private Integer usid;
    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 穿的多 0一样 1少 2多
     */
    private Integer wearMore;
    /**
     * 怕热 0不是 1是
     */
    private Integer hot;
    /**
     * 怕冷 0不是 1是
     */
    private Integer cold;
    /**
     * 心烦 0不是 1是
     */
    private Integer irritation;
    /**
     * 上火 0不是 1是
     */
    private Integer getInflamed;
    /**
     * 一天大便次数
     */
    private Integer shit;
    /**
     * 小便颜色 0清澈 1黄色
     */
    private Integer pee;
    /**
     * 腰酸 0不是 1是
     */
    private Integer waist;
    /**
     * 食欲 0不好 1好
     */
    private Integer appetite;
    /**
     * 睡眠质量 0不好 1好
     */
    private Integer sleep;

    public Integer getUsid() {
        return usid;
    }

    public void setUsid(Integer usid) {
        this.usid = usid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getWearMore() {
        return wearMore;
    }

    public void setWearMore(Integer wearMore) {
        this.wearMore = wearMore;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getCold() {
        return cold;
    }

    public void setCold(Integer cold) {
        this.cold = cold;
    }

    public Integer getIrritation() {
        return irritation;
    }

    public void setIrritation(Integer irritation) {
        this.irritation = irritation;
    }

    public Integer getGetInflamed() {
        return getInflamed;
    }

    public void setGetInflamed(Integer getInflamed) {
        this.getInflamed = getInflamed;
    }

    public Integer getShit() {
        return shit;
    }

    public void setShit(Integer shit) {
        this.shit = shit;
    }

    public Integer getPee() {
        return pee;
    }

    public void setPee(Integer pee) {
        this.pee = pee;
    }

    public Integer getWaist() {
        return waist;
    }

    public void setWaist(Integer waist) {
        this.waist = waist;
    }

    public Integer getAppetite() {
        return appetite;
    }

    public void setAppetite(Integer appetite) {
        this.appetite = appetite;
    }

    public Integer getSleep() {
        return sleep;
    }

    public void setSleep(Integer sleep) {
        this.sleep = sleep;
    }

    @Override
    public String toString() {
        return "UserSurvey{" +
                "usid=" + usid +
                ", uid=" + uid +
                ", wearMore=" + wearMore +
                ", hot=" + hot +
                ", cold=" + cold +
                ", irritation=" + irritation +
                ", getInflamed=" + getInflamed +
                ", shit=" + shit +
                ", pee=" + pee +
                ", waist=" + waist +
                ", appetite=" + appetite +
                ", sleep=" + sleep +
                '}';
    }
}
