package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;

import java.math.BigDecimal;

public class PriceProposal {

    private Integer proposal;
    private BigDecimal amount;
    private String barrier;
    private String basis;
    private CONTRACT_TYPE contract_type;
    private String currency;
    private Integer duration;
    private DURATION_UNIT duration_unit;
    private String symbol;

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
        System.out.printf("passando por aqui");
        return String.format("PriceProposal{proposal=%s, amount=%s, " +
                        "%sbasis='%s', contract_type=%s, currency='%s', duration=%s, duration_unit=%s, symbol=%s}",
                getProposal(), getAmount(),
                (contract_type == CONTRACT_TYPE.DIGITODD
                        || contract_type == CONTRACT_TYPE.DIGITEVEN
                        || contract_type == CONTRACT_TYPE.CALL
                        || contract_type == CONTRACT_TYPE.PUT)
                        ? "" : "barrier='%s', ", getBasis(), getContract_type(),
                getCurrency(), getDuration(), getDuration_unit(), getSymbol()
        );
//        return "PriceProposal{" +
//                "proposal=" + proposal +
//                ", amount=" + amount +
//                ", barrier='" + barrier + '\'' +
//                ", basis='" + basis + '\'' +
//                ", contract_type=" + contract_type +
//                ", currency='" + currency + '\'' +
//                ", duration=" + duration +
//                ", duration_unit=" + duration_unit +
//                ", symbol=" + symbol +
//                '}';
    }
}
