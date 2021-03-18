package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.TICK_STYLE;

import java.io.Serializable;

public class Passthrough implements Serializable {
    public static final long serialVersionUID = 1L;

    int t_id;
    int s_id;
    int typeCandle_id;
    int typeContract_id;
    Integer priceProposal_id = -1;
    String mensagem;


    public Passthrough() {
    }

    public Passthrough(int t_id, int s_id, int typeCandle_id, int typeContract_id, Integer priceProposal_id, String mensagem) {
        this.t_id = t_id;
        this.s_id = s_id;
        this.typeCandle_id = typeCandle_id;
        this.typeContract_id = typeContract_id;
        if (priceProposal_id != null)
            this.priceProposal_id = priceProposal_id;
        this.mensagem = mensagem;
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

    public int getTypeCandle_id() {
        return typeCandle_id;
    }

    public void setTypeCandle_id(int typeCandle_id) {
        this.typeCandle_id = typeCandle_id;
    }

    public int getTypeContract_id() {
        return typeContract_id;
    }

    public void setTypeContract_id(int typeContract_id) {
        this.typeContract_id = typeContract_id;
    }

    public int getPriceProposal_id() {
        return priceProposal_id;
    }

    public void setPriceProposal_id(int priceProposal_id) {
        this.priceProposal_id = priceProposal_id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "Passthrough{" +
                "t_id=" + t_id +
                ", s_id=" + s_id +
                ", typeCandle_id=" + typeCandle_id +
                ", typeContract_id=" + typeContract_id +
                ", priceProposal_id=" + priceProposal_id +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}
