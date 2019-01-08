package com.innovation.bean;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;


/**
 * Created by Luolu on 2018/8/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class OfflineBuildResultObject extends HttpRespObject {

    //保单号
    public int build_result_type = 0;
    //用户ID
    public String build_result_similarity = "";

    public String build_result_result;

    public String build_result_pid="";

    public String build_result_libIds="";
    public int buildStatus=0;
    public int buildSum=0;
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
        buildStatus = data.optInt("buildStatus", 0);
        buildSum = data.optInt("buildSum", 0);
    }
}
