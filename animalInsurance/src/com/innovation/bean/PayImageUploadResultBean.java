package com.innovation.bean;

/**
 * Created by Luolu on 2018/9/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PayImageUploadResultBean {

    /**
     * data : {"libId ":123456,"picQuality":1}
     * msg : 新增保单成功
     * status : 1
     */

    private DataOffLineBaodanBean data;
    private String msg;
    private int status;

    public DataOffLineBaodanBean getData() {
        return data;
    }

    public void setData(DataOffLineBaodanBean data) {
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

    public static class DataOffLineBaodanBean {
        /**
         * libId  : 123456
         * picQuality : 1
         */

        private int libId;
        private int picQuality;

        public int getLibId() {
            return libId;
        }

        public void setLibId(int libId) {
            this.libId = libId;
        }

        public int getPicQuality() {
            return picQuality;
        }

        public void setPicQuality(int picQuality) {
            this.picQuality = picQuality;
        }
    }
}
