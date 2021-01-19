package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.services.ServiceMascara;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

import javax.persistence.*;

@Entity(name = "HistoricoTicks")
@Table(name = "historicoTicks")
public class HistoricoTicks {
    public static final long serialVersionUID = 1L;

    private LongProperty id = new SimpleLongProperty();
    private Number price;
    private Integer ultimoDigito;
    private Number pip_size;
    private Integer time;

    public HistoricoTicks(Number price, Number pip_size, Integer time) {
        this.price = price;
        this.pip_size = pip_size;
        this.time = time;
        setUltimoDigito(price);
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

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public Integer getUltimoDigito() {
        return ultimoDigito;
    }

    public void setUltimoDigito(Integer ultimoDigito) {
        this.ultimoDigito = ultimoDigito;
    }

    public Number getPip_size() {
        return pip_size;
    }

    public void setPip_size(Number pip_size) {
        this.pip_size = pip_size;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setUltimoDigito(Number ultimoQuote) {
        String str = ServiceMascara.getValorFormatado(getPip_size().intValue(), getPrice().doubleValue());
        setUltimoDigito(Integer.parseInt(str.substring(str.length() - 1)));
    }

    @Transient
    public String getQuoteCompleto() {
        return ServiceMascara.getValorFormatado(getPip_size().intValue(), getPrice().doubleValue());
    }

    @Override
    public String toString() {
        return "HistoricoTicks{" +
                "price=" + price +
                ", ultimoDigito=" + ultimoDigito +
                ", pip_size=" + pip_size +
                ", time=" + time +
                '}';
    }
}
