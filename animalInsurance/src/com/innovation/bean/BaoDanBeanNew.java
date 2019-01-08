package com.innovation.bean;

public class BaoDanBeanNew {
    public String bankName;
    public int baodanType;
    public int id;
    public String createtime;

    public BaoDanBeanNew(String bankName, int baodanType, int id, String createtime) {
        this.bankName = bankName;
        this.baodanType = baodanType;
        this.id = id;
        this.createtime = createtime;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBaodanType() {
        return baodanType;
    }

    public void setBaodanType(int baodanType) {
        this.baodanType = baodanType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
