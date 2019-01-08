package com.innovation.bean;

public class GsonBean {
    /**
     * data : {"lipei":{"amount":1,"appremark":"","bankNo":"123","baodanNo":"1049","biduiStatus":"","cardNo":"50023019920325099x","cardType":1,"collectAmount":1,"createtime":"2018-08-08 12:32:59","id":1036,"lipeiNo":"20180808123258","lipeiStatus":0,"lipeiStatusName":"","name":"测试","reason":"火灾","status":1,"uid":128,"updatetime":""},"pig":{"address":"北京市朝阳区顺白路上1号靠近私享家大厦","baodanNo":"1049","baodanNoReal":"1049","createtime":"2018-08-08 12:33:52","erji":"","heyingPic":"","id":2408,"juanNo":"001","libId":8428,"lipeiNo":"20180808123258","person":"测试","pid":"8593c2cfda354f5a9e96f32de37b89cc","pidseven":"","pigInfo":"{\"lib_id\":8428,\"lib_envinfo\":{\"imei\":\"97c2744865a8aae6c871ea877aea05a6\",\"gps\":\"北京市朝阳区顺白路上1号北京红厂设计创意产业基地\"},\"lib_createtime\":\"2018年08月08日12时33分\"}","pigNo":"002","pigType":1,"sDegree":"","sanji":"","seqNo":"","sheNo":"001","shiyangMethod":"","siji":"","status":1,"toubaoCost":"","toubaoType":"","type":2,"updatetime":"","wuji":"","xuling":"","yiji":""}}
     * msg : 信息保存成功
     * status : 1
     */

    private DataBean data;
    private String msg;
    private int status;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
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

    public static class DataBean {
        /**
         * lipei : {"amount":1,"appremark":"","bankNo":"123","baodanNo":"1049","biduiStatus":"","cardNo":"50023019920325099x","cardType":1,"collectAmount":1,"createtime":"2018-08-08 12:32:59","id":1036,"lipeiNo":"20180808123258","lipeiStatus":0,"lipeiStatusName":"","name":"测试","reason":"火灾","status":1,"uid":128,"updatetime":""}
         * pig : {"address":"北京市朝阳区顺白路上1号靠近私享家大厦","baodanNo":"1049","baodanNoReal":"1049","createtime":"2018-08-08 12:33:52","erji":"","heyingPic":"","id":2408,"juanNo":"001","libId":8428,"lipeiNo":"20180808123258","person":"测试","pid":"8593c2cfda354f5a9e96f32de37b89cc","pidseven":"","pigInfo":"{\"lib_id\":8428,\"lib_envinfo\":{\"imei\":\"97c2744865a8aae6c871ea877aea05a6\",\"gps\":\"北京市朝阳区顺白路上1号北京红厂设计创意产业基地\"},\"lib_createtime\":\"2018年08月08日12时33分\"}","pigNo":"002","pigType":1,"sDegree":"","sanji":"","seqNo":"","sheNo":"001","shiyangMethod":"","siji":"","status":1,"toubaoCost":"","toubaoType":"","type":2,"updatetime":"","wuji":"","xuling":"","yiji":""}
         */

        private LipeiBean lipei;
        private PigBean pig;

        public LipeiBean getLipei() {
            return lipei;
        }

        public void setLipei(LipeiBean lipei) {
            this.lipei = lipei;
        }

        public PigBean getPig() {
            return pig;
        }

        public void setPig(PigBean pig) {
            this.pig = pig;
        }

        public static class LipeiBean {
            /**
             * amount : 1
             * appremark :
             * bankNo : 123
             * baodanNo : 1049
             * biduiStatus :
             * cardNo : 50023019920325099x
             * cardType : 1
             * collectAmount : 1
             * createtime : 2018-08-08 12:32:59
             * id : 1036
             * lipeiNo : 20180808123258
             * lipeiStatus : 0
             * lipeiStatusName :
             * name : 测试
             * reason : 火灾
             * status : 1
             * uid : 128
             * updatetime :
             */

            private int amount;
            private String appremark;
            private String bankNo;
            private String baodanNo;
            private String biduiStatus;
            private String cardNo;
            private int cardType;
            private int collectAmount;
            private String createtime;
            private int id;
            private String lipeiNo;
            private int lipeiStatus;
            private String lipeiStatusName;
            private String name;
            private String reason;
            private int status;
            private int uid;
            private String updatetime;

            public int getAmount() {
                return amount;
            }

            public void setAmount(int amount) {
                this.amount = amount;
            }

            public String getAppremark() {
                return appremark;
            }

            public void setAppremark(String appremark) {
                this.appremark = appremark;
            }

            public String getBankNo() {
                return bankNo;
            }

            public void setBankNo(String bankNo) {
                this.bankNo = bankNo;
            }

            public String getBaodanNo() {
                return baodanNo;
            }

            public void setBaodanNo(String baodanNo) {
                this.baodanNo = baodanNo;
            }

            public String getBiduiStatus() {
                return biduiStatus;
            }

            public void setBiduiStatus(String biduiStatus) {
                this.biduiStatus = biduiStatus;
            }

            public String getCardNo() {
                return cardNo;
            }

            public void setCardNo(String cardNo) {
                this.cardNo = cardNo;
            }

            public int getCardType() {
                return cardType;
            }

            public void setCardType(int cardType) {
                this.cardType = cardType;
            }

            public int getCollectAmount() {
                return collectAmount;
            }

            public void setCollectAmount(int collectAmount) {
                this.collectAmount = collectAmount;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getLipeiNo() {
                return lipeiNo;
            }

            public void setLipeiNo(String lipeiNo) {
                this.lipeiNo = lipeiNo;
            }

            public int getLipeiStatus() {
                return lipeiStatus;
            }

            public void setLipeiStatus(int lipeiStatus) {
                this.lipeiStatus = lipeiStatus;
            }

            public String getLipeiStatusName() {
                return lipeiStatusName;
            }

            public void setLipeiStatusName(String lipeiStatusName) {
                this.lipeiStatusName = lipeiStatusName;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public int getUid() {
                return uid;
            }

            public void setUid(int uid) {
                this.uid = uid;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }
        }

        public static class PigBean {
            /**
             * address : 北京市朝阳区顺白路上1号靠近私享家大厦
             * baodanNo : 1049
             * baodanNoReal : 1049
             * createtime : 2018-08-08 12:33:52
             * erji :
             * heyingPic :
             * id : 2408
             * juanNo : 001
             * libId : 8428
             * lipeiNo : 20180808123258
             * person : 测试
             * pid : 8593c2cfda354f5a9e96f32de37b89cc
             * pidseven :
             * pigInfo : {"lib_id":8428,"lib_envinfo":{"imei":"97c2744865a8aae6c871ea877aea05a6","gps":"北京市朝阳区顺白路上1号北京红厂设计创意产业基地"},"lib_createtime":"2018年08月08日12时33分"}
             * pigNo : 002
             * pigType : 1
             * sDegree :
             * sanji :
             * seqNo :
             * sheNo : 001
             * shiyangMethod :
             * siji :
             * status : 1
             * toubaoCost :
             * toubaoType :
             * type : 2
             * updatetime :
             * wuji :
             * xuling :
             * yiji :
             */

            private String address;
            private String baodanNo;
            private String baodanNoReal;
            private String createtime;
            private String erji;
            private String heyingPic;
            private int id;
            private String juanNo;
            private int libId;
            private String lipeiNo;
            private String person;
            private String pid;
            private String pidseven;
            private String pigInfo;
            private String pigNo;
            private int pigType;
            private String sDegree;
            private String sanji;
            private String seqNo;
            private String sheNo;
            private String shiyangMethod;
            private String siji;
            private int status;
            private String toubaoCost;
            private String toubaoType;
            private int type;
            private String updatetime;
            private String wuji;
            private String xuling;
            private String yiji;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getBaodanNo() {
                return baodanNo;
            }

            public void setBaodanNo(String baodanNo) {
                this.baodanNo = baodanNo;
            }

            public String getBaodanNoReal() {
                return baodanNoReal;
            }

            public void setBaodanNoReal(String baodanNoReal) {
                this.baodanNoReal = baodanNoReal;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getErji() {
                return erji;
            }

            public void setErji(String erji) {
                this.erji = erji;
            }

            public String getHeyingPic() {
                return heyingPic;
            }

            public void setHeyingPic(String heyingPic) {
                this.heyingPic = heyingPic;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getJuanNo() {
                return juanNo;
            }

            public void setJuanNo(String juanNo) {
                this.juanNo = juanNo;
            }

            public int getLibId() {
                return libId;
            }

            public void setLibId(int libId) {
                this.libId = libId;
            }

            public String getLipeiNo() {
                return lipeiNo;
            }

            public void setLipeiNo(String lipeiNo) {
                this.lipeiNo = lipeiNo;
            }

            public String getPerson() {
                return person;
            }

            public void setPerson(String person) {
                this.person = person;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getPidseven() {
                return pidseven;
            }

            public void setPidseven(String pidseven) {
                this.pidseven = pidseven;
            }

            public String getPigInfo() {
                return pigInfo;
            }

            public void setPigInfo(String pigInfo) {
                this.pigInfo = pigInfo;
            }

            public String getPigNo() {
                return pigNo;
            }

            public void setPigNo(String pigNo) {
                this.pigNo = pigNo;
            }

            public int getPigType() {
                return pigType;
            }

            public void setPigType(int pigType) {
                this.pigType = pigType;
            }

            public String getSDegree() {
                return sDegree;
            }

            public void setSDegree(String sDegree) {
                this.sDegree = sDegree;
            }

            public String getSanji() {
                return sanji;
            }

            public void setSanji(String sanji) {
                this.sanji = sanji;
            }

            public String getSeqNo() {
                return seqNo;
            }

            public void setSeqNo(String seqNo) {
                this.seqNo = seqNo;
            }

            public String getSheNo() {
                return sheNo;
            }

            public void setSheNo(String sheNo) {
                this.sheNo = sheNo;
            }

            public String getShiyangMethod() {
                return shiyangMethod;
            }

            public void setShiyangMethod(String shiyangMethod) {
                this.shiyangMethod = shiyangMethod;
            }

            public String getSiji() {
                return siji;
            }

            public void setSiji(String siji) {
                this.siji = siji;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getToubaoCost() {
                return toubaoCost;
            }

            public void setToubaoCost(String toubaoCost) {
                this.toubaoCost = toubaoCost;
            }

            public String getToubaoType() {
                return toubaoType;
            }

            public void setToubaoType(String toubaoType) {
                this.toubaoType = toubaoType;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            public String getWuji() {
                return wuji;
            }

            public void setWuji(String wuji) {
                this.wuji = wuji;
            }

            public String getXuling() {
                return xuling;
            }

            public void setXuling(String xuling) {
                this.xuling = xuling;
            }

            public String getYiji() {
                return yiji;
            }

            public void setYiji(String yiji) {
                this.yiji = yiji;
            }
        }
    }
}
