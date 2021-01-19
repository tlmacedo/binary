package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum SYMBOL {
    R_10(0, "R_10"),
    R_25(1, "R_25"),
    R_50(2, "R_50"),
    R_75(3, "R_75"),
    R_100(4, "R_100");
//    R_10(0, "R_10"),
//    R_25(1, "R_25");

    private Integer cod;
    private String descricao;

    private SYMBOL(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static SYMBOL toEnum(Integer cod) {
        if (cod == null) return null;
        for (SYMBOL symbol : SYMBOL.values())
            if (cod.equals(symbol.getCod()))
                return symbol;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<SYMBOL> getList() {
        List list = Arrays.asList(SYMBOL.values());
        Collections.sort(list, new Comparator<SYMBOL>() {
            @Override
            public int compare(SYMBOL e1, SYMBOL e2) {
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
