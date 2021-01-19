package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "Symbol")
@Table(name = "symbol")
public class Symbol implements Serializable {
    public static long serialVersionUID = 1L;

    private LongProperty id = new SimpleLongProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty descricao = new SimpleStringProperty();
    private IntegerProperty pip_size = new SimpleIntegerProperty();
    private BooleanProperty ativa = new SimpleBooleanProperty();

    public Symbol() {
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

    @Column(length = 20, nullable = false, unique = true)
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Column(length = 30, nullable = false, unique = true)
    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
    }

    @Column(length = 2, nullable = false, scale = 0)
    public int getPip_size() {
        return pip_size.get();
    }

    public IntegerProperty pip_sizeProperty() {
        return pip_size;
    }

    public void setPip_size(int pip_size) {
        this.pip_size.set(pip_size);
    }

    @Column(length = 1, nullable = false)
    public boolean isAtiva() {
        return ativa.get();
    }

    public BooleanProperty ativaProperty() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa.set(ativa);
    }

    @Override
    public String toString() {
        return getName();
    }
}
