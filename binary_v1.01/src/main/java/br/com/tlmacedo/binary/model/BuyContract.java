package br.com.tlmacedo.binary.model;

public class BuyContract {

    private String buy;
    private Number price;

    public BuyContract(String buy) {
        this.buy =buy;
        this.price = 100000;
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BuyContract{" +
                "buy='" + buy + '\'' +
                ", price=" + price +
                '}';
    }
}