package br.com.tlmacedo.binary.model.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

public class SimNao implements Serializable {
    public static final long serialVersionUID = 1L;

    BooleanProperty sim = new SimpleBooleanProperty();
    StringProperty descricao = new SimpleStringProperty();

    public SimNao() {
    }

    public SimNao(Boolean sim, String descricao) {
        this.sim = new SimpleBooleanProperty(sim == true);
        this.descricao = new SimpleStringProperty(descricao);
    }

    public boolean isSim() {
        return sim.get();
    }

    public BooleanProperty simProperty() {
        return sim;
    }

    public void setSim(boolean sim) {
        this.sim.set(sim);
    }

    public String getDescricao() {
        return descricao.get();
    }

    public StringProperty descricaoProperty() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao.set(descricao);
    }

    @Override
    public String toString() {
        return descricaoProperty().getValue();
    }
}
