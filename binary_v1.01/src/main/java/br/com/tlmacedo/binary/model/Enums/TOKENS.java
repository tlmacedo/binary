package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum TOKENS {
    BOTSOFT_CAFEPERFEITO(0, "w2BWEOPBVKRoDzL"),
    BOT_CAFEPERFEITO(1, "OvaxOrN0pcghYdM")
    ,
    BOT_THIAGO(2, "6iaZF7GqA6naB5Q")    ;

    private Integer cod;
    private String descricao;

    private TOKENS(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static TOKENS toEnum(Integer cod) {
        if (cod == null) return null;
        for (TOKENS symbol : TOKENS.values())
            if (cod.equals(symbol.getCod()))
                return symbol;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<TOKENS> getList() {
        List list = Arrays.asList(TOKENS.values());
        Collections.sort(list, new Comparator<TOKENS>() {
            @Override
            public int compare(TOKENS e1, TOKENS e2) {
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

//    @Override
//    public String toString() {
//        return getDescricao();
//    }

}
