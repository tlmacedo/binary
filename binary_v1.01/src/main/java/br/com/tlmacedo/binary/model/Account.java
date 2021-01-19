package br.com.tlmacedo.binary.model;

public class Account {

    private String  currency;
    private boolean is_disabled;
    private boolean is_virtual;
    private String landing_company_name;
    private String loginid;

    public Account() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isIs_disabled() {
        return is_disabled;
    }

    public void setIs_disabled(boolean is_disabled) {
        this.is_disabled = is_disabled;
    }

    public boolean isIs_virtual() {
        return is_virtual;
    }

    public void setIs_virtual(boolean is_virtual) {
        this.is_virtual = is_virtual;
    }

    public String getLanding_company_name() {
        return landing_company_name;
    }

    public void setLanding_company_name(String landing_company_name) {
        this.landing_company_name = landing_company_name;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    @Override
    public String toString() {
        return "Account{" +
                "currency='" + currency + '\'' +
                ", is_disabled=" + is_disabled +
                ", is_virtual=" + is_virtual +
                ", landing_company_name='" + landing_company_name + '\'' +
                ", loginid='" + loginid + '\'' +
                '}';
    }
}
