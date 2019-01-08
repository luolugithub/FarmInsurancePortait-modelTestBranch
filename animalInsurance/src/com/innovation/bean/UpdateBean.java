package com.innovation.bean;

import com.innovation.utils.HttpRespObject;
import com.innovation.utils.NewHttpRespObject;

public class UpdateBean extends NewHttpRespObject {
    //data
    public String update_data = "";

    @Override
    public void setdata(String data) {
        if(data == null)
            return;
        update_data = data;
    }
}
