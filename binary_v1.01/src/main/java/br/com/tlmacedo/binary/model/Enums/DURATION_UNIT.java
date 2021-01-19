package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum DURATION_UNIT {
    d(1, "dias"),
    m(1, "minutos"),
    s(1, "secundos"),
    h(1, "horas"),
    t(1, "ticks");

    private Integer cod;
    private String descricao;

    private DURATION_UNIT(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }


    public static List<DURATION_UNIT> getList() {
        List list = Arrays.asList(DURATION_UNIT.values());
        Collections.sort(list, new Comparator<DURATION_UNIT>() {
            @Override
            public int compare(DURATION_UNIT e1, DURATION_UNIT e2) {
                return e1.getDescricao().compareTo(e2.getDescricao());
            }
        });
        return list;
    }

    public Integer getCod() {
        return cod;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return getDescricao();
    }


}
