package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategia.*;
import br.com.tlmacedo.binary.model.dao.ContaTokenDAO;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.tableModel.TmodelTransacoes;
import br.com.tlmacedo.binary.model.vo.Error;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_Mascara;
import br.com.tlmacedo.binary.services.Util_Json;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.tlmacedo.binary.interfaces.Constants.*;

public class Operacao implements Initializable {

    /**
     * Objetos DAO conecta com banco de dados
     */
    //** Banco de Dados **
    static SymbolDAO symbolDAO = new SymbolDAO();
    static ContaTokenDAO contaTokenDAO = new ContaTokenDAO();
    static TransacoesDAO transacoesDAO = new TransacoesDAO();
    static TransactionDAO transactionDAO = new TransactionDAO();


    /**
     * Identificação de volatilidades
     */
    //** Variaveis de identificacoes das volatilidades
    static final ObservableList<Symbol> symbolObservableList = FXCollections.observableArrayList(getSymbolDAO().getAll(Symbol.class, "ativa=true", null));
    static final Integer VOL_10 = 0;
    static final Integer VOL_25 = 1;
    static final Integer VOL_50 = 2;
    static final Integer VOL_75 = 3;
    static final Integer VOL_100 = 4;
    static final Integer VOL_HZ10 = 5;
    static final Integer VOL_HZ25 = 6;
    static final Integer VOL_HZ50 = 7;
    static final Integer VOL_HZ75 = 8;
    static final Integer VOL_HZ100 = 9;
    static final String[] VOL_NAME = getSymbolObservableList().stream().map(Symbol::getName).collect(Collectors.toList()).toArray(String[]::new);
    static BooleanProperty VOL_1S = new SimpleBooleanProperty(false);

    /**
     * Contas corretora
     */
    static ObjectProperty<ContaToken> contaToken = new SimpleObjectProperty<>();
    static ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>();


    /**
     * Conexão e operação com WebService
     */
    static final ObjectProperty<WSClient> wsClient = new SimpleObjectProperty<>(new WSClient());
    static BooleanProperty wsConectado = new SimpleBooleanProperty(false);

    /**
     * Graficos
     */
    //** informações para graficos **
    static IntegerProperty[] grafDigitoMaiorQtd = new IntegerProperty[getSymbolObservableList().size()];
    static IntegerProperty[] grafDigitoMenorQtd = new IntegerProperty[getSymbolObservableList().size()];
    static IntegerProperty qtdTicksGrafico = new SimpleIntegerProperty(100);

    //** gráficos em barras **


    //** graficos em linha **

    static final IntegerProperty qtdTicksAnalisar = new SimpleIntegerProperty(1000);

    /**
     * Robos
     */
    static ObjectProperty<ROBOS> roboSelecionado = new SimpleObjectProperty<>();
    static ObjectProperty<Estrategia> roboEstrategia = new SimpleObjectProperty<>();


    /**
     * Variaveis de controle do sistema
     */

    Timeline roboRelogio;
    LongProperty roboHoraInicial = new SimpleLongProperty();
    LongProperty roboCronometro = new SimpleLongProperty();
    BooleanProperty roboCronometroAtivado = new SimpleBooleanProperty(false);


    /**
     * Volatilidade
     */
    //**model_DAO**
    //**TModelTransacoes**
    static ObjectProperty<Transacoes>[] transacao = new ObjectProperty[getSymbolObservableList().size()];
    static TmodelTransacoes[] tmodelTransacoes = new TmodelTransacoes[getSymbolObservableList().size()];
    static FilteredList<Transacoes>[] transacoesFilteredList = new FilteredList[getSymbolObservableList().size()];
    //**Listas**
    static ObjectProperty<Error>[] error = new ObjectProperty[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[] historicoDeTicksGraficoObservableList = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();
    static ObservableList<Transaction>[] transactionObservableList = new ObservableList[getSymbolObservableList().size()];
    //**Informacoes**
    static ObjectProperty<Tick>[] ultimoTick = new ObjectProperty[getSymbolObservableList().size()];
    static IntegerProperty[] ultimoDigito = new IntegerProperty[getSymbolObservableList().size()];
    static BooleanProperty[] tickSubindo = new BooleanProperty[getSymbolObservableList().size()];
    ChangeListener[] listenerTickSubindo = new ChangeListener[getSymbolObservableList().size()];
    static StringProperty[] infDet01 = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] infVlr01 = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] infPorc01 = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] infDet02 = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] infVlr02 = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] infPorc02 = new StringProperty[getSymbolObservableList().size()];
    static IntegerProperty[] qtdMaiorSeqDerrota = new IntegerProperty[getSymbolObservableList().size()];
    static IntegerProperty[] qtdMaiorSeqVitoria = new IntegerProperty[getSymbolObservableList().size()];

    //**Parametros negociação**
    static StringProperty contratoRobo = new SimpleStringProperty("");
    static BooleanProperty transacoesAutorizadas = new SimpleBooleanProperty(false);
    static BooleanProperty paramEstrategiaCarregados = new SimpleBooleanProperty(false);
    static ObjectProperty<BigDecimal> saldoInicialConta = new SimpleObjectProperty<>();
    static IntegerProperty qtdLossResetStake = new SimpleIntegerProperty(0);
    static IntegerProperty qtdStopLoss = new SimpleIntegerProperty();
    static ObjectProperty<BigDecimal> vlrStopLoss = new SimpleObjectProperty<>();
    static ObjectProperty<BigDecimal> vlrStopGain = new SimpleObjectProperty<>();
    static IntegerProperty tempoDeContrato = new SimpleIntegerProperty();
    static ObjectProperty<DURATION_UNIT> durationUnit = new SimpleObjectProperty<>();

    static BooleanProperty geralvolatilidadeCompraAutorizada = new SimpleBooleanProperty(false);
    static BooleanProperty geralvolatilidadeNegociando = new SimpleBooleanProperty(false);
    static BooleanProperty[] volatilidadeAtiva = new BooleanProperty[getSymbolObservableList().size()];
    static BooleanProperty[] volatilidadeCompraAutorizada = new BooleanProperty[getSymbolObservableList().size()];
    static BooleanProperty[] volatilidadeNegociando = new BooleanProperty[getSymbolObservableList().size()];
    //ChangeListener[] listenerVolatilidadeNegociando = new ChangeListener[getSymbolObservableList().size()];

    //**Parametros na operação**
    static BooleanProperty[] renovarTodosContratos = new BooleanProperty[getSymbolObservableList().size()];
    static ObjectProperty<BigDecimal>[] stakePadrao = new ObjectProperty[getSymbolObservableList().size()];
    static ObjectProperty<BigDecimal>[] stakeContrato = new ObjectProperty[getSymbolObservableList().size()];
    static ObjectProperty<BigDecimal>[] fatorMartingale = new ObjectProperty[getSymbolObservableList().size()];
    static ObjectProperty<PriceProposal>[] lastPriceProposal = new ObjectProperty[getSymbolObservableList().size()];
    static IntegerProperty[] qtdDerrotas = new IntegerProperty[getSymbolObservableList().size()];
    static IntegerProperty[] qtdVitorias = new IntegerProperty[getSymbolObservableList().size()];

    static EventHandler<ActionEvent>[] actionBtnContrato = new EventHandler[getSymbolObservableList().size()];

    static BooleanProperty[] estrategiaBotaoContrato = new BooleanProperty[getSymbolObservableList().size()];
    static BooleanProperty[] estrategiaBotaoComprar = new BooleanProperty[getSymbolObservableList().size()];
    static BooleanProperty[] estrategiaBotaoPausar = new BooleanProperty[getSymbolObservableList().size()];
    static BooleanProperty[] estrategiaBotaoStop = new BooleanProperty[getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal>[] meuLucroVolatilidade = new ObjectProperty[getSymbolObservableList().size()];

    /**
     * Graficos por volatilidade
     */
    XYChart.Series<String, Number>[] grafBarVolatilidade_R = new XYChart.Series[getSymbolObservableList().size()];
    ObservableList<Data<String, Number>>[] grafBarListDataDigitos_R = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<LongProperty>[] grafBarListValorDigito_R = new ObservableList[getSymbolObservableList().size()];
    Text[][] grafBarTxtDigito_R = new Text[getSymbolObservableList().size()][10];

    XYChart.Series<String, Number>[] grafLineVolatilidade_R = new XYChart.Series[getSymbolObservableList().size()];
    ObservableList<Data<String, Number>>[] grafLineListDataDigitos_R = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[] grafLineListValorDigito_R = new ObservableList[getSymbolObservableList().size()];

    XYChart.Series<String, Number>[] grafMACDVolatilidade_R = new XYChart.Series[getSymbolObservableList().size()];
    ObservableList<Data<String, Number>>[] grafMACDListDataDigitos_R = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[] grafMACDListValorDigito_R = new ObservableList[getSymbolObservableList().size()];

    /**
     * Objetos do formulario
     */

    // Detalhes e informações da conta
    public AnchorPane painelViewBinary;

    public TitledPane tpn_DetalhesConta;
    public ComboBox<ContaToken> cboConta;
    public Label lblLegendaNExecucoes;
    public Label lblTotalExecucoes;
    public Label lblTotalVitorias;
    public Label lblTotalDerrotas;
    public Label lblTotalLucro;
    public Label lblTotalLucroPorc;
    public Label lblProprietarioConta;
    public Label lblIdConta;
    public Label lblSaldoConta;
    public Label lblMoedaSaldo;
    public Label lblSaldoInicial;
    public Label lblTotalInvestido;
    public Label lblTotalPremiacao;
    public Label lblSaldoFinal;

    // Negociação
    public TitledPane tpn_negociacao;
    public ComboBox<ROBOS> cboRobos;
    public Button btnContratos;
    public Button btnIniciar;
    public Button btnPausar;
    public Button btnStop;
    public ComboBox<SimNao> cboVelocidadeTicksGrafico;
    public ComboBox<Integer> cboQtdTicksGrafico;
    public Label lblRoboHoraInicial;
    public Label lblRoboHoraAtual;
    public Label lblRoboCronometro;


    // Volatilidade R10
    public TitledPane tpn_R10;
    public BarChart<String, Number> grafBar_R10;
    public NumberAxis yAxisBar_R10;
    public BarChart<String, Number> grafBar_HZ10;
    public NumberAxis yAxisBar_HZ10;
    public LineChart grafLine_R10;
    public NumberAxis yAxisLine_R10;
    public LineChart grafLine_HZ10;
    public NumberAxis yAxisLine_HZ10;
    public Label lblInf01_R10;
    public Label lblVlrInf01_R10;
    public Label lblPorcInf01_R10;
    public Label lblInf02_R10;
    public Label lblVlrInf02_R10;
    public Label lblPorcInf02_R10;
    public Label lblTickUltimo_R10;
    public Label lblLegendaTickUltimo_R10;
    public Button btnContratos_R10;
    public Button btnComprar_R10;
    public Button btnPausar_R10;
    public Button btnStop_R10;
    public Label lblInvestido_R10;
    public Label lblInvestidoPorc_R10;
    public Label lblPremiacao_R10;
    public Label lblPremiacaoPorc_R10;
    public Label lblLucro_R10;
    public Label lblLucroPorc_R10;
    public TableView tbvTransacoes_R10;
    public CheckBox chkAtivo_R10;
    public Label tpnLblLegendaExecucoes_R10;
    public Label tpnLblExecucoes_R10;
    public Label tpnLblVitorias_R10;
    public Label tpnLblDerrotas_R10;
    public Label tpnLblLucro_R10;

    // Volatilidade R25
    public TitledPane tpn_R25;
    public BarChart<String, Number> grafBar_R25;
    public NumberAxis yAxisBar_R25;
    public BarChart<String, Number> grafBar_HZ25;
    public NumberAxis yAxisBar_HZ25;
    public LineChart grafLine_R25;
    public NumberAxis yAxisLine_R25;
    public LineChart grafLine_HZ25;
    public NumberAxis yAxisLine_HZ25;
    public Label lblInf01_R25;
    public Label lblVlrInf01_R25;
    public Label lblPorcInf01_R25;
    public Label lblInf02_R25;
    public Label lblVlrInf02_R25;
    public Label lblPorcInf02_R25;
    public Label lblTickUltimo_R25;
    public Label lblLegendaTickUltimo_R25;
    public Button btnContratos_R25;
    public Button btnComprar_R25;
    public Button btnPausar_R25;
    public Button btnStop_R25;
    public Label lblInvestido_R25;
    public Label lblInvestidoPorc_R25;
    public Label lblPremiacao_R25;
    public Label lblPremiacaoPorc_R25;
    public Label lblLucro_R25;
    public Label lblLucroPorc_R25;
    public TableView tbvTransacoes_R25;
    public CheckBox chkAtivo_R25;
    public Label tpnLblLegendaExecucoes_R25;
    public Label tpnLblExecucoes_R25;
    public Label tpnLblVitorias_R25;
    public Label tpnLblDerrotas_R25;
    public Label tpnLblLucro_R25;

    // Volatilidade R50
    public TitledPane tpn_R50;
    public BarChart<String, Number> grafBar_R50;
    public NumberAxis yAxisBar_R50;
    public BarChart<String, Number> grafBar_HZ50;
    public NumberAxis yAxisBar_HZ50;
    public LineChart grafLine_R50;
    public NumberAxis yAxisLine_R50;
    public LineChart grafLine_HZ50;
    public NumberAxis yAxisLine_HZ50;
    public Label lblInf01_R50;
    public Label lblVlrInf01_R50;
    public Label lblPorcInf01_R50;
    public Label lblInf02_R50;
    public Label lblVlrInf02_R50;
    public Label lblPorcInf02_R50;
    public Label lblTickUltimo_R50;
    public Label lblLegendaTickUltimo_R50;
    public Button btnContratos_R50;
    public Button btnComprar_R50;
    public Button btnPausar_R50;
    public Button btnStop_R50;
    public Label lblInvestido_R50;
    public Label lblInvestidoPorc_R50;
    public Label lblPremiacao_R50;
    public Label lblPremiacaoPorc_R50;
    public Label lblLucro_R50;
    public Label lblLucroPorc_R50;
    public TableView tbvTransacoes_R50;
    public CheckBox chkAtivo_R50;
    public Label tpnLblLegendaExecucoes_R50;
    public Label tpnLblExecucoes_R50;
    public Label tpnLblVitorias_R50;
    public Label tpnLblDerrotas_R50;
    public Label tpnLblLucro_R50;

    // Volatilidade R75
    public TitledPane tpn_R75;
    public BarChart<String, Number> grafBar_R75;
    public NumberAxis yAxisBar_R75;
    public BarChart<String, Number> grafBar_HZ75;
    public NumberAxis yAxisBar_HZ75;
    public LineChart grafLine_R75;
    public NumberAxis yAxisLine_R75;
    public LineChart grafLine_HZ75;
    public NumberAxis yAxisLine_HZ75;
    public Label lblInf01_R75;
    public Label lblVlrInf01_R75;
    public Label lblPorcInf01_R75;
    public Label lblInf02_R75;
    public Label lblVlrInf02_R75;
    public Label lblPorcInf02_R75;
    public Label lblTickUltimo_R75;
    public Label lblLegendaTickUltimo_R75;
    public Button btnContratos_R75;
    public Button btnComprar_R75;
    public Button btnPausar_R75;
    public Button btnStop_R75;
    public Label lblInvestido_R75;
    public Label lblInvestidoPorc_R75;
    public Label lblPremiacao_R75;
    public Label lblPremiacaoPorc_R75;
    public Label lblLucro_R75;
    public Label lblLucroPorc_R75;
    public TableView tbvTransacoes_R75;
    public CheckBox chkAtivo_R75;
    public Label tpnLblLegendaExecucoes_R75;
    public Label tpnLblExecucoes_R75;
    public Label tpnLblVitorias_R75;
    public Label tpnLblDerrotas_R75;
    public Label tpnLblLucro_R75;

    // Volatilidade R100
    public TitledPane tpn_R100;
    public BarChart<String, Number> grafBar_R100;
    public NumberAxis yAxisBar_R100;
    public BarChart<String, Number> grafBar_HZ100;
    public NumberAxis yAxisBar_HZ100;
    public LineChart grafLine_R100;
    public NumberAxis yAxisLine_R100;
    public LineChart grafLine_HZ100;
    public NumberAxis yAxisLine_HZ100;
    public Label lblInf01_R100;
    public Label lblVlrInf01_R100;
    public Label lblPorcInf01_R100;
    public Label lblInf02_R100;
    public Label lblVlrInf02_R100;
    public Label lblPorcInf02_R100;
    public Label lblTickUltimo_R100;
    public Label lblLegendaTickUltimo_R100;
    public Button btnContratos_R100;
    public Button btnComprar_R100;
    public Button btnPausar_R100;
    public Button btnStop_R100;
    public Label lblInvestido_R100;
    public Label lblInvestidoPorc_R100;
    public Label lblPremiacao_R100;
    public Label lblPremiacaoPorc_R100;
    public Label lblLucro_R100;
    public Label lblLucroPorc_R100;
    public TableView tbvTransacoes_R100;
    public CheckBox chkAtivo_R100;
    public Label tpnLblLegendaExecucoes_R100;
    public Label tpnLblExecucoes_R100;
    public Label tpnLblVitorias_R100;
    public Label tpnLblDerrotas_R100;
    public Label tpnLblLucro_R100;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        carregarVariaveisSistema();
        carregarObjetosVolatilidade();
        carregarObjetosFormulario();
        carregarObjetosFXML();
        escutaObjetos_Variaveis();
        escutaBotoes();
        escutaEstrategias();

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void carregarVariaveisSistema() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            getGrafDigitoMaiorQtd()[symbolId] = new SimpleIntegerProperty(0);
            getGrafDigitoMenorQtd()[symbolId] = new SimpleIntegerProperty(0);
        }

    }

    private void carregarObjetosFormulario() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            getVolatilidadeAtiva()[symbolId] = new SimpleBooleanProperty();
            getVolatilidadeNegociando()[symbolId] = new SimpleBooleanProperty();
            getTickSubindo()[symbolId] = new SimpleBooleanProperty();
            getUltimoTick()[symbolId] = new SimpleObjectProperty<>();
            getUltimoDigito()[symbolId] = new SimpleIntegerProperty();
            getInfDet01()[symbolId] = new SimpleStringProperty();
            getInfVlr01()[symbolId] = new SimpleStringProperty();
            getInfPorc01()[symbolId] = new SimpleStringProperty();
            getInfDet02()[symbolId] = new SimpleStringProperty();
            getInfVlr02()[symbolId] = new SimpleStringProperty();
            getInfPorc02()[symbolId] = new SimpleStringProperty();
            getTransacao()[symbolId] = new SimpleObjectProperty<>();
            carregarObjetosFormulario(symbolId);
        }

    }

//    private void removeListenerObjetosFormulario() {
//        getTickSubindo()
//        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
//            getListenerTickSubindo()[symbolId]
//        }
//    }

    private void carregarObjetosFormulario(Integer symbolId) {

        if ((isVol1s() && symbolId < 5) || (!isVol1s() && symbolId >= 5)) return;
        switch (symbolId) {
            case 0, 5 -> {
                if (getSymbolObservableList().size() > 5)
                    getVolatilidadeAtiva()[isVol1s() ? VOL_10 : VOL_HZ10].unbind();
                getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].bind(getChkAtivo_R10().selectedProperty());

                getLblTickUltimo_R10().textProperty().unbind();
                getLblTickUltimo_R10().textProperty().bind(getUltimoTick()[!isVol1s() ? VOL_10 : VOL_HZ10].asString());

                getLblInf01_R10().textProperty().unbind();
                getLblInf01_R10().textProperty().bind(getInfDet01()[!isVol1s() ? VOL_10 : VOL_HZ10]);
                getLblVlrInf01_R10().textProperty().unbind();
                getLblVlrInf01_R10().textProperty().bind(getInfVlr01()[!isVol1s() ? VOL_10 : VOL_HZ10]);
                getLblPorcInf01_R10().textProperty().unbind();
                getLblPorcInf01_R10().textProperty().bind(getInfPorc01()[!isVol1s() ? VOL_10 : VOL_HZ10]);
                getLblInf02_R10().textProperty().unbind();
                getLblInf02_R10().textProperty().bind(getInfDet02()[!isVol1s() ? VOL_10 : VOL_HZ10]);
                getLblVlrInf02_R10().textProperty().unbind();
                getLblVlrInf02_R10().textProperty().bind(getInfVlr02()[!isVol1s() ? VOL_10 : VOL_HZ10]);
                getLblPorcInf02_R10().textProperty().unbind();
                getLblPorcInf02_R10().textProperty().bind(getInfPorc02()[!isVol1s() ? VOL_10 : VOL_HZ10]);

                getLblTickUltimo_R10().styleProperty().unbind();
                getLblTickUltimo_R10().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getTickSubindo()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue() == null)
                        return null;
                    return getTickSubindo()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
                }, getTickSubindo()[!isVol1s() ? VOL_10 : VOL_HZ10]));

                getLblLegendaTickUltimo_R10().styleProperty().unbind();
                getLblLegendaTickUltimo_R10().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getVolatilidadeNegociando()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue() == null) return null;
                    return getVolatilidadeNegociando()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
                }, getVolatilidadeNegociando()[!isVol1s() ? VOL_10 : VOL_HZ10]));
            }
            case 1, 6 -> {
                if (getSymbolObservableList().size() > 5)
                    getVolatilidadeAtiva()[isVol1s() ? VOL_25 : VOL_HZ25].unbind();
                getVolatilidadeAtiva()[!isVol1s() ? VOL_25 : VOL_HZ25].bind(getChkAtivo_R25().selectedProperty());

                getLblTickUltimo_R25().textProperty().unbind();
                getLblTickUltimo_R25().textProperty().bind(getUltimoTick()[!isVol1s() ? VOL_25 : VOL_HZ25].asString());

                getLblInf01_R25().textProperty().unbind();
                getLblInf01_R25().textProperty().bind(getInfDet01()[!isVol1s() ? VOL_25 : VOL_HZ25]);
                getLblVlrInf01_R25().textProperty().unbind();
                getLblVlrInf01_R25().textProperty().bind(getInfVlr01()[!isVol1s() ? VOL_25 : VOL_HZ25]);
                getLblPorcInf01_R25().textProperty().unbind();
                getLblPorcInf01_R25().textProperty().bind(getInfPorc01()[!isVol1s() ? VOL_25 : VOL_HZ25]);
                getLblInf02_R25().textProperty().unbind();
                getLblInf02_R25().textProperty().bind(getInfDet02()[!isVol1s() ? VOL_25 : VOL_HZ25]);
                getLblVlrInf02_R25().textProperty().unbind();
                getLblVlrInf02_R25().textProperty().bind(getInfVlr02()[!isVol1s() ? VOL_25 : VOL_HZ25]);
                getLblPorcInf02_R25().textProperty().unbind();
                getLblPorcInf02_R25().textProperty().bind(getInfPorc02()[!isVol1s() ? VOL_25 : VOL_HZ25]);

                getLblTickUltimo_R25().styleProperty().unbind();
                getLblTickUltimo_R25().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getTickSubindo()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue() == null)
                        return null;
                    return getTickSubindo()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
                }, getTickSubindo()[!isVol1s() ? VOL_25 : VOL_HZ25]));

                getLblLegendaTickUltimo_R25().styleProperty().unbind();
                getLblLegendaTickUltimo_R25().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getVolatilidadeNegociando()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue() == null) return null;
                    return getVolatilidadeNegociando()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
                }, getVolatilidadeNegociando()[!isVol1s() ? VOL_25 : VOL_HZ25]));
            }
            case 2, 7 -> {
                if (getSymbolObservableList().size() > 5)
                    getVolatilidadeAtiva()[isVol1s() ? VOL_50 : VOL_HZ50].unbind();
                getVolatilidadeAtiva()[!isVol1s() ? VOL_50 : VOL_HZ50].bind(getChkAtivo_R50().selectedProperty());

                getLblTickUltimo_R50().textProperty().unbind();
                getLblTickUltimo_R50().textProperty().bind(getUltimoTick()[!isVol1s() ? VOL_50 : VOL_HZ50].asString());

                getLblInf01_R50().textProperty().unbind();
                getLblInf01_R50().textProperty().bind(getInfDet01()[!isVol1s() ? VOL_50 : VOL_HZ50]);
                getLblVlrInf01_R50().textProperty().unbind();
                getLblVlrInf01_R50().textProperty().bind(getInfVlr01()[!isVol1s() ? VOL_50 : VOL_HZ50]);
                getLblPorcInf01_R50().textProperty().unbind();
                getLblPorcInf01_R50().textProperty().bind(getInfPorc01()[!isVol1s() ? VOL_50 : VOL_HZ50]);
                getLblInf02_R50().textProperty().unbind();
                getLblInf02_R50().textProperty().bind(getInfDet02()[!isVol1s() ? VOL_50 : VOL_HZ50]);
                getLblVlrInf02_R50().textProperty().unbind();
                getLblVlrInf02_R50().textProperty().bind(getInfVlr02()[!isVol1s() ? VOL_50 : VOL_HZ50]);
                getLblPorcInf02_R50().textProperty().unbind();
                getLblPorcInf02_R50().textProperty().bind(getInfPorc02()[!isVol1s() ? VOL_50 : VOL_HZ50]);

                getLblTickUltimo_R50().styleProperty().unbind();
                getLblTickUltimo_R50().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getTickSubindo()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue() == null)
                        return null;
                    return getTickSubindo()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
                }, getTickSubindo()[!isVol1s() ? VOL_50 : VOL_HZ50]));

                getLblLegendaTickUltimo_R50().styleProperty().unbind();
                getLblLegendaTickUltimo_R50().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getVolatilidadeNegociando()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue() == null) return null;
                    return getVolatilidadeNegociando()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
                }, getVolatilidadeNegociando()[!isVol1s() ? VOL_50 : VOL_HZ50]));
            }
            case 3, 8 -> {
                if (getSymbolObservableList().size() > 5)
                    getVolatilidadeAtiva()[isVol1s() ? VOL_75 : VOL_HZ75].unbind();
                getVolatilidadeAtiva()[!isVol1s() ? VOL_75 : VOL_HZ75].bind(getChkAtivo_R75().selectedProperty());

                getLblTickUltimo_R75().textProperty().unbind();
                getLblTickUltimo_R75().textProperty().bind(getUltimoTick()[!isVol1s() ? VOL_75 : VOL_HZ75].asString());

                getLblInf01_R75().textProperty().unbind();
                getLblInf01_R75().textProperty().bind(getInfDet01()[!isVol1s() ? VOL_75 : VOL_HZ75]);
                getLblVlrInf01_R75().textProperty().unbind();
                getLblVlrInf01_R75().textProperty().bind(getInfVlr01()[!isVol1s() ? VOL_75 : VOL_HZ75]);
                getLblPorcInf01_R75().textProperty().unbind();
                getLblPorcInf01_R75().textProperty().bind(getInfPorc01()[!isVol1s() ? VOL_75 : VOL_HZ75]);
                getLblInf02_R75().textProperty().unbind();
                getLblInf02_R75().textProperty().bind(getInfDet02()[!isVol1s() ? VOL_75 : VOL_HZ75]);
                getLblVlrInf02_R75().textProperty().unbind();
                getLblVlrInf02_R75().textProperty().bind(getInfVlr02()[!isVol1s() ? VOL_75 : VOL_HZ75]);
                getLblPorcInf02_R75().textProperty().unbind();
                getLblPorcInf02_R75().textProperty().bind(getInfPorc02()[!isVol1s() ? VOL_75 : VOL_HZ75]);

                getLblTickUltimo_R75().styleProperty().unbind();
                getLblTickUltimo_R75().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getTickSubindo()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue() == null)
                        return null;
                    return getTickSubindo()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
                }, getTickSubindo()[!isVol1s() ? VOL_75 : VOL_HZ75]));

                getLblLegendaTickUltimo_R75().styleProperty().unbind();
                getLblLegendaTickUltimo_R75().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getVolatilidadeNegociando()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue() == null) return null;
                    return getVolatilidadeNegociando()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
                }, getVolatilidadeNegociando()[!isVol1s() ? VOL_75 : VOL_HZ75]));
            }
            case 4, 9 -> {
                if (getSymbolObservableList().size() > 5)
                    getVolatilidadeAtiva()[isVol1s() ? VOL_100 : VOL_HZ100].unbind();
                getVolatilidadeAtiva()[!isVol1s() ? VOL_100 : VOL_HZ100].bind(getChkAtivo_R100().selectedProperty());

                getLblTickUltimo_R100().textProperty().unbind();
                getLblTickUltimo_R100().textProperty().bind(getUltimoTick()[!isVol1s() ? VOL_100 : VOL_HZ100].asString());

                getLblInf01_R100().textProperty().unbind();
                getLblInf01_R100().textProperty().bind(getInfDet01()[!isVol1s() ? VOL_100 : VOL_HZ100]);
                getLblVlrInf01_R100().textProperty().unbind();
                getLblVlrInf01_R100().textProperty().bind(getInfVlr01()[!isVol1s() ? VOL_100 : VOL_HZ100]);
                getLblPorcInf01_R100().textProperty().unbind();
                getLblPorcInf01_R100().textProperty().bind(getInfPorc01()[!isVol1s() ? VOL_100 : VOL_HZ100]);
                getLblInf02_R100().textProperty().unbind();
                getLblInf02_R100().textProperty().bind(getInfDet02()[!isVol1s() ? VOL_100 : VOL_HZ100]);
                getLblVlrInf02_R100().textProperty().unbind();
                getLblVlrInf02_R100().textProperty().bind(getInfVlr02()[!isVol1s() ? VOL_100 : VOL_HZ100]);
                getLblPorcInf02_R100().textProperty().unbind();
                getLblPorcInf02_R100().textProperty().bind(getInfPorc02()[!isVol1s() ? VOL_100 : VOL_HZ100]);

                getLblTickUltimo_R100().styleProperty().unbind();
                getLblTickUltimo_R100().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getTickSubindo()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue() == null)
                        return null;
                    return getTickSubindo()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
                }, getTickSubindo()[!isVol1s() ? VOL_100 : VOL_HZ100]));

                getLblLegendaTickUltimo_R100().styleProperty().unbind();
                getLblLegendaTickUltimo_R100().styleProperty().bind(Bindings.createStringBinding(() -> {
                    if (getVolatilidadeNegociando()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue() == null) return null;
                    return getVolatilidadeNegociando()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
                }, getVolatilidadeNegociando()[!isVol1s() ? VOL_100 : VOL_HZ100]));
            }
        }
        vincularTabelasTransacoes(symbolId);

    }

    private void vincularTabelasTransacoes(Integer symbolId) {

        if ((isVol1s() && symbolId < 5) || (!isVol1s() && symbolId >= 5)) return;

        getTmodelTransacoes()[symbolId] = new TmodelTransacoes(symbolId);
        getTmodelTransacoes()[symbolId].criarTabela();

        getTransacoesFilteredList()[symbolId] = new FilteredList<>(getTransacoesObservableList());
        getTmodelTransacoes()[symbolId].setTransacoesFilteredList(getTransacoesFilteredList()[symbolId]);


        switch (symbolId) {
            case 0, 5 -> {

                if (getSymbolObservableList().size() > 5)
                    getTmodelTransacoes()[isVol1s() ? VOL_10 : VOL_HZ10].setTbvTransacoes(null);
                getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].setTbvTransacoes(getTbvTransacoes_R10());

                getTpnLblExecucoes_R10().textProperty().unbind();
                getTpnLblExecucoes_R10().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                        .qtdNExecucaoProperty().asString());

                getTpnLblVitorias_R10().textProperty().unbind();
                getTpnLblVitorias_R10().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdVitorias()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue().compareTo(getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue()) >= 0)
                        getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_10 : VOL_HZ10].setValue(getQtdVitorias()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].getQtdNVitoria(),
                            getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].qtdNVitoriaProperty(), getQtdVitorias()[!isVol1s() ? VOL_10 : VOL_HZ10]));

                getTpnLblDerrotas_R10().textProperty().unbind();
                getTpnLblDerrotas_R10().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdDerrotas()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue().compareTo(getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue()) >= 0)
                        getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_10 : VOL_HZ10].setValue(getQtdDerrotas()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].getQtdNDerrota(),
                            getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].qtdNDerrotaProperty(), getQtdDerrotas()[!isVol1s() ? VOL_10 : VOL_HZ10]));

                getTpnLblLucro_R10().textProperty().unbind();
                getTpnLblLucro_R10().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                        .totalLucroProperty().asString());
                getMeuLucroVolatilidade()[!isVol1s() ? VOL_10 : VOL_HZ10].bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].totalLucroProperty());

                getLblInvestido_R10().textProperty().unbind();
                getLblInvestido_R10().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                        .totalInvestidoProperty().asString());
                getLblInvestidoPorc_R10().textProperty().unbind();
                getLblInvestidoPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                                                .getTotalInvestido()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].totalInvestidoProperty()));

                getLblPremiacao_R10().textProperty().unbind();
                getLblPremiacao_R10().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                        .totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R10().textProperty().unbind();
                getLblPremiacaoPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                                                .getTotalPremiacao()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].totalPremiacaoProperty()));

                getLblLucro_R10().textProperty().unbind();
                getLblLucro_R10().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                        .totalLucroProperty().asString());
                getLblLucroPorc_R10().textProperty().unbind();
                getLblLucroPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_StopGain(getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10]
                                                .getTotalLucro()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_10 : VOL_HZ10].totalLucroProperty()));

            }
            case 1, 6 -> {

                if (getSymbolObservableList().size() > 5)
                    getTmodelTransacoes()[isVol1s() ? VOL_25 : VOL_HZ25].setTbvTransacoes(null);
                getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].setTbvTransacoes(getTbvTransacoes_R25());

                getTpnLblExecucoes_R25().textProperty().unbind();
                getTpnLblExecucoes_R25().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                        .qtdNExecucaoProperty().asString());

                getTpnLblVitorias_R25().textProperty().unbind();
                getTpnLblVitorias_R25().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdVitorias()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue().compareTo(getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue()) >= 0)
                        getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_25 : VOL_HZ25].setValue(getQtdVitorias()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].getQtdNVitoria(),
                            getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].qtdNVitoriaProperty(), getQtdVitorias()[!isVol1s() ? VOL_25 : VOL_HZ25]));

                getTpnLblDerrotas_R25().textProperty().unbind();
                getTpnLblDerrotas_R25().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdDerrotas()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue().compareTo(getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue()) >= 0)
                        getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_25 : VOL_HZ25].setValue(getQtdDerrotas()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].getQtdNDerrota(),
                            getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].qtdNDerrotaProperty(), getQtdDerrotas()[!isVol1s() ? VOL_25 : VOL_HZ25]));

                getTpnLblLucro_R25().textProperty().unbind();
                getTpnLblLucro_R25().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                        .totalLucroProperty().asString());
                getMeuLucroVolatilidade()[!isVol1s() ? VOL_25 : VOL_HZ25].bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].totalLucroProperty());

                getLblInvestido_R25().textProperty().unbind();
                getLblInvestido_R25().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                        .totalInvestidoProperty().asString());
                getLblInvestidoPorc_R25().textProperty().unbind();
                getLblInvestidoPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                                                .getTotalInvestido()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].totalInvestidoProperty()));

                getLblPremiacao_R25().textProperty().unbind();
                getLblPremiacao_R25().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                        .totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R25().textProperty().unbind();
                getLblPremiacaoPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                                                .getTotalPremiacao()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].totalPremiacaoProperty()));

                getLblLucro_R25().textProperty().unbind();
                getLblLucro_R25().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                        .totalLucroProperty().asString());
                getLblLucroPorc_R25().textProperty().unbind();
                getLblLucroPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_StopGain(getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25]
                                                .getTotalLucro()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_25 : VOL_HZ25].totalLucroProperty()));

            }
            case 2, 7 -> {

                if (getSymbolObservableList().size() > 5)
                    getTmodelTransacoes()[isVol1s() ? VOL_50 : VOL_HZ50].setTbvTransacoes(null);
                getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].setTbvTransacoes(getTbvTransacoes_R50());

                getTpnLblExecucoes_R50().textProperty().unbind();
                getTpnLblExecucoes_R50().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                        .qtdNExecucaoProperty().asString());

                getTpnLblVitorias_R50().textProperty().unbind();
                getTpnLblVitorias_R50().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdVitorias()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue().compareTo(getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue()) >= 0)
                        getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_50 : VOL_HZ50].setValue(getQtdVitorias()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].getQtdNVitoria(),
                            getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].qtdNVitoriaProperty(), getQtdVitorias()[!isVol1s() ? VOL_50 : VOL_HZ50]));

                getTpnLblDerrotas_R50().textProperty().unbind();
                getTpnLblDerrotas_R50().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdDerrotas()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue().compareTo(getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue()) >= 0)
                        getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_50 : VOL_HZ50].setValue(getQtdDerrotas()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].getQtdNDerrota(),
                            getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].qtdNDerrotaProperty(), getQtdDerrotas()[!isVol1s() ? VOL_50 : VOL_HZ50]));

                getTpnLblLucro_R50().textProperty().unbind();
                getTpnLblLucro_R50().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                        .totalLucroProperty().asString());
                getMeuLucroVolatilidade()[!isVol1s() ? VOL_50 : VOL_HZ50].bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].totalLucroProperty());

                getLblInvestido_R50().textProperty().unbind();
                getLblInvestido_R50().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                        .totalInvestidoProperty().asString());
                getLblInvestidoPorc_R50().textProperty().unbind();
                getLblInvestidoPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                                                .getTotalInvestido()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].totalInvestidoProperty()));

                getLblPremiacao_R50().textProperty().unbind();
                getLblPremiacao_R50().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                        .totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R50().textProperty().unbind();
                getLblPremiacaoPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                                                .getTotalPremiacao()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].totalPremiacaoProperty()));

                getLblLucro_R50().textProperty().unbind();
                getLblLucro_R50().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                        .totalLucroProperty().asString());
                getLblLucroPorc_R50().textProperty().unbind();
                getLblLucroPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_StopGain(getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50]
                                                .getTotalLucro()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_50 : VOL_HZ50].totalLucroProperty()));

            }
            case 3, 8 -> {

                if (getSymbolObservableList().size() > 5)
                    getTmodelTransacoes()[isVol1s() ? VOL_75 : VOL_HZ75].setTbvTransacoes(null);
                getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].setTbvTransacoes(getTbvTransacoes_R75());

                getTpnLblExecucoes_R75().textProperty().unbind();
                getTpnLblExecucoes_R75().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                        .qtdNExecucaoProperty().asString());

                getTpnLblVitorias_R75().textProperty().unbind();
                getTpnLblVitorias_R75().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdVitorias()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue().compareTo(getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue()) >= 0)
                        getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_75 : VOL_HZ75].setValue(getQtdVitorias()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].getQtdNVitoria(),
                            getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].qtdNVitoriaProperty(), getQtdVitorias()[!isVol1s() ? VOL_75 : VOL_HZ75]));

                getTpnLblDerrotas_R75().textProperty().unbind();
                getTpnLblDerrotas_R75().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdDerrotas()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue().compareTo(getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue()) >= 0)
                        getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_75 : VOL_HZ75].setValue(getQtdDerrotas()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].getQtdNDerrota(),
                            getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].qtdNDerrotaProperty(), getQtdDerrotas()[!isVol1s() ? VOL_75 : VOL_HZ75]));

                getTpnLblLucro_R75().textProperty().unbind();
                getTpnLblLucro_R75().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                        .totalLucroProperty().asString());
                getMeuLucroVolatilidade()[!isVol1s() ? VOL_75 : VOL_HZ75].bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].totalLucroProperty());

                getLblInvestido_R75().textProperty().unbind();
                getLblInvestido_R75().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                        .totalInvestidoProperty().asString());
                getLblInvestidoPorc_R75().textProperty().unbind();
                getLblInvestidoPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                                                .getTotalInvestido()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].totalInvestidoProperty()));

                getLblPremiacao_R75().textProperty().unbind();
                getLblPremiacao_R75().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                        .totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R75().textProperty().unbind();
                getLblPremiacaoPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                                                .getTotalPremiacao()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].totalPremiacaoProperty()));

                getLblLucro_R75().textProperty().unbind();
                getLblLucro_R75().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                        .totalLucroProperty().asString());
                getLblLucroPorc_R75().textProperty().unbind();
                getLblLucroPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_StopGain(getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75]
                                                .getTotalLucro()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_75 : VOL_HZ75].totalLucroProperty()));

            }
            case 4, 9 -> {

                if (getSymbolObservableList().size() > 5)
                    getTmodelTransacoes()[isVol1s() ? VOL_100 : VOL_HZ100].setTbvTransacoes(null);
                getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].setTbvTransacoes(getTbvTransacoes_R100());

                getTpnLblExecucoes_R100().textProperty().unbind();
                getTpnLblExecucoes_R100().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                        .qtdNExecucaoProperty().asString());

                getTpnLblVitorias_R100().textProperty().unbind();
                getTpnLblVitorias_R100().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdVitorias()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue().compareTo(getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue()) >= 0)
                        getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_100 : VOL_HZ100].setValue(getQtdVitorias()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].getQtdNVitoria(),
                            getQtdMaiorSeqVitoria()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].qtdNVitoriaProperty(), getQtdVitorias()[!isVol1s() ? VOL_100 : VOL_HZ100]));

                getTpnLblDerrotas_R100().textProperty().unbind();
                getTpnLblDerrotas_R100().textProperty().bind(Bindings.createStringBinding(() -> {
                    if (getQtdDerrotas()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue().compareTo(getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue()) >= 0)
                        getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_100 : VOL_HZ100].setValue(getQtdDerrotas()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue());
                    return String.format("%s[%s]",
                            getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].getQtdNDerrota(),
                            getQtdMaiorSeqDerrota()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue());
                }, getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].qtdNDerrotaProperty(), getQtdDerrotas()[!isVol1s() ? VOL_100 : VOL_HZ100]));

                getTpnLblLucro_R100().textProperty().unbind();
                getTpnLblLucro_R100().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                        .totalLucroProperty().asString());
                getMeuLucroVolatilidade()[!isVol1s() ? VOL_100 : VOL_HZ100].bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].totalLucroProperty());

                getLblInvestido_R100().textProperty().unbind();
                getLblInvestido_R100().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                        .totalInvestidoProperty().asString());
                getLblInvestidoPorc_R100().textProperty().unbind();
                getLblInvestidoPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                                                .getTotalInvestido()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].totalInvestidoProperty()));

                getLblPremiacao_R100().textProperty().unbind();
                getLblPremiacao_R100().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                        .totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R100().textProperty().unbind();
                getLblPremiacaoPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_Saldo(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                                                .getTotalPremiacao()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].totalPremiacaoProperty()));

                getLblLucro_R100().textProperty().unbind();
                getLblLucro_R100().textProperty().bind(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                        .totalLucroProperty().asString());
                getLblLucroPorc_R100().textProperty().unbind();
                getLblLucroPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("%s%%",
                                        Service_Mascara.getValorMoeda(getPorc_StopGain(getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100]
                                                .getTotalLucro()))),
                        getTmodelTransacoes()[!isVol1s() ? VOL_100 : VOL_HZ100].totalLucroProperty()));

            }
        }


        getTransacoesObservableList().addListener((ListChangeListener<? super Transacoes>) c -> {
            getLblTotalExecucoes().setText(String.valueOf(c.getList().size()));
            getLblTotalVitorias().setText(String.valueOf(c.getList().stream().filter(transacoes -> transacoes.isConsolidado())
                    .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0).count()));
            getLblTotalDerrotas().setText(String.valueOf(c.getList().stream().filter(transacoes -> transacoes.isConsolidado())
                    .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0).count()));
            getLblTotalInvestido().setText(String.valueOf(c.getList().stream()
                    .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add).negate()
                    .setScale(2, RoundingMode.HALF_UP)));
            getLblTotalPremiacao().setText(String.valueOf(c.getList().stream().filter(transacoes -> transacoes.isConsolidado())
                    .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP)));
            BigDecimal lucroTotalTemp = c.getList().stream().filter(transacoes -> transacoes.isConsolidado())
                    .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

            getLblTotalLucro().setText(lucroTotalTemp.toString());
            getLblTotalLucroPorc().setText(getPorc_StopGain(lucroTotalTemp).toString() + "%");
        });


        getTmodelTransacoes()[symbolId].setTransacoesObservableList(getTransacoesObservableList());
        getTmodelTransacoes()[symbolId].escutarTransacoesTabela();
        getTmodelTransacoes()[symbolId].tabela_preencher();

    }


    private void carregarObjetosFXML() {

        getCboVelocidadeTicksGrafico().getItems().setAll(new SimNao(false, "Não"), new SimNao(true, "sim"));
        getCboVelocidadeTicksGrafico().getSelectionModel().select(0);
        VOL_1SProperty().bind(Bindings.createBooleanBinding(() -> getCboVelocidadeTicksGrafico().getValue().isSim(),
                getCboVelocidadeTicksGrafico().valueProperty()));

        getCboQtdTicksGrafico().getItems().setAll(100, 75, 50, 25, 10);
        getCboQtdTicksGrafico().getSelectionModel().select(0);
        qtdTicksGraficoProperty().bind(getCboQtdTicksGrafico().valueProperty());

        getCboConta().setItems(getContaTokenDAO().getAll(ContaToken.class, "ativo=1", null)
                .stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboConta().getSelectionModel().select(-1);
        contaTokenProperty().bind(getCboConta().valueProperty());

        setRoboRelogio(new Timeline(
                new KeyFrame(Duration.seconds(1), event -> getLblRoboHoraAtual().setText(LocalDateTime.now()
                        .format(DTF_DATA_HORA_SEGUNDOS)))
        ));
        getRoboRelogio().setCycleCount(Animation.INDEFINITE);
        getRoboRelogio().play();

        getCboRobos().setItems(ROBOS.getList().stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboRobos().getSelectionModel().select(0);
        roboSelecionadoProperty().bind(getCboRobos().valueProperty());

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void carregarObjetosVolatilidade() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {

            getEstrategiaBotaoContrato()[symbolId] = new SimpleBooleanProperty(true);
            getEstrategiaBotaoComprar()[symbolId] = new SimpleBooleanProperty(true);
            getEstrategiaBotaoPausar()[symbolId] = new SimpleBooleanProperty(true);
            getEstrategiaBotaoStop()[symbolId] = new SimpleBooleanProperty(true);

            //**Listas**
            getError()[symbolId] = new SimpleObjectProperty<>();
            getHistoricoDeTicksAnaliseObservableList()[symbolId] = FXCollections.observableArrayList();
            getHistoricoDeTicksGraficoObservableList()[symbolId] = FXCollections.observableArrayList();
            getTransactionObservableList()[symbolId] = FXCollections.observableArrayList();

            //**Informacoes**
            getUltimoTick()[symbolId] = new SimpleObjectProperty<>();
            getUltimoDigito()[symbolId] = new SimpleIntegerProperty();
            getTickSubindo()[symbolId] = new SimpleBooleanProperty();

            //**Parametros negociação**
            getVolatilidadeAtiva()[symbolId] = new SimpleBooleanProperty();
            int finalSymbolId = symbolId;
            getVolatilidadeAtiva()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                System.out.printf("volatilidadeAtiva__%s:[%s]\n", getVolName()[finalSymbolId], n);
            });
            getVolatilidadeCompraAutorizada()[symbolId] = new SimpleBooleanProperty();
            getVolatilidadeCompraAutorizada()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                System.out.printf("volatilidadeCompraAutorizada__%s:[%s]\n", getVolName()[finalSymbolId], n);
            });
            geralvolatilidadeCompraAutorizadaProperty().addListener((ov, o, n) -> {
                if (n == null) return;
                System.out.printf("geralvolatilidadeCompraAutorizada:[%s]\n", n);
            });
            getVolatilidadeNegociando()[symbolId] = new SimpleBooleanProperty();
            getStakePadrao()[symbolId] = new SimpleObjectProperty<>();
            getMeuLucroVolatilidade()[symbolId] = new SimpleObjectProperty<>();
            getStakeContrato()[symbolId] = new SimpleObjectProperty<>();
            getLastPriceProposal()[symbolId] = new SimpleObjectProperty<>();
            getFatorMartingale()[symbolId] = new SimpleObjectProperty<>();
            getQtdDerrotas()[symbolId] = new SimpleIntegerProperty(0);
            getQtdMaiorSeqVitoria()[symbolId] = new SimpleIntegerProperty(0);
            getQtdVitorias()[symbolId] = new SimpleIntegerProperty(0);
            getQtdMaiorSeqDerrota()[symbolId] = new SimpleIntegerProperty(0);
            getRenovarTodosContratos()[symbolId] = new SimpleBooleanProperty(true);

            graficoBarras(symbolId);
        }

        Thread threadInicial = new Thread(getTaskWsBinary());
        threadInicial.setDaemon(true);
        threadInicial.start();

    }

    private void escutaObjetos_Variaveis() {

        VOL_1SProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            if (getGrafBar_R10().isVisible() || getGrafBar_HZ10().isVisible()) {
                getGrafBar_HZ10().setVisible(n);
                getGrafBar_R10().setVisible(!n);
            } else {
                getGrafLine_R10().setVisible(n);
                getGrafLine_HZ10().setVisible(!n);
            }
            if (getGrafBar_R25().isVisible() || getGrafBar_HZ25().isVisible()) {
                getGrafBar_HZ25().setVisible(n);
                getGrafBar_R25().setVisible(!n);
            } else {
                getGrafLine_R25().setVisible(n);
                getGrafLine_HZ25().setVisible(!n);
            }
            if (getGrafBar_R50().isVisible() || getGrafBar_HZ50().isVisible()) {
                getGrafBar_HZ50().setVisible(n);
                getGrafBar_R50().setVisible(!n);
            } else {
                getGrafLine_R50().setVisible(n);
                getGrafLine_HZ50().setVisible(!n);
            }
            if (getGrafBar_R75().isVisible() || getGrafBar_HZ75().isVisible()) {
                getGrafBar_HZ75().setVisible(n);
                getGrafBar_R75().setVisible(!n);
            } else {
                getGrafLine_R75().setVisible(n);
                getGrafLine_HZ75().setVisible(!n);
            }
            if (getGrafBar_R100().isVisible() || getGrafBar_HZ100().isVisible()) {
                getGrafBar_HZ100().setVisible(n);
                getGrafBar_R100().setVisible(!n);
            } else {
                getGrafLine_R100().setVisible(n);
                getGrafLine_HZ100().setVisible(!n);
            }
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                carregarObjetosFormulario(symbolId);
                escutaBotoes();
            }
        });

        qtdTicksGraficoProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            solicitarHistory();
            getyAxisBar_R10().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_HZ10().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_R25().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_HZ25().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_R50().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_HZ50().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_R75().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_HZ75().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_R100().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            getyAxisBar_HZ100().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
        });

        contaTokenProperty().addListener((ov, o, n) -> {
            if (n == null)
                authorizeProperty().setValue(null);
            else
                solicitarAutorizacaoAplicacao();
        });

        authorizeProperty().addListener((ov, o, n) -> {
            getLblProprietarioConta().setText(n != null
                    ? String.format("%s (%s)", n.getFullname(), n.getEmail())
                    : "");
            getLblIdConta().setText(n != null
                    ? n.getLoginid()
                    : "");
            getLblSaldoConta().setText(n != null
                    ? Service_Mascara.getValorFormatado(2, n.getBalance())
                    : "0.00");
            saldoInicialContaProperty().setValue(n.getBalance());
            getLblMoedaSaldo().setText(n != null
                    ? n.getCurrency()
                    : "");
            getLblSaldoInicial().setText("0.00");
            getLblTotalInvestido().setText("0.00");
            getLblTotalPremiacao().setText("0.00");
            getLblSaldoFinal().setText("0.00");

            //getLblHoraInicio().setText("");
        });

        roboHoraInicialProperty().addListener((ov, o, n) -> {
            if (n.intValue() > 0) {
                setRoboCronometro(0);
                setRoboCronometroAtivado(true);
                getLblRoboHoraInicial().setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(getRoboHoraInicial() / 1000),
                        TimeZone.getDefault().toZoneId()).format(DTF_DATA_HORA_SEGUNDOS));
            } else {
                setRoboCronometroAtivado(false);
            }
        });

        getLblRoboHoraAtual().textProperty().addListener((ov, o, n) -> {
            if (n == null || n.equals("") || !isRoboCronometroAtivado()) return;
            setRoboCronometro(System.currentTimeMillis() - getRoboHoraInicial());
            //setRoboCronometro(getRoboCronometro() + 1);
        });

        roboCronometroProperty().addListener((ov, o, n) -> {
            if (n.intValue() <= 0) return;
            getLblRoboCronometro().setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(n.longValue() / 1000),
                    TimeZone.getDefault().toZoneId()).format(DTF_MINUTOS_SEGUNDOS));
        });

        getLblRoboCronometro().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !isRoboCronometroAtivado()) {
                getLblRoboHoraInicial().setText("");
                getLblRoboCronometro().setText("");
            }
        });

        roboSelecionadoProperty().addListener((ov, o, n) -> {
            if (n == null || n.getDescricao() == "")
                setRoboEstrategia(null);
            else
                switch (n) {
                    case EVEN_ODD_01_PORCENTAGEM -> {
                        EvenOdd_01_Porcentagem evenOdd01Porcentagem = new EvenOdd_01_Porcentagem(this);
                        setRoboEstrategia(evenOdd01Porcentagem);
                    }
                    case EVEN_01_PORCENTAGEM -> setRoboEstrategia(new Even_01_Porcentagem(this));

                    case ODD_01_PORCENTAGEM -> {
                        Odd_01_Porcentagem odd01Porcentagem = new Odd_01_Porcentagem(this);
                        setRoboEstrategia(odd01Porcentagem);
                    }
                    case OVER_01 -> setRoboEstrategia(new Over_01(this));
                }
            carregaBotoesEstrategia();
        });

        transacoesAutorizadasProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            if (n)
                monitorarTransacoes();
        });

    }

    private void carregaBotoesEstrategia() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            if (!getVolatilidadeAtiva()[symbolId].getValue()) return;

            int finalSymbolId = symbolId;
            getEstrategiaBotaoContrato()[symbolId].setValue(true);
//            getEstrategiaBotaoPausar()[symbolId].setValue(true);
//            getEstrategiaBotaoComprar()[symbolId].setValue(true);
//            getEstrategiaBotaoStop()[symbolId].setValue(true);

            if (!getRoboSelecionado().equals(ROBOS.NULL))
                getEstrategiaBotaoContrato()[symbolId].setValue(false);

            getEstrategiaBotaoComprar()[finalSymbolId].bind(Bindings.createBooleanBinding(() ->
                            !getVolatilidadeCompraAutorizada()[finalSymbolId].getValue(),
                    getVolatilidadeCompraAutorizada()[finalSymbolId]));

            getEstrategiaBotaoPausar()[finalSymbolId].bind(Bindings.createBooleanBinding(() ->
                            !getEstrategiaBotaoContrato()[finalSymbolId].getValue()
                                    && !getVolatilidadeCompraAutorizada()[finalSymbolId].getValue(),
                    getEstrategiaBotaoContrato()[finalSymbolId]));

            getEstrategiaBotaoStop()[finalSymbolId].bind(getEstrategiaBotaoContrato()[finalSymbolId].not());

            switch (getRoboSelecionado()) {

                case NULL -> {
                    getEstrategiaBotaoContrato()[symbolId].setValue(true);
                }
                case EVEN_ODD_01_PORCENTAGEM -> {
                    EvenOdd_01_Porcentagem.getProposal()[symbolId][0].addListener((ov, o, n) ->
                            getEstrategiaBotaoContrato()[finalSymbolId].setValue(n != null
                                    || getAuthorize() == null));
                }

                case EVEN_01_PORCENTAGEM -> {
                    Even_01_Porcentagem.getProposal()[finalSymbolId].addListener((ov, o, n) ->
                            getEstrategiaBotaoContrato()[finalSymbolId].setValue(n != null
                                    || getAuthorize() == null));
                }

                case ODD_01_PORCENTAGEM -> {
                    Odd_01_Porcentagem.getProposal()[symbolId].addListener((ov, o, n) ->
                            getEstrategiaBotaoContrato()[finalSymbolId].setValue(n != null
                                    || getAuthorize() == null));
                }

                case OVER_01 -> {
                    Over_01.getProposal()[symbolId].addListener((ov, o, n) ->
                            getEstrategiaBotaoContrato()[finalSymbolId].setValue(n != null
                                    || getAuthorize() == null));
                }
            }
        }

    }

    private void escutaBotoes() {

        /** binding botões volatilidade 10 */
        getBtnContratos_R10().disableProperty().unbind();
        getBtnContratos_R10().disableProperty().bind(getEstrategiaBotaoContrato()[!isVol1s() ? VOL_10 : VOL_HZ10]);

        getBtnPausar_R10().disableProperty().unbind();
        getBtnPausar_R10().disableProperty().bind(getEstrategiaBotaoPausar()[!isVol1s() ? VOL_10 : VOL_HZ10]);

        getBtnComprar_R10().disableProperty().unbind();
        getBtnComprar_R10().disableProperty().bind(getEstrategiaBotaoComprar()[!isVol1s() ? VOL_10 : VOL_HZ10]);

        getBtnStop_R10().disableProperty().unbind();
        getBtnStop_R10().disableProperty().bind(getEstrategiaBotaoStop()[!isVol1s() ? VOL_10 : VOL_HZ10]);

        /** binding botões volatilidade 25 */
        getBtnContratos_R25().disableProperty().unbind();
        getBtnContratos_R25().disableProperty().bind(getEstrategiaBotaoContrato()[!isVol1s() ? VOL_25 : VOL_HZ25]);

        getBtnPausar_R25().disableProperty().unbind();
        getBtnPausar_R25().disableProperty().bind(getEstrategiaBotaoPausar()[!isVol1s() ? VOL_25 : VOL_HZ25]);

        getBtnComprar_R25().disableProperty().unbind();
        getBtnComprar_R25().disableProperty().bind(getEstrategiaBotaoComprar()[!isVol1s() ? VOL_25 : VOL_HZ25]);

        getBtnStop_R25().disableProperty().unbind();
        getBtnStop_R25().disableProperty().bind(getEstrategiaBotaoStop()[!isVol1s() ? VOL_25 : VOL_HZ25]);

        /** binding botões volatilidade 50 */
        getBtnContratos_R50().disableProperty().unbind();
        getBtnContratos_R50().disableProperty().bind(getEstrategiaBotaoContrato()[!isVol1s() ? VOL_50 : VOL_HZ50]);

        getBtnPausar_R50().disableProperty().unbind();
        getBtnPausar_R50().disableProperty().bind(getEstrategiaBotaoPausar()[!isVol1s() ? VOL_50 : VOL_HZ50]);

        getBtnComprar_R50().disableProperty().unbind();
        getBtnComprar_R50().disableProperty().bind(getEstrategiaBotaoComprar()[!isVol1s() ? VOL_50 : VOL_HZ50]);

        getBtnStop_R50().disableProperty().unbind();
        getBtnStop_R50().disableProperty().bind(getEstrategiaBotaoStop()[!isVol1s() ? VOL_50 : VOL_HZ50]);

        /** binding botões volatilidade 75 */
        getBtnContratos_R75().disableProperty().unbind();
        getBtnContratos_R75().disableProperty().bind(getEstrategiaBotaoContrato()[!isVol1s() ? VOL_75 : VOL_HZ75]);

        getBtnPausar_R75().disableProperty().unbind();
        getBtnPausar_R75().disableProperty().bind(getEstrategiaBotaoPausar()[!isVol1s() ? VOL_75 : VOL_HZ75]);

        getBtnComprar_R75().disableProperty().unbind();
        getBtnComprar_R75().disableProperty().bind(getEstrategiaBotaoComprar()[!isVol1s() ? VOL_75 : VOL_HZ75]);

        getBtnStop_R75().disableProperty().unbind();
        getBtnStop_R75().disableProperty().bind(getEstrategiaBotaoStop()[!isVol1s() ? VOL_75 : VOL_HZ75]);

        /** binding botões volatilidade 100 */
        getBtnContratos_R100().disableProperty().unbind();
        getBtnContratos_R100().disableProperty().bind(getEstrategiaBotaoContrato()[!isVol1s() ? VOL_100 : VOL_HZ100]);

        getBtnPausar_R100().disableProperty().unbind();
        getBtnPausar_R100().disableProperty().bind(getEstrategiaBotaoPausar()[!isVol1s() ? VOL_100 : VOL_HZ100]);

        getBtnComprar_R100().disableProperty().unbind();
        getBtnComprar_R100().disableProperty().bind(getEstrategiaBotaoComprar()[!isVol1s() ? VOL_100 : VOL_HZ100]);

        getBtnStop_R100().disableProperty().unbind();
        getBtnStop_R100().disableProperty().bind(getEstrategiaBotaoStop()[!isVol1s() ? VOL_100 : VOL_HZ100]);

        carregaBotoesEstrategia();

        getBtnContratos().disableProperty().bind(Bindings.createBooleanBinding(() ->
                        (getBtnContratos_R10().isDisable() && getBtnContratos_R25().isDisable()
                                && getBtnContratos_R50().isDisable() && getBtnContratos_R75().isDisable()
                                && getBtnContratos_R100().isDisable()),
                getBtnContratos_R10().disableProperty(), getBtnContratos_R25().disableProperty(),
                getBtnContratos_R50().disableProperty(), getBtnContratos_R75().disableProperty(),
                getBtnContratos_R100().disableProperty()));

        getBtnIniciar().disableProperty().bind(Bindings.createBooleanBinding(() ->
                        (getBtnPausar_R10().isDisable() && getBtnPausar_R25().isDisable()
                                && getBtnPausar_R50().isDisable() && getBtnPausar_R75().isDisable()
                                && getBtnPausar_R100().isDisable()),
                getBtnPausar_R10().disableProperty(), getBtnPausar_R25().disableProperty(),
                getBtnPausar_R50().disableProperty(), getBtnPausar_R75().disableProperty(),
                getBtnPausar_R100().disableProperty()));

        getBtnPausar().disableProperty().bind(getBtnIniciar().disableProperty());

        getBtnStop().disableProperty().bind(Bindings.createBooleanBinding(() ->
                        (getBtnStop_R10().isDisable() && getBtnStop_R25().isDisable()
                                && getBtnStop_R50().isDisable() && getBtnStop_R75().isDisable()
                                && getBtnStop_R100().isDisable()),
                getBtnStop_R10().disableProperty(), getBtnStop_R25().disableProperty(),
                getBtnStop_R50().disableProperty(), getBtnStop_R75().disableProperty(),
                getBtnStop_R100().disableProperty()));

        getBtnContratos_R10().setOnAction(event -> getRoboEstrategia().atualizaNovosParametros(!isVol1s() ? VOL_10 : VOL_HZ10));
        getBtnContratos_R25().setOnAction(event -> getRoboEstrategia().atualizaNovosParametros(!isVol1s() ? VOL_25 : VOL_HZ25));
        getBtnContratos_R50().setOnAction(event -> getRoboEstrategia().atualizaNovosParametros(!isVol1s() ? VOL_50 : VOL_HZ50));
        getBtnContratos_R75().setOnAction(event -> getRoboEstrategia().atualizaNovosParametros(!isVol1s() ? VOL_75 : VOL_HZ75));
        getBtnContratos_R100().setOnAction(event -> getRoboEstrategia().atualizaNovosParametros(!isVol1s() ? VOL_100 : VOL_HZ100));
        getBtnContratos().setOnAction(event -> {
            if (!getRoboSelecionado().equals(ROBOS.OVER_01))
                setGeralvolatilidadeCompraAutorizada(true);
            getRoboEstrategia().atualizaNovosParametros(null);
        });

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            int finalSymbolId = symbolId;
            getVolatilidadeCompraAutorizada()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                switch (finalSymbolId) {
                    case 0, 5 -> getBtnPausar_R10().setText(n ? "Pausar" : "Iniciar");
                    case 1, 6 -> getBtnPausar_R25().setText(n ? "Pausar" : "Iniciar");
                    case 2, 7 -> getBtnPausar_R50().setText(n ? "Pausar" : "Iniciar");
                    case 3, 8 -> getBtnPausar_R75().setText(n ? "Pausar" : "Iniciar");
                    case 4, 9 -> getBtnPausar_R100().setText(n ? "Pausar" : "Iniciar");
                }
            });
        }

        getBtnPausar_R10().setOnAction(event -> {
            if (getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue())
                getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_10 : VOL_HZ10].setValue(
                        !getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue());
        });
        getBtnPausar_R25().setOnAction(event -> {
            if (getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue())
                getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_25 : VOL_HZ25].setValue(
                        !getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_25 : VOL_HZ25].getValue());
        });
        getBtnPausar_R50().setOnAction(event -> {
            if (getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue())
                getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_50 : VOL_HZ50].setValue(
                        !getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_50 : VOL_HZ50].getValue());
        });
        getBtnPausar_R75().setOnAction(event -> {
            if (getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue())
                getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_75 : VOL_HZ75].setValue(
                        !getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_75 : VOL_HZ75].getValue());
        });
        getBtnPausar_R100().setOnAction(event -> {
            if (getVolatilidadeAtiva()[!isVol1s() ? VOL_10 : VOL_HZ10].getValue())
                getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_100 : VOL_HZ100].setValue(
                        !getVolatilidadeCompraAutorizada()[!isVol1s() ? VOL_100 : VOL_HZ100].getValue());
        });
        getBtnIniciar().setOnAction(event -> {
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                if (!getVolatilidadeAtiva()[symbolId].getValue()) continue;
                getVolatilidadeCompraAutorizada()[symbolId].setValue(true);
            }
        });
        getBtnPausar().setOnAction(event -> {
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                if (!getVolatilidadeAtiva()[symbolId].getValue()) continue;
                getVolatilidadeCompraAutorizada()[symbolId].setValue(false);
            }
        });

        getBtnComprar_R10().setOnAction(event -> getRoboEstrategia().solicitaCompraContrato(!isVol1s() ? VOL_10 : VOL_HZ10));
        getBtnComprar_R25().setOnAction(event -> getRoboEstrategia().solicitaCompraContrato(!isVol1s() ? VOL_25 : VOL_HZ25));
        getBtnComprar_R50().setOnAction(event -> getRoboEstrategia().solicitaCompraContrato(!isVol1s() ? VOL_50 : VOL_HZ50));
        getBtnComprar_R75().setOnAction(event -> getRoboEstrategia().solicitaCompraContrato(!isVol1s() ? VOL_75 : VOL_HZ75));
        getBtnComprar_R100().setOnAction(event -> getRoboEstrategia().solicitaCompraContrato(!isVol1s() ? VOL_100 : VOL_HZ100));


        getBtnStop_R10().setOnAction(event -> botaoStop(!isVol1s() ? VOL_10 : VOL_HZ10));
        getBtnStop_R25().setOnAction(event -> botaoStop(!isVol1s() ? VOL_25 : VOL_HZ25));
        getBtnStop_R50().setOnAction(event -> botaoStop(!isVol1s() ? VOL_50 : VOL_HZ50));
        getBtnStop_R75().setOnAction(event -> botaoStop(!isVol1s() ? VOL_75 : VOL_HZ75));
        getBtnStop_R100().setOnAction(event -> botaoStop(!isVol1s() ? VOL_100 : VOL_HZ100));
        getBtnStop().setOnAction(event -> {
            setGeralvolatilidadeCompraAutorizada(false);
            for (int symbolId = 0; symbolId < Operacao.getSymbolObservableList().size(); symbolId++) {
                if (!Operacao.getVolatilidadeAtiva()[symbolId].getValue()) continue;
                botaoStop(symbolId);
            }
        });
    }

    private void botaoStop(Integer symbolId) {
        getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
        getRoboEstrategia().stopEstrategia(symbolId);
    }

    private void escutaEstrategias() {
        paramEstrategiaCarregadosProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            if (!n)
                limpaParamentosNegociacaoBasicos();
        });

        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getStakePadrao()[symbolId].addListener((ov, o, n) -> {
                if (n == null) return;
                getStakeContrato()[symbolId].setValue(n);
            });
        }
    }

    public static void limpaParamentosNegociacaoBasicos() {
        setQtdStopLoss(0);
        setVlrStopGain(BigDecimal.ZERO);
        setVlrStopLoss(BigDecimal.ZERO);

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            getStakePadrao()[symbolId].setValue(BigDecimal.ZERO);
            getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
            getFatorMartingale()[symbolId].setValue(BigDecimal.ZERO);
            getQtdDerrotas()[symbolId].setValue(0);
            getQtdVitorias()[symbolId].setValue(0);
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

    private void solicitarAutorizacaoAplicacao() {

        if (contaTokenProperty().getValue() == null) return;
        String jsonAutorizacao = String.format("{\"authorize\":\"%s\"}", getContaToken().getTokenApi());
        getWsClient().getWebSocket().send(jsonAutorizacao);

    }

    private void solicitarTicks() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            String jsonTickStream = Util_Json.getJson_from_Object(new TickStream(getVolName()[symbolId]));
            getWsClient().getWebSocket().send(jsonTickStream);
        }

    }

    private void solicitarHistory() {

        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(getVolName()[symbolId], getQtdTicksAnalisar()));
            getWsClient().getWebSocket().send(jsonHistory);
        }

    }

    private static void solicitarTransacoes() {

        String jsonTransacoes = Util_Json.getJson_from_Object(new TransactionsStream());
        getWsClient().getWebSocket().send(jsonTransacoes);
        setTransacoesAutorizadas(true);

    }

    public static void solicitarEnvioContrato(Integer symbol, String jsonPriceProposal) {

        if (!getVolatilidadeAtiva()[symbol].getValue()) return;
        if (!isTransacoesAutorizadas())
            solicitarTransacoes();
        getWsClient().getWebSocket().send(jsonPriceProposal);

    }

    public static void solicitarCompraContrato(Integer symbolId, Proposal proposal) {
        if (!getVolatilidadeAtiva()[symbolId].getValue()
                || !getVolatilidadeCompraAutorizada()[symbolId].getValue()
                || (isGeralvolatilidadeCompraAutorizada() && isGeralvolatilidadeNegociando())
            //        || proposal.getId() == null
        ) return;
//        System.out.printf("proposal: %s\n", proposal);
        try {
            //??? Verifica Limite
            getVolatilidadeNegociando()[symbolId].setValue(true);
            setGeralvolatilidadeNegociando(isGeralvolatilidadeCompraAutorizada());
            String jsonBuyContrato = Util_Json.getJson_from_Object(new BuyContract(proposal));
            getWsClient().getWebSocket().send(jsonBuyContrato);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void negociarNovamente(Integer symbolId, boolean isSoros) {
        getVolatilidadeNegociando()[symbolId].addListener((ov, o, n) -> {
            if (!getVolatilidadeAtiva()[symbolId].getValue()) return;
            if (!o || n) return;
            if (!n) {
                if (isGeralvolatilidadeCompraAutorizada())
                    if (isGeralvolatilidadeNegociando())
                        setGeralvolatilidadeNegociando(false);
                atualizaStakesNovaNegociacao(symbolId, isSoros);
//                if (getVolatilidadeCompraAutorizada()[symbolId].getValue()) {
                getRoboEstrategia().definicaoDeContrato(symbolId);
//                }
            }
        });
    }

    private static void atualizaStakesNovaNegociacao(Integer symbolId, boolean isSoros) {
        if (!getVolatilidadeCompraAutorizada()[symbolId].getValue()) return;
        BigDecimal ultLucro = getTransacoesObservableList().stream()
                .filter(transacoes -> transacoes.getSymbol().getId() == symbolId + 1)
                .sorted(Comparator.comparing(Transacoes::getDataHoraCompra).reversed())
                .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                .findFirst().get();

        if (ultLucro.compareTo(BigDecimal.ZERO) > 0) {
            getRenovarTodosContratos()[symbolId].setValue(getQtdDerrotas()[symbolId].getValue().compareTo(0) > 0);
            getQtdVitorias()[symbolId].setValue(getQtdVitorias()[symbolId].getValue() + 1);
            getQtdDerrotas()[symbolId].setValue(0);
            if (isSoros)
                getStakeContrato()[symbolId].setValue(getStakeContrato()[symbolId].getValue().add(ultLucro));
            else
                getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
        } else {
            getRenovarTodosContratos()[symbolId].setValue(true);
            getQtdDerrotas()[symbolId].setValue(getQtdDerrotas()[symbolId].getValue() + 1);
            getQtdVitorias()[symbolId].setValue(0);
            if (getQtdLossResetStake() > 0
                    && getQtdDerrotas()[symbolId].getValue().compareTo(getQtdLossResetStake()) >= 0)
                getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
            else
                getStakeContrato()[symbolId].setValue(
                        getStakeContrato()[symbolId].getValue()
                                .add(getStakeContrato()[symbolId].getValue().multiply(getFatorMartingale()[symbolId].getValue()))
                                .setScale(2, RoundingMode.HALF_UP));
        }

        if (getMeuLucroVolatilidade()[symbolId].getValue().compareTo(getVlrStopGain()) >= 0
                || getQtdDerrotas()[symbolId].getValue().compareTo(getQtdStopLoss()) >= 0) {
            getVolatilidadeCompraAutorizada()[symbolId].setValue(false);
            if (!isGeralvolatilidadeCompraAutorizada()) return;
            int symbol_Id = symbolId;
            if (symbolId < 4)
                symbol_Id = symbolId + 1;
            else
                symbol_Id = 0;
            if (!getVolatilidadeAtiva()[symbol_Id].getValue())
                while (!getVolatilidadeAtiva()[symbol_Id].getValue()) {
                    if (symbolId < 4)
                        symbol_Id = symbolId + 1;
                    else
                        symbol_Id = 0;
                    if (!getVolatilidadeAtiva()[symbol_Id].getValue()) continue;
                }
//            getRoboEstrategia().definicaoDeContrato(symbol_Id);
            getVolatilidadeCompraAutorizada()[symbol_Id].setValue(true);
        }

    }

    public static void setParametrosNegociacao(Integer symbolId, BigDecimal stakePadrao, Integer tempoContrato, DURATION_UNIT duracaoContrato,
                                               BigDecimal vlrStopGain, BigDecimal vlrStopLoss, BigDecimal fatorMartingale,
                                               Integer qtdStopLoss, Integer qtdLossResetStake) {
        if ((isVol1s() && symbolId < 5) || (!isVol1s() && symbolId >= 5)) return;
        getStakePadrao()[symbolId].setValue(stakePadrao);
        getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
        getFatorMartingale()[symbolId].setValue(fatorMartingale);
        setQtdStopLoss(qtdStopLoss);

        setTempoDeContrato(tempoContrato);
        setDurationUnit(duracaoContrato);
        setVlrStopGain(vlrStopGain == null
                ? getSaldoInicialConta().multiply(new BigDecimal(0.03))
                : vlrStopGain);
        setVlrStopLoss(vlrStopLoss == null
                ? getSaldoInicialConta().multiply(new BigDecimal(0.08))
                : vlrStopLoss);

        setQtdLossResetStake(qtdLossResetStake == null
                ? 0 : qtdLossResetStake);

    }


    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private Task getTaskWsBinary() {
        Task taskWsBinary = new Task() {
            @Override
            protected Object call() throws Exception {
                getWsClient().connect();

                wsConectadoProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n) {
                            solicitarTicks();
                            monitorarTicks();
                            carregarGraficos();
                            solicitarHistory();
                        } else {
                            getBtnStop().fire();
                            new Service_Alert("Conexão fechou", "Conexão com a binary foi fechada!!", null);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                return null;
            }
        };
        return taskWsBinary;
    }

    private void graficoBarras(Integer symbolId) {

        getGrafBarListDataDigitos_R()[symbolId] = FXCollections.observableArrayList();
        getGrafBarListValorDigito_R()[symbolId] = FXCollections.observableArrayList();
        getGrafBarVolatilidade_R()[symbolId] = new XYChart.Series<>();

        for (int digito = 0; digito < 10; digito++) {
            getGrafBarTxtDigito_R()[symbolId][digito] = new Text("");
            getGrafBarTxtDigito_R()[symbolId][digito].setFont(Font.font("Arial", 10));
            getGrafBarTxtDigito_R()[symbolId][digito].setStyle("-fx-text-fill: white;");
            getGrafBarListValorDigito_R()[symbolId].add(digito, new SimpleLongProperty(0));
            getGrafBarListDataDigitos_R()[symbolId].add(digito, new Data<>(String.valueOf(digito), 0));
            int finalDigito = digito;
            getGrafBarListValorDigito_R()[symbolId].get(digito).addListener((ov, o, n) -> {
                if (n == null) return;
                Double porcentagem = 0.;
                if (n.intValue() != 0)
                    porcentagem = (n.intValue() / (getQtdTicksGrafico() / 100.));
                getGrafBarListDataDigitos_R()[symbolId].get(finalDigito).setYValue(porcentagem.intValue());
                getGrafBarTxtDigito_R()[symbolId][finalDigito].setText(String.format("%s%%", porcentagem.intValue()));
//                if (n.intValue() >= getGrafMaiorQtd()[symbolId].getValue()) {
//                    getGrafBarListDataDigitos_R()[symbolId].get(finalDigito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MAIOR);
//                } else if (n.intValue() <= getGrafMenorQtd()[symbolId].getValue()) {
//                    getGrafBarListDataDigitos_R()[symbolId].get(finalDigito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MENOR);
//                } else {
//                    getGrafBarListDataDigitos_R()[symbolId].get(finalDigito).getNode().setStyle(STYLE_GRAF_BARRAS_DEFAULT);
//                }
            });
        }

        switch (symbolId) {
            case 0, 5 -> {
                if (symbolId < 5) {
                    getyAxisBar_R10().setUpperBound(25);
                    getGrafBar_R10().getData().add(getGrafBarVolatilidade_R()[VOL_10]);
                    getGrafBar_R10().setVisible(true);
                } else {
                    getyAxisBar_HZ10().setUpperBound(25);
                    getGrafBar_HZ10().getData().add(getGrafBarVolatilidade_R()[VOL_HZ10]);
                    getGrafBar_HZ10().setVisible(false);
                }
            }
            case 1, 6 -> {
                if (symbolId < 5) {
                    getyAxisBar_R25().setUpperBound(25);
                    getGrafBar_R25().getData().add(getGrafBarVolatilidade_R()[VOL_25]);
                    getGrafBar_R25().setVisible(true);
                } else {
                    getyAxisBar_HZ25().setUpperBound(25);
                    getGrafBar_HZ25().getData().add(getGrafBarVolatilidade_R()[VOL_HZ25]);
                    getGrafBar_HZ25().setVisible(false);
                }
            }
            case 2, 7 -> {
                if (symbolId < 5) {
                    getyAxisBar_R50().setUpperBound(25);
                    getGrafBar_R50().getData().add(getGrafBarVolatilidade_R()[VOL_50]);
                    getGrafBar_R50().setVisible(true);
                } else {
                    getyAxisBar_HZ50().setUpperBound(25);
                    getGrafBar_HZ50().getData().add(getGrafBarVolatilidade_R()[VOL_HZ50]);
                    getGrafBar_HZ50().setVisible(false);
                }
            }
            case 3, 8 -> {
                if (symbolId < 5) {
                    getyAxisBar_R75().setUpperBound(25);
                    getGrafBar_R75().getData().add(getGrafBarVolatilidade_R()[VOL_75]);
                    getGrafBar_R75().setVisible(true);
                } else {
                    getyAxisBar_HZ75().setUpperBound(25);
                    getGrafBar_HZ75().getData().add(getGrafBarVolatilidade_R()[VOL_HZ75]);
                    getGrafBar_HZ75().setVisible(false);
                }
            }
            case 4, 9 -> {
                if (symbolId < 5) {
                    getyAxisBar_R100().setUpperBound(25);
                    getGrafBar_R100().getData().add(getGrafBarVolatilidade_R()[VOL_100]);
                    getGrafBar_R100().setVisible(true);
                } else {
                    getyAxisBar_HZ100().setUpperBound(25);
                    getGrafBar_HZ100().getData().add(getGrafBarVolatilidade_R()[VOL_HZ100]);
                    getGrafBar_HZ100().setVisible(false);
                }
            }
        }

    }

    private void graficoLinhas(Integer symbolId) {

        getGrafLineListDataDigitos_R()[symbolId] = FXCollections.observableArrayList();
        getGrafLineListValorDigito_R()[symbolId] = FXCollections.observableArrayList();
        getGrafLineVolatilidade_R()[symbolId] = new XYChart.Series<>(getGrafLineListDataDigitos_R()[symbolId]);

        getGrafLineListValorDigito_R()[symbolId].addListener((ListChangeListener<? super HistoricoDeTicks>) c -> {
            while (c.next()) {
                for (HistoricoDeTicks tick : c.getRemoved()) {
                    getGrafLineListDataDigitos_R()[symbolId].remove(0);
                }

                for (HistoricoDeTicks tick : c.getAddedSubList()) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss");
                    String hora = dtf.format(LocalDateTime.ofInstant(Instant.ofEpochSecond(tick.getTime()),
                            TimeZone.getDefault().toZoneId())
                            .toLocalTime());
                    getGrafLineListDataDigitos_R()[symbolId].add(new Data<>(hora, tick.getPrice().doubleValue()));
                }
            }
        });

        switch (symbolId) {
            case 0, 5 -> {
                if (symbolId < 5) {
                    getyAxisLine_R10().setUpperBound(25);
                    getGrafLine_R10().getData().add(getGrafLineVolatilidade_R()[VOL_10]);
                    getGrafLine_R10().setVisible(true);
                } else {
                    getyAxisLine_HZ10().setUpperBound(25);
                    getGrafLine_HZ10().getData().add(getGrafLineVolatilidade_R()[VOL_HZ10]);
                    getGrafLine_HZ10().setVisible(true);
                }
            }
            case 1, 6 -> {
                if (symbolId < 5) {
                    getyAxisLine_R25().setUpperBound(25);
                    getGrafLine_R25().getData().add(getGrafLineVolatilidade_R()[VOL_25]);
                    getGrafLine_R25().setVisible(true);
                } else {
                    getyAxisLine_HZ25().setUpperBound(25);
                    getGrafLine_HZ25().getData().add(getGrafLineVolatilidade_R()[VOL_HZ25]);
                    getGrafLine_HZ25().setVisible(true);
                }
            }
            case 2, 7 -> {
                if (symbolId < 5) {
                    getyAxisLine_R50().setUpperBound(25);
                    getGrafLine_R50().getData().add(getGrafLineVolatilidade_R()[VOL_50]);
                    getGrafLine_R50().setVisible(true);
                } else {
                    getyAxisLine_HZ50().setUpperBound(25);
                    getGrafLine_HZ50().getData().add(getGrafLineVolatilidade_R()[VOL_HZ50]);
                    getGrafLine_HZ50().setVisible(true);
                }
            }
            case 3, 8 -> {
                if (symbolId < 5) {
                    getyAxisLine_R75().setUpperBound(25);
                    getGrafLine_R75().getData().add(getGrafLineVolatilidade_R()[VOL_75]);
                    getGrafLine_R75().setVisible(true);
                } else {
                    getyAxisLine_HZ75().setUpperBound(25);
                    getGrafLine_HZ75().getData().add(getGrafLineVolatilidade_R()[VOL_HZ75]);
                    getGrafLine_HZ75().setVisible(true);
                }
            }
            case 4, 9 -> {
                if (symbolId < 5) {
                    getyAxisLine_R100().setUpperBound(25);
                    getGrafLine_R100().getData().add(getGrafLineVolatilidade_R()[VOL_100]);
                    getGrafLine_R100().setVisible(true);
                } else {
                    getyAxisLine_HZ100().setUpperBound(25);
                    getGrafLine_HZ100().getData().add(getGrafLineVolatilidade_R()[VOL_HZ100]);
                    getGrafLine_HZ100().setVisible(true);
                }
            }
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

    private void displayLabelForData(XYChart.Data<String, Number> data, Text text) {
        Platform.runLater(() -> {
            if (data == null || text == null) return;
            final Node node = data.getNode();
            if (node == null) return;
            ((Group) node.getParent()).getChildren().add(text);
            node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
                text.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - 6.));
                text.setLayoutY(Math.round(bounds.getMinY() - 12. * 0.5));
                text.setFill(Color.WHITE);
            });
        });
    }

    private void atualizaCoresGrafico(Integer symbolId) {
        for (int digito = 0; digito < 10; digito++) {
            if (getGrafBarListValorDigito_R()[symbolId].get(digito).getValue().intValue() >= getGrafDigitoMaiorQtd()[symbolId].getValue())
                getGrafBarListDataDigitos_R()[symbolId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MAIOR);
            else if (getGrafBarListValorDigito_R()[symbolId].get(digito).getValue().intValue() <= getGrafDigitoMenorQtd()[symbolId].getValue())
                getGrafBarListDataDigitos_R()[symbolId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MENOR);
            else
                getGrafBarListDataDigitos_R()[symbolId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DEFAULT);
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

    private void monitorarTicks() {
        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getUltimoTick()[symbolId].addListener((ov, o, n) -> {
                Platform.runLater(() -> {
                    Map<Integer, Long> vlrDigitos = getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .map(HistoricoDeTicks::getUltimoDigito)
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    getGrafDigitoMenorQtd()[symbolId].setValue(Collections.min(vlrDigitos.values()));
                    getGrafDigitoMaiorQtd()[symbolId].setValue(Collections.max(vlrDigitos.values()));

                    for (int digito = 0; digito < 10; digito++) {
                        getGrafBarListValorDigito_R()[symbolId].get(digito).setValue(
                                vlrDigitos.containsKey(digito) ? vlrDigitos.get(digito) : 0L);
                        if (!vlrDigitos.containsKey(digito))
                            getGrafDigitoMenorQtd()[symbolId].setValue(0);
                    }
                    if (getHistoricoDeTicksGraficoObservableList()[symbolId].size() > 1)
                        getTickSubindo()[symbolId].setValue(
                                getHistoricoDeTicksGraficoObservableList()[symbolId].get(0).getPrice()
                                        .compareTo(getHistoricoDeTicksGraficoObservableList()[symbolId].get(1).getPrice()) >= 0);

                    Transacoes transacoesTemp;
                    if (getTransacoesObservableList().size() > 0
                            && (transacoesTemp = getTransacoesObservableList().stream()
                            .filter(transacoes -> transacoes.getSymbol().getId() == symbolId + 1
                                    && (transacoes.getTickCompra() == null
                                    || transacoes.getTickVenda() == null))
                            .findFirst().orElse(null)) != null) {
                        int index = getTransacoesObservableList().indexOf(transacoesTemp);
                        try {
                            if (transacoesTemp.getTickCompra() == null) {
                                transacoesTemp.setTickCompra(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                                        .filter(historicoDeTicks -> historicoDeTicks.getTime() >= transacoesTemp.getDataHoraCompra())
                                        .findFirst().get().getPrice());
                            } else {
                                String contrato = transacoesTemp.getContract_type().toLowerCase();
                                transacoesTemp.setTickVenda(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                                        .filter(historicoDeTicks -> {
                                            if (contrato.contains("call") || contrato.contains("put"))
                                                return historicoDeTicks.getTime() > transacoesTemp.getDataHoraExpiry();
                                            return historicoDeTicks.getTime() >= transacoesTemp.getDataHoraExpiry();
                                        }).sorted(Comparator.comparing(HistoricoDeTicks::getTime))
                                        .findFirst().get().getPrice());
                                if (transacoesTemp.getTickCompra() != null && transacoesTemp.getTickVenda() != null) {
                                    getTransacoesDAO().merger(transacoesTemp);
                                }
                            }
                        } catch (Exception ex) {
                            if (!(ex instanceof NullPointerException) && !(ex instanceof NoSuchElementException))
                                ex.printStackTrace();
                        } finally {
                            getTransacoesObservableList().set(index, transacoesTemp);
                        }
                    }

                    atualizaCoresGrafico(symbolId);
                });
            });
        }
    }

    private void monitorarTransacoes() {
        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getTransactionObservableList()[symbolId].addListener((ListChangeListener<? super Transaction>) c -> {
                while (c.next())
                    for (Transaction transaction : c.getAddedSubList())
                        try {
                            ACTION action = ACTION.valueOf(transaction.getAction().toUpperCase());
                            if (action != null) {
                                Platform.runLater(() -> {
                                    getLblSaldoFinal().setText(
                                            Service_Mascara.getValorMoeda(transaction.getBalance().setScale(2, RoundingMode.HALF_UP)));
                                    switch (action) {
                                        case BUY -> {
                                            getTransacao()[symbolId].setValue(new Transacoes(getSymbolObservableList().get(symbolId)));
                                            getTransacao()[symbolId].getValue().newTransacao_BUY(transaction);
                                        }
                                        case SELL -> {
                                            getTransacao()[symbolId].getValue().newTransacao_SELL(transaction);
                                            getVolatilidadeNegociando()[symbolId].setValue(false);
                                        }
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            if (!(ex instanceof NullPointerException) && !(ex instanceof IllegalStateException))
                                ex.printStackTrace();
                            getVolatilidadeNegociando()[symbolId].setValue(false);
                        }
            });
        }
    }

    private void carregarGraficos() {
        Platform.runLater(() -> {
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                for (int digito = 0; digito < 10; digito++) {
                    getGrafBarVolatilidade_R()[symbolId].getData().add(getGrafBarListDataDigitos_R()[symbolId].get(digito));
                    getGrafBarVolatilidade_R()[symbolId].setName(String.valueOf(digito));
                    displayLabelForData(getGrafBarListDataDigitos_R()[symbolId].get(digito),
                            getGrafBarTxtDigito_R()[symbolId][digito]);
                }
            }
        });
    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public static Integer getSymbolId(String symbol) {

        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            if (symbol.equals(getVolName()[i]))
                return i;
        }
        return null;

    }

    public static Symbol getSymbol(String symbol) {

        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            if (symbol.equals(getVolName()[i]))
                return getSymbolObservableList().get(i);
        }
        return null;

    }

    private BigDecimal getPorc_Saldo(String strVlr) {

        BigDecimal vlr = BigDecimal.ZERO;
        try {
            vlr = new BigDecimal(strVlr.replace(".", "")).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            if (!(ex instanceof NumberFormatException))
                ex.printStackTrace();
        }
        return getPorc_Saldo(vlr);

    }

    private BigDecimal getPorc_Saldo(BigDecimal vlr) {

        if (vlr.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return vlr.divide(getSaldoInicialConta(), 5, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);

    }

    private BigDecimal getPorc_StopGain(String strVlr) {

        BigDecimal vlr = BigDecimal.ZERO;
        try {
            vlr = new BigDecimal(strVlr.replace(".", "")).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            if (!(ex instanceof NumberFormatException))
                ex.printStackTrace();
        }
        return getPorc_Saldo(vlr);

    }

    private BigDecimal getPorc_StopGain(BigDecimal vlr) {

        if (vlr.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return vlr.divide(getVlrStopGain(), 5, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);

    }


    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */


    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public static SymbolDAO getSymbolDAO() {
        return symbolDAO;
    }

    public static void setSymbolDAO(SymbolDAO symbolDAO) {
        Operacao.symbolDAO = symbolDAO;
    }

    public static ContaTokenDAO getContaTokenDAO() {
        return contaTokenDAO;
    }

    public static void setContaTokenDAO(ContaTokenDAO contaTokenDAO) {
        Operacao.contaTokenDAO = contaTokenDAO;
    }

    public static ObservableList<Symbol> getSymbolObservableList() {
        return symbolObservableList;
    }

    public static Integer getVol10() {
        return VOL_10;
    }

    public static Integer getVol25() {
        return VOL_25;
    }

    public static Integer getVol50() {
        return VOL_50;
    }

    public static Integer getVol75() {
        return VOL_75;
    }

    public static Integer getVol100() {
        return VOL_100;
    }

    public static String[] getVolName() {
        return VOL_NAME;
    }

    public static WSClient getWsClient() {
        return wsClient.get();
    }

    public static ObjectProperty<WSClient> wsClientProperty() {
        return wsClient;
    }

    public static void setWsClient(WSClient wsClient) {
        Operacao.wsClient.set(wsClient);
    }

    public static boolean isWsConectado() {
        return wsConectado.get();
    }

    public static BooleanProperty wsConectadoProperty() {
        return wsConectado;
    }

    public static void setWsConectado(boolean wsConectado) {
        Operacao.wsConectado.set(wsConectado);
    }

    public static int getQtdTicksGrafico() {
        return qtdTicksGrafico.get();
    }

    public static IntegerProperty qtdTicksGraficoProperty() {
        return qtdTicksGrafico;
    }

    public static void setQtdTicksGrafico(int qtdTicksGrafico) {
        Operacao.qtdTicksGrafico.set(qtdTicksGrafico);
    }

    public static Authorize getAuthorize() {
        return authorize.get();
    }

    public static ObjectProperty<Authorize> authorizeProperty() {
        return authorize;
    }

    public static void setAuthorize(Authorize authorize) {
        Operacao.authorize.set(authorize);
    }

    public static ContaToken getContaToken() {
        return contaToken.get();
    }

    public static ObjectProperty<ContaToken> contaTokenProperty() {
        return contaToken;
    }

    public static void setContaToken(ContaToken contaToken) {
        Operacao.contaToken.set(contaToken);
    }

    public static int getQtdTicksAnalisar() {
        return qtdTicksAnalisar.get();
    }

    public static IntegerProperty qtdTicksAnalisarProperty() {
        return qtdTicksAnalisar;
    }

    public static void setQtdTicksAnalisar(int qtdTicksAnalisar) {
        Operacao.qtdTicksAnalisar.set(qtdTicksAnalisar);
    }

    public static IntegerProperty[] getGrafDigitoMaiorQtd() {
        return grafDigitoMaiorQtd;
    }

    public static void setGrafDigitoMaiorQtd(IntegerProperty[] grafDigitoMaiorQtd) {
        Operacao.grafDigitoMaiorQtd = grafDigitoMaiorQtd;
    }

    public static IntegerProperty[] getGrafDigitoMenorQtd() {
        return grafDigitoMenorQtd;
    }

    public static void setGrafDigitoMenorQtd(IntegerProperty[] grafDigitoMenorQtd) {
        Operacao.grafDigitoMenorQtd = grafDigitoMenorQtd;
    }

    public Timeline getRoboRelogio() {
        return roboRelogio;
    }

    public void setRoboRelogio(Timeline roboRelogio) {
        this.roboRelogio = roboRelogio;
    }

    public static ROBOS getRoboSelecionado() {
        return roboSelecionado.get();
    }

    public static ObjectProperty<ROBOS> roboSelecionadoProperty() {
        return roboSelecionado;
    }

    public static void setRoboSelecionado(ROBOS roboSelecionado) {
        Operacao.roboSelecionado.set(roboSelecionado);
    }

    public static Estrategia getRoboEstrategia() {
        return roboEstrategia.get();
    }

    public static ObjectProperty<Estrategia> roboEstrategiaProperty() {
        return roboEstrategia;
    }

    public static void setRoboEstrategia(Estrategia roboEstrategia) {
        Operacao.roboEstrategia.set(roboEstrategia);
    }

    public long getRoboHoraInicial() {
        return roboHoraInicial.get();
    }

    public LongProperty roboHoraInicialProperty() {
        return roboHoraInicial;
    }

    public void setRoboHoraInicial(long roboHoraInicial) {
        this.roboHoraInicial.set(roboHoraInicial);
    }

    public long getRoboCronometro() {
        return roboCronometro.get();
    }

    public LongProperty roboCronometroProperty() {
        return roboCronometro;
    }

    public void setRoboCronometro(long roboCronometro) {
        this.roboCronometro.set(roboCronometro);
    }

    public boolean isRoboCronometroAtivado() {
        return roboCronometroAtivado.get();
    }

    public BooleanProperty roboCronometroAtivadoProperty() {
        return roboCronometroAtivado;
    }

    public void setRoboCronometroAtivado(boolean roboCronometroAtivado) {
        this.roboCronometroAtivado.set(roboCronometroAtivado);
    }

    public static ObjectProperty<Error>[] getError() {
        return error;
    }

    public static void setError(ObjectProperty<Error>[] error) {
        Operacao.error = error;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksGraficoObservableList() {
        return historicoDeTicksGraficoObservableList;
    }

    public static void setHistoricoDeTicksGraficoObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksGraficoObservableList) {
        Operacao.historicoDeTicksGraficoObservableList = historicoDeTicksGraficoObservableList;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksAnaliseObservableList() {
        return historicoDeTicksAnaliseObservableList;
    }

    public static void setHistoricoDeTicksAnaliseObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList) {
        Operacao.historicoDeTicksAnaliseObservableList = historicoDeTicksAnaliseObservableList;
    }

    public static ObservableList<Transaction>[] getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction>[] transactionObservableList) {
        Operacao.transactionObservableList = transactionObservableList;
    }

    public static ObjectProperty<Tick>[] getUltimoTick() {
        return ultimoTick;
    }

    public static void setUltimoTick(ObjectProperty<Tick>[] ultimoTick) {
        Operacao.ultimoTick = ultimoTick;
    }

    public static IntegerProperty[] getUltimoDigito() {
        return ultimoDigito;
    }

    public static void setUltimoDigito(IntegerProperty[] ultimoDigito) {
        Operacao.ultimoDigito = ultimoDigito;
    }

    public static BooleanProperty[] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[] tickSubindo) {
        Operacao.tickSubindo = tickSubindo;
    }

    public static StringProperty[] getInfDet01() {
        return infDet01;
    }

    public static void setInfDet01(StringProperty[] infDet01) {
        Operacao.infDet01 = infDet01;
    }

    public static StringProperty[] getInfVlr01() {
        return infVlr01;
    }

    public static void setInfVlr01(StringProperty[] infVlr01) {
        Operacao.infVlr01 = infVlr01;
    }

    public static StringProperty[] getInfPorc01() {
        return infPorc01;
    }

    public static void setInfPorc01(StringProperty[] infPorc01) {
        Operacao.infPorc01 = infPorc01;
    }

    public static StringProperty[] getInfDet02() {
        return infDet02;
    }

    public static void setInfDet02(StringProperty[] infDet02) {
        Operacao.infDet02 = infDet02;
    }

    public static StringProperty[] getInfVlr02() {
        return infVlr02;
    }

    public static void setInfVlr02(StringProperty[] infVlr02) {
        Operacao.infVlr02 = infVlr02;
    }

    public static StringProperty[] getInfPorc02() {
        return infPorc02;
    }

    public static void setInfPorc02(StringProperty[] infPorc02) {
        Operacao.infPorc02 = infPorc02;
    }

    public static String getContratoRobo() {
        return contratoRobo.get();
    }

    public static StringProperty contratoRoboProperty() {
        return contratoRobo;
    }

    public static void setContratoRobo(String contratoRobo) {
        Operacao.contratoRobo.set(contratoRobo);
    }

    public static boolean isTransacoesAutorizadas() {
        return transacoesAutorizadas.get();
    }

    public static BooleanProperty transacoesAutorizadasProperty() {
        return transacoesAutorizadas;
    }

    public static void setTransacoesAutorizadas(boolean transacoesAutorizadas) {
        Operacao.transacoesAutorizadas.set(transacoesAutorizadas);
    }

    public static boolean isParamEstrategiaCarregados() {
        return paramEstrategiaCarregados.get();
    }

    public static BooleanProperty paramEstrategiaCarregadosProperty() {
        return paramEstrategiaCarregados;
    }

    public static void setParamEstrategiaCarregados(boolean paramEstrategiaCarregados) {
        Operacao.paramEstrategiaCarregados.set(paramEstrategiaCarregados);
    }

    public static BigDecimal getSaldoInicialConta() {
        return saldoInicialConta.get();
    }

    public static ObjectProperty<BigDecimal> saldoInicialContaProperty() {
        return saldoInicialConta;
    }

    public static void setSaldoInicialConta(BigDecimal saldoInicialConta) {
        Operacao.saldoInicialConta.set(saldoInicialConta);
    }

    public static ObjectProperty<BigDecimal>[] getStakePadrao() {
        return stakePadrao;
    }

    public static void setStakePadrao(ObjectProperty<BigDecimal>[] stakePadrao) {
        Operacao.stakePadrao = stakePadrao;
    }

    public static ObjectProperty<BigDecimal>[] getFatorMartingale() {
        return fatorMartingale;
    }

    public static void setFatorMartingale(ObjectProperty<BigDecimal>[] fatorMartingale) {
        Operacao.fatorMartingale = fatorMartingale;
    }

    public static int getQtdStopLoss() {
        return qtdStopLoss.get();
    }

    public static IntegerProperty qtdStopLossProperty() {
        return qtdStopLoss;
    }

    public static void setQtdStopLoss(int qtdStopLoss) {
        Operacao.qtdStopLoss.set(qtdStopLoss);
    }

    public static BigDecimal getVlrStopLoss() {
        return vlrStopLoss.get();
    }

    public static ObjectProperty<BigDecimal> vlrStopLossProperty() {
        return vlrStopLoss;
    }

    public static void setVlrStopLoss(BigDecimal vlrStopLoss) {
        Operacao.vlrStopLoss.set(vlrStopLoss);
    }

    public static BigDecimal getVlrStopGain() {
        return vlrStopGain.get();
    }

    public static ObjectProperty<BigDecimal> vlrStopGainProperty() {
        return vlrStopGain;
    }

    public static void setVlrStopGain(BigDecimal vlrStopGain) {
        Operacao.vlrStopGain.set(vlrStopGain);
    }

    public static int getTempoDeContrato() {
        return tempoDeContrato.get();
    }

    public static IntegerProperty tempoDeContratoProperty() {
        return tempoDeContrato;
    }

    public static void setTempoDeContrato(int tempoDeContrato) {
        Operacao.tempoDeContrato.set(tempoDeContrato);
    }

    public static DURATION_UNIT getDurationUnit() {
        return durationUnit.get();
    }

    public static ObjectProperty<DURATION_UNIT> durationUnitProperty() {
        return durationUnit;
    }

    public static void setDurationUnit(DURATION_UNIT durationUnit) {
        Operacao.durationUnit.set(durationUnit);
    }

    public static boolean isGeralvolatilidadeCompraAutorizada() {
        return geralvolatilidadeCompraAutorizada.get();
    }

    public static BooleanProperty geralvolatilidadeCompraAutorizadaProperty() {
        return geralvolatilidadeCompraAutorizada;
    }

    public static void setGeralvolatilidadeCompraAutorizada(boolean geralvolatilidadeCompraAutorizada) {
        Operacao.geralvolatilidadeCompraAutorizada.set(geralvolatilidadeCompraAutorizada);
    }

    public static boolean isGeralvolatilidadeNegociando() {
        return geralvolatilidadeNegociando.get();
    }

    public static BooleanProperty geralvolatilidadeNegociandoProperty() {
        return geralvolatilidadeNegociando;
    }

    public static void setGeralvolatilidadeNegociando(boolean geralvolatilidadeNegociando) {
        Operacao.geralvolatilidadeNegociando.set(geralvolatilidadeNegociando);
    }

    public static BooleanProperty[] getVolatilidadeAtiva() {
        return volatilidadeAtiva;
    }

    public static void setVolatilidadeAtiva(BooleanProperty[] volatilidadeAtiva) {
        Operacao.volatilidadeAtiva = volatilidadeAtiva;
    }

    public static BooleanProperty[] getVolatilidadeCompraAutorizada() {
        return volatilidadeCompraAutorizada;
    }

    public static void setVolatilidadeCompraAutorizada(BooleanProperty[] volatilidadeCompraAutorizada) {
        Operacao.volatilidadeCompraAutorizada = volatilidadeCompraAutorizada;
    }

    public static BooleanProperty[] getVolatilidadeNegociando() {
        return volatilidadeNegociando;
    }

    public static void setVolatilidadeNegociando(BooleanProperty[] volatilidadeNegociando) {
        Operacao.volatilidadeNegociando = volatilidadeNegociando;
    }

    public static BooleanProperty[] getRenovarTodosContratos() {
        return renovarTodosContratos;
    }

    public static void setRenovarTodosContratos(BooleanProperty[] renovarTodosContratos) {
        Operacao.renovarTodosContratos = renovarTodosContratos;
    }

    public static ObjectProperty<BigDecimal>[] getStakeContrato() {
        return stakeContrato;
    }

    public static void setStakeContrato(ObjectProperty<BigDecimal>[] stakeContrato) {
        Operacao.stakeContrato = stakeContrato;
    }

    public static ObjectProperty<PriceProposal>[] getLastPriceProposal() {
        return lastPriceProposal;
    }

    public static void setLastPriceProposal(ObjectProperty<PriceProposal>[] lastPriceProposal) {
        Operacao.lastPriceProposal = lastPriceProposal;
    }

    public static IntegerProperty[] getQtdDerrotas() {
        return qtdDerrotas;
    }

    public static void setQtdDerrotas(IntegerProperty[] qtdDerrotas) {
        Operacao.qtdDerrotas = qtdDerrotas;
    }

    public static IntegerProperty[] getQtdVitorias() {
        return qtdVitorias;
    }

    public static void setQtdVitorias(IntegerProperty[] qtdVitorias) {
        Operacao.qtdVitorias = qtdVitorias;
    }

    public static EventHandler<ActionEvent>[] getActionBtnContrato() {
        return actionBtnContrato;
    }

    public static void setActionBtnContrato(EventHandler<ActionEvent>[] actionBtnContrato) {
        Operacao.actionBtnContrato = actionBtnContrato;
    }

    public XYChart.Series<String, Number>[] getGrafBarVolatilidade_R() {
        return grafBarVolatilidade_R;
    }

    public void setGrafBarVolatilidade_R(XYChart.Series<String, Number>[] grafBarVolatilidade_R) {
        this.grafBarVolatilidade_R = grafBarVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGrafBarListDataDigitos_R() {
        return grafBarListDataDigitos_R;
    }

    public void setGrafBarListDataDigitos_R(ObservableList<Data<String, Number>>[] grafBarListDataDigitos_R) {
        this.grafBarListDataDigitos_R = grafBarListDataDigitos_R;
    }

    public static ObservableList<LongProperty>[] getGrafBarListValorDigito_R() {
        return grafBarListValorDigito_R;
    }

    public static void setGrafBarListValorDigito_R(ObservableList<LongProperty>[] grafBarListValorDigito_R) {
        Operacao.grafBarListValorDigito_R = grafBarListValorDigito_R;
    }

    public Text[][] getGrafBarTxtDigito_R() {
        return grafBarTxtDigito_R;
    }

    public void setGrafBarTxtDigito_R(Text[][] grafBarTxtDigito_R) {
        this.grafBarTxtDigito_R = grafBarTxtDigito_R;
    }

    public XYChart.Series<String, Number>[] getGrafLineVolatilidade_R() {
        return grafLineVolatilidade_R;
    }

    public void setGrafLineVolatilidade_R(XYChart.Series<String, Number>[] grafLineVolatilidade_R) {
        this.grafLineVolatilidade_R = grafLineVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGrafLineListDataDigitos_R() {
        return grafLineListDataDigitos_R;
    }

    public void setGrafLineListDataDigitos_R(ObservableList<Data<String, Number>>[] grafLineListDataDigitos_R) {
        this.grafLineListDataDigitos_R = grafLineListDataDigitos_R;
    }

    public static ObservableList<HistoricoDeTicks>[] getGrafLineListValorDigito_R() {
        return grafLineListValorDigito_R;
    }

    public static void setGrafLineListValorDigito_R(ObservableList<HistoricoDeTicks>[] grafLineListValorDigito_R) {
        Operacao.grafLineListValorDigito_R = grafLineListValorDigito_R;
    }

    public XYChart.Series<String, Number>[] getGrafMACDVolatilidade_R() {
        return grafMACDVolatilidade_R;
    }

    public void setGrafMACDVolatilidade_R(XYChart.Series<String, Number>[] grafMACDVolatilidade_R) {
        this.grafMACDVolatilidade_R = grafMACDVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGrafMACDListDataDigitos_R() {
        return grafMACDListDataDigitos_R;
    }

    public void setGrafMACDListDataDigitos_R(ObservableList<Data<String, Number>>[] grafMACDListDataDigitos_R) {
        this.grafMACDListDataDigitos_R = grafMACDListDataDigitos_R;
    }

    public static ObservableList<HistoricoDeTicks>[] getGrafMACDListValorDigito_R() {
        return grafMACDListValorDigito_R;
    }

    public static void setGrafMACDListValorDigito_R(ObservableList<HistoricoDeTicks>[] grafMACDListValorDigito_R) {
        Operacao.grafMACDListValorDigito_R = grafMACDListValorDigito_R;
    }

    public AnchorPane getPainelViewBinary() {
        return painelViewBinary;
    }

    public void setPainelViewBinary(AnchorPane painelViewBinary) {
        this.painelViewBinary = painelViewBinary;
    }

    public TitledPane getTpn_DetalhesConta() {
        return tpn_DetalhesConta;
    }

    public void setTpn_DetalhesConta(TitledPane tpn_DetalhesConta) {
        this.tpn_DetalhesConta = tpn_DetalhesConta;
    }

    public ComboBox<ContaToken> getCboConta() {
        return cboConta;
    }

    public void setCboConta(ComboBox<ContaToken> cboConta) {
        this.cboConta = cboConta;
    }

    public Label getLblLegendaNExecucoes() {
        return lblLegendaNExecucoes;
    }

    public void setLblLegendaNExecucoes(Label lblLegendaNExecucoes) {
        this.lblLegendaNExecucoes = lblLegendaNExecucoes;
    }

    public Label getLblTotalExecucoes() {
        return lblTotalExecucoes;
    }

    public void setLblTotalExecucoes(Label lblTotalExecucoes) {
        this.lblTotalExecucoes = lblTotalExecucoes;
    }

    public Label getLblTotalVitorias() {
        return lblTotalVitorias;
    }

    public void setLblTotalVitorias(Label lblTotalVitorias) {
        this.lblTotalVitorias = lblTotalVitorias;
    }

    public Label getLblTotalDerrotas() {
        return lblTotalDerrotas;
    }

    public void setLblTotalDerrotas(Label lblTotalDerrotas) {
        this.lblTotalDerrotas = lblTotalDerrotas;
    }

    public Label getLblTotalLucro() {
        return lblTotalLucro;
    }

    public void setLblTotalLucro(Label lblTotalLucro) {
        this.lblTotalLucro = lblTotalLucro;
    }

    public Label getLblTotalLucroPorc() {
        return lblTotalLucroPorc;
    }

    public void setLblTotalLucroPorc(Label lblTotalLucroPorc) {
        this.lblTotalLucroPorc = lblTotalLucroPorc;
    }

    public Label getLblProprietarioConta() {
        return lblProprietarioConta;
    }

    public void setLblProprietarioConta(Label lblProprietarioConta) {
        this.lblProprietarioConta = lblProprietarioConta;
    }

    public Label getLblIdConta() {
        return lblIdConta;
    }

    public void setLblIdConta(Label lblIdConta) {
        this.lblIdConta = lblIdConta;
    }

    public Label getLblSaldoConta() {
        return lblSaldoConta;
    }

    public void setLblSaldoConta(Label lblSaldoConta) {
        this.lblSaldoConta = lblSaldoConta;
    }

    public Label getLblMoedaSaldo() {
        return lblMoedaSaldo;
    }

    public void setLblMoedaSaldo(Label lblMoedaSaldo) {
        this.lblMoedaSaldo = lblMoedaSaldo;
    }

    public Label getLblSaldoInicial() {
        return lblSaldoInicial;
    }

    public void setLblSaldoInicial(Label lblSaldoInicial) {
        this.lblSaldoInicial = lblSaldoInicial;
    }

    public Label getLblTotalInvestido() {
        return lblTotalInvestido;
    }

    public void setLblTotalInvestido(Label lblTotalInvestido) {
        this.lblTotalInvestido = lblTotalInvestido;
    }

    public Label getLblTotalPremiacao() {
        return lblTotalPremiacao;
    }

    public void setLblTotalPremiacao(Label lblTotalPremiacao) {
        this.lblTotalPremiacao = lblTotalPremiacao;
    }

    public Label getLblSaldoFinal() {
        return lblSaldoFinal;
    }

    public void setLblSaldoFinal(Label lblSaldoFinal) {
        this.lblSaldoFinal = lblSaldoFinal;
    }

    public TitledPane getTpn_negociacao() {
        return tpn_negociacao;
    }

    public void setTpn_negociacao(TitledPane tpn_negociacao) {
        this.tpn_negociacao = tpn_negociacao;
    }

    public ComboBox<ROBOS> getCboRobos() {
        return cboRobos;
    }

    public void setCboRobos(ComboBox<ROBOS> cboRobos) {
        this.cboRobos = cboRobos;
    }

    public Button getBtnContratos() {
        return btnContratos;
    }

    public void setBtnContratos(Button btnContratos) {
        this.btnContratos = btnContratos;
    }

    public Button getBtnIniciar() {
        return btnIniciar;
    }

    public void setBtnIniciar(Button btnIniciar) {
        this.btnIniciar = btnIniciar;
    }

    public Button getBtnPausar() {
        return btnPausar;
    }

    public void setBtnPausar(Button btnPausar) {
        this.btnPausar = btnPausar;
    }

    public Button getBtnStop() {
        return btnStop;
    }

    public void setBtnStop(Button btnStop) {
        this.btnStop = btnStop;
    }

    public ComboBox<SimNao> getCboVelocidadeTicksGrafico() {
        return cboVelocidadeTicksGrafico;
    }

    public void setCboVelocidadeTicksGrafico(ComboBox<SimNao> cboVelocidadeTicksGrafico) {
        this.cboVelocidadeTicksGrafico = cboVelocidadeTicksGrafico;
    }

    public ComboBox<Integer> getCboQtdTicksGrafico() {
        return cboQtdTicksGrafico;
    }

    public void setCboQtdTicksGrafico(ComboBox<Integer> cboQtdTicksGrafico) {
        this.cboQtdTicksGrafico = cboQtdTicksGrafico;
    }

    public Label getLblRoboHoraInicial() {
        return lblRoboHoraInicial;
    }

    public void setLblRoboHoraInicial(Label lblRoboHoraInicial) {
        this.lblRoboHoraInicial = lblRoboHoraInicial;
    }

    public Label getLblRoboHoraAtual() {
        return lblRoboHoraAtual;
    }

    public void setLblRoboHoraAtual(Label lblRoboHoraAtual) {
        this.lblRoboHoraAtual = lblRoboHoraAtual;
    }

    public Label getLblRoboCronometro() {
        return lblRoboCronometro;
    }

    public void setLblRoboCronometro(Label lblRoboCronometro) {
        this.lblRoboCronometro = lblRoboCronometro;
    }

    public TitledPane getTpn_R10() {
        return tpn_R10;
    }

    public void setTpn_R10(TitledPane tpn_R10) {
        this.tpn_R10 = tpn_R10;
    }

    public BarChart<String, Number> getGrafBar_R10() {
        return grafBar_R10;
    }

    public void setGrafBar_R10(BarChart<String, Number> grafBar_R10) {
        this.grafBar_R10 = grafBar_R10;
    }

    public NumberAxis getyAxisBar_R10() {
        return yAxisBar_R10;
    }

    public void setyAxisBar_R10(NumberAxis yAxisBar_R10) {
        this.yAxisBar_R10 = yAxisBar_R10;
    }

    public BarChart<String, Number> getGrafBar_HZ10() {
        return grafBar_HZ10;
    }

    public void setGrafBar_HZ10(BarChart<String, Number> grafBar_HZ10) {
        this.grafBar_HZ10 = grafBar_HZ10;
    }

    public NumberAxis getyAxisBar_HZ10() {
        return yAxisBar_HZ10;
    }

    public void setyAxisBar_HZ10(NumberAxis yAxisBar_HZ10) {
        this.yAxisBar_HZ10 = yAxisBar_HZ10;
    }

    public LineChart getGrafLine_R10() {
        return grafLine_R10;
    }

    public void setGrafLine_R10(LineChart grafLine_R10) {
        this.grafLine_R10 = grafLine_R10;
    }

    public NumberAxis getyAxisLine_R10() {
        return yAxisLine_R10;
    }

    public void setyAxisLine_R10(NumberAxis yAxisLine_R10) {
        this.yAxisLine_R10 = yAxisLine_R10;
    }

    public Label getLblInf01_R10() {
        return lblInf01_R10;
    }

    public void setLblInf01_R10(Label lblInf01_R10) {
        this.lblInf01_R10 = lblInf01_R10;
    }

    public Label getLblVlrInf01_R10() {
        return lblVlrInf01_R10;
    }

    public void setLblVlrInf01_R10(Label lblVlrInf01_R10) {
        this.lblVlrInf01_R10 = lblVlrInf01_R10;
    }

    public Label getLblPorcInf01_R10() {
        return lblPorcInf01_R10;
    }

    public void setLblPorcInf01_R10(Label lblPorcInf01_R10) {
        this.lblPorcInf01_R10 = lblPorcInf01_R10;
    }

    public Label getLblInf02_R10() {
        return lblInf02_R10;
    }

    public void setLblInf02_R10(Label lblInf02_R10) {
        this.lblInf02_R10 = lblInf02_R10;
    }

    public Label getLblVlrInf02_R10() {
        return lblVlrInf02_R10;
    }

    public void setLblVlrInf02_R10(Label lblVlrInf02_R10) {
        this.lblVlrInf02_R10 = lblVlrInf02_R10;
    }

    public Label getLblPorcInf02_R10() {
        return lblPorcInf02_R10;
    }

    public void setLblPorcInf02_R10(Label lblPorcInf02_R10) {
        this.lblPorcInf02_R10 = lblPorcInf02_R10;
    }

    public Label getLblTickUltimo_R10() {
        return lblTickUltimo_R10;
    }

    public void setLblTickUltimo_R10(Label lblTickUltimo_R10) {
        this.lblTickUltimo_R10 = lblTickUltimo_R10;
    }

    public Label getLblLegendaTickUltimo_R10() {
        return lblLegendaTickUltimo_R10;
    }

    public void setLblLegendaTickUltimo_R10(Label lblLegendaTickUltimo_R10) {
        this.lblLegendaTickUltimo_R10 = lblLegendaTickUltimo_R10;
    }

    public Button getBtnContratos_R10() {
        return btnContratos_R10;
    }

    public void setBtnContratos_R10(Button btnContratos_R10) {
        this.btnContratos_R10 = btnContratos_R10;
    }

    public Button getBtnComprar_R10() {
        return btnComprar_R10;
    }

    public void setBtnComprar_R10(Button btnComprar_R10) {
        this.btnComprar_R10 = btnComprar_R10;
    }

    public Button getBtnPausar_R10() {
        return btnPausar_R10;
    }

    public void setBtnPausar_R10(Button btnPausar_R10) {
        this.btnPausar_R10 = btnPausar_R10;
    }

    public Button getBtnStop_R10() {
        return btnStop_R10;
    }

    public void setBtnStop_R10(Button btnStop_R10) {
        this.btnStop_R10 = btnStop_R10;
    }

    public Label getLblInvestido_R10() {
        return lblInvestido_R10;
    }

    public void setLblInvestido_R10(Label lblInvestido_R10) {
        this.lblInvestido_R10 = lblInvestido_R10;
    }

    public Label getLblInvestidoPorc_R10() {
        return lblInvestidoPorc_R10;
    }

    public void setLblInvestidoPorc_R10(Label lblInvestidoPorc_R10) {
        this.lblInvestidoPorc_R10 = lblInvestidoPorc_R10;
    }

    public Label getLblPremiacao_R10() {
        return lblPremiacao_R10;
    }

    public void setLblPremiacao_R10(Label lblPremiacao_R10) {
        this.lblPremiacao_R10 = lblPremiacao_R10;
    }

    public Label getLblPremiacaoPorc_R10() {
        return lblPremiacaoPorc_R10;
    }

    public void setLblPremiacaoPorc_R10(Label lblPremiacaoPorc_R10) {
        this.lblPremiacaoPorc_R10 = lblPremiacaoPorc_R10;
    }

    public Label getLblLucro_R10() {
        return lblLucro_R10;
    }

    public void setLblLucro_R10(Label lblLucro_R10) {
        this.lblLucro_R10 = lblLucro_R10;
    }

    public Label getLblLucroPorc_R10() {
        return lblLucroPorc_R10;
    }

    public void setLblLucroPorc_R10(Label lblLucroPorc_R10) {
        this.lblLucroPorc_R10 = lblLucroPorc_R10;
    }

    public TableView getTbvTransacoes_R10() {
        return tbvTransacoes_R10;
    }

    public void setTbvTransacoes_R10(TableView tbvTransacoes_R10) {
        this.tbvTransacoes_R10 = tbvTransacoes_R10;
    }

    public CheckBox getChkAtivo_R10() {
        return chkAtivo_R10;
    }

    public void setChkAtivo_R10(CheckBox chkAtivo_R10) {
        this.chkAtivo_R10 = chkAtivo_R10;
    }

    public Label getTpnLblLegendaExecucoes_R10() {
        return tpnLblLegendaExecucoes_R10;
    }

    public void setTpnLblLegendaExecucoes_R10(Label tpnLblLegendaExecucoes_R10) {
        this.tpnLblLegendaExecucoes_R10 = tpnLblLegendaExecucoes_R10;
    }

    public Label getTpnLblExecucoes_R10() {
        return tpnLblExecucoes_R10;
    }

    public void setTpnLblExecucoes_R10(Label tpnLblExecucoes_R10) {
        this.tpnLblExecucoes_R10 = tpnLblExecucoes_R10;
    }

    public Label getTpnLblVitorias_R10() {
        return tpnLblVitorias_R10;
    }

    public void setTpnLblVitorias_R10(Label tpnLblVitorias_R10) {
        this.tpnLblVitorias_R10 = tpnLblVitorias_R10;
    }

    public Label getTpnLblDerrotas_R10() {
        return tpnLblDerrotas_R10;
    }

    public void setTpnLblDerrotas_R10(Label tpnLblDerrotas_R10) {
        this.tpnLblDerrotas_R10 = tpnLblDerrotas_R10;
    }

    public Label getTpnLblLucro_R10() {
        return tpnLblLucro_R10;
    }

    public void setTpnLblLucro_R10(Label tpnLblLucro_R10) {
        this.tpnLblLucro_R10 = tpnLblLucro_R10;
    }

    public TitledPane getTpn_R25() {
        return tpn_R25;
    }

    public void setTpn_R25(TitledPane tpn_R25) {
        this.tpn_R25 = tpn_R25;
    }

    public BarChart<String, Number> getGrafBar_R25() {
        return grafBar_R25;
    }

    public void setGrafBar_R25(BarChart<String, Number> grafBar_R25) {
        this.grafBar_R25 = grafBar_R25;
    }

    public NumberAxis getyAxisBar_R25() {
        return yAxisBar_R25;
    }

    public void setyAxisBar_R25(NumberAxis yAxisBar_R25) {
        this.yAxisBar_R25 = yAxisBar_R25;
    }

    public BarChart<String, Number> getGrafBar_HZ25() {
        return grafBar_HZ25;
    }

    public void setGrafBar_HZ25(BarChart<String, Number> grafBar_HZ25) {
        this.grafBar_HZ25 = grafBar_HZ25;
    }

    public NumberAxis getyAxisBar_HZ25() {
        return yAxisBar_HZ25;
    }

    public void setyAxisBar_HZ25(NumberAxis yAxisBar_HZ25) {
        this.yAxisBar_HZ25 = yAxisBar_HZ25;
    }

    public LineChart getGrafLine_R25() {
        return grafLine_R25;
    }

    public void setGrafLine_R25(LineChart grafLine_R25) {
        this.grafLine_R25 = grafLine_R25;
    }

    public NumberAxis getyAxisLine_R25() {
        return yAxisLine_R25;
    }

    public void setyAxisLine_R25(NumberAxis yAxisLine_R25) {
        this.yAxisLine_R25 = yAxisLine_R25;
    }

    public Label getLblInf01_R25() {
        return lblInf01_R25;
    }

    public void setLblInf01_R25(Label lblInf01_R25) {
        this.lblInf01_R25 = lblInf01_R25;
    }

    public Label getLblVlrInf01_R25() {
        return lblVlrInf01_R25;
    }

    public void setLblVlrInf01_R25(Label lblVlrInf01_R25) {
        this.lblVlrInf01_R25 = lblVlrInf01_R25;
    }

    public Label getLblPorcInf01_R25() {
        return lblPorcInf01_R25;
    }

    public void setLblPorcInf01_R25(Label lblPorcInf01_R25) {
        this.lblPorcInf01_R25 = lblPorcInf01_R25;
    }

    public Label getLblInf02_R25() {
        return lblInf02_R25;
    }

    public void setLblInf02_R25(Label lblInf02_R25) {
        this.lblInf02_R25 = lblInf02_R25;
    }

    public Label getLblVlrInf02_R25() {
        return lblVlrInf02_R25;
    }

    public void setLblVlrInf02_R25(Label lblVlrInf02_R25) {
        this.lblVlrInf02_R25 = lblVlrInf02_R25;
    }

    public Label getLblPorcInf02_R25() {
        return lblPorcInf02_R25;
    }

    public void setLblPorcInf02_R25(Label lblPorcInf02_R25) {
        this.lblPorcInf02_R25 = lblPorcInf02_R25;
    }

    public Label getLblTickUltimo_R25() {
        return lblTickUltimo_R25;
    }

    public void setLblTickUltimo_R25(Label lblTickUltimo_R25) {
        this.lblTickUltimo_R25 = lblTickUltimo_R25;
    }

    public Label getLblLegendaTickUltimo_R25() {
        return lblLegendaTickUltimo_R25;
    }

    public void setLblLegendaTickUltimo_R25(Label lblLegendaTickUltimo_R25) {
        this.lblLegendaTickUltimo_R25 = lblLegendaTickUltimo_R25;
    }

    public Button getBtnContratos_R25() {
        return btnContratos_R25;
    }

    public void setBtnContratos_R25(Button btnContratos_R25) {
        this.btnContratos_R25 = btnContratos_R25;
    }

    public Button getBtnComprar_R25() {
        return btnComprar_R25;
    }

    public void setBtnComprar_R25(Button btnComprar_R25) {
        this.btnComprar_R25 = btnComprar_R25;
    }

    public Button getBtnPausar_R25() {
        return btnPausar_R25;
    }

    public void setBtnPausar_R25(Button btnPausar_R25) {
        this.btnPausar_R25 = btnPausar_R25;
    }

    public Button getBtnStop_R25() {
        return btnStop_R25;
    }

    public void setBtnStop_R25(Button btnStop_R25) {
        this.btnStop_R25 = btnStop_R25;
    }

    public Label getLblInvestido_R25() {
        return lblInvestido_R25;
    }

    public void setLblInvestido_R25(Label lblInvestido_R25) {
        this.lblInvestido_R25 = lblInvestido_R25;
    }

    public Label getLblInvestidoPorc_R25() {
        return lblInvestidoPorc_R25;
    }

    public void setLblInvestidoPorc_R25(Label lblInvestidoPorc_R25) {
        this.lblInvestidoPorc_R25 = lblInvestidoPorc_R25;
    }

    public Label getLblPremiacao_R25() {
        return lblPremiacao_R25;
    }

    public void setLblPremiacao_R25(Label lblPremiacao_R25) {
        this.lblPremiacao_R25 = lblPremiacao_R25;
    }

    public Label getLblPremiacaoPorc_R25() {
        return lblPremiacaoPorc_R25;
    }

    public void setLblPremiacaoPorc_R25(Label lblPremiacaoPorc_R25) {
        this.lblPremiacaoPorc_R25 = lblPremiacaoPorc_R25;
    }

    public Label getLblLucro_R25() {
        return lblLucro_R25;
    }

    public void setLblLucro_R25(Label lblLucro_R25) {
        this.lblLucro_R25 = lblLucro_R25;
    }

    public Label getLblLucroPorc_R25() {
        return lblLucroPorc_R25;
    }

    public void setLblLucroPorc_R25(Label lblLucroPorc_R25) {
        this.lblLucroPorc_R25 = lblLucroPorc_R25;
    }

    public TableView getTbvTransacoes_R25() {
        return tbvTransacoes_R25;
    }

    public void setTbvTransacoes_R25(TableView tbvTransacoes_R25) {
        this.tbvTransacoes_R25 = tbvTransacoes_R25;
    }

    public CheckBox getChkAtivo_R25() {
        return chkAtivo_R25;
    }

    public void setChkAtivo_R25(CheckBox chkAtivo_R25) {
        this.chkAtivo_R25 = chkAtivo_R25;
    }

    public Label getTpnLblLegendaExecucoes_R25() {
        return tpnLblLegendaExecucoes_R25;
    }

    public void setTpnLblLegendaExecucoes_R25(Label tpnLblLegendaExecucoes_R25) {
        this.tpnLblLegendaExecucoes_R25 = tpnLblLegendaExecucoes_R25;
    }

    public Label getTpnLblExecucoes_R25() {
        return tpnLblExecucoes_R25;
    }

    public void setTpnLblExecucoes_R25(Label tpnLblExecucoes_R25) {
        this.tpnLblExecucoes_R25 = tpnLblExecucoes_R25;
    }

    public Label getTpnLblVitorias_R25() {
        return tpnLblVitorias_R25;
    }

    public void setTpnLblVitorias_R25(Label tpnLblVitorias_R25) {
        this.tpnLblVitorias_R25 = tpnLblVitorias_R25;
    }

    public Label getTpnLblDerrotas_R25() {
        return tpnLblDerrotas_R25;
    }

    public void setTpnLblDerrotas_R25(Label tpnLblDerrotas_R25) {
        this.tpnLblDerrotas_R25 = tpnLblDerrotas_R25;
    }

    public Label getTpnLblLucro_R25() {
        return tpnLblLucro_R25;
    }

    public void setTpnLblLucro_R25(Label tpnLblLucro_R25) {
        this.tpnLblLucro_R25 = tpnLblLucro_R25;
    }

    public TitledPane getTpn_R50() {
        return tpn_R50;
    }

    public void setTpn_R50(TitledPane tpn_R50) {
        this.tpn_R50 = tpn_R50;
    }

    public BarChart<String, Number> getGrafBar_R50() {
        return grafBar_R50;
    }

    public void setGrafBar_R50(BarChart<String, Number> grafBar_R50) {
        this.grafBar_R50 = grafBar_R50;
    }

    public NumberAxis getyAxisBar_R50() {
        return yAxisBar_R50;
    }

    public void setyAxisBar_R50(NumberAxis yAxisBar_R50) {
        this.yAxisBar_R50 = yAxisBar_R50;
    }

    public BarChart<String, Number> getGrafBar_HZ50() {
        return grafBar_HZ50;
    }

    public void setGrafBar_HZ50(BarChart<String, Number> grafBar_HZ50) {
        this.grafBar_HZ50 = grafBar_HZ50;
    }

    public NumberAxis getyAxisBar_HZ50() {
        return yAxisBar_HZ50;
    }

    public void setyAxisBar_HZ50(NumberAxis yAxisBar_HZ50) {
        this.yAxisBar_HZ50 = yAxisBar_HZ50;
    }

    public LineChart getGrafLine_R50() {
        return grafLine_R50;
    }

    public void setGrafLine_R50(LineChart grafLine_R50) {
        this.grafLine_R50 = grafLine_R50;
    }

    public NumberAxis getyAxisLine_R50() {
        return yAxisLine_R50;
    }

    public void setyAxisLine_R50(NumberAxis yAxisLine_R50) {
        this.yAxisLine_R50 = yAxisLine_R50;
    }

    public Label getLblInf01_R50() {
        return lblInf01_R50;
    }

    public void setLblInf01_R50(Label lblInf01_R50) {
        this.lblInf01_R50 = lblInf01_R50;
    }

    public Label getLblVlrInf01_R50() {
        return lblVlrInf01_R50;
    }

    public void setLblVlrInf01_R50(Label lblVlrInf01_R50) {
        this.lblVlrInf01_R50 = lblVlrInf01_R50;
    }

    public Label getLblPorcInf01_R50() {
        return lblPorcInf01_R50;
    }

    public void setLblPorcInf01_R50(Label lblPorcInf01_R50) {
        this.lblPorcInf01_R50 = lblPorcInf01_R50;
    }

    public Label getLblInf02_R50() {
        return lblInf02_R50;
    }

    public void setLblInf02_R50(Label lblInf02_R50) {
        this.lblInf02_R50 = lblInf02_R50;
    }

    public Label getLblVlrInf02_R50() {
        return lblVlrInf02_R50;
    }

    public void setLblVlrInf02_R50(Label lblVlrInf02_R50) {
        this.lblVlrInf02_R50 = lblVlrInf02_R50;
    }

    public Label getLblPorcInf02_R50() {
        return lblPorcInf02_R50;
    }

    public void setLblPorcInf02_R50(Label lblPorcInf02_R50) {
        this.lblPorcInf02_R50 = lblPorcInf02_R50;
    }

    public Label getLblTickUltimo_R50() {
        return lblTickUltimo_R50;
    }

    public void setLblTickUltimo_R50(Label lblTickUltimo_R50) {
        this.lblTickUltimo_R50 = lblTickUltimo_R50;
    }

    public Label getLblLegendaTickUltimo_R50() {
        return lblLegendaTickUltimo_R50;
    }

    public void setLblLegendaTickUltimo_R50(Label lblLegendaTickUltimo_R50) {
        this.lblLegendaTickUltimo_R50 = lblLegendaTickUltimo_R50;
    }

    public Button getBtnContratos_R50() {
        return btnContratos_R50;
    }

    public void setBtnContratos_R50(Button btnContratos_R50) {
        this.btnContratos_R50 = btnContratos_R50;
    }

    public Button getBtnComprar_R50() {
        return btnComprar_R50;
    }

    public void setBtnComprar_R50(Button btnComprar_R50) {
        this.btnComprar_R50 = btnComprar_R50;
    }

    public Button getBtnPausar_R50() {
        return btnPausar_R50;
    }

    public void setBtnPausar_R50(Button btnPausar_R50) {
        this.btnPausar_R50 = btnPausar_R50;
    }

    public Button getBtnStop_R50() {
        return btnStop_R50;
    }

    public void setBtnStop_R50(Button btnStop_R50) {
        this.btnStop_R50 = btnStop_R50;
    }

    public Label getLblInvestido_R50() {
        return lblInvestido_R50;
    }

    public void setLblInvestido_R50(Label lblInvestido_R50) {
        this.lblInvestido_R50 = lblInvestido_R50;
    }

    public Label getLblInvestidoPorc_R50() {
        return lblInvestidoPorc_R50;
    }

    public void setLblInvestidoPorc_R50(Label lblInvestidoPorc_R50) {
        this.lblInvestidoPorc_R50 = lblInvestidoPorc_R50;
    }

    public Label getLblPremiacao_R50() {
        return lblPremiacao_R50;
    }

    public void setLblPremiacao_R50(Label lblPremiacao_R50) {
        this.lblPremiacao_R50 = lblPremiacao_R50;
    }

    public Label getLblPremiacaoPorc_R50() {
        return lblPremiacaoPorc_R50;
    }

    public void setLblPremiacaoPorc_R50(Label lblPremiacaoPorc_R50) {
        this.lblPremiacaoPorc_R50 = lblPremiacaoPorc_R50;
    }

    public Label getLblLucro_R50() {
        return lblLucro_R50;
    }

    public void setLblLucro_R50(Label lblLucro_R50) {
        this.lblLucro_R50 = lblLucro_R50;
    }

    public Label getLblLucroPorc_R50() {
        return lblLucroPorc_R50;
    }

    public void setLblLucroPorc_R50(Label lblLucroPorc_R50) {
        this.lblLucroPorc_R50 = lblLucroPorc_R50;
    }

    public TableView getTbvTransacoes_R50() {
        return tbvTransacoes_R50;
    }

    public void setTbvTransacoes_R50(TableView tbvTransacoes_R50) {
        this.tbvTransacoes_R50 = tbvTransacoes_R50;
    }

    public CheckBox getChkAtivo_R50() {
        return chkAtivo_R50;
    }

    public void setChkAtivo_R50(CheckBox chkAtivo_R50) {
        this.chkAtivo_R50 = chkAtivo_R50;
    }

    public Label getTpnLblLegendaExecucoes_R50() {
        return tpnLblLegendaExecucoes_R50;
    }

    public void setTpnLblLegendaExecucoes_R50(Label tpnLblLegendaExecucoes_R50) {
        this.tpnLblLegendaExecucoes_R50 = tpnLblLegendaExecucoes_R50;
    }

    public Label getTpnLblExecucoes_R50() {
        return tpnLblExecucoes_R50;
    }

    public void setTpnLblExecucoes_R50(Label tpnLblExecucoes_R50) {
        this.tpnLblExecucoes_R50 = tpnLblExecucoes_R50;
    }

    public Label getTpnLblVitorias_R50() {
        return tpnLblVitorias_R50;
    }

    public void setTpnLblVitorias_R50(Label tpnLblVitorias_R50) {
        this.tpnLblVitorias_R50 = tpnLblVitorias_R50;
    }

    public Label getTpnLblDerrotas_R50() {
        return tpnLblDerrotas_R50;
    }

    public void setTpnLblDerrotas_R50(Label tpnLblDerrotas_R50) {
        this.tpnLblDerrotas_R50 = tpnLblDerrotas_R50;
    }

    public Label getTpnLblLucro_R50() {
        return tpnLblLucro_R50;
    }

    public void setTpnLblLucro_R50(Label tpnLblLucro_R50) {
        this.tpnLblLucro_R50 = tpnLblLucro_R50;
    }

    public TitledPane getTpn_R75() {
        return tpn_R75;
    }

    public void setTpn_R75(TitledPane tpn_R75) {
        this.tpn_R75 = tpn_R75;
    }

    public BarChart<String, Number> getGrafBar_R75() {
        return grafBar_R75;
    }

    public void setGrafBar_R75(BarChart<String, Number> grafBar_R75) {
        this.grafBar_R75 = grafBar_R75;
    }

    public NumberAxis getyAxisBar_R75() {
        return yAxisBar_R75;
    }

    public void setyAxisBar_R75(NumberAxis yAxisBar_R75) {
        this.yAxisBar_R75 = yAxisBar_R75;
    }

    public BarChart<String, Number> getGrafBar_HZ75() {
        return grafBar_HZ75;
    }

    public void setGrafBar_HZ75(BarChart<String, Number> grafBar_HZ75) {
        this.grafBar_HZ75 = grafBar_HZ75;
    }

    public NumberAxis getyAxisBar_HZ75() {
        return yAxisBar_HZ75;
    }

    public void setyAxisBar_HZ75(NumberAxis yAxisBar_HZ75) {
        this.yAxisBar_HZ75 = yAxisBar_HZ75;
    }

    public LineChart getGrafLine_R75() {
        return grafLine_R75;
    }

    public void setGrafLine_R75(LineChart grafLine_R75) {
        this.grafLine_R75 = grafLine_R75;
    }

    public NumberAxis getyAxisLine_R75() {
        return yAxisLine_R75;
    }

    public void setyAxisLine_R75(NumberAxis yAxisLine_R75) {
        this.yAxisLine_R75 = yAxisLine_R75;
    }

    public Label getLblInf01_R75() {
        return lblInf01_R75;
    }

    public void setLblInf01_R75(Label lblInf01_R75) {
        this.lblInf01_R75 = lblInf01_R75;
    }

    public Label getLblVlrInf01_R75() {
        return lblVlrInf01_R75;
    }

    public void setLblVlrInf01_R75(Label lblVlrInf01_R75) {
        this.lblVlrInf01_R75 = lblVlrInf01_R75;
    }

    public Label getLblPorcInf01_R75() {
        return lblPorcInf01_R75;
    }

    public void setLblPorcInf01_R75(Label lblPorcInf01_R75) {
        this.lblPorcInf01_R75 = lblPorcInf01_R75;
    }

    public Label getLblInf02_R75() {
        return lblInf02_R75;
    }

    public void setLblInf02_R75(Label lblInf02_R75) {
        this.lblInf02_R75 = lblInf02_R75;
    }

    public Label getLblVlrInf02_R75() {
        return lblVlrInf02_R75;
    }

    public void setLblVlrInf02_R75(Label lblVlrInf02_R75) {
        this.lblVlrInf02_R75 = lblVlrInf02_R75;
    }

    public Label getLblPorcInf02_R75() {
        return lblPorcInf02_R75;
    }

    public void setLblPorcInf02_R75(Label lblPorcInf02_R75) {
        this.lblPorcInf02_R75 = lblPorcInf02_R75;
    }

    public Label getLblTickUltimo_R75() {
        return lblTickUltimo_R75;
    }

    public void setLblTickUltimo_R75(Label lblTickUltimo_R75) {
        this.lblTickUltimo_R75 = lblTickUltimo_R75;
    }

    public Label getLblLegendaTickUltimo_R75() {
        return lblLegendaTickUltimo_R75;
    }

    public void setLblLegendaTickUltimo_R75(Label lblLegendaTickUltimo_R75) {
        this.lblLegendaTickUltimo_R75 = lblLegendaTickUltimo_R75;
    }

    public Button getBtnContratos_R75() {
        return btnContratos_R75;
    }

    public void setBtnContratos_R75(Button btnContratos_R75) {
        this.btnContratos_R75 = btnContratos_R75;
    }

    public Button getBtnComprar_R75() {
        return btnComprar_R75;
    }

    public void setBtnComprar_R75(Button btnComprar_R75) {
        this.btnComprar_R75 = btnComprar_R75;
    }

    public Button getBtnPausar_R75() {
        return btnPausar_R75;
    }

    public void setBtnPausar_R75(Button btnPausar_R75) {
        this.btnPausar_R75 = btnPausar_R75;
    }

    public Button getBtnStop_R75() {
        return btnStop_R75;
    }

    public void setBtnStop_R75(Button btnStop_R75) {
        this.btnStop_R75 = btnStop_R75;
    }

    public Label getLblInvestido_R75() {
        return lblInvestido_R75;
    }

    public void setLblInvestido_R75(Label lblInvestido_R75) {
        this.lblInvestido_R75 = lblInvestido_R75;
    }

    public Label getLblInvestidoPorc_R75() {
        return lblInvestidoPorc_R75;
    }

    public void setLblInvestidoPorc_R75(Label lblInvestidoPorc_R75) {
        this.lblInvestidoPorc_R75 = lblInvestidoPorc_R75;
    }

    public Label getLblPremiacao_R75() {
        return lblPremiacao_R75;
    }

    public void setLblPremiacao_R75(Label lblPremiacao_R75) {
        this.lblPremiacao_R75 = lblPremiacao_R75;
    }

    public Label getLblPremiacaoPorc_R75() {
        return lblPremiacaoPorc_R75;
    }

    public void setLblPremiacaoPorc_R75(Label lblPremiacaoPorc_R75) {
        this.lblPremiacaoPorc_R75 = lblPremiacaoPorc_R75;
    }

    public Label getLblLucro_R75() {
        return lblLucro_R75;
    }

    public void setLblLucro_R75(Label lblLucro_R75) {
        this.lblLucro_R75 = lblLucro_R75;
    }

    public Label getLblLucroPorc_R75() {
        return lblLucroPorc_R75;
    }

    public void setLblLucroPorc_R75(Label lblLucroPorc_R75) {
        this.lblLucroPorc_R75 = lblLucroPorc_R75;
    }

    public TableView getTbvTransacoes_R75() {
        return tbvTransacoes_R75;
    }

    public void setTbvTransacoes_R75(TableView tbvTransacoes_R75) {
        this.tbvTransacoes_R75 = tbvTransacoes_R75;
    }

    public CheckBox getChkAtivo_R75() {
        return chkAtivo_R75;
    }

    public void setChkAtivo_R75(CheckBox chkAtivo_R75) {
        this.chkAtivo_R75 = chkAtivo_R75;
    }

    public Label getTpnLblLegendaExecucoes_R75() {
        return tpnLblLegendaExecucoes_R75;
    }

    public void setTpnLblLegendaExecucoes_R75(Label tpnLblLegendaExecucoes_R75) {
        this.tpnLblLegendaExecucoes_R75 = tpnLblLegendaExecucoes_R75;
    }

    public Label getTpnLblExecucoes_R75() {
        return tpnLblExecucoes_R75;
    }

    public void setTpnLblExecucoes_R75(Label tpnLblExecucoes_R75) {
        this.tpnLblExecucoes_R75 = tpnLblExecucoes_R75;
    }

    public Label getTpnLblVitorias_R75() {
        return tpnLblVitorias_R75;
    }

    public void setTpnLblVitorias_R75(Label tpnLblVitorias_R75) {
        this.tpnLblVitorias_R75 = tpnLblVitorias_R75;
    }

    public Label getTpnLblDerrotas_R75() {
        return tpnLblDerrotas_R75;
    }

    public void setTpnLblDerrotas_R75(Label tpnLblDerrotas_R75) {
        this.tpnLblDerrotas_R75 = tpnLblDerrotas_R75;
    }

    public Label getTpnLblLucro_R75() {
        return tpnLblLucro_R75;
    }

    public void setTpnLblLucro_R75(Label tpnLblLucro_R75) {
        this.tpnLblLucro_R75 = tpnLblLucro_R75;
    }

    public TitledPane getTpn_R100() {
        return tpn_R100;
    }

    public void setTpn_R100(TitledPane tpn_R100) {
        this.tpn_R100 = tpn_R100;
    }

    public BarChart<String, Number> getGrafBar_R100() {
        return grafBar_R100;
    }

    public void setGrafBar_R100(BarChart<String, Number> grafBar_R100) {
        this.grafBar_R100 = grafBar_R100;
    }

    public NumberAxis getyAxisBar_R100() {
        return yAxisBar_R100;
    }

    public void setyAxisBar_R100(NumberAxis yAxisBar_R100) {
        this.yAxisBar_R100 = yAxisBar_R100;
    }

    public BarChart<String, Number> getGrafBar_HZ100() {
        return grafBar_HZ100;
    }

    public void setGrafBar_HZ100(BarChart<String, Number> grafBar_HZ100) {
        this.grafBar_HZ100 = grafBar_HZ100;
    }

    public NumberAxis getyAxisBar_HZ100() {
        return yAxisBar_HZ100;
    }

    public void setyAxisBar_HZ100(NumberAxis yAxisBar_HZ100) {
        this.yAxisBar_HZ100 = yAxisBar_HZ100;
    }

    public LineChart getGrafLine_R100() {
        return grafLine_R100;
    }

    public void setGrafLine_R100(LineChart grafLine_R100) {
        this.grafLine_R100 = grafLine_R100;
    }

    public NumberAxis getyAxisLine_R100() {
        return yAxisLine_R100;
    }

    public void setyAxisLine_R100(NumberAxis yAxisLine_R100) {
        this.yAxisLine_R100 = yAxisLine_R100;
    }

    public Label getLblInf01_R100() {
        return lblInf01_R100;
    }

    public void setLblInf01_R100(Label lblInf01_R100) {
        this.lblInf01_R100 = lblInf01_R100;
    }

    public Label getLblVlrInf01_R100() {
        return lblVlrInf01_R100;
    }

    public void setLblVlrInf01_R100(Label lblVlrInf01_R100) {
        this.lblVlrInf01_R100 = lblVlrInf01_R100;
    }

    public Label getLblPorcInf01_R100() {
        return lblPorcInf01_R100;
    }

    public void setLblPorcInf01_R100(Label lblPorcInf01_R100) {
        this.lblPorcInf01_R100 = lblPorcInf01_R100;
    }

    public Label getLblInf02_R100() {
        return lblInf02_R100;
    }

    public void setLblInf02_R100(Label lblInf02_R100) {
        this.lblInf02_R100 = lblInf02_R100;
    }

    public Label getLblVlrInf02_R100() {
        return lblVlrInf02_R100;
    }

    public void setLblVlrInf02_R100(Label lblVlrInf02_R100) {
        this.lblVlrInf02_R100 = lblVlrInf02_R100;
    }

    public Label getLblPorcInf02_R100() {
        return lblPorcInf02_R100;
    }

    public void setLblPorcInf02_R100(Label lblPorcInf02_R100) {
        this.lblPorcInf02_R100 = lblPorcInf02_R100;
    }

    public Label getLblTickUltimo_R100() {
        return lblTickUltimo_R100;
    }

    public void setLblTickUltimo_R100(Label lblTickUltimo_R100) {
        this.lblTickUltimo_R100 = lblTickUltimo_R100;
    }

    public Label getLblLegendaTickUltimo_R100() {
        return lblLegendaTickUltimo_R100;
    }

    public void setLblLegendaTickUltimo_R100(Label lblLegendaTickUltimo_R100) {
        this.lblLegendaTickUltimo_R100 = lblLegendaTickUltimo_R100;
    }

    public Button getBtnContratos_R100() {
        return btnContratos_R100;
    }

    public void setBtnContratos_R100(Button btnContratos_R100) {
        this.btnContratos_R100 = btnContratos_R100;
    }

    public Button getBtnComprar_R100() {
        return btnComprar_R100;
    }

    public void setBtnComprar_R100(Button btnComprar_R100) {
        this.btnComprar_R100 = btnComprar_R100;
    }

    public Button getBtnPausar_R100() {
        return btnPausar_R100;
    }

    public void setBtnPausar_R100(Button btnPausar_R100) {
        this.btnPausar_R100 = btnPausar_R100;
    }

    public Button getBtnStop_R100() {
        return btnStop_R100;
    }

    public void setBtnStop_R100(Button btnStop_R100) {
        this.btnStop_R100 = btnStop_R100;
    }

    public Label getLblInvestido_R100() {
        return lblInvestido_R100;
    }

    public void setLblInvestido_R100(Label lblInvestido_R100) {
        this.lblInvestido_R100 = lblInvestido_R100;
    }

    public Label getLblInvestidoPorc_R100() {
        return lblInvestidoPorc_R100;
    }

    public void setLblInvestidoPorc_R100(Label lblInvestidoPorc_R100) {
        this.lblInvestidoPorc_R100 = lblInvestidoPorc_R100;
    }

    public Label getLblPremiacao_R100() {
        return lblPremiacao_R100;
    }

    public void setLblPremiacao_R100(Label lblPremiacao_R100) {
        this.lblPremiacao_R100 = lblPremiacao_R100;
    }

    public Label getLblPremiacaoPorc_R100() {
        return lblPremiacaoPorc_R100;
    }

    public void setLblPremiacaoPorc_R100(Label lblPremiacaoPorc_R100) {
        this.lblPremiacaoPorc_R100 = lblPremiacaoPorc_R100;
    }

    public Label getLblLucro_R100() {
        return lblLucro_R100;
    }

    public void setLblLucro_R100(Label lblLucro_R100) {
        this.lblLucro_R100 = lblLucro_R100;
    }

    public Label getLblLucroPorc_R100() {
        return lblLucroPorc_R100;
    }

    public void setLblLucroPorc_R100(Label lblLucroPorc_R100) {
        this.lblLucroPorc_R100 = lblLucroPorc_R100;
    }

    public TableView getTbvTransacoes_R100() {
        return tbvTransacoes_R100;
    }

    public void setTbvTransacoes_R100(TableView tbvTransacoes_R100) {
        this.tbvTransacoes_R100 = tbvTransacoes_R100;
    }

    public CheckBox getChkAtivo_R100() {
        return chkAtivo_R100;
    }

    public void setChkAtivo_R100(CheckBox chkAtivo_R100) {
        this.chkAtivo_R100 = chkAtivo_R100;
    }

    public Label getTpnLblLegendaExecucoes_R100() {
        return tpnLblLegendaExecucoes_R100;
    }

    public void setTpnLblLegendaExecucoes_R100(Label tpnLblLegendaExecucoes_R100) {
        this.tpnLblLegendaExecucoes_R100 = tpnLblLegendaExecucoes_R100;
    }

    public Label getTpnLblExecucoes_R100() {
        return tpnLblExecucoes_R100;
    }

    public void setTpnLblExecucoes_R100(Label tpnLblExecucoes_R100) {
        this.tpnLblExecucoes_R100 = tpnLblExecucoes_R100;
    }

    public Label getTpnLblVitorias_R100() {
        return tpnLblVitorias_R100;
    }

    public void setTpnLblVitorias_R100(Label tpnLblVitorias_R100) {
        this.tpnLblVitorias_R100 = tpnLblVitorias_R100;
    }

    public Label getTpnLblDerrotas_R100() {
        return tpnLblDerrotas_R100;
    }

    public void setTpnLblDerrotas_R100(Label tpnLblDerrotas_R100) {
        this.tpnLblDerrotas_R100 = tpnLblDerrotas_R100;
    }

    public Label getTpnLblLucro_R100() {
        return tpnLblLucro_R100;
    }

    public void setTpnLblLucro_R100(Label tpnLblLucro_R100) {
        this.tpnLblLucro_R100 = tpnLblLucro_R100;
    }

    public LineChart getGrafLine_HZ10() {
        return grafLine_HZ10;
    }

    public void setGrafLine_HZ10(LineChart grafLine_HZ10) {
        this.grafLine_HZ10 = grafLine_HZ10;
    }

    public NumberAxis getyAxisLine_HZ10() {
        return yAxisLine_HZ10;
    }

    public void setyAxisLine_HZ10(NumberAxis yAxisLine_HZ10) {
        this.yAxisLine_HZ10 = yAxisLine_HZ10;
    }

    public LineChart getGrafLine_HZ25() {
        return grafLine_HZ25;
    }

    public void setGrafLine_HZ25(LineChart grafLine_HZ25) {
        this.grafLine_HZ25 = grafLine_HZ25;
    }

    public NumberAxis getyAxisLine_HZ25() {
        return yAxisLine_HZ25;
    }

    public void setyAxisLine_HZ25(NumberAxis yAxisLine_HZ25) {
        this.yAxisLine_HZ25 = yAxisLine_HZ25;
    }

    public LineChart getGrafLine_HZ50() {
        return grafLine_HZ50;
    }

    public void setGrafLine_HZ50(LineChart grafLine_HZ50) {
        this.grafLine_HZ50 = grafLine_HZ50;
    }

    public NumberAxis getyAxisLine_HZ50() {
        return yAxisLine_HZ50;
    }

    public void setyAxisLine_HZ50(NumberAxis yAxisLine_HZ50) {
        this.yAxisLine_HZ50 = yAxisLine_HZ50;
    }

    public LineChart getGrafLine_HZ75() {
        return grafLine_HZ75;
    }

    public void setGrafLine_HZ75(LineChart grafLine_HZ75) {
        this.grafLine_HZ75 = grafLine_HZ75;
    }

    public NumberAxis getyAxisLine_HZ75() {
        return yAxisLine_HZ75;
    }

    public void setyAxisLine_HZ75(NumberAxis yAxisLine_HZ75) {
        this.yAxisLine_HZ75 = yAxisLine_HZ75;
    }

    public LineChart getGrafLine_HZ100() {
        return grafLine_HZ100;
    }

    public void setGrafLine_HZ100(LineChart grafLine_HZ100) {
        this.grafLine_HZ100 = grafLine_HZ100;
    }

    public NumberAxis getyAxisLine_HZ100() {
        return yAxisLine_HZ100;
    }

    public void setyAxisLine_HZ100(NumberAxis yAxisLine_HZ100) {
        this.yAxisLine_HZ100 = yAxisLine_HZ100;
    }

    public static Integer getVolHz10() {
        return VOL_HZ10;
    }

    public static Integer getVolHz25() {
        return VOL_HZ25;
    }

    public static Integer getVolHz50() {
        return VOL_HZ50;
    }

    public static Integer getVolHz75() {
        return VOL_HZ75;
    }

    public static Integer getVolHz100() {
        return VOL_HZ100;
    }

    public static boolean isVol1s() {
        return VOL_1S.get();
    }

    public static BooleanProperty VOL_1SProperty() {
        return VOL_1S;
    }

    public static void setVol1s(boolean vol1s) {
        VOL_1S.set(vol1s);
    }

    public ChangeListener[] getListenerTickSubindo() {
        return listenerTickSubindo;
    }

    public void setListenerTickSubindo(ChangeListener[] listenerTickSubindo) {
        this.listenerTickSubindo = listenerTickSubindo;
    }

//    public ChangeListener[] getListenerVolatilidadeNegociando() {
//        return listenerVolatilidadeNegociando;
//    }
//
//    public void setListenerVolatilidadeNegociando(ChangeListener[] listenerVolatilidadeNegociando) {
//        this.listenerVolatilidadeNegociando = listenerVolatilidadeNegociando;
//    }

    public static BooleanProperty[] getEstrategiaBotaoContrato() {
        return estrategiaBotaoContrato;
    }

    public static void setEstrategiaBotaoContrato(BooleanProperty[] estrategiaBotaoContrato) {
        Operacao.estrategiaBotaoContrato = estrategiaBotaoContrato;
    }

    public static BooleanProperty[] getEstrategiaBotaoComprar() {
        return estrategiaBotaoComprar;
    }

    public static void setEstrategiaBotaoComprar(BooleanProperty[] estrategiaBotaoComprar) {
        Operacao.estrategiaBotaoComprar = estrategiaBotaoComprar;
    }

    public static BooleanProperty[] getEstrategiaBotaoPausar() {
        return estrategiaBotaoPausar;
    }

    public static void setEstrategiaBotaoPausar(BooleanProperty[] estrategiaBotaoPausar) {
        Operacao.estrategiaBotaoPausar = estrategiaBotaoPausar;
    }

    public static BooleanProperty[] getEstrategiaBotaoStop() {
        return estrategiaBotaoStop;
    }

    public static void setEstrategiaBotaoStop(BooleanProperty[] estrategiaBotaoStop) {
        Operacao.estrategiaBotaoStop = estrategiaBotaoStop;
    }

    public static ObservableList<Transacoes> getTransacoesObservableList() {
        return transacoesObservableList;
    }

    public static void setTransacoesObservableList(ObservableList<Transacoes> transacoesObservableList) {
        Operacao.transacoesObservableList = transacoesObservableList;
    }

    public static TmodelTransacoes[] getTmodelTransacoes() {
        return tmodelTransacoes;
    }

    public static void setTmodelTransacoes(TmodelTransacoes[] tmodelTransacoes) {
        Operacao.tmodelTransacoes = tmodelTransacoes;
    }

    public static FilteredList<Transacoes>[] getTransacoesFilteredList() {
        return transacoesFilteredList;
    }

    public static void setTransacoesFilteredList(FilteredList<Transacoes>[] transacoesFilteredList) {
        Operacao.transacoesFilteredList = transacoesFilteredList;
    }

    public static TransacoesDAO getTransacoesDAO() {
        return transacoesDAO;
    }

    public static void setTransacoesDAO(TransacoesDAO transacoesDAO) {
        Operacao.transacoesDAO = transacoesDAO;
    }

    public static ObjectProperty<Transacoes>[] getTransacao() {
        return transacao;
    }

    public static void setTransacao(ObjectProperty<Transacoes>[] transacao) {
        Operacao.transacao = transacao;
    }

    public static TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public static void setTransactionDAO(TransactionDAO transactionDAO) {
        Operacao.transactionDAO = transactionDAO;
    }

    public static int getQtdLossResetStake() {
        return qtdLossResetStake.get();
    }

    public static IntegerProperty qtdLossResetStakeProperty() {
        return qtdLossResetStake;
    }

    public static void setQtdLossResetStake(int qtdLossResetStake) {
        Operacao.qtdLossResetStake.set(qtdLossResetStake);
    }

    public static ObjectProperty<BigDecimal>[] getMeuLucroVolatilidade() {
        return meuLucroVolatilidade;
    }

    public static void setMeuLucroVolatilidade(ObjectProperty<BigDecimal>[] meuLucroVolatilidade) {
        Operacao.meuLucroVolatilidade = meuLucroVolatilidade;
    }

    public static IntegerProperty[] getQtdMaiorSeqDerrota() {
        return qtdMaiorSeqDerrota;
    }

    public static void setQtdMaiorSeqDerrota(IntegerProperty[] qtdMaiorSeqDerrota) {
        Operacao.qtdMaiorSeqDerrota = qtdMaiorSeqDerrota;
    }

    public static IntegerProperty[] getQtdMaiorSeqVitoria() {
        return qtdMaiorSeqVitoria;
    }

    public static void setQtdMaiorSeqVitoria(IntegerProperty[] qtdMaiorSeqVitoria) {
        Operacao.qtdMaiorSeqVitoria = qtdMaiorSeqVitoria;
    }


}
