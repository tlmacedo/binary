package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.model.Enums.Error;

public class Proposal {

    private Number ask_price;
    private Integer date_start;
    private String display_value;
    private String id;
    private String longcode;
    private Number payout;
    private Number spot;
    private Integer spot_time;
    private Error error;

    public Proposal() {
    }

    public Number getAsk_price() {
        return ask_price;
    }

    public void setAsk_price(Number ask_price) {
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

    public Number getPayout() {
        return payout;
    }

    public void setPayout(Number payout) {
        this.payout = payout;
    }

    public Number getSpot() {
        return spot;
    }

    public void setSpot(Number spot) {
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
