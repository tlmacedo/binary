package br.com.tlmacedo.binary.model.vo;


import br.com.tlmacedo.binary.controller.Operacao;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "HistoricoDeTicks")
@Table(name = "historico_de_ticks")
public class HistoricoDeTicks implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    IntegerProperty ultimoDigito = new SimpleIntegerProperty();
    IntegerProperty pip_size = new SimpleIntegerProperty();
    IntegerProperty time = new SimpleIntegerProperty();

    public HistoricoDeTicks() {
    }

    public HistoricoDeTicks(Integer symbolId, BigDecimal price, Integer time) {
        this.symbol = new SimpleObjectProperty<>(Operacao.getSymbolObservableList().get(symbolId));
        this.price = new SimpleObjectProperty<>(price);
        if (getSymbol().getName().contains("1HZ"))
            this.pip_size = new SimpleIntegerProperty(2);
        else
            this.pip_size = new SimpleIntegerProperty(getSymbol().getPip_size());
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public Symbol getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<Symbol> symbolProperty() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol.set(symbol);
    }

    @Column(length = 19, nullable = false, scale = 4)
    public BigDecimal getPrice() {
        return price.get();
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

    @Column(length = 1, nullable = false)
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
        String str = getQuoteCompreto();
        setUltimoDigito(Integer.parseInt(str.substring(str.length() - 1)));
    }

    @Transient
    public String getQuoteCompreto() {
        return Service_Mascara.getValorFormatado(getPip_size(), getPrice());
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
