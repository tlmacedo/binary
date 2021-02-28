package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.dao.ContaTokenDAO;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.*;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_Mascara;
import br.com.tlmacedo.binary.services.Util_Json;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import static br.com.tlmacedo.binary.interfaces.Constants.*;
import static br.com.tlmacedo.binary.model.enums.TICK_STYLE.CANDLES;

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
    static final ObservableList<ContaToken> CONTA_TOKEN_OBSERVABLE_LIST
            = FXCollections.observableArrayList(
            getContaTokenDAO().getAll(ContaToken.class, "tokenAtivo=1", "cReal, moeda, descricao"));
    static ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>();


    /**
     * Conexão e operação com WebService
     */
    static BooleanProperty wsConectado = new SimpleBooleanProperty(false);
    static final ObjectProperty<WSClient> WS_CLIENT_OBJECT_PROPERTY = new SimpleObjectProperty<>(new WSClient());
    static final TICK_STYLE TICK_STYLE = CANDLES;

    /**
     * Robos
     */
    static ObjectProperty<ROBOS> ROBO_Selecionado = new SimpleObjectProperty<>();
    static final ObjectProperty<Robo> ROBO_ATIVO = new SimpleObjectProperty<>();


    /**
     * Variaveis de controle do sistema
     */
    static BooleanProperty appAutorizado = new SimpleBooleanProperty(false);
    static StringProperty parametrosUtilizadosRobo = new SimpleStringProperty("");
    static Timeline roboRelogio;
    static LongProperty roboHoraInicial = new SimpleLongProperty();
    static LongProperty roboCronometro = new SimpleLongProperty();
    static BooleanProperty roboCronometroAtivado = new SimpleBooleanProperty(false);
    static ObjectProperty<BigDecimal> saldoInicial = new SimpleObjectProperty<>(BigDecimal.ZERO);

    /**
     * Variaveis de informações para operadores
     */
    //** Variaveis **
    static ObjectProperty<Tick>[][] ultimoTick = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static ObjectProperty<Ohlc>[][] ultimoOhlc = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static StringProperty[] ultimoTickStr = new StringProperty[getSymbolObservableList().size()];
    static StringProperty[] ultimoOhlcStr = new StringProperty[getSymbolObservableList().size()];
    static BooleanProperty[] tickSubindo = new BooleanProperty[getSymbolObservableList().size()];

    static IntegerProperty[] timeCandleStart = new IntegerProperty[TICK_TIME.values().length];
    static IntegerProperty[] timeCandleToClose = new IntegerProperty[TICK_TIME.values().length];

    static IntegerProperty[][] qtdCallOrPut = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdCall = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdPut = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];

    //** Listas **
    static ObservableList<HistoricoDeTicks>[][] historicoDeTicksObservableList = new ObservableList[TICK_TIME.values().length][getSymbolObservableList().size()];
    static ObservableList<HistoricoDeOhlc>[][] historicoDeOhlcObservableList = new ObservableList[TICK_TIME.values().length][getSymbolObservableList().size()];
    static ObservableList<Transaction>[][] transactionObservableList = new ObservableList[TICK_TIME.values().length][getSymbolObservableList().size()];
//    static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();


    //** Operações com Robos **
    private static BooleanProperty[] timeAtivo = new BooleanProperty[TICK_TIME.values().length];
    static BooleanProperty btnContratoDisabled = new SimpleBooleanProperty(true);
    static BooleanProperty btnIniciardisabled = new SimpleBooleanProperty(true);
    static BooleanProperty btnPausarDisabled = new SimpleBooleanProperty(true);
    static BooleanProperty btnStopDisabled = new SimpleBooleanProperty(true);
    static ObjectProperty<BigDecimal>[] vlrStkPadrao = new ObjectProperty[TICK_TIME.values().length];
    static ObjectProperty<BigDecimal>[][] vlrStkContrato = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static IntegerProperty[] qtdCandlesEntrada = new IntegerProperty[TICK_TIME.values().length];

    static PriceProposal[][] priceProposal = new PriceProposal[TICK_TIME.values().length][getSymbolObservableList().size()];


    public static final Integer TIME_1M = 0;
    public static final Integer TIME_2M = 1;
    public static final Integer TIME_3M = 2;
    public static final Integer TIME_5M = 3;
    public static final Integer TIME_10M = 4;
    public static final Integer TIME_15M = 5;

    public static final Integer SYMBOL_01 = 0;
    public static final Integer SYMBOL_02 = 1;
    public static final Integer SYMBOL_03 = 2;
    public static final Integer SYMBOL_04 = 3;
    public static final Integer SYMBOL_05 = 4;
    public static final Integer SYMBOL_06 = 5;
    public static final Integer SYMBOL_07 = 6;
    public static final Integer SYMBOL_08 = 7;
    public static final Integer SYMBOL_09 = 8;
    public static final Integer SYMBOL_10 = 9;
    public static final Integer SYMBOL_11 = 10;
    public static final Integer SYMBOL_12 = 11;
//    public static final String[] VOL_NAME = symbolObservableList.stream().map(Symbol::getName).collect(Collectors.toList()).toArray(String[]::new);


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

    public AnchorPane pnlViewBinary;

    // Detalhes e informações da conta
    public TitledPane tpn_Detalhes;
    public ComboBox<ContaToken> cboTpnDetalhesContaBinary;
    public Label lblTpnDetalhesQtdStakes;
    public Label lblTpnDetalhesQtdWins;
    public Label lblTpnDetalhesQtdLoss;
    public Label lblTpnDetalhesProfitVlr;
    public Label lblTpnDetalhesProfitPorc;
    public Label lblDetalhesProprietarioConta;
    public Label lblDetalhesContaId;
    public HBox hboxDetalhesSaldoConta;
    public Label lblDetalhesSaldoContaVlr;
    public Label lblDetalhesSaldoContaCifrao;
    public Label lblDetalhesSaldoInicial;
    public Label lblDetalhesTotalIn;
    public Label lblDetalhesTotalOut;
    public Label lblDetalhesSaldoFinal;

    // Negociação
    public TitledPane tpn_Negociacao;
    public ComboBox<ROBOS> cboNegociacaoRobos;
    public Label lblNegociacaoParametros;
    public ComboBox<Integer> cboTpnNegociacaoQtdCandlesAnalise;
    public Label lblTpnNegociacaoDtHoraInicial;
    public Label lblTpnNegociacaoDtHoraAtual;
    public Label lblTpnNegociacaoTempoUso;
    public JFXButton btnTpnNegociacao_Contratos;
    public JFXButton btnTpnNegociacao_Iniciar;
    public JFXButton btnTpnNegociacao_Pausar;
    public JFXButton btnTpnNegociacao_Stop;


    // LastTicks
    public TitledPane tpn_LastTicks;
    public Label lblSymbol_01;
    public Label lblLastTickSymbol_01;
    public Label lblSymbol_02;
    public Label lblLastTickSymbol_02;
    public Label lblSymbol_03;
    public Label lblLastTickSymbol_03;
    public Label lblSymbol_04;
    public Label lblLastTickSymbol_04;
    public Label lblSymbol_05;
    public Label lblLastTickSymbol_05;
    public Label lblSymbol_06;
    public Label lblLastTickSymbol_06;
    public Label lblSymbol_07;
    public Label lblLastTickSymbol_07;
    public Label lblSymbol_08;
    public Label lblLastTickSymbol_08;
    public Label lblSymbol_09;
    public Label lblLastTickSymbol_09;
    public Label lblSymbol_10;
    public Label lblLastTickSymbol_10;
    public Label lblSymbol_11;
    public Label lblLastTickSymbol_11;
    public Label lblSymbol_12;
    public Label lblLastTickSymbol_12;

    // Time_01 *-*-*
    public TitledPane tpn_T01;
    public JFXCheckBox chkTpn01_TimeAtivo;
    public Label lblTpnT01_CandleTimeStart;
    public Label lblTpnT01_TimeEnd;
    public Label lblTpnT01_QtdStakes;
    public Label lblTpnT01_QtdWins;
    public Label lblTpnT01_QtdLoss;
    public Label lblTpnT01_VlrIn;
    public Label lblTpnT01_VlrOut;
    public Label lblTpnT01_VlrDiff;
    // Time_01 *-*-* Symbol_01
    public Label lblSymbol_T01_Op01;
    public Label lblQtdCall_T01_Op01;
    public Label lblQtdPut_T01_Op01;
    public Label lblQtdCallOrPut_T01_Op01;
    public ImageView imgCallOrPut_T01_Op01;
    public Label lblQtdStakes_T01_Op01;
    public Label lblQtdWins_T01_Op01;
    public Label lblQtdLoss_T01_Op01;
    public Label lblVlrIn_T01_Op01;
    public Label lblVlrOut_T01_Op01;
    public Label lblVlrDiff_T01_Op01;
    public TableView tbvTransacoes_T01_Op01;
    // Time_01 *-*-* Symbol_02
    public Label lblSymbol_T01_Op02;
    public Label lblQtdCall_T01_Op02;
    public Label lblQtdPut_T01_Op02;
    public Label lblQtdCallOrPut_T01_Op02;
    public ImageView imgCallOrPut_T01_Op02;
    public Label lblQtdStakes_T01_Op02;
    public Label lblQtdWins_T01_Op02;
    public Label lblQtdLoss_T01_Op02;
    public Label lblVlrIn_T01_Op02;
    public Label lblVlrOut_T01_Op02;
    public Label lblVlrDiff_T01_Op02;
    public TableView tbvTransacoes_T01_Op02;
    // Time_01 *-*-* Symbol_03
    public Label lblSymbol_T01_Op03;
    public Label lblQtdCall_T01_Op03;
    public Label lblQtdPut_T01_Op03;
    public Label lblQtdCallOrPut_T01_Op03;
    public ImageView imgCallOrPut_T01_Op03;
    public Label lblQtdStakes_T01_Op03;
    public Label lblQtdWins_T01_Op03;
    public Label lblQtdLoss_T01_Op03;
    public Label lblVlrIn_T01_Op03;
    public Label lblVlrOut_T01_Op03;
    public Label lblVlrDiff_T01_Op03;
    public TableView tbvTransacoes_T01_Op03;
    // Time_01 *-*-* Symbol_04
    public Label lblSymbol_T01_Op04;
    public Label lblQtdCall_T01_Op04;
    public Label lblQtdPut_T01_Op04;
    public Label lblQtdCallOrPut_T01_Op04;
    public ImageView imgCallOrPut_T01_Op04;
    public Label lblQtdStakes_T01_Op04;
    public Label lblQtdWins_T01_Op04;
    public Label lblQtdLoss_T01_Op04;
    public Label lblVlrIn_T01_Op04;
    public Label lblVlrOut_T01_Op04;
    public Label lblVlrDiff_T01_Op04;
    public TableView tbvTransacoes_T01_Op04;
    // Time_01 *-*-* Symbol_05
    public Label lblSymbol_T01_Op05;
    public Label lblQtdCall_T01_Op05;
    public Label lblQtdPut_T01_Op05;
    public Label lblQtdCallOrPut_T01_Op05;
    public ImageView imgCallOrPut_T01_Op05;
    public Label lblQtdStakes_T01_Op05;
    public Label lblQtdWins_T01_Op05;
    public Label lblQtdLoss_T01_Op05;
    public Label lblVlrIn_T01_Op05;
    public Label lblVlrOut_T01_Op05;
    public Label lblVlrDiff_T01_Op05;
    public TableView tbvTransacoes_T01_Op05;
    // Time_01 *-*-* Symbol_06
    public Label lblSymbol_T01_Op06;
    public Label lblQtdCall_T01_Op06;
    public Label lblQtdPut_T01_Op06;
    public Label lblQtdCallOrPut_T01_Op06;
    public ImageView imgCallOrPut_T01_Op06;
    public Label lblQtdStakes_T01_Op06;
    public Label lblQtdWins_T01_Op06;
    public Label lblQtdLoss_T01_Op06;
    public Label lblVlrIn_T01_Op06;
    public Label lblVlrOut_T01_Op06;
    public Label lblVlrDiff_T01_Op06;
    public TableView tbvTransacoes_T01_Op06;
    // Time_01 *-*-* Symbol_07
    public Label lblSymbol_T01_Op07;
    public Label lblQtdCall_T01_Op07;
    public Label lblQtdPut_T01_Op07;
    public Label lblQtdCallOrPut_T01_Op07;
    public ImageView imgCallOrPut_T01_Op07;
    public Label lblQtdStakes_T01_Op07;
    public Label lblQtdWins_T01_Op07;
    public Label lblQtdLoss_T01_Op07;
    public Label lblVlrIn_T01_Op07;
    public Label lblVlrOut_T01_Op07;
    public Label lblVlrDiff_T01_Op07;
    public TableView tbvTransacoes_T01_Op07;
    // Time_01 *-*-* Symbol_08
    public Label lblSymbol_T01_Op08;
    public Label lblQtdCall_T01_Op08;
    public Label lblQtdPut_T01_Op08;
    public Label lblQtdCallOrPut_T01_Op08;
    public ImageView imgCallOrPut_T01_Op08;
    public Label lblQtdStakes_T01_Op08;
    public Label lblQtdWins_T01_Op08;
    public Label lblQtdLoss_T01_Op08;
    public Label lblVlrIn_T01_Op08;
    public Label lblVlrOut_T01_Op08;
    public Label lblVlrDiff_T01_Op08;
    public TableView tbvTransacoes_T01_Op08;
    // Time_01 *-*-* Symbol_09
    public Label lblSymbol_T01_Op09;
    public Label lblQtdCall_T01_Op09;
    public Label lblQtdPut_T01_Op09;
    public Label lblQtdCallOrPut_T01_Op09;
    public ImageView imgCallOrPut_T01_Op09;
    public Label lblQtdStakes_T01_Op09;
    public Label lblQtdWins_T01_Op09;
    public Label lblQtdLoss_T01_Op09;
    public Label lblVlrIn_T01_Op09;
    public Label lblVlrOut_T01_Op09;
    public Label lblVlrDiff_T01_Op09;
    public TableView tbvTransacoes_T01_Op09;
    // Time_01 *-*-* Symbol_10
    public Label lblSymbol_T01_Op10;
    public Label lblQtdCall_T01_Op10;
    public Label lblQtdPut_T01_Op10;
    public Label lblQtdCallOrPut_T01_Op10;
    public ImageView imgCallOrPut_T01_Op10;
    public Label lblQtdStakes_T01_Op10;
    public Label lblQtdWins_T01_Op10;
    public Label lblQtdLoss_T01_Op10;
    public Label lblVlrIn_T01_Op10;
    public Label lblVlrOut_T01_Op10;
    public Label lblVlrDiff_T01_Op10;
    public TableView tbvTransacoes_T01_Op10;
    // Time_01 *-*-* Symbol_11
    public Label lblSymbol_T01_Op11;
    public Label lblQtdCall_T01_Op11;
    public Label lblQtdPut_T01_Op11;
    public Label lblQtdCallOrPut_T01_Op11;
    public ImageView imgCallOrPut_T01_Op11;
    public Label lblQtdStakes_T01_Op11;
    public Label lblQtdWins_T01_Op11;
    public Label lblQtdLoss_T01_Op11;
    public Label lblVlrIn_T01_Op11;
    public Label lblVlrOut_T01_Op11;
    public Label lblVlrDiff_T01_Op11;
    public TableView tbvTransacoes_T01_Op11;
    // Time_01 *-*-* Symbol_12
    public Label lblSymbol_T01_Op12;
    public Label lblQtdCall_T01_Op12;
    public Label lblQtdPut_T01_Op12;
    public Label lblQtdCallOrPut_T01_Op12;
    public ImageView imgCallOrPut_T01_Op12;
    public Label lblQtdStakes_T01_Op12;
    public Label lblQtdWins_T01_Op12;
    public Label lblQtdLoss_T01_Op12;
    public Label lblVlrIn_T01_Op12;
    public Label lblVlrOut_T01_Op12;
    public Label lblVlrDiff_T01_Op12;
    public TableView tbvTransacoes_T01_Op12;


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
                getWsClientObjectProperty().connect();
                wsConectadoProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n) {
                            monitorarTicks();
                            solicitarTicks();
                        } else {
                            getBtnTpnNegociacao_Stop().fire();
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

        getCboTpnNegociacaoQtdCandlesAnalise().getItems().setAll(100, 75, 50, 25, 0);
        getCboTpnNegociacaoQtdCandlesAnalise().getSelectionModel().select(0);

        for (int time_id = 0; time_id < TICK_TIME.values().length; time_id++) {

            getTimeAtivo()[time_id] = new SimpleBooleanProperty(false);

            for (int symbol_id = 0; symbol_id < getSymbolObservableList().size(); symbol_id++) {
                if (time_id == 0) {
                    getTickSubindo()[symbol_id] = new SimpleBooleanProperty(false);
                    getUltimoTickStr()[symbol_id] = new SimpleStringProperty("");
                    getUltimoOhlcStr()[symbol_id] = new SimpleStringProperty("");
                }
                if (symbol_id == 0) {
                    getTimeCandleStart()[time_id] = new SimpleIntegerProperty(0);
                    getTimeCandleToClose()[time_id] = new SimpleIntegerProperty(0);
                }


                getUltimoTick()[time_id][symbol_id] = new SimpleObjectProperty<>();
                getUltimoOhlc()[time_id][symbol_id] = new SimpleObjectProperty<>();
                getHistoricoDeTicksObservableList()[time_id][symbol_id] = FXCollections.observableArrayList();
                getHistoricoDeOhlcObservableList()[time_id][symbol_id] = FXCollections.observableArrayList();
                getTransactionObservableList()[time_id][symbol_id] = FXCollections.observableArrayList();


                getQtdCallOrPut()[time_id][symbol_id] = new SimpleIntegerProperty(0);
                getQtdCall()[time_id][symbol_id] = new SimpleIntegerProperty(0);
                getQtdPut()[time_id][symbol_id] = new SimpleIntegerProperty(0);
            }
        }

        Thread threadInicial = new Thread(getTaskWsBinary());
        threadInicial.setDaemon(true);
        threadInicial.start();

    }

    private void carregarObjetos() {

        getCboTpnDetalhesContaBinary().setItems(getContaTokenObservableList());

        getCboNegociacaoRobos().setItems(ROBOS.getList().stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboNegociacaoRobos().getItems().add(0, null);

        objetosBindings();

        escutandoObjetos();

        comandosDeBotoes();

    }

    private void objetosBindings() {

        saldoInicialProperty().bind(Bindings.createObjectBinding(() -> {
            if (authorizeProperty().getValue() == null)
                return BigDecimal.ZERO;
            return authorizeProperty().getValue().getBalance();
        }, authorizeProperty()));

        getLblDetalhesSaldoInicial().textProperty().bind(Bindings.createStringBinding(() ->
                        Service_Mascara.getValorMoeda(saldoInicialProperty().getValue()),
                saldoInicialProperty()));

        getLblNegociacaoParametros().textProperty().bind(parametrosUtilizadosRoboProperty());

        getBtnTpnNegociacao_Contratos().disableProperty().bind(btnContratoDisabledProperty());
        getBtnTpnNegociacao_Iniciar().disableProperty().bind(btnIniciardisabledProperty());
        getBtnTpnNegociacao_Pausar().disableProperty().bind(btnPausarDisabledProperty());
        getBtnTpnNegociacao_Stop().disableProperty().bind(btnStopDisabledProperty());

    }

    private void escutandoObjetos() {

        appAutorizadoProperty().addListener((ov, o, n) -> {
            getHboxDetalhesSaldoConta().getStyleClass().clear();
            if (n != null && n) {
                if (getAuthorize().getIs_virtual() == 1)
                    getHboxDetalhesSaldoConta().getStyleClass().add("vlr-conta-virtual");
                else
                    getHboxDetalhesSaldoConta().getStyleClass().add("vlr-conta-real");
            }
        });

        authorizeProperty().addListener((ov, o, n) -> {
            if (n != null) {
                solicitarTransacoes();
                setBtnContratoDisabled(true);
            } else {
                setBtnContratoDisabled(getRoboAtivo() == null);
            }
            getLblDetalhesProprietarioConta().setText(n != null
                    ? (n.getFullname().replaceAll("\\W", "").length() > 0
                    ? String.format("%s (%s)", n.getFullname(), n.getEmail())
                    : String.format("%s", n.getEmail()))
                    : "");
            getLblDetalhesContaId().setText(n != null
                    ? String.format("%s %s",
                    n.getLoginid().replaceAll("\\d", ""),
                    n.getLoginid().replaceAll("\\D", ""))
                    : "");

            getHboxDetalhesSaldoConta().getStyleClass().clear();
            if (getAuthorize().getIs_virtual() == 1)
                getHboxDetalhesSaldoConta().getStyleClass().add("vlr-conta-virtual");
            else
                getHboxDetalhesSaldoConta().getStyleClass().add("vlr-conta-real");

            getLblDetalhesSaldoContaVlr().setText(n != null
                    ? Service_Mascara.getValorMoeda(n.getBalance())
                    : "0.00");
            getLblDetalhesSaldoContaCifrao().setText(n != null
                    ? n.getCurrency()
                    : "");
        });

        getCboTpnDetalhesContaBinary().valueProperty().addListener((ov, o, n) -> {
            if (n == null) {
                setAppAutorizado(false);
                return;
            }
            solicitarAutorizacaoApp(n.getTokenApi());
        });

        getCboNegociacaoRobos().valueProperty().addListener((ov, o, n) -> {
            setROBO_Selecionado(n);
            if (n == null) {
                setRoboAtivo(null);
                setBtnContratoDisabled(true);
                return;
            } else {
                setBtnContratoDisabled(getAuthorize() == null);
            }
            switch (n) {
                case ABR -> {
                    Abr abr = new Abr();
                    setRoboAtivo(abr);
                }
            }
        });

    }

    private void comandosDeBotoes() {

        getBtnTpnNegociacao_Contratos().setOnAction(event -> getRoboAtivo().definicaoDeContrato());

        getBtnTpnNegociacao_Iniciar().setOnAction(event -> getRoboAtivo().monitorarCondicoesParaComprar());

        getBtnTpnNegociacao_Stop().setOnAction(event -> {
            getCboNegociacaoRobos().getSelectionModel().select(0);
        });

    }

    private void carregarAcoesObjetos() {

//        getOperador()[0].bind(getCboSymbol01().valueProperty());
//        getOperador()[1].bind(getCboSymbol02().valueProperty());
//        getOperador()[2].bind(getCboSymbol03().valueProperty());
//        getOperador()[3].bind(getCboSymbol04().valueProperty());
//        getOperador()[4].bind(getCboSymbol05().valueProperty());
//
//        for (int operadorId = 0; operadorId < 5; operadorId++) {
//            int finalOperadorId = operadorId;
//            getOperador()[operadorId].addListener((ov, o, n) -> {
//                if (n == null) return;
//                solicitarTicks(n);
//            });
//        }

    }

    private void conectarObjetosEmVariaveis() {

        conectarObjetosEmVariaveis_LastTicks();

        conectarObjetosEmVariaveis_Time01(TIME_1M);

        conectarTimesAtivos();


//        getTpn_T02().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_2M)));
//        getTpn_T03().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_3M)));
//        getTpn_T04().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_5M)));
//        getTpn_T05().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_10M)));
//        getTpn_T06().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_15M)));


        //        getUltimoOhlc()[0][0].addListener((ov, o, n) -> {
//            if (n == null)
//                getLblLastTickSymbol_01().setText("");
//            if (n == null || o == null)
//                return;
//            getLblLastTickSymbol_01().setText(n.toString());
//            if (getLblRoboHoraInicial().getText().equals(""))
//                getLblRoboHoraInicial().setText(
//                        LocalDateTime.ofInstant(Instant.ofEpochSecond(n.getOpen_time()),
//                                TimeZone.getDefault().toZoneId()).format(DTF_TMODEL_DATA_TRANSACTION)
//                );
//            if (n.getClose().compareTo(o.getClose()) >= 0)
//                getTpnLlUltTick_Op01().setStyle(STYLE_TICK_SUBINDO);
//            else
//                getTpnLlUltTick_Op01().setStyle(STYLE_TICK_DESCENDO);
//            Integer time_close = (n.getGranularity() - (n.getEpoch() - n.getOpen_time()));
//            getLblTime_Op01_T01().setText(String.format("%s M [t -%ss]",
//                    n.getGranularity() / 60,
//                    time_close));
//            if (time_close <= 2 && time_close > 0) {
//                if (n.getClose().compareTo(n.getOpen()) > 0) {
//                    if (getQtdCallPut()[0][0].getValue().compareTo(BigDecimal.ZERO) > 0)
//                        getQtdCallPut()[0][0].setValue(getQtdCallPut()[0][0].getValue().add(BigDecimal.ONE));
//                    else
//                        getQtdCallPut()[0][0].setValue(BigDecimal.ONE);
//                    getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().toString());
//                    getImgCallPut_Op01_T01().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
//                } else if (n.getClose().compareTo(n.getOpen()) < 0) {
//                    if (getQtdCallPut()[0][0].getValue().compareTo(BigDecimal.ZERO) < 0)
//                        getQtdCallPut()[0][0].setValue(getQtdCallPut()[0][0].getValue().subtract(BigDecimal.ONE));
//                    else
//                        getQtdCallPut()[0][0].setValue(BigDecimal.ONE.negate());
//                    getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().negate().toString());
//                    getImgCallPut_Op01_T01().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
//                } else {
//                    getLblQtdCallPut_Op01_T01().setText("0");
//                    getLblQtdCallPut_Op01_T01().setGraphic(null);
//                }
//                getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().toString().replace("-", ""));
//            }
//        });


    }

    /**
     * Gerar PriceProposal and Proposal
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public void gerarContrato(TICK_TIME time, Symbol symbol, CONTRACT_TYPE cType) throws Exception {

        Passthrough passthrough = new Passthrough(symbol, time, getTickStyle(), cType, "");
        int t_id = time.getCod(), s_id = symbol.getId().intValue() - 1;

        getPriceProposal()[t_id][s_id] = new PriceProposal();

        getPriceProposal()[t_id][s_id].setProposal(1);
        getPriceProposal()[t_id][s_id].setAmount(getVlrStkContrato()[t_id][s_id].getValue());
        getPriceProposal()[t_id][s_id].setBasis(PRICE_PROPOSAL_BASIS);
        getPriceProposal()[t_id][s_id].setContract_type(cType);
        getPriceProposal()[t_id][s_id].setCurrency(getAuthorize().getCurrency().toUpperCase());
        getPriceProposal()[t_id][s_id].setDuration(Integer.valueOf(time.getDescricao().replaceAll("\\D", "")) * 60);
        getPriceProposal()[t_id][s_id].setDuration_unit(DURATION_UNIT.s);
        getPriceProposal()[t_id][s_id].setSymbol(symbol.getSymbol());
        getPriceProposal()[t_id][s_id].setPassthrough(passthrough);

        solicitarProposal(getPriceProposal()[t_id][s_id]);

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

    private void solicitarAutorizacaoApp(String tokenApi) {

        if (tokenApi == null) return;
        String jsonAuthorize = String.format("{\"authorize\": \"%s\"}", tokenApi);
        getWsClientObjectProperty().getMyWebSocket().send(jsonAuthorize);

    }

    private void solicitarTransacoes() {

        if (getAuthorize() == null) return;
        try {
            String jsonTransacoes = Util_Json.getJson_from_Object(new TransactionsStream());
            getWsClientObjectProperty().getMyWebSocket().send(jsonTransacoes);
            setAppAutorizado(true);
        } catch (Exception ex) {
            setAppAutorizado(false);
            ex.printStackTrace();
        }

    }

    private void solicitarTicks() {

        Symbol symbol;
        Passthrough passthrough = new Passthrough();
        for (int z = 0; z < getSymbolObservableList().size(); z++) {
            symbol = getSymbolObservableList().get(z);
            passthrough.setTickStyle(getTickStyle());
            passthrough.setSymbol(symbol);
            Integer tempoVela;
            for (TICK_TIME tickTime : TICK_TIME.values()) {
                tempoVela = Integer.parseInt(tickTime.getDescricao().replaceAll("\\D", "")) * 60;
                passthrough.setTickTime(tickTime);
                String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(symbol.getSymbol(),
                        getCboTpnNegociacaoQtdCandlesAnalise().getValue(), getTickStyle(), tempoVela, passthrough));
                if (tempoVela == null) jsonHistory = jsonHistory.replace(",\"granularity\":null", "");
                if (passthrough == null) jsonHistory = jsonHistory.replace(",\"passthrough\":null", "");
                getWsClientObjectProperty().getMyWebSocket().send(jsonHistory);
            }
        }

    }

    public void solicitarProposal(PriceProposal priceProposal) {

        if (!isAppAutorizado()) return;
        try {
            String jsonPriceProposal = Util_Json.getJson_from_Object(priceProposal)
                    .replace("\"barrier\":null,", "");
            getWsClientObjectProperty().getMyWebSocket().send(jsonPriceProposal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void solicitarCompraContrato(Proposal proposal) {

        try {
            String jsonBuyContrato = Util_Json.getJson_from_Object(new BuyContract(proposal));
            System.out.printf("jsonBuyContrato: %s\n", jsonBuyContrato);
            getWsClientObjectProperty().getMyWebSocket().send(jsonBuyContrato);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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
    private void conectarObjetosEmVariaveis_LastTicks() {
        /**
         * Last Ticks
         */

        //SYMBOL_01
        getLblSymbol_01().setText(getSymbolObservableList().get(SYMBOL_01).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_01().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_01].getValue()
                                : getUltimoOhlcStr()[SYMBOL_01].getValue(),
                getUltimoTickStr()[SYMBOL_01], getUltimoOhlcStr()[SYMBOL_01]));
        getLblLastTickSymbol_01().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_01].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_01]));

        //SYMBOL_02
        getLblSymbol_02().setText(getSymbolObservableList().get(SYMBOL_02).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_02().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_02].getValue()
                                : getUltimoOhlcStr()[SYMBOL_02].getValue(),
                getUltimoTickStr()[SYMBOL_02], getUltimoOhlcStr()[SYMBOL_02]));
        getLblLastTickSymbol_02().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_02].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_02]));

        //SYMBOL_03
        getLblSymbol_03().setText(getSymbolObservableList().get(SYMBOL_03).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_03().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_03].getValue()
                                : getUltimoOhlcStr()[SYMBOL_03].getValue(),
                getUltimoTickStr()[SYMBOL_03], getUltimoOhlcStr()[SYMBOL_03]));
        getLblLastTickSymbol_03().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_03].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_03]));

        //SYMBOL_04
        getLblSymbol_04().setText(getSymbolObservableList().get(SYMBOL_04).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_04().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_04].getValue()
                                : getUltimoOhlcStr()[SYMBOL_04].getValue(),
                getUltimoTickStr()[SYMBOL_04], getUltimoOhlcStr()[SYMBOL_04]));
        getLblLastTickSymbol_04().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_04].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_04]));

        //SYMBOL_05
        getLblSymbol_05().setText(getSymbolObservableList().get(SYMBOL_05).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_05().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_05].getValue()
                                : getUltimoOhlcStr()[SYMBOL_05].getValue(),
                getUltimoTickStr()[SYMBOL_05], getUltimoOhlcStr()[SYMBOL_05]));
        getLblLastTickSymbol_05().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_05].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_05]));

        //SYMBOL_06
        getLblSymbol_06().setText(getSymbolObservableList().get(SYMBOL_06).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_06().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_06].getValue()
                                : getUltimoOhlcStr()[SYMBOL_06].getValue(),
                getUltimoTickStr()[SYMBOL_06], getUltimoOhlcStr()[SYMBOL_06]));
        getLblLastTickSymbol_06().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_06].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_06]));

        //SYMBOL_07
        getLblSymbol_07().setText(getSymbolObservableList().get(SYMBOL_07).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_07().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_07].getValue()
                                : getUltimoOhlcStr()[SYMBOL_07].getValue(),
                getUltimoTickStr()[SYMBOL_07], getUltimoOhlcStr()[SYMBOL_07]));
        getLblLastTickSymbol_07().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_07].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_07]));

        //SYMBOL_08
        getLblSymbol_08().setText(getSymbolObservableList().get(SYMBOL_08).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_08().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_08].getValue()
                                : getUltimoOhlcStr()[SYMBOL_08].getValue(),
                getUltimoTickStr()[SYMBOL_08], getUltimoOhlcStr()[SYMBOL_08]));
        getLblLastTickSymbol_08().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_08].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_08]));

        //SYMBOL_09
        getLblSymbol_09().setText(getSymbolObservableList().get(SYMBOL_09).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_09().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_09].getValue()
                                : getUltimoOhlcStr()[SYMBOL_09].getValue(),
                getUltimoTickStr()[SYMBOL_09], getUltimoOhlcStr()[SYMBOL_09]));
        getLblLastTickSymbol_09().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_09].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_09]));

        //SYMBOL_10
        getLblSymbol_10().setText(getSymbolObservableList().get(SYMBOL_10).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_10().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_10].getValue()
                                : getUltimoOhlcStr()[SYMBOL_10].getValue(),
                getUltimoTickStr()[SYMBOL_10], getUltimoOhlcStr()[SYMBOL_10]));
        getLblLastTickSymbol_10().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_10].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_10]));

        //SYMBOL_11
        getLblSymbol_11().setText(getSymbolObservableList().get(SYMBOL_11).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_11().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_11].getValue()
                                : getUltimoOhlcStr()[SYMBOL_11].getValue(),
                getUltimoTickStr()[SYMBOL_11], getUltimoOhlcStr()[SYMBOL_11]));
        getLblLastTickSymbol_11().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_11].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_11]));

        //SYMBOL_12
        getLblSymbol_12().setText(getSymbolObservableList().get(SYMBOL_12).getDisplay_name().replace(" Index", ""));
        getLblLastTickSymbol_12().textProperty().bind(Bindings.createStringBinding(() ->
                        getTickStyle().equals(TICK_STYLE.TICKS)
                                ? getUltimoTickStr()[SYMBOL_12].getValue()
                                : getUltimoOhlcStr()[SYMBOL_12].getValue(),
                getUltimoTickStr()[SYMBOL_12], getUltimoOhlcStr()[SYMBOL_12]));
        getLblLastTickSymbol_12().styleProperty().bind(Bindings.createStringBinding(() ->
                        getTickSubindo()[SYMBOL_12].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO,
                getTickSubindo()[SYMBOL_12]));

    }

    private void conectarObjetosEmVariaveis_Time01(Integer timer) {

        if (timer == TIME_1M) {
            getTpn_T01().setText(String.format("T%s - ", TICK_TIME.toEnum(timer)));
            getLblTpnT01_CandleTimeStart().textProperty().bind(Bindings.createStringBinding(() ->
                            getDataFromInteger(getTimeCandleStart()[timer].getValue()),
                    getTimeCandleStart()[timer]));
            getLblTpnT01_TimeEnd().textProperty().bind(Bindings.createStringBinding(() ->
                            String.format("%s s", getTimeCandleToClose()[timer].getValue()),
                    getTimeCandleToClose()[timer]));

            //*-*-* Op_01
            getLblSymbol_T01_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
            getLblQtdCall_T01_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_01].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_01]));
            getLblQtdPut_T01_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_01].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_01]));
            getLblQtdCallOrPut_T01_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_01].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op01().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_01].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op01().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op01().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_01].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_01]));

            //*-*-* Op_02
            getLblSymbol_T01_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
            getLblQtdCall_T01_Op02().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_02].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_02]));
            getLblQtdPut_T01_Op02().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_02].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_02]));
            getLblQtdCallOrPut_T01_Op02().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_02].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op02().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_02].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op02().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op02().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_02].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_02]));

            //*-*-* Op_03
            getLblSymbol_T01_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
            getLblQtdCall_T01_Op03().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_03].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_03]));
            getLblQtdPut_T01_Op03().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_03].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_03]));
            getLblQtdCallOrPut_T01_Op03().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_03].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op03().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_03].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op03().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op03().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_03].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_03]));

            //*-*-* Op_04
            getLblSymbol_T01_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
            getLblQtdCall_T01_Op04().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_04].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_04]));
            getLblQtdPut_T01_Op04().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_04].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_04]));
            getLblQtdCallOrPut_T01_Op04().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_04].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op04().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_04].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op04().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op04().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_04].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_04]));

            //*-*-* Op_05
            getLblSymbol_T01_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
            getLblQtdCall_T01_Op05().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_05].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_05]));
            getLblQtdPut_T01_Op05().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_05].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_05]));
            getLblQtdCallOrPut_T01_Op05().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_05].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op05().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_05].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op05().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op05().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_05].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_05]));

            //*-*-* Op_06
            getLblSymbol_T01_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
            getLblQtdCall_T01_Op06().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_06].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_06]));
            getLblQtdPut_T01_Op06().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_06].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_06]));
            getLblQtdCallOrPut_T01_Op06().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_06].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op06().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_06].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op06().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op06().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_06].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_06]));

            //*-*-* Op_07
            getLblSymbol_T01_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
            getLblQtdCall_T01_Op07().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_07].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_07]));
            getLblQtdPut_T01_Op07().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_07].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_07]));
            getLblQtdCallOrPut_T01_Op07().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_07].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op07().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_07].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op07().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op07().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_07].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_07]));

            //*-*-* Op_08
            getLblSymbol_T01_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
            getLblQtdCall_T01_Op08().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_08].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_08]));
            getLblQtdPut_T01_Op08().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_08].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_08]));
            getLblQtdCallOrPut_T01_Op08().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_08].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op08().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_08].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op08().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op08().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_08].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_08]));

            //*-*-* Op_09
            getLblSymbol_T01_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
            getLblQtdCall_T01_Op09().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_09].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_09]));
            getLblQtdPut_T01_Op09().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_09].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_09]));
            getLblQtdCallOrPut_T01_Op09().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_09].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op09().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_09].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op09().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op09().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_09].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_09]));

            //*-*-* Op_10
            getLblSymbol_T01_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
            getLblQtdCall_T01_Op10().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_10].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_10]));
            getLblQtdPut_T01_Op10().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_10].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_10]));
            getLblQtdCallOrPut_T01_Op10().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_10].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op10().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_10].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op10().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op10().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_10].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_10]));

            //*-*-* Op_11
            getLblSymbol_T01_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
            getLblQtdCall_T01_Op11().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_11].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_11]));
            getLblQtdPut_T01_Op11().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_11].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_11]));
            getLblQtdCallOrPut_T01_Op11().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_11].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op11().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_11].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op11().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op11().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_11].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_11]));

            //*-*-* Op_12
            getLblSymbol_T01_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
            getLblQtdCall_T01_Op12().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdCall()[timer][SYMBOL_12].getValue().toString(),
                    getQtdCall()[timer][SYMBOL_12]));
            getLblQtdPut_T01_Op12().textProperty().bind(Bindings.createStringBinding(() ->
                            getQtdPut()[timer][SYMBOL_12].getValue().toString(),
                    getQtdPut()[timer][SYMBOL_12]));
            getLblQtdCallOrPut_T01_Op12().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getQtdCallOrPut()[timer][SYMBOL_12].getValue().compareTo(1) >= 0)
                    getImgCallOrPut_T01_Op12().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                else if (getQtdCallOrPut()[timer][SYMBOL_12].getValue().compareTo(-1) <= 0)
                    getImgCallOrPut_T01_Op12().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                else
                    getImgCallOrPut_T01_Op12().setImage(null);
                return String.valueOf(Math.abs(getQtdCallOrPut()[timer][SYMBOL_12].getValue()));
            }, getQtdCallOrPut()[timer][SYMBOL_12]));

        } else if (timer == TIME_2M) {
//                getTpn_T02().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_2M)));
//                getLblSymbol_T02_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
//                getLblSymbol_T02_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
//                getLblSymbol_T02_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
//                getLblSymbol_T02_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
//                getLblSymbol_T02_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
//                getLblSymbol_T02_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
//                getLblSymbol_T02_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
//                getLblSymbol_T02_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
//                getLblSymbol_T02_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
//                getLblSymbol_T02_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
//                getLblSymbol_T02_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
//                getLblSymbol_T02_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
        } else if (timer == TIME_3M) {
//                getTpn_T03().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_3M)));
//                getLblSymbol_T03_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
//                getLblSymbol_T03_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
//                getLblSymbol_T03_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
//                getLblSymbol_T03_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
//                getLblSymbol_T03_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
//                getLblSymbol_T03_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
//                getLblSymbol_T03_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
//                getLblSymbol_T03_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
//                getLblSymbol_T03_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
//                getLblSymbol_T03_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
//                getLblSymbol_T03_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
//                getLblSymbol_T03_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
        } else if (timer == TIME_5M) {
//                getTpn_T04().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_5M)));
//                getLblSymbol_T04_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
//                getLblSymbol_T04_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
//                getLblSymbol_T04_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
//                getLblSymbol_T04_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
//                getLblSymbol_T04_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
//                getLblSymbol_T04_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
//                getLblSymbol_T04_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
//                getLblSymbol_T04_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
//                getLblSymbol_T04_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
//                getLblSymbol_T04_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
//                getLblSymbol_T04_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
//                getLblSymbol_T04_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
        } else if (timer == TIME_10M) {
//                getTpn_T05().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_10M)));
//                getLblSymbol_T05_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
//                getLblSymbol_T05_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
//                getLblSymbol_T05_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
//                getLblSymbol_T05_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
//                getLblSymbol_T05_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
//                getLblSymbol_T05_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
//                getLblSymbol_T05_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
//                getLblSymbol_T05_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
//                getLblSymbol_T05_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
//                getLblSymbol_T05_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
//                getLblSymbol_T05_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
//                getLblSymbol_T05_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
        } else if (timer == TIME_15M) {
//                getTpn_T06().setText(String.format("T%s - ", TICK_TIME.toEnum(TIME_15M)));
//                getLblSymbol_T06_Op01().setText(getSymbolObservableList().get(SYMBOL_01).getSymbol());
//                getLblSymbol_T06_Op02().setText(getSymbolObservableList().get(SYMBOL_02).getSymbol());
//                getLblSymbol_T06_Op03().setText(getSymbolObservableList().get(SYMBOL_03).getSymbol());
//                getLblSymbol_T06_Op04().setText(getSymbolObservableList().get(SYMBOL_04).getSymbol());
//                getLblSymbol_T06_Op05().setText(getSymbolObservableList().get(SYMBOL_05).getSymbol());
//                getLblSymbol_T06_Op06().setText(getSymbolObservableList().get(SYMBOL_06).getSymbol());
//                getLblSymbol_T06_Op07().setText(getSymbolObservableList().get(SYMBOL_07).getSymbol());
//                getLblSymbol_T06_Op08().setText(getSymbolObservableList().get(SYMBOL_08).getSymbol());
//                getLblSymbol_T06_Op09().setText(getSymbolObservableList().get(SYMBOL_09).getSymbol());
//                getLblSymbol_T06_Op10().setText(getSymbolObservableList().get(SYMBOL_10).getSymbol());
//                getLblSymbol_T06_Op11().setText(getSymbolObservableList().get(SYMBOL_11).getSymbol());
//                getLblSymbol_T06_Op12().setText(getSymbolObservableList().get(SYMBOL_12).getSymbol());
        }

    }

    private void conectarTimesAtivos() {

        getTimeAtivo()[TIME_1M].bind(getChkTpn01_TimeAtivo().selectedProperty());
//        getTimeAtivo()[TIME_2M].bind(getChkTpn02_TimeAtivo().selectedProperty());
//        getTimeAtivo()[TIME_3M].bind(getChkTpn03_TimeAtivo().selectedProperty());
//        getTimeAtivo()[TIME_5M].bind(getChkTpn04_TimeAtivo().selectedProperty());
//        getTimeAtivo()[TIME_10M].bind(getChkTpn05_TimeAtivo().selectedProperty());
//        getTimeAtivo()[TIME_15M].bind(getChkTpn06_TimeAtivo().selectedProperty());

        getChkTpn01_TimeAtivo().setSelected(true);
//        getChkTpn02_TimeAtivo().setSelected(true);
//        getChkTpn03_TimeAtivo().setSelected(true);
//        getChkTpn04_TimeAtivo().setSelected(true);
//        getChkTpn05_TimeAtivo().setSelected(true);
//        getChkTpn06_TimeAtivo().setSelected(true);

    }

    private String getDataFromInteger(Integer epoch) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch),
                TimeZone.getDefault().toZoneId()).format(DTF_HORA_MINUTOS);
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

    public static ObservableList<ContaToken> getContaTokenObservableList() {
        return CONTA_TOKEN_OBSERVABLE_LIST;
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

    public static boolean isWsConectado() {
        return wsConectado.get();
    }

    public static BooleanProperty wsConectadoProperty() {
        return wsConectado;
    }

    public static void setWsConectado(boolean wsConectado) {
        Operacoes.wsConectado.set(wsConectado);
    }

    public static WSClient getWsClientObjectProperty() {
        return WS_CLIENT_OBJECT_PROPERTY.get();
    }

    public static ObjectProperty<WSClient> WS_CLIENT_OBJECT_PROPERTYProperty() {
        return WS_CLIENT_OBJECT_PROPERTY;
    }

    public static void setWsClientObjectProperty(WSClient wsClientObjectProperty) {
        WS_CLIENT_OBJECT_PROPERTY.set(wsClientObjectProperty);
    }

    public static br.com.tlmacedo.binary.model.enums.TICK_STYLE getTickStyle() {
        return TICK_STYLE;
    }

    public static Robo getRoboAtivo() {
        return ROBO_ATIVO.get();
    }

    public static ObjectProperty<Robo> ROBO_ATIVOProperty() {
        return ROBO_ATIVO;
    }

    public static void setRoboAtivo(Robo roboAtivo) {
        ROBO_ATIVO.set(roboAtivo);
    }

    public static boolean isAppAutorizado() {
        return appAutorizado.get();
    }

    public static BooleanProperty appAutorizadoProperty() {
        return appAutorizado;
    }

    public static void setAppAutorizado(boolean appAutorizado) {
        Operacoes.appAutorizado.set(appAutorizado);
    }

    public static Timeline getRoboRelogio() {
        return roboRelogio;
    }

    public static void setRoboRelogio(Timeline roboRelogio) {
        Operacoes.roboRelogio = roboRelogio;
    }

    public static long getRoboHoraInicial() {
        return roboHoraInicial.get();
    }

    public static LongProperty roboHoraInicialProperty() {
        return roboHoraInicial;
    }

    public static void setRoboHoraInicial(long roboHoraInicial) {
        Operacoes.roboHoraInicial.set(roboHoraInicial);
    }

    public static long getRoboCronometro() {
        return roboCronometro.get();
    }

    public static LongProperty roboCronometroProperty() {
        return roboCronometro;
    }

    public static void setRoboCronometro(long roboCronometro) {
        Operacoes.roboCronometro.set(roboCronometro);
    }

    public static boolean isRoboCronometroAtivado() {
        return roboCronometroAtivado.get();
    }

    public static BooleanProperty roboCronometroAtivadoProperty() {
        return roboCronometroAtivado;
    }

    public static void setRoboCronometroAtivado(boolean roboCronometroAtivado) {
        Operacoes.roboCronometroAtivado.set(roboCronometroAtivado);
    }

    public static BigDecimal getSaldoInicial() {
        return saldoInicial.get();
    }

    public static ObjectProperty<BigDecimal> saldoInicialProperty() {
        return saldoInicial;
    }

    public static void setSaldoInicial(BigDecimal saldoInicial) {
        Operacoes.saldoInicial.set(saldoInicial);
    }

    public static IntegerProperty[] getQtdCandlesEntrada() {
        return qtdCandlesEntrada;
    }

    public static void setQtdCandlesEntrada(IntegerProperty[] qtdCandlesEntrada) {
        Operacoes.qtdCandlesEntrada = qtdCandlesEntrada;
    }

    public static ObjectProperty<Tick>[][] getUltimoTick() {
        return ultimoTick;
    }

    public static void setUltimoTick(ObjectProperty<Tick>[][] ultimoTick) {
        Operacoes.ultimoTick = ultimoTick;
    }

    public static ObjectProperty<Ohlc>[][] getUltimoOhlc() {
        return ultimoOhlc;
    }

    public static void setUltimoOhlc(ObjectProperty<Ohlc>[][] ultimoOhlc) {
        Operacoes.ultimoOhlc = ultimoOhlc;
    }

    public static StringProperty[] getUltimoTickStr() {
        return ultimoTickStr;
    }

    public static void setUltimoTickStr(StringProperty[] ultimoTickStr) {
        Operacoes.ultimoTickStr = ultimoTickStr;
    }

    public static StringProperty[] getUltimoOhlcStr() {
        return ultimoOhlcStr;
    }

    public static void setUltimoOhlcStr(StringProperty[] ultimoOhlcStr) {
        Operacoes.ultimoOhlcStr = ultimoOhlcStr;
    }

    public static BooleanProperty[] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[] tickSubindo) {
        Operacoes.tickSubindo = tickSubindo;
    }

    public static IntegerProperty[] getTimeCandleStart() {
        return timeCandleStart;
    }

    public static void setTimeCandleStart(IntegerProperty[] timeCandleStart) {
        Operacoes.timeCandleStart = timeCandleStart;
    }

    public static IntegerProperty[] getTimeCandleToClose() {
        return timeCandleToClose;
    }

    public static void setTimeCandleToClose(IntegerProperty[] timeCandleToClose) {
        Operacoes.timeCandleToClose = timeCandleToClose;
    }

    public static IntegerProperty[][] getQtdCallOrPut() {
        return qtdCallOrPut;
    }

    public static void setQtdCallOrPut(IntegerProperty[][] qtdCallOrPut) {
        Operacoes.qtdCallOrPut = qtdCallOrPut;
    }

    public static IntegerProperty[][] getQtdCall() {
        return qtdCall;
    }

    public static void setQtdCall(IntegerProperty[][] qtdCall) {
        Operacoes.qtdCall = qtdCall;
    }

    public static IntegerProperty[][] getQtdPut() {
        return qtdPut;
    }

    public static void setQtdPut(IntegerProperty[][] qtdPut) {
        Operacoes.qtdPut = qtdPut;
    }

    public static ObservableList<HistoricoDeTicks>[][] getHistoricoDeTicksObservableList() {
        return historicoDeTicksObservableList;
    }

    public static void setHistoricoDeTicksObservableList(ObservableList<HistoricoDeTicks>[][] historicoDeTicksObservableList) {
        Operacoes.historicoDeTicksObservableList = historicoDeTicksObservableList;
    }

    public static ObservableList<HistoricoDeOhlc>[][] getHistoricoDeOhlcObservableList() {
        return historicoDeOhlcObservableList;
    }

    public static void setHistoricoDeOhlcObservableList(ObservableList<HistoricoDeOhlc>[][] historicoDeOhlcObservableList) {
        Operacoes.historicoDeOhlcObservableList = historicoDeOhlcObservableList;
    }

    public static BooleanProperty[] getTimeAtivo() {
        return timeAtivo;
    }

    public static void setTimeAtivo(BooleanProperty[] timeAtivo) {
        Operacoes.timeAtivo = timeAtivo;
    }

    public static ObjectProperty<BigDecimal>[] getVlrStkPadrao() {
        return vlrStkPadrao;
    }

    public static void setVlrStkPadrao(ObjectProperty<BigDecimal>[] vlrStkPadrao) {
        Operacoes.vlrStkPadrao = vlrStkPadrao;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrStkContrato() {
        return vlrStkContrato;
    }

    public static void setVlrStkContrato(ObjectProperty<BigDecimal>[][] vlrStkContrato) {
        Operacoes.vlrStkContrato = vlrStkContrato;
    }


    public static PriceProposal[][] getPriceProposal() {
        return priceProposal;
    }

    public static void setPriceProposal(PriceProposal[][] priceProposal) {
        Operacoes.priceProposal = priceProposal;
    }

    public AnchorPane getPnlViewBinary() {
        return pnlViewBinary;
    }

    public void setPnlViewBinary(AnchorPane pnlViewBinary) {
        this.pnlViewBinary = pnlViewBinary;
    }

    public TitledPane getTpn_Detalhes() {
        return tpn_Detalhes;
    }

    public void setTpn_Detalhes(TitledPane tpn_Detalhes) {
        this.tpn_Detalhes = tpn_Detalhes;
    }

    public ComboBox<ContaToken> getCboTpnDetalhesContaBinary() {
        return cboTpnDetalhesContaBinary;
    }

    public void setCboTpnDetalhesContaBinary(ComboBox<ContaToken> cboTpnDetalhesContaBinary) {
        this.cboTpnDetalhesContaBinary = cboTpnDetalhesContaBinary;
    }

    public Label getLblTpnDetalhesQtdStakes() {
        return lblTpnDetalhesQtdStakes;
    }

    public void setLblTpnDetalhesQtdStakes(Label lblTpnDetalhesQtdStakes) {
        this.lblTpnDetalhesQtdStakes = lblTpnDetalhesQtdStakes;
    }

    public Label getLblTpnDetalhesQtdWins() {
        return lblTpnDetalhesQtdWins;
    }

    public void setLblTpnDetalhesQtdWins(Label lblTpnDetalhesQtdWins) {
        this.lblTpnDetalhesQtdWins = lblTpnDetalhesQtdWins;
    }

    public Label getLblTpnDetalhesQtdLoss() {
        return lblTpnDetalhesQtdLoss;
    }

    public void setLblTpnDetalhesQtdLoss(Label lblTpnDetalhesQtdLoss) {
        this.lblTpnDetalhesQtdLoss = lblTpnDetalhesQtdLoss;
    }

    public Label getLblTpnDetalhesProfitVlr() {
        return lblTpnDetalhesProfitVlr;
    }

    public void setLblTpnDetalhesProfitVlr(Label lblTpnDetalhesProfitVlr) {
        this.lblTpnDetalhesProfitVlr = lblTpnDetalhesProfitVlr;
    }

    public Label getLblTpnDetalhesProfitPorc() {
        return lblTpnDetalhesProfitPorc;
    }

    public void setLblTpnDetalhesProfitPorc(Label lblTpnDetalhesProfitPorc) {
        this.lblTpnDetalhesProfitPorc = lblTpnDetalhesProfitPorc;
    }

    public Label getLblDetalhesProprietarioConta() {
        return lblDetalhesProprietarioConta;
    }

    public void setLblDetalhesProprietarioConta(Label lblDetalhesProprietarioConta) {
        this.lblDetalhesProprietarioConta = lblDetalhesProprietarioConta;
    }

    public Label getLblDetalhesContaId() {
        return lblDetalhesContaId;
    }

    public void setLblDetalhesContaId(Label lblDetalhesContaId) {
        this.lblDetalhesContaId = lblDetalhesContaId;
    }

    public HBox getHboxDetalhesSaldoConta() {
        return hboxDetalhesSaldoConta;
    }

    public void setHboxDetalhesSaldoConta(HBox hboxDetalhesSaldoConta) {
        this.hboxDetalhesSaldoConta = hboxDetalhesSaldoConta;
    }

    public Label getLblDetalhesSaldoContaVlr() {
        return lblDetalhesSaldoContaVlr;
    }

    public void setLblDetalhesSaldoContaVlr(Label lblDetalhesSaldoContaVlr) {
        this.lblDetalhesSaldoContaVlr = lblDetalhesSaldoContaVlr;
    }

    public Label getLblDetalhesSaldoContaCifrao() {
        return lblDetalhesSaldoContaCifrao;
    }

    public void setLblDetalhesSaldoContaCifrao(Label lblDetalhesSaldoContaCifrao) {
        this.lblDetalhesSaldoContaCifrao = lblDetalhesSaldoContaCifrao;
    }

    public Label getLblDetalhesSaldoInicial() {
        return lblDetalhesSaldoInicial;
    }

    public void setLblDetalhesSaldoInicial(Label lblDetalhesSaldoInicial) {
        this.lblDetalhesSaldoInicial = lblDetalhesSaldoInicial;
    }

    public Label getLblDetalhesTotalIn() {
        return lblDetalhesTotalIn;
    }

    public void setLblDetalhesTotalIn(Label lblDetalhesTotalIn) {
        this.lblDetalhesTotalIn = lblDetalhesTotalIn;
    }

    public Label getLblDetalhesTotalOut() {
        return lblDetalhesTotalOut;
    }

    public void setLblDetalhesTotalOut(Label lblDetalhesTotalOut) {
        this.lblDetalhesTotalOut = lblDetalhesTotalOut;
    }

    public Label getLblDetalhesSaldoFinal() {
        return lblDetalhesSaldoFinal;
    }

    public void setLblDetalhesSaldoFinal(Label lblDetalhesSaldoFinal) {
        this.lblDetalhesSaldoFinal = lblDetalhesSaldoFinal;
    }

    public TitledPane getTpn_Negociacao() {
        return tpn_Negociacao;
    }

    public void setTpn_Negociacao(TitledPane tpn_Negociacao) {
        this.tpn_Negociacao = tpn_Negociacao;
    }

    public ComboBox<ROBOS> getCboNegociacaoRobos() {
        return cboNegociacaoRobos;
    }

    public void setCboNegociacaoRobos(ComboBox<ROBOS> cboNegociacaoRobos) {
        this.cboNegociacaoRobos = cboNegociacaoRobos;
    }

    public Label getLblNegociacaoParametros() {
        return lblNegociacaoParametros;
    }

    public void setLblNegociacaoParametros(Label lblNegociacaoParametros) {
        this.lblNegociacaoParametros = lblNegociacaoParametros;
    }

    public ComboBox<Integer> getCboTpnNegociacaoQtdCandlesAnalise() {
        return cboTpnNegociacaoQtdCandlesAnalise;
    }

    public void setCboTpnNegociacaoQtdCandlesAnalise(ComboBox<Integer> cboTpnNegociacaoQtdCandlesAnalise) {
        this.cboTpnNegociacaoQtdCandlesAnalise = cboTpnNegociacaoQtdCandlesAnalise;
    }

    public Label getLblTpnNegociacaoDtHoraInicial() {
        return lblTpnNegociacaoDtHoraInicial;
    }

    public void setLblTpnNegociacaoDtHoraInicial(Label lblTpnNegociacaoDtHoraInicial) {
        this.lblTpnNegociacaoDtHoraInicial = lblTpnNegociacaoDtHoraInicial;
    }

    public Label getLblTpnNegociacaoDtHoraAtual() {
        return lblTpnNegociacaoDtHoraAtual;
    }

    public void setLblTpnNegociacaoDtHoraAtual(Label lblTpnNegociacaoDtHoraAtual) {
        this.lblTpnNegociacaoDtHoraAtual = lblTpnNegociacaoDtHoraAtual;
    }

    public Label getLblTpnNegociacaoTempoUso() {
        return lblTpnNegociacaoTempoUso;
    }

    public void setLblTpnNegociacaoTempoUso(Label lblTpnNegociacaoTempoUso) {
        this.lblTpnNegociacaoTempoUso = lblTpnNegociacaoTempoUso;
    }

    public JFXButton getBtnTpnNegociacao_Contratos() {
        return btnTpnNegociacao_Contratos;
    }

    public void setBtnTpnNegociacao_Contratos(JFXButton btnTpnNegociacao_Contratos) {
        this.btnTpnNegociacao_Contratos = btnTpnNegociacao_Contratos;
    }

    public JFXButton getBtnTpnNegociacao_Iniciar() {
        return btnTpnNegociacao_Iniciar;
    }

    public void setBtnTpnNegociacao_Iniciar(JFXButton btnTpnNegociacao_Iniciar) {
        this.btnTpnNegociacao_Iniciar = btnTpnNegociacao_Iniciar;
    }

    public JFXButton getBtnTpnNegociacao_Pausar() {
        return btnTpnNegociacao_Pausar;
    }

    public void setBtnTpnNegociacao_Pausar(JFXButton btnTpnNegociacao_Pausar) {
        this.btnTpnNegociacao_Pausar = btnTpnNegociacao_Pausar;
    }

    public JFXButton getBtnTpnNegociacao_Stop() {
        return btnTpnNegociacao_Stop;
    }

    public void setBtnTpnNegociacao_Stop(JFXButton btnTpnNegociacao_Stop) {
        this.btnTpnNegociacao_Stop = btnTpnNegociacao_Stop;
    }

    public TitledPane getTpn_LastTicks() {
        return tpn_LastTicks;
    }

    public void setTpn_LastTicks(TitledPane tpn_LastTicks) {
        this.tpn_LastTicks = tpn_LastTicks;
    }

    public Label getLblSymbol_01() {
        return lblSymbol_01;
    }

    public void setLblSymbol_01(Label lblSymbol_01) {
        this.lblSymbol_01 = lblSymbol_01;
    }

    public Label getLblLastTickSymbol_01() {
        return lblLastTickSymbol_01;
    }

    public void setLblLastTickSymbol_01(Label lblLastTickSymbol_01) {
        this.lblLastTickSymbol_01 = lblLastTickSymbol_01;
    }

    public Label getLblSymbol_02() {
        return lblSymbol_02;
    }

    public void setLblSymbol_02(Label lblSymbol_02) {
        this.lblSymbol_02 = lblSymbol_02;
    }

    public Label getLblLastTickSymbol_02() {
        return lblLastTickSymbol_02;
    }

    public void setLblLastTickSymbol_02(Label lblLastTickSymbol_02) {
        this.lblLastTickSymbol_02 = lblLastTickSymbol_02;
    }

    public Label getLblSymbol_03() {
        return lblSymbol_03;
    }

    public void setLblSymbol_03(Label lblSymbol_03) {
        this.lblSymbol_03 = lblSymbol_03;
    }

    public Label getLblLastTickSymbol_03() {
        return lblLastTickSymbol_03;
    }

    public void setLblLastTickSymbol_03(Label lblLastTickSymbol_03) {
        this.lblLastTickSymbol_03 = lblLastTickSymbol_03;
    }

    public Label getLblSymbol_04() {
        return lblSymbol_04;
    }

    public void setLblSymbol_04(Label lblSymbol_04) {
        this.lblSymbol_04 = lblSymbol_04;
    }

    public Label getLblLastTickSymbol_04() {
        return lblLastTickSymbol_04;
    }

    public void setLblLastTickSymbol_04(Label lblLastTickSymbol_04) {
        this.lblLastTickSymbol_04 = lblLastTickSymbol_04;
    }

    public Label getLblSymbol_05() {
        return lblSymbol_05;
    }

    public void setLblSymbol_05(Label lblSymbol_05) {
        this.lblSymbol_05 = lblSymbol_05;
    }

    public Label getLblLastTickSymbol_05() {
        return lblLastTickSymbol_05;
    }

    public void setLblLastTickSymbol_05(Label lblLastTickSymbol_05) {
        this.lblLastTickSymbol_05 = lblLastTickSymbol_05;
    }

    public Label getLblSymbol_06() {
        return lblSymbol_06;
    }

    public void setLblSymbol_06(Label lblSymbol_06) {
        this.lblSymbol_06 = lblSymbol_06;
    }

    public Label getLblLastTickSymbol_06() {
        return lblLastTickSymbol_06;
    }

    public void setLblLastTickSymbol_06(Label lblLastTickSymbol_06) {
        this.lblLastTickSymbol_06 = lblLastTickSymbol_06;
    }

    public Label getLblSymbol_07() {
        return lblSymbol_07;
    }

    public void setLblSymbol_07(Label lblSymbol_07) {
        this.lblSymbol_07 = lblSymbol_07;
    }

    public Label getLblLastTickSymbol_07() {
        return lblLastTickSymbol_07;
    }

    public void setLblLastTickSymbol_07(Label lblLastTickSymbol_07) {
        this.lblLastTickSymbol_07 = lblLastTickSymbol_07;
    }

    public Label getLblSymbol_08() {
        return lblSymbol_08;
    }

    public void setLblSymbol_08(Label lblSymbol_08) {
        this.lblSymbol_08 = lblSymbol_08;
    }

    public Label getLblLastTickSymbol_08() {
        return lblLastTickSymbol_08;
    }

    public void setLblLastTickSymbol_08(Label lblLastTickSymbol_08) {
        this.lblLastTickSymbol_08 = lblLastTickSymbol_08;
    }

    public Label getLblSymbol_09() {
        return lblSymbol_09;
    }

    public void setLblSymbol_09(Label lblSymbol_09) {
        this.lblSymbol_09 = lblSymbol_09;
    }

    public Label getLblLastTickSymbol_09() {
        return lblLastTickSymbol_09;
    }

    public void setLblLastTickSymbol_09(Label lblLastTickSymbol_09) {
        this.lblLastTickSymbol_09 = lblLastTickSymbol_09;
    }

    public Label getLblSymbol_10() {
        return lblSymbol_10;
    }

    public void setLblSymbol_10(Label lblSymbol_10) {
        this.lblSymbol_10 = lblSymbol_10;
    }

    public Label getLblLastTickSymbol_10() {
        return lblLastTickSymbol_10;
    }

    public void setLblLastTickSymbol_10(Label lblLastTickSymbol_10) {
        this.lblLastTickSymbol_10 = lblLastTickSymbol_10;
    }

    public Label getLblSymbol_11() {
        return lblSymbol_11;
    }

    public void setLblSymbol_11(Label lblSymbol_11) {
        this.lblSymbol_11 = lblSymbol_11;
    }

    public Label getLblLastTickSymbol_11() {
        return lblLastTickSymbol_11;
    }

    public void setLblLastTickSymbol_11(Label lblLastTickSymbol_11) {
        this.lblLastTickSymbol_11 = lblLastTickSymbol_11;
    }

    public Label getLblSymbol_12() {
        return lblSymbol_12;
    }

    public void setLblSymbol_12(Label lblSymbol_12) {
        this.lblSymbol_12 = lblSymbol_12;
    }

    public Label getLblLastTickSymbol_12() {
        return lblLastTickSymbol_12;
    }

    public void setLblLastTickSymbol_12(Label lblLastTickSymbol_12) {
        this.lblLastTickSymbol_12 = lblLastTickSymbol_12;
    }

    public TitledPane getTpn_T01() {
        return tpn_T01;
    }

    public void setTpn_T01(TitledPane tpn_T01) {
        this.tpn_T01 = tpn_T01;
    }

    public JFXCheckBox getChkTpn01_TimeAtivo() {
        return chkTpn01_TimeAtivo;
    }

    public void setChkTpn01_TimeAtivo(JFXCheckBox chkTpn01_TimeAtivo) {
        this.chkTpn01_TimeAtivo = chkTpn01_TimeAtivo;
    }

    public Label getLblTpnT01_CandleTimeStart() {
        return lblTpnT01_CandleTimeStart;
    }

    public void setLblTpnT01_CandleTimeStart(Label lblTpnT01_CandleTimeStart) {
        this.lblTpnT01_CandleTimeStart = lblTpnT01_CandleTimeStart;
    }

    public Label getLblTpnT01_TimeEnd() {
        return lblTpnT01_TimeEnd;
    }

    public void setLblTpnT01_TimeEnd(Label lblTpnT01_TimeEnd) {
        this.lblTpnT01_TimeEnd = lblTpnT01_TimeEnd;
    }

    public Label getLblTpnT01_QtdStakes() {
        return lblTpnT01_QtdStakes;
    }

    public void setLblTpnT01_QtdStakes(Label lblTpnT01_QtdStakes) {
        this.lblTpnT01_QtdStakes = lblTpnT01_QtdStakes;
    }

    public Label getLblTpnT01_QtdWins() {
        return lblTpnT01_QtdWins;
    }

    public void setLblTpnT01_QtdWins(Label lblTpnT01_QtdWins) {
        this.lblTpnT01_QtdWins = lblTpnT01_QtdWins;
    }

    public Label getLblTpnT01_QtdLoss() {
        return lblTpnT01_QtdLoss;
    }

    public void setLblTpnT01_QtdLoss(Label lblTpnT01_QtdLoss) {
        this.lblTpnT01_QtdLoss = lblTpnT01_QtdLoss;
    }

    public Label getLblTpnT01_VlrIn() {
        return lblTpnT01_VlrIn;
    }

    public void setLblTpnT01_VlrIn(Label lblTpnT01_VlrIn) {
        this.lblTpnT01_VlrIn = lblTpnT01_VlrIn;
    }

    public Label getLblTpnT01_VlrOut() {
        return lblTpnT01_VlrOut;
    }

    public void setLblTpnT01_VlrOut(Label lblTpnT01_VlrOut) {
        this.lblTpnT01_VlrOut = lblTpnT01_VlrOut;
    }

    public Label getLblTpnT01_VlrDiff() {
        return lblTpnT01_VlrDiff;
    }

    public void setLblTpnT01_VlrDiff(Label lblTpnT01_VlrDiff) {
        this.lblTpnT01_VlrDiff = lblTpnT01_VlrDiff;
    }

    public Label getLblSymbol_T01_Op01() {
        return lblSymbol_T01_Op01;
    }

    public void setLblSymbol_T01_Op01(Label lblSymbol_T01_Op01) {
        this.lblSymbol_T01_Op01 = lblSymbol_T01_Op01;
    }

    public Label getLblQtdCall_T01_Op01() {
        return lblQtdCall_T01_Op01;
    }

    public void setLblQtdCall_T01_Op01(Label lblQtdCall_T01_Op01) {
        this.lblQtdCall_T01_Op01 = lblQtdCall_T01_Op01;
    }

    public Label getLblQtdPut_T01_Op01() {
        return lblQtdPut_T01_Op01;
    }

    public void setLblQtdPut_T01_Op01(Label lblQtdPut_T01_Op01) {
        this.lblQtdPut_T01_Op01 = lblQtdPut_T01_Op01;
    }

    public Label getLblQtdCallOrPut_T01_Op01() {
        return lblQtdCallOrPut_T01_Op01;
    }

    public void setLblQtdCallOrPut_T01_Op01(Label lblQtdCallOrPut_T01_Op01) {
        this.lblQtdCallOrPut_T01_Op01 = lblQtdCallOrPut_T01_Op01;
    }

    public ImageView getImgCallOrPut_T01_Op01() {
        return imgCallOrPut_T01_Op01;
    }

    public void setImgCallOrPut_T01_Op01(ImageView imgCallOrPut_T01_Op01) {
        this.imgCallOrPut_T01_Op01 = imgCallOrPut_T01_Op01;
    }

    public Label getLblQtdStakes_T01_Op01() {
        return lblQtdStakes_T01_Op01;
    }

    public void setLblQtdStakes_T01_Op01(Label lblQtdStakes_T01_Op01) {
        this.lblQtdStakes_T01_Op01 = lblQtdStakes_T01_Op01;
    }

    public Label getLblQtdWins_T01_Op01() {
        return lblQtdWins_T01_Op01;
    }

    public void setLblQtdWins_T01_Op01(Label lblQtdWins_T01_Op01) {
        this.lblQtdWins_T01_Op01 = lblQtdWins_T01_Op01;
    }

    public Label getLblQtdLoss_T01_Op01() {
        return lblQtdLoss_T01_Op01;
    }

    public void setLblQtdLoss_T01_Op01(Label lblQtdLoss_T01_Op01) {
        this.lblQtdLoss_T01_Op01 = lblQtdLoss_T01_Op01;
    }

    public Label getLblVlrIn_T01_Op01() {
        return lblVlrIn_T01_Op01;
    }

    public void setLblVlrIn_T01_Op01(Label lblVlrIn_T01_Op01) {
        this.lblVlrIn_T01_Op01 = lblVlrIn_T01_Op01;
    }

    public Label getLblVlrOut_T01_Op01() {
        return lblVlrOut_T01_Op01;
    }

    public void setLblVlrOut_T01_Op01(Label lblVlrOut_T01_Op01) {
        this.lblVlrOut_T01_Op01 = lblVlrOut_T01_Op01;
    }

    public Label getLblVlrDiff_T01_Op01() {
        return lblVlrDiff_T01_Op01;
    }

    public void setLblVlrDiff_T01_Op01(Label lblVlrDiff_T01_Op01) {
        this.lblVlrDiff_T01_Op01 = lblVlrDiff_T01_Op01;
    }

    public TableView getTbvTransacoes_T01_Op01() {
        return tbvTransacoes_T01_Op01;
    }

    public void setTbvTransacoes_T01_Op01(TableView tbvTransacoes_T01_Op01) {
        this.tbvTransacoes_T01_Op01 = tbvTransacoes_T01_Op01;
    }

    public Label getLblSymbol_T01_Op02() {
        return lblSymbol_T01_Op02;
    }

    public void setLblSymbol_T01_Op02(Label lblSymbol_T01_Op02) {
        this.lblSymbol_T01_Op02 = lblSymbol_T01_Op02;
    }

    public Label getLblQtdCall_T01_Op02() {
        return lblQtdCall_T01_Op02;
    }

    public void setLblQtdCall_T01_Op02(Label lblQtdCall_T01_Op02) {
        this.lblQtdCall_T01_Op02 = lblQtdCall_T01_Op02;
    }

    public Label getLblQtdPut_T01_Op02() {
        return lblQtdPut_T01_Op02;
    }

    public void setLblQtdPut_T01_Op02(Label lblQtdPut_T01_Op02) {
        this.lblQtdPut_T01_Op02 = lblQtdPut_T01_Op02;
    }

    public Label getLblQtdCallOrPut_T01_Op02() {
        return lblQtdCallOrPut_T01_Op02;
    }

    public void setLblQtdCallOrPut_T01_Op02(Label lblQtdCallOrPut_T01_Op02) {
        this.lblQtdCallOrPut_T01_Op02 = lblQtdCallOrPut_T01_Op02;
    }

    public ImageView getImgCallOrPut_T01_Op02() {
        return imgCallOrPut_T01_Op02;
    }

    public void setImgCallOrPut_T01_Op02(ImageView imgCallOrPut_T01_Op02) {
        this.imgCallOrPut_T01_Op02 = imgCallOrPut_T01_Op02;
    }

    public Label getLblQtdStakes_T01_Op02() {
        return lblQtdStakes_T01_Op02;
    }

    public void setLblQtdStakes_T01_Op02(Label lblQtdStakes_T01_Op02) {
        this.lblQtdStakes_T01_Op02 = lblQtdStakes_T01_Op02;
    }

    public Label getLblQtdWins_T01_Op02() {
        return lblQtdWins_T01_Op02;
    }

    public void setLblQtdWins_T01_Op02(Label lblQtdWins_T01_Op02) {
        this.lblQtdWins_T01_Op02 = lblQtdWins_T01_Op02;
    }

    public Label getLblQtdLoss_T01_Op02() {
        return lblQtdLoss_T01_Op02;
    }

    public void setLblQtdLoss_T01_Op02(Label lblQtdLoss_T01_Op02) {
        this.lblQtdLoss_T01_Op02 = lblQtdLoss_T01_Op02;
    }

    public Label getLblVlrIn_T01_Op02() {
        return lblVlrIn_T01_Op02;
    }

    public void setLblVlrIn_T01_Op02(Label lblVlrIn_T01_Op02) {
        this.lblVlrIn_T01_Op02 = lblVlrIn_T01_Op02;
    }

    public Label getLblVlrOut_T01_Op02() {
        return lblVlrOut_T01_Op02;
    }

    public void setLblVlrOut_T01_Op02(Label lblVlrOut_T01_Op02) {
        this.lblVlrOut_T01_Op02 = lblVlrOut_T01_Op02;
    }

    public Label getLblVlrDiff_T01_Op02() {
        return lblVlrDiff_T01_Op02;
    }

    public void setLblVlrDiff_T01_Op02(Label lblVlrDiff_T01_Op02) {
        this.lblVlrDiff_T01_Op02 = lblVlrDiff_T01_Op02;
    }

    public TableView getTbvTransacoes_T01_Op02() {
        return tbvTransacoes_T01_Op02;
    }

    public void setTbvTransacoes_T01_Op02(TableView tbvTransacoes_T01_Op02) {
        this.tbvTransacoes_T01_Op02 = tbvTransacoes_T01_Op02;
    }

    public Label getLblSymbol_T01_Op03() {
        return lblSymbol_T01_Op03;
    }

    public void setLblSymbol_T01_Op03(Label lblSymbol_T01_Op03) {
        this.lblSymbol_T01_Op03 = lblSymbol_T01_Op03;
    }

    public Label getLblQtdCall_T01_Op03() {
        return lblQtdCall_T01_Op03;
    }

    public void setLblQtdCall_T01_Op03(Label lblQtdCall_T01_Op03) {
        this.lblQtdCall_T01_Op03 = lblQtdCall_T01_Op03;
    }

    public Label getLblQtdPut_T01_Op03() {
        return lblQtdPut_T01_Op03;
    }

    public void setLblQtdPut_T01_Op03(Label lblQtdPut_T01_Op03) {
        this.lblQtdPut_T01_Op03 = lblQtdPut_T01_Op03;
    }

    public Label getLblQtdCallOrPut_T01_Op03() {
        return lblQtdCallOrPut_T01_Op03;
    }

    public void setLblQtdCallOrPut_T01_Op03(Label lblQtdCallOrPut_T01_Op03) {
        this.lblQtdCallOrPut_T01_Op03 = lblQtdCallOrPut_T01_Op03;
    }

    public ImageView getImgCallOrPut_T01_Op03() {
        return imgCallOrPut_T01_Op03;
    }

    public void setImgCallOrPut_T01_Op03(ImageView imgCallOrPut_T01_Op03) {
        this.imgCallOrPut_T01_Op03 = imgCallOrPut_T01_Op03;
    }

    public Label getLblQtdStakes_T01_Op03() {
        return lblQtdStakes_T01_Op03;
    }

    public void setLblQtdStakes_T01_Op03(Label lblQtdStakes_T01_Op03) {
        this.lblQtdStakes_T01_Op03 = lblQtdStakes_T01_Op03;
    }

    public Label getLblQtdWins_T01_Op03() {
        return lblQtdWins_T01_Op03;
    }

    public void setLblQtdWins_T01_Op03(Label lblQtdWins_T01_Op03) {
        this.lblQtdWins_T01_Op03 = lblQtdWins_T01_Op03;
    }

    public Label getLblQtdLoss_T01_Op03() {
        return lblQtdLoss_T01_Op03;
    }

    public void setLblQtdLoss_T01_Op03(Label lblQtdLoss_T01_Op03) {
        this.lblQtdLoss_T01_Op03 = lblQtdLoss_T01_Op03;
    }

    public Label getLblVlrIn_T01_Op03() {
        return lblVlrIn_T01_Op03;
    }

    public void setLblVlrIn_T01_Op03(Label lblVlrIn_T01_Op03) {
        this.lblVlrIn_T01_Op03 = lblVlrIn_T01_Op03;
    }

    public Label getLblVlrOut_T01_Op03() {
        return lblVlrOut_T01_Op03;
    }

    public void setLblVlrOut_T01_Op03(Label lblVlrOut_T01_Op03) {
        this.lblVlrOut_T01_Op03 = lblVlrOut_T01_Op03;
    }

    public Label getLblVlrDiff_T01_Op03() {
        return lblVlrDiff_T01_Op03;
    }

    public void setLblVlrDiff_T01_Op03(Label lblVlrDiff_T01_Op03) {
        this.lblVlrDiff_T01_Op03 = lblVlrDiff_T01_Op03;
    }

    public TableView getTbvTransacoes_T01_Op03() {
        return tbvTransacoes_T01_Op03;
    }

    public void setTbvTransacoes_T01_Op03(TableView tbvTransacoes_T01_Op03) {
        this.tbvTransacoes_T01_Op03 = tbvTransacoes_T01_Op03;
    }

    public Label getLblSymbol_T01_Op04() {
        return lblSymbol_T01_Op04;
    }

    public void setLblSymbol_T01_Op04(Label lblSymbol_T01_Op04) {
        this.lblSymbol_T01_Op04 = lblSymbol_T01_Op04;
    }

    public Label getLblQtdCall_T01_Op04() {
        return lblQtdCall_T01_Op04;
    }

    public void setLblQtdCall_T01_Op04(Label lblQtdCall_T01_Op04) {
        this.lblQtdCall_T01_Op04 = lblQtdCall_T01_Op04;
    }

    public Label getLblQtdPut_T01_Op04() {
        return lblQtdPut_T01_Op04;
    }

    public void setLblQtdPut_T01_Op04(Label lblQtdPut_T01_Op04) {
        this.lblQtdPut_T01_Op04 = lblQtdPut_T01_Op04;
    }

    public Label getLblQtdCallOrPut_T01_Op04() {
        return lblQtdCallOrPut_T01_Op04;
    }

    public void setLblQtdCallOrPut_T01_Op04(Label lblQtdCallOrPut_T01_Op04) {
        this.lblQtdCallOrPut_T01_Op04 = lblQtdCallOrPut_T01_Op04;
    }

    public ImageView getImgCallOrPut_T01_Op04() {
        return imgCallOrPut_T01_Op04;
    }

    public void setImgCallOrPut_T01_Op04(ImageView imgCallOrPut_T01_Op04) {
        this.imgCallOrPut_T01_Op04 = imgCallOrPut_T01_Op04;
    }

    public Label getLblQtdStakes_T01_Op04() {
        return lblQtdStakes_T01_Op04;
    }

    public void setLblQtdStakes_T01_Op04(Label lblQtdStakes_T01_Op04) {
        this.lblQtdStakes_T01_Op04 = lblQtdStakes_T01_Op04;
    }

    public Label getLblQtdWins_T01_Op04() {
        return lblQtdWins_T01_Op04;
    }

    public void setLblQtdWins_T01_Op04(Label lblQtdWins_T01_Op04) {
        this.lblQtdWins_T01_Op04 = lblQtdWins_T01_Op04;
    }

    public Label getLblQtdLoss_T01_Op04() {
        return lblQtdLoss_T01_Op04;
    }

    public void setLblQtdLoss_T01_Op04(Label lblQtdLoss_T01_Op04) {
        this.lblQtdLoss_T01_Op04 = lblQtdLoss_T01_Op04;
    }

    public Label getLblVlrIn_T01_Op04() {
        return lblVlrIn_T01_Op04;
    }

    public void setLblVlrIn_T01_Op04(Label lblVlrIn_T01_Op04) {
        this.lblVlrIn_T01_Op04 = lblVlrIn_T01_Op04;
    }

    public Label getLblVlrOut_T01_Op04() {
        return lblVlrOut_T01_Op04;
    }

    public void setLblVlrOut_T01_Op04(Label lblVlrOut_T01_Op04) {
        this.lblVlrOut_T01_Op04 = lblVlrOut_T01_Op04;
    }

    public Label getLblVlrDiff_T01_Op04() {
        return lblVlrDiff_T01_Op04;
    }

    public void setLblVlrDiff_T01_Op04(Label lblVlrDiff_T01_Op04) {
        this.lblVlrDiff_T01_Op04 = lblVlrDiff_T01_Op04;
    }

    public TableView getTbvTransacoes_T01_Op04() {
        return tbvTransacoes_T01_Op04;
    }

    public void setTbvTransacoes_T01_Op04(TableView tbvTransacoes_T01_Op04) {
        this.tbvTransacoes_T01_Op04 = tbvTransacoes_T01_Op04;
    }

    public Label getLblSymbol_T01_Op05() {
        return lblSymbol_T01_Op05;
    }

    public void setLblSymbol_T01_Op05(Label lblSymbol_T01_Op05) {
        this.lblSymbol_T01_Op05 = lblSymbol_T01_Op05;
    }

    public Label getLblQtdCall_T01_Op05() {
        return lblQtdCall_T01_Op05;
    }

    public void setLblQtdCall_T01_Op05(Label lblQtdCall_T01_Op05) {
        this.lblQtdCall_T01_Op05 = lblQtdCall_T01_Op05;
    }

    public Label getLblQtdPut_T01_Op05() {
        return lblQtdPut_T01_Op05;
    }

    public void setLblQtdPut_T01_Op05(Label lblQtdPut_T01_Op05) {
        this.lblQtdPut_T01_Op05 = lblQtdPut_T01_Op05;
    }

    public Label getLblQtdCallOrPut_T01_Op05() {
        return lblQtdCallOrPut_T01_Op05;
    }

    public void setLblQtdCallOrPut_T01_Op05(Label lblQtdCallOrPut_T01_Op05) {
        this.lblQtdCallOrPut_T01_Op05 = lblQtdCallOrPut_T01_Op05;
    }

    public ImageView getImgCallOrPut_T01_Op05() {
        return imgCallOrPut_T01_Op05;
    }

    public void setImgCallOrPut_T01_Op05(ImageView imgCallOrPut_T01_Op05) {
        this.imgCallOrPut_T01_Op05 = imgCallOrPut_T01_Op05;
    }

    public Label getLblQtdStakes_T01_Op05() {
        return lblQtdStakes_T01_Op05;
    }

    public void setLblQtdStakes_T01_Op05(Label lblQtdStakes_T01_Op05) {
        this.lblQtdStakes_T01_Op05 = lblQtdStakes_T01_Op05;
    }

    public Label getLblQtdWins_T01_Op05() {
        return lblQtdWins_T01_Op05;
    }

    public void setLblQtdWins_T01_Op05(Label lblQtdWins_T01_Op05) {
        this.lblQtdWins_T01_Op05 = lblQtdWins_T01_Op05;
    }

    public Label getLblQtdLoss_T01_Op05() {
        return lblQtdLoss_T01_Op05;
    }

    public void setLblQtdLoss_T01_Op05(Label lblQtdLoss_T01_Op05) {
        this.lblQtdLoss_T01_Op05 = lblQtdLoss_T01_Op05;
    }

    public Label getLblVlrIn_T01_Op05() {
        return lblVlrIn_T01_Op05;
    }

    public void setLblVlrIn_T01_Op05(Label lblVlrIn_T01_Op05) {
        this.lblVlrIn_T01_Op05 = lblVlrIn_T01_Op05;
    }

    public Label getLblVlrOut_T01_Op05() {
        return lblVlrOut_T01_Op05;
    }

    public void setLblVlrOut_T01_Op05(Label lblVlrOut_T01_Op05) {
        this.lblVlrOut_T01_Op05 = lblVlrOut_T01_Op05;
    }

    public Label getLblVlrDiff_T01_Op05() {
        return lblVlrDiff_T01_Op05;
    }

    public void setLblVlrDiff_T01_Op05(Label lblVlrDiff_T01_Op05) {
        this.lblVlrDiff_T01_Op05 = lblVlrDiff_T01_Op05;
    }

    public TableView getTbvTransacoes_T01_Op05() {
        return tbvTransacoes_T01_Op05;
    }

    public void setTbvTransacoes_T01_Op05(TableView tbvTransacoes_T01_Op05) {
        this.tbvTransacoes_T01_Op05 = tbvTransacoes_T01_Op05;
    }

    public Label getLblSymbol_T01_Op06() {
        return lblSymbol_T01_Op06;
    }

    public void setLblSymbol_T01_Op06(Label lblSymbol_T01_Op06) {
        this.lblSymbol_T01_Op06 = lblSymbol_T01_Op06;
    }

    public Label getLblQtdCall_T01_Op06() {
        return lblQtdCall_T01_Op06;
    }

    public void setLblQtdCall_T01_Op06(Label lblQtdCall_T01_Op06) {
        this.lblQtdCall_T01_Op06 = lblQtdCall_T01_Op06;
    }

    public Label getLblQtdPut_T01_Op06() {
        return lblQtdPut_T01_Op06;
    }

    public void setLblQtdPut_T01_Op06(Label lblQtdPut_T01_Op06) {
        this.lblQtdPut_T01_Op06 = lblQtdPut_T01_Op06;
    }

    public Label getLblQtdCallOrPut_T01_Op06() {
        return lblQtdCallOrPut_T01_Op06;
    }

    public void setLblQtdCallOrPut_T01_Op06(Label lblQtdCallOrPut_T01_Op06) {
        this.lblQtdCallOrPut_T01_Op06 = lblQtdCallOrPut_T01_Op06;
    }

    public ImageView getImgCallOrPut_T01_Op06() {
        return imgCallOrPut_T01_Op06;
    }

    public void setImgCallOrPut_T01_Op06(ImageView imgCallOrPut_T01_Op06) {
        this.imgCallOrPut_T01_Op06 = imgCallOrPut_T01_Op06;
    }

    public Label getLblQtdStakes_T01_Op06() {
        return lblQtdStakes_T01_Op06;
    }

    public void setLblQtdStakes_T01_Op06(Label lblQtdStakes_T01_Op06) {
        this.lblQtdStakes_T01_Op06 = lblQtdStakes_T01_Op06;
    }

    public Label getLblQtdWins_T01_Op06() {
        return lblQtdWins_T01_Op06;
    }

    public void setLblQtdWins_T01_Op06(Label lblQtdWins_T01_Op06) {
        this.lblQtdWins_T01_Op06 = lblQtdWins_T01_Op06;
    }

    public Label getLblQtdLoss_T01_Op06() {
        return lblQtdLoss_T01_Op06;
    }

    public void setLblQtdLoss_T01_Op06(Label lblQtdLoss_T01_Op06) {
        this.lblQtdLoss_T01_Op06 = lblQtdLoss_T01_Op06;
    }

    public Label getLblVlrIn_T01_Op06() {
        return lblVlrIn_T01_Op06;
    }

    public void setLblVlrIn_T01_Op06(Label lblVlrIn_T01_Op06) {
        this.lblVlrIn_T01_Op06 = lblVlrIn_T01_Op06;
    }

    public Label getLblVlrOut_T01_Op06() {
        return lblVlrOut_T01_Op06;
    }

    public void setLblVlrOut_T01_Op06(Label lblVlrOut_T01_Op06) {
        this.lblVlrOut_T01_Op06 = lblVlrOut_T01_Op06;
    }

    public Label getLblVlrDiff_T01_Op06() {
        return lblVlrDiff_T01_Op06;
    }

    public void setLblVlrDiff_T01_Op06(Label lblVlrDiff_T01_Op06) {
        this.lblVlrDiff_T01_Op06 = lblVlrDiff_T01_Op06;
    }

    public TableView getTbvTransacoes_T01_Op06() {
        return tbvTransacoes_T01_Op06;
    }

    public void setTbvTransacoes_T01_Op06(TableView tbvTransacoes_T01_Op06) {
        this.tbvTransacoes_T01_Op06 = tbvTransacoes_T01_Op06;
    }

    public Label getLblSymbol_T01_Op07() {
        return lblSymbol_T01_Op07;
    }

    public void setLblSymbol_T01_Op07(Label lblSymbol_T01_Op07) {
        this.lblSymbol_T01_Op07 = lblSymbol_T01_Op07;
    }

    public Label getLblQtdCall_T01_Op07() {
        return lblQtdCall_T01_Op07;
    }

    public void setLblQtdCall_T01_Op07(Label lblQtdCall_T01_Op07) {
        this.lblQtdCall_T01_Op07 = lblQtdCall_T01_Op07;
    }

    public Label getLblQtdPut_T01_Op07() {
        return lblQtdPut_T01_Op07;
    }

    public void setLblQtdPut_T01_Op07(Label lblQtdPut_T01_Op07) {
        this.lblQtdPut_T01_Op07 = lblQtdPut_T01_Op07;
    }

    public Label getLblQtdCallOrPut_T01_Op07() {
        return lblQtdCallOrPut_T01_Op07;
    }

    public void setLblQtdCallOrPut_T01_Op07(Label lblQtdCallOrPut_T01_Op07) {
        this.lblQtdCallOrPut_T01_Op07 = lblQtdCallOrPut_T01_Op07;
    }

    public ImageView getImgCallOrPut_T01_Op07() {
        return imgCallOrPut_T01_Op07;
    }

    public void setImgCallOrPut_T01_Op07(ImageView imgCallOrPut_T01_Op07) {
        this.imgCallOrPut_T01_Op07 = imgCallOrPut_T01_Op07;
    }

    public Label getLblQtdStakes_T01_Op07() {
        return lblQtdStakes_T01_Op07;
    }

    public void setLblQtdStakes_T01_Op07(Label lblQtdStakes_T01_Op07) {
        this.lblQtdStakes_T01_Op07 = lblQtdStakes_T01_Op07;
    }

    public Label getLblQtdWins_T01_Op07() {
        return lblQtdWins_T01_Op07;
    }

    public void setLblQtdWins_T01_Op07(Label lblQtdWins_T01_Op07) {
        this.lblQtdWins_T01_Op07 = lblQtdWins_T01_Op07;
    }

    public Label getLblQtdLoss_T01_Op07() {
        return lblQtdLoss_T01_Op07;
    }

    public void setLblQtdLoss_T01_Op07(Label lblQtdLoss_T01_Op07) {
        this.lblQtdLoss_T01_Op07 = lblQtdLoss_T01_Op07;
    }

    public Label getLblVlrIn_T01_Op07() {
        return lblVlrIn_T01_Op07;
    }

    public void setLblVlrIn_T01_Op07(Label lblVlrIn_T01_Op07) {
        this.lblVlrIn_T01_Op07 = lblVlrIn_T01_Op07;
    }

    public Label getLblVlrOut_T01_Op07() {
        return lblVlrOut_T01_Op07;
    }

    public void setLblVlrOut_T01_Op07(Label lblVlrOut_T01_Op07) {
        this.lblVlrOut_T01_Op07 = lblVlrOut_T01_Op07;
    }

    public Label getLblVlrDiff_T01_Op07() {
        return lblVlrDiff_T01_Op07;
    }

    public void setLblVlrDiff_T01_Op07(Label lblVlrDiff_T01_Op07) {
        this.lblVlrDiff_T01_Op07 = lblVlrDiff_T01_Op07;
    }

    public TableView getTbvTransacoes_T01_Op07() {
        return tbvTransacoes_T01_Op07;
    }

    public void setTbvTransacoes_T01_Op07(TableView tbvTransacoes_T01_Op07) {
        this.tbvTransacoes_T01_Op07 = tbvTransacoes_T01_Op07;
    }

    public Label getLblSymbol_T01_Op08() {
        return lblSymbol_T01_Op08;
    }

    public void setLblSymbol_T01_Op08(Label lblSymbol_T01_Op08) {
        this.lblSymbol_T01_Op08 = lblSymbol_T01_Op08;
    }

    public Label getLblQtdCall_T01_Op08() {
        return lblQtdCall_T01_Op08;
    }

    public void setLblQtdCall_T01_Op08(Label lblQtdCall_T01_Op08) {
        this.lblQtdCall_T01_Op08 = lblQtdCall_T01_Op08;
    }

    public Label getLblQtdPut_T01_Op08() {
        return lblQtdPut_T01_Op08;
    }

    public void setLblQtdPut_T01_Op08(Label lblQtdPut_T01_Op08) {
        this.lblQtdPut_T01_Op08 = lblQtdPut_T01_Op08;
    }

    public Label getLblQtdCallOrPut_T01_Op08() {
        return lblQtdCallOrPut_T01_Op08;
    }

    public void setLblQtdCallOrPut_T01_Op08(Label lblQtdCallOrPut_T01_Op08) {
        this.lblQtdCallOrPut_T01_Op08 = lblQtdCallOrPut_T01_Op08;
    }

    public ImageView getImgCallOrPut_T01_Op08() {
        return imgCallOrPut_T01_Op08;
    }

    public void setImgCallOrPut_T01_Op08(ImageView imgCallOrPut_T01_Op08) {
        this.imgCallOrPut_T01_Op08 = imgCallOrPut_T01_Op08;
    }

    public Label getLblQtdStakes_T01_Op08() {
        return lblQtdStakes_T01_Op08;
    }

    public void setLblQtdStakes_T01_Op08(Label lblQtdStakes_T01_Op08) {
        this.lblQtdStakes_T01_Op08 = lblQtdStakes_T01_Op08;
    }

    public Label getLblQtdWins_T01_Op08() {
        return lblQtdWins_T01_Op08;
    }

    public void setLblQtdWins_T01_Op08(Label lblQtdWins_T01_Op08) {
        this.lblQtdWins_T01_Op08 = lblQtdWins_T01_Op08;
    }

    public Label getLblQtdLoss_T01_Op08() {
        return lblQtdLoss_T01_Op08;
    }

    public void setLblQtdLoss_T01_Op08(Label lblQtdLoss_T01_Op08) {
        this.lblQtdLoss_T01_Op08 = lblQtdLoss_T01_Op08;
    }

    public Label getLblVlrIn_T01_Op08() {
        return lblVlrIn_T01_Op08;
    }

    public void setLblVlrIn_T01_Op08(Label lblVlrIn_T01_Op08) {
        this.lblVlrIn_T01_Op08 = lblVlrIn_T01_Op08;
    }

    public Label getLblVlrOut_T01_Op08() {
        return lblVlrOut_T01_Op08;
    }

    public void setLblVlrOut_T01_Op08(Label lblVlrOut_T01_Op08) {
        this.lblVlrOut_T01_Op08 = lblVlrOut_T01_Op08;
    }

    public Label getLblVlrDiff_T01_Op08() {
        return lblVlrDiff_T01_Op08;
    }

    public void setLblVlrDiff_T01_Op08(Label lblVlrDiff_T01_Op08) {
        this.lblVlrDiff_T01_Op08 = lblVlrDiff_T01_Op08;
    }

    public TableView getTbvTransacoes_T01_Op08() {
        return tbvTransacoes_T01_Op08;
    }

    public void setTbvTransacoes_T01_Op08(TableView tbvTransacoes_T01_Op08) {
        this.tbvTransacoes_T01_Op08 = tbvTransacoes_T01_Op08;
    }

    public Label getLblSymbol_T01_Op09() {
        return lblSymbol_T01_Op09;
    }

    public void setLblSymbol_T01_Op09(Label lblSymbol_T01_Op09) {
        this.lblSymbol_T01_Op09 = lblSymbol_T01_Op09;
    }

    public Label getLblQtdCall_T01_Op09() {
        return lblQtdCall_T01_Op09;
    }

    public void setLblQtdCall_T01_Op09(Label lblQtdCall_T01_Op09) {
        this.lblQtdCall_T01_Op09 = lblQtdCall_T01_Op09;
    }

    public Label getLblQtdPut_T01_Op09() {
        return lblQtdPut_T01_Op09;
    }

    public void setLblQtdPut_T01_Op09(Label lblQtdPut_T01_Op09) {
        this.lblQtdPut_T01_Op09 = lblQtdPut_T01_Op09;
    }

    public Label getLblQtdCallOrPut_T01_Op09() {
        return lblQtdCallOrPut_T01_Op09;
    }

    public void setLblQtdCallOrPut_T01_Op09(Label lblQtdCallOrPut_T01_Op09) {
        this.lblQtdCallOrPut_T01_Op09 = lblQtdCallOrPut_T01_Op09;
    }

    public ImageView getImgCallOrPut_T01_Op09() {
        return imgCallOrPut_T01_Op09;
    }

    public void setImgCallOrPut_T01_Op09(ImageView imgCallOrPut_T01_Op09) {
        this.imgCallOrPut_T01_Op09 = imgCallOrPut_T01_Op09;
    }

    public Label getLblQtdStakes_T01_Op09() {
        return lblQtdStakes_T01_Op09;
    }

    public void setLblQtdStakes_T01_Op09(Label lblQtdStakes_T01_Op09) {
        this.lblQtdStakes_T01_Op09 = lblQtdStakes_T01_Op09;
    }

    public Label getLblQtdWins_T01_Op09() {
        return lblQtdWins_T01_Op09;
    }

    public void setLblQtdWins_T01_Op09(Label lblQtdWins_T01_Op09) {
        this.lblQtdWins_T01_Op09 = lblQtdWins_T01_Op09;
    }

    public Label getLblQtdLoss_T01_Op09() {
        return lblQtdLoss_T01_Op09;
    }

    public void setLblQtdLoss_T01_Op09(Label lblQtdLoss_T01_Op09) {
        this.lblQtdLoss_T01_Op09 = lblQtdLoss_T01_Op09;
    }

    public Label getLblVlrIn_T01_Op09() {
        return lblVlrIn_T01_Op09;
    }

    public void setLblVlrIn_T01_Op09(Label lblVlrIn_T01_Op09) {
        this.lblVlrIn_T01_Op09 = lblVlrIn_T01_Op09;
    }

    public Label getLblVlrOut_T01_Op09() {
        return lblVlrOut_T01_Op09;
    }

    public void setLblVlrOut_T01_Op09(Label lblVlrOut_T01_Op09) {
        this.lblVlrOut_T01_Op09 = lblVlrOut_T01_Op09;
    }

    public Label getLblVlrDiff_T01_Op09() {
        return lblVlrDiff_T01_Op09;
    }

    public void setLblVlrDiff_T01_Op09(Label lblVlrDiff_T01_Op09) {
        this.lblVlrDiff_T01_Op09 = lblVlrDiff_T01_Op09;
    }

    public TableView getTbvTransacoes_T01_Op09() {
        return tbvTransacoes_T01_Op09;
    }

    public void setTbvTransacoes_T01_Op09(TableView tbvTransacoes_T01_Op09) {
        this.tbvTransacoes_T01_Op09 = tbvTransacoes_T01_Op09;
    }

    public Label getLblSymbol_T01_Op10() {
        return lblSymbol_T01_Op10;
    }

    public void setLblSymbol_T01_Op10(Label lblSymbol_T01_Op10) {
        this.lblSymbol_T01_Op10 = lblSymbol_T01_Op10;
    }

    public Label getLblQtdCall_T01_Op10() {
        return lblQtdCall_T01_Op10;
    }

    public void setLblQtdCall_T01_Op10(Label lblQtdCall_T01_Op10) {
        this.lblQtdCall_T01_Op10 = lblQtdCall_T01_Op10;
    }

    public Label getLblQtdPut_T01_Op10() {
        return lblQtdPut_T01_Op10;
    }

    public void setLblQtdPut_T01_Op10(Label lblQtdPut_T01_Op10) {
        this.lblQtdPut_T01_Op10 = lblQtdPut_T01_Op10;
    }

    public Label getLblQtdCallOrPut_T01_Op10() {
        return lblQtdCallOrPut_T01_Op10;
    }

    public void setLblQtdCallOrPut_T01_Op10(Label lblQtdCallOrPut_T01_Op10) {
        this.lblQtdCallOrPut_T01_Op10 = lblQtdCallOrPut_T01_Op10;
    }

    public ImageView getImgCallOrPut_T01_Op10() {
        return imgCallOrPut_T01_Op10;
    }

    public void setImgCallOrPut_T01_Op10(ImageView imgCallOrPut_T01_Op10) {
        this.imgCallOrPut_T01_Op10 = imgCallOrPut_T01_Op10;
    }

    public Label getLblQtdStakes_T01_Op10() {
        return lblQtdStakes_T01_Op10;
    }

    public void setLblQtdStakes_T01_Op10(Label lblQtdStakes_T01_Op10) {
        this.lblQtdStakes_T01_Op10 = lblQtdStakes_T01_Op10;
    }

    public Label getLblQtdWins_T01_Op10() {
        return lblQtdWins_T01_Op10;
    }

    public void setLblQtdWins_T01_Op10(Label lblQtdWins_T01_Op10) {
        this.lblQtdWins_T01_Op10 = lblQtdWins_T01_Op10;
    }

    public Label getLblQtdLoss_T01_Op10() {
        return lblQtdLoss_T01_Op10;
    }

    public void setLblQtdLoss_T01_Op10(Label lblQtdLoss_T01_Op10) {
        this.lblQtdLoss_T01_Op10 = lblQtdLoss_T01_Op10;
    }

    public Label getLblVlrIn_T01_Op10() {
        return lblVlrIn_T01_Op10;
    }

    public void setLblVlrIn_T01_Op10(Label lblVlrIn_T01_Op10) {
        this.lblVlrIn_T01_Op10 = lblVlrIn_T01_Op10;
    }

    public Label getLblVlrOut_T01_Op10() {
        return lblVlrOut_T01_Op10;
    }

    public void setLblVlrOut_T01_Op10(Label lblVlrOut_T01_Op10) {
        this.lblVlrOut_T01_Op10 = lblVlrOut_T01_Op10;
    }

    public Label getLblVlrDiff_T01_Op10() {
        return lblVlrDiff_T01_Op10;
    }

    public void setLblVlrDiff_T01_Op10(Label lblVlrDiff_T01_Op10) {
        this.lblVlrDiff_T01_Op10 = lblVlrDiff_T01_Op10;
    }

    public TableView getTbvTransacoes_T01_Op10() {
        return tbvTransacoes_T01_Op10;
    }

    public void setTbvTransacoes_T01_Op10(TableView tbvTransacoes_T01_Op10) {
        this.tbvTransacoes_T01_Op10 = tbvTransacoes_T01_Op10;
    }

    public Label getLblSymbol_T01_Op11() {
        return lblSymbol_T01_Op11;
    }

    public void setLblSymbol_T01_Op11(Label lblSymbol_T01_Op11) {
        this.lblSymbol_T01_Op11 = lblSymbol_T01_Op11;
    }

    public Label getLblQtdCall_T01_Op11() {
        return lblQtdCall_T01_Op11;
    }

    public void setLblQtdCall_T01_Op11(Label lblQtdCall_T01_Op11) {
        this.lblQtdCall_T01_Op11 = lblQtdCall_T01_Op11;
    }

    public Label getLblQtdPut_T01_Op11() {
        return lblQtdPut_T01_Op11;
    }

    public void setLblQtdPut_T01_Op11(Label lblQtdPut_T01_Op11) {
        this.lblQtdPut_T01_Op11 = lblQtdPut_T01_Op11;
    }

    public Label getLblQtdCallOrPut_T01_Op11() {
        return lblQtdCallOrPut_T01_Op11;
    }

    public void setLblQtdCallOrPut_T01_Op11(Label lblQtdCallOrPut_T01_Op11) {
        this.lblQtdCallOrPut_T01_Op11 = lblQtdCallOrPut_T01_Op11;
    }

    public ImageView getImgCallOrPut_T01_Op11() {
        return imgCallOrPut_T01_Op11;
    }

    public void setImgCallOrPut_T01_Op11(ImageView imgCallOrPut_T01_Op11) {
        this.imgCallOrPut_T01_Op11 = imgCallOrPut_T01_Op11;
    }

    public Label getLblQtdStakes_T01_Op11() {
        return lblQtdStakes_T01_Op11;
    }

    public void setLblQtdStakes_T01_Op11(Label lblQtdStakes_T01_Op11) {
        this.lblQtdStakes_T01_Op11 = lblQtdStakes_T01_Op11;
    }

    public Label getLblQtdWins_T01_Op11() {
        return lblQtdWins_T01_Op11;
    }

    public void setLblQtdWins_T01_Op11(Label lblQtdWins_T01_Op11) {
        this.lblQtdWins_T01_Op11 = lblQtdWins_T01_Op11;
    }

    public Label getLblQtdLoss_T01_Op11() {
        return lblQtdLoss_T01_Op11;
    }

    public void setLblQtdLoss_T01_Op11(Label lblQtdLoss_T01_Op11) {
        this.lblQtdLoss_T01_Op11 = lblQtdLoss_T01_Op11;
    }

    public Label getLblVlrIn_T01_Op11() {
        return lblVlrIn_T01_Op11;
    }

    public void setLblVlrIn_T01_Op11(Label lblVlrIn_T01_Op11) {
        this.lblVlrIn_T01_Op11 = lblVlrIn_T01_Op11;
    }

    public Label getLblVlrOut_T01_Op11() {
        return lblVlrOut_T01_Op11;
    }

    public void setLblVlrOut_T01_Op11(Label lblVlrOut_T01_Op11) {
        this.lblVlrOut_T01_Op11 = lblVlrOut_T01_Op11;
    }

    public Label getLblVlrDiff_T01_Op11() {
        return lblVlrDiff_T01_Op11;
    }

    public void setLblVlrDiff_T01_Op11(Label lblVlrDiff_T01_Op11) {
        this.lblVlrDiff_T01_Op11 = lblVlrDiff_T01_Op11;
    }

    public TableView getTbvTransacoes_T01_Op11() {
        return tbvTransacoes_T01_Op11;
    }

    public void setTbvTransacoes_T01_Op11(TableView tbvTransacoes_T01_Op11) {
        this.tbvTransacoes_T01_Op11 = tbvTransacoes_T01_Op11;
    }

    public Label getLblSymbol_T01_Op12() {
        return lblSymbol_T01_Op12;
    }

    public void setLblSymbol_T01_Op12(Label lblSymbol_T01_Op12) {
        this.lblSymbol_T01_Op12 = lblSymbol_T01_Op12;
    }

    public Label getLblQtdCall_T01_Op12() {
        return lblQtdCall_T01_Op12;
    }

    public void setLblQtdCall_T01_Op12(Label lblQtdCall_T01_Op12) {
        this.lblQtdCall_T01_Op12 = lblQtdCall_T01_Op12;
    }

    public Label getLblQtdPut_T01_Op12() {
        return lblQtdPut_T01_Op12;
    }

    public void setLblQtdPut_T01_Op12(Label lblQtdPut_T01_Op12) {
        this.lblQtdPut_T01_Op12 = lblQtdPut_T01_Op12;
    }

    public Label getLblQtdCallOrPut_T01_Op12() {
        return lblQtdCallOrPut_T01_Op12;
    }

    public void setLblQtdCallOrPut_T01_Op12(Label lblQtdCallOrPut_T01_Op12) {
        this.lblQtdCallOrPut_T01_Op12 = lblQtdCallOrPut_T01_Op12;
    }

    public ImageView getImgCallOrPut_T01_Op12() {
        return imgCallOrPut_T01_Op12;
    }

    public void setImgCallOrPut_T01_Op12(ImageView imgCallOrPut_T01_Op12) {
        this.imgCallOrPut_T01_Op12 = imgCallOrPut_T01_Op12;
    }

    public Label getLblQtdStakes_T01_Op12() {
        return lblQtdStakes_T01_Op12;
    }

    public void setLblQtdStakes_T01_Op12(Label lblQtdStakes_T01_Op12) {
        this.lblQtdStakes_T01_Op12 = lblQtdStakes_T01_Op12;
    }

    public Label getLblQtdWins_T01_Op12() {
        return lblQtdWins_T01_Op12;
    }

    public void setLblQtdWins_T01_Op12(Label lblQtdWins_T01_Op12) {
        this.lblQtdWins_T01_Op12 = lblQtdWins_T01_Op12;
    }

    public Label getLblQtdLoss_T01_Op12() {
        return lblQtdLoss_T01_Op12;
    }

    public void setLblQtdLoss_T01_Op12(Label lblQtdLoss_T01_Op12) {
        this.lblQtdLoss_T01_Op12 = lblQtdLoss_T01_Op12;
    }

    public Label getLblVlrIn_T01_Op12() {
        return lblVlrIn_T01_Op12;
    }

    public void setLblVlrIn_T01_Op12(Label lblVlrIn_T01_Op12) {
        this.lblVlrIn_T01_Op12 = lblVlrIn_T01_Op12;
    }

    public Label getLblVlrOut_T01_Op12() {
        return lblVlrOut_T01_Op12;
    }

    public void setLblVlrOut_T01_Op12(Label lblVlrOut_T01_Op12) {
        this.lblVlrOut_T01_Op12 = lblVlrOut_T01_Op12;
    }

    public Label getLblVlrDiff_T01_Op12() {
        return lblVlrDiff_T01_Op12;
    }

    public void setLblVlrDiff_T01_Op12(Label lblVlrDiff_T01_Op12) {
        this.lblVlrDiff_T01_Op12 = lblVlrDiff_T01_Op12;
    }

    public TableView getTbvTransacoes_T01_Op12() {
        return tbvTransacoes_T01_Op12;
    }

    public void setTbvTransacoes_T01_Op12(TableView tbvTransacoes_T01_Op12) {
        this.tbvTransacoes_T01_Op12 = tbvTransacoes_T01_Op12;
    }

    public static ROBOS getROBO_Selecionado() {
        return ROBO_Selecionado.get();
    }

    public static ObjectProperty<ROBOS> ROBO_SelecionadoProperty() {
        return ROBO_Selecionado;
    }

    public static void setROBO_Selecionado(ROBOS ROBO_Selecionado) {
        Operacoes.ROBO_Selecionado.set(ROBO_Selecionado);
    }

    public static boolean isBtnContratoDisabled() {
        return btnContratoDisabled.get();
    }

    public static BooleanProperty btnContratoDisabledProperty() {
        return btnContratoDisabled;
    }

    public static void setBtnContratoDisabled(boolean btnContratoDisabled) {
        Operacoes.btnContratoDisabled.set(btnContratoDisabled);
    }

    public static boolean isBtnIniciardisabled() {
        return btnIniciardisabled.get();
    }

    public static BooleanProperty btnIniciardisabledProperty() {
        return btnIniciardisabled;
    }

    public static void setBtnIniciardisabled(boolean btnIniciardisabled) {
        Operacoes.btnIniciardisabled.set(btnIniciardisabled);
    }

    public static boolean isBtnPausarDisabled() {
        return btnPausarDisabled.get();
    }

    public static BooleanProperty btnPausarDisabledProperty() {
        return btnPausarDisabled;
    }

    public static void setBtnPausarDisabled(boolean btnPausarDisabled) {
        Operacoes.btnPausarDisabled.set(btnPausarDisabled);
    }

    public static boolean isBtnStopDisabled() {
        return btnStopDisabled.get();
    }

    public static BooleanProperty btnStopDisabledProperty() {
        return btnStopDisabled;
    }

    public static void setBtnStopDisabled(boolean btnStopDisabled) {
        Operacoes.btnStopDisabled.set(btnStopDisabled);
    }

    public static String getParametrosUtilizadosRobo() {
        return parametrosUtilizadosRobo.get();
    }

    public static StringProperty parametrosUtilizadosRoboProperty() {
        return parametrosUtilizadosRobo;
    }

    public static void setParametrosUtilizadosRobo(String parametrosUtilizadosRobo) {
        Operacoes.parametrosUtilizadosRobo.set(parametrosUtilizadosRobo);
    }

    public static ObservableList<Transaction>[][] getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction>[][] transactionObservableList) {
        Operacoes.transactionObservableList = transactionObservableList;
    }
}
