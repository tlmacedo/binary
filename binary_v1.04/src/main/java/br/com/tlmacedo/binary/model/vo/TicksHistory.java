package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.TICK_STYLE;

import java.io.Serializable;

public class TicksHistory implements Serializable {
    public static final long serialVersionUID = 1L;

    String ticks_history;
    String end = "latest";
    Integer adjust_start_time = 1;
    Integer count;
    Integer granularity;
    Integer start = 1;
    String style;
    Integer subscribe = 1;
    Passthrough passthrough;

    public TicksHistory(String symbol, Integer count, TICK_STYLE tickStyle, Integer candleTime, Passthrough passthrough) {

        this.ticks_history = symbol;
        this.count = count;
        this.style = tickStyle.toString();
        if (tickStyle.equals(TICK_STYLE.CANDLES))
            this.granularity = candleTime;
        this.passthrough = passthrough;

    }

    public String getTicks_history() {
        return ticks_history;
    }

    public void setTicks_history(String ticks_history) {
        this.ticks_history = ticks_history;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getAdjust_start_time() {
        return adjust_start_time;
    }

    public void setAdjust_start_time(Integer adjust_start_time) {
        this.adjust_start_time = adjust_start_time;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getGranularity() {
        return granularity;
    }

    public void setGranularity(Integer granularity) {
        this.granularity = granularity;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Integer subscribe) {
        this.subscribe = subscribe;
    }

    public Passthrough getPassthrough() {
        return passthrough;
    }

    public void setPassthrough(Passthrough passthrough) {
        this.passthrough = passthrough;
    }

    @Override
    public String toString() {
        return "TicksHistory{" +
                "ticks_history='" + ticks_history + '\'' +
                ", end='" + end + '\'' +
                ", adjust_start_time=" + adjust_start_time +
                ", count=" + count +
                granularity != null
                ? ", granularity=" + granularity
                : "" +
                ", start=" + start +
                ", style='" + style + '\'' +
                ", subscribe=" + subscribe +
                passthrough != null
                ? ", passthrough=" + passthrough
                : "" +
                '}';
    }

}
