package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.controller.WSClient;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.HistoricoDeTicks;
import br.com.tlmacedo.binary.model.vo.PriceProposal;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.ServiceAlertMensagem;
import br.com.tlmacedo.binary.services.UtilJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

public class EstrategiaOver implements Estrategia {

    private Operacoes operacoes;
    private IntegerProperty digitoBarreira;
    private ObjectProperty<PriceProposal>[] priceProposal = new ObjectProperty[Operacoes.getSymbolObservableList().size()];
    private static ObjectProperty<Proposal>[] proposal = new ObjectProperty[Operacoes.getSymbolObservableList().size()];
    private Integer[] contTicksAbaixo = new Integer[Operacoes.getSymbolObservableList().size()];

    public EstrategiaOver(Operacoes operacoes, Integer digitoBarreira) {
        setOperacoes(operacoes);
        this.digitoBarreira = new SimpleIntegerProperty(digitoBarreira);
        for (int symbolId = 0; symbolId < Operacoes.getSymbolObservableList().size(); symbolId++) {
            if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) continue;
            getOperacoes().getFatorMartingale()[symbolId] = new SimpleObjectProperty<>();
            getOperacoes().getFatorMartingale()[symbolId].setValue(new BigDecimal(2.5));
            getContTicksAbaixo()[symbolId] = 0;
            getPriceProposal()[symbolId] = new SimpleObjectProperty<>();
            getProposal()[symbolId] = new SimpleObjectProperty<>();
            //analisarTicks(symbolId);
        }
        botoesSetAction();
    }

    @Override
    public boolean definicaoDeContrato(Integer symbolId) {
        String jsonPriceProposal;
        try {
            if (gerarContrato(symbolId)) {
                jsonPriceProposal = UtilJson.getJson_From_Object(WSClient.getMapper(),
                        getPriceProposal()[symbolId].getValue());
                getOperacoes().enviarContrato(symbolId, jsonPriceProposal);
                return true;
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void botoesSetAction() {
        for (int i = 0; i < Operacoes.getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getOperacoes().carregarBotoes(symbolId, actionContrato(symbolId), "contrato");
            getOperacoes().carregarBotoes(symbolId, actionCompra(symbolId), "compra");
            getOperacoes().carregarBotoes(symbolId, actionStop(symbolId), "stop");
        }
    }

    @Override
    public boolean gerarContrato(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return false;
        if (getOperacoes().getStakeContrato()[symbolId].getValue() == null) {
            ServiceAlertMensagem alertMensagem = new ServiceAlertMensagem();
            String nameSymbol = "";
            switch (symbolId) {
                case 0 -> nameSymbol = "R_10";
                case 1 -> nameSymbol = "R_25";
                case 2 -> nameSymbol = "R_50";
                case 3 -> nameSymbol = "R_75";
                case 4 -> nameSymbol = "R_100";
            }
            try {
                alertMensagem.setCabecalho("definir stake.");
                alertMensagem.setContentText("ContentText");
                alertMensagem.setStrIco("/image/ico/black/ic_tpn_telefone_dp24.png");
                String vlr = alertMensagem.alertTextField("##0.00",
                        getOperacoes().getTxtValorStake().getText(),
                        String.format("Valor da stake para volatilidade %s ?", nameSymbol)).get();
                getOperacoes().getStakePadrao()[symbolId].setValue(new BigDecimal(vlr));
                getOperacoes().getStakeContrato()[symbolId]
                        .setValue(getOperacoes().getStakePadrao()[symbolId].getValue());
                alertMensagem = new ServiceAlertMensagem();
                String qtd = alertMensagem.alertTextField("#0",
                        getOperacoes().getTxtQtdRepete().getText(),
                        String.format("qtd repetições para volatilidade %s ?", nameSymbol)).get();
                getOperacoes().getQtdRepeticoes()[symbolId].setValue(Integer.valueOf(qtd.replace("\\D", "")));
            } catch (Exception ex) {
                if (ex instanceof NoSuchElementException)
                    return false;
                else
                    ex.printStackTrace();
            }
        }
        getPriceProposal()[symbolId].setValue(new PriceProposal());
        getPriceProposal()[symbolId].getValue().setProposal(1);
        if (getOperacoes().getStakeContrato()[symbolId].getValue()
                .compareTo(BigDecimal.ZERO) == 0)
            getOperacoes().getStakeContrato()[symbolId]
                    .setValue(new BigDecimal(getOperacoes().getTxtValorStake().getText()));
        getPriceProposal()[symbolId].getValue()
                .setAmount(getOperacoes().getStakeContrato()[symbolId].getValue()
                        .setScale(2, RoundingMode.HALF_UP));
        getPriceProposal()[symbolId].getValue()
                .setBarrier(digitoBarreiraProperty().getValue().toString());
        getPriceProposal()[symbolId].getValue()
                .setBasis("stake");
        getPriceProposal()[symbolId].getValue()
                .setContract_type(CONTRACT_TYPE.DIGITOVER);
        getPriceProposal()[symbolId].getValue()
                .setCurrency("USD");
        getPriceProposal()[symbolId].getValue()
                .setDuration(Integer.parseInt(getOperacoes().getTxtDuracaoTicks().getText()
                        .replaceAll("\\D", "")));
        getPriceProposal()[symbolId].getValue()
                .setDuration_unit(getOperacoes().getCboDuracaoTipo().getSelectionModel().getSelectedItem());
        getPriceProposal()[symbolId].getValue()
                .setSymbol(Operacoes.VOL_NAME[symbolId]);
        return true;
    }

    @Override
    public void acompanhaCompraDeContrato(Integer symbolId) {
        resultadoNegociarNovamente(symbolId);
        Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId]
                .addListener((ListChangeListener<? super HistoricoDeTicks>) c -> {
                    if (!getOperacoes().getCompraAutorizada()[symbolId].getValue()
                            || getOperacoes().getVolatilidadeEmNegociacao()[symbolId].getValue())
                        return;

                    while (c.next()) {
                        for (HistoricoDeTicks ticks : c.getAddedSubList()) {
                            if (ticks.getUltimoDigito() <= digitoBarreiraProperty().getValue()) {
                                contTicksAbaixo[symbolId]++;
                            } else {
                                if (contTicksAbaixo[symbolId] > 0) {
                                    contTicksAbaixo[symbolId] = 0;
                                }
                            }
                            if (contTicksAbaixo[symbolId] >= getOperacoes().getQtdRepeticoes()[symbolId].getValue()) {
                                disparaOrdemCompra(symbolId);
                                getContTicksAbaixo()[symbolId] = 0;
                            }
                        }
                    }
                });
    }

    @Override
    public void acompanhaVendaDeContratoSeDisponivel(Integer symbolId) {

    }

    @Override
    public void resultadoNegociarNovamente(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return;
        getOperacoes().getVolatilidadeEmNegociacao()[symbolId].addListener((ov, o, n) -> {
            if (!o || n) return;
            if (!n) {
                if (Operacoes.isCompraAutorizadaGeral())
                    if (Operacoes.isVolatilidadeEmNegociacaoGeral())
                        Operacoes.volatilidadeEmNegociacaoGeralProperty().setValue(false);
                getOperacoes().atualizaStakesNegociacao(symbolId, false);
                definicaoDeContrato(symbolId);
            }
        });
    }

    @Override
    public void disparaOrdemCompra(Integer symbolId) {
        switch (symbolId) {
            case 0 -> getOperacoes().getBtnComprar_R10().fire();
            case 1 -> getOperacoes().getBtnComprar_R25().fire();
            case 2 -> getOperacoes().getBtnComprar_R50().fire();
            case 3 -> getOperacoes().getBtnComprar_R75().fire();
            case 4 -> getOperacoes().getBtnComprar_R100().fire();
        }
    }

    @Override
    public EventHandler actionContrato(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return null;
        EventHandler<ActionEvent> actionEventContratoVolatilidade = event -> {
            if (getProposal()[symbolId].getValue() != null && getOperacoes().getStakePadrao()[symbolId].getValue().compareTo(BigDecimal.ZERO) != 0)
                getOperacoes().getStakeContrato()[symbolId].setValue(getOperacoes().getStakePadrao()[symbolId].getValue());
            if (definicaoDeContrato(symbolId)) {
                acompanhaCompraDeContrato(symbolId);
                getOperacoes().ativarBotoesVolatilidade(symbolId, true);
            }
        };
        return actionEventContratoVolatilidade;
    }

    @Override
    public EventHandler actionCompra(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return null;
        EventHandler<ActionEvent> actionEventComprarVolatilidade = event -> {
            Operacoes.getLastPriceProposal()[symbolId]
                    .setValue(getPriceProposal()[symbolId].getValue());
            getOperacoes().comprarContrato(symbolId, getProposal()[symbolId].getValue());
        };
        return actionEventComprarVolatilidade;
    }

    @Override
    public EventHandler actionStop(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return null;
        EventHandler<ActionEvent> actionEventStopVolatilidade = event -> {
            getOperacoes().getCompraAutorizada()[symbolId].setValue(false);
            getProposal()[symbolId].setValue(null);
            getOperacoes().getStakePadrao()[symbolId] = new SimpleObjectProperty<>();
            getOperacoes().getStakeContrato()[symbolId] = new SimpleObjectProperty<>();
            getOperacoes().ativarBotoesVolatilidade(symbolId, false);
        };
        return actionEventStopVolatilidade;
    }

    private int analisarTicks(Integer symbolId) {
        ObservableList<Integer> listSeq = FXCollections.observableArrayList();
        final int[] contador = {0}, win = {0}, loss = {0};
        for (int i = 0; i < 10; i++) {
            listSeq.add(0);
            win[i] = 0;
        }
        Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId].stream()
                .mapToInt(HistoricoDeTicks::getUltimoDigito)
                .forEach(value -> {
//                    if (symbolId == 0)
//                        System.out.printf("ultDigit:[%s]\n", value);
                    if (value <= 2) {
                        if (contador[0] == 2)
                            loss[0]++;
                        contador[0]++;
                    } else {
                        if (contador[0] > 1) {
                            int index = contador[0] - 1;
                            int tmp = listSeq.get(index) + 1;
                            listSeq.set(index, tmp);
                        }
                        contador[0] = 0;
                    }
                });
        return 0;
    }

    public Operacoes getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(Operacoes operacoes) {
        this.operacoes = operacoes;
    }

    public int getDigitoBarreira() {
        return digitoBarreira.get();
    }

    public IntegerProperty digitoBarreiraProperty() {
        return digitoBarreira;
    }

    public void setDigitoBarreira(int digitoBarreira) {
        this.digitoBarreira.set(digitoBarreira);
    }

    public ObjectProperty<PriceProposal>[] getPriceProposal() {
        return priceProposal;
    }

    public void setPriceProposal(ObjectProperty<PriceProposal>[] priceProposal) {
        this.priceProposal = priceProposal;
    }

    public static ObjectProperty<Proposal>[] getProposal() {
        return proposal;
    }

    public void setProposal(ObjectProperty<Proposal>[] proposal) {
        this.proposal = proposal;
    }

    public Integer[] getContTicksAbaixo() {
        return contTicksAbaixo;
    }

    public void setContTicksAbaixo(Integer[] contTicksAbaixo) {
        this.contTicksAbaixo = contTicksAbaixo;
    }
}
