package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;

import java.io.Serializable;
import java.math.BigDecimal;

public class Ohlc implements Serializable {
    public static final long serialVersionUID = 1L;

    String id;
    String symbol;
    Integer granularity;
    BigDecimal close;
    Integer epoch;
    BigDecimal high;
    BigDecimal low;
    BigDecimal open;
    Integer open_time;
    Integer pip_size;

    public Ohlc() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getGranularity() {
        return granularity;
    }

    public void setGranularity(Integer granularity) {
        this.granularity = granularity;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Integer getEpoch() {
        return epoch;
    }

    public void setEpoch(Integer epoch) {
        this.epoch = epoch;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public Integer getOpen_time() {
        return open_time;
    }

    public void setOpen_time(Integer open_time) {
        this.open_time = open_time;
    }

    public Integer getPip_size() {
        return pip_size;
    }

    public void setPip_size(Integer pip_size) {
        this.pip_size = pip_size;
    }

    public String getQuoteCompleto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getClose());
    }

    @Override
    public String toString() {
        if (getQuoteCompleto() != null)
            return getQuoteCompleto();
        return "";
    }


    public String toString_Geral() {
        return "Ohlc{" +
                "close=" + close +
                ", epoch=" + epoch +
                ", granularity=" + granularity +
                ", high=" + high +
                ", id='" + id + '\'' +
                ", low=" + low +
                ", open=" + open +
                ", open_time=" + open_time +
                ", pip_size=" + pip_size +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
