package com.innovation.bean;

/**
 * Created by Luolu on 2018/10/26.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class QueryVideoFlagDataBean {

    /**
     * data : {"toubaoVideoFlag":"0","lipeiVideoFlag":"0"}
     * msg : 查询成功！
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
         * toubaoVideoFlag : 0
         * lipeiVideoFlag : 0
         */

        private String toubaoVideoFlag;
        private String lipeiVideoFlag;
        private String leftNum;
        private String middleNum;
        private String rightNum;

        public String getToubaoVideoFlag() {
            return toubaoVideoFlag;
        }

        public void setToubaoVideoFlag(String toubaoVideoFlag) {
            this.toubaoVideoFlag = toubaoVideoFlag;
        }

        public String getLipeiVideoFlag() {
            return lipeiVideoFlag;
        }

        public void setLipeiVideoFlag(String lipeiVideoFlag) {
            this.lipeiVideoFlag = lipeiVideoFlag;
        }

        public String getLeftNum() {
            return leftNum;
        }

        public void setLeftNum(String leftNum) {
            this.leftNum = leftNum;
        }

        public String getMiddleNum() {
            return middleNum;
        }

        public void setMiddleNum(String middleNum) {
            this.middleNum = middleNum;
        }

        public String getRightNum() {
            return rightNum;
        }

        public void setRightNum(String rightNum) {
            this.rightNum = rightNum;
        }
    }
}
