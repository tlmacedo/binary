package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum TICK_TIME {

    T1M(0, "1M"),
    T2M(1, "2M"),
    T3M(2, "3M"),
    T5M(3, "5M"),
    T10M(4, "10M"),
    T15M(5, "15M");

    private Integer cod;
    private String descricao;

    TICK_TIME(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static TICK_TIME toEnum(Integer cod) {
        if (cod == null) return null;
        for (TICK_TIME style : TICK_TIME.values())
            if (cod == style.getCod())
                return style;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<TICK_TIME> getList() {
        List<TICK_TIME> list = Arrays.asList(TICK_TIME.values());
        list.sort(Comparator.comparing(TICK_TIME::getDescricao));
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

    @Override
    public String toString() {
        return getDescricao();
    }
}
