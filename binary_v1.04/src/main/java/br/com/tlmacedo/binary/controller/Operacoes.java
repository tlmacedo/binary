package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.dao.*;
import br.com.tlmacedo.binary.model.enums.*;
import br.com.tlmacedo.binary.model.tableModel.TmodelTransacoes;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Service_DataTime;
import br.com.tlmacedo.binary.services.Service_Mascara;
import br.com.tlmacedo.binary.services.Util_Json;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static br.com.tlmacedo.binary.interfaces.Constants.*;
import static br.com.tlmacedo.binary.model.enums.TICK_STYLE.CANDLES;


/**
 * TelegramId: 1025551558
 */
public class Operacoes implements Initializable {

    /**
     * Objetos DAO conecta com banco de dados
     */
    //** Banco de Dados **
    static TimeFrameDAO timeFrameDAO = new TimeFrameDAO();
    static SymbolDAO symbolDAO = new SymbolDAO();
    static ContaTokenDAO contaTokenDAO = new ContaTokenDAO();
    static TransacoesDAO transacoesDAO = new TransacoesDAO();
    static TransactionDAO transactionDAO = new TransactionDAO();
    static HistoricoDeTicksDAO historicoDeTicksDAO = new HistoricoDeTicksDAO();
    static LogSistemaStartDAO logSistemaStartDAO = new LogSistemaStartDAO();


    /**
     * Identificação de volatilidades
     */
    //** Variaveis de identificacoes das volatilidades
    static final ObservableList<TimeFrame> TIME_FRAME_OBSERVABLE_LIST = FXCollections.observableArrayList(
            getTimeFrameDAO().getAll(TimeFrame.class, "ativo=1", null));
    static final ObservableList<Symbol> SYMBOL_OBSERVABLE_LIST = FXCollections.observableArrayList(
            getSymbolDAO().getAll(Symbol.class, "ativo=1", null));

    /**
     * Contas corretora
     */
    static final ObservableList<ContaToken> CONTA_TOKEN_OBSERVABLE_LIST = FXCollections.observableArrayList(
            getContaTokenDAO().getAll(ContaToken.class, "tokenAtivo=1", "cReal, moeda, descricao"));
    static ObjectProperty<ContaToken> contaToken = new SimpleObjectProperty<>();
    static ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>();

    /**
     * Conexão e operação com WebService
     */
    static BooleanProperty wsConectado = new SimpleBooleanProperty(false);
    static final ObjectProperty<WSClient> WS_CLIENT_OBJECT_PROPERTY = new SimpleObjectProperty<>(new WSClient());
    static final Integer typeCandle_id = CANDLES.getCod();

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
    static ObjectProperty<BigDecimal> saldoFinal = new SimpleObjectProperty<>(BigDecimal.ZERO);

    /**
     * Variaveis de informações para operadores
     */
    //** Variaveis **
    static IntegerProperty qtdCandlesAnalise = new SimpleIntegerProperty();
    static ObjectProperty<Ohlc>[] ultimoOhlcStr = new ObjectProperty[getSymbolObservableList().size()];

    static StringProperty[] timeCandleStart = new StringProperty[getTimeFrameObservableList().size()];
    static IntegerProperty[] timeCandleToClose = new IntegerProperty[getTimeFrameObservableList().size()];

    static BooleanProperty[][] firstBuy = new BooleanProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdCallOrPut = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdCall = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
    static IntegerProperty[][] qtdPut = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    //** Listas **
    static ObservableList<HistoricoDeTicks> historicoDeTicksObservableList = FXCollections.observableArrayList();
    static ObservableList<HistoricoDeCandles> historicoDeCandlesObservableList = FXCollections.observableArrayList();
    static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();

    static FilteredList<HistoricoDeCandles>[][] historicoDeCandlesFilteredList = new FilteredList[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
    static FilteredList<Transacoes>[] transacoesFilteredList_tFrame = new FilteredList[getTimeFrameObservableList().size()];
    static FilteredList<Transacoes>[][] transacoesFilteredList_symbol = new FilteredList[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
//    static FilteredList<Transacoes>[] transacoesTimerFilteredList = new FilteredList[getTimeFrameObservableList().size()];
//    static FilteredList<Transacoes>[] transacoesSymbolFilteredList = new FilteredList[getSymbolObservableList().size()];

    static TmodelTransacoes[][] tmodelTransacoes = new TmodelTransacoes[getTimeFrameObservableList().size()][getSymbolObservableList().size()];


    //** Operações com Robos **
    static BooleanProperty[] timeAtivo = new BooleanProperty[getTimeFrameObservableList().size()];
    static BooleanProperty contratoGerado = new SimpleBooleanProperty(false);
    static BooleanProperty disableContratoBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disableIniciarBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disablePausarBtn = new SimpleBooleanProperty(true);
    static BooleanProperty disableStopBtn = new SimpleBooleanProperty(true);
    static BooleanProperty roboMonitorando = new SimpleBooleanProperty(false);
    static BooleanProperty roboMonitorandoPausado = new SimpleBooleanProperty(false);

    static IntegerProperty[] qtdCandlesEntrada = new IntegerProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[] porcMartingale = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[] vlrStkPadrao = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrStkContrato = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrLossAcumulado = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];


    static IntegerProperty qtdStakes = new SimpleIntegerProperty(0);
    static IntegerProperty[] qtdTimeFrameStakes = new IntegerProperty[getTimeFrameObservableList().size()];
    static IntegerProperty[][] qtdTframeSymbolStakes = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static IntegerProperty qtdStakesWins = new SimpleIntegerProperty(0);
    static IntegerProperty[] qtdTimeFrameStakesWins = new IntegerProperty[getTimeFrameObservableList().size()];
    static IntegerProperty[][] qtdTframeSymbolStakesWins = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static IntegerProperty qtdStakesLoss = new SimpleIntegerProperty(0);
    static IntegerProperty[] qtdTimeFrameStakesLoss = new IntegerProperty[getTimeFrameObservableList().size()];
    static IntegerProperty[][] qtdTframeSymbolStakesLoss = new IntegerProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal> vlrStakesIn = new SimpleObjectProperty<>(BigDecimal.ZERO);
    static ObjectProperty<BigDecimal>[] vlrTimeFrameStakesIn = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolStakesIn = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal> vlrStakesOut = new SimpleObjectProperty<>(BigDecimal.ZERO);
    static ObjectProperty<BigDecimal>[] vlrTimeFrameStakesOut = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolStakesOut = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal> vlrStakesDiff = new SimpleObjectProperty<>(BigDecimal.ZERO);
    static ObjectProperty<BigDecimal>[] vlrTimeFrameStakesDiff = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolStakesDiff = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];


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
    public JFXCheckBox chkTpn_T01_TimeAtivo;
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

    // Time_02 *-*-*
    public TitledPane tpn_T02;
    public JFXCheckBox chkTpn_T02_TimeAtivo;
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
    public TableView tbvTransacoes_T02_Op01;
    // Time_02 *-*-* Symbol_02
    public Label lblSymbol_T02_Op02;
    public Label lblQtdCall_T02_Op02;
    public Label lblQtdPut_T02_Op02;
    public Label lblQtdCallOrPut_T02_Op02;
    public ImageView imgCallOrPut_T02_Op02;
    public Label lblQtdStakes_T02_Op02;
    public Label lblQtdWins_T02_Op02;
    public Label lblQtdLoss_T02_Op02;
    public Label lblVlrIn_T02_Op02;
    public Label lblVlrOut_T02_Op02;
    public Label lblVlrDiff_T02_Op02;
    public TableView tbvTransacoes_T02_Op02;
    // Time_02 *-*-* Symbol_03
    public Label lblSymbol_T02_Op03;
    public Label lblQtdCall_T02_Op03;
    public Label lblQtdPut_T02_Op03;
    public Label lblQtdCallOrPut_T02_Op03;
    public ImageView imgCallOrPut_T02_Op03;
    public Label lblQtdStakes_T02_Op03;
    public Label lblQtdWins_T02_Op03;
    public Label lblQtdLoss_T02_Op03;
    public Label lblVlrIn_T02_Op03;
    public Label lblVlrOut_T02_Op03;
    public Label lblVlrDiff_T02_Op03;
    public TableView tbvTransacoes_T02_Op03;
    // Time_02 *-*-* Symbol_04
    public Label lblSymbol_T02_Op04;
    public Label lblQtdCall_T02_Op04;
    public Label lblQtdPut_T02_Op04;
    public Label lblQtdCallOrPut_T02_Op04;
    public ImageView imgCallOrPut_T02_Op04;
    public Label lblQtdStakes_T02_Op04;
    public Label lblQtdWins_T02_Op04;
    public Label lblQtdLoss_T02_Op04;
    public Label lblVlrIn_T02_Op04;
    public Label lblVlrOut_T02_Op04;
    public Label lblVlrDiff_T02_Op04;
    public TableView tbvTransacoes_T02_Op04;
    // Time_02 *-*-* Symbol_05
    public Label lblSymbol_T02_Op05;
    public Label lblQtdCall_T02_Op05;
    public Label lblQtdPut_T02_Op05;
    public Label lblQtdCallOrPut_T02_Op05;
    public ImageView imgCallOrPut_T02_Op05;
    public Label lblQtdStakes_T02_Op05;
    public Label lblQtdWins_T02_Op05;
    public Label lblQtdLoss_T02_Op05;
    public Label lblVlrIn_T02_Op05;
    public Label lblVlrOut_T02_Op05;
    public Label lblVlrDiff_T02_Op05;
    public TableView tbvTransacoes_T02_Op05;
    // Time_02 *-*-* Symbol_06
    public Label lblSymbol_T02_Op06;
    public Label lblQtdCall_T02_Op06;
    public Label lblQtdPut_T02_Op06;
    public Label lblQtdCallOrPut_T02_Op06;
    public ImageView imgCallOrPut_T02_Op06;
    public Label lblQtdStakes_T02_Op06;
    public Label lblQtdWins_T02_Op06;
    public Label lblQtdLoss_T02_Op06;
    public Label lblVlrIn_T02_Op06;
    public Label lblVlrOut_T02_Op06;
    public Label lblVlrDiff_T02_Op06;
    public TableView tbvTransacoes_T02_Op06;
    // Time_02 *-*-* Symbol_07
    public Label lblSymbol_T02_Op07;
    public Label lblQtdCall_T02_Op07;
    public Label lblQtdPut_T02_Op07;
    public Label lblQtdCallOrPut_T02_Op07;
    public ImageView imgCallOrPut_T02_Op07;
    public Label lblQtdStakes_T02_Op07;
    public Label lblQtdWins_T02_Op07;
    public Label lblQtdLoss_T02_Op07;
    public Label lblVlrIn_T02_Op07;
    public Label lblVlrOut_T02_Op07;
    public Label lblVlrDiff_T02_Op07;
    public TableView tbvTransacoes_T02_Op07;
    // Time_02 *-*-* Symbol_08
    public Label lblSymbol_T02_Op08;
    public Label lblQtdCall_T02_Op08;
    public Label lblQtdPut_T02_Op08;
    public Label lblQtdCallOrPut_T02_Op08;
    public ImageView imgCallOrPut_T02_Op08;
    public Label lblQtdStakes_T02_Op08;
    public Label lblQtdWins_T02_Op08;
    public Label lblQtdLoss_T02_Op08;
    public Label lblVlrIn_T02_Op08;
    public Label lblVlrOut_T02_Op08;
    public Label lblVlrDiff_T02_Op08;
    public TableView tbvTransacoes_T02_Op08;
    // Time_02 *-*-* Symbol_09
    public Label lblSymbol_T02_Op09;
    public Label lblQtdCall_T02_Op09;
    public Label lblQtdPut_T02_Op09;
    public Label lblQtdCallOrPut_T02_Op09;
    public ImageView imgCallOrPut_T02_Op09;
    public Label lblQtdStakes_T02_Op09;
    public Label lblQtdWins_T02_Op09;
    public Label lblQtdLoss_T02_Op09;
    public Label lblVlrIn_T02_Op09;
    public Label lblVlrOut_T02_Op09;
    public Label lblVlrDiff_T02_Op09;
    public TableView tbvTransacoes_T02_Op09;
    // Time_02 *-*-* Symbol_10
    public Label lblSymbol_T02_Op10;
    public Label lblQtdCall_T02_Op10;
    public Label lblQtdPut_T02_Op10;
    public Label lblQtdCallOrPut_T02_Op10;
    public ImageView imgCallOrPut_T02_Op10;
    public Label lblQtdStakes_T02_Op10;
    public Label lblQtdWins_T02_Op10;
    public Label lblQtdLoss_T02_Op10;
    public Label lblVlrIn_T02_Op10;
    public Label lblVlrOut_T02_Op10;
    public Label lblVlrDiff_T02_Op10;
    public TableView tbvTransacoes_T02_Op10;
    // Time_02 *-*-* Symbol_11
    public Label lblSymbol_T02_Op11;
    public Label lblQtdCall_T02_Op11;
    public Label lblQtdPut_T02_Op11;
    public Label lblQtdCallOrPut_T02_Op11;
    public ImageView imgCallOrPut_T02_Op11;
    public Label lblQtdStakes_T02_Op11;
    public Label lblQtdWins_T02_Op11;
    public Label lblQtdLoss_T02_Op11;
    public Label lblVlrIn_T02_Op11;
    public Label lblVlrOut_T02_Op11;
    public Label lblVlrDiff_T02_Op11;
    public TableView tbvTransacoes_T02_Op11;
    // Time_02 *-*-* Symbol_12
    public Label lblSymbol_T02_Op12;
    public Label lblQtdCall_T02_Op12;
    public Label lblQtdPut_T02_Op12;
    public Label lblQtdCallOrPut_T02_Op12;
    public ImageView imgCallOrPut_T02_Op12;
    public Label lblQtdStakes_T02_Op12;
    public Label lblQtdWins_T02_Op12;
    public Label lblQtdLoss_T02_Op12;
    public Label lblVlrIn_T02_Op12;
    public Label lblVlrOut_T02_Op12;
    public Label lblVlrDiff_T02_Op12;
    public TableView tbvTransacoes_T02_Op12;

    // Time_03 *-*-*
    public TitledPane tpn_T03;
    public JFXCheckBox chkTpn_T03_TimeAtivo;
    public Label lblTpn_T03_CandleTimeStart;
    public Label lblTpn_T03_TimeEnd;
    public Label lblTpn_T03_QtdStakes;
    public Label lblTpn_T03_QtdWins;
    public Label lblTpn_T03_QtdLoss;
    public Label lblTpn_T03_VlrIn;
    public Label lblTpn_T03_VlrOut;
    public Label lblTpn_T03_VlrDiff;
    // Time_03 *-*-* Symbol_01
    public Label lblSymbol_T03_Op01;
    public Label lblQtdCall_T03_Op01;
    public Label lblQtdPut_T03_Op01;
    public Label lblQtdCallOrPut_T03_Op01;
    public ImageView imgCallOrPut_T03_Op01;
    public Label lblQtdStakes_T03_Op01;
    public Label lblQtdWins_T03_Op01;
    public Label lblQtdLoss_T03_Op01;
    public Label lblVlrIn_T03_Op01;
    public Label lblVlrOut_T03_Op01;
    public Label lblVlrDiff_T03_Op01;
    public TableView tbvTransacoes_T03_Op01;
    // Time_03 *-*-* Symbol_02
    public Label lblSymbol_T03_Op02;
    public Label lblQtdCall_T03_Op02;
    public Label lblQtdPut_T03_Op02;
    public Label lblQtdCallOrPut_T03_Op02;
    public ImageView imgCallOrPut_T03_Op02;
    public Label lblQtdStakes_T03_Op02;
    public Label lblQtdWins_T03_Op02;
    public Label lblQtdLoss_T03_Op02;
    public Label lblVlrIn_T03_Op02;
    public Label lblVlrOut_T03_Op02;
    public Label lblVlrDiff_T03_Op02;
    public TableView tbvTransacoes_T03_Op02;
    // Time_03 *-*-* Symbol_03
    public Label lblSymbol_T03_Op03;
    public Label lblQtdCall_T03_Op03;
    public Label lblQtdPut_T03_Op03;
    public Label lblQtdCallOrPut_T03_Op03;
    public ImageView imgCallOrPut_T03_Op03;
    public Label lblQtdStakes_T03_Op03;
    public Label lblQtdWins_T03_Op03;
    public Label lblQtdLoss_T03_Op03;
    public Label lblVlrIn_T03_Op03;
    public Label lblVlrOut_T03_Op03;
    public Label lblVlrDiff_T03_Op03;
    public TableView tbvTransacoes_T03_Op03;
    // Time_03 *-*-* Symbol_04
    public Label lblSymbol_T03_Op04;
    public Label lblQtdCall_T03_Op04;
    public Label lblQtdPut_T03_Op04;
    public Label lblQtdCallOrPut_T03_Op04;
    public ImageView imgCallOrPut_T03_Op04;
    public Label lblQtdStakes_T03_Op04;
    public Label lblQtdWins_T03_Op04;
    public Label lblQtdLoss_T03_Op04;
    public Label lblVlrIn_T03_Op04;
    public Label lblVlrOut_T03_Op04;
    public Label lblVlrDiff_T03_Op04;
    public TableView tbvTransacoes_T03_Op04;
    // Time_03 *-*-* Symbol_05
    public Label lblSymbol_T03_Op05;
    public Label lblQtdCall_T03_Op05;
    public Label lblQtdPut_T03_Op05;
    public Label lblQtdCallOrPut_T03_Op05;
    public ImageView imgCallOrPut_T03_Op05;
    public Label lblQtdStakes_T03_Op05;
    public Label lblQtdWins_T03_Op05;
    public Label lblQtdLoss_T03_Op05;
    public Label lblVlrIn_T03_Op05;
    public Label lblVlrOut_T03_Op05;
    public Label lblVlrDiff_T03_Op05;
    public TableView tbvTransacoes_T03_Op05;
    // Time_03 *-*-* Symbol_06
    public Label lblSymbol_T03_Op06;
    public Label lblQtdCall_T03_Op06;
    public Label lblQtdPut_T03_Op06;
    public Label lblQtdCallOrPut_T03_Op06;
    public ImageView imgCallOrPut_T03_Op06;
    public Label lblQtdStakes_T03_Op06;
    public Label lblQtdWins_T03_Op06;
    public Label lblQtdLoss_T03_Op06;
    public Label lblVlrIn_T03_Op06;
    public Label lblVlrOut_T03_Op06;
    public Label lblVlrDiff_T03_Op06;
    public TableView tbvTransacoes_T03_Op06;
    // Time_03 *-*-* Symbol_07
    public Label lblSymbol_T03_Op07;
    public Label lblQtdCall_T03_Op07;
    public Label lblQtdPut_T03_Op07;
    public Label lblQtdCallOrPut_T03_Op07;
    public ImageView imgCallOrPut_T03_Op07;
    public Label lblQtdStakes_T03_Op07;
    public Label lblQtdWins_T03_Op07;
    public Label lblQtdLoss_T03_Op07;
    public Label lblVlrIn_T03_Op07;
    public Label lblVlrOut_T03_Op07;
    public Label lblVlrDiff_T03_Op07;
    public TableView tbvTransacoes_T03_Op07;
    // Time_03 *-*-* Symbol_08
    public Label lblSymbol_T03_Op08;
    public Label lblQtdCall_T03_Op08;
    public Label lblQtdPut_T03_Op08;
    public Label lblQtdCallOrPut_T03_Op08;
    public ImageView imgCallOrPut_T03_Op08;
    public Label lblQtdStakes_T03_Op08;
    public Label lblQtdWins_T03_Op08;
    public Label lblQtdLoss_T03_Op08;
    public Label lblVlrIn_T03_Op08;
    public Label lblVlrOut_T03_Op08;
    public Label lblVlrDiff_T03_Op08;
    public TableView tbvTransacoes_T03_Op08;
    // Time_03 *-*-* Symbol_09
    public Label lblSymbol_T03_Op09;
    public Label lblQtdCall_T03_Op09;
    public Label lblQtdPut_T03_Op09;
    public Label lblQtdCallOrPut_T03_Op09;
    public ImageView imgCallOrPut_T03_Op09;
    public Label lblQtdStakes_T03_Op09;
    public Label lblQtdWins_T03_Op09;
    public Label lblQtdLoss_T03_Op09;
    public Label lblVlrIn_T03_Op09;
    public Label lblVlrOut_T03_Op09;
    public Label lblVlrDiff_T03_Op09;
    public TableView tbvTransacoes_T03_Op09;
    // Time_03 *-*-* Symbol_10
    public Label lblSymbol_T03_Op10;
    public Label lblQtdCall_T03_Op10;
    public Label lblQtdPut_T03_Op10;
    public Label lblQtdCallOrPut_T03_Op10;
    public ImageView imgCallOrPut_T03_Op10;
    public Label lblQtdStakes_T03_Op10;
    public Label lblQtdWins_T03_Op10;
    public Label lblQtdLoss_T03_Op10;
    public Label lblVlrIn_T03_Op10;
    public Label lblVlrOut_T03_Op10;
    public Label lblVlrDiff_T03_Op10;
    public TableView tbvTransacoes_T03_Op10;
    // Time_03 *-*-* Symbol_11
    public Label lblSymbol_T03_Op11;
    public Label lblQtdCall_T03_Op11;
    public Label lblQtdPut_T03_Op11;
    public Label lblQtdCallOrPut_T03_Op11;
    public ImageView imgCallOrPut_T03_Op11;
    public Label lblQtdStakes_T03_Op11;
    public Label lblQtdWins_T03_Op11;
    public Label lblQtdLoss_T03_Op11;
    public Label lblVlrIn_T03_Op11;
    public Label lblVlrOut_T03_Op11;
    public Label lblVlrDiff_T03_Op11;
    public TableView tbvTransacoes_T03_Op11;
    // Time_03 *-*-* Symbol_12
    public Label lblSymbol_T03_Op12;
    public Label lblQtdCall_T03_Op12;
    public Label lblQtdPut_T03_Op12;
    public Label lblQtdCallOrPut_T03_Op12;
    public ImageView imgCallOrPut_T03_Op12;
    public Label lblQtdStakes_T03_Op12;
    public Label lblQtdWins_T03_Op12;
    public Label lblQtdLoss_T03_Op12;
    public Label lblVlrIn_T03_Op12;
    public Label lblVlrOut_T03_Op12;
    public Label lblVlrDiff_T03_Op12;
    public TableView tbvTransacoes_T03_Op12;

    // Time_04 *-*-*
    public TitledPane tpn_T04;
    public JFXCheckBox chkTpn_T04_TimeAtivo;
    public Label lblTpn_T04_CandleTimeStart;
    public Label lblTpn_T04_TimeEnd;
    public Label lblTpn_T04_QtdStakes;
    public Label lblTpn_T04_QtdWins;
    public Label lblTpn_T04_QtdLoss;
    public Label lblTpn_T04_VlrIn;
    public Label lblTpn_T04_VlrOut;
    public Label lblTpn_T04_VlrDiff;
    // Time_04 *-*-* Symbol_01
    public Label lblSymbol_T04_Op01;
    public Label lblQtdCall_T04_Op01;
    public Label lblQtdPut_T04_Op01;
    public Label lblQtdCallOrPut_T04_Op01;
    public ImageView imgCallOrPut_T04_Op01;
    public Label lblQtdStakes_T04_Op01;
    public Label lblQtdWins_T04_Op01;
    public Label lblQtdLoss_T04_Op01;
    public Label lblVlrIn_T04_Op01;
    public Label lblVlrOut_T04_Op01;
    public Label lblVlrDiff_T04_Op01;
    public TableView tbvTransacoes_T04_Op01;
    // Time_04 *-*-* Symbol_02
    public Label lblSymbol_T04_Op02;
    public Label lblQtdCall_T04_Op02;
    public Label lblQtdPut_T04_Op02;
    public Label lblQtdCallOrPut_T04_Op02;
    public ImageView imgCallOrPut_T04_Op02;
    public Label lblQtdStakes_T04_Op02;
    public Label lblQtdWins_T04_Op02;
    public Label lblQtdLoss_T04_Op02;
    public Label lblVlrIn_T04_Op02;
    public Label lblVlrOut_T04_Op02;
    public Label lblVlrDiff_T04_Op02;
    public TableView tbvTransacoes_T04_Op02;
    // Time_04 *-*-* Symbol_03
    public Label lblSymbol_T04_Op03;
    public Label lblQtdCall_T04_Op03;
    public Label lblQtdPut_T04_Op03;
    public Label lblQtdCallOrPut_T04_Op03;
    public ImageView imgCallOrPut_T04_Op03;
    public Label lblQtdStakes_T04_Op03;
    public Label lblQtdWins_T04_Op03;
    public Label lblQtdLoss_T04_Op03;
    public Label lblVlrIn_T04_Op03;
    public Label lblVlrOut_T04_Op03;
    public Label lblVlrDiff_T04_Op03;
    public TableView tbvTransacoes_T04_Op03;
    // Time_04 *-*-* Symbol_04
    public Label lblSymbol_T04_Op04;
    public Label lblQtdCall_T04_Op04;
    public Label lblQtdPut_T04_Op04;
    public Label lblQtdCallOrPut_T04_Op04;
    public ImageView imgCallOrPut_T04_Op04;
    public Label lblQtdStakes_T04_Op04;
    public Label lblQtdWins_T04_Op04;
    public Label lblQtdLoss_T04_Op04;
    public Label lblVlrIn_T04_Op04;
    public Label lblVlrOut_T04_Op04;
    public Label lblVlrDiff_T04_Op04;
    public TableView tbvTransacoes_T04_Op04;
    // Time_04 *-*-* Symbol_05
    public Label lblSymbol_T04_Op05;
    public Label lblQtdCall_T04_Op05;
    public Label lblQtdPut_T04_Op05;
    public Label lblQtdCallOrPut_T04_Op05;
    public ImageView imgCallOrPut_T04_Op05;
    public Label lblQtdStakes_T04_Op05;
    public Label lblQtdWins_T04_Op05;
    public Label lblQtdLoss_T04_Op05;
    public Label lblVlrIn_T04_Op05;
    public Label lblVlrOut_T04_Op05;
    public Label lblVlrDiff_T04_Op05;
    public TableView tbvTransacoes_T04_Op05;
    // Time_04 *-*-* Symbol_06
    public Label lblSymbol_T04_Op06;
    public Label lblQtdCall_T04_Op06;
    public Label lblQtdPut_T04_Op06;
    public Label lblQtdCallOrPut_T04_Op06;
    public ImageView imgCallOrPut_T04_Op06;
    public Label lblQtdStakes_T04_Op06;
    public Label lblQtdWins_T04_Op06;
    public Label lblQtdLoss_T04_Op06;
    public Label lblVlrIn_T04_Op06;
    public Label lblVlrOut_T04_Op06;
    public Label lblVlrDiff_T04_Op06;
    public TableView tbvTransacoes_T04_Op06;
    // Time_04 *-*-* Symbol_07
    public Label lblSymbol_T04_Op07;
    public Label lblQtdCall_T04_Op07;
    public Label lblQtdPut_T04_Op07;
    public Label lblQtdCallOrPut_T04_Op07;
    public ImageView imgCallOrPut_T04_Op07;
    public Label lblQtdStakes_T04_Op07;
    public Label lblQtdWins_T04_Op07;
    public Label lblQtdLoss_T04_Op07;
    public Label lblVlrIn_T04_Op07;
    public Label lblVlrOut_T04_Op07;
    public Label lblVlrDiff_T04_Op07;
    public TableView tbvTransacoes_T04_Op07;
    // Time_04 *-*-* Symbol_08
    public Label lblSymbol_T04_Op08;
    public Label lblQtdCall_T04_Op08;
    public Label lblQtdPut_T04_Op08;
    public Label lblQtdCallOrPut_T04_Op08;
    public ImageView imgCallOrPut_T04_Op08;
    public Label lblQtdStakes_T04_Op08;
    public Label lblQtdWins_T04_Op08;
    public Label lblQtdLoss_T04_Op08;
    public Label lblVlrIn_T04_Op08;
    public Label lblVlrOut_T04_Op08;
    public Label lblVlrDiff_T04_Op08;
    public TableView tbvTransacoes_T04_Op08;
    // Time_04 *-*-* Symbol_09
    public Label lblSymbol_T04_Op09;
    public Label lblQtdCall_T04_Op09;
    public Label lblQtdPut_T04_Op09;
    public Label lblQtdCallOrPut_T04_Op09;
    public ImageView imgCallOrPut_T04_Op09;
    public Label lblQtdStakes_T04_Op09;
    public Label lblQtdWins_T04_Op09;
    public Label lblQtdLoss_T04_Op09;
    public Label lblVlrIn_T04_Op09;
    public Label lblVlrOut_T04_Op09;
    public Label lblVlrDiff_T04_Op09;
    public TableView tbvTransacoes_T04_Op09;
    // Time_04 *-*-* Symbol_10
    public Label lblSymbol_T04_Op10;
    public Label lblQtdCall_T04_Op10;
    public Label lblQtdPut_T04_Op10;
    public Label lblQtdCallOrPut_T04_Op10;
    public ImageView imgCallOrPut_T04_Op10;
    public Label lblQtdStakes_T04_Op10;
    public Label lblQtdWins_T04_Op10;
    public Label lblQtdLoss_T04_Op10;
    public Label lblVlrIn_T04_Op10;
    public Label lblVlrOut_T04_Op10;
    public Label lblVlrDiff_T04_Op10;
    public TableView tbvTransacoes_T04_Op10;
    // Time_04 *-*-* Symbol_11
    public Label lblSymbol_T04_Op11;
    public Label lblQtdCall_T04_Op11;
    public Label lblQtdPut_T04_Op11;
    public Label lblQtdCallOrPut_T04_Op11;
    public ImageView imgCallOrPut_T04_Op11;
    public Label lblQtdStakes_T04_Op11;
    public Label lblQtdWins_T04_Op11;
    public Label lblQtdLoss_T04_Op11;
    public Label lblVlrIn_T04_Op11;
    public Label lblVlrOut_T04_Op11;
    public Label lblVlrDiff_T04_Op11;
    public TableView tbvTransacoes_T04_Op11;
    // Time_04 *-*-* Symbol_12
    public Label lblSymbol_T04_Op12;
    public Label lblQtdCall_T04_Op12;
    public Label lblQtdPut_T04_Op12;
    public Label lblQtdCallOrPut_T04_Op12;
    public ImageView imgCallOrPut_T04_Op12;
    public Label lblQtdStakes_T04_Op12;
    public Label lblQtdWins_T04_Op12;
    public Label lblQtdLoss_T04_Op12;
    public Label lblVlrIn_T04_Op12;
    public Label lblVlrOut_T04_Op12;
    public Label lblVlrDiff_T04_Op12;
    public TableView tbvTransacoes_T04_Op12;

    // Time_05 *-*-*
    public TitledPane tpn_T05;
    public JFXCheckBox chkTpn_T05_TimeAtivo;
    public Label lblTpn_T05_CandleTimeStart;
    public Label lblTpn_T05_TimeEnd;
    public Label lblTpn_T05_QtdStakes;
    public Label lblTpn_T05_QtdWins;
    public Label lblTpn_T05_QtdLoss;
    public Label lblTpn_T05_VlrIn;
    public Label lblTpn_T05_VlrOut;
    public Label lblTpn_T05_VlrDiff;
    // Time_05 *-*-* Symbol_01
    public Label lblSymbol_T05_Op01;
    public Label lblQtdCall_T05_Op01;
    public Label lblQtdPut_T05_Op01;
    public Label lblQtdCallOrPut_T05_Op01;
    public ImageView imgCallOrPut_T05_Op01;
    public Label lblQtdStakes_T05_Op01;
    public Label lblQtdWins_T05_Op01;
    public Label lblQtdLoss_T05_Op01;
    public Label lblVlrIn_T05_Op01;
    public Label lblVlrOut_T05_Op01;
    public Label lblVlrDiff_T05_Op01;
    public TableView tbvTransacoes_T05_Op01;
    // Time_05 *-*-* Symbol_02
    public Label lblSymbol_T05_Op02;
    public Label lblQtdCall_T05_Op02;
    public Label lblQtdPut_T05_Op02;
    public Label lblQtdCallOrPut_T05_Op02;
    public ImageView imgCallOrPut_T05_Op02;
    public Label lblQtdStakes_T05_Op02;
    public Label lblQtdWins_T05_Op02;
    public Label lblQtdLoss_T05_Op02;
    public Label lblVlrIn_T05_Op02;
    public Label lblVlrOut_T05_Op02;
    public Label lblVlrDiff_T05_Op02;
    public TableView tbvTransacoes_T05_Op02;
    // Time_05 *-*-* Symbol_03
    public Label lblSymbol_T05_Op03;
    public Label lblQtdCall_T05_Op03;
    public Label lblQtdPut_T05_Op03;
    public Label lblQtdCallOrPut_T05_Op03;
    public ImageView imgCallOrPut_T05_Op03;
    public Label lblQtdStakes_T05_Op03;
    public Label lblQtdWins_T05_Op03;
    public Label lblQtdLoss_T05_Op03;
    public Label lblVlrIn_T05_Op03;
    public Label lblVlrOut_T05_Op03;
    public Label lblVlrDiff_T05_Op03;
    public TableView tbvTransacoes_T05_Op03;
    // Time_05 *-*-* Symbol_04
    public Label lblSymbol_T05_Op04;
    public Label lblQtdCall_T05_Op04;
    public Label lblQtdPut_T05_Op04;
    public Label lblQtdCallOrPut_T05_Op04;
    public ImageView imgCallOrPut_T05_Op04;
    public Label lblQtdStakes_T05_Op04;
    public Label lblQtdWins_T05_Op04;
    public Label lblQtdLoss_T05_Op04;
    public Label lblVlrIn_T05_Op04;
    public Label lblVlrOut_T05_Op04;
    public Label lblVlrDiff_T05_Op04;
    public TableView tbvTransacoes_T05_Op04;
    // Time_05 *-*-* Symbol_05
    public Label lblSymbol_T05_Op05;
    public Label lblQtdCall_T05_Op05;
    public Label lblQtdPut_T05_Op05;
    public Label lblQtdCallOrPut_T05_Op05;
    public ImageView imgCallOrPut_T05_Op05;
    public Label lblQtdStakes_T05_Op05;
    public Label lblQtdWins_T05_Op05;
    public Label lblQtdLoss_T05_Op05;
    public Label lblVlrIn_T05_Op05;
    public Label lblVlrOut_T05_Op05;
    public Label lblVlrDiff_T05_Op05;
    public TableView tbvTransacoes_T05_Op05;
    // Time_05 *-*-* Symbol_06
    public Label lblSymbol_T05_Op06;
    public Label lblQtdCall_T05_Op06;
    public Label lblQtdPut_T05_Op06;
    public Label lblQtdCallOrPut_T05_Op06;
    public ImageView imgCallOrPut_T05_Op06;
    public Label lblQtdStakes_T05_Op06;
    public Label lblQtdWins_T05_Op06;
    public Label lblQtdLoss_T05_Op06;
    public Label lblVlrIn_T05_Op06;
    public Label lblVlrOut_T05_Op06;
    public Label lblVlrDiff_T05_Op06;
    public TableView tbvTransacoes_T05_Op06;
    // Time_05 *-*-* Symbol_07
    public Label lblSymbol_T05_Op07;
    public Label lblQtdCall_T05_Op07;
    public Label lblQtdPut_T05_Op07;
    public Label lblQtdCallOrPut_T05_Op07;
    public ImageView imgCallOrPut_T05_Op07;
    public Label lblQtdStakes_T05_Op07;
    public Label lblQtdWins_T05_Op07;
    public Label lblQtdLoss_T05_Op07;
    public Label lblVlrIn_T05_Op07;
    public Label lblVlrOut_T05_Op07;
    public Label lblVlrDiff_T05_Op07;
    public TableView tbvTransacoes_T05_Op07;
    // Time_05 *-*-* Symbol_08
    public Label lblSymbol_T05_Op08;
    public Label lblQtdCall_T05_Op08;
    public Label lblQtdPut_T05_Op08;
    public Label lblQtdCallOrPut_T05_Op08;
    public ImageView imgCallOrPut_T05_Op08;
    public Label lblQtdStakes_T05_Op08;
    public Label lblQtdWins_T05_Op08;
    public Label lblQtdLoss_T05_Op08;
    public Label lblVlrIn_T05_Op08;
    public Label lblVlrOut_T05_Op08;
    public Label lblVlrDiff_T05_Op08;
    public TableView tbvTransacoes_T05_Op08;
    // Time_05 *-*-* Symbol_09
    public Label lblSymbol_T05_Op09;
    public Label lblQtdCall_T05_Op09;
    public Label lblQtdPut_T05_Op09;
    public Label lblQtdCallOrPut_T05_Op09;
    public ImageView imgCallOrPut_T05_Op09;
    public Label lblQtdStakes_T05_Op09;
    public Label lblQtdWins_T05_Op09;
    public Label lblQtdLoss_T05_Op09;
    public Label lblVlrIn_T05_Op09;
    public Label lblVlrOut_T05_Op09;
    public Label lblVlrDiff_T05_Op09;
    public TableView tbvTransacoes_T05_Op09;
    // Time_05 *-*-* Symbol_10
    public Label lblSymbol_T05_Op10;
    public Label lblQtdCall_T05_Op10;
    public Label lblQtdPut_T05_Op10;
    public Label lblQtdCallOrPut_T05_Op10;
    public ImageView imgCallOrPut_T05_Op10;
    public Label lblQtdStakes_T05_Op10;
    public Label lblQtdWins_T05_Op10;
    public Label lblQtdLoss_T05_Op10;
    public Label lblVlrIn_T05_Op10;
    public Label lblVlrOut_T05_Op10;
    public Label lblVlrDiff_T05_Op10;
    public TableView tbvTransacoes_T05_Op10;
    // Time_05 *-*-* Symbol_11
    public Label lblSymbol_T05_Op11;
    public Label lblQtdCall_T05_Op11;
    public Label lblQtdPut_T05_Op11;
    public Label lblQtdCallOrPut_T05_Op11;
    public ImageView imgCallOrPut_T05_Op11;
    public Label lblQtdStakes_T05_Op11;
    public Label lblQtdWins_T05_Op11;
    public Label lblQtdLoss_T05_Op11;
    public Label lblVlrIn_T05_Op11;
    public Label lblVlrOut_T05_Op11;
    public Label lblVlrDiff_T05_Op11;
    public TableView tbvTransacoes_T05_Op11;
    // Time_05 *-*-* Symbol_12
    public Label lblSymbol_T05_Op12;
    public Label lblQtdCall_T05_Op12;
    public Label lblQtdPut_T05_Op12;
    public Label lblQtdCallOrPut_T05_Op12;
    public ImageView imgCallOrPut_T05_Op12;
    public Label lblQtdStakes_T05_Op12;
    public Label lblQtdWins_T05_Op12;
    public Label lblQtdLoss_T05_Op12;
    public Label lblVlrIn_T05_Op12;
    public Label lblVlrOut_T05_Op12;
    public Label lblVlrDiff_T05_Op12;
    public TableView tbvTransacoes_T05_Op12;

    // Time_06 *-*-*
    public TitledPane tpn_T06;
    public JFXCheckBox chkTpn_T06_TimeAtivo;
    public Label lblTpn_T06_CandleTimeStart;
    public Label lblTpn_T06_TimeEnd;
    public Label lblTpn_T06_QtdStakes;
    public Label lblTpn_T06_QtdWins;
    public Label lblTpn_T06_QtdLoss;
    public Label lblTpn_T06_VlrIn;
    public Label lblTpn_T06_VlrOut;
    public Label lblTpn_T06_VlrDiff;
    // Time_06 *-*-* Symbol_01
    public Label lblSymbol_T06_Op01;
    public Label lblQtdCall_T06_Op01;
    public Label lblQtdPut_T06_Op01;
    public Label lblQtdCallOrPut_T06_Op01;
    public ImageView imgCallOrPut_T06_Op01;
    public Label lblQtdStakes_T06_Op01;
    public Label lblQtdWins_T06_Op01;
    public Label lblQtdLoss_T06_Op01;
    public Label lblVlrIn_T06_Op01;
    public Label lblVlrOut_T06_Op01;
    public Label lblVlrDiff_T06_Op01;
    public TableView tbvTransacoes_T06_Op01;
    // Time_06 *-*-* Symbol_02
    public Label lblSymbol_T06_Op02;
    public Label lblQtdCall_T06_Op02;
    public Label lblQtdPut_T06_Op02;
    public Label lblQtdCallOrPut_T06_Op02;
    public ImageView imgCallOrPut_T06_Op02;
    public Label lblQtdStakes_T06_Op02;
    public Label lblQtdWins_T06_Op02;
    public Label lblQtdLoss_T06_Op02;
    public Label lblVlrIn_T06_Op02;
    public Label lblVlrOut_T06_Op02;
    public Label lblVlrDiff_T06_Op02;
    public TableView tbvTransacoes_T06_Op02;
    // Time_06 *-*-* Symbol_03
    public Label lblSymbol_T06_Op03;
    public Label lblQtdCall_T06_Op03;
    public Label lblQtdPut_T06_Op03;
    public Label lblQtdCallOrPut_T06_Op03;
    public ImageView imgCallOrPut_T06_Op03;
    public Label lblQtdStakes_T06_Op03;
    public Label lblQtdWins_T06_Op03;
    public Label lblQtdLoss_T06_Op03;
    public Label lblVlrIn_T06_Op03;
    public Label lblVlrOut_T06_Op03;
    public Label lblVlrDiff_T06_Op03;
    public TableView tbvTransacoes_T06_Op03;
    // Time_06 *-*-* Symbol_04
    public Label lblSymbol_T06_Op04;
    public Label lblQtdCall_T06_Op04;
    public Label lblQtdPut_T06_Op04;
    public Label lblQtdCallOrPut_T06_Op04;
    public ImageView imgCallOrPut_T06_Op04;
    public Label lblQtdStakes_T06_Op04;
    public Label lblQtdWins_T06_Op04;
    public Label lblQtdLoss_T06_Op04;
    public Label lblVlrIn_T06_Op04;
    public Label lblVlrOut_T06_Op04;
    public Label lblVlrDiff_T06_Op04;
    public TableView tbvTransacoes_T06_Op04;
    // Time_06 *-*-* Symbol_05
    public Label lblSymbol_T06_Op05;
    public Label lblQtdCall_T06_Op05;
    public Label lblQtdPut_T06_Op05;
    public Label lblQtdCallOrPut_T06_Op05;
    public ImageView imgCallOrPut_T06_Op05;
    public Label lblQtdStakes_T06_Op05;
    public Label lblQtdWins_T06_Op05;
    public Label lblQtdLoss_T06_Op05;
    public Label lblVlrIn_T06_Op05;
    public Label lblVlrOut_T06_Op05;
    public Label lblVlrDiff_T06_Op05;
    public TableView tbvTransacoes_T06_Op05;
    // Time_06 *-*-* Symbol_06
    public Label lblSymbol_T06_Op06;
    public Label lblQtdCall_T06_Op06;
    public Label lblQtdPut_T06_Op06;
    public Label lblQtdCallOrPut_T06_Op06;
    public ImageView imgCallOrPut_T06_Op06;
    public Label lblQtdStakes_T06_Op06;
    public Label lblQtdWins_T06_Op06;
    public Label lblQtdLoss_T06_Op06;
    public Label lblVlrIn_T06_Op06;
    public Label lblVlrOut_T06_Op06;
    public Label lblVlrDiff_T06_Op06;
    public TableView tbvTransacoes_T06_Op06;
    // Time_06 *-*-* Symbol_07
    public Label lblSymbol_T06_Op07;
    public Label lblQtdCall_T06_Op07;
    public Label lblQtdPut_T06_Op07;
    public Label lblQtdCallOrPut_T06_Op07;
    public ImageView imgCallOrPut_T06_Op07;
    public Label lblQtdStakes_T06_Op07;
    public Label lblQtdWins_T06_Op07;
    public Label lblQtdLoss_T06_Op07;
    public Label lblVlrIn_T06_Op07;
    public Label lblVlrOut_T06_Op07;
    public Label lblVlrDiff_T06_Op07;
    public TableView tbvTransacoes_T06_Op07;
    // Time_06 *-*-* Symbol_08
    public Label lblSymbol_T06_Op08;
    public Label lblQtdCall_T06_Op08;
    public Label lblQtdPut_T06_Op08;
    public Label lblQtdCallOrPut_T06_Op08;
    public ImageView imgCallOrPut_T06_Op08;
    public Label lblQtdStakes_T06_Op08;
    public Label lblQtdWins_T06_Op08;
    public Label lblQtdLoss_T06_Op08;
    public Label lblVlrIn_T06_Op08;
    public Label lblVlrOut_T06_Op08;
    public Label lblVlrDiff_T06_Op08;
    public TableView tbvTransacoes_T06_Op08;
    // Time_06 *-*-* Symbol_09
    public Label lblSymbol_T06_Op09;
    public Label lblQtdCall_T06_Op09;
    public Label lblQtdPut_T06_Op09;
    public Label lblQtdCallOrPut_T06_Op09;
    public ImageView imgCallOrPut_T06_Op09;
    public Label lblQtdStakes_T06_Op09;
    public Label lblQtdWins_T06_Op09;
    public Label lblQtdLoss_T06_Op09;
    public Label lblVlrIn_T06_Op09;
    public Label lblVlrOut_T06_Op09;
    public Label lblVlrDiff_T06_Op09;
    public TableView tbvTransacoes_T06_Op09;
    // Time_06 *-*-* Symbol_10
    public Label lblSymbol_T06_Op10;
    public Label lblQtdCall_T06_Op10;
    public Label lblQtdPut_T06_Op10;
    public Label lblQtdCallOrPut_T06_Op10;
    public ImageView imgCallOrPut_T06_Op10;
    public Label lblQtdStakes_T06_Op10;
    public Label lblQtdWins_T06_Op10;
    public Label lblQtdLoss_T06_Op10;
    public Label lblVlrIn_T06_Op10;
    public Label lblVlrOut_T06_Op10;
    public Label lblVlrDiff_T06_Op10;
    public TableView tbvTransacoes_T06_Op10;
    // Time_06 *-*-* Symbol_11
    public Label lblSymbol_T06_Op11;
    public Label lblQtdCall_T06_Op11;
    public Label lblQtdPut_T06_Op11;
    public Label lblQtdCallOrPut_T06_Op11;
    public ImageView imgCallOrPut_T06_Op11;
    public Label lblQtdStakes_T06_Op11;
    public Label lblQtdWins_T06_Op11;
    public Label lblQtdLoss_T06_Op11;
    public Label lblVlrIn_T06_Op11;
    public Label lblVlrOut_T06_Op11;
    public Label lblVlrDiff_T06_Op11;
    public TableView tbvTransacoes_T06_Op11;
    // Time_06 *-*-* Symbol_12
    public Label lblSymbol_T06_Op12;
    public Label lblQtdCall_T06_Op12;
    public Label lblQtdPut_T06_Op12;
    public Label lblQtdCallOrPut_T06_Op12;
    public ImageView imgCallOrPut_T06_Op12;
    public Label lblQtdStakes_T06_Op12;
    public Label lblQtdWins_T06_Op12;
    public Label lblQtdLoss_T06_Op12;
    public Label lblVlrIn_T06_Op12;
    public Label lblVlrOut_T06_Op12;
    public Label lblVlrDiff_T06_Op12;
    public TableView tbvTransacoes_T06_Op12;


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

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
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
                            solicitarTicks(true);
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

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            int finalT_id = t_id;

            getTimeCandleStart()[t_id] = new SimpleStringProperty("");
            getTimeCandleToClose()[t_id] = new SimpleIntegerProperty(0);
            getQtdTimeFrameStakes()[t_id] = new SimpleIntegerProperty(0);
            getQtdTimeFrameStakesWins()[t_id] = new SimpleIntegerProperty(0);
            getQtdTimeFrameStakesLoss()[t_id] = new SimpleIntegerProperty(0);
            getVlrTimeFrameStakesIn()[t_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getVlrTimeFrameStakesOut()[t_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getVlrTimeFrameStakesDiff()[t_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);

            getTransacoesFilteredList_tFrame()[t_id] = new FilteredList<>(getTransacoesObservableList());
            getTransacoesFilteredList_tFrame()[t_id].setPredicate(transacoes -> transacoes.getT_id() == finalT_id);

            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                if (t_id == 0)
                    getUltimoOhlcStr()[s_id] = new SimpleObjectProperty<>();

                getFirstBuy()[t_id][s_id] = new SimpleBooleanProperty(true);
                getQtdCallOrPut()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdCall()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdPut()[t_id][s_id] = new SimpleIntegerProperty(0);

                getQtdTframeSymbolStakes()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdTframeSymbolStakesWins()[t_id][s_id] = new SimpleIntegerProperty(0);
                getQtdTframeSymbolStakesLoss()[t_id][s_id] = new SimpleIntegerProperty(0);

                getVlrTframeSymbolStakesIn()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                getVlrTframeSymbolStakesOut()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                getVlrTframeSymbolStakesDiff()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);

                getHistoricoDeCandlesFilteredList()[t_id][s_id] = new FilteredList<>(getHistoricoDeCandlesObservableList());
                getHistoricoDeCandlesFilteredList()[t_id][s_id].setPredicate(candles ->
                        candles.getTimeFrame().getId() == getTimeFrameObservableList().get(finalT_id).getId()
                                && candles.getSymbol().getId() == getSymbolObservableList().get(finalS_id).getId());

                getTransacoesFilteredList_symbol()[t_id][s_id] = new FilteredList<>(getTransacoesObservableList());
                getTransacoesFilteredList_symbol()[t_id][s_id].setPredicate(transacoes -> transacoes.getT_id() == finalT_id
                        && transacoes.getS_id() == finalS_id);

                getTmodelTransacoes()[t_id][s_id] = new TmodelTransacoes(getTransacoesObservableList(), t_id, s_id);
                getTmodelTransacoes()[t_id][s_id].criar_tabela();

                contectarTabelaEmLista(t_id, s_id);

                getTmodelTransacoes()[t_id][s_id].tabela_preencher();
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

        contaTokenProperty().bind(getCboTpnDetalhesContaBinary().valueProperty());

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

        disableIniciarBtnProperty().bind(Bindings.createBooleanBinding(() ->
                        (!isContratoGerado() || (isRoboMonitorando())
                                && !isRoboMonitorandoPausado()),
                contratoGeradoProperty(), roboMonitorandoProperty(), roboMonitorandoPausadoProperty()));

        disablePausarBtnProperty().bind(Bindings.createBooleanBinding(() ->
                        (!isContratoGerado() || !isRoboMonitorando() && !isRoboMonitorandoPausado()),
                roboMonitorandoProperty(), roboMonitorandoPausadoProperty()));

        disableStopBtnProperty().bind(contratoGeradoProperty().not());

        ROBOProperty().addListener((ov, o, n) -> {

            if (n == null) {
                setParametrosUtilizadosRobo("");
                return;
            }

        });

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            int finalT_id = t_id;
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getHistoricoDeCandlesFilteredList()[t_id][s_id]
                        .addListener((ListChangeListener<? super HistoricoDeCandles>) c -> {
                            while (c.next()) {
                                if (c.wasRemoved()) {
                                    getQtdCallOrPut()[finalT_id][finalS_id].setValue(0);
                                    for (HistoricoDeCandles tmpCandle : c.getList().stream()
                                            .sorted(Comparator.comparing(HistoricoDeCandles::getEpoch))
                                            .collect(Collectors.toList()))
                                        calcularCallAndPut(tmpCandle, finalT_id, finalS_id);
                                }
                                for (HistoricoDeCandles hCandle : c.getAddedSubList()) {
                                    if (c.getList().size() == 1 && finalS_id == 0)
                                        getTimeCandleStart()[finalT_id].setValue(
                                                Service_DataTime.getCarimboStr(hCandle.getEpoch(), DTF_HORA_MINUTOS));
                                    calcularCallAndPut(hCandle, finalT_id, finalS_id);
                                }
                                getQtdCall()[finalT_id][finalS_id].setValue(c.getList().stream()
                                        .filter(candles -> candles.getClose().compareTo(candles.getOpen()) > 0)
                                        .count());
                                getQtdPut()[finalT_id][finalS_id].setValue(c.getList().stream()
                                        .filter(candles -> candles.getClose().compareTo(candles.getOpen()) <= 0)
                                        .count());
                            }
                        });
            }
        }

        for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
            int finalS_id = s_id;
            getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                if (o == null || n == null) return;
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getS_id() == finalS_id
                                && transacoes.getTickNegociacaoInicio().compareTo(BigDecimal.ZERO) == 0
                                && transacoes.getTickVenda().compareTo(BigDecimal.ZERO) == 0)
                        .forEach(transacao -> {
                            List<HistoricoDeTicks> tmpHistory;
//                            int index = getTransacoesObservableList().indexOf(transacao);
                            if ((tmpHistory = getHistoricoDeTicksObservableList().stream()
                                    .filter(historicoDeTicks -> historicoDeTicks.getSymbol().getId()
                                            == getSymbolObservableList().get(finalS_id).getId()
                                            && historicoDeTicks.getTime() >= transacao.getDataHoraCompra())
                                    .collect(Collectors.toList())).size() > 1) {
                                transacao.setTickCompra(tmpHistory.get(0).getPrice());
                                transacao.setTickNegociacaoInicio(tmpHistory.get(1).getPrice());
                                getTransacoesDAO().merger(transacao);
//                                getTransacoesObservableList().set(index, getTransacoesDAO().merger(transacao));
                            }
                        });
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getS_id() == finalS_id
                                && transacoes.getTickVenda().compareTo(BigDecimal.ZERO) == 0
                                && transacoes.getDataHoraExpiry() < n.getEpoch())
                        .forEach(transacao -> {
                            BigDecimal tmpTick;
//                            int index = getTransacoesObservableList().indexOf(transacao);
                            if ((tmpTick = getHistoricoDeTicksObservableList().stream()
                                    .filter(historicoDeTicks -> historicoDeTicks.getSymbol().getId()
                                            == getSymbolObservableList().get(finalS_id).getId()
                                            && historicoDeTicks.getTime() == transacao.getDataHoraExpiry())
                                    .findFirst().orElse(null).getPrice()) != null) {
                                transacao.setTickVenda(tmpTick);
                                getTransacoesDAO().merger(transacao);
//                                getTransacoesObservableList().set(index, getTransacoesDAO().merger(transacao));
                            }
                        });
            });
        }

        getTransacoesObservableList().addListener((ListChangeListener<? super Transacoes>) c -> {
            setQtdStakes(c.getList().size());
            setQtdStakesWins((int) c.getList().stream().filter(transacoes ->
                    transacoes.isConsolidado()
                            && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                    .count());
            setQtdStakesLoss((int) c.getList().stream().filter(transacoes -> transacoes.isConsolidado()
                    && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                    .count());
            setVlrStakesIn(c.getList().stream()
                    .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP));
            setVlrStakesOut(c.getList().stream()
                    .filter(transacoes -> transacoes.isConsolidado())
                    .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP));
            setVlrStakesDiff(c.getList().stream()
                    .filter(transacoes -> transacoes.isConsolidado())
                    .map(Transacoes::getStakeResult).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP));
        });

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            int finalT_id = t_id;
            getTransacoesFilteredList_tFrame()[t_id].addListener((ListChangeListener<? super Transacoes>) c -> {
                getQtdTimeFrameStakes()[finalT_id].setValue(c.getList().size());
                getQtdTimeFrameStakesWins()[finalT_id].setValue((int) c.getList().stream().filter(transacoes ->
                        transacoes.isConsolidado()
                                && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                        .count());
                getQtdTimeFrameStakesLoss()[finalT_id].setValue((int) c.getList().stream().filter(transacoes -> transacoes.isConsolidado()
                        && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                        .count());
                getVlrTimeFrameStakesIn()[finalT_id].setValue(c.getList().stream()
                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));
                getVlrTimeFrameStakesOut()[finalT_id].setValue(c.getList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));
                getVlrTimeFrameStakesDiff()[finalT_id].setValue(c.getList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(Transacoes::getStakeResult).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));
            });
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                int finalS_id = s_id;
                getTransacoesFilteredList_symbol()[t_id][s_id].addListener((ListChangeListener<? super Transacoes>) c -> {
                    getQtdTframeSymbolStakes()[finalT_id][finalS_id].setValue(c.getList().size());
                    getQtdTframeSymbolStakesWins()[finalT_id][finalS_id].setValue((int) c.getList().stream().filter(transacoes ->
                            transacoes.isConsolidado()
                                    && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                            .count());
                    getQtdTframeSymbolStakesLoss()[finalT_id][finalS_id].setValue((int) c.getList().stream().filter(transacoes -> transacoes.isConsolidado()
                            && transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                            .count());
                    getVlrTframeSymbolStakesIn()[finalT_id][finalS_id].setValue(c.getList().stream()
                            .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP));
                    getVlrTframeSymbolStakesOut()[finalT_id][finalS_id].setValue(c.getList().stream()
                            .filter(transacoes -> transacoes.isConsolidado())
                            .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP));
                    getVlrTframeSymbolStakesDiff()[finalT_id][finalS_id].setValue(c.getList().stream()
                            .filter(transacoes -> transacoes.isConsolidado())
                            .map(Transacoes::getStakeResult).reduce(BigDecimal.ZERO, BigDecimal::add)
                            .setScale(2, RoundingMode.HALF_UP));
                });
            }
        }

    }

    private void variaveis_Comandos() {

    }

    private void objetos_Carregar() {

        getCboTpnNegociacaoQtdCandlesAnalise().getItems().setAll(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100);
        getCboTpnNegociacaoQtdCandlesAnalise().getSelectionModel().select(1);

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

        getLblDetalhesSaldoFinal().textProperty().bind(Bindings.createStringBinding(() ->
                        Service_Mascara.getValorMoeda(saldoFinalProperty().getValue()),
                saldoFinalProperty()));

        getLblNegociacaoParametros().textProperty().bind(parametrosUtilizadosRoboProperty());

        getBtnTpnNegociacao_Contratos().disableProperty().bind(disableContratoBtnProperty());
        getBtnTpnNegociacao_Iniciar().disableProperty().bind(disableIniciarBtnProperty());
        getBtnTpnNegociacao_Pausar().disableProperty().bind(disablePausarBtnProperty());
        getBtnTpnNegociacao_Stop().disableProperty().bind(disableStopBtnProperty());

    }

    private void objetos_Listener() {

        qtdCandlesAnaliseProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            solicitarTicks(false);
        });

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

    }

    private void objetos_Comandos() {

        getBtnTpnNegociacao_Contratos().setOnAction(event -> {
            try {
                if (!getCboTpnDetalhesContaBinary().getValue().isTransactionOK())
                    solicitarTransacoes();
                getRobo().definicaoDeContrato();
                setContratoGerado(true);
                setFirstBuy(true);
            } catch (Exception ex) {
                if (ex instanceof NoSuchElementException)
                    return;
                ex.printStackTrace();
            }
        });

        getBtnTpnNegociacao_Iniciar().setOnAction(event -> {
            try {
                if (isRoboMonitorando()) {
                    setRoboMonitorandoPausado(false);
                } else {
                    getRobo().monitorarCondicoesParaComprar();
                    setRoboMonitorando(true);
                    getLogSistemaStartDAO().merger(
                            new LogSistemaStart(getCboTpnDetalhesContaBinary().getValue(),
                                    Service_DataTime.getIntegerDateNow(),
                                    Service_Mascara.getParametrosToDB()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        getBtnTpnNegociacao_Pausar().setOnAction(event -> setRoboMonitorandoPausado(true));

        getBtnTpnNegociacao_Stop().setOnAction(event -> {
            getCboNegociacaoRobos().getSelectionModel().select(0);
            setRoboMonitorando(false);
            setRoboMonitorandoPausado(false);
            setContratoGerado(false);
            setRobo(null);
        });

    }

    private void conectarObjetosEmVariaveis() {

        conectarObjetosEmVariaveis_LastTicks();

        conectarObjetosEmVariaveis_Timers();

        //preencherTabelas();

    }

    private void calcularCallAndPut(HistoricoDeCandles candle, int t_id, int s_id) {

        if (candle.getClose().compareTo(candle.getOpen()) > 0) {
            getQtdCallOrPut()[t_id][s_id].setValue(
                    getQtdCallOrPut()[t_id][s_id].getValue().compareTo(0) > 0
                            ? getQtdCallOrPut()[t_id][s_id].getValue() + 1
                            : 1);
        } else if (candle.getClose().compareTo(candle.getOpen()) < 0) {
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

    public static void gerarContrato(int t_id, int s_id, int typeContract_id, BigDecimal vlrPriceProposal) throws Exception {

        boolean priceLoss = vlrPriceProposal != null;

        PriceProposal priceProposal = new PriceProposal();
        Passthrough passthrough = new Passthrough(t_id, s_id, getTypeCandle_id(),
                typeContract_id, priceLoss, "");

        priceProposal.setProposal(1);
        priceProposal.setAmount(vlrPriceProposal != null
                ? vlrPriceProposal.setScale(2, RoundingMode.HALF_UP)
                : getVlrStkPadrao()[t_id].getValue().setScale(2, RoundingMode.HALF_UP));
        priceProposal.setBasis(PRICE_PROPOSAL_BASIS);
        priceProposal.setContract_type(CONTRACT_TYPE.toEnum(typeContract_id));
        priceProposal.setCurrency(getAuthorize().getCurrency().toUpperCase());
        priceProposal.setDuration(getTimeFrameObservableList().get(t_id).getGranularity());
        priceProposal.setDuration_unit(getTimeFrameObservableList().get(t_id).getDuration_unit());
        priceProposal.setSymbol(getSymbolObservableList().get(s_id).getSymbol());
        priceProposal.setPassthrough(passthrough);

        solicitarProposal(priceProposal);

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

    private void solicitarTicks(boolean subscribe) {

        Passthrough passthrough = new Passthrough();

        getHistoricoDeCandlesObservableList().clear();

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            Integer tempoVela = getTimeFrameObservableList().get(t_id).getGranularity();
            passthrough.setT_id(t_id);
            passthrough.setTypeCandle_id(getTypeCandle_id());

            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                passthrough.setS_id(s_id);
                String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(s_id,
                        getQtdCandlesAnalise(), TICK_STYLE.toEnum(getTypeCandle_id()).getDescricao(),
                        tempoVela, passthrough, subscribe));
                getWsClientObjectProperty().getMyWebSocket().send(jsonHistory);
            }
        }

    }

    public static void solicitarProposal(PriceProposal priceProposal) {

        if (!isAppAutorizado()) return;
        try {
            String jsonPriceProposal = Util_Json.getJson_from_Object(priceProposal);
            getWsClientObjectProperty().getMyWebSocket().send(jsonPriceProposal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void solicitarCompraContrato(Proposal proposal) {

        try {
            if (proposal == null) return;
            String jsonBuyContrato = Util_Json.getJson_from_Object(new BuyContract(proposal));
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

    public void setFirstBuy(Boolean isTrue) {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++)
            for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++)
                getFirstBuy()[t_id][s_id].setValue(isTrue);

    }

    public Image getImagemCandle(int t_id, int s_id) {

        if (getQtdCallOrPut()[t_id][s_id].getValue().compareTo(1) >= 0)
            return new Image("image/ico/ic_seta_call_sobe_black_18dp.png");
        else if (getQtdCallOrPut()[t_id][s_id].getValue().compareTo(-1) <= 0)
            return new Image("image/ico/ic_seta_put_desce_black_18dp.png");
        else
            return null;

    }

    public static void newTransaction(Transaction transaction) {

        try {
            switch (ACTION.valueOf(transaction.getAction().toUpperCase())) {
                case BUY -> {
                    new Transacoes().isBUY(transaction);
                    Operacoes.setSaldoFinal(transaction.getBalance());
                }
                case SELL -> {
                    try {
                        getTransacoesObservableList().stream()
                                .filter(transacoes -> transacoes.getContract_id() == transaction.getContract_id())
                                .findFirst().get().isSELL(transaction);
                        Operacoes.setSaldoFinal(transaction.getBalance());
                    } catch (Exception ex) {
                        if (!(ex instanceof NoSuchElementException))
                            ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            if (!(ex instanceof NullPointerException) && !(ex instanceof IllegalStateException))
                ex.printStackTrace();
        }

    }

    private void conectarObjetosEmVariaveis_LastTicks() {

        getLblTpnDetalhesQtdStakes().textProperty().bind(qtdStakesProperty().asString());
        getLblTpnDetalhesQtdWins().textProperty().bind(qtdStakesWinsProperty().asString());
        getLblTpnDetalhesQtdLoss().textProperty().bind(qtdStakesLossProperty().asString());
        getLblDetalhesTotalIn().textProperty().bind(vlrStakesInProperty().asString());
        getLblDetalhesTotalOut().textProperty().bind(vlrStakesOutProperty().asString());
        getLblTpnDetalhesProfitVlr().textProperty().bind(vlrStakesDiffProperty().asString());
        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (t_id == 0) {
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    if (s_id == 0) {
                        getLblSymbol_01().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_01().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_01().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 1) {
                        getLblSymbol_02().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_02().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_02().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 2) {
                        getLblSymbol_03().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_03().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_03().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 3) {
                        getLblSymbol_04().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_04().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_04().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 4) {
                        getLblSymbol_05().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_05().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_05().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 5) {
                        getLblSymbol_06().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_06().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_06().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 6) {
                        getLblSymbol_07().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_07().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_07().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 7) {
                        getLblSymbol_08().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_08().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_08().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 8) {
                        getLblSymbol_09().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_09().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_09().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 9) {
                        getLblSymbol_10().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_10().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_10().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 10) {
                        getLblSymbol_11().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_11().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_11().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }
                    if (s_id == 11) {
                        getLblSymbol_12().setText(getSymbolObservableList().get(s_id).toString());
                        getUltimoOhlcStr()[s_id].addListener((ov, o, n) -> {
                            getLblLastTickSymbol_12().setText(n == null ? "" : n.toString());
                            getLblLastTickSymbol_12().setStyle((o == null || n.getClose().compareTo(o.getClose()) <= 0)
                                    ? STYLE_TICK_DESCENDO : STYLE_TICK_SUBINDO);
                        });
                    }

                }
            }
        }

    }

    private void conectarObjetosEmVariaveis_Timers() {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            int finalT_id = t_id;
            if (t_id == 0) {
                getTpn_T01().setText(getTimeFrameObservableList().get(t_id).toString());
                getLblTpn_T01_CandleTimeStart().textProperty().bind(getTimeCandleStart()[finalT_id]);
                getLblTpn_T01_TimeEnd().textProperty().bind(getTimeCandleToClose()[t_id].asString());

                getLblTpn_T01_QtdStakes().textProperty().bind(getQtdTimeFrameStakes()[t_id].asString());
                getLblTpn_T01_QtdWins().textProperty().bind(getQtdTimeFrameStakesWins()[t_id].asString());
                getLblTpn_T01_QtdLoss().textProperty().bind(getQtdTimeFrameStakesLoss()[t_id].asString());
                getLblTpn_T01_VlrIn().textProperty().bind(getVlrTimeFrameStakesIn()[t_id].asString());
                getLblTpn_T01_VlrOut().textProperty().bind(getVlrTimeFrameStakesOut()[t_id].asString());
                getLblTpn_T01_VlrDiff().textProperty().bind(getVlrTimeFrameStakesDiff()[t_id].asString());
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    int finalS_id = s_id;
                    if (s_id == 0) {
                        getLblSymbol_T01_Op01().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op01().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op01().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op01().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op01().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op01().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op01().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op01().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op01().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op01().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 1) {
                        getLblSymbol_T01_Op02().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op02().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op02().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op02().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op02().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op02().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op02().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op02().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op02().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op02().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op02().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 2) {
                        getLblSymbol_T01_Op03().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op03().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op03().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op03().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op03().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op03().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op03().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op03().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op03().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op03().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op03().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 3) {
                        getLblSymbol_T01_Op04().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op04().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op04().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op04().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op04().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op04().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op04().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op04().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op04().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op04().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op04().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 4) {
                        getLblSymbol_T01_Op05().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op05().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op05().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op05().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op05().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op05().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op05().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op05().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op05().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op05().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op05().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 5) {
                        getLblSymbol_T01_Op06().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op06().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op06().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op06().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op06().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op06().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op06().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op06().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op06().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op06().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op06().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 6) {
                        getLblSymbol_T01_Op07().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op07().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op07().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op07().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op07().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op07().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op07().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op07().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op07().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op07().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op07().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 7) {
                        getLblSymbol_T01_Op08().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op08().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op08().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op08().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op08().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op08().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op08().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op08().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op08().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op08().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op08().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 8) {
                        getLblSymbol_T01_Op09().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op09().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op09().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op09().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op09().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op09().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op09().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op09().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op09().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op09().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op09().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 9) {
                        getLblSymbol_T01_Op10().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op10().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op10().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op10().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op10().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op10().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op10().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op10().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op10().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op10().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op10().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 10) {
                        getLblSymbol_T01_Op11().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op11().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op11().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op11().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op11().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op11().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op11().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op11().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op11().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op11().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op11().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 11) {
                        getLblSymbol_T01_Op12().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T01_Op12().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T01_Op12().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T01_Op12().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T01_Op12().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T01_Op12().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T01_Op12().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T01_Op12().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T01_Op12().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op12().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op12().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    }
                }
            } else if (t_id == 1) {
                getTpn_T02().setText(getTimeFrameObservableList().get(t_id).toString());
                getLblTpn_T02_CandleTimeStart().textProperty().bind(getTimeCandleStart()[finalT_id]);
                getLblTpn_T02_TimeEnd().textProperty().bind(getTimeCandleToClose()[t_id].asString());

                getLblTpn_T02_QtdStakes().textProperty().bind(getQtdTimeFrameStakes()[t_id].asString());
                getLblTpn_T02_QtdWins().textProperty().bind(getQtdTimeFrameStakesWins()[t_id].asString());
                getLblTpn_T02_QtdLoss().textProperty().bind(getQtdTimeFrameStakesLoss()[t_id].asString());
                getLblTpn_T02_VlrIn().textProperty().bind(getVlrTimeFrameStakesIn()[t_id].asString());
                getLblTpn_T02_VlrOut().textProperty().bind(getVlrTimeFrameStakesOut()[t_id].asString());
                getLblTpn_T02_VlrDiff().textProperty().bind(getVlrTimeFrameStakesDiff()[t_id].asString());
                for (int s_id = 0; s_id < getSymbolObservableList().size(); s_id++) {
                    int finalS_id = s_id;
                    if (s_id == 0) {
                        getLblSymbol_T02_Op01().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op01().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op01().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op01().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op01().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op01().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op01().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op01().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op01().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op01().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op01().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 1) {
                        getLblSymbol_T02_Op02().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op02().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op02().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op02().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op02().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op02().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op02().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op02().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op02().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op02().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op02().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 2) {
                        getLblSymbol_T02_Op03().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op03().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op03().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op03().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op03().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op03().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op03().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op03().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op03().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op03().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op03().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 3) {
                        getLblSymbol_T02_Op04().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op04().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op04().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op04().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op04().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op04().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op04().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op04().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op04().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op04().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op04().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 4) {
                        getLblSymbol_T02_Op05().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op05().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op05().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op05().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op05().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op05().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op05().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op05().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op05().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op05().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op05().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 5) {
                        getLblSymbol_T02_Op06().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op06().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op06().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op06().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op06().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op06().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op06().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op06().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op06().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op06().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op06().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 6) {
                        getLblSymbol_T02_Op07().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op07().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op07().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op07().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op07().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op07().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op07().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op07().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op07().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op07().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op07().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 7) {
                        getLblSymbol_T02_Op08().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op08().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op08().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op08().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op08().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op08().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op08().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op08().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op08().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op08().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op08().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 8) {
                        getLblSymbol_T02_Op09().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op09().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op09().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op09().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op09().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op09().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op09().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op09().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op09().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op09().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op09().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 9) {
                        getLblSymbol_T02_Op10().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op10().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op10().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op10().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op10().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op10().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op10().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op10().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op10().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op10().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op10().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 10) {
                        getLblSymbol_T02_Op11().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op11().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op11().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op11().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op11().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op11().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op11().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op11().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op11().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op11().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op11().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    } else if (s_id == 11) {
                        getLblSymbol_T02_Op12().setText(getSymbolObservableList().get(s_id).toString());
                        getLblQtdCallOrPut_T02_Op12().textProperty().bind(Bindings.createStringBinding(() ->
                                        String.valueOf(Math.abs(getQtdCallOrPut()[finalT_id][finalS_id].getValue())),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getImgCallOrPut_T02_Op12().imageProperty().bind(Bindings.createObjectBinding(() ->
                                        getImagemCandle(finalT_id, finalS_id),
                                getQtdCallOrPut()[finalT_id][finalS_id]));
                        getLblQtdCall_T02_Op12().textProperty().bind(getQtdCall()[t_id][s_id].asString());
                        getLblQtdPut_T02_Op12().textProperty().bind(getQtdPut()[t_id][s_id].asString());

                        getLblQtdStakes_T02_Op12().textProperty().bind(getQtdTframeSymbolStakes()[t_id][s_id].asString());
                        getLblQtdWins_T02_Op12().textProperty().bind(getQtdTframeSymbolStakesWins()[t_id][s_id].asString());
                        getLblQtdLoss_T02_Op12().textProperty().bind(getQtdTframeSymbolStakesLoss()[t_id][s_id].asString());
                        getLblVlrIn_T02_Op12().textProperty().bind(getVlrTframeSymbolStakesIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op12().textProperty().bind(getVlrTframeSymbolStakesOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op12().textProperty().bind(getVlrTframeSymbolStakesDiff()[t_id][s_id].asString());
                    }
                }
            }


        }

    }

    private void conectarTimesAtivos() {

        for (int t_id = 0; t_id < getTimeFrameObservableList().size(); t_id++) {
            if (t_id == 0) {
                getTimeAtivo()[t_id].bind(getChkTpn_T01_TimeAtivo().selectedProperty());
                getChkTpn_T01_TimeAtivo().setSelected(true);
            } else if (t_id == 1) {
                getTimeAtivo()[t_id].bind(getChkTpn_T02_TimeAtivo().selectedProperty());
                getChkTpn_T02_TimeAtivo().setSelected(true);
            }
        }

    }

    private void contectarTabelaEmLista(int t_id, int s_id) {

        if (t_id == 0) {
            if (s_id == 0) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op01());
            else if (s_id == 1) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op02());
            else if (s_id == 2) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op03());
            else if (s_id == 3) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op04());
            else if (s_id == 4) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op05());
            else if (s_id == 5) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op06());
            else if (s_id == 6) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op07());
            else if (s_id == 7) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op08());
            else if (s_id == 8) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op09());
            else if (s_id == 9) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op10());
            else if (s_id == 10)
                getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op11());
            else if (s_id == 11)
                getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op12());
        } else if (t_id == 1) {
            if (s_id == 0) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T01_Op01());
            else if (s_id == 1) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op02());
            else if (s_id == 2) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op03());
            else if (s_id == 3) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op04());
            else if (s_id == 4) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op05());
            else if (s_id == 5) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op06());
            else if (s_id == 6) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op07());
            else if (s_id == 7) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op08());
            else if (s_id == 8) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op09());
            else if (s_id == 9) getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op10());
            else if (s_id == 10)
                getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op11());
            else if (s_id == 11)
                getTmodelTransacoes()[t_id][s_id].setTbvTransacoes(getTbvTransacoes_T02_Op12());
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












}
