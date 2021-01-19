package br.com.tlmacedo.binary.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Authorize implements Serializable {
    public static final long serialVersionUID = 1L;

    List<Account> account_list = new ArrayList<>();
    BigDecimal balance;
    String country;
    String currency;
    String email;
    String fullname;
    Integer is_virtual;
    String landing_company_fullname;
    String landing_company_name;
    Local_currencies local_currencies;
    String loginid;
    List<String> scopes = new ArrayList<>();
    List<String> upgradeable_landing_companies = new ArrayList<>();
    Integer user_id;

    public Authorize() {
    }

    public List<Account> getAccount_list() {
        return account_list;
    }

    public void setAccount_list(List<Account> account_list) {
        this.account_list = account_list;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getIs_virtual() {
        return is_virtual;
    }

    public void setIs_virtual(Integer is_virtual) {
        this.is_virtual = is_virtual;
    }

    public String getLanding_company_fullname() {
        return landing_company_fullname;
    }

    public void setLanding_company_fullname(String landing_company_fullname) {
        this.landing_company_fullname = landing_company_fullname;
    }

    public String getLanding_company_name() {
        return landing_company_name;
    }

    public void setLanding_company_name(String landing_company_name) {
        this.landing_company_name = landing_company_name;
    }

    @JsonIgnore
    public Local_currencies getLocal_currencies() {
        return local_currencies;
    }

    public void setLocal_currencies(Local_currencies local_currencies) {
        this.local_currencies = local_currencies;
    }

    public String getLoginid() {
        return loginid;
    }

    public void setLoginid(String loginid) {
        this.loginid = loginid;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getUpgradeable_landing_companies() {
        return upgradeable_landing_companies;
    }

    public void setUpgradeable_landing_companies(List<String> upgradeable_landing_companies) {
        this.upgradeable_landing_companies = upgradeable_landing_companies;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Authorize{" +
                "account_list=" + account_list +
                ", balance=" + balance +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", email='" + email + '\'' +
                ", fullname='" + fullname + '\'' +
                ", is_virtual=" + is_virtual +
                ", landing_company_fullname='" + landing_company_fullname + '\'' +
                ", landing_company_name='" + landing_company_name + '\'' +
                ", local_currencies=" + local_currencies +
                ", loginid='" + loginid + '\'' +
                ", scopes=" + scopes +
                ", upgradeable_landing_companies=" + upgradeable_landing_companies +
                ", user_id=" + user_id +
                '}';
    }
}
