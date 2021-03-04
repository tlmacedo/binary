package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][] proposal = new Proposal[TICK_TIME.values().length][getSymbolObservableList().size()][2];

    @Override
    public void definicaoDeContrato() throws Exception {

        BigDecimal vlr;
        Integer qtd;
        Service_Alert alert = new Service_Alert();
        alert.setCabecalho("Stake");
        alert.setContentText("Qual o valor da stake padrão para operações?");
        vlr = new BigDecimal(alert.alertTextField("#,##0.00", "0.35", "").get());

        alert = new Service_Alert();
        alert.setContentText("Espera quantas candles seguidas em pull ou call?");
        qtd = Integer.valueOf(alert.alertTextField("#,##0.*0", "5", "").get()
                .replaceAll("\\D", ""));

        for (TICK_TIME tick_time : TICK_TIME.values()) {
            if (!getTimeAtivo()[tick_time.getCod()].getValue()) continue;
            getVlrStkPadrao()[tick_time.getCod()].setValue(vlr);
            getQtdCandlesEntrada()[tick_time.getCod()].setValue(qtd);
            for (int i = 0; i < getSymbolObservableList().size(); i++) {
                getVlrStkContrato()[tick_time.getCod()][i]
                        .setValue(getVlrStkPadrao()[tick_time.getCod()].getValue());
                for (int contractType = 0; contractType < 2; contractType++) {
                    getProposal()[tick_time.getCod()][i][contractType] = new Proposal();
                    gerarContrato(tick_time, getSymbolObservableList().get(i), CONTRACT_TYPE.toEnum(contractType));
                }
            }
        }

        setParametrosUtilizadosRobo(String.format("Robô: %s\nvlr_Stake: %s %s\t\tqtd_Candles: %s",
                getRobo().getClass().getSimpleName(), getAuthorize().getCurrency(), vlr, qtd));

    }

    @Override
    public void monitorarCondicoesParaComprar() throws Exception {

        System.out.printf("\n\n\n");
        for (TICK_TIME tickTime : TICK_TIME.values()) {
            if (!getTimeAtivo()[tickTime.getCod()].getValue()) continue;
            int t_id = tickTime.getCod();
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                int finalS_id1 = s_id;
                getQtdCallOrPut()[t_id][s_id].addListener((ov, o, n) -> {
                    if (isRoboMonitorandoPausado()) return;
                    if (Math.abs(n.intValue()) >= getQtdCandlesEntrada()[t_id].getValue()) {
                        if (getProposal()[t_id][finalS_id][n.intValue() < 0 ? 0 : 1] != null) {
                            solicitarCompraContrato(getProposal()[t_id][finalS_id][n.intValue() < 0 ? 0 : 1]);
                            try {
                                getProposal()[t_id][finalS_id][n.intValue() < 0 ? 0 : 1] = null;
                                gerarContrato(tickTime, getSymbolObservableList().get(finalS_id), CONTRACT_TYPE.toEnum(n.intValue() < 0 ? 0 : 1));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

    }

    public Abr() {

        for (TICK_TIME tick_time : TICK_TIME.values()) {
            if (getTimeAtivo()[tick_time.getCod()].getValue()) {
                getVlrStkPadrao()[tick_time.getCod()] = new SimpleObjectProperty<>();
                getQtdCandlesEntrada()[tick_time.getCod()] = new SimpleIntegerProperty();
                for (int i = 0; i < getSymbolObservableList().size(); i++)
                    getVlrStkContrato()[tick_time.getCod()][i] = new SimpleObjectProperty<>();
            }
        }

    }


    /**
     * Getters and Setters!!!
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public static Proposal[][][] getProposal() {
        return proposal;
    }

    public static void setProposal(Proposal[][][] proposal) {
        Abr.proposal = proposal;
    }
}
