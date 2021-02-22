package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.model.dao.ContaTokenDAO;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_STYLE;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Util_Json;
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
    static final ObservableList<ContaToken> CONTA_TOKEN_OBSERVABLE_LIST =
            FXCollections.observableArrayList(
                    getContaTokenDAO().getAll(ContaToken.class, null, null)
            );
    static ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>();


    /**
     * Conexão e operação com WebService
     */
    static final ObjectProperty<WSClient> WS_CLIENT_OBJECT_PROPERTY = new SimpleObjectProperty<>(new WSClient());
    static BooleanProperty wsConectado = new SimpleBooleanProperty(false);
    static final TICK_STYLE TICK_STYLE = CANDLES;

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
//    static ObjectProperty<Symbol>[] operador = new ObjectProperty[getSymbolObservableList().size()];
    //    static BooleanProperty[] operadorAtivo = new BooleanProperty[5];
//    static BooleanProperty[] operadorCompraAutorizada = new BooleanProperty[5];
//    static BooleanProperty[] operadorNegociando = new BooleanProperty[5];
    static ObjectProperty<BigDecimal>[][] qtdCallPut = new ObjectProperty[getSymbolObservableList().size()][TICK_TIME.values().length];
    static BooleanProperty[][] tickSubindo = new BooleanProperty[getSymbolObservableList().size()][TICK_TIME.values().length];
    static ObjectProperty<Tick>[][] ultimoTick = new ObjectProperty[getSymbolObservableList().size()][TICK_TIME.values().length];
    static ObjectProperty<Ohlc>[][] ultimoOhlc = new ObjectProperty[getSymbolObservableList().size()][TICK_TIME.values().length];
    //    static IntegerProperty[] maiorQtdDigito = new IntegerProperty[5];
//    static IntegerProperty[] menorQtdDigito = new IntegerProperty[5];
//    static StringProperty[] informacaoDetalhe01 = new StringProperty[5];
//    static StringProperty[] informacaoValor01 = new StringProperty[5];
//    static StringProperty[] informacaoDetalhe02 = new StringProperty[5];
//    static StringProperty[] informacaoValor02 = new StringProperty[5];
    //** Listas **
//    static ObservableList<HistoricoDeTicks>[] historicoDeTicks_TempObservableList = new ObservableList[getSymbolObservableList().size()];
//    static ObservableList<HistoricoDeTicks>[] historicoDeTicksAnalise_TempObservableList = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[][] historicoDeTicksObservableList = new ObservableList[getSymbolObservableList().size()][TICK_TIME.values().length];
    static ObservableList<HistoricoDeOhlc>[][] historicoDeOhlcObservableList = new ObservableList[getSymbolObservableList().size()][TICK_TIME.values().length];
//    static ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList = new ObservableList[5];
//    static ObservableList<Transaction>[] transactionObservableList = new ObservableList[5];
//    static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();

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
    public ComboBox<Integer> cboQtdTicksGrafico;
    public Label lblRoboHoraInicial;
    public Label lblRoboHoraAtual;
    public Label lblRoboCronometro;


    // Symbol_01 *-*-*
    public TitledPane tpn_Op01;
    public Label tpnLblQtdOperacoes_T01;
    public Label tpnLblQtdVitorias_T01;
    public Label tpnLblQtdDerrotas_T01;
    public Label tpnLblVlrIn_T01;
    public Label tpnLblVlrOut_T01;
    public Label tpnLblVlrDiff_T01;
    public Label tpnLlUltTick_Op01;
    public Label tpnLlUltTickLegenda_Op01;
    // Symbol_01 *-*-* Time_01
    public Label lblTime_Op01_T01;
    public Label lblQtdCallPut_Op01_T01;
    public ImageView imgCallPut_Op01_T01;
    public Label lblQtdOperaçoes_Op01_T01;
    public Label lblQtdVitorias_Op01_T01;
    public Label lblQtdDerrotas_Op01_T01;
    public Label lblVlrIn_Op01_T01;
    public Label lblVlrOut_Op01_T01;
    public Label lblVlrDiff_Op01_T01;
    public TableView tbvTransacoes_Op01_T01;
    // Symbol_01 *-*-* Time_02
    public Label lblTime_Op01_T02;
    public Label lblQtdCallPut_Op01_T02;
    public ImageView imgCallPut_Op01_T02;
    public Label lblQtdOperaçoes_Op01_T02;
    public Label lblQtdVitorias_Op01_T02;
    public Label lblQtdDerrotas_Op01_T02;
    public Label lblVlrIn_Op01_T02;
    public Label lblVlrOut_Op01_T02;
    public Label lblVlrDiff_Op01_T02;
    public TableView tbvTransacoes_Op01_T02;
    // Symbol_01 *-*-* Time_03
    public Label lblTime_Op01_T03;
    public Label lblQtdCallPut_Op01_T03;
    public ImageView imgCallPut_Op01_T03;
    public Label lblQtdOperaçoes_Op01_T03;
    public Label lblQtdVitorias_Op01_T03;
    public Label lblQtdDerrotas_Op01_T03;
    public Label lblVlrIn_Op01_T03;
    public Label lblVlrOut_Op01_T03;
    public Label lblVlrDiff_Op01_T03;
    public TableView tbvTransacoes_Op01_T03;


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


        getCboQtdTicksGrafico().getItems().setAll(100, 75, 50, 25, 0);
        getCboQtdTicksGrafico().getSelectionModel().select(0);

        for (int symbol_id = 0; symbol_id < getSymbolObservableList().size(); symbol_id++) {
            for (int time_id = 0; time_id < TICK_TIME.values().length; time_id++) {
                getHistoricoDeOhlcObservableList()[symbol_id][time_id] = FXCollections.observableArrayList();
                getUltimoOhlc()[symbol_id][time_id] = new SimpleObjectProperty<>();
                getQtdCallPut()[symbol_id][time_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            }
        }

        Thread threadInicial = new Thread(getTaskWsBinary());
        threadInicial.setDaemon(true);
        threadInicial.start();

    }

    private void carregarObjetos() {

//        getCboConta().setItems(getContaTokenDAO().getAll(ContaToken.class, "ativo=1", null)
//                .stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
//        ObservableList<Symbol> symbols = getSymbolDAO().getAll(Symbol.class, null, null).stream().collect(Collectors.toCollection(FXCollections::observableArrayList));
//
//        getCboSymbol01().setItems(symbols);
//        getCboSymbol02().setItems(symbols);
//        getCboSymbol03().setItems(symbols);
//        getCboSymbol04().setItems(symbols);
//        getCboSymbol05().setItems(symbols);

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

        getTpn_Op01().setText(getSymbolObservableList().get(0).getSymbol());
        getUltimoOhlc()[0][0].addListener((ov, o, n) -> {
            if (n == null)
                getTpnLlUltTick_Op01().setText("");
            if (n == null || o == null)
                return;
            getTpnLlUltTick_Op01().setText(n.toString());
            if (getLblRoboHoraInicial().getText().equals(""))
                getLblRoboHoraInicial().setText(
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(n.getOpen_time()),
                                TimeZone.getDefault().toZoneId()).format(DTF_TMODEL_DATA_TRANSACTION)
                );
            if (n.getClose().compareTo(o.getClose()) >= 0)
                getTpnLlUltTick_Op01().setStyle(STYLE_TICK_SUBINDO);
            else
                getTpnLlUltTick_Op01().setStyle(STYLE_TICK_DESCENDO);
            Integer time_close = (n.getGranularity() - (n.getEpoch() - n.getOpen_time()));
            getLblTime_Op01_T01().setText(String.format("%s M [t -%ss]",
                    n.getGranularity() / 60,
                    time_close));
            if (time_close <= 2 && time_close > 0) {
                if (n.getClose().compareTo(n.getOpen()) > 0) {
                    if (getQtdCallPut()[0][0].getValue().compareTo(BigDecimal.ZERO) > 0)
                        getQtdCallPut()[0][0].setValue(getQtdCallPut()[0][0].getValue().add(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][0].setValue(BigDecimal.ONE);
                    getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().toString());
                    getImgCallPut_Op01_T01().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                } else if (n.getClose().compareTo(n.getOpen()) < 0) {
                    if (getQtdCallPut()[0][0].getValue().compareTo(BigDecimal.ZERO) < 0)
                        getQtdCallPut()[0][0].setValue(getQtdCallPut()[0][0].getValue().subtract(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][0].setValue(BigDecimal.ONE.negate());
                    getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().negate().toString());
                    getImgCallPut_Op01_T01().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                } else {
                    getLblQtdCallPut_Op01_T01().setText("0");
                    getLblQtdCallPut_Op01_T01().setGraphic(null);
                }
                getLblQtdCallPut_Op01_T01().setText(getQtdCallPut()[0][0].getValue().toString().replace("-", ""));
            }
        });

        getUltimoOhlc()[0][1].addListener((ov, o, n) -> {
            Integer time_close = (n.getGranularity() - (n.getEpoch() - n.getOpen_time()));
            getLblTime_Op01_T02().setText(String.format("%s M [t -%ss]",
                    n.getGranularity() / 60,
                    time_close));
            if (time_close <= 2 && time_close > 0) {
                if (n.getClose().compareTo(n.getOpen()) > 0) {
                    if (getQtdCallPut()[0][1].getValue().compareTo(BigDecimal.ZERO) > 0)
                        getQtdCallPut()[0][1].setValue(getQtdCallPut()[0][1].getValue().add(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][1].setValue(BigDecimal.ONE);
                    getLblQtdCallPut_Op01_T02().setText(getQtdCallPut()[0][1].getValue().toString());
                    getImgCallPut_Op01_T02().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                } else if (n.getClose().compareTo(n.getOpen()) < 0) {
                    if (getQtdCallPut()[0][1].getValue().compareTo(BigDecimal.ZERO) < 0)
                        getQtdCallPut()[0][1].setValue(getQtdCallPut()[0][1].getValue().subtract(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][1].setValue(BigDecimal.ONE.negate());
                    getLblQtdCallPut_Op01_T02().setText(getQtdCallPut()[0][1].getValue().negate().toString());
                    getImgCallPut_Op01_T02().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                } else {
                    getLblQtdCallPut_Op01_T02().setText("0");
                    getLblQtdCallPut_Op01_T02().setGraphic(null);
                }
                getLblQtdCallPut_Op01_T02().setText(getQtdCallPut()[0][1].getValue().toString().replace("-", ""));
            }
        });


        getUltimoOhlc()[0][2].addListener((ov, o, n) -> {
            Integer time_close = (n.getGranularity() - (n.getEpoch() - n.getOpen_time()));
            getLblTime_Op01_T03().setText(String.format("%s M [t -%ss]",
                    n.getGranularity() / 60,
                    time_close));
            if (time_close <= 2 && time_close > 0) {
                if (n.getClose().compareTo(n.getOpen()) > 0) {
                    if (getQtdCallPut()[0][2].getValue().compareTo(BigDecimal.ZERO) > 0)
                        getQtdCallPut()[0][2].setValue(getQtdCallPut()[0][2].getValue().add(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][2].setValue(BigDecimal.ONE);
                    getLblQtdCallPut_Op01_T03().setText(getQtdCallPut()[0][2].getValue().toString());
                    getImgCallPut_Op01_T03().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                } else if (n.getClose().compareTo(n.getOpen()) < 0) {
                    if (getQtdCallPut()[0][2].getValue().compareTo(BigDecimal.ZERO) < 0)
                        getQtdCallPut()[0][2].setValue(getQtdCallPut()[0][2].getValue().subtract(BigDecimal.ONE));
                    else
                        getQtdCallPut()[0][2].setValue(BigDecimal.ONE.negate());
                    getLblQtdCallPut_Op01_T03().setText(getQtdCallPut()[0][2].getValue().negate().toString());
                    getImgCallPut_Op01_T03().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                } else {
                    getLblQtdCallPut_Op01_T03().setText("0");
                    getLblQtdCallPut_Op01_T03().setGraphic(null);
                }
                getLblQtdCallPut_Op01_T03().setText(getQtdCallPut()[0][2].getValue().toString().replace("-", ""));
            }
        });


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
                        getCboQtdTicksGrafico().getValue(), getTickStyle(), tempoVela, passthrough));
                if (tempoVela == null) jsonHistory = jsonHistory.replace(",\"granularity\":null", "");
                if (passthrough == null) jsonHistory = jsonHistory.replace(",\"passthrough\":null", "");
//                if (tickTime.getCod() > 0) jsonHistory = jsonHistory.replace(",\"subscribe\":1", "");
//                System.out.printf("jsonHistory: %s\n", jsonHistory);
                getWsClientObjectProperty().getMyWebSocket().send(jsonHistory);
            }
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

    public static WSClient getWsClientObjectProperty() {
        return WS_CLIENT_OBJECT_PROPERTY.get();
    }

    public static ObjectProperty<WSClient> WS_CLIENT_OBJECT_PROPERTYProperty() {
        return WS_CLIENT_OBJECT_PROPERTY;
    }

    public static void setWsClientObjectProperty(WSClient wsClientObjectProperty) {
        WS_CLIENT_OBJECT_PROPERTY.set(wsClientObjectProperty);
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

    public static br.com.tlmacedo.binary.model.enums.TICK_STYLE getTickStyle() {
        return TICK_STYLE;
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

    public static ObjectProperty<BigDecimal>[][] getQtdCallPut() {
        return qtdCallPut;
    }

    public static void setQtdCallPut(ObjectProperty<BigDecimal>[][] qtdCallPut) {
        Operacoes.qtdCallPut = qtdCallPut;
    }

    public static BooleanProperty[][] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[][] tickSubindo) {
        Operacoes.tickSubindo = tickSubindo;
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

    public Label getTpnLblQtdOperacoes_T01() {
        return tpnLblQtdOperacoes_T01;
    }

    public void setTpnLblQtdOperacoes_T01(Label tpnLblQtdOperacoes_T01) {
        this.tpnLblQtdOperacoes_T01 = tpnLblQtdOperacoes_T01;
    }

    public Label getTpnLblQtdVitorias_T01() {
        return tpnLblQtdVitorias_T01;
    }

    public void setTpnLblQtdVitorias_T01(Label tpnLblQtdVitorias_T01) {
        this.tpnLblQtdVitorias_T01 = tpnLblQtdVitorias_T01;
    }

    public Label getTpnLblQtdDerrotas_T01() {
        return tpnLblQtdDerrotas_T01;
    }

    public void setTpnLblQtdDerrotas_T01(Label tpnLblQtdDerrotas_T01) {
        this.tpnLblQtdDerrotas_T01 = tpnLblQtdDerrotas_T01;
    }

    public Label getTpnLblVlrIn_T01() {
        return tpnLblVlrIn_T01;
    }

    public void setTpnLblVlrIn_T01(Label tpnLblVlrIn_T01) {
        this.tpnLblVlrIn_T01 = tpnLblVlrIn_T01;
    }

    public Label getTpnLblVlrOut_T01() {
        return tpnLblVlrOut_T01;
    }

    public void setTpnLblVlrOut_T01(Label tpnLblVlrOut_T01) {
        this.tpnLblVlrOut_T01 = tpnLblVlrOut_T01;
    }

    public Label getTpnLblVlrDiff_T01() {
        return tpnLblVlrDiff_T01;
    }

    public void setTpnLblVlrDiff_T01(Label tpnLblVlrDiff_T01) {
        this.tpnLblVlrDiff_T01 = tpnLblVlrDiff_T01;
    }

    public Label getTpnLlUltTick_Op01() {
        return tpnLlUltTick_Op01;
    }

    public void setTpnLlUltTick_Op01(Label tpnLlUltTick_Op01) {
        this.tpnLlUltTick_Op01 = tpnLlUltTick_Op01;
    }

    public Label getTpnLlUltTickLegenda_Op01() {
        return tpnLlUltTickLegenda_Op01;
    }

    public void setTpnLlUltTickLegenda_Op01(Label tpnLlUltTickLegenda_Op01) {
        this.tpnLlUltTickLegenda_Op01 = tpnLlUltTickLegenda_Op01;
    }

    public Label getLblTime_Op01_T01() {
        return lblTime_Op01_T01;
    }

    public void setLblTime_Op01_T01(Label lblTime_Op01_T01) {
        this.lblTime_Op01_T01 = lblTime_Op01_T01;
    }

    public Label getLblQtdCallPut_Op01_T01() {
        return lblQtdCallPut_Op01_T01;
    }

    public void setLblQtdCallPut_Op01_T01(Label lblQtdCallPut_Op01_T01) {
        this.lblQtdCallPut_Op01_T01 = lblQtdCallPut_Op01_T01;
    }

    public ImageView getImgCallPut_Op01_T01() {
        return imgCallPut_Op01_T01;
    }

    public void setImgCallPut_Op01_T01(ImageView imgCallPut_Op01_T01) {
        this.imgCallPut_Op01_T01 = imgCallPut_Op01_T01;
    }

    public Label getLblQtdOperaçoes_Op01_T01() {
        return lblQtdOperaçoes_Op01_T01;
    }

    public void setLblQtdOperaçoes_Op01_T01(Label lblQtdOperaçoes_Op01_T01) {
        this.lblQtdOperaçoes_Op01_T01 = lblQtdOperaçoes_Op01_T01;
    }

    public Label getLblQtdVitorias_Op01_T01() {
        return lblQtdVitorias_Op01_T01;
    }

    public void setLblQtdVitorias_Op01_T01(Label lblQtdVitorias_Op01_T01) {
        this.lblQtdVitorias_Op01_T01 = lblQtdVitorias_Op01_T01;
    }

    public Label getLblQtdDerrotas_Op01_T01() {
        return lblQtdDerrotas_Op01_T01;
    }

    public void setLblQtdDerrotas_Op01_T01(Label lblQtdDerrotas_Op01_T01) {
        this.lblQtdDerrotas_Op01_T01 = lblQtdDerrotas_Op01_T01;
    }

    public Label getLblVlrIn_Op01_T01() {
        return lblVlrIn_Op01_T01;
    }

    public void setLblVlrIn_Op01_T01(Label lblVlrIn_Op01_T01) {
        this.lblVlrIn_Op01_T01 = lblVlrIn_Op01_T01;
    }

    public Label getLblVlrOut_Op01_T01() {
        return lblVlrOut_Op01_T01;
    }

    public void setLblVlrOut_Op01_T01(Label lblVlrOut_Op01_T01) {
        this.lblVlrOut_Op01_T01 = lblVlrOut_Op01_T01;
    }

    public Label getLblVlrDiff_Op01_T01() {
        return lblVlrDiff_Op01_T01;
    }

    public void setLblVlrDiff_Op01_T01(Label lblVlrDiff_Op01_T01) {
        this.lblVlrDiff_Op01_T01 = lblVlrDiff_Op01_T01;
    }

    public TableView getTbvTransacoes_Op01_T01() {
        return tbvTransacoes_Op01_T01;
    }

    public void setTbvTransacoes_Op01_T01(TableView tbvTransacoes_Op01_T01) {
        this.tbvTransacoes_Op01_T01 = tbvTransacoes_Op01_T01;
    }

    public Label getLblTime_Op01_T02() {
        return lblTime_Op01_T02;
    }

    public void setLblTime_Op01_T02(Label lblTime_Op01_T02) {
        this.lblTime_Op01_T02 = lblTime_Op01_T02;
    }

    public Label getLblQtdCallPut_Op01_T02() {
        return lblQtdCallPut_Op01_T02;
    }

    public void setLblQtdCallPut_Op01_T02(Label lblQtdCallPut_Op01_T02) {
        this.lblQtdCallPut_Op01_T02 = lblQtdCallPut_Op01_T02;
    }

    public ImageView getImgCallPut_Op01_T02() {
        return imgCallPut_Op01_T02;
    }

    public void setImgCallPut_Op01_T02(ImageView imgCallPut_Op01_T02) {
        this.imgCallPut_Op01_T02 = imgCallPut_Op01_T02;
    }

    public Label getLblQtdOperaçoes_Op01_T02() {
        return lblQtdOperaçoes_Op01_T02;
    }

    public void setLblQtdOperaçoes_Op01_T02(Label lblQtdOperaçoes_Op01_T02) {
        this.lblQtdOperaçoes_Op01_T02 = lblQtdOperaçoes_Op01_T02;
    }

    public Label getLblQtdVitorias_Op01_T02() {
        return lblQtdVitorias_Op01_T02;
    }

    public void setLblQtdVitorias_Op01_T02(Label lblQtdVitorias_Op01_T02) {
        this.lblQtdVitorias_Op01_T02 = lblQtdVitorias_Op01_T02;
    }

    public Label getLblQtdDerrotas_Op01_T02() {
        return lblQtdDerrotas_Op01_T02;
    }

    public void setLblQtdDerrotas_Op01_T02(Label lblQtdDerrotas_Op01_T02) {
        this.lblQtdDerrotas_Op01_T02 = lblQtdDerrotas_Op01_T02;
    }

    public Label getLblVlrIn_Op01_T02() {
        return lblVlrIn_Op01_T02;
    }

    public void setLblVlrIn_Op01_T02(Label lblVlrIn_Op01_T02) {
        this.lblVlrIn_Op01_T02 = lblVlrIn_Op01_T02;
    }

    public Label getLblVlrOut_Op01_T02() {
        return lblVlrOut_Op01_T02;
    }

    public void setLblVlrOut_Op01_T02(Label lblVlrOut_Op01_T02) {
        this.lblVlrOut_Op01_T02 = lblVlrOut_Op01_T02;
    }

    public Label getLblVlrDiff_Op01_T02() {
        return lblVlrDiff_Op01_T02;
    }

    public void setLblVlrDiff_Op01_T02(Label lblVlrDiff_Op01_T02) {
        this.lblVlrDiff_Op01_T02 = lblVlrDiff_Op01_T02;
    }

    public TableView getTbvTransacoes_Op01_T02() {
        return tbvTransacoes_Op01_T02;
    }

    public void setTbvTransacoes_Op01_T02(TableView tbvTransacoes_Op01_T02) {
        this.tbvTransacoes_Op01_T02 = tbvTransacoes_Op01_T02;
    }

    public Label getLblTime_Op01_T03() {
        return lblTime_Op01_T03;
    }

    public void setLblTime_Op01_T03(Label lblTime_Op01_T03) {
        this.lblTime_Op01_T03 = lblTime_Op01_T03;
    }

    public Label getLblQtdCallPut_Op01_T03() {
        return lblQtdCallPut_Op01_T03;
    }

    public void setLblQtdCallPut_Op01_T03(Label lblQtdCallPut_Op01_T03) {
        this.lblQtdCallPut_Op01_T03 = lblQtdCallPut_Op01_T03;
    }

    public ImageView getImgCallPut_Op01_T03() {
        return imgCallPut_Op01_T03;
    }

    public void setImgCallPut_Op01_T03(ImageView imgCallPut_Op01_T03) {
        this.imgCallPut_Op01_T03 = imgCallPut_Op01_T03;
    }

    public Label getLblQtdOperaçoes_Op01_T03() {
        return lblQtdOperaçoes_Op01_T03;
    }

    public void setLblQtdOperaçoes_Op01_T03(Label lblQtdOperaçoes_Op01_T03) {
        this.lblQtdOperaçoes_Op01_T03 = lblQtdOperaçoes_Op01_T03;
    }

    public Label getLblQtdVitorias_Op01_T03() {
        return lblQtdVitorias_Op01_T03;
    }

    public void setLblQtdVitorias_Op01_T03(Label lblQtdVitorias_Op01_T03) {
        this.lblQtdVitorias_Op01_T03 = lblQtdVitorias_Op01_T03;
    }

    public Label getLblQtdDerrotas_Op01_T03() {
        return lblQtdDerrotas_Op01_T03;
    }

    public void setLblQtdDerrotas_Op01_T03(Label lblQtdDerrotas_Op01_T03) {
        this.lblQtdDerrotas_Op01_T03 = lblQtdDerrotas_Op01_T03;
    }

    public Label getLblVlrIn_Op01_T03() {
        return lblVlrIn_Op01_T03;
    }

    public void setLblVlrIn_Op01_T03(Label lblVlrIn_Op01_T03) {
        this.lblVlrIn_Op01_T03 = lblVlrIn_Op01_T03;
    }

    public Label getLblVlrOut_Op01_T03() {
        return lblVlrOut_Op01_T03;
    }

    public void setLblVlrOut_Op01_T03(Label lblVlrOut_Op01_T03) {
        this.lblVlrOut_Op01_T03 = lblVlrOut_Op01_T03;
    }

    public Label getLblVlrDiff_Op01_T03() {
        return lblVlrDiff_Op01_T03;
    }

    public void setLblVlrDiff_Op01_T03(Label lblVlrDiff_Op01_T03) {
        this.lblVlrDiff_Op01_T03 = lblVlrDiff_Op01_T03;
    }

    public TableView getTbvTransacoes_Op01_T03() {
        return tbvTransacoes_Op01_T03;
    }

    public void setTbvTransacoes_Op01_T03(TableView tbvTransacoes_Op01_T03) {
        this.tbvTransacoes_Op01_T03 = tbvTransacoes_Op01_T03;
    }
}
