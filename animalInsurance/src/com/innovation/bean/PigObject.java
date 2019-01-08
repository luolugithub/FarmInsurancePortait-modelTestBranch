package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class PigObject extends HttpRespObject {
    //舍号
    public String pig_sheNo = "";
    //圈号
    public String pig_juanNo = "";
    //猪标号
    public String pig_No = "";
    //地址
    public String pig_address = "";
    //保单号
    public String pig_baodanNo = "";
    //唯一猪的标识ID
    public int pig_libId;
    //采集人姓名
    public String pig_person = "";
    //猪的种类
    public String pig_Type = "";
    //采集类别1:投保采集 2:理赔采集
    public int pig_caijiType;
    // 理赔单号，type=2时必需
    public String pig_lipeiNo;

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        pig_baodanNo = data.optString("baodanNo", "");
        pig_sheNo = data.optString("sheNo", "");
        pig_juanNo = data.optString("juanNo", "");
        pig_No = data.optString("pigNo", "");
        pig_address = data.optString("address", "");
        pig_libId = data.optInt("libId", 0);
        pig_person = data.optString("person", "");
        pig_Type = data.optString("pigType", "");
        pig_caijiType = data.optInt("type", 1);
        pig_lipeiNo = data.optString("lipeiNo", "");
    }




}
