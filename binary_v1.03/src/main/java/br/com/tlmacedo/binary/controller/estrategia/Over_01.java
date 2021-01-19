package br.com.tlmacedo.binary.controller.estrategia;

import br.com.tlmacedo.binary.controller.Operacao;
import br.com.tlmacedo.binary.interfaces.Constants;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;
import br.com.tlmacedo.binary.model.vo.HistoricoDeTicks;
import br.com.tlmacedo.binary.model.vo.PriceProposal;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_Mascara;
import br.com.tlmacedo.binary.services.Util_Json;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

public class Over_01 implements Estrategia {

    /**
     * Variavel basica
     */
    Operacao operacao;

    /**
     * Detalhes Informações para robo
     */
    IntegerProperty[] superiorQtd = new IntegerProperty[Operacao.getSymbolObservableList().size()];
    IntegerProperty[] inferiorQtd = new IntegerProperty[Operacao.getSymbolObservableList().size()];
    ObjectProperty<BigDecimal>[] superiorPorcentagem = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    ObjectProperty<BigDecimal>[] inferiorPorcentagem = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    IntegerProperty[] qtdRepeticaoEntrada = new IntegerProperty[Operacao.getSymbolObservableList().size()];
    IntegerProperty[] contadorRepeticaoEntrada = new IntegerProperty[Operacao.getSymbolObservableList().size()];

    IntegerProperty[] digitBarrier = new IntegerProperty[Operacao.getSymbolObservableList().size()];

    /**
     * Variaveis utilização do robo
     */
    ObjectProperty<PriceProposal>[] priceProposal = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    static ObjectProperty<Proposal>[] proposal = new ObjectProperty[Operacao.getSymbolObservableList().size()];


    public Over_01(Operacao operacao) {

        setOperacao(operacao);
        Operacao.setParamEstrategiaCarregados(false);
        estatisticasRobo();
        if (!Operacao.isParamEstrategiaCarregados()) {
            Operacao.setParamEstrategiaCarregados(carregaParametros());
        }

    }

    private boolean carregaParametros() {

        try {
//                Operacao.setStakePadrao(Operacao.getSaldoInicialConta().multiply(new BigDecimal(0.01)));
            //Service_Alert serviceAlert = new Service_Alert();


            for (int symbolId = 0; symbolId < Operacao.getSymbolObservableList().size(); symbolId++) {
                if ((Operacao.isVol1s() && symbolId < 5) || (!Operacao.isVol1s() && symbolId >= 5)) continue;
                if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) continue;
                Operacao.getFatorMartingale()[symbolId].setValue(new BigDecimal(0.));
                Operacao.getStakePadrao()[symbolId].setValue(new BigDecimal(0.35));

                getPriceProposal()[symbolId] = new SimpleObjectProperty<>();
                getProposal()[symbolId] = new SimpleObjectProperty<>();
                getDigitBarrier()[symbolId] = new SimpleIntegerProperty(5);
                getQtdRepeticaoEntrada()[symbolId] = new SimpleIntegerProperty(4);
                getContadorRepeticaoEntrada()[symbolId] = new SimpleIntegerProperty(0);
                Operacao.setParametrosNegociacao(symbolId, Operacao.getStakePadrao()[symbolId].getValue(), 1, DURATION_UNIT.t,
                        null, null, Operacao.getFatorMartingale()[symbolId].getValue(), 10, 4);
                acompanharMovimentoVolatilidade(symbolId);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    @Override
    public void definicaoDeContrato(Integer symbolId) {

        /**
         * Even = 0 e Odd = 1
         */
        if (Operacao.isParamEstrategiaCarregados()) {
            try {
                if (gerarContrato(symbolId)) {
                    String jsonPriceProposal = Util_Json
                            .getJson_from_Object(getPriceProposal()[symbolId].getValue());
                    //.replace("\"barrier\":null,", "");
                    Operacao.solicitarEnvioContrato(symbolId, jsonPriceProposal);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void acompanharMovimentoVolatilidade(Integer symbolId) {

        Operacao.getUltimoTick()[symbolId].addListener((ov, o, n) -> {
            if (n == null) return;

            if (!Operacao.getVolatilidadeCompraAutorizada()[symbolId].getValue()
                    || Operacao.getVolatilidadeNegociando()[symbolId].getValue())
                return;

            if (getQtdRepeticaoEntrada()[symbolId].getValue().compareTo(0) == 0) {
                getContadorRepeticaoEntrada()[symbolId].setValue(0);
            } else {
                if (n.getUltimoDigito().compareTo(getDigitBarrier()[symbolId].getValue()) <= 0) {
                    getContadorRepeticaoEntrada()[symbolId].setValue(getContadorRepeticaoEntrada()[symbolId].getValue() + 1);
                } else {
                    getContadorRepeticaoEntrada()[symbolId].setValue(0);
                }
            }
            if (getContadorRepeticaoEntrada()[symbolId].getValue().compareTo(getQtdRepeticaoEntrada()[symbolId].getValue()) >= 0) {
//                if (getSuperiorPorcentagem()[symbolId].getValue().compareTo(new BigDecimal(40.)) >= 0)
                solicitaCompraContrato(symbolId);
                getContadorRepeticaoEntrada()[symbolId].setValue(0);
            }
        });
        negociarNovamente(symbolId);

    }

    @Override
    public void acompanharVendaDeContrato(Integer symbolId) {

    }

    @Override
    public void negociarNovamente(Integer symbolId) {

        Operacao.negociarNovamente(symbolId, false);

    }

    @Override
    public void solicitaCompraContrato(Integer symbolId) {
        Operacao.getLastPriceProposal()[symbolId].setValue(getPriceProposal()[symbolId].getValue());
        Operacao.solicitarCompraContrato(symbolId, getProposal()[symbolId].getValue());
        System.out.printf("solicitouCompraContrato__%s\n", Operacao.getVolName()[symbolId]
                //        , getProposal()[symbolId].getValue()
        );
        getProposal()[symbolId].getValue().setId(null);

    }

    @Override
    public void pausarContinuarRobo(Integer symbolId) {

    }

    @Override
    public void stopEstrategia(Integer symbolId) {

        Operacao.getVolatilidadeCompraAutorizada()[symbolId].setValue(false);
        getProposal()[symbolId].setValue(null);
        Operacao.getRenovarTodosContratos()[symbolId].setValue(true);

    }

    @Override
    public void atualizaNovosParametros(Integer symbolId) {

        BigDecimal stkDefault = null;
        Integer qtdTick = 1, qtdRepeticaoEntrada = 4, digitBarreira = 5;

        try {
            stkDefault = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("Valor da stake padrão para %s US$:",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V"))),
                            "Valor?").alertTextField("#,##0.00", "0.35", "").get(), 2);
            qtdTick = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("Qtd de ticks para %s",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V"))),
                            "Ticks?").alertTextField("#,##0.*0", String.valueOf(qtdTick), "").get(), 0)
                    .intValue();
            qtdRepeticaoEntrada = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("qtd de repetições menor que barreira para %s: ",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V"))),
                            "Repetições?").alertTextField("#,##0.*0",
                            String.valueOf(qtdRepeticaoEntrada), "").get(), 0)
                    .intValue();
            digitBarreira = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("digito de barreira para %s:",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V"))),
                            "Digito barreira?").alertTextField("#,##0.*0",
                            String.valueOf(digitBarreira), "").get(), 0)
                    .intValue();

        } catch (Exception ex) {
            if (!(ex instanceof NoSuchElementException))
                ex.printStackTrace();
            return;
        }
        if (symbolId == null) {
            for (int finalSymbolId = 0; finalSymbolId < Operacao.getSymbolObservableList().size(); finalSymbolId++) {
                if (!Operacao.getVolatilidadeAtiva()[finalSymbolId].getValue()) continue;
                Operacao.setParametrosNegociacao(finalSymbolId, stkDefault, qtdTick, DURATION_UNIT.t,
                        null, null, Operacao.getFatorMartingale()[finalSymbolId].getValue(), 10, 4);
                getQtdRepeticaoEntrada()[finalSymbolId].setValue(qtdRepeticaoEntrada);
                getDigitBarrier()[finalSymbolId].setValue(digitBarreira);
                definicaoDeContrato(finalSymbolId);
            }
        } else {
            if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) return;
            Operacao.setParametrosNegociacao(symbolId, stkDefault, 1, DURATION_UNIT.t,
                    null, null, Operacao.getFatorMartingale()[symbolId].getValue(), 10, 4);
            getQtdRepeticaoEntrada()[symbolId].setValue(qtdRepeticaoEntrada);
            getDigitBarrier()[symbolId].setValue(digitBarreira);
            definicaoDeContrato(symbolId);
        }

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private boolean gerarContrato(Integer symbolId) {

        if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) return false;
        //???? Alert para preenchimento das informações para estrategia do robo
        try {
            getPriceProposal()[symbolId].setValue(new PriceProposal());

            getPriceProposal()[symbolId].getValue()
                    .setProposal(1);
            getPriceProposal()[symbolId].getValue()
                    .setAmount(Operacao.getStakeContrato()[symbolId].getValue()
                            .compareTo(BigDecimal.ZERO) == 0
                            ? Operacao.getStakePadrao()[symbolId].getValue().setScale(2, RoundingMode.HALF_UP)
                            : Operacao.getStakeContrato()[symbolId].getValue().setScale(2, RoundingMode.HALF_UP));
            getPriceProposal()[symbolId].getValue()
                    .setBasis(Constants.PRICE_PROPOSAL_BASIS);
            getPriceProposal()[symbolId].getValue()
                    .setBarrier(getDigitBarrier()[symbolId].getValue().toString());
            getPriceProposal()[symbolId].getValue()
                    .setContract_type(CONTRACT_TYPE.DIGITOVER);
            getPriceProposal()[symbolId].getValue()
                    .setCurrency(Operacao.authorizeProperty().getValue().getCurrency());
            getPriceProposal()[symbolId].getValue()
                    .setDuration(Operacao.getTempoDeContrato());
            getPriceProposal()[symbolId].getValue()
                    .setDuration_unit(Operacao.getDurationUnit());
            getPriceProposal()[symbolId].getValue()
                    .setSymbol(Operacao.getVolName()[symbolId]);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    private void estatisticasRobo() {

        for (int i = 0; i < Operacao.getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getSuperiorQtd()[symbolId] = new SimpleIntegerProperty(0);
            getInferiorQtd()[symbolId] = new SimpleIntegerProperty(0);
            getSuperiorPorcentagem()[symbolId] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getInferiorPorcentagem()[symbolId] = new SimpleObjectProperty<>(BigDecimal.ZERO);

            Operacao.getInfDet01()[symbolId].setValue(String.format("Over_%s", Operacao.getVolName()[symbolId]).replace("R", "V"));
            Operacao.getInfVlr01()[symbolId].bind(getSuperiorQtd()[symbolId].asString());
            Operacao.getInfPorc01()[symbolId].bind(Bindings.createStringBinding(() ->
                            getSuperiorPorcentagem()[symbolId].getValue().intValue() + "%",
                    getSuperiorPorcentagem()[symbolId]));

            Operacao.getInfDet02()[symbolId].setValue(String.format("Under_%s", Operacao.getVolName()[symbolId]).replace("R", "V"));
            Operacao.getInfVlr02()[symbolId].bind(getInferiorQtd()[symbolId].asString());
            Operacao.getInfPorc02()[symbolId].bind(Bindings.createStringBinding(() ->
                            getInferiorPorcentagem()[symbolId].getValue().intValue() + "%",
                    getInferiorPorcentagem()[symbolId]));

            final Double[] margem = new Double[1];
            Operacao.getUltimoTick()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                margem[0] = (100. / Operacao.getQtdTicksGrafico());
                getSuperiorQtd()[symbolId].setValue(Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                        .mapToInt(HistoricoDeTicks::getUltimoDigito)
                        .filter(value -> value > getDigitBarrier()[symbolId].getValue())
                        .count());
                getInferiorQtd()[symbolId].setValue(Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                        .mapToInt(HistoricoDeTicks::getUltimoDigito)
                        .filter(value -> value <= getDigitBarrier()[symbolId].getValue())
                        .count());

                getSuperiorPorcentagem()[symbolId].setValue(new BigDecimal(margem[0] * getSuperiorQtd()[symbolId].getValue()));
                getInferiorPorcentagem()[symbolId].setValue(new BigDecimal(margem[0] * getInferiorQtd()[symbolId].getValue()));
            });
        }

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public IntegerProperty[] getSuperiorQtd() {
        return superiorQtd;
    }

    public void setSuperiorQtd(IntegerProperty[] superiorQtd) {
        this.superiorQtd = superiorQtd;
    }

    public IntegerProperty[] getInferiorQtd() {
        return inferiorQtd;
    }

    public void setInferiorQtd(IntegerProperty[] inferiorQtd) {
        this.inferiorQtd = inferiorQtd;
    }

    public ObjectProperty<BigDecimal>[] getSuperiorPorcentagem() {
        return superiorPorcentagem;
    }

    public void setSuperiorPorcentagem(ObjectProperty<BigDecimal>[] superiorPorcentagem) {
        this.superiorPorcentagem = superiorPorcentagem;
    }

    public ObjectProperty<BigDecimal>[] getInferiorPorcentagem() {
        return inferiorPorcentagem;
    }

    public void setInferiorPorcentagem(ObjectProperty<BigDecimal>[] inferiorPorcentagem) {
        this.inferiorPorcentagem = inferiorPorcentagem;
    }

    public IntegerProperty[] getDigitBarrier() {
        return digitBarrier;
    }

    public void setDigitBarrier(IntegerProperty[] digitBarrier) {
        this.digitBarrier = digitBarrier;
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

    public static void setProposal(ObjectProperty<Proposal>[] proposal) {
        Over_01.proposal = proposal;
    }

    public IntegerProperty[] getQtdRepeticaoEntrada() {
        return qtdRepeticaoEntrada;
    }

    public void setQtdRepeticaoEntrada(IntegerProperty[] qtdRepeticaoEntrada) {
        this.qtdRepeticaoEntrada = qtdRepeticaoEntrada;
    }

    public IntegerProperty[] getContadorRepeticaoEntrada() {
        return contadorRepeticaoEntrada;
    }

    public void setContadorRepeticaoEntrada(IntegerProperty[] contadorRepeticaoEntrada) {
        this.contadorRepeticaoEntrada = contadorRepeticaoEntrada;
    }
}
