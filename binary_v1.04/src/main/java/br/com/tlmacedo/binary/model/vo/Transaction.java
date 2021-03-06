package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.services.Service_DataTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "transaction")
@Table(name = "transaction")
public class Transaction {
    public static final long serialVersionUID = 1L;

    ContaToken contaToken;

    String action;
    BigDecimal amount;
    BigDecimal balance;
    String barrier;
    Long contract_id;
    String currency;
    Integer date_expiry;
    String display_name;
    Integer high_barrier;
    String id;
    String longcode;
    String low_barrier;
    Integer purchase_time;
    String stop_out;
    Symbol symbol;
    TimeFrame tFrame;
    String take_profit;
    Long transaction_id;
    Integer transaction_time;
    BigDecimal payout;
    int t_id;
    int s_id;


    public Transaction() {
    }

    @ManyToOne
    public ContaToken getContaToken() {
        return contaToken;
    }

    public void setContaToken(ContaToken contaToken) {
        this.contaToken = contaToken;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getBarrier() {
        return barrier;
    }

    public void setBarrier(String barrier) {
        this.barrier = barrier;
    }

    public Long getContract_id() {
        return contract_id;
    }

    public void setContract_id(Long contract_id) {
        this.contract_id = contract_id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDate_expiry() {
        return date_expiry;
    }

    public void setDate_expiry(Integer date_expiry) {
        this.date_expiry = date_expiry;
    }

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

    @Id
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
        this.t_id = Service_DataTime.getTimeFrame_t_id(getLongcode());
        this.tFrame = Operacoes.getTimeFrameObservableList().get(getT_id());
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

    @ManyToOne
    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = Operacoes.getSymbolObservableList().stream()
                .filter(symbol1 -> symbol1.getSymbol().equals(symbol))
                .findFirst().orElse(null);
        for (int s = 0; s < Operacoes.getSymbolObservableList().size(); s++)
            if (Operacoes.getSymbolObservableList().get(s).getId()
                    == getSymbol().getId()) {
                setS_id(s);
                break;
            }
    }

    @ManyToOne
    @Transient
    public TimeFrame gettFrame() {
        return tFrame;
    }

    public void settFrame(TimeFrame tFrame) {
        this.tFrame = tFrame;
    }

    public String getTake_profit() {
        return take_profit;
    }

    public void setTake_profit(String take_profit) {
        this.take_profit = take_profit;
    }

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

    public BigDecimal getPayout() {
        return payout;
    }

    public void setPayout(BigDecimal payout) {
        this.payout = payout;
    }

    public int getT_id() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id = t_id;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "contaToken=" + contaToken +
                ", action='" + action + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                ", barrier='" + barrier + '\'' +
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
                ", tFrame=" + tFrame +
                ", take_profit='" + take_profit + '\'' +
                ", transaction_id=" + transaction_id +
                ", transaction_time=" + transaction_time +
                ", payout=" + payout +
                ", t_id=" + t_id +
                ", s_id=" + s_id +
                '}';
    }
}
