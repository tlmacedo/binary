package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Service_NewVlrContrato extends Operacoes {


    public static BigDecimal getPorcMf(BigDecimal mg) {
        return mg.divide(new BigDecimal("100."), 5, RoundingMode.HALF_UP).add(BigDecimal.ONE);
    }

    public static void calculaVlrContratos(int t_id, int s_id, boolean loss) {

        getVlrStkContrato()[t_id][s_id].setValue(loss
                ? getVlrTmpLoss(t_id, s_id)
                : getVlrStkPadrao()[t_id].getValue());

    }

    public static BigDecimal getVlrTmpLoss(int t_id, int s_id) {
        return getVlrStkContrato()[t_id][s_id].getValue()
                .multiply(getPorcMf(getPorcMartingale()[t_id].getValue()));
    }

}
