package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.services.Service_Mascara;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cancel implements Serializable {
    public static final long serialVersionUID = 1L;

    BigDecimal balance_after;
    Integer contract_id;
    Integer reference_id;
    BigDecimal sold_for;
    Integer transaction_id;

    public Cancel() {
    }

    public Cancel(Proposal proposal) {
        this.contract_id = Integer.valueOf(proposal.getId());
    }

    public BigDecimal getBalance_after() {
        return balance_after;
    }

    public void setBalance_after(BigDecimal balance_after) {
        this.balance_after = balance_after;
    }

    public Integer getContract_id() {
        return contract_id;
    }

    public void setContract_id(Integer contract_id) {
        this.contract_id = contract_id;
    }

    public Integer getReference_id() {
        return reference_id;
    }

    public void setReference_id(Integer reference_id) {
        this.reference_id = reference_id;
    }

    public BigDecimal getSold_for() {
        return sold_for;
    }

    public void setSold_for(BigDecimal sold_for) {
        this.sold_for = sold_for;
    }

    public Integer getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Integer transaction_id) {
        this.transaction_id = transaction_id;
    }

    @Override
    public String toString() {
        return "Cancel{" +
                "balance_after=" + balance_after +
                ", contract_id=" + contract_id +
                ", reference_id=" + reference_id +
                ", sold_for=" + sold_for +
                ", transaction_id=" + transaction_id +
                '}';
    }
}
