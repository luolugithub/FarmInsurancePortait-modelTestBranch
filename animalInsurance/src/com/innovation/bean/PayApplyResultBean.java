package com.innovation.bean;

/**
 * Created by Luolu on 2018/9/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PayApplyResultBean {

    /**
     * data : {"lipeiId":123456}
     * msg : 理赔申请成功
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
         * lipeiId : 123456
         */

        private int lipeiId;

        public int getLipeiId() {
            return lipeiId;
        }

        public void setLipeiId(int lipeiId) {
            this.lipeiId = lipeiId;
        }
    }
}
