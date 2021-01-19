package br.com.tlmacedo.binary.model.vo;

import java.math.BigDecimal;

public class BuyContract {

    String buy;
    BigDecimal price;

    public BuyContract(String buy) {
        this.buy = buy;
        this.price = new BigDecimal(10000);
    }

    public BuyContract(Proposal proposal) {
        this.buy = proposal.getId();
        this.price = new BigDecimal(10000);
    }

    public String getBuy() {
        return buy;
    }

    public void setBuy(String buy) {
        this.buy = buy;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
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
