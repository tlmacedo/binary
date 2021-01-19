package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum CONTRACT_TYPE {

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

    Integer cod;
    String descricao;

    CONTRACT_TYPE(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static CONTRACT_TYPE toEnum(Integer cod) {
        if (cod == null) return null;
        for (CONTRACT_TYPE type : CONTRACT_TYPE.values())
            if (cod == type.getCod())
                return type;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<CONTRACT_TYPE> getList() {
        List<CONTRACT_TYPE> list = Arrays.asList(CONTRACT_TYPE.values());
        list.sort(Comparator.comparing(CONTRACT_TYPE::getDescricao));
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
