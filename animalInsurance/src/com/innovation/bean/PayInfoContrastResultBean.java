package com.innovation.bean;

import java.util.List;

/**
 * Created by Luolu on 2018/9/18.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class PayInfoContrastResultBean {

    /**
     * data : {"lipeiId":2566,"resultPic":[{"detail":" 保单号：1024 投保采集时间：2018-10-09 19:20:01 采集人姓名：测试 耳标号：无 标的识别码：1","libId":"21775","pic":"http://47.92.167.61:3389/20181009/128/lib/pic/Data/201809302144171648/21775/Time-2018_10_09_071943/Angle-02/20181009071735195.jpg"},{"detail":" 保单号：1024 投保采集时间：2018-10-11 19:45:33 采集人姓名：测试 耳标号：无 标的识别码：无","libId":"21800","pic":"http://47.92.167.61:3389/20181011/128/lib/pic/Data/201809302144171648/21800/Time-2018_10_11_074513/Angle-01/20181011074425314.jpg"},{"detail":" 保单号：1024 投保采集时间：2018-10-09 10:21:53 采集人姓名：来总 耳标号：无 标的识别码：223","libId":"21756","pic":"http://47.92.167.61:3389/20181009/187/lib/pic/Data/110/21756/Time-2018_10_09_102128/Angle-02/20181009102111121.jpg"}],"resultStatus ":1,"resultMsg":"在投保库无唯一相似对象，系统推荐以下近似对象："}
     */

    private DataOffLineBaodanBean data;

    public DataOffLineBaodanBean getData() {
        return data;
    }

    public void setData(DataOffLineBaodanBean data) {
        this.data = data;
    }

    public static class DataOffLineBaodanBean {
        /**
         * lipeiId : 2566
         * resultPic : [{"detail":" 保单号：1024 投保采集时间：2018-10-09 19:20:01 采集人姓名：测试 耳标号：无 标的识别码：1","libId":"21775","pic":"http://47.92.167.61:3389/20181009/128/lib/pic/Data/201809302144171648/21775/Time-2018_10_09_071943/Angle-02/20181009071735195.jpg"},{"detail":" 保单号：1024 投保采集时间：2018-10-11 19:45:33 采集人姓名：测试 耳标号：无 标的识别码：无","libId":"21800","pic":"http://47.92.167.61:3389/20181011/128/lib/pic/Data/201809302144171648/21800/Time-2018_10_11_074513/Angle-01/20181011074425314.jpg"},{"detail":" 保单号：1024 投保采集时间：2018-10-09 10:21:53 采集人姓名：来总 耳标号：无 标的识别码：223","libId":"21756","pic":"http://47.92.167.61:3389/20181009/187/lib/pic/Data/110/21756/Time-2018_10_09_102128/Angle-02/20181009102111121.jpg"}]
         * resultStatus  : 1
         * resultMsg : 在投保库无唯一相似对象，系统推荐以下近似对象：
         */

        private int lipeiId;
        private int resultStatus;
        private String resultMsg;
        private List<ResultPicOffLineBaodanBean> resultPic;

        public int getLipeiId() {
            return lipeiId;
        }

        public void setLipeiId(int lipeiId) {
            this.lipeiId = lipeiId;
        }

        public int getResultStatus() {
            return resultStatus;
        }

        public void setResultStatus(int resultStatus) {
            this.resultStatus = resultStatus;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }

        public List<ResultPicOffLineBaodanBean> getResultPic() {
            return resultPic;
        }

        public void setResultPic(List<ResultPicOffLineBaodanBean> resultPic) {
            this.resultPic = resultPic;
        }

        public static class ResultPicOffLineBaodanBean {
            /**
             * detail :  保单号：1024 投保采集时间：2018-10-09 19:20:01 采集人姓名：测试 耳标号：无 标的识别码：1
             * libId : 21775
             * pic : http://47.92.167.61:3389/20181009/128/lib/pic/Data/201809302144171648/21775/Time-2018_10_09_071943/Angle-02/20181009071735195.jpg
             */

            private String detail;
            private String libId;
            private String pic;

            public String getDetail() {
                return detail;
            }

            public void setDetail(String detail) {
                this.detail = detail;
            }

            public String getLibId() {
                return libId;
            }

            public void setLibId(String libId) {
                this.libId = libId;
            }

            public String getPic() {
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }
        }
    }
}
