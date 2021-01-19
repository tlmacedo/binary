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

public class EvenOdd_01_Porcentagem implements Estrategia {

    /**
     * Variavel basica
     */
    Operacao operacao;

    /**
     * Detalhes Informações para robo
     */
    IntegerProperty[] paresQtd = new IntegerProperty[Operacao.getSymbolObservableList().size()];
    IntegerProperty[] imparesQtd = new IntegerProperty[Operacao.getSymbolObservableList().size()];
    ObjectProperty<BigDecimal>[] paresPorcentagem = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    ObjectProperty<BigDecimal>[] imparesPorcentagem = new ObjectProperty[Operacao.getSymbolObservableList().size()];

    /**
     * Variaveis utilização do robo
     */
    IntegerProperty digito = new SimpleIntegerProperty();
    static Integer qtdContratos = 2;
    ObjectProperty<PriceProposal>[][] priceProposal = new ObjectProperty[Operacao.getSymbolObservableList().size()][getQtdContratos()];
    static ObjectProperty<Proposal>[][] proposal = new ObjectProperty[Operacao.getSymbolObservableList().size()][getQtdContratos()];
    IntegerProperty digitoEntradaPorcentagem = new SimpleIntegerProperty();


    IntegerProperty[] contadorVirtual = new IntegerProperty[Operacao.getSymbolObservableList().size()];


    public EvenOdd_01_Porcentagem(Operacao operacao) {

        setOperacao(operacao);
        Operacao.setParamEstrategiaCarregados(false);
        estatisticasRobo();
        if (!Operacao.isParamEstrategiaCarregados()) {
            Operacao.setParamEstrategiaCarregados(carregaParametros());
        }

    }

    private boolean carregaParametros() {

        try {
            setDigitoEntradaPorcentagem(65);

            for (int symbolId = 0; symbolId < Operacao.getSymbolObservableList().size(); symbolId++) {
                Operacao.setParametrosNegociacao(symbolId, new BigDecimal(0.35), 1, DURATION_UNIT.t,
                        null, null, new BigDecimal(0.), 5, null);
                if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) continue;
                for (int contrato = 0; contrato < getQtdContratos(); contrato++) {
                    getPriceProposal()[symbolId][contrato] = new SimpleObjectProperty<>();
                    getProposal()[symbolId][contrato] = new SimpleObjectProperty<>();
                }
                getContadorVirtual()[symbolId] = new SimpleIntegerProperty(0);
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
                int ini = 0, size = getQtdContratos();
                if (Operacao.getLastPriceProposal()[symbolId].getValue() != null
                        && !Operacao.getRenovarTodosContratos()[symbolId].getValue()) {
                    if (Operacao.getLastPriceProposal()[symbolId].getValue().getContract_type().equals(CONTRACT_TYPE.DIGITEVEN))
                        size = 1;
                    else
                        ini = 1;
                }
                for (int i = ini; i < size; i++) {
                    setDigito(i);
                    if (gerarContrato(symbolId)) {
                        String jsonPriceProposal = Util_Json
                                .getJson_from_Object(getPriceProposal()[symbolId][getDigito()].getValue())
                                .replace("\"barrier\":null,", "");
                        Operacao.solicitarEnvioContrato(symbolId, jsonPriceProposal);
                    }
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

            setDigito(n.getUltimoDigito() % 2 == 0 ? 1 : 0);

            if (!Operacao.getVolatilidadeCompraAutorizada()[symbolId].getValue()
                    || Operacao.getVolatilidadeNegociando()[symbolId].getValue()) {
                getContadorVirtual()[symbolId].setValue(0);
                return;
            }

            if ((getParesPorcentagem()[symbolId].getValue().intValue() > getDigitoEntradaPorcentagem()
                    && n.getUltimoDigito() % 2 != 0)
                    || (getImparesPorcentagem()[symbolId].getValue().intValue() > getDigitoEntradaPorcentagem()
                    && n.getUltimoDigito() % 2 == 0)) {
                getContadorVirtual()[symbolId].setValue(getContadorVirtual()[symbolId].getValue() + 1);
                System.out.printf("vol:[%s]\tcont:[%s]\n", Operacao.getVolName()[symbolId], getContadorVirtual()[symbolId].getValue());
                if (getContadorVirtual()[symbolId].getValue().compareTo(0) > 0) {
                    solicitaCompraContrato(symbolId);
                    System.out.printf("vol:[%s]\tcomprou:[%s]\n", Operacao.getVolName()[symbolId], getContadorVirtual()[symbolId].getValue());
                    getContadorVirtual()[symbolId].setValue(0);
                }
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

        Operacao.getLastPriceProposal()[symbolId].setValue(getPriceProposal()[symbolId][getDigito()].getValue());
        Operacao.solicitarCompraContrato(symbolId, getProposal()[symbolId][getDigito()].getValue());
        getProposal()[symbolId][getDigito()].getValue().setId(null);

    }

    @Override
    public void pausarContinuarRobo(Integer symbolId) {

    }

    @Override
    public void stopEstrategia(Integer symbolId) {

        Operacao.getVolatilidadeCompraAutorizada()[symbolId].setValue(false);
        for (int i = 0; i < getQtdContratos(); i++) {
            getProposal()[symbolId][i].setValue(null);
        }
        Operacao.getRenovarTodosContratos()[symbolId].setValue(true);
    }

    @Override
    public void atualizaNovosParametros(Integer symbolId) {

        BigDecimal stkDefault = null;
        Integer qtdTick = 1;
        try {
            stkDefault = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("Valor da stake padrão para %s US$:",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V_"))),
                            "Valor?").alertTextField("#,##0.00", "0.35", "").get(), 2);
            qtdTick = Service_Mascara.getValorMoeda(
                    new Service_Alert(String.format("Qtd de ticks para %s",
                            symbolId == null
                                    ? "todas as volatilidades"
                                    : String.format("volatilidade %s", Operacao.getVolName()[symbolId].replace("R", "V_"))),
                            "Ticks?").alertTextField("#,##0.*0", String.valueOf(qtdTick), "").get(), 0)
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
                        null, null, new BigDecimal(0.), 5, null);
                definicaoDeContrato(finalSymbolId);
            }
        } else {
            if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) return;
            Operacao.setParametrosNegociacao(symbolId, stkDefault, 1, DURATION_UNIT.t,
                    null, null, new BigDecimal(0.), 5, null);
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
            getPriceProposal()[symbolId][getDigito()].setValue(new PriceProposal());

            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setProposal(1);
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setAmount(Operacao.getStakeContrato()[symbolId].getValue()
                            .compareTo(BigDecimal.ZERO) == 0
                            ? Operacao.getStakePadrao()[symbolId].getValue().setScale(2, RoundingMode.HALF_UP)
                            : Operacao.getStakeContrato()[symbolId].getValue().setScale(2, RoundingMode.HALF_UP));
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setBasis(Constants.PRICE_PROPOSAL_BASIS);
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setContract_type(getDigito() % 2 == 0
                            ? CONTRACT_TYPE.DIGITEVEN
                            : CONTRACT_TYPE.DIGITODD);
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setCurrency(Operacao.authorizeProperty().getValue().getCurrency());
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setDuration(Operacao.getTempoDeContrato());
            getPriceProposal()[symbolId][getDigito()].getValue()
                    .setDuration_unit(Operacao.getDurationUnit());
            getPriceProposal()[symbolId][getDigito()].getValue()
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
            getParesQtd()[symbolId] = new SimpleIntegerProperty(0);
            getImparesQtd()[symbolId] = new SimpleIntegerProperty(0);
            getParesPorcentagem()[symbolId] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getImparesPorcentagem()[symbolId] = new SimpleObjectProperty<>(BigDecimal.ZERO);

            Operacao.getInfDet01()[symbolId].setValue(String.format("Pares_%s", Operacao.getVolName()[symbolId]).replace("R", "V"));
            Operacao.getInfVlr01()[symbolId].bind(getParesQtd()[symbolId].asString());
            Operacao.getInfPorc01()[symbolId].bind(Bindings.createStringBinding(() ->
                            getParesPorcentagem()[symbolId].getValue().intValue() + "%",
                    getParesPorcentagem()[symbolId]));

            Operacao.getInfDet02()[symbolId].setValue(String.format("Impares_%s", Operacao.getVolName()[symbolId]).replace("R", "V"));
            Operacao.getInfVlr02()[symbolId].bind(getImparesQtd()[symbolId].asString());
            Operacao.getInfPorc02()[symbolId].bind(Bindings.createStringBinding(() ->
                            getImparesPorcentagem()[symbolId].getValue().intValue() + "%",
                    getImparesPorcentagem()[symbolId]));

            final Double[] margem = new Double[1];
            Operacao.getUltimoTick()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                margem[0] = (100. / Operacao.getQtdTicksGrafico());
                getParesQtd()[symbolId].setValue(Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                        .mapToInt(HistoricoDeTicks::getUltimoDigito)
                        .filter(value -> value % 2 == 0)
                        .count());
                getImparesQtd()[symbolId].setValue(Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                        .mapToInt(HistoricoDeTicks::getUltimoDigito)
                        .filter(value -> value % 2 != 0)
                        .count());

                getParesPorcentagem()[symbolId].setValue(new BigDecimal(margem[0] * getParesQtd()[symbolId].getValue()));
                getImparesPorcentagem()[symbolId].setValue(new BigDecimal(margem[0] * getImparesQtd()[symbolId].getValue()));

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

    public static Integer getQtdContratos() {
        return qtdContratos;
    }

    public static void setQtdContratos(Integer qtdContratos) {
        EvenOdd_01_Porcentagem.qtdContratos = qtdContratos;
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
        EvenOdd_01_Porcentagem.proposal = proposal;
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

    public Operacao getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacao operacao) {
        this.operacao = operacao;
    }

    public IntegerProperty[] getParesQtd() {
        return paresQtd;
    }

    public void setParesQtd(IntegerProperty[] paresQtd) {
        this.paresQtd = paresQtd;
    }

    public IntegerProperty[] getImparesQtd() {
        return imparesQtd;
    }

    public void setImparesQtd(IntegerProperty[] imparesQtd) {
        this.imparesQtd = imparesQtd;
    }

    public ObjectProperty<BigDecimal>[] getParesPorcentagem() {
        return paresPorcentagem;
    }

    public void setParesPorcentagem(ObjectProperty<BigDecimal>[] paresPorcentagem) {
        this.paresPorcentagem = paresPorcentagem;
    }

    public ObjectProperty<BigDecimal>[] getImparesPorcentagem() {
        return imparesPorcentagem;
    }

    public void setImparesPorcentagem(ObjectProperty<BigDecimal>[] imparesPorcentagem) {
        this.imparesPorcentagem = imparesPorcentagem;
    }

    public int getDigitoEntradaPorcentagem() {
        return digitoEntradaPorcentagem.get();
    }

    public IntegerProperty digitoEntradaPorcentagemProperty() {
        return digitoEntradaPorcentagem;
    }

    public void setDigitoEntradaPorcentagem(int digitoEntradaPorcentagem) {
        this.digitoEntradaPorcentagem.set(digitoEntradaPorcentagem);
    }

    public IntegerProperty[] getContadorVirtual() {
        return contadorVirtual;
    }

    public void setContadorVirtual(IntegerProperty[] contadorVirtual) {
        this.contadorVirtual = contadorVirtual;
    }
}
