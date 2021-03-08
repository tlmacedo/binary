package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.model.vo.Transacoes;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_NewVlrContrato;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][][] proposal = new Proposal[TICK_TIME.values().length][getSymbolObservableList().size()][2][2];
    static ObjectProperty<BigDecimal>[][] vlrTmpWin = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTmpLoss = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];

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
        qtd = Integer.valueOf(alert.alertTextField("#,##0.*0", "1", "").get()
                .replaceAll("\\D", ""));

        alert = new Service_Alert();
        alert.setContentText("Qual a porcentagem do martingale em cima do loss acumulado?");
        martingale = new BigDecimal(alert.alertTextField("#,##0.00", "100.00", "").get());

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            getVlrStkPadrao()[t_id].setValue(vlr);
            getQtdCandlesEntrada()[t_id].setValue(qtd);
            getPorcMartingale()[t_id].setValue(martingale);
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                getVlrStkContrato()[t_id][s_id]
                        .setValue(getVlrStkPadrao()[t_id].getValue());
                for (int contractType = 0; contractType < 2; contractType++) {
                    for (int i = 0; i < 2; i++) {
                        getProposal()[t_id][s_id][contractType][i] = new Proposal();
                        if (i == 0)
                            gerarContrato(t_id, s_id, CONTRACT_TYPE.toEnum(contractType),
                                    i == 0 ? null : getVlrStkPadrao()[t_id].getValue());
                    }
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
                    if (n == null || isRoboMonitorandoPausado()) return;
                    boolean maior = Math.abs(n.intValue()) > getQtdCandlesEntrada()[finalT_id].getValue(),
                            igual = Math.abs(n.intValue()) == getQtdCandlesEntrada()[finalT_id].getValue();
                    if (maior || igual) {
                        Proposal proposal;
                        if (getTransacoesObservableList()[finalT_id][finalS_id].size() == 0)
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1][0];
                        else
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1]
                                    [maior ? 1 : 0];
                        if (proposal != null) {
                            getVlrStkContrato()[finalT_id][finalS_id].setValue(proposal.getAsk_price());
                            solicitarCompraContrato(proposal);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void gerarNovosContratos(int t_id, int s_id) {

        try {
            for (int contractType = 0; contractType < 2; contractType++) {
                for (int lastWin = 0; lastWin < 2; lastWin++) {
                    gerarContrato(t_id, s_id, CONTRACT_TYPE.toEnum(contractType), lastWin == 0
                            ? null : Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
                    getLastTransictionIsBuy()[t_id][s_id] = new SimpleBooleanProperty(false);
                    getVlrTmpLoss()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
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

    public static Proposal[][][][] getProposal() {
        return proposal;
    }

    public static void setProposal(Proposal[][][][] proposal) {
        Abr.proposal = proposal;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrTmpWin() {
        return vlrTmpWin;
    }

    public static void setVlrTmpWin(ObjectProperty<BigDecimal>[][] vlrTmpWin) {
        Abr.vlrTmpWin = vlrTmpWin;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrTmpLoss() {
        return vlrTmpLoss;
    }

    public static void setVlrTmpLoss(ObjectProperty<BigDecimal>[][] vlrTmpLoss) {
        Abr.vlrTmpLoss = vlrTmpLoss;
    }
}
