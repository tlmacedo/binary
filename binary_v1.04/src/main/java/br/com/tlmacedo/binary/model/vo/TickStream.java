package br.com.tlmacedo.binary.model.vo;

import java.io.Serializable;

public class TickStream implements Serializable {
    public static final long serialVersionUID = 1L;

    String ticks;
    Integer subscribe = 1;

    public TickStream(String ticks, Integer subscribe) {
        this.ticks = ticks;
        this.subscribe = subscribe;
    }

    public TickStream(String ticks) {
        this.ticks = ticks;
    }

    public String getTicks() {
        return ticks;
    }

    public void setTicks(String ticks) {
        this.ticks = ticks;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    @Override
    public String toString() {
        return "TickStream{" +
                "ticks='" + ticks + '\'' +
                ", subscribe=" + subscribe +
                '}';
    }
}
