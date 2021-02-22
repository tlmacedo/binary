package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.TICK_STYLE;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;

import java.io.Serializable;

public class Passthrough implements Serializable {
    public static final long serialVersionUID = 1L;

    Symbol symbol;
    TICK_TIME tickTime;
    TICK_STYLE tickStyle;
    String mensagem;

    public Passthrough() {
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public TICK_TIME getTickTime() {
        return tickTime;
    }

    public void setTickTime(TICK_TIME tickTime) {
        this.tickTime = tickTime;
    }

    public TICK_STYLE getTickStyle() {
        return tickStyle;
    }

    public void setTickStyle(TICK_STYLE tickStyle) {
        this.tickStyle = tickStyle;
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
                "symbol=" + symbol +
                ", tickTime=" + tickTime +
                ", tickStyle=" + tickStyle +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}
