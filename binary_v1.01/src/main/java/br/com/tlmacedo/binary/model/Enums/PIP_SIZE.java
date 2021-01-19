package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum PIP_SIZE {
    R_10(0, "3"),
    R_25(1, "3"),
    R_50(2, "4"),
    R_75(3, "4"),
    R_100(4, "2");

    private Integer cod;
    private String descricao;

    private PIP_SIZE(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static PIP_SIZE toEnum(Integer cod) {
        if (cod == null) return null;
        for (PIP_SIZE symbol : PIP_SIZE.values())
            if (cod.equals(symbol.getCod()))
                return symbol;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<PIP_SIZE> getList() {
        List list = Arrays.asList(PIP_SIZE.values());
        Collections.sort(list, new Comparator<PIP_SIZE>() {
            @Override
            public int compare(PIP_SIZE e1, PIP_SIZE e2) {
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
