package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class BaodanBean extends HttpRespObject {
    //保单号
    public String ibaodanNo;
    //保单号
    public int ibaodanStatus;
    //姓名
    public String iname = "";
    //身份证号
    public long icardType;
    //身份证号T
    public String icardNo = "";
    //投保类型
    public long ibaodanType;
    //投保分类
    public long ibaodanKind;
    //投保数量
    public long iamount;
    //投保金额
    public long imoney;
    //代理人
    public String iproxyName = "";
    //采集数量
    public long icollectAmount;
    //理赔数量
    public long ilipeiAmount;
    //投保时间
    public String ibaodanTime;
    //电话
    public String ibaodanPhone;
    //地址
    public String iaddress;
    //经度
    public String ilongitude;
    //纬度
    public String ilatitude;
    //保单id
    public int iid;
    //保险费率
    public double baodanRate;
    //身份证正面照片
    public String cardFrontShow;
    //身份证反面照片
    public String cardBackShow;
    //开户行
    public String bankName;
    //银行账号
    public String bankNo;
    //银行卡照片
    public String bankFrontShow;
    //真实保单号
    public String ibaodanNoReal;
    public String createtime;

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        iname = data.optString("name", "");
        icardType = data.optLong("cardType",0);
        icardNo = data.optString("cardNo", "");
        ibaodanNo = data.optString("baodanNo","");
        ibaodanStatus = data.optInt("baodanStatus",0);
        ibaodanType = data.optLong("baodanType",0);
        ibaodanKind = data.optLong("toubaoKind",0);
        iamount = data.optLong("amount",0);
        imoney = data.optLong("money",0);
        iproxyName = data.optString("proxyName","");
        icollectAmount = data.optLong("collectAmount",0);
        ilipeiAmount = data.optLong("lipeiAmount",0);
        ibaodanTime = data.optString("baodanTime", "");
        createtime = data.optString("createtime", "");
        ibaodanPhone = data.optString("phone", "");
        iaddress = data.optString("address", "");
        ilongitude = data.optString("longitude", "");
        ilatitude = data.optString("latitude", "");
        iid = data.optInt("id",0);
        baodanRate = data.optDouble("baodanRate",0);
        cardFrontShow = data.optString("cardFrontShow", "");
        cardBackShow = data.optString("cardBackShow", "");
        bankName = data.optString("bankName", "");
        bankNo = data.optString("bankNo", "");
        bankFrontShow = data.optString("bankFrontShow", "");
        ibaodanNoReal = data.optString("baodanNoReal", "");
    }
}
