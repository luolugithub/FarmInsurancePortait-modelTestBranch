package com.innovation.bean;

/**
 * Created by Luolu on 2018/9/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PayInfoCheckResultBean {

    /**
     * data : ......
     * msg : 校验成功
     * status : 1
     */

    private String data;
    private String msg;
    private int status;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
