package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Candles implements Serializable {
    public static final long serialVersionUID = 1L;

    List<Candle> candles = new ArrayList<>();

    public Candles() {
    }

    public Candles(List candlesList) {
        this.candles = candlesList;
    }

    public List<Candle> getCandles() {
        return candles;
    }

    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }

    @Override
    public String toString() {
        return "Candles{" +
                "candles=" + candles +
                '}';
    }


}
