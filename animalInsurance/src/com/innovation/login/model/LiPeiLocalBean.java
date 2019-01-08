package com.innovation.login.model;

public class LiPeiLocalBean {
    //保单号
    public String pbaodanNo;
    //投保人
    public String pinsurename;
    //证件号
    public String pcardNo;
    //出险原因
    public String pinsureReason;
    //区舍栏
    public String pinsureQSL;
    // 日期
    public String pinsureDate;
    //经度
    public String plongitude;
    // 纬度
    public String platitude;
    // 种类
    public String panimalType;
    // 耳标号
    public String earsTagNo;
    // zip路径
    public String pzippath;
    // 录入状态  1未录入  2已录入  3已上传
    public String precordeText;
    // 提示信息
    public String precordeMsg;

    public LiPeiLocalBean(String pbaodanNo, String pinsurename, String pcardNo, String pinsureReason, String pinsureQSL, String pinsureDate, String plongitude, String platitude, String panimalType, String earsTagNo, String pzippath, String precordeText, String precordeMsg) {
        this.pbaodanNo = pbaodanNo;
        this.pinsurename = pinsurename;
        this.pcardNo = pcardNo;
        this.pinsureReason = pinsureReason;
        this.pinsureQSL = pinsureQSL;
        this.pinsureDate = pinsureDate;
        this.plongitude = plongitude;
        this.platitude = platitude;
        this.panimalType = panimalType;
        this.earsTagNo = earsTagNo;
        this.pzippath = pzippath;
        this.precordeText = precordeText;
        this.precordeMsg = precordeMsg;
    }

    public void setPrecordeText(String precordeText) {
        this.precordeText = precordeText;
    }
}
