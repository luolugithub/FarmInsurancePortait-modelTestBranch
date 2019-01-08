package com.innovation.utils;

import org.json.JSONObject;

/**
 * Created  on 2018/4/10.
 */

public abstract class HttpRespObject {

    /**
     * 操作成功
     */
    public static final int STATUS_OK = 1;
    /**
     * 失败,data中给出失败提示
     */
    public static final int STATUS_0 = 0;

    /**
     * 非法访问
     */
    public static final int STATUS_01 = -1;

    /**
     * 参数非法
     */
    public static final int STATUS_02 = -2;

    /**
     * 查询结果为空
     */
    public static final int STATUS_03 = -3;

    /**
     * 接口异常
     */
    public static final int STATUS_04 = -4;

    /**
     * 会话超时
     */
    public static final int STATUS_05 = -5;


    public int status = -4444;
    public String msg = "";
    public JSONObject data = null;

    public abstract void setdata(JSONObject data);
}
