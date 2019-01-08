package com.innovation.bean;

/**
 * Created by Luolu on 2018/10/8.
 * InnovationAI
 * luolu@innovationai.cn
 */
public class InsureAddAnimalBean {

    /**
     * data : {"animalId":6939}
     * msg : 投保牲畜录入处理成功！
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
         * animalId : 6939
         */

        private int animalId;

        public int getAnimalId() {
            return animalId;
        }

        public void setAnimalId(int animalId) {
            this.animalId = animalId;
        }
    }
}
