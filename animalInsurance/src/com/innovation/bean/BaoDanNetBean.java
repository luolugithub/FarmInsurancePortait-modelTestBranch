package com.innovation.bean;

public class BaoDanNetBean {
    private String baodanNo;
    private String yanBiaoName;
    private String name;
    private String cardNo;
    private String createtime;

    public BaoDanNetBean(String baodanNo, String yanBiaoName, String name, String cardNo, String createtime) {
        this.baodanNo = baodanNo;
        this.yanBiaoName = yanBiaoName;
        this.name = name;
        this.cardNo = cardNo;
        this.createtime = createtime;
    }

    public String getBaodanNo() {
        return baodanNo;
    }

    public void setBaodanNo(String baodanNo) {
        this.baodanNo = baodanNo;
    }

    public String getYanBiaoName() {
        return yanBiaoName;
    }

    public void setYanBiaoName(String yanBiaoName) {
        this.yanBiaoName = yanBiaoName;
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

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
