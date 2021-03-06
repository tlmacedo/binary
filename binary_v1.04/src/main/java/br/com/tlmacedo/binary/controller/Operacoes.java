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
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolIn = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal> vlrStakesOut = new SimpleObjectProperty<>(BigDecimal.ZERO);
    static ObjectProperty<BigDecimal>[] vlrTimeFrameStakesOut = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolOut = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];

    static ObjectProperty<BigDecimal> vlrStakesDiff = new SimpleObjectProperty<>(BigDecimal.ZERO);
    static ObjectProperty<BigDecimal>[] vlrTimeFrameStakesDiff = new ObjectProperty[getTimeFrameObservableList().size()];
    static ObjectProperty<BigDecimal>[][] vlrTframeSymbolDiff = new ObjectProperty[getTimeFrameObservableList().size()][getSymbolObservableList().size()];


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

                getVlrTframeSymbolIn()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                getVlrTframeSymbolOut()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);
                getVlrTframeSymbolDiff()[t_id][s_id] = new SimpleObjectProperty<>(BigDecimal.ZERO);

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
                }
                case SELL -> {
                    try {
                        getTransacoesObservableList().stream()
                                .filter(transacoes -> transacoes.getContract_id() == transaction.getContract_id())
                                .findFirst().get().isSELL(transaction);
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
                        getLblVlrIn_T01_Op01().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op01().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op01().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op02().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op02().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op02().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op03().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op03().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op03().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op04().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op04().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op04().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op05().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op05().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op05().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op06().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op06().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op06().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op07().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op07().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op07().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op08().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op08().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op08().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op09().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op09().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op09().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op10().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op10().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op10().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op11().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op11().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op11().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T01_Op12().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T01_Op12().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T01_Op12().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op01().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op01().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op01().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op02().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op02().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op02().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op03().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op03().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op03().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op04().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op04().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op04().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op05().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op05().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op05().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op06().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op06().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op06().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op07().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op07().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op07().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op08().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op08().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op08().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op09().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op09().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op09().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op10().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op10().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op10().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op11().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op11().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op11().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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
                        getLblVlrIn_T02_Op12().textProperty().bind(getVlrTframeSymbolIn()[t_id][s_id].asString());
                        getLblVlrOut_T02_Op12().textProperty().bind(getVlrTframeSymbolOut()[t_id][s_id].asString());
                        getLblVlrDiff_T02_Op12().textProperty().bind(getVlrTframeSymbolDiff()[t_id][s_id].asString());
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


    public static TimeFrameDAO getTimeFrameDAO() {
        return timeFrameDAO;
    }

    public static void setTimeFrameDAO(TimeFrameDAO timeFrameDAO) {
        Operacoes.timeFrameDAO = timeFrameDAO;
    }

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

    public static HistoricoDeTicksDAO getHistoricoDeTicksDAO() {
        return historicoDeTicksDAO;
    }

    public static void setHistoricoDeTicksDAO(HistoricoDeTicksDAO historicoDeTicksDAO) {
        Operacoes.historicoDeTicksDAO = historicoDeTicksDAO;
    }

    public static LogSistemaStartDAO getLogSistemaStartDAO() {
        return logSistemaStartDAO;
    }

    public static void setLogSistemaStartDAO(LogSistemaStartDAO logSistemaStartDAO) {
        Operacoes.logSistemaStartDAO = logSistemaStartDAO;
    }

    public static ObservableList<TimeFrame> getTimeFrameObservableList() {
        return TIME_FRAME_OBSERVABLE_LIST;
    }

    public static ObservableList<Symbol> getSymbolObservableList() {
        return SYMBOL_OBSERVABLE_LIST;
    }

    public static ObservableList<ContaToken> getContaTokenObservableList() {
        return CONTA_TOKEN_OBSERVABLE_LIST;
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

    public static Integer getTypeCandle_id() {
        return typeCandle_id;
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

    public static int getQtdCandlesAnalise() {
        return qtdCandlesAnalise.get();
    }

    public static IntegerProperty qtdCandlesAnaliseProperty() {
        return qtdCandlesAnalise;
    }

    public static void setQtdCandlesAnalise(int qtdCandlesAnalise) {
        Operacoes.qtdCandlesAnalise.set(qtdCandlesAnalise);
    }

    public static ObjectProperty<Ohlc>[] getUltimoOhlcStr() {
        return ultimoOhlcStr;
    }

    public static void setUltimoOhlcStr(ObjectProperty<Ohlc>[] ultimoOhlcStr) {
        Operacoes.ultimoOhlcStr = ultimoOhlcStr;
    }

    public static StringProperty[] getTimeCandleStart() {
        return timeCandleStart;
    }

    public static void setTimeCandleStart(StringProperty[] timeCandleStart) {
        Operacoes.timeCandleStart = timeCandleStart;
    }

    public static IntegerProperty[] getTimeCandleToClose() {
        return timeCandleToClose;
    }

    public static void setTimeCandleToClose(IntegerProperty[] timeCandleToClose) {
        Operacoes.timeCandleToClose = timeCandleToClose;
    }

    public static BooleanProperty[][] getFirstBuy() {
        return firstBuy;
    }

    public static void setFirstBuy(BooleanProperty[][] firstBuy) {
        Operacoes.firstBuy = firstBuy;
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

    public static ObservableList<HistoricoDeTicks> getHistoricoDeTicksObservableList() {
        return historicoDeTicksObservableList;
    }

    public static void setHistoricoDeTicksObservableList
            (ObservableList<HistoricoDeTicks> historicoDeTicksObservableList) {
        Operacoes.historicoDeTicksObservableList = historicoDeTicksObservableList;
    }

    public static ObservableList<HistoricoDeCandles> getHistoricoDeCandlesObservableList() {
        return historicoDeCandlesObservableList;
    }

    public static void setHistoricoDeCandlesObservableList
            (ObservableList<HistoricoDeCandles> historicoDeCandlesObservableList) {
        Operacoes.historicoDeCandlesObservableList = historicoDeCandlesObservableList;
    }

    public static ObservableList<Transacoes> getTransacoesObservableList() {
        return transacoesObservableList;
    }

    public static void setTransacoesObservableList(ObservableList<Transacoes> transacoesObservableList) {
        Operacoes.transacoesObservableList = transacoesObservableList;
    }

    public static TmodelTransacoes[][] getTmodelTransacoes() {
        return tmodelTransacoes;
    }

    public static void setTmodelTransacoes(TmodelTransacoes[][] tmodelTransacoes) {
        Operacoes.tmodelTransacoes = tmodelTransacoes;
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

    public static IntegerProperty[] getQtdCandlesEntrada() {
        return qtdCandlesEntrada;
    }

    public static void setQtdCandlesEntrada(IntegerProperty[] qtdCandlesEntrada) {
        Operacoes.qtdCandlesEntrada = qtdCandlesEntrada;
    }

    public static ObjectProperty<BigDecimal>[] getPorcMartingale() {
        return porcMartingale;
    }

    public static void setPorcMartingale(ObjectProperty<BigDecimal>[] porcMartingale) {
        Operacoes.porcMartingale = porcMartingale;
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

    public static ObjectProperty<BigDecimal>[][] getVlrLossAcumulado() {
        return vlrLossAcumulado;
    }

    public static void setVlrLossAcumulado(ObjectProperty<BigDecimal>[][] vlrLossAcumulado) {
        Operacoes.vlrLossAcumulado = vlrLossAcumulado;
    }

    public static int getQtdStakes() {
        return qtdStakes.get();
    }

    public static IntegerProperty qtdStakesProperty() {
        return qtdStakes;
    }

    public static void setQtdStakes(int qtdStakes) {
        Operacoes.qtdStakes.set(qtdStakes);
    }

    public static IntegerProperty[] getQtdTimeFrameStakes() {
        return qtdTimeFrameStakes;
    }

    public static void setQtdTimeFrameStakes(IntegerProperty[] qtdTimeFrameStakes) {
        Operacoes.qtdTimeFrameStakes = qtdTimeFrameStakes;
    }

    public static IntegerProperty[][] getQtdTframeSymbolStakes() {
        return qtdTframeSymbolStakes;
    }

    public static void setQtdTframeSymbolStakes(IntegerProperty[][] qtdTframeSymbolStakes) {
        Operacoes.qtdTframeSymbolStakes = qtdTframeSymbolStakes;
    }

    public static int getQtdStakesWins() {
        return qtdStakesWins.get();
    }

    public static IntegerProperty qtdStakesWinsProperty() {
        return qtdStakesWins;
    }

    public static void setQtdStakesWins(int qtdStakesWins) {
        Operacoes.qtdStakesWins.set(qtdStakesWins);
    }

    public static IntegerProperty[] getQtdTimeFrameStakesWins() {
        return qtdTimeFrameStakesWins;
    }

    public static void setQtdTimeFrameStakesWins(IntegerProperty[] qtdTimeFrameStakesWins) {
        Operacoes.qtdTimeFrameStakesWins = qtdTimeFrameStakesWins;
    }

    public static IntegerProperty[][] getQtdTframeSymbolStakesWins() {
        return qtdTframeSymbolStakesWins;
    }

    public static void setQtdTframeSymbolStakesWins(IntegerProperty[][] qtdTframeSymbolStakesWins) {
        Operacoes.qtdTframeSymbolStakesWins = qtdTframeSymbolStakesWins;
    }

    public static int getQtdStakesLoss() {
        return qtdStakesLoss.get();
    }

    public static IntegerProperty qtdStakesLossProperty() {
        return qtdStakesLoss;
    }

    public static void setQtdStakesLoss(int qtdStakesLoss) {
        Operacoes.qtdStakesLoss.set(qtdStakesLoss);
    }

    public static IntegerProperty[] getQtdTimeFrameStakesLoss() {
        return qtdTimeFrameStakesLoss;
    }

    public static void setQtdTimeFrameStakesLoss(IntegerProperty[] qtdTimeFrameStakesLoss) {
        Operacoes.qtdTimeFrameStakesLoss = qtdTimeFrameStakesLoss;
    }

    public static IntegerProperty[][] getQtdTframeSymbolStakesLoss() {
        return qtdTframeSymbolStakesLoss;
    }

    public static void setQtdTframeSymbolStakesLoss(IntegerProperty[][] qtdTframeSymbolStakesLoss) {
        Operacoes.qtdTframeSymbolStakesLoss = qtdTframeSymbolStakesLoss;
    }

    public static BigDecimal getVlrStakesIn() {
        return vlrStakesIn.get();
    }

    public static ObjectProperty<BigDecimal> vlrStakesInProperty() {
        return vlrStakesIn;
    }

    public static void setVlrStakesIn(BigDecimal vlrStakesIn) {
        Operacoes.vlrStakesIn.set(vlrStakesIn);
    }

    public static ObjectProperty<BigDecimal>[] getVlrTimeFrameStakesIn() {
        return vlrTimeFrameStakesIn;
    }

    public static void setVlrTimeFrameStakesIn(ObjectProperty<BigDecimal>[] vlrTimeFrameStakesIn) {
        Operacoes.vlrTimeFrameStakesIn = vlrTimeFrameStakesIn;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrTframeSymbolIn() {
        return vlrTframeSymbolIn;
    }

    public static void setVlrTframeSymbolIn(ObjectProperty<BigDecimal>[][] vlrTframeSymbolIn) {
        Operacoes.vlrTframeSymbolIn = vlrTframeSymbolIn;
    }

    public static BigDecimal getVlrStakesOut() {
        return vlrStakesOut.get();
    }

    public static ObjectProperty<BigDecimal> vlrStakesOutProperty() {
        return vlrStakesOut;
    }

    public static void setVlrStakesOut(BigDecimal vlrStakesOut) {
        Operacoes.vlrStakesOut.set(vlrStakesOut);
    }

    public static ObjectProperty<BigDecimal>[] getVlrTimeFrameStakesOut() {
        return vlrTimeFrameStakesOut;
    }

    public static void setVlrTimeFrameStakesOut(ObjectProperty<BigDecimal>[] vlrTimeFrameStakesOut) {
        Operacoes.vlrTimeFrameStakesOut = vlrTimeFrameStakesOut;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrTframeSymbolOut() {
        return vlrTframeSymbolOut;
    }

    public static void setVlrTframeSymbolOut(ObjectProperty<BigDecimal>[][] vlrTframeSymbolOut) {
        Operacoes.vlrTframeSymbolOut = vlrTframeSymbolOut;
    }

    public static BigDecimal getVlrStakesDiff() {
        return vlrStakesDiff.get();
    }

    public static ObjectProperty<BigDecimal> vlrStakesDiffProperty() {
        return vlrStakesDiff;
    }

    public static void setVlrStakesDiff(BigDecimal vlrStakesDiff) {
        Operacoes.vlrStakesDiff.set(vlrStakesDiff);
    }

    public static ObjectProperty<BigDecimal>[] getVlrTimeFrameStakesDiff() {
        return vlrTimeFrameStakesDiff;
    }

    public static void setVlrTimeFrameStakesDiff(ObjectProperty<BigDecimal>[] vlrTimeFrameStakesDiff) {
        Operacoes.vlrTimeFrameStakesDiff = vlrTimeFrameStakesDiff;
    }

    public static ObjectProperty<BigDecimal>[][] getVlrTframeSymbolDiff() {
        return vlrTframeSymbolDiff;
    }

    public static void setVlrTframeSymbolDiff(ObjectProperty<BigDecimal>[][] vlrTframeSymbolDiff) {
        Operacoes.vlrTframeSymbolDiff = vlrTframeSymbolDiff;
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

    public JFXCheckBox getChkTpn_T01_TimeAtivo() {
        return chkTpn_T01_TimeAtivo;
    }

    public void setChkTpn_T01_TimeAtivo(JFXCheckBox chkTpn_T01_TimeAtivo) {
        this.chkTpn_T01_TimeAtivo = chkTpn_T01_TimeAtivo;
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

    public TitledPane getTpn_T02() {
        return tpn_T02;
    }

    public void setTpn_T02(TitledPane tpn_T02) {
        this.tpn_T02 = tpn_T02;
    }

    public JFXCheckBox getChkTpn_T02_TimeAtivo() {
        return chkTpn_T02_TimeAtivo;
    }

    public void setChkTpn_T02_TimeAtivo(JFXCheckBox chkTpn_T02_TimeAtivo) {
        this.chkTpn_T02_TimeAtivo = chkTpn_T02_TimeAtivo;
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

    public TableView getTbvTransacoes_T02_Op01() {
        return tbvTransacoes_T02_Op01;
    }

    public void setTbvTransacoes_T02_Op01(TableView tbvTransacoes_T02_Op01) {
        this.tbvTransacoes_T02_Op01 = tbvTransacoes_T02_Op01;
    }

    public Label getLblSymbol_T02_Op02() {
        return lblSymbol_T02_Op02;
    }

    public void setLblSymbol_T02_Op02(Label lblSymbol_T02_Op02) {
        this.lblSymbol_T02_Op02 = lblSymbol_T02_Op02;
    }

    public Label getLblQtdCall_T02_Op02() {
        return lblQtdCall_T02_Op02;
    }

    public void setLblQtdCall_T02_Op02(Label lblQtdCall_T02_Op02) {
        this.lblQtdCall_T02_Op02 = lblQtdCall_T02_Op02;
    }

    public Label getLblQtdPut_T02_Op02() {
        return lblQtdPut_T02_Op02;
    }

    public void setLblQtdPut_T02_Op02(Label lblQtdPut_T02_Op02) {
        this.lblQtdPut_T02_Op02 = lblQtdPut_T02_Op02;
    }

    public Label getLblQtdCallOrPut_T02_Op02() {
        return lblQtdCallOrPut_T02_Op02;
    }

    public void setLblQtdCallOrPut_T02_Op02(Label lblQtdCallOrPut_T02_Op02) {
        this.lblQtdCallOrPut_T02_Op02 = lblQtdCallOrPut_T02_Op02;
    }

    public ImageView getImgCallOrPut_T02_Op02() {
        return imgCallOrPut_T02_Op02;
    }

    public void setImgCallOrPut_T02_Op02(ImageView imgCallOrPut_T02_Op02) {
        this.imgCallOrPut_T02_Op02 = imgCallOrPut_T02_Op02;
    }

    public Label getLblQtdStakes_T02_Op02() {
        return lblQtdStakes_T02_Op02;
    }

    public void setLblQtdStakes_T02_Op02(Label lblQtdStakes_T02_Op02) {
        this.lblQtdStakes_T02_Op02 = lblQtdStakes_T02_Op02;
    }

    public Label getLblQtdWins_T02_Op02() {
        return lblQtdWins_T02_Op02;
    }

    public void setLblQtdWins_T02_Op02(Label lblQtdWins_T02_Op02) {
        this.lblQtdWins_T02_Op02 = lblQtdWins_T02_Op02;
    }

    public Label getLblQtdLoss_T02_Op02() {
        return lblQtdLoss_T02_Op02;
    }

    public void setLblQtdLoss_T02_Op02(Label lblQtdLoss_T02_Op02) {
        this.lblQtdLoss_T02_Op02 = lblQtdLoss_T02_Op02;
    }

    public Label getLblVlrIn_T02_Op02() {
        return lblVlrIn_T02_Op02;
    }

    public void setLblVlrIn_T02_Op02(Label lblVlrIn_T02_Op02) {
        this.lblVlrIn_T02_Op02 = lblVlrIn_T02_Op02;
    }

    public Label getLblVlrOut_T02_Op02() {
        return lblVlrOut_T02_Op02;
    }

    public void setLblVlrOut_T02_Op02(Label lblVlrOut_T02_Op02) {
        this.lblVlrOut_T02_Op02 = lblVlrOut_T02_Op02;
    }

    public Label getLblVlrDiff_T02_Op02() {
        return lblVlrDiff_T02_Op02;
    }

    public void setLblVlrDiff_T02_Op02(Label lblVlrDiff_T02_Op02) {
        this.lblVlrDiff_T02_Op02 = lblVlrDiff_T02_Op02;
    }

    public TableView getTbvTransacoes_T02_Op02() {
        return tbvTransacoes_T02_Op02;
    }

    public void setTbvTransacoes_T02_Op02(TableView tbvTransacoes_T02_Op02) {
        this.tbvTransacoes_T02_Op02 = tbvTransacoes_T02_Op02;
    }

    public Label getLblSymbol_T02_Op03() {
        return lblSymbol_T02_Op03;
    }

    public void setLblSymbol_T02_Op03(Label lblSymbol_T02_Op03) {
        this.lblSymbol_T02_Op03 = lblSymbol_T02_Op03;
    }

    public Label getLblQtdCall_T02_Op03() {
        return lblQtdCall_T02_Op03;
    }

    public void setLblQtdCall_T02_Op03(Label lblQtdCall_T02_Op03) {
        this.lblQtdCall_T02_Op03 = lblQtdCall_T02_Op03;
    }

    public Label getLblQtdPut_T02_Op03() {
        return lblQtdPut_T02_Op03;
    }

    public void setLblQtdPut_T02_Op03(Label lblQtdPut_T02_Op03) {
        this.lblQtdPut_T02_Op03 = lblQtdPut_T02_Op03;
    }

    public Label getLblQtdCallOrPut_T02_Op03() {
        return lblQtdCallOrPut_T02_Op03;
    }

    public void setLblQtdCallOrPut_T02_Op03(Label lblQtdCallOrPut_T02_Op03) {
        this.lblQtdCallOrPut_T02_Op03 = lblQtdCallOrPut_T02_Op03;
    }

    public ImageView getImgCallOrPut_T02_Op03() {
        return imgCallOrPut_T02_Op03;
    }

    public void setImgCallOrPut_T02_Op03(ImageView imgCallOrPut_T02_Op03) {
        this.imgCallOrPut_T02_Op03 = imgCallOrPut_T02_Op03;
    }

    public Label getLblQtdStakes_T02_Op03() {
        return lblQtdStakes_T02_Op03;
    }

    public void setLblQtdStakes_T02_Op03(Label lblQtdStakes_T02_Op03) {
        this.lblQtdStakes_T02_Op03 = lblQtdStakes_T02_Op03;
    }

    public Label getLblQtdWins_T02_Op03() {
        return lblQtdWins_T02_Op03;
    }

    public void setLblQtdWins_T02_Op03(Label lblQtdWins_T02_Op03) {
        this.lblQtdWins_T02_Op03 = lblQtdWins_T02_Op03;
    }

    public Label getLblQtdLoss_T02_Op03() {
        return lblQtdLoss_T02_Op03;
    }

    public void setLblQtdLoss_T02_Op03(Label lblQtdLoss_T02_Op03) {
        this.lblQtdLoss_T02_Op03 = lblQtdLoss_T02_Op03;
    }

    public Label getLblVlrIn_T02_Op03() {
        return lblVlrIn_T02_Op03;
    }

    public void setLblVlrIn_T02_Op03(Label lblVlrIn_T02_Op03) {
        this.lblVlrIn_T02_Op03 = lblVlrIn_T02_Op03;
    }

    public Label getLblVlrOut_T02_Op03() {
        return lblVlrOut_T02_Op03;
    }

    public void setLblVlrOut_T02_Op03(Label lblVlrOut_T02_Op03) {
        this.lblVlrOut_T02_Op03 = lblVlrOut_T02_Op03;
    }

    public Label getLblVlrDiff_T02_Op03() {
        return lblVlrDiff_T02_Op03;
    }

    public void setLblVlrDiff_T02_Op03(Label lblVlrDiff_T02_Op03) {
        this.lblVlrDiff_T02_Op03 = lblVlrDiff_T02_Op03;
    }

    public TableView getTbvTransacoes_T02_Op03() {
        return tbvTransacoes_T02_Op03;
    }

    public void setTbvTransacoes_T02_Op03(TableView tbvTransacoes_T02_Op03) {
        this.tbvTransacoes_T02_Op03 = tbvTransacoes_T02_Op03;
    }

    public Label getLblSymbol_T02_Op04() {
        return lblSymbol_T02_Op04;
    }

    public void setLblSymbol_T02_Op04(Label lblSymbol_T02_Op04) {
        this.lblSymbol_T02_Op04 = lblSymbol_T02_Op04;
    }

    public Label getLblQtdCall_T02_Op04() {
        return lblQtdCall_T02_Op04;
    }

    public void setLblQtdCall_T02_Op04(Label lblQtdCall_T02_Op04) {
        this.lblQtdCall_T02_Op04 = lblQtdCall_T02_Op04;
    }

    public Label getLblQtdPut_T02_Op04() {
        return lblQtdPut_T02_Op04;
    }

    public void setLblQtdPut_T02_Op04(Label lblQtdPut_T02_Op04) {
        this.lblQtdPut_T02_Op04 = lblQtdPut_T02_Op04;
    }

    public Label getLblQtdCallOrPut_T02_Op04() {
        return lblQtdCallOrPut_T02_Op04;
    }

    public void setLblQtdCallOrPut_T02_Op04(Label lblQtdCallOrPut_T02_Op04) {
        this.lblQtdCallOrPut_T02_Op04 = lblQtdCallOrPut_T02_Op04;
    }

    public ImageView getImgCallOrPut_T02_Op04() {
        return imgCallOrPut_T02_Op04;
    }

    public void setImgCallOrPut_T02_Op04(ImageView imgCallOrPut_T02_Op04) {
        this.imgCallOrPut_T02_Op04 = imgCallOrPut_T02_Op04;
    }

    public Label getLblQtdStakes_T02_Op04() {
        return lblQtdStakes_T02_Op04;
    }

    public void setLblQtdStakes_T02_Op04(Label lblQtdStakes_T02_Op04) {
        this.lblQtdStakes_T02_Op04 = lblQtdStakes_T02_Op04;
    }

    public Label getLblQtdWins_T02_Op04() {
        return lblQtdWins_T02_Op04;
    }

    public void setLblQtdWins_T02_Op04(Label lblQtdWins_T02_Op04) {
        this.lblQtdWins_T02_Op04 = lblQtdWins_T02_Op04;
    }

    public Label getLblQtdLoss_T02_Op04() {
        return lblQtdLoss_T02_Op04;
    }

    public void setLblQtdLoss_T02_Op04(Label lblQtdLoss_T02_Op04) {
        this.lblQtdLoss_T02_Op04 = lblQtdLoss_T02_Op04;
    }

    public Label getLblVlrIn_T02_Op04() {
        return lblVlrIn_T02_Op04;
    }

    public void setLblVlrIn_T02_Op04(Label lblVlrIn_T02_Op04) {
        this.lblVlrIn_T02_Op04 = lblVlrIn_T02_Op04;
    }

    public Label getLblVlrOut_T02_Op04() {
        return lblVlrOut_T02_Op04;
    }

    public void setLblVlrOut_T02_Op04(Label lblVlrOut_T02_Op04) {
        this.lblVlrOut_T02_Op04 = lblVlrOut_T02_Op04;
    }

    public Label getLblVlrDiff_T02_Op04() {
        return lblVlrDiff_T02_Op04;
    }

    public void setLblVlrDiff_T02_Op04(Label lblVlrDiff_T02_Op04) {
        this.lblVlrDiff_T02_Op04 = lblVlrDiff_T02_Op04;
    }

    public TableView getTbvTransacoes_T02_Op04() {
        return tbvTransacoes_T02_Op04;
    }

    public void setTbvTransacoes_T02_Op04(TableView tbvTransacoes_T02_Op04) {
        this.tbvTransacoes_T02_Op04 = tbvTransacoes_T02_Op04;
    }

    public Label getLblSymbol_T02_Op05() {
        return lblSymbol_T02_Op05;
    }

    public void setLblSymbol_T02_Op05(Label lblSymbol_T02_Op05) {
        this.lblSymbol_T02_Op05 = lblSymbol_T02_Op05;
    }

    public Label getLblQtdCall_T02_Op05() {
        return lblQtdCall_T02_Op05;
    }

    public void setLblQtdCall_T02_Op05(Label lblQtdCall_T02_Op05) {
        this.lblQtdCall_T02_Op05 = lblQtdCall_T02_Op05;
    }

    public Label getLblQtdPut_T02_Op05() {
        return lblQtdPut_T02_Op05;
    }

    public void setLblQtdPut_T02_Op05(Label lblQtdPut_T02_Op05) {
        this.lblQtdPut_T02_Op05 = lblQtdPut_T02_Op05;
    }

    public Label getLblQtdCallOrPut_T02_Op05() {
        return lblQtdCallOrPut_T02_Op05;
    }

    public void setLblQtdCallOrPut_T02_Op05(Label lblQtdCallOrPut_T02_Op05) {
        this.lblQtdCallOrPut_T02_Op05 = lblQtdCallOrPut_T02_Op05;
    }

    public ImageView getImgCallOrPut_T02_Op05() {
        return imgCallOrPut_T02_Op05;
    }

    public void setImgCallOrPut_T02_Op05(ImageView imgCallOrPut_T02_Op05) {
        this.imgCallOrPut_T02_Op05 = imgCallOrPut_T02_Op05;
    }

    public Label getLblQtdStakes_T02_Op05() {
        return lblQtdStakes_T02_Op05;
    }

    public void setLblQtdStakes_T02_Op05(Label lblQtdStakes_T02_Op05) {
        this.lblQtdStakes_T02_Op05 = lblQtdStakes_T02_Op05;
    }

    public Label getLblQtdWins_T02_Op05() {
        return lblQtdWins_T02_Op05;
    }

    public void setLblQtdWins_T02_Op05(Label lblQtdWins_T02_Op05) {
        this.lblQtdWins_T02_Op05 = lblQtdWins_T02_Op05;
    }

    public Label getLblQtdLoss_T02_Op05() {
        return lblQtdLoss_T02_Op05;
    }

    public void setLblQtdLoss_T02_Op05(Label lblQtdLoss_T02_Op05) {
        this.lblQtdLoss_T02_Op05 = lblQtdLoss_T02_Op05;
    }

    public Label getLblVlrIn_T02_Op05() {
        return lblVlrIn_T02_Op05;
    }

    public void setLblVlrIn_T02_Op05(Label lblVlrIn_T02_Op05) {
        this.lblVlrIn_T02_Op05 = lblVlrIn_T02_Op05;
    }

    public Label getLblVlrOut_T02_Op05() {
        return lblVlrOut_T02_Op05;
    }

    public void setLblVlrOut_T02_Op05(Label lblVlrOut_T02_Op05) {
        this.lblVlrOut_T02_Op05 = lblVlrOut_T02_Op05;
    }

    public Label getLblVlrDiff_T02_Op05() {
        return lblVlrDiff_T02_Op05;
    }

    public void setLblVlrDiff_T02_Op05(Label lblVlrDiff_T02_Op05) {
        this.lblVlrDiff_T02_Op05 = lblVlrDiff_T02_Op05;
    }

    public TableView getTbvTransacoes_T02_Op05() {
        return tbvTransacoes_T02_Op05;
    }

    public void setTbvTransacoes_T02_Op05(TableView tbvTransacoes_T02_Op05) {
        this.tbvTransacoes_T02_Op05 = tbvTransacoes_T02_Op05;
    }

    public Label getLblSymbol_T02_Op06() {
        return lblSymbol_T02_Op06;
    }

    public void setLblSymbol_T02_Op06(Label lblSymbol_T02_Op06) {
        this.lblSymbol_T02_Op06 = lblSymbol_T02_Op06;
    }

    public Label getLblQtdCall_T02_Op06() {
        return lblQtdCall_T02_Op06;
    }

    public void setLblQtdCall_T02_Op06(Label lblQtdCall_T02_Op06) {
        this.lblQtdCall_T02_Op06 = lblQtdCall_T02_Op06;
    }

    public Label getLblQtdPut_T02_Op06() {
        return lblQtdPut_T02_Op06;
    }

    public void setLblQtdPut_T02_Op06(Label lblQtdPut_T02_Op06) {
        this.lblQtdPut_T02_Op06 = lblQtdPut_T02_Op06;
    }

    public Label getLblQtdCallOrPut_T02_Op06() {
        return lblQtdCallOrPut_T02_Op06;
    }

    public void setLblQtdCallOrPut_T02_Op06(Label lblQtdCallOrPut_T02_Op06) {
        this.lblQtdCallOrPut_T02_Op06 = lblQtdCallOrPut_T02_Op06;
    }

    public ImageView getImgCallOrPut_T02_Op06() {
        return imgCallOrPut_T02_Op06;
    }

    public void setImgCallOrPut_T02_Op06(ImageView imgCallOrPut_T02_Op06) {
        this.imgCallOrPut_T02_Op06 = imgCallOrPut_T02_Op06;
    }

    public Label getLblQtdStakes_T02_Op06() {
        return lblQtdStakes_T02_Op06;
    }

    public void setLblQtdStakes_T02_Op06(Label lblQtdStakes_T02_Op06) {
        this.lblQtdStakes_T02_Op06 = lblQtdStakes_T02_Op06;
    }

    public Label getLblQtdWins_T02_Op06() {
        return lblQtdWins_T02_Op06;
    }

    public void setLblQtdWins_T02_Op06(Label lblQtdWins_T02_Op06) {
        this.lblQtdWins_T02_Op06 = lblQtdWins_T02_Op06;
    }

    public Label getLblQtdLoss_T02_Op06() {
        return lblQtdLoss_T02_Op06;
    }

    public void setLblQtdLoss_T02_Op06(Label lblQtdLoss_T02_Op06) {
        this.lblQtdLoss_T02_Op06 = lblQtdLoss_T02_Op06;
    }

    public Label getLblVlrIn_T02_Op06() {
        return lblVlrIn_T02_Op06;
    }

    public void setLblVlrIn_T02_Op06(Label lblVlrIn_T02_Op06) {
        this.lblVlrIn_T02_Op06 = lblVlrIn_T02_Op06;
    }

    public Label getLblVlrOut_T02_Op06() {
        return lblVlrOut_T02_Op06;
    }

    public void setLblVlrOut_T02_Op06(Label lblVlrOut_T02_Op06) {
        this.lblVlrOut_T02_Op06 = lblVlrOut_T02_Op06;
    }

    public Label getLblVlrDiff_T02_Op06() {
        return lblVlrDiff_T02_Op06;
    }

    public void setLblVlrDiff_T02_Op06(Label lblVlrDiff_T02_Op06) {
        this.lblVlrDiff_T02_Op06 = lblVlrDiff_T02_Op06;
    }

    public TableView getTbvTransacoes_T02_Op06() {
        return tbvTransacoes_T02_Op06;
    }

    public void setTbvTransacoes_T02_Op06(TableView tbvTransacoes_T02_Op06) {
        this.tbvTransacoes_T02_Op06 = tbvTransacoes_T02_Op06;
    }

    public Label getLblSymbol_T02_Op07() {
        return lblSymbol_T02_Op07;
    }

    public void setLblSymbol_T02_Op07(Label lblSymbol_T02_Op07) {
        this.lblSymbol_T02_Op07 = lblSymbol_T02_Op07;
    }

    public Label getLblQtdCall_T02_Op07() {
        return lblQtdCall_T02_Op07;
    }

    public void setLblQtdCall_T02_Op07(Label lblQtdCall_T02_Op07) {
        this.lblQtdCall_T02_Op07 = lblQtdCall_T02_Op07;
    }

    public Label getLblQtdPut_T02_Op07() {
        return lblQtdPut_T02_Op07;
    }

    public void setLblQtdPut_T02_Op07(Label lblQtdPut_T02_Op07) {
        this.lblQtdPut_T02_Op07 = lblQtdPut_T02_Op07;
    }

    public Label getLblQtdCallOrPut_T02_Op07() {
        return lblQtdCallOrPut_T02_Op07;
    }

    public void setLblQtdCallOrPut_T02_Op07(Label lblQtdCallOrPut_T02_Op07) {
        this.lblQtdCallOrPut_T02_Op07 = lblQtdCallOrPut_T02_Op07;
    }

    public ImageView getImgCallOrPut_T02_Op07() {
        return imgCallOrPut_T02_Op07;
    }

    public void setImgCallOrPut_T02_Op07(ImageView imgCallOrPut_T02_Op07) {
        this.imgCallOrPut_T02_Op07 = imgCallOrPut_T02_Op07;
    }

    public Label getLblQtdStakes_T02_Op07() {
        return lblQtdStakes_T02_Op07;
    }

    public void setLblQtdStakes_T02_Op07(Label lblQtdStakes_T02_Op07) {
        this.lblQtdStakes_T02_Op07 = lblQtdStakes_T02_Op07;
    }

    public Label getLblQtdWins_T02_Op07() {
        return lblQtdWins_T02_Op07;
    }

    public void setLblQtdWins_T02_Op07(Label lblQtdWins_T02_Op07) {
        this.lblQtdWins_T02_Op07 = lblQtdWins_T02_Op07;
    }

    public Label getLblQtdLoss_T02_Op07() {
        return lblQtdLoss_T02_Op07;
    }

    public void setLblQtdLoss_T02_Op07(Label lblQtdLoss_T02_Op07) {
        this.lblQtdLoss_T02_Op07 = lblQtdLoss_T02_Op07;
    }

    public Label getLblVlrIn_T02_Op07() {
        return lblVlrIn_T02_Op07;
    }

    public void setLblVlrIn_T02_Op07(Label lblVlrIn_T02_Op07) {
        this.lblVlrIn_T02_Op07 = lblVlrIn_T02_Op07;
    }

    public Label getLblVlrOut_T02_Op07() {
        return lblVlrOut_T02_Op07;
    }

    public void setLblVlrOut_T02_Op07(Label lblVlrOut_T02_Op07) {
        this.lblVlrOut_T02_Op07 = lblVlrOut_T02_Op07;
    }

    public Label getLblVlrDiff_T02_Op07() {
        return lblVlrDiff_T02_Op07;
    }

    public void setLblVlrDiff_T02_Op07(Label lblVlrDiff_T02_Op07) {
        this.lblVlrDiff_T02_Op07 = lblVlrDiff_T02_Op07;
    }

    public TableView getTbvTransacoes_T02_Op07() {
        return tbvTransacoes_T02_Op07;
    }

    public void setTbvTransacoes_T02_Op07(TableView tbvTransacoes_T02_Op07) {
        this.tbvTransacoes_T02_Op07 = tbvTransacoes_T02_Op07;
    }

    public Label getLblSymbol_T02_Op08() {
        return lblSymbol_T02_Op08;
    }

    public void setLblSymbol_T02_Op08(Label lblSymbol_T02_Op08) {
        this.lblSymbol_T02_Op08 = lblSymbol_T02_Op08;
    }

    public Label getLblQtdCall_T02_Op08() {
        return lblQtdCall_T02_Op08;
    }

    public void setLblQtdCall_T02_Op08(Label lblQtdCall_T02_Op08) {
        this.lblQtdCall_T02_Op08 = lblQtdCall_T02_Op08;
    }

    public Label getLblQtdPut_T02_Op08() {
        return lblQtdPut_T02_Op08;
    }

    public void setLblQtdPut_T02_Op08(Label lblQtdPut_T02_Op08) {
        this.lblQtdPut_T02_Op08 = lblQtdPut_T02_Op08;
    }

    public Label getLblQtdCallOrPut_T02_Op08() {
        return lblQtdCallOrPut_T02_Op08;
    }

    public void setLblQtdCallOrPut_T02_Op08(Label lblQtdCallOrPut_T02_Op08) {
        this.lblQtdCallOrPut_T02_Op08 = lblQtdCallOrPut_T02_Op08;
    }

    public ImageView getImgCallOrPut_T02_Op08() {
        return imgCallOrPut_T02_Op08;
    }

    public void setImgCallOrPut_T02_Op08(ImageView imgCallOrPut_T02_Op08) {
        this.imgCallOrPut_T02_Op08 = imgCallOrPut_T02_Op08;
    }

    public Label getLblQtdStakes_T02_Op08() {
        return lblQtdStakes_T02_Op08;
    }

    public void setLblQtdStakes_T02_Op08(Label lblQtdStakes_T02_Op08) {
        this.lblQtdStakes_T02_Op08 = lblQtdStakes_T02_Op08;
    }

    public Label getLblQtdWins_T02_Op08() {
        return lblQtdWins_T02_Op08;
    }

    public void setLblQtdWins_T02_Op08(Label lblQtdWins_T02_Op08) {
        this.lblQtdWins_T02_Op08 = lblQtdWins_T02_Op08;
    }

    public Label getLblQtdLoss_T02_Op08() {
        return lblQtdLoss_T02_Op08;
    }

    public void setLblQtdLoss_T02_Op08(Label lblQtdLoss_T02_Op08) {
        this.lblQtdLoss_T02_Op08 = lblQtdLoss_T02_Op08;
    }

    public Label getLblVlrIn_T02_Op08() {
        return lblVlrIn_T02_Op08;
    }

    public void setLblVlrIn_T02_Op08(Label lblVlrIn_T02_Op08) {
        this.lblVlrIn_T02_Op08 = lblVlrIn_T02_Op08;
    }

    public Label getLblVlrOut_T02_Op08() {
        return lblVlrOut_T02_Op08;
    }

    public void setLblVlrOut_T02_Op08(Label lblVlrOut_T02_Op08) {
        this.lblVlrOut_T02_Op08 = lblVlrOut_T02_Op08;
    }

    public Label getLblVlrDiff_T02_Op08() {
        return lblVlrDiff_T02_Op08;
    }

    public void setLblVlrDiff_T02_Op08(Label lblVlrDiff_T02_Op08) {
        this.lblVlrDiff_T02_Op08 = lblVlrDiff_T02_Op08;
    }

    public TableView getTbvTransacoes_T02_Op08() {
        return tbvTransacoes_T02_Op08;
    }

    public void setTbvTransacoes_T02_Op08(TableView tbvTransacoes_T02_Op08) {
        this.tbvTransacoes_T02_Op08 = tbvTransacoes_T02_Op08;
    }

    public Label getLblSymbol_T02_Op09() {
        return lblSymbol_T02_Op09;
    }

    public void setLblSymbol_T02_Op09(Label lblSymbol_T02_Op09) {
        this.lblSymbol_T02_Op09 = lblSymbol_T02_Op09;
    }

    public Label getLblQtdCall_T02_Op09() {
        return lblQtdCall_T02_Op09;
    }

    public void setLblQtdCall_T02_Op09(Label lblQtdCall_T02_Op09) {
        this.lblQtdCall_T02_Op09 = lblQtdCall_T02_Op09;
    }

    public Label getLblQtdPut_T02_Op09() {
        return lblQtdPut_T02_Op09;
    }

    public void setLblQtdPut_T02_Op09(Label lblQtdPut_T02_Op09) {
        this.lblQtdPut_T02_Op09 = lblQtdPut_T02_Op09;
    }

    public Label getLblQtdCallOrPut_T02_Op09() {
        return lblQtdCallOrPut_T02_Op09;
    }

    public void setLblQtdCallOrPut_T02_Op09(Label lblQtdCallOrPut_T02_Op09) {
        this.lblQtdCallOrPut_T02_Op09 = lblQtdCallOrPut_T02_Op09;
    }

    public ImageView getImgCallOrPut_T02_Op09() {
        return imgCallOrPut_T02_Op09;
    }

    public void setImgCallOrPut_T02_Op09(ImageView imgCallOrPut_T02_Op09) {
        this.imgCallOrPut_T02_Op09 = imgCallOrPut_T02_Op09;
    }

    public Label getLblQtdStakes_T02_Op09() {
        return lblQtdStakes_T02_Op09;
    }

    public void setLblQtdStakes_T02_Op09(Label lblQtdStakes_T02_Op09) {
        this.lblQtdStakes_T02_Op09 = lblQtdStakes_T02_Op09;
    }

    public Label getLblQtdWins_T02_Op09() {
        return lblQtdWins_T02_Op09;
    }

    public void setLblQtdWins_T02_Op09(Label lblQtdWins_T02_Op09) {
        this.lblQtdWins_T02_Op09 = lblQtdWins_T02_Op09;
    }

    public Label getLblQtdLoss_T02_Op09() {
        return lblQtdLoss_T02_Op09;
    }

    public void setLblQtdLoss_T02_Op09(Label lblQtdLoss_T02_Op09) {
        this.lblQtdLoss_T02_Op09 = lblQtdLoss_T02_Op09;
    }

    public Label getLblVlrIn_T02_Op09() {
        return lblVlrIn_T02_Op09;
    }

    public void setLblVlrIn_T02_Op09(Label lblVlrIn_T02_Op09) {
        this.lblVlrIn_T02_Op09 = lblVlrIn_T02_Op09;
    }

    public Label getLblVlrOut_T02_Op09() {
        return lblVlrOut_T02_Op09;
    }

    public void setLblVlrOut_T02_Op09(Label lblVlrOut_T02_Op09) {
        this.lblVlrOut_T02_Op09 = lblVlrOut_T02_Op09;
    }

    public Label getLblVlrDiff_T02_Op09() {
        return lblVlrDiff_T02_Op09;
    }

    public void setLblVlrDiff_T02_Op09(Label lblVlrDiff_T02_Op09) {
        this.lblVlrDiff_T02_Op09 = lblVlrDiff_T02_Op09;
    }

    public TableView getTbvTransacoes_T02_Op09() {
        return tbvTransacoes_T02_Op09;
    }

    public void setTbvTransacoes_T02_Op09(TableView tbvTransacoes_T02_Op09) {
        this.tbvTransacoes_T02_Op09 = tbvTransacoes_T02_Op09;
    }

    public Label getLblSymbol_T02_Op10() {
        return lblSymbol_T02_Op10;
    }

    public void setLblSymbol_T02_Op10(Label lblSymbol_T02_Op10) {
        this.lblSymbol_T02_Op10 = lblSymbol_T02_Op10;
    }

    public Label getLblQtdCall_T02_Op10() {
        return lblQtdCall_T02_Op10;
    }

    public void setLblQtdCall_T02_Op10(Label lblQtdCall_T02_Op10) {
        this.lblQtdCall_T02_Op10 = lblQtdCall_T02_Op10;
    }

    public Label getLblQtdPut_T02_Op10() {
        return lblQtdPut_T02_Op10;
    }

    public void setLblQtdPut_T02_Op10(Label lblQtdPut_T02_Op10) {
        this.lblQtdPut_T02_Op10 = lblQtdPut_T02_Op10;
    }

    public Label getLblQtdCallOrPut_T02_Op10() {
        return lblQtdCallOrPut_T02_Op10;
    }

    public void setLblQtdCallOrPut_T02_Op10(Label lblQtdCallOrPut_T02_Op10) {
        this.lblQtdCallOrPut_T02_Op10 = lblQtdCallOrPut_T02_Op10;
    }

    public ImageView getImgCallOrPut_T02_Op10() {
        return imgCallOrPut_T02_Op10;
    }

    public void setImgCallOrPut_T02_Op10(ImageView imgCallOrPut_T02_Op10) {
        this.imgCallOrPut_T02_Op10 = imgCallOrPut_T02_Op10;
    }

    public Label getLblQtdStakes_T02_Op10() {
        return lblQtdStakes_T02_Op10;
    }

    public void setLblQtdStakes_T02_Op10(Label lblQtdStakes_T02_Op10) {
        this.lblQtdStakes_T02_Op10 = lblQtdStakes_T02_Op10;
    }

    public Label getLblQtdWins_T02_Op10() {
        return lblQtdWins_T02_Op10;
    }

    public void setLblQtdWins_T02_Op10(Label lblQtdWins_T02_Op10) {
        this.lblQtdWins_T02_Op10 = lblQtdWins_T02_Op10;
    }

    public Label getLblQtdLoss_T02_Op10() {
        return lblQtdLoss_T02_Op10;
    }

    public void setLblQtdLoss_T02_Op10(Label lblQtdLoss_T02_Op10) {
        this.lblQtdLoss_T02_Op10 = lblQtdLoss_T02_Op10;
    }

    public Label getLblVlrIn_T02_Op10() {
        return lblVlrIn_T02_Op10;
    }

    public void setLblVlrIn_T02_Op10(Label lblVlrIn_T02_Op10) {
        this.lblVlrIn_T02_Op10 = lblVlrIn_T02_Op10;
    }

    public Label getLblVlrOut_T02_Op10() {
        return lblVlrOut_T02_Op10;
    }

    public void setLblVlrOut_T02_Op10(Label lblVlrOut_T02_Op10) {
        this.lblVlrOut_T02_Op10 = lblVlrOut_T02_Op10;
    }

    public Label getLblVlrDiff_T02_Op10() {
        return lblVlrDiff_T02_Op10;
    }

    public void setLblVlrDiff_T02_Op10(Label lblVlrDiff_T02_Op10) {
        this.lblVlrDiff_T02_Op10 = lblVlrDiff_T02_Op10;
    }

    public TableView getTbvTransacoes_T02_Op10() {
        return tbvTransacoes_T02_Op10;
    }

    public void setTbvTransacoes_T02_Op10(TableView tbvTransacoes_T02_Op10) {
        this.tbvTransacoes_T02_Op10 = tbvTransacoes_T02_Op10;
    }

    public Label getLblSymbol_T02_Op11() {
        return lblSymbol_T02_Op11;
    }

    public void setLblSymbol_T02_Op11(Label lblSymbol_T02_Op11) {
        this.lblSymbol_T02_Op11 = lblSymbol_T02_Op11;
    }

    public Label getLblQtdCall_T02_Op11() {
        return lblQtdCall_T02_Op11;
    }

    public void setLblQtdCall_T02_Op11(Label lblQtdCall_T02_Op11) {
        this.lblQtdCall_T02_Op11 = lblQtdCall_T02_Op11;
    }

    public Label getLblQtdPut_T02_Op11() {
        return lblQtdPut_T02_Op11;
    }

    public void setLblQtdPut_T02_Op11(Label lblQtdPut_T02_Op11) {
        this.lblQtdPut_T02_Op11 = lblQtdPut_T02_Op11;
    }

    public Label getLblQtdCallOrPut_T02_Op11() {
        return lblQtdCallOrPut_T02_Op11;
    }

    public void setLblQtdCallOrPut_T02_Op11(Label lblQtdCallOrPut_T02_Op11) {
        this.lblQtdCallOrPut_T02_Op11 = lblQtdCallOrPut_T02_Op11;
    }

    public ImageView getImgCallOrPut_T02_Op11() {
        return imgCallOrPut_T02_Op11;
    }

    public void setImgCallOrPut_T02_Op11(ImageView imgCallOrPut_T02_Op11) {
        this.imgCallOrPut_T02_Op11 = imgCallOrPut_T02_Op11;
    }

    public Label getLblQtdStakes_T02_Op11() {
        return lblQtdStakes_T02_Op11;
    }

    public void setLblQtdStakes_T02_Op11(Label lblQtdStakes_T02_Op11) {
        this.lblQtdStakes_T02_Op11 = lblQtdStakes_T02_Op11;
    }

    public Label getLblQtdWins_T02_Op11() {
        return lblQtdWins_T02_Op11;
    }

    public void setLblQtdWins_T02_Op11(Label lblQtdWins_T02_Op11) {
        this.lblQtdWins_T02_Op11 = lblQtdWins_T02_Op11;
    }

    public Label getLblQtdLoss_T02_Op11() {
        return lblQtdLoss_T02_Op11;
    }

    public void setLblQtdLoss_T02_Op11(Label lblQtdLoss_T02_Op11) {
        this.lblQtdLoss_T02_Op11 = lblQtdLoss_T02_Op11;
    }

    public Label getLblVlrIn_T02_Op11() {
        return lblVlrIn_T02_Op11;
    }

    public void setLblVlrIn_T02_Op11(Label lblVlrIn_T02_Op11) {
        this.lblVlrIn_T02_Op11 = lblVlrIn_T02_Op11;
    }

    public Label getLblVlrOut_T02_Op11() {
        return lblVlrOut_T02_Op11;
    }

    public void setLblVlrOut_T02_Op11(Label lblVlrOut_T02_Op11) {
        this.lblVlrOut_T02_Op11 = lblVlrOut_T02_Op11;
    }

    public Label getLblVlrDiff_T02_Op11() {
        return lblVlrDiff_T02_Op11;
    }

    public void setLblVlrDiff_T02_Op11(Label lblVlrDiff_T02_Op11) {
        this.lblVlrDiff_T02_Op11 = lblVlrDiff_T02_Op11;
    }

    public TableView getTbvTransacoes_T02_Op11() {
        return tbvTransacoes_T02_Op11;
    }

    public void setTbvTransacoes_T02_Op11(TableView tbvTransacoes_T02_Op11) {
        this.tbvTransacoes_T02_Op11 = tbvTransacoes_T02_Op11;
    }

    public Label getLblSymbol_T02_Op12() {
        return lblSymbol_T02_Op12;
    }

    public void setLblSymbol_T02_Op12(Label lblSymbol_T02_Op12) {
        this.lblSymbol_T02_Op12 = lblSymbol_T02_Op12;
    }

    public Label getLblQtdCall_T02_Op12() {
        return lblQtdCall_T02_Op12;
    }

    public void setLblQtdCall_T02_Op12(Label lblQtdCall_T02_Op12) {
        this.lblQtdCall_T02_Op12 = lblQtdCall_T02_Op12;
    }

    public Label getLblQtdPut_T02_Op12() {
        return lblQtdPut_T02_Op12;
    }

    public void setLblQtdPut_T02_Op12(Label lblQtdPut_T02_Op12) {
        this.lblQtdPut_T02_Op12 = lblQtdPut_T02_Op12;
    }

    public Label getLblQtdCallOrPut_T02_Op12() {
        return lblQtdCallOrPut_T02_Op12;
    }

    public void setLblQtdCallOrPut_T02_Op12(Label lblQtdCallOrPut_T02_Op12) {
        this.lblQtdCallOrPut_T02_Op12 = lblQtdCallOrPut_T02_Op12;
    }

    public ImageView getImgCallOrPut_T02_Op12() {
        return imgCallOrPut_T02_Op12;
    }

    public void setImgCallOrPut_T02_Op12(ImageView imgCallOrPut_T02_Op12) {
        this.imgCallOrPut_T02_Op12 = imgCallOrPut_T02_Op12;
    }

    public Label getLblQtdStakes_T02_Op12() {
        return lblQtdStakes_T02_Op12;
    }

    public void setLblQtdStakes_T02_Op12(Label lblQtdStakes_T02_Op12) {
        this.lblQtdStakes_T02_Op12 = lblQtdStakes_T02_Op12;
    }

    public Label getLblQtdWins_T02_Op12() {
        return lblQtdWins_T02_Op12;
    }

    public void setLblQtdWins_T02_Op12(Label lblQtdWins_T02_Op12) {
        this.lblQtdWins_T02_Op12 = lblQtdWins_T02_Op12;
    }

    public Label getLblQtdLoss_T02_Op12() {
        return lblQtdLoss_T02_Op12;
    }

    public void setLblQtdLoss_T02_Op12(Label lblQtdLoss_T02_Op12) {
        this.lblQtdLoss_T02_Op12 = lblQtdLoss_T02_Op12;
    }

    public Label getLblVlrIn_T02_Op12() {
        return lblVlrIn_T02_Op12;
    }

    public void setLblVlrIn_T02_Op12(Label lblVlrIn_T02_Op12) {
        this.lblVlrIn_T02_Op12 = lblVlrIn_T02_Op12;
    }

    public Label getLblVlrOut_T02_Op12() {
        return lblVlrOut_T02_Op12;
    }

    public void setLblVlrOut_T02_Op12(Label lblVlrOut_T02_Op12) {
        this.lblVlrOut_T02_Op12 = lblVlrOut_T02_Op12;
    }

    public Label getLblVlrDiff_T02_Op12() {
        return lblVlrDiff_T02_Op12;
    }

    public void setLblVlrDiff_T02_Op12(Label lblVlrDiff_T02_Op12) {
        this.lblVlrDiff_T02_Op12 = lblVlrDiff_T02_Op12;
    }

    public TableView getTbvTransacoes_T02_Op12() {
        return tbvTransacoes_T02_Op12;
    }

    public void setTbvTransacoes_T02_Op12(TableView tbvTransacoes_T02_Op12) {
        this.tbvTransacoes_T02_Op12 = tbvTransacoes_T02_Op12;
    }

    public static FilteredList<HistoricoDeCandles>[][] getHistoricoDeCandlesFilteredList() {
        return historicoDeCandlesFilteredList;
    }

    public static void setHistoricoDeCandlesFilteredList(FilteredList<HistoricoDeCandles>[][]
                                                                 historicoDeCandlesFilteredList) {
        Operacoes.historicoDeCandlesFilteredList = historicoDeCandlesFilteredList;
    }

    public static FilteredList<Transacoes>[] getTransacoesFilteredList_tFrame() {
        return transacoesFilteredList_tFrame;
    }

    public static void setTransacoesFilteredList_tFrame(FilteredList<Transacoes>[] transacoesFilteredList_tFrame) {
        Operacoes.transacoesFilteredList_tFrame = transacoesFilteredList_tFrame;
    }

    public static FilteredList<Transacoes>[][] getTransacoesFilteredList_symbol() {
        return transacoesFilteredList_symbol;
    }

    public static void setTransacoesFilteredList_symbol(FilteredList<Transacoes>[][] transacoesFilteredList_symbol) {
        Operacoes.transacoesFilteredList_symbol = transacoesFilteredList_symbol;
    }

}
