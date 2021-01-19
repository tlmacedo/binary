package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum DURATION_UNIT {

    d(0, "dias"),
    m(1, "minutos"),
    s(2, "secundos"),
    h(3, "horas"),
    t(4, "ticks");

    Integer cod;
    String descricao;

    DURATION_UNIT(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static DURATION_UNIT toEnum(Integer cod) {
        if (cod == null) return null;
        for (DURATION_UNIT duration : DURATION_UNIT.values())
            if (cod == duration.getCod())
                return duration;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<DURATION_UNIT> getList() {
        List<DURATION_UNIT> list = Arrays.asList(DURATION_UNIT.values());
        list.sort(Comparator.comparing(DURATION_UNIT::getDescricao));
        return list;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
