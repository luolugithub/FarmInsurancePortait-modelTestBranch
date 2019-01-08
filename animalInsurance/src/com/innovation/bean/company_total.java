package com.innovation.bean;

public class company_total {
    private int companyId;
    private String companyName;
    public company_total(int companyId, String companyName) {
        super();
        this.companyId = companyId;
        this.companyName = companyName;
    }
    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

}

