package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TransactionsStream {

    private IntegerProperty transaction;
    private IntegerProperty subscribe;

    public TransactionsStream(Integer transaction) {
        this.transaction = new SimpleIntegerProperty(transaction);
        this.subscribe = new SimpleIntegerProperty(1);
    }

    public int getTransaction() {
        return transaction.get();
    }

    public IntegerProperty transactionProperty() {
        return transaction;
    }

    public void setTransaction(int transaction) {
        this.transaction.set(transaction);
    }

    public int getSubscribe() {
        return subscribe.get();
    }

    public IntegerProperty subscribeProperty() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe.set(subscribe);
    }

    @Override
    public String toString() {
        return "TransactionsStream{" +
                "transaction=" + transaction +
                ", subscribe=" + subscribe +
                '}';
    }
}
