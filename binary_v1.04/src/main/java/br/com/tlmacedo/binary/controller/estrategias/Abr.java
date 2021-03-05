package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.model.vo.Transacoes;
import br.com.tlmacedo.binary.services.Service_Alert;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][][] proposal = new Proposal[TICK_TIME.values().length][getSymbolObservableList().size()][2][2];

    @Override
    public void definicaoDeContrato() throws Exception {

        BigDecimal vlr, martingale;
        Integer qtd;
        Service_Alert alert = new Service_Alert();
        alert.setCabecalho("Stake");
        alert.setContentText("Qual o valor da stake padrão para operações?");
        vlr = new BigDecimal(alert.alertTextField("#,##0.00", "0.35", "").get());

        alert = new Service_Alert();
        alert.setContentText("Espera quantas candles seguidas em pull ou call?");
        qtd = Integer.valueOf(alert.alertTextField("#,##0.*0", "5", "").get()
                .replaceAll("\\D", ""));

        alert = new Service_Alert();
        alert.setContentText("Qual a porcentagem do martingale em cima do loss acumulado?");
        martingale = new BigDecimal(alert.alertTextField("#,##0.00", "90.00", "").get());

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            getVlrStkPadrao()[t_id].setValue(vlr);
            getQtdCandlesEntrada()[t_id].setValue(qtd);
            getPorcMartingale()[t_id].setValue(martingale);
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                getVlrStkContrato()[t_id][s_id]
                        .setValue(getVlrStkPadrao()[t_id].getValue());
                for (int contractType = 0; contractType < 2; contractType++) {
                    for (int lastWin = 0; lastWin < 2; lastWin++)
                        getProposal()[t_id][s_id][contractType][lastWin] = new Proposal();
                    gerarContrato(t_id, s_id, CONTRACT_TYPE.toEnum(contractType));
                }
            }
        }

        setParametrosUtilizadosRobo(String.format("Robô: %s\nvlr_Stake: %s %s\tqtd_Candles: %s\tmart: %s%%",
                getRobo().getClass().getSimpleName(), getAuthorize().getCurrency(), vlr, qtd, martingale));

    }

    @Override
    public void monitorarCondicoesParaComprar() throws Exception {

        System.out.printf("\n\n\n");
        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getQtdCallOrPut()[t_id][s_id].addListener((ov, o, n) -> {
                    if (isRoboMonitorandoPausado()) return;
                    if (Math.abs(n.intValue()) >= getQtdCandlesEntrada()[finalT_id].getValue()) {
                        Proposal proposal;
                        if ((proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1]
                                [getLastTransictionIsWin()[finalT_id][finalS_id].getValue() ? 0 : 1]) != null) {
                            BigDecimal lastPayout = proposal.getPayout(), askPrice = proposal.getAsk_price();
                            solicitarCompraContrato(proposal);
                            for (int contract_id = 0; contract_id < 2; contract_id++)
                                for (int lastWin = 0; lastWin < 2; lastWin++)
                                    proposal = null;

                            gerarNovosContratos(finalT_id, finalS_id, lastPayout, askPrice);
                        }
                    }
                });
                getTransacoesFilteredList()[finalT_id][finalS_id].addListener((ListChangeListener<? super Transacoes>) c -> {
                    while (c.next())
                        if (c.wasUpdated()) {
                            System.out.printf("\n\nTransacoesFilteredList()[%s][%s]\n", finalT_id, finalS_id);
                            c.getList().forEach(System.out::println);
                        }
                });
            }
        }

    }

    public Abr() {

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++)
            if (getTimeAtivo()[t_id].getValue()) {
                getVlrStkPadrao()[t_id] = new SimpleObjectProperty<>();
                getQtdCandlesEntrada()[t_id] = new SimpleIntegerProperty();
                getPorcMartingale()[t_id] = new SimpleObjectProperty<>();
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    getVlrStkContrato()[t_id][s_id] = new SimpleObjectProperty<>();
                    getVlrLossAcumulado()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                    getLastTransictionIsWin()[t_id][s_id] = new SimpleBooleanProperty(true);
                }
            }

    }

    public static void gerarNovosContratos(int t_id, int s_id, BigDecimal payout, BigDecimal askPrice) {

        Platform.runLater(() -> {
            try {
                BigDecimal myPorcMg = new BigDecimal("2.")
                        .subtract(getPorcMartingale()[t_id].getValue().divide(new BigDecimal("100."), 5, RoundingMode.HALF_UP));
                BigDecimal myVlrLossAcumulado = new BigDecimal(getVlrLossAcumulado()[t_id][s_id].getValue().toString());
                myVlrLossAcumulado = myVlrLossAcumulado.add(payout);
                if (myVlrLossAcumulado.compareTo(BigDecimal.ZERO) < 0) {
                    myVlrLossAcumulado = myVlrLossAcumulado.negate();
                    if (myVlrLossAcumulado.compareTo(getVlrStkPadrao()[t_id].getValue()) >= 0) {
                        getVlrStkContrato()[t_id][s_id].setValue(
                                myVlrLossAcumulado.multiply(myPorcMg));
                    } else {
                        getVlrStkContrato()[t_id][s_id].setValue(
                                (getVlrStkPadrao()[t_id].getValue().add(myVlrLossAcumulado))
                                        .multiply(myPorcMg));
                    }
                } else {
                    getVlrStkContrato()[t_id][s_id].setValue(getVlrStkPadrao()[t_id].getValue());
                }
                for (int contractType = 0; contractType < 2; contractType++) {
                    getProposal()[t_id][s_id][contractType][0] = new Proposal();
                    gerarContrato(t_id, s_id, CONTRACT_TYPE.toEnum(contractType));
                }

                myVlrLossAcumulado = new BigDecimal(getVlrLossAcumulado()[t_id][s_id].getValue().toString());
                myVlrLossAcumulado = myVlrLossAcumulado.add(askPrice.negate());
                getVlrStkContrato()[t_id][s_id].setValue(
                        myVlrLossAcumulado.multiply(myPorcMg));
                for (int contractType = 0; contractType < 2; contractType++) {
                    getProposal()[t_id][s_id][contractType][1] = new Proposal();
                    gerarContrato(t_id, s_id, CONTRACT_TYPE.toEnum(contractType));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

//        gerarContrato(t_id, s_id);
//        if (transaction.getAmount().compareTo(BigDecimal.ZERO) == 0) {
//            Operacoes.getVlrLossAcumulado()[t_id][s_id].setValue(
//                    Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue()
//                            .add(transacao.getStakeCompra()));
//        } else {
//            Operacoes.getVlrLossAcumulado()[t_id][s_id].setValue(
//                    Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue()
//                            .add(transaction.getAmount()));
//            if (Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue().compareTo(BigDecimal.ZERO) < 0) {
//                Operacoes.getVlrLossAcumulado()[t_id][s_id].setValue(
//                        Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue()
//                                .add(Operacoes.getVlrStkPadrao()[t_id].getValue().negate())
//                );
//            }
//        }
//        System.out.printf("oldContrato: %s\n", Operacoes.getVlrStkContrato()[t_id][s_id]);
//        System.out.printf("getLossAcumulado==0:[%s]\n", Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue()
//                .compareTo(BigDecimal.ZERO));
//        if (Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue().compareTo(BigDecimal.ZERO) < 0) {
//            Operacoes.getVlrStkContrato()[t_id][s_id].setValue(
//                    (Operacoes.getVlrLossAcumulado()[t_id][s_id].getValue()
//                            .divide(Operacoes.getPorcMartingale()[t_id].getValue(), RoundingMode.HALF_UP))
//                            .multiply(new BigDecimal("100.")));
//        } else {
//            Operacoes.getVlrLossAcumulado()[t_id][s_id].setValue(BigDecimal.ZERO);
//            Operacoes.getVlrStkContrato()[t_id][s_id].setValue(
//                    Operacoes.getVlrStkPadrao()[t_id].getValue());
//        }
//        Operacoes.gerarContrato(t_id, s_id, CONTRACT_TYPE.valueOf(transacao.getContract_type()));
//        System.out.printf("newContrato: %s\n", Operacoes.getVlrStkContrato()[t_id][s_id].getValue());


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

    public static Proposal[][][][] getProposal() {
        return proposal;
    }

    public static void setProposal(Proposal[][][][] proposal) {
        Abr.proposal = proposal;
    }
}
