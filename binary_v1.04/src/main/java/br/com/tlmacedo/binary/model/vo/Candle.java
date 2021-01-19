package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Candle implements Serializable {
    public static final long serialVersionUID = 1L;

    BigDecimal close;
    Long epoch;
    BigDecimal high;
    BigDecimal low;
    BigDecimal open;

    public Candle() {
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Long getEpoch() {
        return epoch;
    }

    public void setEpoch(Long epoch) {
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

    @Override
    public String toString() {
        return "Candle{" +
                "close=" + close +
                ", epoch=" + epoch +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                '}';
    }
}
