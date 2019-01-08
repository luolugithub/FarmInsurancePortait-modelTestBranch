package com.innovation.login.model;

public class LocalModelNongxian {
    //地址
    private String address = "";
    //保单数量
    private int amount = 0;
    //动物类型
    private int animalType = 0;
    /**
     * 保单号
     */
    private String baodanNo = "";
    //保单状态
    private String baodanStatus = "";
    //投保时间
    private String baodanTime = "";
    //验标名称
    private String yanBiaoName = "";
    //投保类型
    private String baodanType = "";
    //证件号
    private String cardNo = "";
    //证件类型
    private String cardType = "";
    //采集数量
    private String collectAmount = "";
    //id
    private String id = "";
    //纬度
    private double latitude = 0;
    //理赔数量
    private String lipeiAmount = "";
    //经度
    private double longitude = 0;

    //金额
    private String money = "";
    //投保人
    private String name = "";
    //
    private String phone = "";
    //代理人
    private String proxyName = "";
    //状态
    private String status = "";
    //投保类型
    private String toubaoKind = "";

    //保险费率
    private int baodanRate;
    //开户行
    private String bankName;
    //银行账号
    private String bankNo;
    private String bankBack = "";
    private String bankBackShow = "";
    private String bankFront = "";
    private String bankFrontShow = "";
    private String baodanNoReal = "";
    private String bdsId = "";
    private String createtime = "";
    private String uid = "";
    private String insureDate;
    private String type;
    public LocalModelNongxian(String baodanNo, String name, String cardNo, String insureDate,String type,String yanBiaoName) {
        this.baodanNo = baodanNo;
        this.name = name;
        this.cardNo = cardNo;
        this.insureDate = insureDate;
        this.type = type;
        this.yanBiaoName = yanBiaoName;
    }

    @Override
    public String toString() {
        return "LocalModelNongxian{" +
                "address='" + address + '\'' +
                ", amount=" + amount +
                ", animalType=" + animalType +
                ", baodanNo='" + baodanNo + '\'' +
                ", baodanStatus='" + baodanStatus + '\'' +
                ", baodanTime='" + baodanTime + '\'' +
                ", yanBiaoName='" + yanBiaoName + '\'' +
                ", baodanType='" + baodanType + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", cardType='" + cardType + '\'' +
                ", collectAmount='" + collectAmount + '\'' +
                ", id='" + id + '\'' +
                ", latitude=" + latitude +
                ", lipeiAmount='" + lipeiAmount + '\'' +
                ", longitude=" + longitude +
                ", money='" + money + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", proxyName='" + proxyName + '\'' +
                ", status='" + status + '\'' +
                ", toubaoKind='" + toubaoKind + '\'' +
                ", baodanRate=" + baodanRate +
                ", bankName='" + bankName + '\'' +
                ", bankNo='" + bankNo + '\'' +
                ", bankBack='" + bankBack + '\'' +
                ", bankBackShow='" + bankBackShow + '\'' +
                ", bankFront='" + bankFront + '\'' +
                ", bankFrontShow='" + bankFrontShow + '\'' +
                ", baodanNoReal='" + baodanNoReal + '\'' +
                ", bdsId='" + bdsId + '\'' +
                ", createtime='" + createtime + '\'' +
                ", uid='" + uid + '\'' +
                ", insureDate='" + insureDate + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAnimalType() {
        return animalType;
    }

    public void setAnimalType(int animalType) {
        this.animalType = animalType;
    }

    public String getBaodanNo() {
        return baodanNo;
    }

    public void setBaodanNo(String baodanNo) {
        this.baodanNo = baodanNo;
    }

    public String getBaodanStatus() {
        return baodanStatus;
    }

    public void setBaodanStatus(String baodanStatus) {
        this.baodanStatus = baodanStatus;
    }

    public String getBaodanTime() {
        return baodanTime;
    }

    public void setBaodanTime(String baodanTime) {
        this.baodanTime = baodanTime;
    }

    public String getYanBiaoName() {
        return yanBiaoName;
    }

    public void setYanBiaoName(String yanBiaoName) {
        this.yanBiaoName = yanBiaoName;
    }

    public String getBaodanType() {
        return baodanType;
    }

    public void setBaodanType(String baodanType) {
        this.baodanType = baodanType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCollectAmount() {
        return collectAmount;
    }

    public void setCollectAmount(String collectAmount) {
        this.collectAmount = collectAmount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLipeiAmount() {
        return lipeiAmount;
    }

    public void setLipeiAmount(String lipeiAmount) {
        this.lipeiAmount = lipeiAmount;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToubaoKind() {
        return toubaoKind;
    }

    public void setToubaoKind(String toubaoKind) {
        this.toubaoKind = toubaoKind;
    }

    public int getBaodanRate() {
        return baodanRate;
    }

    public void setBaodanRate(int baodanRate) {
        this.baodanRate = baodanRate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getBankBack() {
        return bankBack;
    }

    public void setBankBack(String bankBack) {
        this.bankBack = bankBack;
    }

    public String getBankBackShow() {
        return bankBackShow;
    }

    public void setBankBackShow(String bankBackShow) {
        this.bankBackShow = bankBackShow;
    }

    public String getBankFront() {
        return bankFront;
    }

    public void setBankFront(String bankFront) {
        this.bankFront = bankFront;
    }

    public String getBankFrontShow() {
        return bankFrontShow;
    }

    public void setBankFrontShow(String bankFrontShow) {
        this.bankFrontShow = bankFrontShow;
    }

    public String getBaodanNoReal() {
        return baodanNoReal;
    }

    public void setBaodanNoReal(String baodanNoReal) {
        this.baodanNoReal = baodanNoReal;
    }

    public String getBdsId() {
        return bdsId;
    }

    public void setBdsId(String bdsId) {
        this.bdsId = bdsId;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInsureDate() {
        return insureDate;
    }

    public void setInsureDate(String insureDate) {
        this.insureDate = insureDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
