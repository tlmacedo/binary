package br.com.tlmacedo.binary.model.enums;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum ROBOS {

    //    NULL(0, null),
    ABR(1, "ABR");

    Integer cod;
    String descricao;

    ROBOS(Integer cod, String descricao) {
        this.cod = cod;
        this.descricao = descricao;
    }

    public static ROBOS toEnum(Integer cod) {
        if (cod == null) return null;
        for (ROBOS robo : ROBOS.values())
            if (cod == robo.getCod())
                return robo;
        throw new IllegalArgumentException("Id inválido!!!");
    }

    public static List<ROBOS> getList() {
        List<ROBOS> list = Arrays.asList(ROBOS.values());
        list.sort(Comparator.comparing(ROBOS::getDescricao));
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
//        if (getDescricao() == null)
//            return "";
        return getDescricao().toUpperCase();
    }
}
