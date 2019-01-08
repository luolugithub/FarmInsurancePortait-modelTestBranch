package com.innovation.login;

import org.json.JSONObject;

/**
 * Created by biao on 23/11/2017.
 */

public abstract class RespObject {
    /**
     * 网络错误
     */
    public static final int STATUS_NET_ERR = -1;
    /**
     * 操作成功
     */
    public static final int STATUS_0 = 0;
    /**
     * 参数错误
     */
    public static final int STATUS_101 = 101;
    /**
     * 未找到数据
     */
    public static final int STATUS_102 = 102;
    /**
     * 数据保存失败
     */
    public static final int STATUS_103 = 103;
    /**
     * 未授权访问
     */
    public static final int STATUS_104 = 104;
    /**
     * 返回数据为空
     */
    public static final int STATUS_105 = 105;
    /**
     * 操作不成功
     */
    public static final int STATUS_106 = 106;
    /**
     * 用户状态正常 值为1
     */
    //  值需要改为0/1?
    public static final int USER_STATUS_1 = 1;
    /**
     * 用户状态异常 值为-1
     */
    public static final int USER_STATUS_2 = -1;
    /**
     * 用户操作类型 创建库
     */
    public static final int OP_TYPE_CREATE = 1;
    /**
     * 用户操作类型 校验库
     */
    public static final int OP_TYPE_CHECK = 2;
    /**
     * 上传资源类型为图片
     */
    public static final int SRC_IMG = 1;
    /**
     * 上传资源类型为视频
     */
    public static final int SRC_VIDEO = 2;

    public int status = STATUS_0;
    public String msg = "";
    public JSONObject data = null;

    /**
     * 短信验证码有效期
     */
    public int codedate = -1;
    public int user_status = USER_STATUS_1;
    public String user_createtime = null;
    public String user_updatetime = null;
    public String token = null;
    /**
     * 本次获取的token的有效期
     */
    public int tokendate = 0;

    //文件上传
    public int user_userid = -1;
    public int op_type = OP_TYPE_CREATE;
    /**
     * 库id,补传视频时必须传入
     */
    public int lib_id = -1;
    /**
     * 手机环境参数信息json串
     */
    public JSONObject lib_envinfo = null;
    /**
     * 图片类型
     */
    public int libd_source = SRC_IMG;

    public abstract void setdata(JSONObject data);
}