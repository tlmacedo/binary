package br.com.tlmacedo.binary.model;

public class TransactionsStream {

    private Integer transaction;
    private Integer subscribe;

    public TransactionsStream(Integer transaction) {
        this.transaction = transaction;
        this.subscribe = 1;
    }

    public Integer getTransaction() {
        return transaction;
    }

    public void setTransaction(Integer transaction) {
        this.transaction = transaction;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public String toString() {
        return "TransactionsStream{" +
                "transaction=" + transaction +
                ", subscribe=" + subscribe +
                '}';
    }
}
