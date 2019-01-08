package com.innovation.login;

/**
 * Created by biao on 23/11/2017.
 */

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {"mobile":"18601120302","sn":"fb00c9e2cb164729d3edf040de02dad7","xx":"asdfasdf"}
 * response:{"status":0,"msg":"操作成功!",
 * "data":{"code":"853131","mobile":"18601120302","aliyun_response":
 * {"Message":"OK","RequestId":"223692F9-CD14-4F17-BDC0-DB430216A3CD","BizId":"631422911396095911^0","Code":"OK"}}}
 */
public class LoginResp extends RespObject {
    /**
     * 手机验证码
     */
    public String code = "";
    /**
     * 手机号
     */
    public String moblienum = "";
    /**
     * 阿里云返回json数据类型
     */
    public JSONObject aliyun_response = null;

    @Override
    public void setdata(JSONObject data) {
        try {
            code = data.optString("code");
            aliyun_response = data.getJSONObject("aliyun_response");
            moblienum = data.getString("mobile");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}