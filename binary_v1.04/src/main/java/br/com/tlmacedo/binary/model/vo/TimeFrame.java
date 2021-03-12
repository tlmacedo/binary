package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "TimeFrame")
@Table(name = "time_frame")
public class TimeFrame implements Serializable {
    public static final long serialVersionUID = 1L;

    LongProperty id = new SimpleLongProperty();
    StringProperty name = new SimpleStringProperty();
    StringProperty label = new SimpleStringProperty();
    IntegerProperty granularity = new SimpleIntegerProperty();
    DURATION_UNIT duration_unit;
    BooleanProperty ativo = new SimpleBooleanProperty();

    public TimeFrame() {
    }

    public TimeFrame(Long id, String name, String label, Integer granularity, DURATION_UNIT duration_unit, Boolean ativo) {
        this.id = new SimpleLongProperty(id);
        this.name = new SimpleStringProperty(name);
        this.label = new SimpleStringProperty(label);
        this.granularity = new SimpleIntegerProperty(granularity);
        this.duration_unit = duration_unit;
        this.ativo = new SimpleBooleanProperty(ativo);
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

    @Column(length = 30, unique = true, nullable = false)
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Column(length = 20, unique = true, nullable = false)
    public String getLabel() {
        return label.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    @Column(length = 4, nullable = false)
    public int getGranularity() {
        return granularity.get();
    }

    public IntegerProperty granularityProperty() {
        return granularity;
    }

    public void setGranularity(int granularity) {
        this.granularity.set(granularity);
    }

    @Enumerated(EnumType.STRING)
    public DURATION_UNIT getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(DURATION_UNIT duration_unit) {
        this.duration_unit = duration_unit;
    }

    @Column(length = 1, nullable = false)
    public boolean isAtivo() {
        return ativo.get();
    }

    public BooleanProperty ativoProperty() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo.set(ativo);
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
