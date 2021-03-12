package br.com.tlmacedo.binary.model.vo;


import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.services.Service_Mascara;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "historicoDeTicks")
@Table(name = "historioco_de_ticks")
public class HistoricoDeTicks implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>(BigDecimal.ZERO);
    IntegerProperty time = new SimpleIntegerProperty();
    IntegerProperty ultimoDigito = new SimpleIntegerProperty();
    IntegerProperty pip_size = new SimpleIntegerProperty();

    public HistoricoDeTicks() {
    }

    public HistoricoDeTicks(Ohlc ohlc) {
        this.symbol = new SimpleObjectProperty<>(Operacoes.getSymbolObservableList().stream()
                .filter(symbol1 -> symbol1.getSymbol().equals(ohlc.getSymbol()))
                .findFirst().get());
        this.price = new SimpleObjectProperty<>(ohlc.getClose());
        this.time = new SimpleIntegerProperty(ohlc.getEpoch());
        this.pip_size = new SimpleIntegerProperty(ohlc.getPip_size());
        setUltimoDigito();
    }

    public HistoricoDeTicks(Symbol symbol, BigDecimal price, Integer time) {
        this.symbol = new SimpleObjectProperty<>(symbol);
        this.price = new SimpleObjectProperty<>(price);
        this.time = new SimpleIntegerProperty(time);
        this.pip_size = new SimpleIntegerProperty(Service_Mascara.getNumberOfDecimal(symbol.getPip()));
        setUltimoDigito();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id.get();
    }

    public LongProperty idProperty() {
        return id;
    }

    public void setId(long id) {
        this.id.set(id);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public Symbol getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<Symbol> symbolProperty() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol.set(symbol);
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
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

    public int getUltimoDigito() {
        return ultimoDigito.get();
    }

    public IntegerProperty ultimoDigitoProperty() {
        return ultimoDigito;
    }

    public void setUltimoDigito(int ultimoDigito) {
        this.ultimoDigito.set(ultimoDigito);
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

    public void setUltimoDigito() {
        String str = getQuoteCompleto();
        setUltimoDigito(Integer.parseInt(str.substring(str.length() - 1)));
    }

    @JsonIgnore
    @Transient
    public String getQuoteCompleto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getPrice());
    }

    @Override
    public String toString() {
        return "HistoricoDeTicks{" +
                "id=" + id +
                ", symbol=" + symbol +
                ", price=" + price +
                ", time=" + time +
                ", ultimoDigito=" + ultimoDigito +
                ", pip_size=" + pip_size +
                '}';
    }
}
