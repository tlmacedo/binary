package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TicksStream {

    private StringProperty ticks = new SimpleStringProperty();
    private IntegerProperty subscribe = new SimpleIntegerProperty();

    public TicksStream(String ticks) {
        this.ticks = new SimpleStringProperty(ticks);
        this.subscribe = new SimpleIntegerProperty(1);
    }

    public TicksStream(String ticks, Integer subscribe) {
        this.ticks = new SimpleStringProperty(ticks);
        this.subscribe = new SimpleIntegerProperty(subscribe);
    }

    public String getTicks() {
        return ticks.get();
    }

    public StringProperty ticksProperty() {
        return ticks;
    }

    public void setTicks(String ticks) {
        this.ticks.set(ticks);
    }

    public int getSubscribe() {
        return subscribe.get();
    }

    public IntegerProperty subscribeProperty() {
        return subscribe;
    }

    public void setSubscribe(int subscribe) {
        this.subscribe.set(subscribe);
    }

    @Override
    public String toString() {
        return "TicksStream{" +
                "ticks=" + ticks +
                ", subscribe=" + subscribe +
                '}';
    }
}
