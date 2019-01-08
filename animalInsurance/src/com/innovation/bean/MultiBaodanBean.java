package com.innovation.bean;

import com.innovation.utils.NewHttpRespObject;

public class MultiBaodanBean extends NewHttpRespObject {

    //data
    public String baodan_data = "";

    @Override
    public void setdata(String data) {
        if(data == null) {
            return;
        }
        baodan_data = data;
    }
}
