package com.innovation.bean;

/**
 * Created by Luolu on 2018/8/17.
 * InnovationAI
 * luolu@innovationai.cn
 */

    /**
     *
     {
     "data":{
     "images":{

     },
     "buildSum":10,
     "type":1,
     "result":"",
     "buildStatus":1,
     "similarity":"",
     "createtime":"2018-08-17 09:39:33",
     "pid":"c69668c01f66446a8e7f726a792b3b43",
     "libIds":"19373"
     },
     "msg":"投保建库查询成功",
     "status":1
     }
     */
public class InsuredResultGsonBean {

        /**
         * data : {"images":{},"buildSum":10,"type":1,"result":"","buildStatus":1,"similarity":"","createtime":"2018-08-17 09:39:33","pid":"c69668c01f66446a8e7f726a792b3b43","libIds":"19373"}
         * msg : 投保建库查询成功
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
             * images : {}
             * buildSum : 10
             * type : 1
             * result :
             * buildStatus : 1
             * similarity :
             * createtime : 2018-08-17 09:39:33
             * pid : c69668c01f66446a8e7f726a792b3b43
             * libIds : 19373
             */

            private ImagesOffLineBaodanBean images;
            private int buildSum;
            private int type;
            private String result;
            private int buildStatus;
            private String similarity;
            private String createtime;
            private String pid;
            private String libIds;

            public ImagesOffLineBaodanBean getImages() {
                return images;
            }

            public void setImages(ImagesOffLineBaodanBean images) {
                this.images = images;
            }

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

            public String getResult() {
                return result;
            }

            public void setResult(String result) {
                this.result = result;
            }

            public int getBuildStatus() {
                return buildStatus;
            }

            public void setBuildStatus(int buildStatus) {
                this.buildStatus = buildStatus;
            }

            public String getSimilarity() {
                return similarity;
            }

            public void setSimilarity(String similarity) {
                this.similarity = similarity;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getLibIds() {
                return libIds;
            }

            public void setLibIds(String libIds) {
                this.libIds = libIds;
            }

            public static class ImagesOffLineBaodanBean {
            }
        }
    }
