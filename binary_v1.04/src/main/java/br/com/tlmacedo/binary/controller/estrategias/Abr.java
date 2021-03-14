package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_NewVlrContrato;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.math.BigDecimal;

public class Abr extends Operacoes implements Robo {

    static CONTRACT_TYPE[] typeContract = new CONTRACT_TYPE[]{CONTRACT_TYPE.CALL, CONTRACT_TYPE.PUT};
    static Boolean[] Win_Loss = new Boolean[]{true, false};
    static Proposal[][][][] proposal = new Proposal[getTimeFrameObservableList().size()]
            [getSymbolObservableList().size()][getTypeContract().length][getWin_Loss().length];
    ChangeListener<? super Number> listener;

    @Override
    public void cancelarContratos() {
        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                getQtdCallOrPut()[t_id][s_id].removeListener(getListener());
            }
        }
    }

    @Override
    public void definicaoDeContrato() throws Exception {

        Service_Alert alert;
        BigDecimal vlr[] = new BigDecimal[getTimeFrameObservableList().size()],
                martingale[] = new BigDecimal[getTimeFrameObservableList().size()];
        Integer qtd[] = new Integer[getTimeFrameObservableList().size()];
//        BigDecimal vlr, martingale;
//        Integer qtd;

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (!getTimeAtivo()[t_id].get()) continue;
            alert = new Service_Alert();
            alert.setTitulo("vlr stake.");
            alert.setCabecalho(String.format("Stake %s", getAuthorize().getCurrency()));
            alert.setContentText(String.format("Qual o valor da stake padrão para operações [%s]?", getTimeFrameObservableList().get(t_id)));
            vlr[t_id] = new BigDecimal(alert.alertTextField("#,##0.00", "0.35", "").get());

            alert = new Service_Alert();
            alert.setCabecalho(String.format("Espera quantas candles seguidas em pull ou call [%s]?", getTimeFrameObservableList().get(t_id)));
            qtd[t_id] = Integer.valueOf(alert.alertTextField("#,##0.*0", "2", "").get()
                    .replaceAll("\\D", ""));

            alert = new Service_Alert();
            alert.setCabecalho(String.format("Qual a porcentagem do martingale em cima do loss acumulado [%s]?", getTimeFrameObservableList().get(t_id)));
            martingale[t_id] = new BigDecimal(alert.alertTextField("#,##0.00", "100.00", "").get());
        }

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            getVlrStkPadrao()[t_id].setValue(vlr[t_id]);
            getQtdCandlesEntrada()[t_id].setValue(qtd[t_id]);
            getPorcMartingale()[t_id].setValue(martingale[t_id]);
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                getVlrStkContrato()[t_id][s_id]
                        .setValue(getVlrStkPadrao()[t_id].getValue());
                for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++) {
                    for (int winLoss_id = 0; winLoss_id < getWin_Loss().length; winLoss_id++) {
                        getProposal()[t_id][s_id][typeContract_id][winLoss_id] = new Proposal();
                        if (winLoss_id == 0)
                            gerarContrato(t_id, s_id, typeContract_id, null);
                    }
                }
            }
        }

        setParametrosUtilizadosRobo(String.format("Robô: %s\nvlr_Stake: %s %s\tqtd_Candles: %s\tmart: %s%%",
                getRobo().getClass().getSimpleName(), getAuthorize().getCurrency(), vlr[0], qtd[0], martingale[0]));

    }

    @Override
    public void monitorarCondicoesParaComprar() throws Exception {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getQtdCallOrPut()[t_id][s_id].addListener((ov, o, n) -> {
                    if (n == null || isRoboMonitorandoPausado() || !isContratoGerado()) return;
                    boolean maior = Math.abs(n.intValue()) > getQtdCandlesEntrada()[finalT_id].getValue(),
                            igual = Math.abs(n.intValue()) == getQtdCandlesEntrada()[finalT_id].getValue();
                    if (maior || igual) {
                        Proposal proposal;
                        if (getFirstBuy()[finalT_id][finalS_id].getValue()) {
                            System.out.printf("isFirstBuy[%s][%s]:n[%s]\n", finalT_id, finalS_id,
                                    n.intValue());
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1][0];
                        } else {
                            System.out.printf("NoFirstBuy[%s][%s]: n[%s] maior[%s]\n", finalT_id, finalS_id,
                                    n.intValue(), maior);
                            proposal = getProposal()[finalT_id][finalS_id][n.intValue() < 0 ? 0 : 1]
                                    [maior ? 1 : 0];
                        }
                        if (proposal != null) {
                            System.out.printf("buyProposal: %s\n", proposal);
                            getVlrStkContrato()[finalT_id][finalS_id].setValue(proposal.getAsk_price());
                            solicitarCompraContrato(proposal);
                            getFirstBuy()[finalT_id][finalS_id].setValue(false);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void gerarNovosContratos(int t_id, int s_id) {

        try {
            for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++) {
                for (int winLoss_id = 0; winLoss_id < getWin_Loss().length; winLoss_id++) {
                    gerarContrato(t_id, s_id, typeContract_id, winLoss_id == 0
                            ? null : Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Abr() {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {

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

    public static CONTRACT_TYPE[] getTypeContract() {
        return typeContract;
    }

    public static void setTypeContract(CONTRACT_TYPE[] typeContract) {
        Abr.typeContract = typeContract;
    }

    public static Boolean[] getWin_Loss() {
        return Win_Loss;
    }

    public static void setWin_Loss(Boolean[] win_Loss) {
        Win_Loss = win_Loss;
    }
}
