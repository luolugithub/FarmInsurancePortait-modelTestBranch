package com.innovation.bean;

public class Company {
    private int id;
    private String fullname;
    private int pid;

    public int getCompanyId() {
        return id;
    }
    public void setCompanyId(int companyId) {
        this.id = companyId;
    }
    public String getCompanyName() {
        return fullname;
    }
    public void setCompanyName(String companyName) {
        this.fullname = companyName;
    }

    public int getCompanyPid() {
        return pid;
    }
    public void setCompanyPid(int companyPid) {
        this.pid = companyPid;
    }
}
