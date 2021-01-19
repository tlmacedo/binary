package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.TICK_STYLE;

import java.io.Serializable;

public class Passthrough implements Serializable {
    public static final long serialVersionUID = 1L;

    Symbol symbol;
    TICK_STYLE tick_style;
    String mensagem;

    public Passthrough() {
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public TICK_STYLE getTick_style() {
        return tick_style;
    }

    public void setTick_style(TICK_STYLE tick_style) {
        this.tick_style = tick_style;
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
                ", tick_style=" + tick_style +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}
