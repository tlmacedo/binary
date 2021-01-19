package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;

import java.math.BigDecimal;

public class PriceProposal {

    Integer proposal;
    BigDecimal amount;
    String barrier;
    String basis;
    CONTRACT_TYPE contract_type;
    String currency;
    Integer duration;
    DURATION_UNIT duration_unit;
    String symbol;

    public PriceProposal() {
    }

    public Integer getProposal() {
        return proposal;
    }

    public void setProposal(Integer proposal) {
        this.proposal = proposal;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBarrier() {
        return barrier;
    }

    public void setBarrier(String barrier) {
        this.barrier = barrier;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public CONTRACT_TYPE getContract_type() {
        return contract_type;
    }

    public void setContract_type(CONTRACT_TYPE contract_type) {
        this.contract_type = contract_type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public DURATION_UNIT getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(DURATION_UNIT duration_unit) {
        this.duration_unit = duration_unit;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "PriceProposal{" +
                "proposal=" + proposal +
                ", amount=" + amount +
                ", barrier='" + barrier + '\'' +
                ", basis='" + basis + '\'' +
                ", contract_type=" + contract_type +
                ", currency='" + currency + '\'' +
                ", duration=" + duration +
                ", duration_unit=" + duration_unit +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
