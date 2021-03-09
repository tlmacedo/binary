package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_NewVlrContrato;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.math.BigDecimal;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][][] proposal = new Proposal[TICK_TIME.values().length][getSymbolObservableList().size()][2][2];
    ChangeListener<? super Number> listener;

    @Override
    public void cancelarContratos() {
        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                getQtdCallOrPut()[t_id][s_id].removeListener(getListener());
            }
        }
    }

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
        qtd = Integer.valueOf(alert.alertTextField("#,##0.*0", "2", "").get()
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
                setListener((ov, o, n) -> {
                    if (n == null || isRoboMonitorandoPausado() || !isContratoGerado()) return;
                    boolean maior = Math.abs(n.intValue()) > getQtdCandlesEntrada()[finalT_id].getValue(),
                            igual = Math.abs(n.intValue()) == getQtdCandlesEntrada()[finalT_id].getValue();
                    if (maior || igual) {
                        Proposal proposal;
                        if (isFirsBuy())
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1][0];
                        else
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1]
                                    [maior ? 1 : 0];
                        if (proposal != null) {
                            getVlrStkContrato()[finalT_id][finalS_id].setValue(proposal.getAsk_price());
                            solicitarCompraContrato(proposal);
                            setFirsBuy(false);
                        }
                    }
                });
                getQtdCallOrPut()[t_id][s_id].addListener(getListener());
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

    public ChangeListener<? super Number> getListener() {
        return listener;
    }

    public void setListener(ChangeListener<? super Number> listener) {
        this.listener = listener;
    }
}
