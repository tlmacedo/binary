package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.controller.WSClient;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.PriceProposal;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.ServiceAlertMensagem;
import br.com.tlmacedo.binary.services.UtilJson;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Random;

public class EstrategiaDiff0 implements Estrategia {

    private BigDecimal fatorMg = new BigDecimal(10.98);
    private ObjectProperty<BigDecimal>[] prejuizoAcumulado = new ObjectProperty[Operacoes.getSymbolObservableList().size()];
    private static IntegerProperty qtdContratos = new SimpleIntegerProperty(10);
    private IntegerProperty digito = new SimpleIntegerProperty(0);
    private Operacoes operacoes;
    private Random ran = new Random();

    private ObjectProperty<PriceProposal>[][] priceProposal = new ObjectProperty[Operacoes.getSymbolObservableList().size()][getQtdContratos()];
    private static ObjectProperty<Proposal>[][] proposal = new ObjectProperty[Operacoes.getSymbolObservableList().size()][getQtdContratos()];
    private Integer[] contadorRepeticoes = new Integer[Operacoes.getSymbolObservableList().size()];

    public EstrategiaDiff0(Operacoes operacoes) {
        setOperacoes(operacoes);
        for (int symbolId = 0; symbolId < getOperacoes().getSymbolObservableList().size(); symbolId++) {
            if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) continue;
            getContadorRepeticoes()[symbolId] = 0;
            getPrejuizoAcumulado()[symbolId] = new SimpleObjectProperty(BigDecimal.ZERO);
            getOperacoes().getFatorMartingale()[symbolId] = new SimpleObjectProperty<>();
            getOperacoes().getFatorMartingale()[symbolId].setValue(getFatorMg());
            for (int j = 0; j < getQtdContratos(); j++) {
                getPriceProposal()[symbolId][j] = new SimpleObjectProperty<>();
                getProposal()[symbolId][j] = new SimpleObjectProperty<>();
            }
        }
        botoesSetAction();
    }

    @Override
    public boolean definicaoDeContrato(Integer symbolId) {
        try {
            getContadorRepeticoes()[symbolId] = Integer.valueOf(getOperacoes().getTxtQtdRepete().getText());
            if (getContadorRepeticoes()[symbolId].compareTo(0) <= 0) {
                setFatorMg(new BigDecimal(1.));
                getOperacoes().getFatorMartingale()[symbolId].setValue(getFatorMg());
            }

            int ini = 0, size = getQtdContratos();
            if (Operacoes.getLastPriceProposal()[symbolId].getValue() != null
                    && !Operacoes.getRenovarTodosContratos()[symbolId].getValue()) {
                ini = Integer.valueOf(Operacoes.getLastPriceProposal()[symbolId].getValue().getBarrier());
                size = ini + 1;
            }
            for (int i = ini; i < size; i++) {
                setDigito(i);
                if (gerarContrato(symbolId)) {
                    String jsonPriceProposal = UtilJson.getJson_From_Object(WSClient.getMapper(), getPriceProposal()[symbolId][getDigito()].getValue());
                    getOperacoes().enviarContrato(symbolId, jsonPriceProposal);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
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
        getPriceProposal()[symbolId][getDigito()].setValue(new PriceProposal());
        getPriceProposal()[symbolId][getDigito()].getValue().setProposal(1);
        if (getOperacoes().getStakeContrato()[symbolId].getValue()
                .compareTo(BigDecimal.ZERO) == 0)
            getOperacoes().getStakeContrato()[symbolId]
                    .setValue(new BigDecimal(getOperacoes().getTxtValorStake().getText()));
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setAmount(getOperacoes().getStakeContrato()[symbolId].getValue()
                        .setScale(2, RoundingMode.HALF_UP));
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setBarrier(String.valueOf(getDigito()));
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setBasis("stake");
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setContract_type(CONTRACT_TYPE.DIGITDIFF);
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setCurrency("USD");
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setDuration(Integer.parseInt(getOperacoes().getTxtDuracaoTicks().getText()
                        .replaceAll("\\D", "")));
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setDuration_unit(getOperacoes().getCboDuracaoTipo().getSelectionModel().getSelectedItem());
        getPriceProposal()[symbolId][getDigito()].getValue()
                .setSymbol(Operacoes.VOL_NAME[symbolId]);
        return true;
    }

    @Override
    public void acompanhaCompraDeContrato(Integer symbolId) {
        resultadoNegociarNovamente(symbolId);

        Operacoes.getUltimoTick()[symbolId].addListener((ov, o, n) -> {
            if (n == null) return;
            if (!getOperacoes().getCompraAutorizada()[symbolId].getValue()
                    || getOperacoes().getVolatilidadeEmNegociacao()[symbolId].getValue())
                return;
            if (Operacoes.getQtdRepeticoes()[symbolId].getValue().compareTo(0) == 0) {

                setDigito(Operacoes.getListMenorQtdDigito()[symbolId].get(
                        getRan().nextInt(Operacoes.getListMenorQtdDigito()[symbolId].size())
                ));
                disparaOrdemCompra(symbolId);
            } else {
                if (Operacoes.getListMenorQtdDigito()[symbolId].stream()
                        .anyMatch(integer -> integer.compareTo(n.getUltimoDigito()) == 0)) {
                    getContadorRepeticoes()[symbolId]++;
                } else {
                    if (getContadorRepeticoes()[symbolId] > 0)
                        getContadorRepeticoes()[symbolId] = 0;
                }
                if (Math.abs(getContadorRepeticoes()[symbolId]) >= Operacoes.getQtdRepeticoes()[symbolId].getValue()) {
                    setDigito(n.getUltimoDigito());
                    disparaOrdemCompra(symbolId);
                    getContadorRepeticoes()[symbolId] = 0;
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
                if (getContadorRepeticoes()[symbolId].compareTo(0) <= 0) {
                    if (getPrejuizoAcumulado()[symbolId].getValue().compareTo(BigDecimal.ZERO) <= 0)
                        getPrejuizoAcumulado()[symbolId].setValue(getPrejuizoAcumulado()[symbolId].getValue()
                                .add(getOperacoes().getLucroPerdaUltimaTransacao(symbolId)));

                    if (getPrejuizoAcumulado()[symbolId].getValue().compareTo(BigDecimal.ZERO) < 0) {
                        getOperacoes().atualizaStakesNegociacao(symbolId, true);
                    } else {
                        getPrejuizoAcumulado()[symbolId].setValue(BigDecimal.ZERO);
                        getOperacoes().atualizaStakesNegociacao(symbolId, false);
                    }
                } else {
                    getOperacoes().atualizaStakesNegociacao(symbolId, false);
                }
                definicaoDeContrato(symbolId);
            }
        });
    }

    @Override
    public void botoesSetAction() {
        for (int symbolId = 0; symbolId < Operacoes.getSymbolObservableList().size(); symbolId++) {
            getOperacoes().carregarBotoes(symbolId, actionContrato(symbolId), "contrato");
            getOperacoes().carregarBotoes(symbolId, actionCompra(symbolId), "compra");
            getOperacoes().carregarBotoes(symbolId, actionStop(symbolId), "stop");
        }
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
            if (Operacoes.isCompraAutorizadaGeral()) {
                if (Operacoes.isVolatilidadeEmNegociacaoGeral())
                    return;
                else
                    Operacoes.volatilidadeEmNegociacaoGeralProperty().setValue(true);
            }
            Operacoes.getLastPriceProposal()[symbolId]
                    .setValue(getPriceProposal()[symbolId][getDigito()].getValue());
            getOperacoes().comprarContrato(symbolId, getProposal()[symbolId][getDigito()].getValue());
        };
        return actionEventComprarVolatilidade;
    }

    @Override
    public EventHandler actionStop(Integer symbolId) {
        if (!getOperacoes().getVolatilidadeAtivada()[symbolId].getValue()) return null;
        EventHandler<ActionEvent> actionEventStopVolatilidade = event -> {
            getOperacoes().getCompraAutorizada()[symbolId].setValue(false);
            getProposal()[symbolId][0].setValue(null);
            getProposal()[symbolId][1].setValue(null);
            getOperacoes().getStakePadrao()[symbolId].setValue(null);
            getOperacoes().getStakeContrato()[symbolId].setValue(null);
            getOperacoes().ativarBotoesVolatilidade(symbolId, false);
        };
        return actionEventStopVolatilidade;
    }

    public BigDecimal getFatorMg() {
        return fatorMg;
    }

    public void setFatorMg(BigDecimal fatorMg) {
        this.fatorMg = fatorMg;
    }

    public static int getQtdContratos() {
        return qtdContratos.get();
    }

    public IntegerProperty qtdContratosProperty() {
        return qtdContratos;
    }

    public void setQtdContratos(int qtdContratos) {
        this.qtdContratos.set(qtdContratos);
    }

    public int getDigito() {
        return digito.get();
    }

    public IntegerProperty digitoProperty() {
        return digito;
    }

    public void setDigito(int digito) {
        this.digito.set(digito);
    }

    public Operacoes getOperacoes() {
        return operacoes;
    }

    public void setOperacoes(Operacoes operacoes) {
        this.operacoes = operacoes;
    }

    public ObjectProperty<PriceProposal>[][] getPriceProposal() {
        return priceProposal;
    }

    public void setPriceProposal(ObjectProperty<PriceProposal>[][] priceProposal) {
        this.priceProposal = priceProposal;
    }

    public static ObjectProperty<Proposal>[][] getProposal() {
        return proposal;
    }

    public static void setProposal(ObjectProperty<Proposal>[][] proposal) {
        EstrategiaDiff0.proposal = proposal;
    }

    public Integer[] getContadorRepeticoes() {
        return contadorRepeticoes;
    }

    public void setContadorRepeticoes(Integer[] contadorRepeticoes) {
        this.contadorRepeticoes = contadorRepeticoes;
    }

    public ObjectProperty<BigDecimal>[] getPrejuizoAcumulado() {
        return prejuizoAcumulado;
    }

    public void setPrejuizoAcumulado(ObjectProperty<BigDecimal>[] prejuizoAcumulado) {
        this.prejuizoAcumulado = prejuizoAcumulado;
    }

    public Random getRan() {
        return ran;
    }

    public void setRan(Random ran) {
        this.ran = ran;
    }
}
