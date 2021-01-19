package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class Account implements Serializable {
    public static final long serialVersionUID = 1L;

    String currency;
    Integer excluded_until;
    Integer is_disabled;
    Integer is_virtual;
    String landing_company_name;
    String loginid;

    public Account() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getExcluded_until() {
        return excluded_until;
    }

    public void setExcluded_until(Integer excluded_until) {
        this.excluded_until = excluded_until;
    }

    public Integer getIs_disabled() {
        return is_disabled;
    }

    public void setIs_disabled(Integer is_disabled) {
        this.is_disabled = is_disabled;
    }

    public Integer getIs_virtual() {
        return is_virtual;
    }

    public void setIs_virtual(Integer is_virtual) {
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
                ", excluded_until=" + excluded_until +
                ", is_disabled=" + is_disabled +
                ", is_virtual=" + is_virtual +
                ", landing_company_name='" + landing_company_name + '\'' +
                ", loginid='" + loginid + '\'' +
                '}';
    }
}
