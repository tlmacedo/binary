package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.services.ServiceMascara;

import java.math.BigDecimal;

public class Tick {

    private BigDecimal ask;
    private BigDecimal bid;
    private Integer epoch;
    private String id;
    private Integer pip_size;
    private BigDecimal quote;
    private String symbol;
    private Integer ultimoDigito;

    public Tick() {
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
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

    public Integer getPip_size() {
        return pip_size;
    }

    public void setPip_size(Integer pip_size) {
        this.pip_size = pip_size;
    }

    public BigDecimal getQuote() {
        return quote;
    }

    public void setQuote(BigDecimal quote) {
        this.quote = quote;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getUltimoDigito() {
        if (ultimoDigito == null)
            setUltimoDigito();
        return ultimoDigito;
    }

//    public void setUltimoDigito(Integer ultimoDigito) {
//        this.ultimoDigito = ultimoDigito;
//    }

    public String getQuoteCompleto() {
        return ServiceMascara.getValorFormatado(getPip_size(), getQuote());
    }

    public void setUltimoDigito() {
        String str = ServiceMascara.getValorFormatado(getPip_size(), getQuote());
        this.ultimoDigito = Integer.parseInt(str.substring(str.length() - 1));
    }

    @Override
    public String toString() {
        return getQuoteCompleto();
    }

    //    @Override
//    public String toString() {
//        return "Tick{" +
//                "ask=" + ask +
//                ", bid=" + bid +
//                ", epoch=" + epoch +
//                ", id='" + id + '\'' +
//                ", pip_size=" + pip_size +
//                ", quote=" + quote +
//                ", symbol='" + symbol + '\'' +
//                ", ultimoDigito=" + ultimoDigito +
//                '}';
//    }
}
