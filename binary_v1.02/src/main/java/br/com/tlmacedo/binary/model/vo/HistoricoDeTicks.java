package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.services.ServiceMascara;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity(name = "HistoricoDeTicks")
@Table(name = "historico_de_ticks")
public class HistoricoDeTicks implements Serializable {
    public static final long serialVersionUID = 1L;

    private LongProperty id = new SimpleLongProperty();
    private ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private IntegerProperty ultimoDigito = new SimpleIntegerProperty();
    private IntegerProperty pip_size = new SimpleIntegerProperty();
    private IntegerProperty time = new SimpleIntegerProperty();

    public HistoricoDeTicks() {
    }

    public HistoricoDeTicks(Integer symbolId, BigDecimal price, Integer time) {
        this.symbol = new SimpleObjectProperty<>(Operacoes.getSymbolObservableList().get(symbolId));
        this.price = new SimpleObjectProperty<>(price);
//        this.ultimoDigito = new SimpleIntegerProperty(ultimoDigito);
        this.pip_size = new SimpleIntegerProperty(getSymbol().pip_sizeProperty().getValue());
        this.time = new SimpleIntegerProperty(time);
        setUltimoDigito();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(length = 19, nullable = false)
    public BigDecimal getPrice() {
        return price.get().setScale(getPip_size(), RoundingMode.HALF_UP);
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    @Column(length = 1, nullable = false)
    public int getUltimoDigito() {
        return ultimoDigito.get();
    }

    public IntegerProperty ultimoDigitoProperty() {
        return ultimoDigito;
    }

    public void setUltimoDigito(int ultimoDigito) {
        this.ultimoDigito.set(ultimoDigito);
    }

    @Column(length = 2, nullable = false)
    public int getPip_size() {
        return pip_size.get();
    }

    public IntegerProperty pip_sizeProperty() {
        return pip_size;
    }

    public void setPip_size(int pip_size) {
        this.pip_size.set(pip_size);
    }

    @Column(nullable = false)
    public int getTime() {
        return time.get();
    }

    public IntegerProperty timeProperty() {
        return time;
    }

    public void setTime(int time) {
        this.time.set(time);
    }

    @Transient
    public void setUltimoDigito() {
        String str = getPrice().toString();
        setUltimoDigito(Integer.parseInt(str.substring(str.length() - 1)));
    }

    @Transient
    public String getQuoteCompleto() {
        return ServiceMascara.getValorFormatado(getPip_size(), getPrice());
    }

    @Override
    public String toString() {
        return "HistoricoDeTicks{" +
                "id=" + id +
                ", symbol=" + symbol +
                ", price=" + price +
                ", ultimoDigito=" + ultimoDigito +
                ", pip_size=" + pip_size +
                ", time=" + time +
                '}';
    }

}
