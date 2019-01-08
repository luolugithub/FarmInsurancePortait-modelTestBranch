package com.innovation.bean;

import com.innovation.login.RespObject;
import com.innovation.utils.HttpRespObject;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyCodeBean extends HttpRespObject {
    /**
     * 手机验证码
     */
    public String result_data = "";
    /**
     * 手机号
     */
    public String result_status = "";
    /**
     * 阿里云返回json数据类型
     */
    public String result_msg = "";

    @Override
    public void setdata(JSONObject data) {
        try {
            result_data = data.getString("data");
            result_msg = data.getString("msg");
            result_status = data.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

