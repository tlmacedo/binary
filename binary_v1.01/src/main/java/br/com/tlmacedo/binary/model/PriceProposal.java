package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.model.Enums.CONTRAC_TYPE;
import br.com.tlmacedo.binary.model.Enums.DURATION_UNIT;
import br.com.tlmacedo.binary.model.Enums.SYMBOL;

public class PriceProposal {

    private Integer proposal;
    private Number amount;
    private String barrier;
    private String basis;
    private CONTRAC_TYPE contract_type;
    private String currency;
    private Integer duration;
    private DURATION_UNIT duration_unit;
    private SYMBOL symbol;

    public PriceProposal() {
    }

    public Integer getProposal() {
        return proposal;
    }

    public void setProposal(Integer proposal) {
        this.proposal = proposal;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
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

    public CONTRAC_TYPE getContract_type() {
        return contract_type;
    }

    public void setContract_type(CONTRAC_TYPE contract_type) {
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

    public SYMBOL getSymbol() {
        return symbol;
    }

    public void setSymbol(SYMBOL symbol) {
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
                ", symbol=" + symbol +
                '}';
    }
}
