package br.com.tlmacedo.binary.model;

import java.util.List;

public class Prices {

    private List<Number> prices;

    public Prices(List<Number> prices) {
        this.prices = prices;
    }

    public List<Number> getPrices() {
        return prices;
    }

    public void setPrices(List<Number> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return "History_Prices{" +
                "prices=" + prices +
                '}';
    }
}
