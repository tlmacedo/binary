package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
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

    Integer cod;
    String descricao;

    ACTION(Integer cod, String descricao) {
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
        List<ACTION> list = Arrays.asList(ACTION.values());
        list.sort(Comparator.comparing(ACTION::getDescricao));
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
        return descricao;
    }
}
