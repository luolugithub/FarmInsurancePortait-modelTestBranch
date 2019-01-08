package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class UserRegisterBean extends HttpRespObject {

    //身份证号
    public String card = "";
    //短信验证码
    public String code = "";
    //短信验证码有效期
    public String codedate = "";
    //创建时间
    public String createtime = "";
    //邮箱
    public String email = "";
    //用户名称
    public String fullname = "";
    //手机号
    public String mobile = "";
    //密码
    public String password = "";
    //状态
    public int status = 0;
    //token
    public String token = "";
    //token有效期
    public String tokendate = "";
    //uid
    public int uid = 0;
    //更新时间
    public String updatetime = "";
    //
    public String username = "";

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        card = data.optString("card", "");
        code = data.optString("code", "");
        codedate = data.optString("codedate", "");
        createtime = data.optString("createtime", "");
        email = data.optString("email", "");
        fullname = data.optString("fullname", "");

        mobile = data.optString("mobile", "");
        password = data.optString("password", "");
        status = data.optInt("status", 0);

        token = data.optString("token", "");
        tokendate = data.optString("tokendate", "");
        uid = data.optInt("uid", 0);

        updatetime = data.optString("updatetime", "");
        username = data.optString("username", "");
        //upload_pigInfo = data.optString("libPigInfo", "");
//        upload_pigInfo = "";
    }
}
