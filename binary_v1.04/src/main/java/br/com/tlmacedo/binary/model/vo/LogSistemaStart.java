package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "LogSistemaStart")
@Table(name = "log_sistema_start")
public class LogSistemaStart implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    ContaToken contaToken = new ContaToken();
    LongProperty date = new SimpleLongProperty();
    StringProperty parametros = new SimpleStringProperty();


    public LogSistemaStart() {

    }

    public LogSistemaStart(ContaToken contaToken, Long date, String parametros) {
        this.contaToken = contaToken;
        this.date = new SimpleLongProperty(date);
        this.parametros = new SimpleStringProperty(parametros);
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
    public ContaToken getContaToken() {
        return contaToken;
    }

    public void setContaToken(ContaToken contaToken) {
        this.contaToken = contaToken;
    }

    @Column(length = 50, nullable = false)
    public long getDate() {
        return date.get();
    }

    public LongProperty dateProperty() {
        return date;
    }

    public void setDate(long date) {
        this.date.set(date);
    }

    @Column(length = 2000, nullable = false)
    public String getParametros() {
        return parametros.get();
    }

    public StringProperty parametrosProperty() {
        return parametros;
    }

    public void setParametros(String parametros) {
        this.parametros.set(parametros);
    }

    @Override
    public String toString() {
        return "LogSistemaStart{" +
                "id=" + id +
                ", date=" + date +
                ", parametros=" + parametros +
                '}';
    }
}
