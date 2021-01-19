package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.services.ServiceMascara;

public class Tick {

    private Number ask;
    private Number bid;
    private Integer epoch;
    private String id;
    private Number pip_size;
    private Number quote;
    private String symbol;
    private Integer ultimoDigt;


    public Tick() {
    }

    public Number getAsk() {
        return ask;
    }

    public void setAsk(Number ask) {
        this.ask = ask;
    }

    public Number getBid() {
        return bid;
    }

    public void setBid(Number bid) {
        this.bid = bid;
    }

    public Integer getEpoch() {
        return epoch;
    }

    public void setEpoch(Integer epoch) {
        this.epoch = epoch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Number getPip_size() {
        return pip_size;
    }

    public void setPip_size(Number pip_size) {
        this.pip_size = pip_size;
    }

    public Number getQuote() {
        return quote;
    }

    public void setQuote(Number quote) {
        this.quote = quote;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getUltimoDigt() {
        String str = ServiceMascara.getValorFormatado(getPip_size().intValue(), getQuote().doubleValue());
        return Integer.parseInt(str.substring(str.length() - 1));
    }

    public void setUltimoDigt(Integer ultimoDigt) {
        this.ultimoDigt = ultimoDigt;
    }

    @Override
    public String toString() {
        return ServiceMascara.getValorFormatado(getPip_size().intValue(), getQuote().doubleValue());
    }


    public String toPrint() {
        return "Tick{" +
                "ask=" + ask +
                ", bid=" + bid +
                ", epoch=" + epoch +
                ", id='" + id + '\'' +
                ", pip_size=" + pip_size +
                ", quote=" + quote +
                ", symbol='" + symbol + '\'' +
                ", ultimoDigt=" + ultimoDigt +
                '}';
    }
}
