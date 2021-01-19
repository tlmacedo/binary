package br.com.tlmacedo.binary.model.Enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum CONTRAC_TYPE {
    //    MULTUP (0, "MULTUP"),
//    MULTDOWN (1, "MULTDOWN"),
//    UPORDOWN (2, "UPORDOWN"),
//    EXPIRYRANGE (3, "EXPIRYRANGE"),
//    ONETOUCH (4, "ONETOUCH"),
//    CALLE (5, "CALLE"),
//    LBHIGHLOW (6, "LBHIGHLOW"),
//    ASIAND (7, "ASIAND"),
//    EXPIRYRANGEE (8, "EXPIRYRANGEE"),
    DIGITDIFF(9, "DIGITDIFF"),
    //    DIGITMATCH (10, "DIGITMATCH"),
    DIGITOVER(11, "DIGITOVER"),
    //    PUTE (12, "PUTE"),
    DIGITUNDER(13, "DIGITUNDER"),
    //    NOTOUCH (14, "NOTOUCH"),
    CALL(15, "CALL"),
    //    RANGE (16, "RANGE"),
//    LBFLOATPUT (17, "LBFLOATPUT"),
    DIGITODD(18, "DIGITODD"),
    PUT(19, "PUT"),
    //    ASIANU (20, "ASIANU"),
//    LBFLOATCALL (21, "LBFLOATCALL"),
//    EXPIRYMISSE (22, "EXPIRYMISSE"),
//    EXPIRYMISS (23, "EXPIRYMISS"),
    DIGITEVEN(24, "DIGITEVEN");
//    TICKHIGH (25, "TICKHIGH"),
//    TICKLOW (26, "TICKLOW"),
//    RESETCALL (27, "RESETCALL"),
//    RESETPUT (28, "RESETPUT"),
//    CALLSPREAD (29, "CALLSPREAD"),
//    PUTSPREAD (30, "PUTSPREAD"),
//    RUNHIGH (31, "RUNHIGH"),
//    RUNLOW(32, "RUNLOW");

    private Integer cod;
    private String descricao;

    private CONTRAC_TYPE(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static CONTRAC_TYPE toEnum(Integer cod) {
        if (cod == null) return null;
        for (CONTRAC_TYPE contracType : CONTRAC_TYPE.values())
            if (cod.equals(contracType.getCod()))
                return contracType;
        throw new IllegalArgumentException("Id inválido");
    }

    public static List<CONTRAC_TYPE> getList() {
        List list = Arrays.asList(CONTRAC_TYPE.values());
        Collections.sort(list, new Comparator<CONTRAC_TYPE>() {
            @Override
            public int compare(CONTRAC_TYPE e1, CONTRAC_TYPE e2) {
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
