package br.com.tlmacedo.binary.model;

public class Local_currencies {

    private Currency currency;

    public Local_currencies() {
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Local_currencies{" +
                "currency=" + currency +
                '}';
    }
}
