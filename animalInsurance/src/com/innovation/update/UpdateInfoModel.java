package com.innovation.update;

import com.innovation.utils.HttpRespObject;

import org.json.JSONObject;

public class UpdateInfoModel extends HttpRespObject {
    //app名字
    private String appname;
    //服务器版本
    private String serverVersion;
    //服务器标志
    private String serverFlag;
    //强制升级
    private String lastForce;
    //app最新版本地址
    private String updateurl;
    //升级信息
    private String upgradeinfo;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getServerFlag() {
        return serverFlag;
    }

    public void setServerFlag(String serverFlag) {
        this.serverFlag = serverFlag;
    }

    public String getLastForce() {
        return lastForce;
    }

    public void setLastForce(String lastForce) {
        this.lastForce = lastForce;
    }

    public String getUpdateurl() {
        return updateurl;
    }

    public void setUpdateurl(String updateurl) {
        this.updateurl = updateurl;
    }

    public String getUpgradeinfo() {
        return upgradeinfo;
    }

    public void setUpgradeinfo(String upgradeinfo) {
        this.upgradeinfo = upgradeinfo;
    }

    @Override
    public void setdata(JSONObject data) {
        if (data == null) {
            return;
        }
        appname = data.optString("appname");
        serverVersion = data.optString("serverVersion", "");
        serverFlag = data.optString("serverFlag", "");
        lastForce = data.optString("lastForce", "");
        updateurl = data.optString("updateurl", "");
        upgradeinfo = data.optString("upgradeinfo","");
    }
}
