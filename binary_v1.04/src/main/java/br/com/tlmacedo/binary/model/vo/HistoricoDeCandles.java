package br.com.tlmacedo.binary.model.vo;


import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;

import java.io.Serializable;
import java.math.BigDecimal;

public class HistoricoDeCandles implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> open = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> high = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> low = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> close = new SimpleObjectProperty<>();
    IntegerProperty pip_size = new SimpleIntegerProperty();
    IntegerProperty time = new SimpleIntegerProperty();

    public HistoricoDeCandles() {
    }

    public HistoricoDeCandles(Symbol symbol, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, Integer pip_size, Integer time) {
        this.symbol = new SimpleObjectProperty<>(symbol);
        this.open = new SimpleObjectProperty<>(open);
        this.high = new SimpleObjectProperty<>(high);
        this.low = new SimpleObjectProperty<>(low);
        this.close = new SimpleObjectProperty<>(close);
        this.pip_size = new SimpleIntegerProperty(pip_size);
        this.time = new SimpleIntegerProperty(time);
    }

    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    public Symbol getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<Symbol> symbolProperty() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol.set(symbol);
    }

    public BigDecimal getOpen() {
        return open.get();
    }

    public ObjectProperty<BigDecimal> openProperty() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open.set(open);
    }

    public BigDecimal getHigh() {
        return high.get();
    }

    public ObjectProperty<BigDecimal> highProperty() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high.set(high);
    }

    public BigDecimal getLow() {
        return low.get();
    }

    public ObjectProperty<BigDecimal> lowProperty() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low.set(low);
    }

    public BigDecimal getClose() {
        return close.get();
    }

    public ObjectProperty<BigDecimal> closeProperty() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close.set(close);
    }

    public int getPip_size() {
        return pip_size.get();
    }

    public IntegerProperty pip_sizeProperty() {
        return pip_size;
    }

    public void setPip_size(int pip_size) {
        this.pip_size.set(pip_size);
    }

    public int getTime() {
        return time.get();
    }

    public IntegerProperty timeProperty() {
        return time;
    }

    public void setTime(int time) {
        this.time.set(time);
    }

    public String getQuoteCompleto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getClose());
    }

    @Override
    public String toString() {
        if (getQuoteCompleto() != null)
            return getQuoteCompleto();
        return "";
    }

//    @Override
//    public String toString() {
//        return "HistoricoDeCandles{" +
//                "id=" + id +
//                ", symbol=" + symbol +
//                ", open=" + open +
//                ", high=" + high +
//                ", low=" + low +
//                ", close=" + close +
//                ", pip_size=" + pip_size +
//                ", time=" + time +
//                '}';
//    }


}
