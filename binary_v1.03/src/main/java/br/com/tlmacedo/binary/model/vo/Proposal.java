package br.com.tlmacedo.binary.model.vo;

import java.math.BigDecimal;

public class Proposal {

    BigDecimal ask_price;
    Integer date_start;
    String display_value;
    String id;
    String longcode;
    BigDecimal payout;
    BigDecimal spot;
    Integer spot_time;
    Error error;

    public Proposal() {
    }

    public BigDecimal getAsk_price() {
        return ask_price;
    }

    public void setAsk_price(BigDecimal ask_price) {
        this.ask_price = ask_price;
    }

    public Integer getDate_start() {
        return date_start;
    }

    public void setDate_start(Integer date_start) {
        this.date_start = date_start;
    }

    public String getDisplay_value() {
        return display_value;
    }

    public void setDisplay_value(String display_value) {
        this.display_value = display_value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongcode() {
        return longcode;
    }

    public void setLongcode(String longcode) {
        this.longcode = longcode;
    }

    public BigDecimal getPayout() {
        return payout;
    }

    public void setPayout(BigDecimal payout) {
        this.payout = payout;
    }

    public BigDecimal getSpot() {
        return spot;
    }

    public void setSpot(BigDecimal spot) {
        this.spot = spot;
    }

    public Integer getSpot_time() {
        return spot_time;
    }

    public void setSpot_time(Integer spot_time) {
        this.spot_time = spot_time;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Proposal{" +
                "ask_price=" + ask_price +
                ", date_start=" + date_start +
                ", display_value='" + display_value + '\'' +
                ", id='" + id + '\'' +
                ", longcode='" + longcode + '\'' +
                ", payout=" + payout +
                ", spot=" + spot +
                ", spot_time=" + spot_time +
                ", error=" + error +
                '}';
    }
}
