package com.innovation.login.model;

public class LocalModel {
    private String baodanNo;
    private String name;
    private String cardNo;
    private String insureDate;
    private String type;

    public LocalModel(String baodanNo, String name, String cardNo, String insureDate,String type) {
        this.baodanNo = baodanNo;
        this.name = name;
        this.cardNo = cardNo;
        this.insureDate = insureDate;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaodanNo() {
        return baodanNo;
    }

    public void setBaodanNo(String baodanNo) {
        this.baodanNo = baodanNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getInsureDate() {
        return insureDate;
    }

    public void setInsureDate(String insureDate) {
        this.insureDate = insureDate;
    }

    @Override
    public String toString() {
        return "LocalModel{" +
                "baodanNo='" + baodanNo + '\'' +
                ", name='" + name + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", insureDate='" + insureDate + '\'' +
                '}';
    }
}
