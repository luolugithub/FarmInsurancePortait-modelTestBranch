package com.innovation.bean;

import com.innovation.utils.HttpRespObject;
import com.innovation.utils.NewHttpRespObject;

import org.json.JSONObject;

public class NewBuildObject extends NewHttpRespObject {

    //data
    public String build_data = "";


    @Override
    public void setdata(String data) {
        if(data == null)
            return;
        build_data = data;
    }

}