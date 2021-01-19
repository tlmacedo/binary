package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum TICK_STYLE {

    TICKS(0, "ticks"),
    CANDLES(7, "candles");

    private Integer cod;
    private String descricao;

    TICK_STYLE(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static TICK_STYLE toEnum(Integer cod) {
        if (cod == null) return null;
        for (TICK_STYLE style : TICK_STYLE.values())
            if (cod == style.getCod())
                return style;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<TICK_STYLE> getList() {
        List<TICK_STYLE> list = Arrays.asList(TICK_STYLE.values());
        list.sort(Comparator.comparing(TICK_STYLE::getDescricao));
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
