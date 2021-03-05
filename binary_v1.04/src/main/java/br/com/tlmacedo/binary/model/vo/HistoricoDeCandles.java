package br.com.tlmacedo.binary.model.vo;


import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class HistoricoDeCandles implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    IntegerProperty granularity = new SimpleIntegerProperty();
    ObjectProperty<BigDecimal> close = new SimpleObjectProperty<>();
    IntegerProperty epoch = new SimpleIntegerProperty();
    ObjectProperty<BigDecimal> high = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> low = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> open = new SimpleObjectProperty<>();
    IntegerProperty pip_size = new SimpleIntegerProperty();

    public HistoricoDeCandles(JSONObject o, int symbol_id, int granularity) {

        try {
            this.symbol = new SimpleObjectProperty<>(Operacoes.getSymbolObservableList().get(symbol_id));
            this.granularity = new SimpleIntegerProperty(granularity);
            this.close = new SimpleObjectProperty<>(o.getBigDecimal("close"));
            this.epoch = new SimpleIntegerProperty(o.getInt("epoch"));
            this.high = new SimpleObjectProperty<>(o.getBigDecimal("high"));
            this.low = new SimpleObjectProperty<>(o.getBigDecimal("low"));
            this.open = new SimpleObjectProperty<>(o.getBigDecimal("open"));
            this.pip_size = new SimpleIntegerProperty(getSymbol().getPip().intValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public HistoricoDeCandles(Ohlc ohlc) {

        this.symbol = new SimpleObjectProperty(Operacoes.getSymbolObservableList().stream()
                .filter(symbol1 -> symbol1.getSymbol().equals(ohlc.getSymbol()))
                .findFirst().get());
        this.granularity = new SimpleIntegerProperty(ohlc.granularity);
        this.close = new SimpleObjectProperty<>(ohlc.getClose());
        this.epoch = new SimpleIntegerProperty(ohlc.getEpoch());
        this.high = new SimpleObjectProperty<>(ohlc.getHigh());
        this.low = new SimpleObjectProperty<>(ohlc.getHigh());
        this.open = new SimpleObjectProperty<>(ohlc.getOpen());
        this.pip_size = new SimpleIntegerProperty(ohlc.getPip_size());

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

    public int getGranularity() {
        return granularity.get();
    }

    public IntegerProperty granularityProperty() {
        return granularity;
    }

    public void setGranularity(int granularity) {
        this.granularity.set(granularity);
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

    public int getEpoch() {
        return epoch.get();
    }

    public IntegerProperty epochProperty() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch.set(epoch);
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

    public BigDecimal getOpen() {
        return open.get();
    }

    public ObjectProperty<BigDecimal> openProperty() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open.set(open);
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

    public String getQuoteCompleto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getClose());
    }

//    @Override
//    public String toString() {
//        if (getQuoteCompleto() != null)
//            return getQuoteCompleto();
//        return "";
//    }


    @Override
    public String toString() {
        return "HistoricoDeCandles{" +
                "id=" + id +
                ", symbol=" + symbol +
                ", granularity=" + granularity +
                ", close=" + close +
                ", epoch=" + epoch +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", pip_size=" + pip_size +
                '}';
    }
}
