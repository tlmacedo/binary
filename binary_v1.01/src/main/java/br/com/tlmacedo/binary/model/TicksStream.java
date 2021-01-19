package br.com.tlmacedo.binary.model;

public class TicksStream {

    private String ticks;
    private Integer subscribe;

    public TicksStream(String ticks) {
        this.ticks = ticks;
        this.subscribe = 1;
    }

    public TicksStream(String ticks, Integer subscribe) {
        this.ticks = ticks;
        this.subscribe = subscribe;
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
        return "TicksStream{" +
                "ticks='" + ticks + '\'' +
                ", subscribe=" + subscribe +
                '}';
    }
}
