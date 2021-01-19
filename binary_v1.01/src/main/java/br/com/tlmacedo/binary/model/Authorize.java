package br.com.tlmacedo.binary.model;

import java.util.List;

public class Authorize {

    private List<Account> accountList;
    private Number balance;
    private String currency;
    private String email;
    private String fullname;
    private Integer is_virtual;
    private String landing_company_fullname;
    private String landing_company_name;
    private Local_currencies local_currencies;
    private String loginid;
    private List<String> scopes;
    private List<String> upgradeable_landing_companies;
    private Integer user_id;

    public Authorize() {
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public Number getBalance() {
        return balance;
    }

    public void setBalance(Number balance) {
        this.balance = balance;
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
                "accountList=" + accountList +
                ", balance=" + balance +
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