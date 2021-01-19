package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum ACTION {
    BUY(0, "buy"),
    SELL(1, "sell"),
    DEPOSIT(2, "deposit"),
    WITHDRAWAL(3, "withdrawal"),
    ESCROW(4, "escrow"),
    ADJUSTMENT(5, "adjustment"),
    VIRTUAL_CREDIT(6, "virtual_credit");

    private Integer cod;
    private String descricao;

    private ACTION(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static ACTION toEnum(Integer cod) {
        if (cod == null) return null;
        for (ACTION action : ACTION.values())
            if (cod.equals(action.getCod()))
                return action;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<ACTION> getList() {
        List list = Arrays.asList(ACTION.values());
        Collections.sort(list, new Comparator<ACTION>() {
            @Override
            public int compare(ACTION e1, ACTION e2) {
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
