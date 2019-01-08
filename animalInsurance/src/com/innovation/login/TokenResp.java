package com.innovation.login;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by biao on 23/11/2017.
 * response:{"status":0,"msg":"操作成功!",
 * "data":{"uid":13,"username":"","fullname":"17694911624","mobile":"17694911624","email":"17694911624@baidu.com","code":"379537","codedate":1511445668,"status":1,"createtime":"2017-11-21 15:00:49","updatetime":"2017-11-23 13:59:08","token":"C682312E19CF8F0A0FB47FB627689B02","tokendate":1512050371}"
 */
public class TokenResp extends RespObject{

    public int uid = 0;
    public String user_username = null;
    public String user_fullname = null;
    public String user_mobile = null;
    public String user_email = null;
    public String codedate = null;
    public String createtime = null;

    public int deptid = 0;

    @Override
    public void setdata(JSONObject data) {
        try {
            uid = data.getInt("uid");
            user_email = data.getString("email");
            user_fullname = data.getString("fullname");
            user_mobile = data.getString("mobile");
            user_username = data.getString("username");
            codedate = data.getString("codedate");
            createtime = data.getString("createtime");
            user_status = data.getInt("status");
            token = data.getString("token");
            tokendate = data.getInt("tokendate");
            deptid = data.getInt("deptid");
//            //存储用户信息、token
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
