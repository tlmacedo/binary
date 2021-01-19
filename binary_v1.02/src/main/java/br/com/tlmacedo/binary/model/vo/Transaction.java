package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.controller.Operacoes;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "Transaction")
@Table(name = "transaction")
public class Transaction {
    public static final Long serialVersionUID = 1L;

    private Token token;
    private String action;
    private BigDecimal amount;
    private BigDecimal balance;
    private String barrier;
    private Long contract_id;
    private String currency;
    private Integer date_expiry;
    private String display_name;
    private Integer high_barrier;
    private String id;
    private String longcode;
    private String low_barrier;
    private Integer purchase_time;
    private String stop_out;
    private Symbol symbol;
    private String take_profit;
    private Long transaction_id;
    private Integer transaction_time;

    public Transaction() {
    }

    @JsonIgnore
    @ManyToOne
    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Column(length = 30, nullable = false)
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(length = 19, scale = 2, nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Column(length = 19, scale = 2, nullable = false)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Column(length = 5)
    public String getBarrier() {
        return barrier;
    }

    public void setBarrier(String barrier) {
        this.barrier = barrier;
    }

    @Column(length = 25, nullable = false)
    public Long getContract_id() {
        return contract_id;
    }

    public void setContract_id(Long contract_id) {
        this.contract_id = contract_id;
    }

    @Column(length = 12, nullable = false)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(length = 25, nullable = false)
    public Integer getDate_expiry() {
        return date_expiry;
    }

    public void setDate_expiry(Integer date_expiry) {
        this.date_expiry = date_expiry;
    }

    @Column(length = 255, nullable = false)
    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public Integer getHigh_barrier() {
        return high_barrier;
    }

    public void setHigh_barrier(Integer high_barrier) {
        this.high_barrier = high_barrier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongcode() {
        return longcode;
    }

    public void setLongcode(String longcode) {
        this.longcode = longcode;
    }

    public String getLow_barrier() {
        return low_barrier;
    }

    public void setLow_barrier(String low_barrier) {
        this.low_barrier = low_barrier;
    }

    public Integer getPurchase_time() {
        return purchase_time;
    }

    public void setPurchase_time(Integer purchase_time) {
        this.purchase_time = purchase_time;
    }

    public String getStop_out() {
        return stop_out;
    }

    public void setStop_out(String stop_out) {
        this.stop_out = stop_out;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol_id", foreignKey = @ForeignKey(name = "fk_transaction_symbol"))
    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = Operacoes.getSymbolObservableList().stream()
                .filter(symbol1 -> symbol1.nameProperty().getValue().toLowerCase().equals(symbol.toLowerCase()))
                .findFirst().get();
    }

    public String getTake_profit() {
        return take_profit;
    }

    public void setTake_profit(String take_profit) {
        this.take_profit = take_profit;
    }

    @Id
    public Long getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Long transaction_id) {
        this.transaction_id = transaction_id;
    }

    public Integer getTransaction_time() {
        return transaction_time;
    }

    public void setTransaction_time(Integer transaction_time) {
        this.transaction_time = transaction_time;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "action='" + action + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", barrier=" + barrier +
                ", contract_id=" + contract_id +
                ", currency='" + currency + '\'' +
                ", date_expiry=" + date_expiry +
                ", display_name='" + display_name + '\'' +
                ", high_barrier=" + high_barrier +
                ", id='" + id + '\'' +
                ", longcode='" + longcode + '\'' +
                ", low_barrier='" + low_barrier + '\'' +
                ", purchase_time=" + purchase_time +
                ", stop_out='" + stop_out + '\'' +
                ", symbol=" + symbol +
                ", take_profit='" + take_profit + '\'' +
                ", transaction_id=" + transaction_id +
                ", transaction_time=" + transaction_time +
                '}';
    }
}
