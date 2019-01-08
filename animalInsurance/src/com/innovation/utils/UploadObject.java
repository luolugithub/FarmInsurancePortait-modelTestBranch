package com.innovation.utils;

import org.json.JSONObject;
/**
 * Author by luolu, Date on 2018/9/27.
 * COMPANY：InnovationAI
 */

public class UploadObject extends HttpRespObject {

    public String libEnvInfo = "";
    public int libId = 0;
    public String libNum = "";
    public int libStatus = 0;
    public int libType = 0;
    public int libUserID = 0;
    public String animalInfo = "";

    @Override
    public void setdata(JSONObject data) {
        if(data == null) {
            return;
        }
        libEnvInfo = data.optString("libEnvinfo", "");
        libId = data.optInt("libId", 0);
        libNum = data.optString("libNum", "");
        libStatus = data.optInt("libStatus", 0);
        libType = data.optInt("libType", 0);
        libUserID = data.optInt("libUserid", 0);
        animalInfo = "";
    }

    @Override
    public String toString() {
        return "UploadObject{" +
                "libEnvInfo='" + libEnvInfo + '\'' +
                ", libId=" + libId +
                ", libNum='" + libNum + '\'' +
                ", libStatus=" + libStatus +
                ", libType=" + libType +
                ", libUserID=" + libUserID +
                ", animalInfo='" + animalInfo + '\'' +
                ", status=" + status +
                ", msg='" + msg + '\'' +
                '}';
    }
}
