package com.innovation.bean;

import java.util.List;

/**
 * Created by Luolu on 2018/10/8.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class ToubaoUploadBean {

    /**
     * data : {"libId":21610,"resultPic":[{"detail":" 采集时间：2018-09-25 14:50:28 采集人姓名：采集员06 耳标号：08","libId":"21610","pic":"http://47.92.167.61:3389/20180925/158/lib/pic/Data/110/21610/Time-2018_09_25_025016/Angle-03/20180925025001500.jpg"}],"resultFlag":1,"resultMsg":" 系统找到一头相似牲畜，信息如下，如为重复录入请放弃本次操作。"}
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
         * libId : 21610
         * resultPic : [{"detail":" 采集时间：2018-09-25 14:50:28 采集人姓名：采集员06 耳标号：08","libId":"21610","pic":"http://47.92.167.61:3389/20180925/158/lib/pic/Data/110/21610/Time-2018_09_25_025016/Angle-03/20180925025001500.jpg"}]
         * resultFlag : 1
         * resultMsg :  系统找到一头相似牲畜，信息如下，如为重复录入请放弃本次操作。
         */

        private int libId;
        private int resultFlag;
        private String resultMsg;
        private List<ResultPicOffLineBaodanBean> resultPic;

        public int getLibId() {
            return libId;
        }

        public void setLibId(int libId) {
            this.libId = libId;
        }

        public int getResultFlag() {
            return resultFlag;
        }

        public void setResultFlag(int resultFlag) {
            this.resultFlag = resultFlag;
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
             * detail :  采集时间：2018-09-25 14:50:28 采集人姓名：采集员06 耳标号：08
             * libId : 21610
             * pic : http://47.92.167.61:3389/20180925/158/lib/pic/Data/110/21610/Time-2018_09_25_025016/Angle-03/20180925025001500.jpg
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
