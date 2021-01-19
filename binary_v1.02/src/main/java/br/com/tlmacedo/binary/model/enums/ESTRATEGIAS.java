package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum ESTRATEGIAS {

    NULL(0, ""),
    DIFF0(1, "DIFF_MENOR"),

    //    CALL(10, "CALL"),
//    PUT(11, "PUT"),
    CALL_PUT(12, "CALL_PUT_Reversed"),

    OVER(20, "OVER*2"),

    EVEN_ODD0(30, "EVEN_ODD"),
    EVEN_ODD1(31, "EVEN_ODD_QUEIROZ");

    private Integer cod;
    private String descricao;

    ESTRATEGIAS(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static ESTRATEGIAS toEnum(Integer cod) {
        if (cod == null) return null;
        for (ESTRATEGIAS estrategias : ESTRATEGIAS.values())
            if (cod.equals(estrategias.getCod()))
                return estrategias;
        throw new IllegalArgumentException("Id inválido!");
    }

    public static List<ESTRATEGIAS> getList() {
        List<ESTRATEGIAS> list = Arrays.asList(ESTRATEGIAS.values());
        list.sort(Comparator.comparing(ESTRATEGIAS::getDescricao));
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
