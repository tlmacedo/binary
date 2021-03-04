package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.dao.ContaTokenDAO;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.*;
import br.com.tlmacedo.binary.model.tableModel.TmodelTransactions;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_DataHoraCarimbo;
import br.com.tlmacedo.binary.services.Service_Mascara;
import br.com.tlmacedo.binary.services.Util_Json;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
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
    static final List<Symbol> SYMBOL_LIST = getSymbolDAO().getAll(Symbol.class, "id=1", null);
    static final ObservableList<Symbol> SYMBOL_OBSERVABLE_LIST =
            FXCollections.observableArrayList(
                    getSymbolDAO().getAll(Symbol.class, "id=1", null)
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
    static final TICK_STYLE tickStyle = CANDLES;

    /**
     * Robos
     */
    static final ObjectProperty<Robo> ROBO = new SimpleObjectProperty<>();


    /**
     * Variaveis de controle do sistema
     */
    static BooleanProperty appAutorizado = new SimpleBooleanProperty(false);
    static StringProperty parametrosUtilizadosRobo = new SimpleStringProperty("");
    static ObjectProperty<BigDecimal> saldoInicial = new SimpleObjectProperty<>(BigDecimal.ZERO);

    /**
     * Variaveis de informações para operadores
     */
    //** Variaveis **
    static IntegerProperty qtdCandlesAnalise = new SimpleIntegerProperty();
    static ObjectProperty<Ohlc>[] ultimoOhlcStr = new ObjectProperty[getSymbolList().size()];
    static ObjectProperty<Ohlc>[][] testaLastCandle = new ObjectProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static BooleanProperty[] tickSubindo = new BooleanProperty[getSymbolList().size()];

    static IntegerProperty[] timeCandleStart = new IntegerProperty[TICK_TIME.values().length];
    static IntegerProperty[] timeCandleToClose = new IntegerProperty[TICK_TIME.values().length];

    static IntegerProperty[][] qtdCallOrPut = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdCall = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdPut = new IntegerProperty[TICK_TIME.values().length][getSymbolObservableList().size()];

    //** Listas **
    static ObservableList<HistoricoDeCandles> historicoDeCandlesObservableList = FXCollections.observableArrayList();
    static ObservableList<Transaction> transactionObservableList = FXCollections.observableArrayList();
    static TmodelTransactions[][] tmodelTransactions = new TmodelTransactions[TICK_TIME.values().length][getSymbolObservableList().size()];
    static FilteredList<Transaction>[][] transactionFilteredList = new FilteredList[TICK_TIME.values().length][getSymbolObservableList().size()];
    static FilteredList<HistoricoDeCandles>[][] historicoDeCandlesFilteredList = new FilteredList[TICK_TIME.values().length][getSymbolObservableList().size()];


    //** Operações com Robos **
    static BooleanProperty[][] resultLastTransiction = new BooleanProperty[TICK_TIME.values().length][getSymbolObservableList().size()];
    static BooleanProperty[] timeAtivo = new BooleanProperty[TICK_TIME.values().length];
    static BooleanProperty contratoGerado = new SimpleBooleanProperty(false);
    static BooleanProperty disableContratoBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disableIniciarBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disablePausarBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disableStopBtn = new SimpleBooleanProperty(true);
    static BooleanProperty roboMonitorando = new SimpleBooleanProperty(false);
    static BooleanProperty roboMonitorandoPausado = new SimpleBooleanProperty(false);
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
    public Label lblTpn_T01_CandleTimeStart;
    public Label lblTpn_T01_TimeEnd;
    public Label lblTpn_T01_QtdStakes;
    public Label lblTpn_T01_QtdWins;
    public Label lblTpn_T01_QtdLoss;
    public Label lblTpn_T01_VlrIn;
    public Label lblTpn_T01_VlrOut;
    public Label lblTpn_T01_VlrDiff;
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
    public TableView tbvTransaction_T01_Op01;
//    // Time_01 *-*-* Symbol_02
//    public Label lblSymbol_T01_Op02;
//    public Label lblQtdCall_T01_Op02;
//    public Label lblQtdPut_T01_Op02;
//    public Label lblQtdCallOrPut_T01_Op02;
//    public ImageView imgCallOrPut_T01_Op02;
//    public Label lblQtdStakes_T01_Op02;
//    public Label lblQtdWins_T01_Op02;
//    public Label lblQtdLoss_T01_Op02;
//    public Label lblVlrIn_T01_Op02;
//    public Label lblVlrOut_T01_Op02;
//    public Label lblVlrDiff_T01_Op02;
//    public TableView tbvTransaction_T01_Op02;
//    // Time_01 *-*-* Symbol_03
//    public Label lblSymbol_T01_Op03;
//    public Label lblQtdCall_T01_Op03;
//    public Label lblQtdPut_T01_Op03;
//    public Label lblQtdCallOrPut_T01_Op03;
//    public ImageView imgCallOrPut_T01_Op03;
//    public Label lblQtdStakes_T01_Op03;
//    public Label lblQtdWins_T01_Op03;
//    public Label lblQtdLoss_T01_Op03;
//    public Label lblVlrIn_T01_Op03;
//    public Label lblVlrOut_T01_Op03;
//    public Label lblVlrDiff_T01_Op03;
//    public TableView tbvTransaction_T01_Op03;
//    // Time_01 *-*-* Symbol_04
//    public Label lblSymbol_T01_Op04;
//    public Label lblQtdCall_T01_Op04;
//    public Label lblQtdPut_T01_Op04;
//    public Label lblQtdCallOrPut_T01_Op04;
//    public ImageView imgCallOrPut_T01_Op04;
//    public Label lblQtdStakes_T01_Op04;
//    public Label lblQtdWins_T01_Op04;
//    public Label lblQtdLoss_T01_Op04;
//    public Label lblVlrIn_T01_Op04;
//    public Label lblVlrOut_T01_Op04;
//    public Label lblVlrDiff_T01_Op04;
//    public TableView tbvTransaction_T01_Op04;
//    // Time_01 *-*-* Symbol_05
//    public Label lblSymbol_T01_Op05;
//    public Label lblQtdCall_T01_Op05;
//    public Label lblQtdPut_T01_Op05;
//    public Label lblQtdCallOrPut_T01_Op05;
//    public ImageView imgCallOrPut_T01_Op05;
//    public Label lblQtdStakes_T01_Op05;
//    public Label lblQtdWins_T01_Op05;
//    public Label lblQtdLoss_T01_Op05;
//    public Label lblVlrIn_T01_Op05;
//    public Label lblVlrOut_T01_Op05;
//    public Label lblVlrDiff_T01_Op05;
//    public TableView tbvTransaction_T01_Op05;
//    // Time_01 *-*-* Symbol_06
//    public Label lblSymbol_T01_Op06;
//    public Label lblQtdCall_T01_Op06;
//    public Label lblQtdPut_T01_Op06;
//    public Label lblQtdCallOrPut_T01_Op06;
//    public ImageView imgCallOrPut_T01_Op06;
//    public Label lblQtdStakes_T01_Op06;
//    public Label lblQtdWins_T01_Op06;
//    public Label lblQtdLoss_T01_Op06;
//    public Label lblVlrIn_T01_Op06;
//    public Label lblVlrOut_T01_Op06;
//    public Label lblVlrDiff_T01_Op06;
//    public TableView tbvTransaction_T01_Op06;
//    // Time_01 *-*-* Symbol_07
//    public Label lblSymbol_T01_Op07;
//    public Label lblQtdCall_T01_Op07;
//    public Label lblQtdPut_T01_Op07;
//    public Label lblQtdCallOrPut_T01_Op07;
//    public ImageView imgCallOrPut_T01_Op07;
//    public Label lblQtdStakes_T01_Op07;
//    public Label lblQtdWins_T01_Op07;
//    public Label lblQtdLoss_T01_Op07;
//    public Label lblVlrIn_T01_Op07;
//    public Label lblVlrOut_T01_Op07;
//    public Label lblVlrDiff_T01_Op07;
//    public TableView tbvTransaction_T01_Op07;
//    // Time_01 *-*-* Symbol_08
//    public Label lblSymbol_T01_Op08;
//    public Label lblQtdCall_T01_Op08;
//    public Label lblQtdPut_T01_Op08;
//    public Label lblQtdCallOrPut_T01_Op08;
//    public ImageView imgCallOrPut_T01_Op08;
//    public Label lblQtdStakes_T01_Op08;
//    public Label lblQtdWins_T01_Op08;
//    public Label lblQtdLoss_T01_Op08;
//    public Label lblVlrIn_T01_Op08;
//    public Label lblVlrOut_T01_Op08;
//    public Label lblVlrDiff_T01_Op08;
//    public TableView tbvTransaction_T01_Op08;
//    // Time_01 *-*-* Symbol_09
//    public Label lblSymbol_T01_Op09;
//    public Label lblQtdCall_T01_Op09;
//    public Label lblQtdPut_T01_Op09;
//    public Label lblQtdCallOrPut_T01_Op09;
//    public ImageView imgCallOrPut_T01_Op09;
//    public Label lblQtdStakes_T01_Op09;
//    public Label lblQtdWins_T01_Op09;
//    public Label lblQtdLoss_T01_Op09;
//    public Label lblVlrIn_T01_Op09;
//    public Label lblVlrOut_T01_Op09;
//    public Label lblVlrDiff_T01_Op09;
//    public TableView tbvTransaction_T01_Op09;
//    // Time_01 *-*-* Symbol_10
//    public Label lblSymbol_T01_Op10;
//    public Label lblQtdCall_T01_Op10;
//    public Label lblQtdPut_T01_Op10;
//    public Label lblQtdCallOrPut_T01_Op10;
//    public ImageView imgCallOrPut_T01_Op10;
//    public Label lblQtdStakes_T01_Op10;
//    public Label lblQtdWins_T01_Op10;
//    public Label lblQtdLoss_T01_Op10;
//    public Label lblVlrIn_T01_Op10;
//    public Label lblVlrOut_T01_Op10;
//    public Label lblVlrDiff_T01_Op10;
//    public TableView tbvTransaction_T01_Op10;
//    // Time_01 *-*-* Symbol_11
//    public Label lblSymbol_T01_Op11;
//    public Label lblQtdCall_T01_Op11;
//    public Label lblQtdPut_T01_Op11;
//    public Label lblQtdCallOrPut_T01_Op11;
//    public ImageView imgCallOrPut_T01_Op11;
//    public Label lblQtdStakes_T01_Op11;
//    public Label lblQtdWins_T01_Op11;
//    public Label lblQtdLoss_T01_Op11;
//    public Label lblVlrIn_T01_Op11;
//    public Label lblVlrOut_T01_Op11;
//    public Label lblVlrDiff_T01_Op11;
//    public TableView tbvTransaction_T01_Op11;
//    // Time_01 *-*-* Symbol_12
//    public Label lblSymbol_T01_Op12;
//    public Label lblQtdCall_T01_Op12;
//    public Label lblQtdPut_T01_Op12;
//    public Label lblQtdCallOrPut_T01_Op12;
//    public ImageView imgCallOrPut_T01_Op12;
//    public Label lblQtdStakes_T01_Op12;
//    public Label lblQtdWins_T01_Op12;
//    public Label lblQtdLoss_T01_Op12;
//    public Label lblVlrIn_T01_Op12;
//    public Label lblVlrOut_T01_Op12;
//    public Label lblVlrDiff_T01_Op12;
//    public TableView tbvTransaction_T01_Op12;

    // Time_02 *-*-*
    public TitledPane tpn_T02;
    public JFXCheckBox chkTpn02_TimeAtivo;
    public Label lblTpn_T02_CandleTimeStart;
    public Label lblTpn_T02_TimeEnd;
    public Label lblTpn_T02_QtdStakes;
    public Label lblTpn_T02_QtdWins;
    public Label lblTpn_T02_QtdLoss;
    public Label lblTpn_T02_VlrIn;
    public Label lblTpn_T02_VlrOut;
    public Label lblTpn_T02_VlrDiff;
    // Time_02 *-*-* Symbol_01
    public Label lblSymbol_T02_Op01;
    public Label lblQtdCall_T02_Op01;
    public Label lblQtdPut_T02_Op01;
    public Label lblQtdCallOrPut_T02_Op01;
    public ImageView imgCallOrPut_T02_Op01;
    public Label lblQtdStakes_T02_Op01;
    public Label lblQtdWins_T02_Op01;
    public Label lblQtdLoss_T02_Op01;
    public Label lblVlrIn_T02_Op01;
    public Label lblVlrOut_T02_Op01;
    public Label lblVlrDiff_T02_Op01;
    public TableView tbvTransaction_T02_Op01;


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

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            getTimeAtivo()[t_id] = new SimpleBooleanProperty(false);
        }

        conectarTimesAtivos();

        variaveis_Carregar();

        objetos_Carregar();

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

    private void variaveis_Carregar() {

        for (int s_id = 0; s_id < getSymbolList().size(); s_id++) {
            getUltimoOhlcStr()[s_id] = new SimpleObjectProperty<>();
            getTickSubindo()[s_id] = new SimpleBooleanProperty(false);
        }

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (t_id > 0 && !getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;

            getTimeCandleStart()[t_id] = new SimpleIntegerProperty(0);
            getTimeCandleToClose()[t_id] = new SimpleIntegerProperty(0);

            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;

                getQtdCall()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdPut()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdCallOrPut()[t_id][s_id] = new SimpleIntegerProperty(0);

                getTestaLastCandle()[t_id][s_id] = new SimpleObjectProperty<>();
                getHistoricoDeCandlesFilteredList()[t_id][s_id] = new FilteredList<>(getHistoricoDeCandlesObservableList());
                getHistoricoDeCandlesFilteredList()[t_id][s_id].setPredicate(historicoDeCandles ->
                        historicoDeCandles.getGranularity() == ((finalT_id + 1) * 60)
                                && historicoDeCandles.getSymbol().getId() - 1 == finalS_id);


//                getTmodelTransactions()[t_id][s_id] = new TmodelTransactions();
//                getTmodelTransactions()[t_id][s_id].criar_tabela();
//

                getTransactionFilteredList()[t_id][s_id] = new FilteredList<>(getTransactionObservableList());
//                getTransactionFilteredList()[t_id][s_id].setPredicate(transaction ->
//                        transaction.get);
//******************

//                getTmodelTransactions()[t_id][s_id].setTransactionFilteredList(getTransactionFilteredList()[t_id][s_id]);
//                contectarTabelaEmLista(t_id, s_id);
//
//                getTmodelTransactions()[t_id][s_id].setTransactionObservableList(getTransactionObservableList());
//                //getTmodelTransactions()[t_id][s_id].escutarTransactions();
//                getTmodelTransactions()[t_id][s_id].tabela_preencher();
            }
        }

        variaveis_Bindins();

        variaveis_Listener();

        variaveis_Comandos();

    }

    private void variaveis_Bindins() {

        saldoInicialProperty().bind(Bindings.createObjectBinding(() -> {
            if (authorizeProperty().getValue() == null)
                return BigDecimal.ZERO;
            return authorizeProperty().getValue().getBalance();
        }, authorizeProperty()));

    }

    private void variaveis_Listener() {

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

        disableContratoBtnProperty().addListener((ov, o, n) -> {

            if (n == null || !n)
                setParametrosUtilizadosRobo("");

        });

        disableContratoBtnProperty().bind(Bindings.createBooleanBinding(() ->
                        (!isWsConectado() || getAuthorize() == null || getRobo() == null || isContratoGerado()),
                wsConectadoProperty(), authorizeProperty(), ROBOProperty(), contratoGeradoProperty()));

        disableIniciarBtnProperty().bind(contratoGeradoProperty().not());

        ROBOProperty().addListener((ov, o, n) -> {

            if (n == null) {
                setParametrosUtilizadosRobo("");
                return;
            }

        });

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getHistoricoDeCandlesFilteredList()[t_id][s_id].addListener((ListChangeListener<? super HistoricoDeCandles>) c -> {
                    while (c.next()) {
                        if (c.wasRemoved()) {
                            getQtdCallOrPut()[finalT_id][finalS_id].setValue(0);
                            getQtdCall()[finalT_id][finalS_id].setValue(0);
                            getQtdPut()[finalT_id][finalS_id].setValue(0);
                            for (HistoricoDeCandles candle : getHistoricoDeCandlesFilteredList()[finalT_id][finalS_id])
                                contarCallAndPut(candle, finalT_id, finalS_id);
                        }
                        for (HistoricoDeCandles candle : c.getAddedSubList()) {
                            contarCallAndPut(candle, finalT_id, finalS_id);
                        }
                    }
                });
            }
        }

    }

    private void variaveis_Comandos() {

    }

    private void objetos_Carregar() {

        getCboTpnNegociacaoQtdCandlesAnalise().getItems().setAll(10, 15, 20, 25, 30, 35, 40, 45, 50);
        getCboTpnNegociacaoQtdCandlesAnalise().getSelectionModel().select(0);

        qtdCandlesAnaliseProperty().bind(Bindings.createIntegerBinding(() ->
                getCboTpnNegociacaoQtdCandlesAnalise().getValue() + 1, getCboTpnNegociacaoQtdCandlesAnalise().valueProperty()));

        getCboTpnDetalhesContaBinary().setItems(getContaTokenObservableList());

        getCboNegociacaoRobos().setItems(ROBOS.getList().stream()
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboNegociacaoRobos().getItems().add(0, null);

        objetos_Bindings();

        objetos_Listener();

        objetos_Comandos();

        Thread threadInicial = new Thread(getTaskWsBinary());
        threadInicial.setDaemon(true);
        threadInicial.start();

    }

    private void objetos_Bindings() {

        getLblDetalhesSaldoInicial().textProperty().bind(Bindings.createStringBinding(() ->
                        Service_Mascara.getValorMoeda(saldoInicialProperty().getValue()),
                saldoInicialProperty()));

        getLblNegociacaoParametros().textProperty().bind(parametrosUtilizadosRoboProperty());

        getBtnTpnNegociacao_Contratos().disableProperty().bind(disableContratoBtnProperty());
        getBtnTpnNegociacao_Iniciar().disableProperty().bind(disableIniciarBtnProperty());
        getBtnTpnNegociacao_Pausar().disableProperty().bind(disablePausarBtnProperty());
        getBtnTpnNegociacao_Stop().disableProperty().bind(disableStopBtnProperty());

    }

    private void objetos_Listener() {

        getCboTpnDetalhesContaBinary().valueProperty().addListener((ov, o, n) -> {
            if (n == null) {
                setAppAutorizado(false);
                return;
            }
            solicitarAutorizacaoApp(n.getTokenApi());
        });

        getCboNegociacaoRobos().valueProperty().addListener((ov, o, n) -> {
            if (n == null) {
                setRobo(null);
                return;
            }
            switch (n) {
                case ABR -> {
                    Abr abr = new Abr();
                    setRobo(abr);
                }
            }
        });

//        getTransactionObservableList().addListener((ListChangeListener<? super Transaction>) c -> {
//            while (c.next()) {
//                for (Transaction transaction : c.getAddedSubList()) {
//                    if (transaction.getAction() == null) return;
//                    int t_id = TIME_1M,
//                            s_id = getSymbolObservableList().stream().filter(symbol -> symbol.getSymbol()
//                                    .equals(transaction.getSymbol().getSymbol()))
//                                    .findFirst().get().getId().intValue() - 1;
//                    System.out.printf("\t\t[%s-%s:%s]\t\ttransaction[%s-%s][%s-%s]: %s\n",
//                            transaction.getDate_expiry(), transaction.getTransaction_time(), transaction.getDate_expiry() - transaction.getTransaction_time(),
//                            t_id, TICK_TIME.toEnum(t_id),
//                            s_id, getSymbolObservableList().get(s_id), transaction);
//                    switch (ACTION.valueOf(transaction.getAction().toUpperCase())) {
//                        case BUY -> {
//                        }
//                        case SELL -> {
//                            getResultLastTransiction()[t_id][s_id].setValue(transaction.getAmount().compareTo(BigDecimal.ZERO) > 0);
//                            if (getResultLastTransiction()[t_id][s_id].getValue())
//                                getVlrStkContrato()[t_id][s_id].setValue(getVlrStkPadrao()[t_id].getValue());
//                            else
//                                getVlrStkContrato()[t_id][s_id].setValue(
//                                        getVlrStkContrato()[t_id][s_id].getValue().multiply(new BigDecimal("2.")));
//                        }
//                    }
//                }
//            }
//        });

    }

    private void objetos_Comandos() {

        getBtnTpnNegociacao_Contratos().setOnAction(event -> {
            try {
                if (!getCboTpnDetalhesContaBinary().getValue().isTransactionOK())
                    solicitarTransacoes();
                getRobo().definicaoDeContrato();
                setContratoGerado(true);
            } catch (Exception ex) {
                if (ex instanceof NoSuchElementException)
                    return;
                ex.printStackTrace();
            }
        });

        getBtnTpnNegociacao_Iniciar().setOnAction(event -> {
            try {
                getRobo().monitorarCondicoesParaComprar();
                setRoboMonitorando(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        getBtnTpnNegociacao_Pausar().setOnAction(event -> setRoboMonitorandoPausado(true));

        getBtnTpnNegociacao_Stop().setOnAction(event -> {
            getCboNegociacaoRobos().getSelectionModel().select(0);
        });

    }

    private void preencherTabelas() {

        Platform.runLater(() -> {
            for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
                if (!getTimeAtivo()[t_id].getValue()) continue;
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    int finalT_id = t_id, finalS_id = s_id;
                    getTmodelTransactions()[t_id][s_id].getTransactionFilteredList()
                            .setPredicate(transaction ->
//                                    (((transaction.getDate_expiry()
//                                            - transaction.getTransaction_time()) / 60) - 1 == finalT_id) &&
                                    transaction.getSymbol().equals(getSymbolObservableList().get(finalS_id).getSymbol()));

                    getTmodelTransactions()[t_id][s_id].criar_tabela();
                    getTmodelTransactions()[t_id][s_id].tabela_preencher();
                }

            }
        });


    }

    private void conectarObjetosEmVariaveis() {

        conectarObjetosEmVariaveis_LastTicks();

        conectarObjetosEmVariaveis_Timers();

        //preencherTabelas();

    }


    private void contarCallAndPut(HistoricoDeCandles candle, int t_id, int s_id) {

        if (candle.getClose().compareTo(candle.getOpen()) > 0) {
            getQtdCall()[t_id][s_id].setValue(
                    getQtdCall()[t_id][s_id].getValue() + 1);
            getQtdCallOrPut()[t_id][s_id].setValue(
                    getQtdCallOrPut()[t_id][s_id].getValue().compareTo(0) > 0
                            ? getQtdCallOrPut()[t_id][s_id].getValue() + 1
                            : 1);
        } else if (candle.getClose().compareTo(candle.getOpen()) < 0) {
            getQtdPut()[t_id][s_id].setValue(
                    getQtdPut()[t_id][s_id].getValue() + 1);
            getQtdCallOrPut()[t_id][s_id].setValue(
                    getQtdCallOrPut()[t_id][s_id].getValue().compareTo(0) < 0
                            ? getQtdCallOrPut()[t_id][s_id].getValue() - 1
                            : -1);
        } else {
            getQtdCallOrPut()[t_id][s_id].setValue(0);
        }

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
        int timeDuration = (Integer.parseInt(time.getDescricao().replaceAll("\\D", "")) * 60) - symbol.getTickTime();
        getPriceProposal()[t_id][s_id].setDuration(timeDuration);
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
            getCboTpnDetalhesContaBinary().getValue().setTransactionOK(true);
            setAppAutorizado(true);
        } catch (Exception ex) {
            setAppAutorizado(false);
            ex.printStackTrace();
        }

    }

    private void solicitarTicksLabels() {

    }

    private void solicitarTicks() {

        Passthrough passthrough = new Passthrough();
        Integer tempoVela = null;

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (t_id > 0 && !getTimeAtivo()[t_id].getValue()) continue;
            tempoVela = Integer.parseInt(TICK_TIME.toEnum(t_id).getDescricao().replaceAll("\\D", "")) * 60;
            passthrough.setTickTime(TICK_TIME.toEnum(t_id));
            passthrough.setTickStyle(getTickStyle());

            for (int s_id = 0; s_id < (t_id == 0 ? getSymbolList().size() : getSymbolObservableList().size()); s_id++) {
                passthrough.setSymbol(getSymbolList().get(s_id));

                String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(getSymbolList().get(s_id).getSymbol(),
                        getCboTpnNegociacaoQtdCandlesAnalise().getValue() + 1, getTickStyle(), tempoVela, passthrough));
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
            if (proposal == null) return;
            Passthrough passthrough = new Passthrough();
            passthrough.setMensagem("testando passthrough!!!");
            String jsonBuyContrato = Util_Json.getJson_from_Object(new BuyContract(proposal, passthrough));
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


    private void conectarObjetosEmVariaveis_LastTicks() {

        for (int s_id = 0; s_id < getSymbolList().size(); s_id++) {
            int finalS_id = s_id;
            if (s_id == SYMBOL_01) {
                getLblSymbol_01().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_01().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_01().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_02) {
                getLblSymbol_02().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_02().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_02().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_03) {
                getLblSymbol_03().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_03().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_03().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_04) {
                getLblSymbol_04().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_04().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_04().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_05) {
                getLblSymbol_05().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_05().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_05().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_06) {
                getLblSymbol_06().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_06().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_06().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_07) {
                getLblSymbol_07().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_07().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_07().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_08) {
                getLblSymbol_08().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_08().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_08().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_09) {
                getLblSymbol_09().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_09().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_09().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_10) {
                getLblSymbol_10().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_10().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_10().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_11) {
                getLblSymbol_11().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_11().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_11().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }

            if (s_id == SYMBOL_12) {
                getLblSymbol_12().setText(getSymbolList().get(s_id).toString());
                getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                    getLblLastTickSymbol_12().setText(n == null ? "" : n.toString());
                    getTickSubindo()[finalS_id].setValue(n != null && o != null && n.getClose().compareTo(o.getClose()) > 0);
                    getLblLastTickSymbol_12().setStyle(getTickSubindo()[finalS_id].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO);
                });
            }
        }

    }

    private void conectarObjetosEmVariaveis_Timers() {
        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (t_id == TIME_1M)
                getTpn_T01().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
            if (t_id == TIME_2M)
                getTpn_T02().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
//            if (t_id == TIME_3M)
//                getTpn_T03().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
//            if (t_id == TIME_5M)
//                getTpn_T04().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
//            if (t_id == TIME_10M)
//                getTpn_T05().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
//            if (t_id == TIME_15M)
//                getTpn_T06().setText(String.format("T%s - ", TICK_TIME.toEnum(t_id)));
        }

        for (int t_id = 0; t_id < TICK_TIME.values().length; t_id++) {
            if (!getTimeAtivo()[t_id].getValue()) continue;
            int finalT_id = t_id;
            if (t_id == TIME_1M) {
                getLblTpn_T01_CandleTimeStart().textProperty().bind(Bindings.createStringBinding(() ->
                                Service_DataHoraCarimbo.getCarimboStr(getTimeCandleStart()[finalT_id].getValue(), DTF_HORA_MINUTOS),
                        getTimeCandleStart()[t_id]));
                getLblTpn_T01_TimeEnd().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("-%s s", getTimeCandleToClose()[finalT_id].getValue()),
                        getTimeCandleToClose()[t_id]));

                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    int finalS_id = s_id;
                    //*-*-* Op_01
                    if (s_id == SYMBOL_01) {
                        getLblSymbol_T01_Op01().setText(getSymbolObservableList().get(s_id).getSymbol());
                        getLblQtdCall_T01_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        getQtdCall()[finalT_id][finalS_id].getValue().toString(),
                                getQtdCall()[t_id][s_id]));
                        getLblQtdPut_T01_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        getQtdPut()[finalT_id][finalS_id].getValue().toString(),
                                getQtdPut()[t_id][s_id]));
                        getLblQtdCallOrPut_T01_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
                            if (getQtdCallOrPut()[finalT_id][finalS_id].getValue().compareTo(1) >= 0)
                                getImgCallOrPut_T01_Op01().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                            else if (getQtdCallOrPut()[finalT_id][finalS_id].getValue().compareTo(-1) <= 0)
                                getImgCallOrPut_T01_Op01().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                            else
                                getImgCallOrPut_T01_Op01().setImage(null);
                            return String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue()));
                        }, getQtdCallOrPut()[t_id][s_id]));
                    }
                }
            }
            if (t_id == TIME_2M) {
                getLblTpn_T02_CandleTimeStart().textProperty().bind(Bindings.createStringBinding(() ->
                                Service_DataHoraCarimbo.getCarimboStr(getTimeCandleStart()[finalT_id].getValue(), DTF_HORA_MINUTOS),
                        getTimeCandleStart()[t_id]));
                getLblTpn_T02_TimeEnd().textProperty().bind(Bindings.createStringBinding(() ->
                                String.format("-%s s", getTimeCandleToClose()[finalT_id].getValue()),
                        getTimeCandleToClose()[t_id]));

                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    int finalS_id = s_id;
                    //*-*-* Op_01
                    if (s_id == SYMBOL_01) {
                        getLblSymbol_T02_Op01().setText(getSymbolObservableList().get(s_id).getSymbol());
                        getLblQtdCall_T02_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        getQtdCall()[finalT_id][finalS_id].getValue().toString(),
                                getQtdCall()[t_id][s_id]));
                        getLblQtdPut_T02_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        getQtdPut()[finalT_id][finalS_id].getValue().toString(),
                                getQtdPut()[t_id][s_id]));
                        getLblQtdCallOrPut_T02_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
                            if (getQtdCallOrPut()[finalT_id][finalS_id].getValue().compareTo(1) >= 0)
                                getImgCallOrPut_T02_Op01().setImage(new Image("image/ico/ic_seta_call_sobe_black_18dp.png"));
                            else if (getQtdCallOrPut()[finalT_id][finalS_id].getValue().compareTo(-1) <= 0)
                                getImgCallOrPut_T02_Op01().setImage(new Image("image/ico/ic_seta_put_desce_black_18dp.png"));
                            else
                                getImgCallOrPut_T02_Op01().setImage(null);
                            return String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue()));
                        }, getQtdCallOrPut()[t_id][s_id]));
                    }
                }
            }
        }

    }

    private void conectarTimesAtivos() {

        getTimeAtivo()[TIME_1M].bind(getChkTpn01_TimeAtivo().selectedProperty());
        getTimeAtivo()[TIME_2M].bind(getChkTpn02_TimeAtivo().selectedProperty());

        getChkTpn01_TimeAtivo().setSelected(true);
        getChkTpn02_TimeAtivo().setSelected(true);

    }

    private void contectarTabelaEmLista(int t_id, int s_id) {
        if (t_id == TIME_1M) {
            if (s_id == SYMBOL_01) {
                getTmodelTransactions()[t_id][s_id].setTbvTransaction(getTbvTransaction_T01_Op01());
                getTmodelTransactions()[t_id][s_id].tabela_preencher();
            }
//            if (s_id == SYMBOL_02) {
//                getTmodelTransactions()[t_id][SYMBOL_02].setTbvTransaction(getTbvTransaction_T01_Op02());
//                getTmodelTransactions()[t_id][SYMBOL_02].tabela_preencher();
//            }
//            if (s_id == SYMBOL_03) {
//                getTmodelTransactions()[t_id][SYMBOL_03].setTbvTransaction(getTbvTransaction_T01_Op03());
//                getTmodelTransactions()[t_id][SYMBOL_03].tabela_preencher();
//            }
//            if (s_id == SYMBOL_04) {
//                getTmodelTransactions()[t_id][SYMBOL_04].setTbvTransaction(getTbvTransaction_T01_Op04());
//                getTmodelTransactions()[t_id][SYMBOL_04].tabela_preencher();
//            }
//            if (s_id == SYMBOL_05) {
//                getTmodelTransactions()[t_id][SYMBOL_05].setTbvTransaction(getTbvTransaction_T01_Op05());
//                getTmodelTransactions()[t_id][SYMBOL_05].tabela_preencher();
//            }
//            if (s_id == SYMBOL_06) {
//                getTmodelTransactions()[t_id][SYMBOL_06].setTbvTransaction(getTbvTransaction_T01_Op06());
//                getTmodelTransactions()[t_id][SYMBOL_06].tabela_preencher();
//            }
//            if (s_id == SYMBOL_07) {
//                getTmodelTransactions()[t_id][SYMBOL_07].setTbvTransaction(getTbvTransaction_T01_Op07());
//                getTmodelTransactions()[t_id][SYMBOL_07].tabela_preencher();
//            }
//            if (s_id == SYMBOL_08) {
//                getTmodelTransactions()[t_id][SYMBOL_08].setTbvTransaction(getTbvTransaction_T01_Op08());
//                getTmodelTransactions()[t_id][SYMBOL_08].tabela_preencher();
//            }
//            if (s_id == SYMBOL_09) {
//                getTmodelTransactions()[t_id][SYMBOL_09].setTbvTransaction(getTbvTransaction_T01_Op09());
//                getTmodelTransactions()[t_id][SYMBOL_09].tabela_preencher();
//            }
//            if (s_id == SYMBOL_10) {
//                getTmodelTransactions()[t_id][SYMBOL_10].setTbvTransaction(getTbvTransaction_T01_Op10());
//                getTmodelTransactions()[t_id][SYMBOL_10].tabela_preencher();
//            }
//            if (s_id == SYMBOL_11) {
//                getTmodelTransactions()[t_id][SYMBOL_11].setTbvTransaction(getTbvTransaction_T01_Op11());
//                getTmodelTransactions()[t_id][SYMBOL_11].tabela_preencher();
//            }
//            if (s_id == SYMBOL_12) {
//                getTmodelTransactions()[t_id][SYMBOL_12].setTbvTransaction(getTbvTransaction_T01_Op12());
//                getTmodelTransactions()[t_id][SYMBOL_12].tabela_preencher();
//            }
        }
        if (t_id == TIME_2M) {
            if (s_id == SYMBOL_02) {
                getTmodelTransactions()[t_id][s_id].setTbvTransaction(getTbvTransaction_T02_Op01());
                getTmodelTransactions()[t_id][s_id].tabela_preencher();
            }
//            if (s_id == SYMBOL_02) {
//                getTmodelTransactions()[t_id][SYMBOL_02].setTbvTransaction(getTbvTransaction_T01_Op02());
//                getTmodelTransactions()[t_id][SYMBOL_02].tabela_preencher();
//            }
//            if (s_id == SYMBOL_03) {
//                getTmodelTransactions()[t_id][SYMBOL_03].setTbvTransaction(getTbvTransaction_T01_Op03());
//                getTmodelTransactions()[t_id][SYMBOL_03].tabela_preencher();
//            }
//            if (s_id == SYMBOL_04) {
//                getTmodelTransactions()[t_id][SYMBOL_04].setTbvTransaction(getTbvTransaction_T01_Op04());
//                getTmodelTransactions()[t_id][SYMBOL_04].tabela_preencher();
//            }
//            if (s_id == SYMBOL_05) {
//                getTmodelTransactions()[t_id][SYMBOL_05].setTbvTransaction(getTbvTransaction_T01_Op05());
//                getTmodelTransactions()[t_id][SYMBOL_05].tabela_preencher();
//            }
//            if (s_id == SYMBOL_06) {
//                getTmodelTransactions()[t_id][SYMBOL_06].setTbvTransaction(getTbvTransaction_T01_Op06());
//                getTmodelTransactions()[t_id][SYMBOL_06].tabela_preencher();
//            }
//            if (s_id == SYMBOL_07) {
//                getTmodelTransactions()[t_id][SYMBOL_07].setTbvTransaction(getTbvTransaction_T01_Op07());
//                getTmodelTransactions()[t_id][SYMBOL_07].tabela_preencher();
//            }
//            if (s_id == SYMBOL_08) {
//                getTmodelTransactions()[t_id][SYMBOL_08].setTbvTransaction(getTbvTransaction_T01_Op08());
//                getTmodelTransactions()[t_id][SYMBOL_08].tabela_preencher();
//            }
//            if (s_id == SYMBOL_09) {
//                getTmodelTransactions()[t_id][SYMBOL_09].setTbvTransaction(getTbvTransaction_T01_Op09());
//                getTmodelTransactions()[t_id][SYMBOL_09].tabela_preencher();
//            }
//            if (s_id == SYMBOL_10) {
//                getTmodelTransactions()[t_id][SYMBOL_10].setTbvTransaction(getTbvTransaction_T01_Op10());
//                getTmodelTransactions()[t_id][SYMBOL_10].tabela_preencher();
//            }
//            if (s_id == SYMBOL_11) {
//                getTmodelTransactions()[t_id][SYMBOL_11].setTbvTransaction(getTbvTransaction_T01_Op11());
//                getTmodelTransactions()[t_id][SYMBOL_11].tabela_preencher();
//            }
//            if (s_id == SYMBOL_12) {
//                getTmodelTransactions()[t_id][SYMBOL_12].setTbvTransaction(getTbvTransaction_T01_Op12());
//                getTmodelTransactions()[t_id][SYMBOL_12].tabela_preencher();
//            }
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


    }


    /**
     * Getters and Setters
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

    public static TICK_STYLE getTickStyle() {
        return tickStyle;
    }

    public static Robo getRobo() {
        return ROBO.get();
    }

    public static ObjectProperty<Robo> ROBOProperty() {
        return ROBO;
    }

    public static void setRobo(Robo robo) {
        ROBO.set(robo);
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

    public static String getParametrosUtilizadosRobo() {
        return parametrosUtilizadosRobo.get();
    }

    public static StringProperty parametrosUtilizadosRoboProperty() {
        return parametrosUtilizadosRobo;
    }

    public static void setParametrosUtilizadosRobo(String parametrosUtilizadosRobo) {
        Operacoes.parametrosUtilizadosRobo.set(parametrosUtilizadosRobo);
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

    public static ObjectProperty<Ohlc>[] getUltimoOhlcStr() {
        return ultimoOhlcStr;
    }

    public static void setUltimoOhlcStr(ObjectProperty<Ohlc>[] ultimoOhlcStr) {
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

    public static ObservableList<HistoricoDeCandles> getHistoricoDeCandlesObservableList() {
        return historicoDeCandlesObservableList;
    }

    public static void setHistoricoDeCandlesObservableList(ObservableList<HistoricoDeCandles> historicoDeCandlesObservableList) {
        Operacoes.historicoDeCandlesObservableList = historicoDeCandlesObservableList;
    }

    public static ObservableList<Transaction> getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction> transactionObservableList) {
        Operacoes.transactionObservableList = transactionObservableList;
    }

    public static TmodelTransactions[][] getTmodelTransactions() {
        return tmodelTransactions;
    }

    public static void setTmodelTransactions(TmodelTransactions[][] tmodelTransactions) {
        Operacoes.tmodelTransactions = tmodelTransactions;
    }

    public static FilteredList<Transaction>[][] getTransactionFilteredList() {
        return transactionFilteredList;
    }

    public static void setTransactionFilteredList(FilteredList<Transaction>[][] transactionFilteredList) {
        Operacoes.transactionFilteredList = transactionFilteredList;
    }

    public static BooleanProperty[][] getResultLastTransiction() {
        return resultLastTransiction;
    }

    public static void setResultLastTransiction(BooleanProperty[][] resultLastTransiction) {
        Operacoes.resultLastTransiction = resultLastTransiction;
    }

    public static BooleanProperty[] getTimeAtivo() {
        return timeAtivo;
    }

    public static void setTimeAtivo(BooleanProperty[] timeAtivo) {
        Operacoes.timeAtivo = timeAtivo;
    }

    public static boolean isContratoGerado() {
        return contratoGerado.get();
    }

    public static BooleanProperty contratoGeradoProperty() {
        return contratoGerado;
    }

    public static void setContratoGerado(boolean contratoGerado) {
        Operacoes.contratoGerado.set(contratoGerado);
    }

    public static boolean isDisableContratoBtn() {
        return disableContratoBtn.get();
    }

    public static BooleanProperty disableContratoBtnProperty() {
        return disableContratoBtn;
    }

    public static void setDisableContratoBtn(boolean disableContratoBtn) {
        Operacoes.disableContratoBtn.set(disableContratoBtn);
    }

    public static boolean isDisableIniciarBtn() {
        return disableIniciarBtn.get();
    }

    public static BooleanProperty disableIniciarBtnProperty() {
        return disableIniciarBtn;
    }

    public static void setDisableIniciarBtn(boolean disableIniciarBtn) {
        Operacoes.disableIniciarBtn.set(disableIniciarBtn);
    }

    public static boolean isDisablePausarBtn() {
        return disablePausarBtn.get();
    }

    public static BooleanProperty disablePausarBtnProperty() {
        return disablePausarBtn;
    }

    public static void setDisablePausarBtn(boolean disablePausarBtn) {
        Operacoes.disablePausarBtn.set(disablePausarBtn);
    }

    public static boolean isDisableStopBtn() {
        return disableStopBtn.get();
    }

    public static BooleanProperty disableStopBtnProperty() {
        return disableStopBtn;
    }

    public static void setDisableStopBtn(boolean disableStopBtn) {
        Operacoes.disableStopBtn.set(disableStopBtn);
    }

    public static boolean isRoboMonitorando() {
        return roboMonitorando.get();
    }

    public static BooleanProperty roboMonitorandoProperty() {
        return roboMonitorando;
    }

    public static void setRoboMonitorando(boolean roboMonitorando) {
        Operacoes.roboMonitorando.set(roboMonitorando);
    }

    public static boolean isRoboMonitorandoPausado() {
        return roboMonitorandoPausado.get();
    }

    public static BooleanProperty roboMonitorandoPausadoProperty() {
        return roboMonitorandoPausado;
    }

    public static void setRoboMonitorandoPausado(boolean roboMonitorandoPausado) {
        Operacoes.roboMonitorandoPausado.set(roboMonitorandoPausado);
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

    public static IntegerProperty[] getQtdCandlesEntrada() {
        return qtdCandlesEntrada;
    }

    public static void setQtdCandlesEntrada(IntegerProperty[] qtdCandlesEntrada) {
        Operacoes.qtdCandlesEntrada = qtdCandlesEntrada;
    }

    public static PriceProposal[][] getPriceProposal() {
        return priceProposal;
    }

    public static void setPriceProposal(PriceProposal[][] priceProposal) {
        Operacoes.priceProposal = priceProposal;
    }

    public static Integer getTime1m() {
        return TIME_1M;
    }

    public static Integer getTime2m() {
        return TIME_2M;
    }

    public static Integer getTime3m() {
        return TIME_3M;
    }

    public static Integer getTime5m() {
        return TIME_5M;
    }

    public static Integer getTime10m() {
        return TIME_10M;
    }

    public static Integer getTime15m() {
        return TIME_15M;
    }

    public static Integer getSymbol01() {
        return SYMBOL_01;
    }

    public static Integer getSymbol02() {
        return SYMBOL_02;
    }

    public static Integer getSymbol03() {
        return SYMBOL_03;
    }

    public static Integer getSymbol04() {
        return SYMBOL_04;
    }

    public static Integer getSymbol05() {
        return SYMBOL_05;
    }

    public static Integer getSymbol06() {
        return SYMBOL_06;
    }

    public static Integer getSymbol07() {
        return SYMBOL_07;
    }

    public static Integer getSymbol08() {
        return SYMBOL_08;
    }

    public static Integer getSymbol09() {
        return SYMBOL_09;
    }

    public static Integer getSymbol10() {
        return SYMBOL_10;
    }

    public static Integer getSymbol11() {
        return SYMBOL_11;
    }

    public static Integer getSymbol12() {
        return SYMBOL_12;
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

    public Label getLblTpn_T01_CandleTimeStart() {
        return lblTpn_T01_CandleTimeStart;
    }

    public void setLblTpn_T01_CandleTimeStart(Label lblTpn_T01_CandleTimeStart) {
        this.lblTpn_T01_CandleTimeStart = lblTpn_T01_CandleTimeStart;
    }

    public Label getLblTpn_T01_TimeEnd() {
        return lblTpn_T01_TimeEnd;
    }

    public void setLblTpn_T01_TimeEnd(Label lblTpn_T01_TimeEnd) {
        this.lblTpn_T01_TimeEnd = lblTpn_T01_TimeEnd;
    }

    public Label getLblTpn_T01_QtdStakes() {
        return lblTpn_T01_QtdStakes;
    }

    public void setLblTpn_T01_QtdStakes(Label lblTpn_T01_QtdStakes) {
        this.lblTpn_T01_QtdStakes = lblTpn_T01_QtdStakes;
    }

    public Label getLblTpn_T01_QtdWins() {
        return lblTpn_T01_QtdWins;
    }

    public void setLblTpn_T01_QtdWins(Label lblTpn_T01_QtdWins) {
        this.lblTpn_T01_QtdWins = lblTpn_T01_QtdWins;
    }

    public Label getLblTpn_T01_QtdLoss() {
        return lblTpn_T01_QtdLoss;
    }

    public void setLblTpn_T01_QtdLoss(Label lblTpn_T01_QtdLoss) {
        this.lblTpn_T01_QtdLoss = lblTpn_T01_QtdLoss;
    }

    public Label getLblTpn_T01_VlrIn() {
        return lblTpn_T01_VlrIn;
    }

    public void setLblTpn_T01_VlrIn(Label lblTpn_T01_VlrIn) {
        this.lblTpn_T01_VlrIn = lblTpn_T01_VlrIn;
    }

    public Label getLblTpn_T01_VlrOut() {
        return lblTpn_T01_VlrOut;
    }

    public void setLblTpn_T01_VlrOut(Label lblTpn_T01_VlrOut) {
        this.lblTpn_T01_VlrOut = lblTpn_T01_VlrOut;
    }

    public Label getLblTpn_T01_VlrDiff() {
        return lblTpn_T01_VlrDiff;
    }

    public void setLblTpn_T01_VlrDiff(Label lblTpn_T01_VlrDiff) {
        this.lblTpn_T01_VlrDiff = lblTpn_T01_VlrDiff;
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

    public TableView getTbvTransaction_T01_Op01() {
        return tbvTransaction_T01_Op01;
    }

    public void setTbvTransaction_T01_Op01(TableView tbvTransaction_T01_Op01) {
        this.tbvTransaction_T01_Op01 = tbvTransaction_T01_Op01;
    }

    public TitledPane getTpn_T02() {
        return tpn_T02;
    }

    public void setTpn_T02(TitledPane tpn_T02) {
        this.tpn_T02 = tpn_T02;
    }

    public JFXCheckBox getChkTpn02_TimeAtivo() {
        return chkTpn02_TimeAtivo;
    }

    public void setChkTpn02_TimeAtivo(JFXCheckBox chkTpn02_TimeAtivo) {
        this.chkTpn02_TimeAtivo = chkTpn02_TimeAtivo;
    }

    public Label getLblTpn_T02_CandleTimeStart() {
        return lblTpn_T02_CandleTimeStart;
    }

    public void setLblTpn_T02_CandleTimeStart(Label lblTpn_T02_CandleTimeStart) {
        this.lblTpn_T02_CandleTimeStart = lblTpn_T02_CandleTimeStart;
    }

    public Label getLblTpn_T02_TimeEnd() {
        return lblTpn_T02_TimeEnd;
    }

    public void setLblTpn_T02_TimeEnd(Label lblTpn_T02_TimeEnd) {
        this.lblTpn_T02_TimeEnd = lblTpn_T02_TimeEnd;
    }

    public Label getLblTpn_T02_QtdStakes() {
        return lblTpn_T02_QtdStakes;
    }

    public void setLblTpn_T02_QtdStakes(Label lblTpn_T02_QtdStakes) {
        this.lblTpn_T02_QtdStakes = lblTpn_T02_QtdStakes;
    }

    public Label getLblTpn_T02_QtdWins() {
        return lblTpn_T02_QtdWins;
    }

    public void setLblTpn_T02_QtdWins(Label lblTpn_T02_QtdWins) {
        this.lblTpn_T02_QtdWins = lblTpn_T02_QtdWins;
    }

    public Label getLblTpn_T02_QtdLoss() {
        return lblTpn_T02_QtdLoss;
    }

    public void setLblTpn_T02_QtdLoss(Label lblTpn_T02_QtdLoss) {
        this.lblTpn_T02_QtdLoss = lblTpn_T02_QtdLoss;
    }

    public Label getLblTpn_T02_VlrIn() {
        return lblTpn_T02_VlrIn;
    }

    public void setLblTpn_T02_VlrIn(Label lblTpn_T02_VlrIn) {
        this.lblTpn_T02_VlrIn = lblTpn_T02_VlrIn;
    }

    public Label getLblTpn_T02_VlrOut() {
        return lblTpn_T02_VlrOut;
    }

    public void setLblTpn_T02_VlrOut(Label lblTpn_T02_VlrOut) {
        this.lblTpn_T02_VlrOut = lblTpn_T02_VlrOut;
    }

    public Label getLblTpn_T02_VlrDiff() {
        return lblTpn_T02_VlrDiff;
    }

    public void setLblTpn_T02_VlrDiff(Label lblTpn_T02_VlrDiff) {
        this.lblTpn_T02_VlrDiff = lblTpn_T02_VlrDiff;
    }

    public Label getLblSymbol_T02_Op01() {
        return lblSymbol_T02_Op01;
    }

    public void setLblSymbol_T02_Op01(Label lblSymbol_T02_Op01) {
        this.lblSymbol_T02_Op01 = lblSymbol_T02_Op01;
    }

    public Label getLblQtdCall_T02_Op01() {
        return lblQtdCall_T02_Op01;
    }

    public void setLblQtdCall_T02_Op01(Label lblQtdCall_T02_Op01) {
        this.lblQtdCall_T02_Op01 = lblQtdCall_T02_Op01;
    }

    public Label getLblQtdPut_T02_Op01() {
        return lblQtdPut_T02_Op01;
    }

    public void setLblQtdPut_T02_Op01(Label lblQtdPut_T02_Op01) {
        this.lblQtdPut_T02_Op01 = lblQtdPut_T02_Op01;
    }

    public Label getLblQtdCallOrPut_T02_Op01() {
        return lblQtdCallOrPut_T02_Op01;
    }

    public void setLblQtdCallOrPut_T02_Op01(Label lblQtdCallOrPut_T02_Op01) {
        this.lblQtdCallOrPut_T02_Op01 = lblQtdCallOrPut_T02_Op01;
    }

    public ImageView getImgCallOrPut_T02_Op01() {
        return imgCallOrPut_T02_Op01;
    }

    public void setImgCallOrPut_T02_Op01(ImageView imgCallOrPut_T02_Op01) {
        this.imgCallOrPut_T02_Op01 = imgCallOrPut_T02_Op01;
    }

    public Label getLblQtdStakes_T02_Op01() {
        return lblQtdStakes_T02_Op01;
    }

    public void setLblQtdStakes_T02_Op01(Label lblQtdStakes_T02_Op01) {
        this.lblQtdStakes_T02_Op01 = lblQtdStakes_T02_Op01;
    }

    public Label getLblQtdWins_T02_Op01() {
        return lblQtdWins_T02_Op01;
    }

    public void setLblQtdWins_T02_Op01(Label lblQtdWins_T02_Op01) {
        this.lblQtdWins_T02_Op01 = lblQtdWins_T02_Op01;
    }

    public Label getLblQtdLoss_T02_Op01() {
        return lblQtdLoss_T02_Op01;
    }

    public void setLblQtdLoss_T02_Op01(Label lblQtdLoss_T02_Op01) {
        this.lblQtdLoss_T02_Op01 = lblQtdLoss_T02_Op01;
    }

    public Label getLblVlrIn_T02_Op01() {
        return lblVlrIn_T02_Op01;
    }

    public void setLblVlrIn_T02_Op01(Label lblVlrIn_T02_Op01) {
        this.lblVlrIn_T02_Op01 = lblVlrIn_T02_Op01;
    }

    public Label getLblVlrOut_T02_Op01() {
        return lblVlrOut_T02_Op01;
    }

    public void setLblVlrOut_T02_Op01(Label lblVlrOut_T02_Op01) {
        this.lblVlrOut_T02_Op01 = lblVlrOut_T02_Op01;
    }

    public Label getLblVlrDiff_T02_Op01() {
        return lblVlrDiff_T02_Op01;
    }

    public void setLblVlrDiff_T02_Op01(Label lblVlrDiff_T02_Op01) {
        this.lblVlrDiff_T02_Op01 = lblVlrDiff_T02_Op01;
    }

    public TableView getTbvTransaction_T02_Op01() {
        return tbvTransaction_T02_Op01;
    }

    public void setTbvTransaction_T02_Op01(TableView tbvTransaction_T02_Op01) {
        this.tbvTransaction_T02_Op01 = tbvTransaction_T02_Op01;
    }

    public static List<Symbol> getSymbolList() {
        return SYMBOL_LIST;
    }

    public static int getQtdCandlesAnalise() {
        return qtdCandlesAnalise.get();
    }

    public static IntegerProperty qtdCandlesAnaliseProperty() {
        return qtdCandlesAnalise;
    }

    public static void setQtdCandlesAnalise(int qtdCandlesAnalise) {
        Operacoes.qtdCandlesAnalise.set(qtdCandlesAnalise);
    }

    public static FilteredList<HistoricoDeCandles>[][] getHistoricoDeCandlesFilteredList() {
        return historicoDeCandlesFilteredList;
    }

    public static void setHistoricoDeCandlesFilteredList(FilteredList<HistoricoDeCandles>[][] historicoDeCandlesFilteredList) {
        Operacoes.historicoDeCandlesFilteredList = historicoDeCandlesFilteredList;
    }

    public static ObjectProperty<Ohlc>[][] getTestaLastCandle() {
        return testaLastCandle;
    }

    public static void setTestaLastCandle(ObjectProperty<Ohlc>[][] testaLastCandle) {
        Operacoes.testaLastCandle = testaLastCandle;
    }


}
