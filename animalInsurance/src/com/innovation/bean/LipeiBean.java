package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class LipeiBean extends HttpRespObject {
    //保单号
    public String pbaodanNo;
    //投保数量
    public int pamount;
    //理赔状态
    public String plipeiStatus = "";
    //采集数量
    public int pcollectAmount;
    // 理赔单号p
    public String lipeiNo;
    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        pbaodanNo = data.optString("baodanNo", "");
        pamount = data.optInt("amount", 0);
        plipeiStatus = data.optString("lipeiStatus", "");
        pcollectAmount = data.optInt("collectAmount", 0);
        lipeiNo = data.optString("lipeiNo", "");
    }
}
