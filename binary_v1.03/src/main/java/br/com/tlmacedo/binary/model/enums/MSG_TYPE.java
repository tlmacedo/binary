package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum MSG_TYPE {

    ERROR(0, "error"),
    AUTHORIZE(1, "authorize"),
    TICK(2, "tick"),
    PROPOSAL(3, "proposal"),
    BUY(4, "buy"),
    HISTORY(5, "history"),
    TRANSACTION(6, "transaction");

    private Integer cod;
    private String descricao;

    MSG_TYPE(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static MSG_TYPE toEnum(Integer cod) {
        if (cod == null) return null;
        for (MSG_TYPE msgType : MSG_TYPE.values())
            if (cod == msgType.getCod())
                return msgType;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<MSG_TYPE> getList() {
        List<MSG_TYPE> list = Arrays.asList(MSG_TYPE.values());
        list.sort(Comparator.comparing(MSG_TYPE::getDescricao));
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
