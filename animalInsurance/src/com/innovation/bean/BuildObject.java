package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

/**
 * Created by haojie on 2018/5/10.
 */

public class BuildObject extends HttpRespObject {

    //保单号
    public String build_baodanNo = "";
    //用户ID
    public int build_userId = 0;
    public int offlineBuildStatus = 0;


    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        build_userId = data.optInt("userId", 0);
        build_baodanNo = data.optString("baodanNo", "");
        offlineBuildStatus = data.optInt("status",99);
    }
}
