package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class PayObject extends HttpRespObject {
    BaodanBean obj_baodan;
    LipeiBean obj_lipei;

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        JSONObject json;
        obj_baodan = new BaodanBean();
        json = data.optJSONObject("baodan");
        obj_baodan.setdata(json);

        obj_lipei = new LipeiBean();
        json = data.optJSONObject("lipei");
        obj_lipei.setdata(json);

    }

}
