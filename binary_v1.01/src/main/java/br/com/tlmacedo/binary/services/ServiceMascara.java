package br.com.tlmacedo.binary.services;

import java.text.DecimalFormat;

public class ServiceMascara {

    public static String getValorFormatado(int qtdDecimal, double valor) {
        String mascara = String.format("%s.%0" + qtdDecimal + "d", "###,###", 0);
        return new DecimalFormat(mascara).format(valor).replace(",", ";").replace(".", ",").replace(";", ".");
    }

    public static String getValorMoeda(double vlr) {
        return new DecimalFormat("#,##0.00").format(vlr);//.replace(",", ";").replace(".", ",").replace(";", ".");
    }
}
