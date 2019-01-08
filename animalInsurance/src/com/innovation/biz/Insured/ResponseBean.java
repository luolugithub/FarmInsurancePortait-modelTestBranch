package com.innovation.biz.Insured;

public class ResponseBean {

    /**
     * data : {"buildSum":3,"type":1,"buildStatus":1,"pid":"1750a08521a9422e938ac41def9a9dc7"}
     * msg : 上传成功
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
         * buildSum : 3
         * type : 1
         * buildStatus : 1
         * pid : 1750a08521a9422e938ac41def9a9dc7
         */

        private int buildSum;
        private int type;
        private int buildStatus;
        private String pid;

        public int getBuildSum() {
            return buildSum;
        }

        public void setBuildSum(int buildSum) {
            this.buildSum = buildSum;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getBuildStatus() {
            return buildStatus;
        }

        public void setBuildStatus(int buildStatus) {
            this.buildStatus = buildStatus;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
    }
}
