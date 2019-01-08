package com.innovation.login;

import com.innovation.utils.HttpUtils;
import com.innvocation.upload.UploadResp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by biao on 22/11/2017.
 */

public class ResponseProcessor {

    public static LoginResp mLoginResp;

    public static TokenResp mTokenResp;

    private ResponseProcessor() {

    }

    /**
     * @param resp
     */
    public static RespObject processResp(String resp, String url) {
        RespObject respObj;
        try {
            if (Utils.LOGIN_URL.equalsIgnoreCase(url)) {
                respObj = new LoginResp();
            } else if (Utils.LOGIN_GET_TOKEN_URL.equalsIgnoreCase(url)) {
                respObj = new TokenResp();
            } else if (Utils.UPLOAD_URL.equalsIgnoreCase(url)) {
                respObj = new UploadResp();
            }
            else if (HttpUtils.PIC_LOGIN_URL.equalsIgnoreCase(url)) {
                respObj = new TokenResp();
            }
            else {
                return null;
            }

            //haojie add
//            String jsonStr = resp; // 需要解析json格式的字符串
//            if(jsonStr != null && jsonStr.startsWith("\ufeff"))
//            {
//                jsonStr =  jsonStr.substring(1);
//            }
//            JSONObject json = new JSONObject(jsonStr);
            //end add

            JSONObject json = new JSONObject(resp);
            respObj.status = json.getInt("status");
            respObj.msg = json.getString("msg");
            respObj.data = json.getJSONObject("data");
            respObj.setdata(respObj.data);
            return respObj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
