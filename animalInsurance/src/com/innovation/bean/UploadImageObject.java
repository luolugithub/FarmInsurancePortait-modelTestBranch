package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class UploadImageObject extends HttpRespObject {


    //用户ID
    public String upload_imagePath = "";
    public String upload_imageShowUrl="";

    @Override
    public void setdata(JSONObject data) {
        if(data == null)
            return;
        upload_imagePath = data.optString("imagePath", "");
        upload_imageShowUrl = data.optString("imageShowUrl", "");
    }
}