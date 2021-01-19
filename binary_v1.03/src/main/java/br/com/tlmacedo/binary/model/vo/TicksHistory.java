package br.com.tlmacedo.binary.model.vo;

public class TicksHistory {

    String ticks_history;
    Integer adjust_start_time;
    Integer count;
    String end;
    Integer start;
    String style;

    public TicksHistory(String ticks_history, Integer count) {
        this.ticks_history = ticks_history;
        this.adjust_start_time = 1;
        this.count = count;
        this.end = "latest";
        this.start = 1;
        this.style = "ticks";
    }

    public String getTicks_history() {
        return ticks_history;
    }

    public void setTicks_history(String ticks_history) {
        this.ticks_history = ticks_history;
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    @Override
    public String toString() {
        return "TicksHistory{" +
                "ticks_history='" + ticks_history + '\'' +
                ", adjust_start_time=" + adjust_start_time +
                ", count=" + count +
                ", end='" + end + '\'' +
                ", start=" + start +
                ", style='" + style + '\'' +
                '}';
    }
}
