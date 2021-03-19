package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.HistoricoDeCandles;
import br.com.tlmacedo.binary.model.vo.Passthrough;
import br.com.tlmacedo.binary.model.vo.PriceProposal;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_NewVlrContrato;
import br.com.tlmacedo.binary.services.Service_TelegramNotifier;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ButtonType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

import static br.com.tlmacedo.binary.interfaces.Constants.PRICE_PROPOSAL_BASIS;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][] proposal;

    @Override
    public boolean variaveisIniciais() {

        try {
            setQtdContractsProposal(3);
            setQtdLossPause(2);
            setTypeContract(new ObjectProperty[]{new SimpleObjectProperty<>(CONTRACT_TYPE.CALL), new SimpleObjectProperty(CONTRACT_TYPE.PUT)});
            setProposal(new Proposal[getTimeFrameObservableList().size()][getSymbolObservableList().size()][getQtdContractsProposal()]);

            for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
                if (!getTimeAtivo()[t_id].getValue()) continue;
                getVlrStkPadrao()[t_id] = new SimpleObjectProperty<>();
                getQtdCandlesEntrada()[t_id] = new SimpleIntegerProperty();
                getPorcMartingale()[t_id] = new SimpleObjectProperty<>();
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    getVlrStkContrato()[t_id][s_id] = new SimpleObjectProperty<>();
                    getVlrLossAcumulado()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                    getSymbolLossPaused()[t_id][s_id] = new SimpleBooleanProperty(false);
                    getFirstBuy()[t_id][s_id] = new SimpleBooleanProperty(true);
                    getFirstContratoGerado()[t_id][s_id] = new SimpleBooleanProperty(false);
                    getQtdLossSymbol()[t_id][s_id] = new SimpleIntegerProperty(0);
                    for (int proposal_id = 0; proposal_id < getQtdContractsProposal(); proposal_id++) {
                        getProposal()[t_id][s_id][proposal_id] = new Proposal();
                    }
                }
            }
            escutarVariaveis();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public boolean definicaoDeContrato() throws Exception {

        if (!variaveisIniciais()) return false;

        try {
            Service_Alert alert = new Service_Alert("configuração",
                    "Todos frames terão a mesma configuração?", "");

            String msgParamRobo = "Robô: %s\n";
            String msgParamTime = "TimeFrame: %s\n";
            String msgParam = "vlrStake: %s%s\twaitCandles: %s\tmartingale: %s%%";

            BigDecimal vlr[] = new BigDecimal[getTimeFrameObservableList().size()];
            BigDecimal martingale[] = new BigDecimal[getTimeFrameObservableList().size()];
            Integer qtd[] = new Integer[getTimeFrameObservableList().size()];

            boolean retIsEqualsConfig = alert.alertYesNo().get().equals(ButtonType.YES);
            try {
                for (int t_id = 0; t_id < (retIsEqualsConfig ? 1 : getTimeFrameObservableList().size()); t_id++) {
                    if (!getTimeAtivo()[t_id].get()) continue;
                    qtd[t_id] = 0;

                    alert = new Service_Alert();
                    alert.setTitulo("vlr stake.");
                    alert.setCabecalho(String.format("Stake %s", getAuthorize().getCurrency()));
                    alert.setContentText(String.format("Qual o valor da stake padrão para operações%s?",
                            !retIsEqualsConfig ? String.format(" [%s]", getTimeFrameObservableList().get(t_id)) : ""));
                    vlr[t_id] = new BigDecimal(alert.alertTextField("#,##0.00*35", "0.35", "").get());


                    while (qtd[t_id] < 2) {
                        alert = new Service_Alert();
                        alert.setCabecalho(String.format("Espera quantas candles seguidas em pull ou call%s?",
                                !retIsEqualsConfig ? String.format(" [%s]", getTimeFrameObservableList().get(t_id)) : ""));
                        qtd[t_id] = Integer.valueOf(alert.alertTextField("#,##0.*2", "10", "").get()
                                .replaceAll("\\D", ""));
                    }

                    alert = new Service_Alert();
                    alert.setCabecalho(String.format("Qual a porcentagem do martingale em cima do loss acumulado?",
                            !retIsEqualsConfig ? String.format(" [%s]", getTimeFrameObservableList().get(t_id)) : ""));
                    martingale[t_id] = new BigDecimal(alert.alertTextField("#,##0.00", "100.00", "").get());
                }
            } catch (Exception ex) {
                if (ex instanceof NoSuchElementException)
                    return false;
            }
            for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
                if (!getTimeAtivo()[t_id].getValue()) continue;
                getVlrStkPadrao()[t_id].setValue(vlr[retIsEqualsConfig ? 0 : t_id]);
                getQtdCandlesEntrada()[t_id].setValue(qtd[retIsEqualsConfig ? 0 : t_id]);
                getPorcMartingale()[t_id].setValue(martingale[retIsEqualsConfig ? 0 : t_id]);
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    getVlrStkContrato()[t_id][s_id]
                            .setValue(getVlrStkPadrao()[t_id].getValue());
                }
            }

            if (retIsEqualsConfig)
                msgParamRobo = msgParamRobo.replace("\n", "\t");
            StringBuilder stbParametros = new StringBuilder(String.format(msgParamRobo, this.getClass().getSimpleName()));
            for (int t_id = 0; t_id < (retIsEqualsConfig ? 1 : getTimeFrameObservableList().size()); t_id++) {
                stbParametros.append(String.format(msgParamTime, retIsEqualsConfig ? "Todas" : getTimeFrameObservableList().get(t_id)));
                stbParametros.append(String.format(msgParam, vlr[t_id], getAuthorize().getCurrency(), qtd[t_id],
                        martingale[t_id]));
            }

            setParametrosUtilizadosRobo(stbParametros.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public void gerarContratosPendentes(Integer ft_id, Integer fs_id) {

        try {
            for (int t_id = (ft_id != null ? ft_id : 0); t_id <= (ft_id != null ? ft_id : getTimeFrameObservableList().size() - 1); t_id++) {
                if (!getTimeAtivo()[t_id].getValue()) continue;
                for (int s_id = (fs_id != null ? fs_id : 0); s_id <= (fs_id != null ? fs_id : getSymbolObservableList().size() - 1); s_id++) {
                    if (!getFirstContratoGerado()[t_id][s_id].getValue()
                            && Math.abs(getQtdCallOrPut()[t_id][s_id].intValue()) >= getQtdCandlesEntrada()[t_id].getValue() - 1) {
                        for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++)
                            gerarContrato(t_id, s_id, typeContract_id, null, typeContract_id);
                        getFirstContratoGerado()[t_id][s_id].setValue(true);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void monitorarCondicoesParaComprar() throws Exception {

        gerarContratosPendentes(null, null);

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getQtdCallOrPut()[t_id][s_id].addListener((ov, o, n) -> {
                    int qtdCandlesAbsoluto = Math.abs(n.intValue());
                    boolean maior = qtdCandlesAbsoluto > getQtdCandlesEntrada()[finalT_id].getValue(),
                            igual = qtdCandlesAbsoluto == getQtdCandlesEntrada()[finalT_id].getValue();
                    if (Math.abs(n.intValue()) == 1
                            && getSymbolLossPaused()[finalT_id][finalS_id].getValue()) {
                        getSymbolLossPaused()[finalT_id][finalS_id].setValue(false);
                        getQtdLossSymbol()[finalT_id][finalS_id].setValue(0);
                    }
                    if (getSymbolLossPaused()[finalT_id][finalS_id].getValue()) return;
                    if (getFirstBuy()[finalT_id][finalS_id].getValue()
                            && qtdCandlesAbsoluto >= getQtdCandlesEntrada()[finalT_id].getValue() - 1)
                        gerarContratosPendentes(finalT_id, finalS_id);
                    if (n == null || isRoboMonitorandoPausado() || !isContratoGerado()) return;
                    if (maior || igual) {
                        Proposal proposal;
                        int proposal_id = (maior
                                ? (!getFirstBuy()[finalT_id][finalS_id].getValue() ? 2 : (n.intValue() < 0 ? 0 : 1))
                                : (n.intValue() < 0 ? 0 : 1));
                        proposal = getProposal()[finalT_id][finalS_id][proposal_id];
//                        Passthrough passthrough = new Passthrough(finalT_id, finalS_id, getTypeCandle_id(), (n.intValue() < 0 ? 0 : 1),
//                                proposal_id, "");
                        if (proposal != null) {
                            getVlrStkContrato()[finalT_id][finalS_id].setValue(proposal.getAsk_price());
                            getFirstBuy()[finalT_id][finalS_id].setValue(false);
                            solicitarCompraContrato(proposal);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void gerarNovosContratos(int t_id, int s_id, Integer typeContract, Integer proposal_id) {

        try {
            cancelarContratosNaoUsados(t_id, s_id);

            if (typeContract != null) {
                gerarContrato(t_id, s_id, typeContract,
                        Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id), proposal_id);
            } else {
                if (proposal_id < getTypeContract().length || getSymbolLossPaused()[t_id][s_id].getValue()) {
                    for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++) {
                        gerarContrato(t_id, s_id, getTypeContract()[typeContract_id].getValue().getCod(),
                                proposal_id >= getTypeContract().length
                                        ? Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id) : null, typeContract_id);
                    }
                }
            }
//            if (typeContract == null && winLoss == null) {
//                for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++)
//                    for (int winLoss_id = 0; winLoss_id < getWin_Loss().length; winLoss_id++)
//                        gerarContrato(t_id, s_id, typeContract_id, winLoss_id == 0
//                                ? null : Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id));
//            } else if (typeContract == null) {
//                for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++)
//                    gerarContrato(t_id, s_id, typeContract_id, winLoss
//                            ? null : Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id));
//            } else {
//                for (int typeContract_id = 0; typeContract_id < getTypeContract().length; typeContract_id++)
//                    if (getTypeContract()[typeContract_id] == typeContract)
//                        gerarContrato(t_id, s_id, typeContract_id, winLoss
//                                ? null : Service_NewVlrContrato.getVlrTmpLoss(t_id, s_id));
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void cancelarContratosNaoUsados(int t_id, int s_id) {
        for (int proposal_id = 0; proposal_id < getQtdContractsProposal(); proposal_id++) {
            //solicitarCancelContrato(getProposal()[t_id][s_id][proposal_id]);
            getProposal()[t_id][s_id][proposal_id] = null;
        }
    }

    public void escutarVariaveis() {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getQtdLossSymbol()[t_id][s_id].addListener((ov, o, n) -> {
                    if (n == null)
                        getSymbolLossPaused()[finalT_id][finalS_id].setValue(false);
                    if (n.intValue() >= getQtdLossPause())
                        getSymbolLossPaused()[finalT_id][finalS_id].setValue(true);
                });
                getSymbolLossPaused()[t_id][s_id].addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (!o && n) {
                            Operacoes.getRobo().gerarNovosContratos(finalT_id, finalS_id,
                                    null, 2);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
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
