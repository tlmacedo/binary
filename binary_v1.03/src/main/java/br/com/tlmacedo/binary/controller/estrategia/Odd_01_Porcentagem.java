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

public class Odd_01_Porcentagem implements Estrategia {

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
    ObjectProperty<PriceProposal>[] priceProposal = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    static ObjectProperty<Proposal>[] proposal = new ObjectProperty[Operacao.getSymbolObservableList().size()];
    IntegerProperty digitoEntradaPorcentagem = new SimpleIntegerProperty();


    public Odd_01_Porcentagem(Operacao operacao) {

        setOperacao(operacao);
        Operacao.setParamEstrategiaCarregados(false);
        estatisticasRobo();
        if (!Operacao.isParamEstrategiaCarregados()) {
            Operacao.setParamEstrategiaCarregados(carregaParametros());
        }

    }

//    private void botoes(Integer symbolId) {
//
//        Operacao.getEstrategiaBotaoContrato()[symbolId].setValue(false);
//        Operacao.getEstrategiaBotaoPausar()[symbolId].setValue(true);
//        Operacao.getEstrategiaBotaoComprar()[symbolId].setValue(true);
//        getProposal()[symbolId][0].addListener((ov, o, n) -> {
//            System.out.printf("%s__proposal:[%s]\n", symbolId, n);
//            Operacao.getEstrategiaBotaoContrato()[symbolId].setValue(n != null
//                    || Operacao.getAuthorize() == null);
//            Operacao.getEstrategiaBotaoPausar()[symbolId].setValue(n == null);
//            Operacao.getEstrategiaBotaoComprar()[symbolId].setValue(n == null);
//        });
//
//    }


    private boolean carregaParametros() {

        try {
//                Operacao.setStakePadrao(Operacao.getSaldoInicialConta().multiply(new BigDecimal(0.01)));
            //Service_Alert serviceAlert = new Service_Alert();

            setDigitoEntradaPorcentagem(50);

            for (int symbolId = 0; symbolId < Operacao.getSymbolObservableList().size(); symbolId++) {
                Operacao.setParametrosNegociacao(symbolId, new BigDecimal(0.35), 1, DURATION_UNIT.t,
                        null, null, new BigDecimal(1.1), 5, null);
                if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) continue;
                getPriceProposal()[symbolId] = new SimpleObjectProperty<>();
                getProposal()[symbolId] = new SimpleObjectProperty<>();
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
                            .getJson_from_Object(getPriceProposal()[symbolId].getValue())
                            .replace("\"barrier\":null,", "");
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

            if (Operacao.getQtdDerrotas()[symbolId].getValue() % 3 != 0) {
                solicitaCompraContrato(symbolId);
            } else {
                if (getImparesPorcentagem()[symbolId].getValue().intValue() > getDigitoEntradaPorcentagem()
                        && n.getUltimoDigito() % 2 == 0) {
//                if (Operacao.getQtdDerrotas()[symbolId].getValue().compareTo(2) >= 0)
//                    if (n.getUltimoDigito() % 2 == 0) return;
                    solicitaCompraContrato(symbolId);
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

        Operacao.getLastPriceProposal()[symbolId].setValue(getPriceProposal()[symbolId].getValue());
        Operacao.solicitarCompraContrato(symbolId, getProposal()[symbolId].getValue());
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
        try {
            stkDefault = Service_Mascara.getValorMoeda(new Service_Alert("valor da stake padrão US$:", "Valor?").alertTextField("#,##0.00", "0.35", "").get(), 2);
        } catch (Exception ex) {
            System.out.printf("00_stkDefault:%s\n", stkDefault);
            if (!(ex instanceof NoSuchElementException))
                ex.printStackTrace();
            return;
        }
        System.out.printf("01_stkDefault:%s\n", stkDefault);
        Operacao.setParametrosNegociacao(symbolId, stkDefault, 1, DURATION_UNIT.t,
                null, null, new BigDecimal(1.1), 5, null);

        definicaoDeContrato(symbolId);

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
                    .setContract_type(CONTRACT_TYPE.DIGITODD);
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
        Odd_01_Porcentagem.proposal = proposal;
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
}
