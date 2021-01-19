package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.model.Enums.Error;

public class Buy {

    private Number balance_after;
    private Number buy_price;
    private Long contract_id;
    private String longcode;
    private Number payout;
    private Integer purchase_time;
    private String shortcode;
    private Integer start_time;
    private Long transaction_id;
    private Error error;

    public Buy() {
    }

    public Number getBalance_after() {
        return balance_after;
    }

    public void setBalance_after(Number balance_after) {
        this.balance_after = balance_after;
    }

    public Number getBuy_price() {
        return buy_price;
    }

    public void setBuy_price(Number buy_price) {
        this.buy_price = buy_price;
    }

    public Long getContract_id() {
        return contract_id;
    }

    public void setContract_id(Long contract_id) {
        this.contract_id = contract_id;
    }

    public String getLongcode() {
        return longcode;
    }

    public void setLongcode(String longcode) {
        this.longcode = longcode;
    }

    public Number getPayout() {
        return payout;
    }

    public void setPayout(Number payout) {
        this.payout = payout;
    }

    public Integer getPurchase_time() {
        return purchase_time;
    }

    public void setPurchase_time(Integer purchase_time) {
        this.purchase_time = purchase_time;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public Integer getStart_time() {
        return start_time;
    }

    public void setStart_time(Integer start_time) {
        this.start_time = start_time;
    }

    public Long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Buy{" +
                "balance_after=" + balance_after +
                ", buy_price=" + buy_price +
                ", contract_id=" + contract_id +
                ", longcode='" + longcode + '\'' +
                ", payout=" + payout +
                ", purchase_time=" + purchase_time +
                ", shortcode='" + shortcode + '\'' +
                ", start_time=" + start_time +
                ", transaction_id=" + transaction_id +
                ", error=" + error +
                '}';
    }
}
