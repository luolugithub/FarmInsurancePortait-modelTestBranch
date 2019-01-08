package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONArray;
import org.json.JSONObject;

public class NewBuildResultObject extends HttpRespObject {

    //保单号
    public int build_result_type = 0;
    //用户ID
    public String build_result_similarity = "";

    public String build_result_result;

    public String build_result_pid="";
    public String buildVertificaton="";

    public String build_result_libIds="";
    public int buildStatus = -110;
    public int buildSum = -110;
    public JSONObject build_result_images;



    @Override
    public void setdata(JSONObject data) {
        if (data == null)
            return;
        build_result_images = data.optJSONObject("images");
        build_result_type = data.optInt("type", 0);
        build_result_similarity = data.optString("similarity", "");
        build_result_result = data.optString("result","");
        build_result_pid = data.optString("pid", "");
        build_result_libIds = data.optString("libIds", "");
        buildVertificaton = data.optString("verification", "");
        buildStatus = data.optInt("buildStatus", 0);
        buildSum = data.optInt("buildSum", 0);
    }

    @Override
    public String toString() {
        return "NewBuildResultObject{" +
                "build_result_type=" + build_result_type +
                ", build_result_similarity='" + build_result_similarity + '\'' +
                ", build_result_result='" + build_result_result + '\'' +
                ", build_result_pid='" + build_result_pid + '\'' +
                ", buildVertificaton='" + buildVertificaton + '\'' +
                ", build_result_libIds='" + build_result_libIds + '\'' +
                ", buildStatus=" + buildStatus +
                ", buildSum=" + buildSum +
                ", build_result_images=" + build_result_images +
                '}';
    }
}