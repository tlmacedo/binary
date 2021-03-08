package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class BuyContract implements Serializable {
    public static final long serialVersionUID = 1L;

    String buy;
    BigDecimal price = new BigDecimal(10000);

    public BuyContract(String buy) {
        this.buy = buy;
    }

    public BuyContract(Proposal proposal) {
        this.buy = proposal.getId();
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
