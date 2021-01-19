package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.RoboEstrategia;
import br.com.tlmacedo.binary.model.dao.ContaTokenDAO;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_STYLE;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Util_Json;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.tlmacedo.binary.interfaces.Constants.*;

public class Operacoes implements Initializable {

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
    static final ObservableList<Symbol> SYMBOL_OBSERVABLE_LIST =
            FXCollections.observableArrayList(
                    getSymbolDAO().getAll(Symbol.class, null, null)
            );

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
    static IntegerProperty graficoQtdTicksAnalise = new SimpleIntegerProperty(1000);
    static IntegerProperty graficoQtdTicks = new SimpleIntegerProperty(100);

    Text[][] graficoTxtLegendaDigito_R = new Text[5][10];

    //** gráficos em barras **
    XYChart.Series<String, Number>[] graficoBarrasVolatilidade_R = new XYChart.Series[5];
    ObservableList<Data<String, Number>>[] graficoBarrasListValoresDigitos_R = new ObservableList[5];
    static ObservableList<LongProperty>[] graficoBarrasListQtdDigito_R = new ObservableList[5];


    //** graficos em linha **
    XYChart.Series<String, Number>[] graficoLinhasVolatilidade_R = new XYChart.Series[5];
    ObservableList<Data<String, Number>>[] graficoLinhasListDataDigitos_R = new ObservableList[5];
    static ObservableList<HistoricoDeTicks>[] graficoLinhasListQtdDigito_R = new ObservableList[5];

    //** gráficos MACD **
    XYChart.Series<String, Number>[] graficoMACDVolatilidade_R = new XYChart.Series[5];
    ObservableList<Data<String, Number>>[] graficoMACDListDataDigitos_R = new ObservableList[5];
    static ObservableList<HistoricoDeTicks>[] graficoMACDListQtdDigito_R = new ObservableList[5];


    /**
     * Robos
     */


    /**
     * Variaveis de controle do sistema
     */

    BooleanProperty appAutorizado = new SimpleBooleanProperty(false);
    Timeline roboRelogio;
    LongProperty roboHoraInicial = new SimpleLongProperty();
    LongProperty roboCronometro = new SimpleLongProperty();
    BooleanProperty roboCronometroAtivado = new SimpleBooleanProperty(false);


    /**
     * Variaveis de informações para operadores
     */
    //** Variaveis **
    static ObjectProperty<Symbol>[] operador = new ObjectProperty[5];
    static BooleanProperty[] operadorAtivo = new BooleanProperty[5];
    static BooleanProperty[] operadorCompraAutorizada = new BooleanProperty[5];
    static BooleanProperty[] operadorNegociando = new BooleanProperty[5];
    static BooleanProperty[] tickSubindo = new BooleanProperty[5];
    static ObjectProperty<Tick>[] ultimoTick = new ObjectProperty[5];
    static IntegerProperty[] maiorQtdDigito = new IntegerProperty[5];
    static IntegerProperty[] menorQtdDigito = new IntegerProperty[5];
    static StringProperty[] informacaoDetalhe01 = new StringProperty[5];
    static StringProperty[] informacaoValor01 = new StringProperty[5];
    static StringProperty[] informacaoDetalhe02 = new StringProperty[5];
    static StringProperty[] informacaoValor02 = new StringProperty[5];
    //** Listas **
    static ObservableList<HistoricoDeTicks>[] historicoDeTicksObservableList = new ObservableList[5];
    static ObservableList<HistoricoDeTicks>[] HistoricoDeTicksAnaliseObservableList = new ObservableList[5];
    static ObservableList<Transaction>[] transactionObservableList = new ObservableList[5];
    static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

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


    // Operações 01
    public TitledPane tpn_Op01;
    public BarChart<String, Number> graficoBarras_Op01;
    public NumberAxis yAxisBarras_Op01;
    public LineChart graficoLinhas_Op01;
    public NumberAxis yAxisLinhas_Op01;
    public Label lblInf01_Op01;
    public Label lblVlrInf01_Op01;
    public Label lblPorcInf01_Op01;
    public Label lblInf02_Op01;
    public Label lblVlrInf02_Op01;
    public Label lblPorcInf02_Op01;
    public Label lblTickUltimo_Op01;
    public Label lblLegendaTickUltimo_Op01;
    public Button btnContratos_Op01;
    public Button btnComprar_Op01;
    public Button btnPausar_Op01;
    public Button btnStop_Op01;
    public Label lblInvestido_Op01;
    public Label lblInvestidoPorc_Op01;
    public Label lblPremiacao_Op01;
    public Label lblPremiacaoPorc_Op01;
    public Label lblLucro_Op01;
    public Label lblLucroPorc_Op01;
    public TableView<Transacoes> tbvTransacoes_Op01;
    public ComboBox<Symbol> cboSymbol01;
    public CheckBox chkAtivo_Op01;
    public Label tpnLblLegendaExecucoes_Op01;
    public Label tpnLblExecucoes_Op01;
    public Label tpnLblVitorias_Op01;
    public Label tpnLblDerrotas_Op01;
    public Label tpnLblLucro_Op01;

    // Operações 02
    public TitledPane tpn_Op02;
    public BarChart<String, Number> graficoBarras_Op02;
    public NumberAxis yAxisBarras_Op02;
    public LineChart graficoLinhas_Op02;
    public NumberAxis yAxisLinhas_Op02;
    public Label lblInf01_Op02;
    public Label lblVlrInf01_Op02;
    public Label lblPorcInf01_Op02;
    public Label lblInf02_Op02;
    public Label lblVlrInf02_Op02;
    public Label lblPorcInf02_Op02;
    public Label lblTickUltimo_Op02;
    public Label lblLegendaTickUltimo_Op02;
    public Button btnContratos_Op02;
    public Button btnComprar_Op02;
    public Button btnPausar_Op02;
    public Button btnStop_Op02;
    public Label lblInvestido_Op02;
    public Label lblInvestidoPorc_Op02;
    public Label lblPremiacao_Op02;
    public Label lblPremiacaoPorc_Op02;
    public Label lblLucro_Op02;
    public Label lblLucroPorc_Op02;
    public TableView<Transacoes> tbvTransacoes_Op02;
    public ComboBox<Symbol> cboSymbol02;
    public CheckBox chkAtivo_Op02;
    public Label tpnLblLegendaExecucoes_Op02;
    public Label tpnLblExecucoes_Op02;
    public Label tpnLblVitorias_Op02;
    public Label tpnLblDerrotas_Op02;
    public Label tpnLblLucro_Op02;

    // Operações 03
    public TitledPane tpn_Op03;
    public BarChart<String, Number> graficoBarras_Op03;
    public NumberAxis yAxisBarras_Op03;
    public LineChart graficoLinhas_Op03;
    public NumberAxis yAxisLinhas_Op03;
    public Label lblInf01_Op03;
    public Label lblVlrInf01_Op03;
    public Label lblPorcInf01_Op03;
    public Label lblInf02_Op03;
    public Label lblVlrInf02_Op03;
    public Label lblPorcInf02_Op03;
    public Label lblTickUltimo_Op03;
    public Label lblLegendaTickUltimo_Op03;
    public Button btnContratos_Op03;
    public Button btnComprar_Op03;
    public Button btnPausar_Op03;
    public Button btnStop_Op03;
    public Label lblInvestido_Op03;
    public Label lblInvestidoPorc_Op03;
    public Label lblPremiacao_Op03;
    public Label lblPremiacaoPorc_Op03;
    public Label lblLucro_Op03;
    public Label lblLucroPorc_Op03;
    public TableView<Transacoes> tbvTransacoes_Op03;
    public ComboBox<Symbol> cboSymbol03;
    public CheckBox chkAtivo_Op03;
    public Label tpnLblLegendaExecucoes_Op03;
    public Label tpnLblExecucoes_Op03;
    public Label tpnLblVitorias_Op03;
    public Label tpnLblDerrotas_Op03;
    public Label tpnLblLucro_Op03;

    // Operações 04
    public TitledPane tpn_Op04;
    public BarChart<String, Number> graficoBarras_Op04;
    public NumberAxis yAxisBarras_Op04;
    public LineChart graficoLinhas_Op04;
    public NumberAxis yAxisLinhas_Op04;
    public Label lblInf01_Op04;
    public Label lblVlrInf01_Op04;
    public Label lblPorcInf01_Op04;
    public Label lblInf02_Op04;
    public Label lblVlrInf02_Op04;
    public Label lblPorcInf02_Op04;
    public Label lblTickUltimo_Op04;
    public Label lblLegendaTickUltimo_Op04;
    public Button btnContratos_Op04;
    public Button btnComprar_Op04;
    public Button btnPausar_Op04;
    public Button btnStop_Op04;
    public Label lblInvestido_Op04;
    public Label lblInvestidoPorc_Op04;
    public Label lblPremiacao_Op04;
    public Label lblPremiacaoPorc_Op04;
    public Label lblLucro_Op04;
    public Label lblLucroPorc_Op04;
    public TableView<Transacoes> tbvTransacoes_Op04;
    public ComboBox<Symbol> cboSymbol04;
    public CheckBox chkAtivo_Op04;
    public Label tpnLblLegendaExecucoes_Op04;
    public Label tpnLblExecucoes_Op04;
    public Label tpnLblVitorias_Op04;
    public Label tpnLblDerrotas_Op04;
    public Label tpnLblLucro_Op04;

    // Operações 05
    public TitledPane tpn_Op05;
    public BarChart<String, Number> graficoBarras_Op05;
    public NumberAxis yAxisBarras_Op05;
    public LineChart graficoLinhas_Op05;
    public NumberAxis yAxisLinhas_Op05;
    public Label lblInf01_Op05;
    public Label lblVlrInf01_Op05;
    public Label lblPorcInf01_Op05;
    public Label lblInf02_Op05;
    public Label lblVlrInf02_Op05;
    public Label lblPorcInf02_Op05;
    public Label lblTickUltimo_Op05;
    public Label lblLegendaTickUltimo_Op05;
    public Button btnContratos_Op05;
    public Button btnComprar_Op05;
    public Button btnPausar_Op05;
    public Button btnStop_Op05;
    public Label lblInvestido_Op05;
    public Label lblInvestidoPorc_Op05;
    public Label lblPremiacao_Op05;
    public Label lblPremiacaoPorc_Op05;
    public Label lblLucro_Op05;
    public Label lblLucroPorc_Op05;
    public TableView<Transacoes> tbvTransacoes_Op05;
    public ComboBox<Symbol> cboSymbol05;
    public CheckBox chkAtivo_Op05;
    public Label tpnLblLegendaExecucoes_Op05;
    public Label tpnLblExecucoes_Op05;
    public Label tpnLblVitorias_Op05;
    public Label tpnLblDerrotas_Op05;
    public Label tpnLblLucro_Op05;

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        carregarVariaveisObjetos();
        carregarObjetos();
        carregarAcoesObjetos();
        conectarObjetosEmVariaveis();

    }

    private Task getTaskWsBinary() {

        return new Task() {
            @Override
            protected Object call() throws Exception {
                getWsClient().connect();
                wsConectadoProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n) {
                            monitorarTicks();
                            carregarGraficos();

                        } else {
                            getBtnStop().fire();
                            new Service_Alert("Conexão fechou", "Conexão com a binary foi fechada!!", null)
                                    .alertOk();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                return null;
            }
        };

    }

    /**
     * Carregar Objetos e Variaveis
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void carregarVariaveisObjetos() {

        for (int operadorId = 0; operadorId < 5; operadorId++) {
            getOperador()[operadorId] = new SimpleObjectProperty<>();
            getOperadorAtivo()[operadorId] = new SimpleBooleanProperty(false);
            getOperadorCompraAutorizada()[operadorId] = new SimpleBooleanProperty(false);
            getOperadorNegociando()[operadorId] = new SimpleBooleanProperty(false);
            getTickSubindo()[operadorId] = new SimpleBooleanProperty();
            getUltimoTick()[operadorId] = new SimpleObjectProperty<>();
            getMaiorQtdDigito()[operadorId] = new SimpleIntegerProperty();
            getMenorQtdDigito()[operadorId] = new SimpleIntegerProperty();
            getInformacaoDetalhe01()[operadorId] = new SimpleStringProperty();
            getInformacaoValor01()[operadorId] = new SimpleStringProperty();
            getInformacaoValor01()[operadorId] = new SimpleStringProperty();
            getInformacaoValor02()[operadorId] = new SimpleStringProperty();

            getHistoricoDeTicksObservableList()[operadorId] = FXCollections.observableArrayList();
            getHistoricoDeTicksAnaliseObservableList()[operadorId] = FXCollections.observableArrayList();
            getTransactionObservableList()[operadorId] = FXCollections.observableArrayList();

            graficoEmBarras(operadorId);
        }
        Thread threadInicial = new Thread(getTaskWsBinary());
        threadInicial.setDaemon(true);
        threadInicial.start();

    }

    private void carregarObjetos() {

        getCboConta().setItems(getContaTokenDAO().getAll(ContaToken.class, "ativo=1", null)
                .stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        ObservableList<Symbol> symbols = getSymbolDAO().getAll(Symbol.class, null, null).stream().collect(Collectors.toCollection(FXCollections::observableArrayList));

        getCboSymbol01().setItems(symbols);
        getCboSymbol02().setItems(symbols);
        getCboSymbol03().setItems(symbols);
        getCboSymbol04().setItems(symbols);
        getCboSymbol05().setItems(symbols);

    }

    private void carregarAcoesObjetos() {

        getOperador()[0].bind(getCboSymbol01().valueProperty());
        getOperador()[1].bind(getCboSymbol02().valueProperty());
        getOperador()[2].bind(getCboSymbol03().valueProperty());
        getOperador()[3].bind(getCboSymbol04().valueProperty());
        getOperador()[4].bind(getCboSymbol05().valueProperty());

        for (int operadorId = 0; operadorId < 5; operadorId++) {
            int finalOperadorId = operadorId;
            getOperador()[operadorId].addListener((ov, o, n) -> {
                if (n == null) return;
                System.out.printf("operador[%s]: %s\n", finalOperadorId, n);
                solicitarTicks(n);
            });
        }

    }

    private void conectarObjetosEmVariaveis() {

        getLblTickUltimo_Op01().textProperty().unbind();
        getLblTickUltimo_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
            if (getUltimoTick()[0].getValue() == null)
                return "";
            return getUltimoTick()[0].getValue().toString();
        }, getUltimoTick()[0]));
        getLblTickUltimo_Op01().styleProperty().unbind();
        getLblTickUltimo_Op01().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getTickSubindo()[0].getValue() == null)
//                return STYLE_TICK_NEUTRO;
            return getTickSubindo()[0].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
        }, getTickSubindo()[0]));
        getLblLegendaTickUltimo_Op01().styleProperty().unbind();
        getLblLegendaTickUltimo_Op01().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getOperadorNegociando()[0].getValue() == null)
                return null;
            return getOperadorNegociando()[0].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
        }, getOperadorNegociando()[0]));

        getLblTickUltimo_Op02().textProperty().unbind();
        getLblTickUltimo_Op02().textProperty().bind(Bindings.createStringBinding(() -> {
            if (getUltimoTick()[1].getValue() == null)
                return "";
            return getUltimoTick()[1].getValue().toString();
        }, getUltimoTick()[1]));
        getLblTickUltimo_Op02().styleProperty().unbind();
        getLblTickUltimo_Op02().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getTickSubindo()[1].getValue() == null)
                return null;
            return getTickSubindo()[1].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
        }, getTickSubindo()[1]));
        getLblLegendaTickUltimo_Op02().styleProperty().unbind();
        getLblLegendaTickUltimo_Op02().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getOperadorNegociando()[1].getValue() == null)
                return null;
            return getOperadorNegociando()[1].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
        }, getOperadorNegociando()[1]));


        getLblTickUltimo_Op03().textProperty().unbind();
        getLblTickUltimo_Op03().textProperty().bind(Bindings.createStringBinding(() -> {
            if (getUltimoTick()[2].getValue() == null)
                return "";
            return getUltimoTick()[2].getValue().toString();
        }, getUltimoTick()[2]));
        getLblTickUltimo_Op03().styleProperty().unbind();
        getLblTickUltimo_Op03().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getTickSubindo()[2].getValue() == null)
                return null;
            return getTickSubindo()[2].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
        }, getTickSubindo()[2]));
        getLblLegendaTickUltimo_Op03().styleProperty().unbind();
        getLblLegendaTickUltimo_Op03().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getOperadorNegociando()[2].getValue() == null)
                return null;
            return getOperadorNegociando()[2].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
        }, getOperadorNegociando()[2]));


        getLblTickUltimo_Op04().textProperty().unbind();
        getLblTickUltimo_Op04().textProperty().bind(Bindings.createStringBinding(() -> {
            if (getUltimoTick()[3].getValue() == null)
                return "";
            return getUltimoTick()[3].getValue().toString();
        }, getUltimoTick()[3]));
        getLblTickUltimo_Op04().styleProperty().unbind();
        getLblTickUltimo_Op04().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getTickSubindo()[3].getValue() == null)
                return null;
            return getTickSubindo()[3].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
        }, getTickSubindo()[3]));
        getLblLegendaTickUltimo_Op04().styleProperty().unbind();
        getLblLegendaTickUltimo_Op04().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getOperadorNegociando()[3].getValue() == null)
                return null;
            return getOperadorNegociando()[3].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
        }, getOperadorNegociando()[3]));


        getLblTickUltimo_Op05().textProperty().unbind();
        getLblTickUltimo_Op05().textProperty().bind(Bindings.createStringBinding(() -> {
            if (getUltimoTick()[4].getValue() == null)
                return "";
            return getUltimoTick()[4].getValue().toString();
        }, getUltimoTick()[4]));
        getLblTickUltimo_Op05().styleProperty().unbind();
        getLblTickUltimo_Op05().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getTickSubindo()[4].getValue() == null)
                return null;
            return getTickSubindo()[4].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
        }, getTickSubindo()[4]));
        getLblLegendaTickUltimo_Op05().styleProperty().unbind();
        getLblLegendaTickUltimo_Op05().styleProperty().bind(Bindings.createStringBinding(() -> {
            if (getOperadorNegociando()[4].getValue() == null)
                return null;
            return getOperadorNegociando()[4].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
        }, getOperadorNegociando()[4]));

    }

    /**
     * Socilitações para Web Service da Binary!!!
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void solicitarActiveSymbols() {

        String jsonActiveSymbols = Util_Json.getJson_from_Object(new ActiveSymbols());
        getWsClient().getMyWebSocket().send(jsonActiveSymbols);

    }

    private void solicitarTicks(Symbol symbol) {

        Integer tempoVela = null;
//        Symbol symbol = getActiveSymbolObservableList().stream()
//                .filter(activeSymbol -> activeSymbol.getId().intValue() == operador.getSymbol().getId())
//                .findFirst().get();
        Passthrough passthrough = new Passthrough();
        passthrough.setSymbol(symbol);
        passthrough.setMensagem("testando para ver o que se dá!");

//        for (TICK_STYLE style : TICK_STYLE.values()) {
        TICK_STYLE style = TICK_STYLE.TICKS;
        passthrough.setTick_style(style);
        String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(symbol.getSymbol(),
                getGraficoQtdTicks(), style, tempoVela, passthrough));
        if (tempoVela == null) jsonHistory = jsonHistory.replace(",\"granularity\":null", "");
        if (passthrough == null) jsonHistory = jsonHistory.replace(",\"passthrough\":null", "");
        getWsClient().getMyWebSocket().send(jsonHistory);
//        }
    }


    /**
     * Comandos diversos
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

//    public static Integer getSymbolId(String symbol) {
//        for (Symbol activeSymbol : getActiveSymbolObservableList())
//            if (symbol.equals(activeSymbol.getSymbol()))
//                return activeSymbol.getId().intValue();
//        return null;
//    }

    /**
     * Atualizações carregamentos e manipulações com graficos.
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void graficoEmBarras(Integer operadorId) {

        getGraficoBarrasListValoresDigitos_R()[operadorId] = FXCollections.observableArrayList();
        getGraficoBarrasListQtdDigito_R()[operadorId] = FXCollections.observableArrayList();
        getGraficoBarrasVolatilidade_R()[operadorId] = new XYChart.Series<>();

        for (int digito = 0; digito < 10; digito++) {
            getGraficoTxtLegendaDigito_R()[operadorId][digito] = new Text();
            getGraficoTxtLegendaDigito_R()[operadorId][digito].setFont(Font.font("Arial", 10));
            getGraficoTxtLegendaDigito_R()[operadorId][digito].setStyle("-fx-text-fill: #fff;");

            getGraficoBarrasListQtdDigito_R()[operadorId].add(digito, new SimpleLongProperty(0));
            getGraficoBarrasListValoresDigitos_R()[operadorId].add(new Data<>(String.valueOf(digito), 0));

            int finalDigito = digito;
            getGraficoBarrasListQtdDigito_R()[operadorId].get(digito).addListener((ov, o, n) -> {
                if (n == null) return;
                Double porcento = n.intValue() != 0
                        ? (n.intValue() / getGraficoQtdTicks() / 100.) : 0.;
                getGraficoBarrasListValoresDigitos_R()[operadorId].get(finalDigito).setYValue(porcento.intValue());
                getGraficoTxtLegendaDigito_R()[operadorId][finalDigito].setText(String.format("%s%%", porcento.intValue()));
            });
        }
        System.out.printf("operadorId: [%s]\n", operadorId);
        switch (operadorId) {
            case 0 -> {
                getyAxisBarras_Op01().setUpperBound(25);
                getGraficoBarras_Op01().getData().add(getGraficoBarrasVolatilidade_R()[0]);
                getGraficoBarras_Op01().setVisible(true);
            }
            case 1 -> {
                getyAxisBarras_Op02().setUpperBound(25);
                getGraficoBarras_Op02().getData().add(getGraficoBarrasVolatilidade_R()[1]);
                getGraficoBarras_Op02().setVisible(true);
            }
            case 2 -> {
                getyAxisBarras_Op03().setUpperBound(25);
                getGraficoBarras_Op03().getData().add(getGraficoBarrasVolatilidade_R()[2]);
                getGraficoBarras_Op03().setVisible(true);
            }
            case 3 -> {
                getyAxisBarras_Op04().setUpperBound(25);
                getGraficoBarras_Op04().getData().add(getGraficoBarrasVolatilidade_R()[3]);
                getGraficoBarras_Op04().setVisible(true);
            }
            case 4 -> {
                getyAxisBarras_Op05().setUpperBound(25);
                getGraficoBarras_Op05().getData().add(getGraficoBarrasVolatilidade_R()[4]);
                getGraficoBarras_Op05().setVisible(true);
            }
        }

    }

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

    private void atualizaCoresGrafico(Integer operadorId) {

        for (int digito = 0; digito < 10; digito++) {
            if (getGraficoBarrasListQtdDigito_R()[operadorId].get(digito).getValue().intValue() >= getMaiorQtdDigito()[operadorId].getValue())
                getGraficoBarrasListValoresDigitos_R()[operadorId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MAIOR);
            else if (getGraficoBarrasListQtdDigito_R()[operadorId].get(digito).getValue().intValue() <= getMenorQtdDigito()[operadorId].getValue())
                getGraficoBarrasListValoresDigitos_R()[operadorId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DIGITO_MENOR);
            else
                getGraficoBarrasListValoresDigitos_R()[operadorId].get(digito).getNode().setStyle(STYLE_GRAF_BARRAS_DEFAULT);
        }

    }

    /**
     * Monitorar retornos do WebService
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void monitorarTicks() {

//        getHistoricoDeTicksObservableList().addListener((ListChangeListener<? super HistoricoDeTicks>) c -> {
//            Platform.runLater(() -> {
//                while (c.next()) {
//                    for (HistoricoDeTicks historicoDeTicks : c.getAddedSubList()) {
//                        if (getCboSymbol01().getValue() != null
//                                && getCboSymbol01().getValue().equals(historicoDeTicks.getSymbol()))
//                            getUltimoTick()[0].setValue();
//                    }
//                }
//            });
//        });

        for (int operadorId = 0; operadorId < 5; operadorId++) {
            int finalOperadorId = operadorId;
            getUltimoTick()[operadorId].addListener((ov, o, n) -> {
                Platform.runLater(() -> {
                    Map<Integer, Long> vlrDigitos = getHistoricoDeTicksObservableList()[finalOperadorId].stream()
                            .filter(historicoDeTicks -> historicoDeTicks.getSymbol().getSymbol()
                                    .equals(getOperador()[finalOperadorId].getValue().getSymbol()))
                            .map(HistoricoDeTicks::getUltimoDigito)
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    System.out.printf("vlrDigitos[%s]: %s\n", finalOperadorId, vlrDigitos);

                    getMenorQtdDigito()[finalOperadorId].setValue(Collections.min(vlrDigitos.values()));
                    getMaiorQtdDigito()[finalOperadorId].setValue(Collections.max(vlrDigitos.values()));

                    for (int digito = 0; digito < 10; digito++) {
                        getGraficoBarrasListQtdDigito_R()[finalOperadorId].get(digito).setValue(
                                vlrDigitos.containsKey(digito) ? vlrDigitos.get(digito) : 0L);
                        if (!vlrDigitos.containsKey(digito))
                            getMenorQtdDigito()[finalOperadorId].setValue(0);
                    }

                    if (getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].size() > 1)
                        getTickSubindo()[finalOperadorId].setValue(
                                getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].get(0).getPrice()
                                        .compareTo(getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].get(1).getPrice()) >= 0);

                    //Transacoes transacoesTemp;
                    //                    if (getTransacoesObservableList().size() > 0
                    //                            && (transacoesTemp = getTransacoesObservableList().stream()
                    //                            .filter(transacoes -> transacoes.getSymbol().getId() == symbolId + 1
                    //                                    && (transacoes.getTickCompra() == null
                    //                                    || transacoes.getTickVenda() == null))
                    //                            .findFirst().orElse(null)) != null) {
                    //                        int index = getTransacoesObservableList().indexOf(transacoesTemp);
                    //                        try {
                    //                            if (transacoesTemp.getTickCompra() == null) {
                    //                                transacoesTemp.setTickCompra(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                    //                                        .filter(historicoDeTicks -> historicoDeTicks.getTime() >= transacoesTemp.getDataHoraCompra())
                    //                                        .findFirst().get().getPrice());
                    //                            } else {
                    //                                String contrato = transacoesTemp.getContract_type().toLowerCase();
                    //                                transacoesTemp.setTickVenda(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                    //                                        .filter(historicoDeTicks -> {
                    //                                            if (contrato.contains("call") || contrato.contains("put"))
                    //                                                return historicoDeTicks.getTime() > transacoesTemp.getDataHoraExpiry();
                    //                                            return historicoDeTicks.getTime() >= transacoesTemp.getDataHoraExpiry();
                    //                                        }).sorted(Comparator.comparing(HistoricoDeTicks::getTime))
                    //                                        .findFirst().get().getPrice());
                    //                                if (transacoesTemp.getTickCompra() != null && transacoesTemp.getTickVenda() != null) {
                    //                                    getTransacoesDAO().merger(transacoesTemp);
                    //                                }
                    //                            }
                    //                        } catch (Exception ex) {
                    //                            if (!(ex instanceof NullPointerException) && !(ex instanceof NoSuchElementException))
                    //                                ex.printStackTrace();
                    //                        } finally {
                    //                            getTransacoesObservableList().set(index, transacoesTemp);
                    //                        }
                    //                    }

                    atualizaCoresGrafico(finalOperadorId);
                });
            });
        }

    }

    private void carregarGraficos() {

        Platform.runLater(() -> {
            for (int operadorId = 0; operadorId < 5; operadorId++) {
                for (int digito = 0; digito < 10; digito++) {
                    getGraficoBarrasVolatilidade_R()[operadorId].getData().add(getGraficoBarrasListValoresDigitos_R()[operadorId].get(digito));
                    getGraficoBarrasVolatilidade_R()[operadorId].setName(String.valueOf(digito));
                    displayLabelForData(getGraficoBarrasListValoresDigitos_R()[operadorId].get(digito),
                            getGraficoTxtLegendaDigito_R()[operadorId][digito]);
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


    public static SymbolDAO getSymbolDAO() {
        return symbolDAO;
    }

    public static void setSymbolDAO(SymbolDAO symbolDAO) {
        Operacoes.symbolDAO = symbolDAO;
    }

    public static ContaTokenDAO getContaTokenDAO() {
        return contaTokenDAO;
    }

    public static void setContaTokenDAO(ContaTokenDAO contaTokenDAO) {
        Operacoes.contaTokenDAO = contaTokenDAO;
    }

    public static TransacoesDAO getTransacoesDAO() {
        return transacoesDAO;
    }

    public static void setTransacoesDAO(TransacoesDAO transacoesDAO) {
        Operacoes.transacoesDAO = transacoesDAO;
    }

    public static TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public static void setTransactionDAO(TransactionDAO transactionDAO) {
        Operacoes.transactionDAO = transactionDAO;
    }

    public static ObservableList<Symbol> getSymbolObservableList() {
        return SYMBOL_OBSERVABLE_LIST;
    }

    public static ContaToken getContaToken() {
        return contaToken.get();
    }

    public static ObjectProperty<ContaToken> contaTokenProperty() {
        return contaToken;
    }

    public static void setContaToken(ContaToken contaToken) {
        Operacoes.contaToken.set(contaToken);
    }

    public static Authorize getAuthorize() {
        return authorize.get();
    }

    public static ObjectProperty<Authorize> authorizeProperty() {
        return authorize;
    }

    public static void setAuthorize(Authorize authorize) {
        Operacoes.authorize.set(authorize);
    }

    public static WSClient getWsClient() {
        return wsClient.get();
    }

    public static ObjectProperty<WSClient> wsClientProperty() {
        return wsClient;
    }

    public static void setWsClient(WSClient wsClient) {
        Operacoes.wsClient.set(wsClient);
    }

    public static boolean isWsConectado() {
        return wsConectado.get();
    }

    public static BooleanProperty wsConectadoProperty() {
        return wsConectado;
    }

    public static void setWsConectado(boolean wsConectado) {
        Operacoes.wsConectado.set(wsConectado);
    }

    public static int getGraficoQtdTicksAnalise() {
        return graficoQtdTicksAnalise.get();
    }

    public static IntegerProperty graficoQtdTicksAnaliseProperty() {
        return graficoQtdTicksAnalise;
    }

    public static void setGraficoQtdTicksAnalise(int graficoQtdTicksAnalise) {
        Operacoes.graficoQtdTicksAnalise.set(graficoQtdTicksAnalise);
    }

    public static int getGraficoQtdTicks() {
        return graficoQtdTicks.get();
    }

    public static IntegerProperty graficoQtdTicksProperty() {
        return graficoQtdTicks;
    }

    public static void setGraficoQtdTicks(int graficoQtdTicks) {
        Operacoes.graficoQtdTicks.set(graficoQtdTicks);
    }

    public Text[][] getGraficoTxtLegendaDigito_R() {
        return graficoTxtLegendaDigito_R;
    }

    public void setGraficoTxtLegendaDigito_R(Text[][] graficoTxtLegendaDigito_R) {
        this.graficoTxtLegendaDigito_R = graficoTxtLegendaDigito_R;
    }

    public XYChart.Series<String, Number>[] getGraficoBarrasVolatilidade_R() {
        return graficoBarrasVolatilidade_R;
    }

    public void setGraficoBarrasVolatilidade_R(XYChart.Series<String, Number>[] graficoBarrasVolatilidade_R) {
        this.graficoBarrasVolatilidade_R = graficoBarrasVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGraficoBarrasListValoresDigitos_R() {
        return graficoBarrasListValoresDigitos_R;
    }

    public void setGraficoBarrasListValoresDigitos_R(ObservableList<Data<String, Number>>[] graficoBarrasListValoresDigitos_R) {
        this.graficoBarrasListValoresDigitos_R = graficoBarrasListValoresDigitos_R;
    }

    public static ObservableList<LongProperty>[] getGraficoBarrasListQtdDigito_R() {
        return graficoBarrasListQtdDigito_R;
    }

    public static void setGraficoBarrasListQtdDigito_R(ObservableList<LongProperty>[] graficoBarrasListQtdDigito_R) {
        Operacoes.graficoBarrasListQtdDigito_R = graficoBarrasListQtdDigito_R;
    }

    public XYChart.Series<String, Number>[] getGraficoLinhasVolatilidade_R() {
        return graficoLinhasVolatilidade_R;
    }

    public void setGraficoLinhasVolatilidade_R(XYChart.Series<String, Number>[] graficoLinhasVolatilidade_R) {
        this.graficoLinhasVolatilidade_R = graficoLinhasVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGraficoLinhasListDataDigitos_R() {
        return graficoLinhasListDataDigitos_R;
    }

    public void setGraficoLinhasListDataDigitos_R(ObservableList<Data<String, Number>>[] graficoLinhasListDataDigitos_R) {
        this.graficoLinhasListDataDigitos_R = graficoLinhasListDataDigitos_R;
    }

    public static ObservableList<HistoricoDeTicks>[] getGraficoLinhasListQtdDigito_R() {
        return graficoLinhasListQtdDigito_R;
    }

    public static void setGraficoLinhasListQtdDigito_R(ObservableList<HistoricoDeTicks>[] graficoLinhasListQtdDigito_R) {
        Operacoes.graficoLinhasListQtdDigito_R = graficoLinhasListQtdDigito_R;
    }

    public XYChart.Series<String, Number>[] getGraficoMACDVolatilidade_R() {
        return graficoMACDVolatilidade_R;
    }

    public void setGraficoMACDVolatilidade_R(XYChart.Series<String, Number>[] graficoMACDVolatilidade_R) {
        this.graficoMACDVolatilidade_R = graficoMACDVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGraficoMACDListDataDigitos_R() {
        return graficoMACDListDataDigitos_R;
    }

    public void setGraficoMACDListDataDigitos_R(ObservableList<Data<String, Number>>[] graficoMACDListDataDigitos_R) {
        this.graficoMACDListDataDigitos_R = graficoMACDListDataDigitos_R;
    }

    public static ObservableList<HistoricoDeTicks>[] getGraficoMACDListQtdDigito_R() {
        return graficoMACDListQtdDigito_R;
    }

    public static void setGraficoMACDListQtdDigito_R(ObservableList<HistoricoDeTicks>[] graficoMACDListQtdDigito_R) {
        Operacoes.graficoMACDListQtdDigito_R = graficoMACDListQtdDigito_R;
    }

    public boolean isAppAutorizado() {
        return appAutorizado.get();
    }

    public BooleanProperty appAutorizadoProperty() {
        return appAutorizado;
    }

    public void setAppAutorizado(boolean appAutorizado) {
        this.appAutorizado.set(appAutorizado);
    }

    public Timeline getRoboRelogio() {
        return roboRelogio;
    }

    public void setRoboRelogio(Timeline roboRelogio) {
        this.roboRelogio = roboRelogio;
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

    public static ObjectProperty<Symbol>[] getOperador() {
        return operador;
    }

    public static void setOperador(ObjectProperty<Symbol>[] operador) {
        Operacoes.operador = operador;
    }

    public static BooleanProperty[] getOperadorAtivo() {
        return operadorAtivo;
    }

    public static void setOperadorAtivo(BooleanProperty[] operadorAtivo) {
        Operacoes.operadorAtivo = operadorAtivo;
    }

    public static BooleanProperty[] getOperadorCompraAutorizada() {
        return operadorCompraAutorizada;
    }

    public static void setOperadorCompraAutorizada(BooleanProperty[] operadorCompraAutorizada) {
        Operacoes.operadorCompraAutorizada = operadorCompraAutorizada;
    }

    public static BooleanProperty[] getOperadorNegociando() {
        return operadorNegociando;
    }

    public static void setOperadorNegociando(BooleanProperty[] operadorNegociando) {
        Operacoes.operadorNegociando = operadorNegociando;
    }

    public static BooleanProperty[] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[] tickSubindo) {
        Operacoes.tickSubindo = tickSubindo;
    }

    public static ObjectProperty<Tick>[] getUltimoTick() {
        return ultimoTick;
    }

    public static void setUltimoTick(ObjectProperty<Tick>[] ultimoTick) {
        Operacoes.ultimoTick = ultimoTick;
    }

    public static IntegerProperty[] getMaiorQtdDigito() {
        return maiorQtdDigito;
    }

    public static void setMaiorQtdDigito(IntegerProperty[] maiorQtdDigito) {
        Operacoes.maiorQtdDigito = maiorQtdDigito;
    }

    public static IntegerProperty[] getMenorQtdDigito() {
        return menorQtdDigito;
    }

    public static void setMenorQtdDigito(IntegerProperty[] menorQtdDigito) {
        Operacoes.menorQtdDigito = menorQtdDigito;
    }

    public static StringProperty[] getInformacaoDetalhe01() {
        return informacaoDetalhe01;
    }

    public static void setInformacaoDetalhe01(StringProperty[] informacaoDetalhe01) {
        Operacoes.informacaoDetalhe01 = informacaoDetalhe01;
    }

    public static StringProperty[] getInformacaoValor01() {
        return informacaoValor01;
    }

    public static void setInformacaoValor01(StringProperty[] informacaoValor01) {
        Operacoes.informacaoValor01 = informacaoValor01;
    }

    public static StringProperty[] getInformacaoDetalhe02() {
        return informacaoDetalhe02;
    }

    public static void setInformacaoDetalhe02(StringProperty[] informacaoDetalhe02) {
        Operacoes.informacaoDetalhe02 = informacaoDetalhe02;
    }

    public static StringProperty[] getInformacaoValor02() {
        return informacaoValor02;
    }

    public static void setInformacaoValor02(StringProperty[] informacaoValor02) {
        Operacoes.informacaoValor02 = informacaoValor02;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksObservableList() {
        return historicoDeTicksObservableList;
    }

    public static void setHistoricoDeTicksObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksObservableList) {
        Operacoes.historicoDeTicksObservableList = historicoDeTicksObservableList;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksAnaliseObservableList() {
        return HistoricoDeTicksAnaliseObservableList;
    }

    public static void setHistoricoDeTicksAnaliseObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList) {
        HistoricoDeTicksAnaliseObservableList = historicoDeTicksAnaliseObservableList;
    }

    public static ObservableList<Transaction>[] getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction>[] transactionObservableList) {
        Operacoes.transactionObservableList = transactionObservableList;
    }

    public static ObservableList<Transacoes> getTransacoesObservableList() {
        return transacoesObservableList;
    }

    public static void setTransacoesObservableList(ObservableList<Transacoes> transacoesObservableList) {
        Operacoes.transacoesObservableList = transacoesObservableList;
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

    public TitledPane getTpn_Op01() {
        return tpn_Op01;
    }

    public void setTpn_Op01(TitledPane tpn_Op01) {
        this.tpn_Op01 = tpn_Op01;
    }

    public BarChart<String, Number> getGraficoBarras_Op01() {
        return graficoBarras_Op01;
    }

    public void setGraficoBarras_Op01(BarChart<String, Number> graficoBarras_Op01) {
        this.graficoBarras_Op01 = graficoBarras_Op01;
    }

    public NumberAxis getyAxisBarras_Op01() {
        return yAxisBarras_Op01;
    }

    public void setyAxisBarras_Op01(NumberAxis yAxisBarras_Op01) {
        this.yAxisBarras_Op01 = yAxisBarras_Op01;
    }

    public LineChart getGraficoLinhas_Op01() {
        return graficoLinhas_Op01;
    }

    public void setGraficoLinhas_Op01(LineChart graficoLinhas_Op01) {
        this.graficoLinhas_Op01 = graficoLinhas_Op01;
    }

    public NumberAxis getyAxisLinhas_Op01() {
        return yAxisLinhas_Op01;
    }

    public void setyAxisLinhas_Op01(NumberAxis yAxisLinhas_Op01) {
        this.yAxisLinhas_Op01 = yAxisLinhas_Op01;
    }

    public Label getLblInf01_Op01() {
        return lblInf01_Op01;
    }

    public void setLblInf01_Op01(Label lblInf01_Op01) {
        this.lblInf01_Op01 = lblInf01_Op01;
    }

    public Label getLblVlrInf01_Op01() {
        return lblVlrInf01_Op01;
    }

    public void setLblVlrInf01_Op01(Label lblVlrInf01_Op01) {
        this.lblVlrInf01_Op01 = lblVlrInf01_Op01;
    }

    public Label getLblPorcInf01_Op01() {
        return lblPorcInf01_Op01;
    }

    public void setLblPorcInf01_Op01(Label lblPorcInf01_Op01) {
        this.lblPorcInf01_Op01 = lblPorcInf01_Op01;
    }

    public Label getLblInf02_Op01() {
        return lblInf02_Op01;
    }

    public void setLblInf02_Op01(Label lblInf02_Op01) {
        this.lblInf02_Op01 = lblInf02_Op01;
    }

    public Label getLblVlrInf02_Op01() {
        return lblVlrInf02_Op01;
    }

    public void setLblVlrInf02_Op01(Label lblVlrInf02_Op01) {
        this.lblVlrInf02_Op01 = lblVlrInf02_Op01;
    }

    public Label getLblPorcInf02_Op01() {
        return lblPorcInf02_Op01;
    }

    public void setLblPorcInf02_Op01(Label lblPorcInf02_Op01) {
        this.lblPorcInf02_Op01 = lblPorcInf02_Op01;
    }

    public Label getLblTickUltimo_Op01() {
        return lblTickUltimo_Op01;
    }

    public void setLblTickUltimo_Op01(Label lblTickUltimo_Op01) {
        this.lblTickUltimo_Op01 = lblTickUltimo_Op01;
    }

    public Label getLblLegendaTickUltimo_Op01() {
        return lblLegendaTickUltimo_Op01;
    }

    public void setLblLegendaTickUltimo_Op01(Label lblLegendaTickUltimo_Op01) {
        this.lblLegendaTickUltimo_Op01 = lblLegendaTickUltimo_Op01;
    }

    public Button getBtnContratos_Op01() {
        return btnContratos_Op01;
    }

    public void setBtnContratos_Op01(Button btnContratos_Op01) {
        this.btnContratos_Op01 = btnContratos_Op01;
    }

    public Button getBtnComprar_Op01() {
        return btnComprar_Op01;
    }

    public void setBtnComprar_Op01(Button btnComprar_Op01) {
        this.btnComprar_Op01 = btnComprar_Op01;
    }

    public Button getBtnPausar_Op01() {
        return btnPausar_Op01;
    }

    public void setBtnPausar_Op01(Button btnPausar_Op01) {
        this.btnPausar_Op01 = btnPausar_Op01;
    }

    public Button getBtnStop_Op01() {
        return btnStop_Op01;
    }

    public void setBtnStop_Op01(Button btnStop_Op01) {
        this.btnStop_Op01 = btnStop_Op01;
    }

    public Label getLblInvestido_Op01() {
        return lblInvestido_Op01;
    }

    public void setLblInvestido_Op01(Label lblInvestido_Op01) {
        this.lblInvestido_Op01 = lblInvestido_Op01;
    }

    public Label getLblInvestidoPorc_Op01() {
        return lblInvestidoPorc_Op01;
    }

    public void setLblInvestidoPorc_Op01(Label lblInvestidoPorc_Op01) {
        this.lblInvestidoPorc_Op01 = lblInvestidoPorc_Op01;
    }

    public Label getLblPremiacao_Op01() {
        return lblPremiacao_Op01;
    }

    public void setLblPremiacao_Op01(Label lblPremiacao_Op01) {
        this.lblPremiacao_Op01 = lblPremiacao_Op01;
    }

    public Label getLblPremiacaoPorc_Op01() {
        return lblPremiacaoPorc_Op01;
    }

    public void setLblPremiacaoPorc_Op01(Label lblPremiacaoPorc_Op01) {
        this.lblPremiacaoPorc_Op01 = lblPremiacaoPorc_Op01;
    }

    public Label getLblLucro_Op01() {
        return lblLucro_Op01;
    }

    public void setLblLucro_Op01(Label lblLucro_Op01) {
        this.lblLucro_Op01 = lblLucro_Op01;
    }

    public Label getLblLucroPorc_Op01() {
        return lblLucroPorc_Op01;
    }

    public void setLblLucroPorc_Op01(Label lblLucroPorc_Op01) {
        this.lblLucroPorc_Op01 = lblLucroPorc_Op01;
    }

    public TableView<Transacoes> getTbvTransacoes_Op01() {
        return tbvTransacoes_Op01;
    }

    public void setTbvTransacoes_Op01(TableView<Transacoes> tbvTransacoes_Op01) {
        this.tbvTransacoes_Op01 = tbvTransacoes_Op01;
    }

    public ComboBox<Symbol> getCboSymbol01() {
        return cboSymbol01;
    }

    public void setCboSymbol01(ComboBox<Symbol> cboSymbol01) {
        this.cboSymbol01 = cboSymbol01;
    }

    public CheckBox getChkAtivo_Op01() {
        return chkAtivo_Op01;
    }

    public void setChkAtivo_Op01(CheckBox chkAtivo_Op01) {
        this.chkAtivo_Op01 = chkAtivo_Op01;
    }

    public Label getTpnLblLegendaExecucoes_Op01() {
        return tpnLblLegendaExecucoes_Op01;
    }

    public void setTpnLblLegendaExecucoes_Op01(Label tpnLblLegendaExecucoes_Op01) {
        this.tpnLblLegendaExecucoes_Op01 = tpnLblLegendaExecucoes_Op01;
    }

    public Label getTpnLblExecucoes_Op01() {
        return tpnLblExecucoes_Op01;
    }

    public void setTpnLblExecucoes_Op01(Label tpnLblExecucoes_Op01) {
        this.tpnLblExecucoes_Op01 = tpnLblExecucoes_Op01;
    }

    public Label getTpnLblVitorias_Op01() {
        return tpnLblVitorias_Op01;
    }

    public void setTpnLblVitorias_Op01(Label tpnLblVitorias_Op01) {
        this.tpnLblVitorias_Op01 = tpnLblVitorias_Op01;
    }

    public Label getTpnLblDerrotas_Op01() {
        return tpnLblDerrotas_Op01;
    }

    public void setTpnLblDerrotas_Op01(Label tpnLblDerrotas_Op01) {
        this.tpnLblDerrotas_Op01 = tpnLblDerrotas_Op01;
    }

    public Label getTpnLblLucro_Op01() {
        return tpnLblLucro_Op01;
    }

    public void setTpnLblLucro_Op01(Label tpnLblLucro_Op01) {
        this.tpnLblLucro_Op01 = tpnLblLucro_Op01;
    }

    public TitledPane getTpn_Op02() {
        return tpn_Op02;
    }

    public void setTpn_Op02(TitledPane tpn_Op02) {
        this.tpn_Op02 = tpn_Op02;
    }

    public BarChart<String, Number> getGraficoBarras_Op02() {
        return graficoBarras_Op02;
    }

    public void setGraficoBarras_Op02(BarChart<String, Number> graficoBarras_Op02) {
        this.graficoBarras_Op02 = graficoBarras_Op02;
    }

    public NumberAxis getyAxisBarras_Op02() {
        return yAxisBarras_Op02;
    }

    public void setyAxisBarras_Op02(NumberAxis yAxisBarras_Op02) {
        this.yAxisBarras_Op02 = yAxisBarras_Op02;
    }

    public LineChart getGraficoLinhas_Op02() {
        return graficoLinhas_Op02;
    }

    public void setGraficoLinhas_Op02(LineChart graficoLinhas_Op02) {
        this.graficoLinhas_Op02 = graficoLinhas_Op02;
    }

    public NumberAxis getyAxisLinhas_Op02() {
        return yAxisLinhas_Op02;
    }

    public void setyAxisLinhas_Op02(NumberAxis yAxisLinhas_Op02) {
        this.yAxisLinhas_Op02 = yAxisLinhas_Op02;
    }

    public Label getLblInf01_Op02() {
        return lblInf01_Op02;
    }

    public void setLblInf01_Op02(Label lblInf01_Op02) {
        this.lblInf01_Op02 = lblInf01_Op02;
    }

    public Label getLblVlrInf01_Op02() {
        return lblVlrInf01_Op02;
    }

    public void setLblVlrInf01_Op02(Label lblVlrInf01_Op02) {
        this.lblVlrInf01_Op02 = lblVlrInf01_Op02;
    }

    public Label getLblPorcInf01_Op02() {
        return lblPorcInf01_Op02;
    }

    public void setLblPorcInf01_Op02(Label lblPorcInf01_Op02) {
        this.lblPorcInf01_Op02 = lblPorcInf01_Op02;
    }

    public Label getLblInf02_Op02() {
        return lblInf02_Op02;
    }

    public void setLblInf02_Op02(Label lblInf02_Op02) {
        this.lblInf02_Op02 = lblInf02_Op02;
    }

    public Label getLblVlrInf02_Op02() {
        return lblVlrInf02_Op02;
    }

    public void setLblVlrInf02_Op02(Label lblVlrInf02_Op02) {
        this.lblVlrInf02_Op02 = lblVlrInf02_Op02;
    }

    public Label getLblPorcInf02_Op02() {
        return lblPorcInf02_Op02;
    }

    public void setLblPorcInf02_Op02(Label lblPorcInf02_Op02) {
        this.lblPorcInf02_Op02 = lblPorcInf02_Op02;
    }

    public Label getLblTickUltimo_Op02() {
        return lblTickUltimo_Op02;
    }

    public void setLblTickUltimo_Op02(Label lblTickUltimo_Op02) {
        this.lblTickUltimo_Op02 = lblTickUltimo_Op02;
    }

    public Label getLblLegendaTickUltimo_Op02() {
        return lblLegendaTickUltimo_Op02;
    }

    public void setLblLegendaTickUltimo_Op02(Label lblLegendaTickUltimo_Op02) {
        this.lblLegendaTickUltimo_Op02 = lblLegendaTickUltimo_Op02;
    }

    public Button getBtnContratos_Op02() {
        return btnContratos_Op02;
    }

    public void setBtnContratos_Op02(Button btnContratos_Op02) {
        this.btnContratos_Op02 = btnContratos_Op02;
    }

    public Button getBtnComprar_Op02() {
        return btnComprar_Op02;
    }

    public void setBtnComprar_Op02(Button btnComprar_Op02) {
        this.btnComprar_Op02 = btnComprar_Op02;
    }

    public Button getBtnPausar_Op02() {
        return btnPausar_Op02;
    }

    public void setBtnPausar_Op02(Button btnPausar_Op02) {
        this.btnPausar_Op02 = btnPausar_Op02;
    }

    public Button getBtnStop_Op02() {
        return btnStop_Op02;
    }

    public void setBtnStop_Op02(Button btnStop_Op02) {
        this.btnStop_Op02 = btnStop_Op02;
    }

    public Label getLblInvestido_Op02() {
        return lblInvestido_Op02;
    }

    public void setLblInvestido_Op02(Label lblInvestido_Op02) {
        this.lblInvestido_Op02 = lblInvestido_Op02;
    }

    public Label getLblInvestidoPorc_Op02() {
        return lblInvestidoPorc_Op02;
    }

    public void setLblInvestidoPorc_Op02(Label lblInvestidoPorc_Op02) {
        this.lblInvestidoPorc_Op02 = lblInvestidoPorc_Op02;
    }

    public Label getLblPremiacao_Op02() {
        return lblPremiacao_Op02;
    }

    public void setLblPremiacao_Op02(Label lblPremiacao_Op02) {
        this.lblPremiacao_Op02 = lblPremiacao_Op02;
    }

    public Label getLblPremiacaoPorc_Op02() {
        return lblPremiacaoPorc_Op02;
    }

    public void setLblPremiacaoPorc_Op02(Label lblPremiacaoPorc_Op02) {
        this.lblPremiacaoPorc_Op02 = lblPremiacaoPorc_Op02;
    }

    public Label getLblLucro_Op02() {
        return lblLucro_Op02;
    }

    public void setLblLucro_Op02(Label lblLucro_Op02) {
        this.lblLucro_Op02 = lblLucro_Op02;
    }

    public Label getLblLucroPorc_Op02() {
        return lblLucroPorc_Op02;
    }

    public void setLblLucroPorc_Op02(Label lblLucroPorc_Op02) {
        this.lblLucroPorc_Op02 = lblLucroPorc_Op02;
    }

    public TableView<Transacoes> getTbvTransacoes_Op02() {
        return tbvTransacoes_Op02;
    }

    public void setTbvTransacoes_Op02(TableView<Transacoes> tbvTransacoes_Op02) {
        this.tbvTransacoes_Op02 = tbvTransacoes_Op02;
    }

    public ComboBox<Symbol> getCboSymbol02() {
        return cboSymbol02;
    }

    public void setCboSymbol02(ComboBox<Symbol> cboSymbol02) {
        this.cboSymbol02 = cboSymbol02;
    }

    public CheckBox getChkAtivo_Op02() {
        return chkAtivo_Op02;
    }

    public void setChkAtivo_Op02(CheckBox chkAtivo_Op02) {
        this.chkAtivo_Op02 = chkAtivo_Op02;
    }

    public Label getTpnLblLegendaExecucoes_Op02() {
        return tpnLblLegendaExecucoes_Op02;
    }

    public void setTpnLblLegendaExecucoes_Op02(Label tpnLblLegendaExecucoes_Op02) {
        this.tpnLblLegendaExecucoes_Op02 = tpnLblLegendaExecucoes_Op02;
    }

    public Label getTpnLblExecucoes_Op02() {
        return tpnLblExecucoes_Op02;
    }

    public void setTpnLblExecucoes_Op02(Label tpnLblExecucoes_Op02) {
        this.tpnLblExecucoes_Op02 = tpnLblExecucoes_Op02;
    }

    public Label getTpnLblVitorias_Op02() {
        return tpnLblVitorias_Op02;
    }

    public void setTpnLblVitorias_Op02(Label tpnLblVitorias_Op02) {
        this.tpnLblVitorias_Op02 = tpnLblVitorias_Op02;
    }

    public Label getTpnLblDerrotas_Op02() {
        return tpnLblDerrotas_Op02;
    }

    public void setTpnLblDerrotas_Op02(Label tpnLblDerrotas_Op02) {
        this.tpnLblDerrotas_Op02 = tpnLblDerrotas_Op02;
    }

    public Label getTpnLblLucro_Op02() {
        return tpnLblLucro_Op02;
    }

    public void setTpnLblLucro_Op02(Label tpnLblLucro_Op02) {
        this.tpnLblLucro_Op02 = tpnLblLucro_Op02;
    }

    public TitledPane getTpn_Op03() {
        return tpn_Op03;
    }

    public void setTpn_Op03(TitledPane tpn_Op03) {
        this.tpn_Op03 = tpn_Op03;
    }

    public BarChart<String, Number> getGraficoBarras_Op03() {
        return graficoBarras_Op03;
    }

    public void setGraficoBarras_Op03(BarChart<String, Number> graficoBarras_Op03) {
        this.graficoBarras_Op03 = graficoBarras_Op03;
    }

    public NumberAxis getyAxisBarras_Op03() {
        return yAxisBarras_Op03;
    }

    public void setyAxisBarras_Op03(NumberAxis yAxisBarras_Op03) {
        this.yAxisBarras_Op03 = yAxisBarras_Op03;
    }

    public LineChart getGraficoLinhas_Op03() {
        return graficoLinhas_Op03;
    }

    public void setGraficoLinhas_Op03(LineChart graficoLinhas_Op03) {
        this.graficoLinhas_Op03 = graficoLinhas_Op03;
    }

    public NumberAxis getyAxisLinhas_Op03() {
        return yAxisLinhas_Op03;
    }

    public void setyAxisLinhas_Op03(NumberAxis yAxisLinhas_Op03) {
        this.yAxisLinhas_Op03 = yAxisLinhas_Op03;
    }

    public Label getLblInf01_Op03() {
        return lblInf01_Op03;
    }

    public void setLblInf01_Op03(Label lblInf01_Op03) {
        this.lblInf01_Op03 = lblInf01_Op03;
    }

    public Label getLblVlrInf01_Op03() {
        return lblVlrInf01_Op03;
    }

    public void setLblVlrInf01_Op03(Label lblVlrInf01_Op03) {
        this.lblVlrInf01_Op03 = lblVlrInf01_Op03;
    }

    public Label getLblPorcInf01_Op03() {
        return lblPorcInf01_Op03;
    }

    public void setLblPorcInf01_Op03(Label lblPorcInf01_Op03) {
        this.lblPorcInf01_Op03 = lblPorcInf01_Op03;
    }

    public Label getLblInf02_Op03() {
        return lblInf02_Op03;
    }

    public void setLblInf02_Op03(Label lblInf02_Op03) {
        this.lblInf02_Op03 = lblInf02_Op03;
    }

    public Label getLblVlrInf02_Op03() {
        return lblVlrInf02_Op03;
    }

    public void setLblVlrInf02_Op03(Label lblVlrInf02_Op03) {
        this.lblVlrInf02_Op03 = lblVlrInf02_Op03;
    }

    public Label getLblPorcInf02_Op03() {
        return lblPorcInf02_Op03;
    }

    public void setLblPorcInf02_Op03(Label lblPorcInf02_Op03) {
        this.lblPorcInf02_Op03 = lblPorcInf02_Op03;
    }

    public Label getLblTickUltimo_Op03() {
        return lblTickUltimo_Op03;
    }

    public void setLblTickUltimo_Op03(Label lblTickUltimo_Op03) {
        this.lblTickUltimo_Op03 = lblTickUltimo_Op03;
    }

    public Label getLblLegendaTickUltimo_Op03() {
        return lblLegendaTickUltimo_Op03;
    }

    public void setLblLegendaTickUltimo_Op03(Label lblLegendaTickUltimo_Op03) {
        this.lblLegendaTickUltimo_Op03 = lblLegendaTickUltimo_Op03;
    }

    public Button getBtnContratos_Op03() {
        return btnContratos_Op03;
    }

    public void setBtnContratos_Op03(Button btnContratos_Op03) {
        this.btnContratos_Op03 = btnContratos_Op03;
    }

    public Button getBtnComprar_Op03() {
        return btnComprar_Op03;
    }

    public void setBtnComprar_Op03(Button btnComprar_Op03) {
        this.btnComprar_Op03 = btnComprar_Op03;
    }

    public Button getBtnPausar_Op03() {
        return btnPausar_Op03;
    }

    public void setBtnPausar_Op03(Button btnPausar_Op03) {
        this.btnPausar_Op03 = btnPausar_Op03;
    }

    public Button getBtnStop_Op03() {
        return btnStop_Op03;
    }

    public void setBtnStop_Op03(Button btnStop_Op03) {
        this.btnStop_Op03 = btnStop_Op03;
    }

    public Label getLblInvestido_Op03() {
        return lblInvestido_Op03;
    }

    public void setLblInvestido_Op03(Label lblInvestido_Op03) {
        this.lblInvestido_Op03 = lblInvestido_Op03;
    }

    public Label getLblInvestidoPorc_Op03() {
        return lblInvestidoPorc_Op03;
    }

    public void setLblInvestidoPorc_Op03(Label lblInvestidoPorc_Op03) {
        this.lblInvestidoPorc_Op03 = lblInvestidoPorc_Op03;
    }

    public Label getLblPremiacao_Op03() {
        return lblPremiacao_Op03;
    }

    public void setLblPremiacao_Op03(Label lblPremiacao_Op03) {
        this.lblPremiacao_Op03 = lblPremiacao_Op03;
    }

    public Label getLblPremiacaoPorc_Op03() {
        return lblPremiacaoPorc_Op03;
    }

    public void setLblPremiacaoPorc_Op03(Label lblPremiacaoPorc_Op03) {
        this.lblPremiacaoPorc_Op03 = lblPremiacaoPorc_Op03;
    }

    public Label getLblLucro_Op03() {
        return lblLucro_Op03;
    }

    public void setLblLucro_Op03(Label lblLucro_Op03) {
        this.lblLucro_Op03 = lblLucro_Op03;
    }

    public Label getLblLucroPorc_Op03() {
        return lblLucroPorc_Op03;
    }

    public void setLblLucroPorc_Op03(Label lblLucroPorc_Op03) {
        this.lblLucroPorc_Op03 = lblLucroPorc_Op03;
    }

    public TableView<Transacoes> getTbvTransacoes_Op03() {
        return tbvTransacoes_Op03;
    }

    public void setTbvTransacoes_Op03(TableView<Transacoes> tbvTransacoes_Op03) {
        this.tbvTransacoes_Op03 = tbvTransacoes_Op03;
    }

    public ComboBox<Symbol> getCboSymbol03() {
        return cboSymbol03;
    }

    public void setCboSymbol03(ComboBox<Symbol> cboSymbol03) {
        this.cboSymbol03 = cboSymbol03;
    }

    public CheckBox getChkAtivo_Op03() {
        return chkAtivo_Op03;
    }

    public void setChkAtivo_Op03(CheckBox chkAtivo_Op03) {
        this.chkAtivo_Op03 = chkAtivo_Op03;
    }

    public Label getTpnLblLegendaExecucoes_Op03() {
        return tpnLblLegendaExecucoes_Op03;
    }

    public void setTpnLblLegendaExecucoes_Op03(Label tpnLblLegendaExecucoes_Op03) {
        this.tpnLblLegendaExecucoes_Op03 = tpnLblLegendaExecucoes_Op03;
    }

    public Label getTpnLblExecucoes_Op03() {
        return tpnLblExecucoes_Op03;
    }

    public void setTpnLblExecucoes_Op03(Label tpnLblExecucoes_Op03) {
        this.tpnLblExecucoes_Op03 = tpnLblExecucoes_Op03;
    }

    public Label getTpnLblVitorias_Op03() {
        return tpnLblVitorias_Op03;
    }

    public void setTpnLblVitorias_Op03(Label tpnLblVitorias_Op03) {
        this.tpnLblVitorias_Op03 = tpnLblVitorias_Op03;
    }

    public Label getTpnLblDerrotas_Op03() {
        return tpnLblDerrotas_Op03;
    }

    public void setTpnLblDerrotas_Op03(Label tpnLblDerrotas_Op03) {
        this.tpnLblDerrotas_Op03 = tpnLblDerrotas_Op03;
    }

    public Label getTpnLblLucro_Op03() {
        return tpnLblLucro_Op03;
    }

    public void setTpnLblLucro_Op03(Label tpnLblLucro_Op03) {
        this.tpnLblLucro_Op03 = tpnLblLucro_Op03;
    }

    public TitledPane getTpn_Op04() {
        return tpn_Op04;
    }

    public void setTpn_Op04(TitledPane tpn_Op04) {
        this.tpn_Op04 = tpn_Op04;
    }

    public BarChart<String, Number> getGraficoBarras_Op04() {
        return graficoBarras_Op04;
    }

    public void setGraficoBarras_Op04(BarChart<String, Number> graficoBarras_Op04) {
        this.graficoBarras_Op04 = graficoBarras_Op04;
    }

    public NumberAxis getyAxisBarras_Op04() {
        return yAxisBarras_Op04;
    }

    public void setyAxisBarras_Op04(NumberAxis yAxisBarras_Op04) {
        this.yAxisBarras_Op04 = yAxisBarras_Op04;
    }

    public LineChart getGraficoLinhas_Op04() {
        return graficoLinhas_Op04;
    }

    public void setGraficoLinhas_Op04(LineChart graficoLinhas_Op04) {
        this.graficoLinhas_Op04 = graficoLinhas_Op04;
    }

    public NumberAxis getyAxisLinhas_Op04() {
        return yAxisLinhas_Op04;
    }

    public void setyAxisLinhas_Op04(NumberAxis yAxisLinhas_Op04) {
        this.yAxisLinhas_Op04 = yAxisLinhas_Op04;
    }

    public Label getLblInf01_Op04() {
        return lblInf01_Op04;
    }

    public void setLblInf01_Op04(Label lblInf01_Op04) {
        this.lblInf01_Op04 = lblInf01_Op04;
    }

    public Label getLblVlrInf01_Op04() {
        return lblVlrInf01_Op04;
    }

    public void setLblVlrInf01_Op04(Label lblVlrInf01_Op04) {
        this.lblVlrInf01_Op04 = lblVlrInf01_Op04;
    }

    public Label getLblPorcInf01_Op04() {
        return lblPorcInf01_Op04;
    }

    public void setLblPorcInf01_Op04(Label lblPorcInf01_Op04) {
        this.lblPorcInf01_Op04 = lblPorcInf01_Op04;
    }

    public Label getLblInf02_Op04() {
        return lblInf02_Op04;
    }

    public void setLblInf02_Op04(Label lblInf02_Op04) {
        this.lblInf02_Op04 = lblInf02_Op04;
    }

    public Label getLblVlrInf02_Op04() {
        return lblVlrInf02_Op04;
    }

    public void setLblVlrInf02_Op04(Label lblVlrInf02_Op04) {
        this.lblVlrInf02_Op04 = lblVlrInf02_Op04;
    }

    public Label getLblPorcInf02_Op04() {
        return lblPorcInf02_Op04;
    }

    public void setLblPorcInf02_Op04(Label lblPorcInf02_Op04) {
        this.lblPorcInf02_Op04 = lblPorcInf02_Op04;
    }

    public Label getLblTickUltimo_Op04() {
        return lblTickUltimo_Op04;
    }

    public void setLblTickUltimo_Op04(Label lblTickUltimo_Op04) {
        this.lblTickUltimo_Op04 = lblTickUltimo_Op04;
    }

    public Label getLblLegendaTickUltimo_Op04() {
        return lblLegendaTickUltimo_Op04;
    }

    public void setLblLegendaTickUltimo_Op04(Label lblLegendaTickUltimo_Op04) {
        this.lblLegendaTickUltimo_Op04 = lblLegendaTickUltimo_Op04;
    }

    public Button getBtnContratos_Op04() {
        return btnContratos_Op04;
    }

    public void setBtnContratos_Op04(Button btnContratos_Op04) {
        this.btnContratos_Op04 = btnContratos_Op04;
    }

    public Button getBtnComprar_Op04() {
        return btnComprar_Op04;
    }

    public void setBtnComprar_Op04(Button btnComprar_Op04) {
        this.btnComprar_Op04 = btnComprar_Op04;
    }

    public Button getBtnPausar_Op04() {
        return btnPausar_Op04;
    }

    public void setBtnPausar_Op04(Button btnPausar_Op04) {
        this.btnPausar_Op04 = btnPausar_Op04;
    }

    public Button getBtnStop_Op04() {
        return btnStop_Op04;
    }

    public void setBtnStop_Op04(Button btnStop_Op04) {
        this.btnStop_Op04 = btnStop_Op04;
    }

    public Label getLblInvestido_Op04() {
        return lblInvestido_Op04;
    }

    public void setLblInvestido_Op04(Label lblInvestido_Op04) {
        this.lblInvestido_Op04 = lblInvestido_Op04;
    }

    public Label getLblInvestidoPorc_Op04() {
        return lblInvestidoPorc_Op04;
    }

    public void setLblInvestidoPorc_Op04(Label lblInvestidoPorc_Op04) {
        this.lblInvestidoPorc_Op04 = lblInvestidoPorc_Op04;
    }

    public Label getLblPremiacao_Op04() {
        return lblPremiacao_Op04;
    }

    public void setLblPremiacao_Op04(Label lblPremiacao_Op04) {
        this.lblPremiacao_Op04 = lblPremiacao_Op04;
    }

    public Label getLblPremiacaoPorc_Op04() {
        return lblPremiacaoPorc_Op04;
    }

    public void setLblPremiacaoPorc_Op04(Label lblPremiacaoPorc_Op04) {
        this.lblPremiacaoPorc_Op04 = lblPremiacaoPorc_Op04;
    }

    public Label getLblLucro_Op04() {
        return lblLucro_Op04;
    }

    public void setLblLucro_Op04(Label lblLucro_Op04) {
        this.lblLucro_Op04 = lblLucro_Op04;
    }

    public Label getLblLucroPorc_Op04() {
        return lblLucroPorc_Op04;
    }

    public void setLblLucroPorc_Op04(Label lblLucroPorc_Op04) {
        this.lblLucroPorc_Op04 = lblLucroPorc_Op04;
    }

    public TableView<Transacoes> getTbvTransacoes_Op04() {
        return tbvTransacoes_Op04;
    }

    public void setTbvTransacoes_Op04(TableView<Transacoes> tbvTransacoes_Op04) {
        this.tbvTransacoes_Op04 = tbvTransacoes_Op04;
    }

    public ComboBox<Symbol> getCboSymbol04() {
        return cboSymbol04;
    }

    public void setCboSymbol04(ComboBox<Symbol> cboSymbol04) {
        this.cboSymbol04 = cboSymbol04;
    }

    public CheckBox getChkAtivo_Op04() {
        return chkAtivo_Op04;
    }

    public void setChkAtivo_Op04(CheckBox chkAtivo_Op04) {
        this.chkAtivo_Op04 = chkAtivo_Op04;
    }

    public Label getTpnLblLegendaExecucoes_Op04() {
        return tpnLblLegendaExecucoes_Op04;
    }

    public void setTpnLblLegendaExecucoes_Op04(Label tpnLblLegendaExecucoes_Op04) {
        this.tpnLblLegendaExecucoes_Op04 = tpnLblLegendaExecucoes_Op04;
    }

    public Label getTpnLblExecucoes_Op04() {
        return tpnLblExecucoes_Op04;
    }

    public void setTpnLblExecucoes_Op04(Label tpnLblExecucoes_Op04) {
        this.tpnLblExecucoes_Op04 = tpnLblExecucoes_Op04;
    }

    public Label getTpnLblVitorias_Op04() {
        return tpnLblVitorias_Op04;
    }

    public void setTpnLblVitorias_Op04(Label tpnLblVitorias_Op04) {
        this.tpnLblVitorias_Op04 = tpnLblVitorias_Op04;
    }

    public Label getTpnLblDerrotas_Op04() {
        return tpnLblDerrotas_Op04;
    }

    public void setTpnLblDerrotas_Op04(Label tpnLblDerrotas_Op04) {
        this.tpnLblDerrotas_Op04 = tpnLblDerrotas_Op04;
    }

    public Label getTpnLblLucro_Op04() {
        return tpnLblLucro_Op04;
    }

    public void setTpnLblLucro_Op04(Label tpnLblLucro_Op04) {
        this.tpnLblLucro_Op04 = tpnLblLucro_Op04;
    }

    public TitledPane getTpn_Op05() {
        return tpn_Op05;
    }

    public void setTpn_Op05(TitledPane tpn_Op05) {
        this.tpn_Op05 = tpn_Op05;
    }

    public BarChart<String, Number> getGraficoBarras_Op05() {
        return graficoBarras_Op05;
    }

    public void setGraficoBarras_Op05(BarChart<String, Number> graficoBarras_Op05) {
        this.graficoBarras_Op05 = graficoBarras_Op05;
    }

    public NumberAxis getyAxisBarras_Op05() {
        return yAxisBarras_Op05;
    }

    public void setyAxisBarras_Op05(NumberAxis yAxisBarras_Op05) {
        this.yAxisBarras_Op05 = yAxisBarras_Op05;
    }

    public LineChart getGraficoLinhas_Op05() {
        return graficoLinhas_Op05;
    }

    public void setGraficoLinhas_Op05(LineChart graficoLinhas_Op05) {
        this.graficoLinhas_Op05 = graficoLinhas_Op05;
    }

    public NumberAxis getyAxisLinhas_Op05() {
        return yAxisLinhas_Op05;
    }

    public void setyAxisLinhas_Op05(NumberAxis yAxisLinhas_Op05) {
        this.yAxisLinhas_Op05 = yAxisLinhas_Op05;
    }

    public Label getLblInf01_Op05() {
        return lblInf01_Op05;
    }

    public void setLblInf01_Op05(Label lblInf01_Op05) {
        this.lblInf01_Op05 = lblInf01_Op05;
    }

    public Label getLblVlrInf01_Op05() {
        return lblVlrInf01_Op05;
    }

    public void setLblVlrInf01_Op05(Label lblVlrInf01_Op05) {
        this.lblVlrInf01_Op05 = lblVlrInf01_Op05;
    }

    public Label getLblPorcInf01_Op05() {
        return lblPorcInf01_Op05;
    }

    public void setLblPorcInf01_Op05(Label lblPorcInf01_Op05) {
        this.lblPorcInf01_Op05 = lblPorcInf01_Op05;
    }

    public Label getLblInf02_Op05() {
        return lblInf02_Op05;
    }

    public void setLblInf02_Op05(Label lblInf02_Op05) {
        this.lblInf02_Op05 = lblInf02_Op05;
    }

    public Label getLblVlrInf02_Op05() {
        return lblVlrInf02_Op05;
    }

    public void setLblVlrInf02_Op05(Label lblVlrInf02_Op05) {
        this.lblVlrInf02_Op05 = lblVlrInf02_Op05;
    }

    public Label getLblPorcInf02_Op05() {
        return lblPorcInf02_Op05;
    }

    public void setLblPorcInf02_Op05(Label lblPorcInf02_Op05) {
        this.lblPorcInf02_Op05 = lblPorcInf02_Op05;
    }

    public Label getLblTickUltimo_Op05() {
        return lblTickUltimo_Op05;
    }

    public void setLblTickUltimo_Op05(Label lblTickUltimo_Op05) {
        this.lblTickUltimo_Op05 = lblTickUltimo_Op05;
    }

    public Label getLblLegendaTickUltimo_Op05() {
        return lblLegendaTickUltimo_Op05;
    }

    public void setLblLegendaTickUltimo_Op05(Label lblLegendaTickUltimo_Op05) {
        this.lblLegendaTickUltimo_Op05 = lblLegendaTickUltimo_Op05;
    }

    public Button getBtnContratos_Op05() {
        return btnContratos_Op05;
    }

    public void setBtnContratos_Op05(Button btnContratos_Op05) {
        this.btnContratos_Op05 = btnContratos_Op05;
    }

    public Button getBtnComprar_Op05() {
        return btnComprar_Op05;
    }

    public void setBtnComprar_Op05(Button btnComprar_Op05) {
        this.btnComprar_Op05 = btnComprar_Op05;
    }

    public Button getBtnPausar_Op05() {
        return btnPausar_Op05;
    }

    public void setBtnPausar_Op05(Button btnPausar_Op05) {
        this.btnPausar_Op05 = btnPausar_Op05;
    }

    public Button getBtnStop_Op05() {
        return btnStop_Op05;
    }

    public void setBtnStop_Op05(Button btnStop_Op05) {
        this.btnStop_Op05 = btnStop_Op05;
    }

    public Label getLblInvestido_Op05() {
        return lblInvestido_Op05;
    }

    public void setLblInvestido_Op05(Label lblInvestido_Op05) {
        this.lblInvestido_Op05 = lblInvestido_Op05;
    }

    public Label getLblInvestidoPorc_Op05() {
        return lblInvestidoPorc_Op05;
    }

    public void setLblInvestidoPorc_Op05(Label lblInvestidoPorc_Op05) {
        this.lblInvestidoPorc_Op05 = lblInvestidoPorc_Op05;
    }

    public Label getLblPremiacao_Op05() {
        return lblPremiacao_Op05;
    }

    public void setLblPremiacao_Op05(Label lblPremiacao_Op05) {
        this.lblPremiacao_Op05 = lblPremiacao_Op05;
    }

    public Label getLblPremiacaoPorc_Op05() {
        return lblPremiacaoPorc_Op05;
    }

    public void setLblPremiacaoPorc_Op05(Label lblPremiacaoPorc_Op05) {
        this.lblPremiacaoPorc_Op05 = lblPremiacaoPorc_Op05;
    }

    public Label getLblLucro_Op05() {
        return lblLucro_Op05;
    }

    public void setLblLucro_Op05(Label lblLucro_Op05) {
        this.lblLucro_Op05 = lblLucro_Op05;
    }

    public Label getLblLucroPorc_Op05() {
        return lblLucroPorc_Op05;
    }

    public void setLblLucroPorc_Op05(Label lblLucroPorc_Op05) {
        this.lblLucroPorc_Op05 = lblLucroPorc_Op05;
    }

    public TableView<Transacoes> getTbvTransacoes_Op05() {
        return tbvTransacoes_Op05;
    }

    public void setTbvTransacoes_Op05(TableView<Transacoes> tbvTransacoes_Op05) {
        this.tbvTransacoes_Op05 = tbvTransacoes_Op05;
    }

    public ComboBox<Symbol> getCboSymbol05() {
        return cboSymbol05;
    }

    public void setCboSymbol05(ComboBox<Symbol> cboSymbol05) {
        this.cboSymbol05 = cboSymbol05;
    }

    public CheckBox getChkAtivo_Op05() {
        return chkAtivo_Op05;
    }

    public void setChkAtivo_Op05(CheckBox chkAtivo_Op05) {
        this.chkAtivo_Op05 = chkAtivo_Op05;
    }

    public Label getTpnLblLegendaExecucoes_Op05() {
        return tpnLblLegendaExecucoes_Op05;
    }

    public void setTpnLblLegendaExecucoes_Op05(Label tpnLblLegendaExecucoes_Op05) {
        this.tpnLblLegendaExecucoes_Op05 = tpnLblLegendaExecucoes_Op05;
    }

    public Label getTpnLblExecucoes_Op05() {
        return tpnLblExecucoes_Op05;
    }

    public void setTpnLblExecucoes_Op05(Label tpnLblExecucoes_Op05) {
        this.tpnLblExecucoes_Op05 = tpnLblExecucoes_Op05;
    }

    public Label getTpnLblVitorias_Op05() {
        return tpnLblVitorias_Op05;
    }

    public void setTpnLblVitorias_Op05(Label tpnLblVitorias_Op05) {
        this.tpnLblVitorias_Op05 = tpnLblVitorias_Op05;
    }

    public Label getTpnLblDerrotas_Op05() {
        return tpnLblDerrotas_Op05;
    }

    public void setTpnLblDerrotas_Op05(Label tpnLblDerrotas_Op05) {
        this.tpnLblDerrotas_Op05 = tpnLblDerrotas_Op05;
    }

    public Label getTpnLblLucro_Op05() {
        return tpnLblLucro_Op05;
    }

    public void setTpnLblLucro_Op05(Label tpnLblLucro_Op05) {
        this.tpnLblLucro_Op05 = tpnLblLucro_Op05;
    }
}
