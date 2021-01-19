package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum ESTRATEGIAS {

    DIGITDIFF(0, "DIGIT_DIFF"),
    DIGITDIFF_MG_PARC(1, "DIGIT_DIFF_MG_PARC"),

    DIGITOVER1(10, "OVER -*1*-"),
    DIGITOVER2(11, "OVER -*2*-"),
    DIGITOVER3(12, "OVER -*3*-"),
    DIGITOVER6(13, "OVER -*6*-"),
    DIGITOVER8(14, "OVER -*8*-"),

    DIGITUNDER8(20, "OVER -*8*-"),
    DIGITUNDER7(21, "OVER -*7*-"),
    DIGITUNDER6(22, "OVER -*6*-"),
    DIGITUNDER3(23, "OVER -*3*-"),
    DIGITUNDER1(24, "OVER -*1*-"),

    DIGITUNDER3OVER6(31, "-*3*-UNDER_OVER-*6*-"),
    DIGITUNDER7OVER2(32, "-*7*-UNDER_OVER-*2*-"),

    DIGITODD(41, "ODD"),
    DIGITEVEN(42, "EVEN"),
    DIGITODDEVEN(43, "ODD_EVEN"),
    PUT(44, "PUT"),
    CALL(45, "CALL"),
    CALLPUT(46, "CALL_PUT");

    private Integer cod;
    private String descricao;

    private ESTRATEGIAS(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static ESTRATEGIAS toEnum(Integer cod) {
        if (cod == null) return null;
        for (ESTRATEGIAS action : ESTRATEGIAS.values())
            if (cod.equals(action.getCod()))
                return action;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<ESTRATEGIAS> getList() {
        List list = Arrays.asList(ESTRATEGIAS.values());
        Collections.sort(list, new Comparator<ESTRATEGIAS>() {
            @Override
            public int compare(ESTRATEGIAS e1, ESTRATEGIAS e2) {
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
