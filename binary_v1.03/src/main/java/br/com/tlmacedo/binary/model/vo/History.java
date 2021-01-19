package br.com.tlmacedo.binary.model.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class History {

    List<Integer> times = new ArrayList<>();
    List<BigDecimal> prices = new ArrayList<>();

    public History() {
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void setTimes(List<Integer> times) {
        this.times = times;
    }

    public List<BigDecimal> getPrices() {
        return prices;
    }

    public void setPrices(List<BigDecimal> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return "History{" +
                "times=" + times +
                ", prices=" + prices +
                '}';
    }
}
