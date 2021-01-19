package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.services.Service_Mascara;

import java.math.BigDecimal;

public class Tick {

    BigDecimal ask;
    BigDecimal bid;
    Integer epoch;
    String id;
    Integer pip_size;
    BigDecimal quote;
    String symbol;
    Integer ultimoDigito;

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
            return getNewUltimoDigito();
        return ultimoDigito;
    }

    private Integer getNewUltimoDigito() {
        String str = getQuoteCompleto();
        return Integer.parseInt(str.substring(str.length() - 1));
    }

    public void setUltimoDigito(Integer ultimoDigito) {
        this.ultimoDigito = ultimoDigito;
    }

    public void setUltimoDigito() {
        String str = getQuoteCompleto();
        setUltimoDigito(Integer.parseInt(str.substring(str.length() - 1)));
    }

    public String getQuoteCompleto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getQuote());
    }

    @Override
    public String toString() {
        if (getQuoteCompleto() != null)
            return getQuoteCompleto();
        return "";
    }
}
