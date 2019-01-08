package com.innovation.bean;

/**
 * Created by Luolu on 2018/9/20.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class ResultBean {
    /**
     * data : {"libId ":123456,"picQuality":1}
     * msg : 新增保单成功
     * status : 1
     */

    private Object data;
    private String msg;
    private int status;

    @Override
    public String toString() {
        return "ResultBean{" +
                "data=" + data +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                '}';
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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
