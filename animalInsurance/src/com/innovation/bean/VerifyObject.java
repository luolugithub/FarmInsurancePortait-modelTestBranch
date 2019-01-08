package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class VerifyObject extends HttpRespObject {

    //保单号
    public String verify_baodanNo = "";
    //用户ID
    public int  verify_userId = 0;
    //唯一猪的标识ID
    public int  verify_libId = 0;
    //猪标号
    public String  verify_libNum = "";
    //猪信息
    public String upload_pigInfo = "";

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        verify_userId = data.optInt("userId", 0);
        verify_libId = data.optInt("libId", 0);
        verify_baodanNo = data.optString("baodanNo", "");
        verify_libNum = data.optString("libNum", "");
    }
}
