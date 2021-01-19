package br.com.tlmacedo.binary.model;

public class History {

    private Prices prices;
    private Times times;

    public History() {
    }

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Times getTimes() {
        return times;
    }

    public void setTimes(Times times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "History{" +
                "prices=" + prices +
                ", times=" + times +
                '}';
    }
}
