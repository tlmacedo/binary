package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.*;
import br.com.tlmacedo.binary.model.dao.SymbolDAO;
import br.com.tlmacedo.binary.model.dao.TokenDAO;
import br.com.tlmacedo.binary.model.dao.TransacoesDAO;
import br.com.tlmacedo.binary.model.dao.TransactionDAO;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.DURATION_UNIT;
import br.com.tlmacedo.binary.model.enums.ESTRATEGIAS;
import br.com.tlmacedo.binary.model.tableModel.TmodelTransacoes;
import br.com.tlmacedo.binary.model.vo.Error;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.ServiceAlertMensagem;
import br.com.tlmacedo.binary.services.ServiceMascara;
import br.com.tlmacedo.binary.services.UtilJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

public class Operacoes implements Initializable {

    /**
     * Objetos do formulario
     */

    // Detalhes e informações da conta
    public AnchorPane painelViewBinary;

    public TitledPane tpn_DetalhesConta;
    public ComboBox<Token> cboConta;
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
    public TextField txtDuracaoTicks;
    public ComboBox<DURATION_UNIT> cboDuracaoTipo;
    public TextField txtValorStake;
    public ComboBox<ESTRATEGIAS> cboEstrategia;
    public TextField txtQtdRepete;
    public TextField txtVlrStopGain;
    public TextField txtStopGainPorcentagem;
    public TextField txtVlrStopLoss;
    public TextField txtStopLossPorcentagem;
    public TextField txtQtdStopLoss;
    public Button btnContratos;
    public Button btnIniciar;
    public Button btnPausar;
    public Button btnStop;
    public ComboBox<Integer> cboQtdTicksGrafico;
    public Label lblHoraInicio;
    public Label lblHoraAtual;
    public Label lblTempoUso;


    // Volatilidade R10
    public TitledPane tpn_R10;
    public BarChart<String, Number> grafBar_R10;
    public NumberAxis yAxisBar_R10;
    public LineChart grafLine_R10;
    public NumberAxis yAxisLine_R10;
    public Label lblPares_R10;
    public Label lblParesPorc_R10;
    public Label lblImpares_R10;
    public Label lblImparesPorc_R10;
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
    public TableView<Transacoes> tbvTransacoes_R10;
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
    public LineChart grafLine_R25;
    public NumberAxis yAxisLine_R25;
    public Label lblPares_R25;
    public Label lblParesPorc_R25;
    public Label lblImpares_R25;
    public Label lblImparesPorc_R25;
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
    public TableView<Transacoes> tbvTransacoes_R25;
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
    public LineChart grafLine_R50;
    public NumberAxis yAxisLine_R50;
    public Label lblPares_R50;
    public Label lblParesPorc_R50;
    public Label lblImpares_R50;
    public Label lblImparesPorc_R50;
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
    public TableView<Transacoes> tbvTransacoes_R50;
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
    public LineChart grafLine_R75;
    public NumberAxis yAxisLine_R75;
    public Label lblPares_R75;
    public Label lblParesPorc_R75;
    public Label lblImpares_R75;
    public Label lblImparesPorc_R75;
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
    public TableView<Transacoes> tbvTransacoes_R75;
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
    public LineChart grafLine_R100;
    public NumberAxis yAxisLine_R100;
    public Label lblPares_R100;
    public Label lblParesPorc_R100;
    public Label lblImpares_R100;
    public Label lblImparesPorc_R100;
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
    public TableView<Transacoes> tbvTransacoes_R100;
    public CheckBox chkAtivo_R100;
    public Label tpnLblLegendaExecucoes_R100;
    public Label tpnLblExecucoes_R100;
    public Label tpnLblVitorias_R100;
    public Label tpnLblDerrotas_R100;
    public Label tpnLblLucro_R100;

    private static final ObservableList<Symbol> symbolObservableList = FXCollections.observableArrayList(new SymbolDAO().getAll(Symbol.class, "ativa=true", null));
    public static final Integer VOL_10 = 0;
    public static final Integer VOL_25 = 1;
    public static final Integer VOL_50 = 2;
    public static final Integer VOL_75 = 3;
    public static final Integer VOL_100 = 4;
    public static final String[] VOL_NAME = symbolObservableList.stream().map(Symbol::getName).collect(Collectors.toList()).toArray(String[]::new);


    private TokenDAO tokenDAO = new TokenDAO();
    private static TransactionDAO transactionDAO = new TransactionDAO();
    private static TransacoesDAO transacoesDAO = new TransacoesDAO();


    /**
     * Geral
     */

    private static ObjectProperty<Token> token = new SimpleObjectProperty<>();
    private static ObjectProperty<Estrategia> estrategia = new SimpleObjectProperty<>();
    private static final BooleanProperty transacoesAtutorizadas = new SimpleBooleanProperty(false);
    private static ObjectProperty<Error>[] error = new ObjectProperty[getSymbolObservableList().size() + 1];
    private static Timeline relogio;
    private static final LongProperty tempoCorrido = new SimpleLongProperty();
    private static final BooleanProperty contTempo = new SimpleBooleanProperty(false);
    private static final LongProperty horaInicial = new SimpleLongProperty();


    private static final BooleanProperty ws_Conectato = new SimpleBooleanProperty(false);
    private final ObjectProperty<WSClient> ws_Cliente = new SimpleObjectProperty<>(new WSClient());
    private static final IntegerProperty qtdTicksGrafico = new SimpleIntegerProperty(100);
    private static final IntegerProperty qtdTicksAnalisar = new SimpleIntegerProperty(1000);
    private final BooleanProperty gerarContratosDisponivel = new SimpleBooleanProperty(false);
    private static final ObjectProperty<Authorize> authorize = new SimpleObjectProperty<>();
    private static ObservableList<Transacoes> transacoesObservableList = FXCollections.observableArrayList();

    /**
     * Style Geral
     */

    public static final StringProperty styleTickSubindo =
            new SimpleStringProperty("-fx-background-color: #2AABE2; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;");
    public static final StringProperty styleTickDescendo
            = new SimpleStringProperty("-fx-background-color: #CD012F; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;");
    public static final StringProperty styleTickNegociando = new SimpleStringProperty("-fx-background-color: #fffd03; -fx-text-fill: #000000;");
    public static final StringProperty styleTickNegociandoFalse = new SimpleStringProperty(".lbl_informacao.right");//new SimpleStringProperty("-fx-font-size: 15px; -fx-background-color: #ff6600; -fx-background-radius: 0 8 8 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");

    public static final StringProperty styleLabelNExecucaoNegociando = new SimpleStringProperty("-fx-border-base: black; -fx-border-shadow: #323232; -fx-light-border: derive(-fx-border-base, 25%); -fx-border-color: -fx-light-border -fx-border-base -fx-border-base -fx-light-border; -fx-border-insets: 0 1 1 0; -fx-background-color: #fffd03, -fx-border-shadow, -fx-background; -fx-background-insets: 1 0 0 1, 2; -fx-background-radius: 0 0 4 4; -fx-border-radius: 0 0 4 4; -fx-padding: 2;");
    public static final StringProperty styleLabelNExecucaoNegociandoFalse = new SimpleStringProperty("-fx-border-base: black; -fx-border-shadow: #323232; -fx-light-border: derive(-fx-border-base, 25%); -fx-border-color: -fx-light-border -fx-border-base -fx-border-base -fx-light-border; -fx-border-insets: 0 1 1 0; -fx-background-color: -fx-border-shadow, -fx-background; -fx-background-insets: 1 0 0 1, 2; -fx-background-radius: 0 0 4 4; -fx-border-radius: 0 0 4 4; -fx-padding: 2;");
    public static final StringProperty styleTotalNExecucaoNegociando = new SimpleStringProperty("-fx-font-size: 10px; -fx-background-color: #fffd03; -fx-background-radius: 8 8 0 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");
    public static final StringProperty styleTotalNExecucaoNegociandoFalse = new SimpleStringProperty("-fx-font-size: 10px; -fx-background-color: #ff6600; -fx-background-radius: 8 8 0 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");


    /**
     * Cada Volatilidade
     */

    private DoubleProperty[] mediaAcima = new DoubleProperty[getSymbolObservableList().size()];
    private DoubleProperty[] mediaAbaixo = new DoubleProperty[getSymbolObservableList().size()];
    private BooleanProperty[] volatilidadeAtivada = new BooleanProperty[getSymbolObservableList().size()];
    private BooleanProperty[] compraAutorizada = new BooleanProperty[getSymbolObservableList().size()];
    private static BooleanProperty compraAutorizadaGeral = new SimpleBooleanProperty(false);
    private static BooleanProperty volatilidadeEmNegociacaoGeral = new SimpleBooleanProperty(false);
    private BooleanProperty[] volatilidadeEmNegociacao = new BooleanProperty[getSymbolObservableList().size()];

    private static ObjectProperty<PriceProposal>[] lastPriceProposal = new ObjectProperty[getSymbolObservableList().size()];
    private static ObservableList<Transaction>[] transactionObservableList = new ObservableList[getSymbolObservableList().size()];

    private static IntegerProperty[] qtdRepeticoes = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] qtdStopLoss = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] qtdDerrotas = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] qtdPares = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] qtdImpares = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] paresPorcentagem = new IntegerProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] imparesPorcentagem = new IntegerProperty[getSymbolObservableList().size()];
    private static BooleanProperty[] renovarTodosContratos = new BooleanProperty[getSymbolObservableList().size()];
    private static final ObjectProperty<BigDecimal> vlrSaldoInicial = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static final ObjectProperty<BigDecimal> vlrLucroAcumulado = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static final ObjectProperty<BigDecimal> vlrStopGain = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static final ObjectProperty<BigDecimal> stopGainPorcentagem = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static final ObjectProperty<BigDecimal> vlrStopLoss = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static final ObjectProperty<BigDecimal> stopLossPorcentagem = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private static ObjectProperty<BigDecimal>[] stakePadrao = new ObjectProperty[getSymbolObservableList().size()];
    private static ObjectProperty<BigDecimal>[] stakeContrato = new ObjectProperty[getSymbolObservableList().size()];
    private static ObjectProperty<BigDecimal>[] fatorMartingale = new ObjectProperty[getSymbolObservableList().size()];


    private static ObservableList<HistoricoDeTicks>[] historicoDeTicksGraficoObservableList = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList = new ObservableList[getSymbolObservableList().size()];
    private static Map<Integer, Long>[] listDigitosAnalise_R = new Map[getSymbolObservableList().size()];
    private static ObservableList<Integer>[] listMaiorQtdDigito = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<Integer>[] listMenorQtdDigito = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<Integer>[] listAnalise100MenorQtdDigito = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<Integer>[] listAnalise200MenorQtdDigito = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<Integer>[] listAnalise1000MenorQtdDigito = new ObservableList[getSymbolObservableList().size()];
    private static ObjectProperty<Tick>[] ultimoTick = new ObjectProperty[getSymbolObservableList().size()];
    private static IntegerProperty[] ultimoDigito = new IntegerProperty[getSymbolObservableList().size()];

    private static BooleanProperty[] tickSubindo = new BooleanProperty[getSymbolObservableList().size()];

    private TmodelTransacoes[] tmodelTransacoes = new TmodelTransacoes[getSymbolObservableList().size()];
    private FilteredList<Transacoes>[] transacoesFilteredList = new FilteredList[getSymbolObservableList().size()];

    /**
     * Graficos
     */
    private XYChart.Series<String, Number>[] grafBarVolatilidade_R = new XYChart.Series[getSymbolObservableList().size()];
    private ObservableList<Data<String, Number>>[] grafBarListDataDigitos_R = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<Long>[] grafBarListValorDigito_R = new ObservableList[getSymbolObservableList().size()];
    private Text[][] grafBarTxtDigito_R = new Text[getSymbolObservableList().size()][10];

    private XYChart.Series<String, Number>[] grafLineVolatilidade_R = new XYChart.Series[getSymbolObservableList().size()];
    private ObservableList<Data<String, Number>>[] grafLineListDataDigitos_R = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<HistoricoDeTicks>[] grafLineListValorDigito_R = new ObservableList[getSymbolObservableList().size()];

    private XYChart.Series<String, Number>[] grafLineMACD_R = new XYChart.Series[getSymbolObservableList().size()];
    private ObservableList<Data<String, Number>>[] grafLineListDataMACD_R = new ObservableList[getSymbolObservableList().size()];
    private static ObservableList<HistoricoDeTicks>[] grafLineListValorMACD_R = new ObservableList[getSymbolObservableList().size()];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceMascara.fieldMask(getTxtValorStake(), "#,##0.00");
        ServiceMascara.fieldMask(getTxtDuracaoTicks(), "##0.*1");
        ServiceMascara.fieldMask(getTxtQtdRepete(), "##0.");


        ServiceMascara.fieldMask(getTxtVlrStopGain(), "##,##0.00");
        ServiceMascara.fieldMask(getTxtStopGainPorcentagem(), "##,##0.00%");

        ServiceMascara.fieldMask(getTxtVlrStopLoss(), "##,##0.00");
        ServiceMascara.fieldMask(getTxtStopLossPorcentagem(), "##0.00%");

        getCboQtdTicksGrafico().getItems().setAll(100, 75, 50, 25, 10);
        getCboQtdTicksGrafico().getSelectionModel().select(0);
        qtdTicksGraficoProperty().bind(getCboQtdTicksGrafico().valueProperty());
        qtdTicksGraficoProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            try {
                solicitarTicksAnalise();

                getyAxisBar_R10().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
                getyAxisBar_R25().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
                getyAxisBar_R50().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
                getyAxisBar_R75().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
                getyAxisBar_R100().setUpperBound(25 + (4 * getCboQtdTicksGrafico().getSelectionModel().getSelectedIndex()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        getCboConta().setItems(getTokenDAO().getAll(Token.class, "valido=1", null)
                .stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboConta().getSelectionModel().select(-1);
        getCboConta().valueProperty().addListener((ov, o, n) -> {
            if (n == null || n == o) return;
            tokenProperty().setValue(n);
            novaOperação();
            autorizarAplicacao();
        });

        atualizaStopGainStopLoss_Padrao();

        authorizeProperty().addListener((ov, o, n) -> {
            if (n == null || n == o) return;
            getLblProprietarioConta().setText(
                    String.format("%s (%s)",
                            n.getFullname(),
                            n.getEmail()));
            if (n.getBalance() != null
                    && n.getBalance().compareTo(BigDecimal.ZERO) != 0)
                setVlrSaldoInicial(n.getBalance());

            getLblSaldoConta().setText(ServiceMascara.getValorMoeda(getVlrSaldoInicial()));

            stopGainPorcentagemProperty().setValue(new BigDecimal(5.).setScale(2));
            vlrStopGainProperty().setValue(getMetaVlrIdeal(stopGainPorcentagemProperty().getValue()));

            stopLossPorcentagemProperty().setValue(new BigDecimal(20.).setScale(2));
            vlrStopLossProperty().setValue(getMetaVlrIdeal(stopLossPorcentagemProperty().getValue()));

            getLblMoedaSaldo().setText(n.getCurrency());
            getLblIdConta().setText(n.getLoginid());
        });

        getCboDuracaoTipo().setItems(Arrays.stream(DURATION_UNIT.values())
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboDuracaoTipo().getSelectionModel().select(4);

        setRelogio(new Timeline(
                new KeyFrame(Duration.millis(1000), event -> getLblHoraAtual().setText(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("pt", "br")))))
        ));

        horaInicialProperty().addListener((ov, o, n) -> {
            if (n.intValue() > 0) {
                tempoCorridoProperty().setValue(0);
                setContTempo(true);
                getLblHoraInicio().setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(horaInicialProperty().getValue() / 1000),
                        TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            } else {
                setContTempo(false);
            }
        });

        getLblHoraAtual().textProperty().addListener((ov, o, n) -> {
            if (isContTempo()) {
                tempoCorridoProperty().setValue(System.currentTimeMillis() - horaInicialProperty().getValue());
            }
        });

        tempoCorridoProperty().addListener((ov, o, n) -> {
            if (n.intValue() <= 0) return;
            getLblTempoUso().setText(LocalDateTime.ofInstant(Instant.ofEpochSecond(n.longValue() / 1000),
                    TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("mm:ss")));
        });

        getLblTempoUso().setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !isContTempo()) {
                getLblHoraInicio().setText("");
                getLblTempoUso().setText("");
            }
        });

        getRelogio().setCycleCount(Animation.INDEFINITE);
        getRelogio().play();


//        getLblHoraInicio().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (isContTempo()) {
//                if (horaInicialProperty().getValue() <= 0) return "";
//                return LocalDateTime.ofInstant(Instant.ofEpochSecond(horaInicialProperty().getValue() / 1000),
//                        TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//            } else {
//                String retorno = getLblHoraInicio().getText();
//                if (retorno.equals(""))
//                    return "";
//                else
//                    return retorno;
//            }
//        }, horaInicialProperty()));
//
//        getLblTempoUso().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (isContTempo()) {
//                if (horaInicialProperty().getValue() <= 0) return "";
//                return LocalDateTime.ofInstant(Instant.ofEpochSecond((System.currentTimeMillis() - horaInicialProperty().getValue()) / 1000),
//                        TimeZone.getDefault().toZoneId()).format(DateTimeFormatter.ofPattern("mm:ss"));
//            } else {
//                return getLblTempoUso().getText();
//            }
//        }, getLblHoraAtual().textProperty()));

        getCboEstrategia().setItems(Arrays.stream(ESTRATEGIAS.values())
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboEstrategia().getSelectionModel().select(-1);

        getCboEstrategia().valueProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            switch (n) {
                case DIFF0 -> setEstrategia(new EstrategiaDiff0(this));
                case CALL_PUT -> setEstrategia(new CallPut_01_Reversed(this));
                case OVER -> {
                    int index = n.getDescricao().toLowerCase().indexOf("over*") + 5;
                    //setEstrategia(new SimpleObjectProperty());
                    setEstrategia(new EstrategiaOver(this, Integer.parseInt(n.getDescricao().substring(index, index + 1))));
                }
                case EVEN_ODD0 -> {
                    setEstrategia(new EstrategiaEvenOdd0(this));
                }
                case EVEN_ODD1 -> {
                    setEstrategia(new EstrategiaEvenOdd1(this));
                }
            }
        });


        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            getError()[symbolId] = new SimpleObjectProperty<>();
            getVolatilidadeAtivada()[symbolId] = new SimpleBooleanProperty(false);
            getCompraAutorizada()[symbolId] = new SimpleBooleanProperty(false);
            getVolatilidadeEmNegociacao()[symbolId] = new SimpleBooleanProperty(false);

            getMediaAcima()[symbolId] = new SimpleDoubleProperty();
            getMediaAbaixo()[symbolId] = new SimpleDoubleProperty();
            getQtdPares()[symbolId] = new SimpleIntegerProperty();
            getParesPorcentagem()[symbolId] = new SimpleIntegerProperty();
            getQtdImpares()[symbolId] = new SimpleIntegerProperty();
            getImparesPorcentagem()[symbolId] = new SimpleIntegerProperty();
            getRenovarTodosContratos()[symbolId] = new SimpleBooleanProperty(true);

            getStakePadrao()[symbolId] = new SimpleObjectProperty<>();
            getStakeContrato()[symbolId] = new SimpleObjectProperty<>();
            getQtdRepeticoes()[symbolId] = new SimpleIntegerProperty();
            getQtdStopLoss()[symbolId] = new SimpleIntegerProperty(0);
            getQtdDerrotas()[symbolId] = new SimpleIntegerProperty(0);
//                    getStakePadrao()[symbolId] = new SimpleObjectProperty<>(new BigDecimal(getTxtValorStake().getText()));
//                    getStakeContrato()[symbolId] = new SimpleObjectProperty<>(getStakePadrao()[symbolId].getValue());
//                    getFatorMartingale()[symbolId] = new SimpleObjectProperty<>();

            getUltimoTick()[symbolId] = new SimpleObjectProperty<>();
            getUltimoDigito()[symbolId] = new SimpleIntegerProperty();
            getTickSubindo()[symbolId] = new SimpleBooleanProperty(false);
            getHistoricoDeTicksGraficoObservableList()[symbolId] = FXCollections.observableArrayList();
            getHistoricoDeTicksAnaliseObservableList()[symbolId] = FXCollections.observableArrayList();
            getListDigitosAnalise_R()[symbolId] = new HashMap();
            for (int i = 0; i < 10; i++)
                getListDigitosAnalise_R()[symbolId].put(i, 0L);

            getListMaiorQtdDigito()[symbolId] = FXCollections.observableArrayList();
            getListMenorQtdDigito()[symbolId] = FXCollections.observableArrayList();
            getListAnalise100MenorQtdDigito()[symbolId] = FXCollections.observableArrayList();
            getListAnalise200MenorQtdDigito()[symbolId] = FXCollections.observableArrayList();
            getListAnalise1000MenorQtdDigito()[symbolId] = FXCollections.observableArrayList();
            getLastPriceProposal()[symbolId] = new SimpleObjectProperty<>();
            getTransactionObservableList()[symbolId] = FXCollections.observableArrayList();

            graficoBarras(symbolId);

            //graficoLinha(symbol);

            //graficoMACD(symbol);

            switch (symbolId) {
                case 0 -> {
                    getChkAtivo_R10().setSelected(true);
                    getVolatilidadeAtivada()[VOL_10].bind(getChkAtivo_R10().selectedProperty());

                    getLblTickUltimo_R10().textProperty().bind(getUltimoTick()[VOL_10].asString());

                    getLblPares_R10().textProperty().bind(getQtdPares()[VOL_10].asString());
                    getLblImpares_R10().textProperty().bind(getQtdImpares()[VOL_10].asString());

                    getLblParesPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getParesPorcentagem()[VOL_10].getValue()),
                            getParesPorcentagem()[VOL_10]));
                    getLblImparesPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getImparesPorcentagem()[VOL_10].getValue()),
                            getImparesPorcentagem()[VOL_10]));

                    getTickSubindo()[VOL_10].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblTickUltimo_R10().setStyle(getStyleTickSubindo());
                        else
                            getLblTickUltimo_R10().setStyle(getStyleTickDescendo());
                    });

                    getVolatilidadeEmNegociacao()[VOL_10].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n) {
                            if (getTpn_R10().isExpanded())
                                getLblLegendaTickUltimo_R10().setStyle(getStyleTickNegociando());
                            else
                                getTpnLblLegendaExecucoes_R10().setStyle(getStyleLabelNExecucaoNegociando());
                        } else {
                            if (getTpn_R10().isExpanded())
                                getLblLegendaTickUltimo_R10().setStyle(getStyleTickNegociandoFalse());
                            else
                                getTpnLblLegendaExecucoes_R10().setStyle(getStyleLabelNExecucaoNegociandoFalse());
                        }
                    });
                }
                case 1 -> {
                    getChkAtivo_R25().setSelected(true);
                    getVolatilidadeAtivada()[VOL_25].bind(getChkAtivo_R25().selectedProperty());

                    getLblTickUltimo_R25().textProperty().bind(getUltimoTick()[VOL_25].asString());

                    getLblPares_R25().textProperty().bind(getQtdPares()[VOL_25].asString());
                    getLblImpares_R25().textProperty().bind(getQtdImpares()[VOL_25].asString());

                    getLblParesPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getParesPorcentagem()[VOL_25].getValue()),
                            getParesPorcentagem()[VOL_25]));
                    getLblImparesPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getImparesPorcentagem()[VOL_25].getValue()),
                            getImparesPorcentagem()[VOL_25]));

                    getTickSubindo()[VOL_25].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblTickUltimo_R25().setStyle(getStyleTickSubindo());
                        else
                            getLblTickUltimo_R25().setStyle(getStyleTickDescendo());
                    });

                    getVolatilidadeEmNegociacao()[VOL_25].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n) {
                            if (getTpn_R25().isExpanded())
                                getLblLegendaTickUltimo_R25().setStyle(getStyleTickNegociando());
                            else
                                getTpnLblLegendaExecucoes_R25().setStyle(getStyleLabelNExecucaoNegociando());
                        } else {
                            if (getTpn_R25().isExpanded())
                                getLblLegendaTickUltimo_R25().setStyle(getStyleTickNegociandoFalse());
                            else
                                getTpnLblLegendaExecucoes_R25().setStyle(getStyleLabelNExecucaoNegociandoFalse());
                        }
                    });
                }
                case 2 -> {
                    getChkAtivo_R50().setSelected(true);
                    getVolatilidadeAtivada()[VOL_50].bind(getChkAtivo_R50().selectedProperty());

                    getLblTickUltimo_R50().textProperty().bind(getUltimoTick()[VOL_50].asString());

                    getLblPares_R50().textProperty().bind(getQtdPares()[VOL_50].asString());
                    getLblImpares_R50().textProperty().bind(getQtdImpares()[VOL_50].asString());

                    getLblParesPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getParesPorcentagem()[VOL_50].getValue()),
                            getParesPorcentagem()[VOL_50]));
                    getLblImparesPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getImparesPorcentagem()[VOL_50].getValue()),
                            getImparesPorcentagem()[VOL_50]));

                    getTickSubindo()[VOL_50].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblTickUltimo_R50().setStyle(getStyleTickSubindo());
                        else
                            getLblTickUltimo_R50().setStyle(getStyleTickDescendo());
                    });

                    getVolatilidadeEmNegociacao()[VOL_50].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n) {
                            if (getTpn_R50().isExpanded())
                                getLblLegendaTickUltimo_R50().setStyle(getStyleTickNegociando());
                            else
                                getTpnLblLegendaExecucoes_R50().setStyle(getStyleLabelNExecucaoNegociando());
                        } else {
                            if (getTpn_R50().isExpanded())
                                getLblLegendaTickUltimo_R50().setStyle(getStyleTickNegociandoFalse());
                            else
                                getTpnLblLegendaExecucoes_R50().setStyle(getStyleLabelNExecucaoNegociandoFalse());
                        }
                    });
                }
                case 3 -> {
                    getChkAtivo_R75().setSelected(true);
                    getVolatilidadeAtivada()[VOL_75].bind(getChkAtivo_R75().selectedProperty());

                    getLblTickUltimo_R75().textProperty().bind(getUltimoTick()[VOL_75].asString());

                    getLblPares_R75().textProperty().bind(getQtdPares()[VOL_75].asString());
                    getLblImpares_R75().textProperty().bind(getQtdImpares()[VOL_75].asString());

                    getLblParesPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getParesPorcentagem()[VOL_75].getValue()),
                            getParesPorcentagem()[VOL_75]));
                    getLblImparesPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getImparesPorcentagem()[VOL_75].getValue()),
                            getImparesPorcentagem()[VOL_75]));

                    getTickSubindo()[VOL_75].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblTickUltimo_R75().setStyle(getStyleTickSubindo());
                        else
                            getLblTickUltimo_R75().setStyle(getStyleTickDescendo());
                    });

                    getVolatilidadeEmNegociacao()[VOL_75].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n) {
                            if (getTpn_R75().isExpanded())
                                getLblLegendaTickUltimo_R75().setStyle(getStyleTickNegociando());
                            else
                                getTpnLblLegendaExecucoes_R75().setStyle(getStyleLabelNExecucaoNegociando());
                        } else {
                            if (getTpn_R75().isExpanded())
                                getLblLegendaTickUltimo_R75().setStyle(getStyleTickNegociandoFalse());
                            else
                                getTpnLblLegendaExecucoes_R75().setStyle(getStyleLabelNExecucaoNegociandoFalse());
                        }
                    });
                }
                case 4 -> {
                    getChkAtivo_R100().setSelected(true);
                    getVolatilidadeAtivada()[VOL_100].bind(getChkAtivo_R100().selectedProperty());

                    getLblTickUltimo_R100().textProperty().bind(getUltimoTick()[VOL_100].asString());

                    getLblPares_R100().textProperty().bind(getQtdPares()[VOL_100].asString());
                    getLblImpares_R100().textProperty().bind(getQtdImpares()[VOL_100].asString());

                    getLblParesPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getParesPorcentagem()[VOL_100].getValue()),
                            getParesPorcentagem()[VOL_100]));
                    getLblImparesPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                    String.format("%s%%", getImparesPorcentagem()[VOL_100].getValue()),
                            getImparesPorcentagem()[VOL_100]));

                    getTickSubindo()[VOL_100].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblTickUltimo_R100().setStyle(getStyleTickSubindo());
                        else
                            getLblTickUltimo_R100().setStyle(getStyleTickDescendo());
                    });

                    getVolatilidadeEmNegociacao()[VOL_100].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n) {
                            if (getTpn_R100().isExpanded())
                                getLblLegendaTickUltimo_R100().setStyle(getStyleTickNegociando());
                            else
                                getTpnLblLegendaExecucoes_R100().setStyle(getStyleLabelNExecucaoNegociando());
                        } else {
                            if (getTpn_R100().isExpanded())
                                getLblLegendaTickUltimo_R100().setStyle(getStyleTickNegociandoFalse());
                            else
                                getTpnLblLegendaExecucoes_R100().setStyle(getStyleLabelNExecucaoNegociandoFalse());
                        }
                    });
                }
            }
        }

        carregarObjetosForm();
        carregarOjetosFormPorVolatilidade();

        Task taskWsBinary = new Task() {
            @Override
            protected Object call() throws Exception {
                criarConexao();

                ws_ConectatoProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n) {
                            solicitarTicks();
                            carregarAnalises();
                            atualizarGrafico();
//                            solicitarTicksHistory();
//                            Thread.sleep(1000);

//                            solicitarTransacoes();
                            solicitarTicksAnalise();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                return null;
            }
        };

        Thread threadGraficos = new Thread(taskWsBinary);
        threadGraficos.setDaemon(true);
        threadGraficos.start();

    }

    private void carregarObjetosForm() {
        getBtnContratos().disableProperty().bind(gerarContratosDisponivelProperty().not());

        gerarContratosDisponivelProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean valido = false;
            if (getCboConta().getSelectionModel().getSelectedIndex() < 0
                    || Double.valueOf(getTxtValorStake().getText().replace(",", "")).isNaN()
                    || getCboEstrategia().getSelectionModel().getSelectedIndex() <= 0
            )
                return false;
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                if ((valido = getVolatilidadeAtivada()[symbolId].getValue()))
                    break;
            }
            return valido;
        }, getCboConta().valueProperty(), getTxtValorStake().textProperty(), getCboEstrategia().valueProperty()));

        gerarContratosDisponivelProperty().addListener((ov, o, n) -> {
            getBtnContratos_R10().setDisable(!(n && getVolatilidadeAtivada()[VOL_10].getValue()));
            getBtnContratos_R25().setDisable(!(n && getVolatilidadeAtivada()[VOL_25].getValue()));
            getBtnContratos_R50().setDisable(!(n && getVolatilidadeAtivada()[VOL_50].getValue()));
            getBtnContratos_R75().setDisable(!(n && getVolatilidadeAtivada()[VOL_75].getValue()));
            getBtnContratos_R100().setDisable(!(n && getVolatilidadeAtivada()[VOL_100].getValue()));
        });

        getBtnContratos().setOnAction(getActionEventContrato());

        //carregarBotoes(-1,null,null);

        getBtnIniciar().setOnAction(event -> {
            compraAutorizadaGeralProperty().setValue(true);
            if (getBtnPausar_R10().getText().toLowerCase().equals("iniciar"))
                getBtnPausar_R10().fire();
            if (getBtnPausar_R25().getText().toLowerCase().equals("iniciar"))
                getBtnPausar_R25().fire();
            if (getBtnPausar_R50().getText().toLowerCase().equals("iniciar"))
                getBtnPausar_R50().fire();
            if (getBtnPausar_R75().getText().toLowerCase().equals("iniciar"))
                getBtnPausar_R75().fire();
            if (getBtnPausar_R100().getText().toLowerCase().equals("iniciar"))
                getBtnPausar_R100().fire();
        });

        getBtnPausar().setOnAction(event -> {
            compraAutorizadaGeralProperty().setValue(false);
            if (getBtnPausar_R10().getText().toLowerCase().equals("pausar"))
                getBtnPausar_R10().fire();
            if (getBtnPausar_R25().getText().toLowerCase().equals("pausar"))
                getBtnPausar_R25().fire();
            if (getBtnPausar_R50().getText().toLowerCase().equals("pausar"))
                getBtnPausar_R50().fire();
            if (getBtnPausar_R75().getText().toLowerCase().equals("pausar"))
                getBtnPausar_R75().fire();
            if (getBtnPausar_R100().getText().toLowerCase().equals("pausar"))
                getBtnPausar_R100().fire();
        });

        getBtnStop().setOnAction(event -> {
            compraAutorizadaGeralProperty().setValue(false);
            getBtnIniciar().setDisable(true);
            getBtnPausar().setDisable(true);
            getBtnStop().setDisable(true);
            ativarControlesContaNegociacao(true);
            getBtnStop_R10().fire();
            getBtnStop_R25().fire();
            getBtnStop_R50().fire();
            getBtnStop_R75().fire();
            getBtnStop_R100().fire();
        });
    }

    public void carregarBotoes(Integer symbolId, EventHandler eventHandler, String tipo) {
        switch (tipo) {
            case "contrato" -> {
                if (VOL_10 == symbolId) getBtnContratos_R10().setOnAction(eventHandler);
                if (VOL_25 == symbolId) getBtnContratos_R25().setOnAction(eventHandler);
                if (VOL_50 == symbolId) getBtnContratos_R50().setOnAction(eventHandler);
                if (VOL_75 == symbolId) getBtnContratos_R75().setOnAction(eventHandler);
                if (VOL_100 == symbolId) getBtnContratos_R100().setOnAction(eventHandler);
            }
            case "compra" -> {
                if (VOL_10 == symbolId) getBtnComprar_R10().setOnAction(eventHandler);
                if (VOL_25 == symbolId) getBtnComprar_R25().setOnAction(eventHandler);
                if (VOL_50 == symbolId) getBtnComprar_R50().setOnAction(eventHandler);
                if (VOL_75 == symbolId) getBtnComprar_R75().setOnAction(eventHandler);
                if (VOL_100 == symbolId) getBtnComprar_R100().setOnAction(eventHandler);
            }
            case "stop" -> {
                if (VOL_10 == symbolId) getBtnStop_R10().setOnAction(eventHandler);
                if (VOL_25 == symbolId) getBtnStop_R25().setOnAction(eventHandler);
                if (VOL_50 == symbolId) getBtnStop_R50().setOnAction(eventHandler);
                if (VOL_75 == symbolId) getBtnStop_R75().setOnAction(eventHandler);
                if (VOL_100 == symbolId) getBtnStop_R100().setOnAction(eventHandler);
            }
        }
        switch (symbolId) {
            case 0 -> {
                getBtnPausar_R10().setOnAction(getActionEventIniciarPausarVolatilidade(VOL_10));
                getCompraAutorizada()[VOL_10].addListener((ov, o, n) -> getBtnPausar_R10().setText(n ? "Pausar" : "Iniciar"));
            }
            case 1 -> {
                getBtnPausar_R25().setOnAction(getActionEventIniciarPausarVolatilidade(VOL_25));
                getCompraAutorizada()[VOL_25].addListener((ov, o, n) -> getBtnPausar_R25().setText(n ? "Pausar" : "Iniciar"));
            }
            case 2 -> {
                getBtnPausar_R50().setOnAction(getActionEventIniciarPausarVolatilidade(VOL_50));
                getCompraAutorizada()[VOL_50].addListener((ov, o, n) -> getBtnPausar_R50().setText(n ? "Pausar" : "Iniciar"));
            }
            case 3 -> {
                getBtnPausar_R75().setOnAction(getActionEventIniciarPausarVolatilidade(VOL_75));
                getCompraAutorizada()[VOL_75].addListener((ov, o, n) -> getBtnPausar_R75().setText(n ? "Pausar" : "Iniciar"));
            }
            case 4 -> {
                getBtnPausar_R100().setOnAction(getActionEventIniciarPausarVolatilidade(VOL_100));
                getCompraAutorizada()[VOL_100].addListener((ov, o, n) -> getBtnPausar_R100().setText(n ? "Pausar" : "Iniciar"));
            }
        }
    }

    private void carregarOjetosFormPorVolatilidade() {
        for (int symbolId = 1; symbolId < getSymbolObservableList().size(); symbolId++) {
            vincularTabelasTransacoes(symbolId);
        }
    }

    private boolean criarConexao() {
        ws_ClienteProperty().getValue().connect();
        return isWs_Conectato();
    }

    private boolean solicitarTicks() throws Exception {
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            try {
                String jsonTickStream = UtilJson.getJson_From_Object(WSClient.getMapper(),
                        new TicksStream(VOL_NAME[symbolId]));
                ws_ClienteProperty().getValue().getMyWebSocket().send(jsonTickStream);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private boolean solicitarTicksHistory() {
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            try {
                String jsonTicksHistory = UtilJson.getJson_From_Object(WSClient.getMapper(),
                        new TicksHistory(VOL_NAME[symbolId], qtdTicksGraficoProperty().getValue()));
                ws_ClienteProperty().getValue().getMyWebSocket().send(jsonTicksHistory);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private boolean solicitarTicksAnalise() {
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            try {
                String jsonTickAnaliseHistory = UtilJson.getJson_From_Object(WSClient.getMapper(),
                        new TicksHistory(VOL_NAME[symbolId], 1000));
                ws_ClienteProperty().getValue().getMyWebSocket().send(jsonTickAnaliseHistory);
            } catch (JsonProcessingException ex) {
                ex.printStackTrace();
            }
        }
        return true;
    }

    private boolean logOut() {
        ws_ClienteProperty().getValue().getMyWebSocket().send("{\"logout\": 1}");
        return true;
    }

    private boolean autorizarAplicacao() {
        Token token;
        if ((token = getCboConta().getSelectionModel().getSelectedItem()) == null) return false;
        String jsonAutorizacao = String.format("{\"authorize\":\"%s\"}", token.tokenProperty().getValue());
        ws_ClienteProperty().getValue().getMyWebSocket().send(jsonAutorizacao);
        return true;
    }

    private boolean solicitarTransacoes() {
        try {
            String jsonTransacoes = UtilJson.getJson_From_Object(WSClient.getMapper(), new TransactionsStream(1));
            ws_ClienteProperty().getValue().getMyWebSocket().send(jsonTransacoes);
            monitorarTransacoes();
            transacoesAtutorizadasProperty().setValue(true);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean enviarContrato(Integer symbolId, String jsonPriceProposal) {
        if (!getVolatilidadeAtivada()[symbolId].getValue()) return false;
        ws_ClienteProperty().getValue().getMyWebSocket().send(jsonPriceProposal);
        if (!transacoesAtutorizadasProperty().getValue())
            solicitarTransacoes();
        ativarControlesContaNegociacao(false);
        return true;
    }

    public boolean comprarContrato(Integer symbolId, Proposal proposal) {
        if (!getVolatilidadeAtivada()[symbolId].getValue()
                || !getCompraAutorizada()[symbolId].getValue()) return false;

        try {
            if (!limitePraCompra(symbolId)) return false;
            getVolatilidadeEmNegociacao()[symbolId].setValue(true);
            String jsonBuyContrato = UtilJson.getJson_From_Object(WSClient.getMapper(), new BuyContract(proposal.getId()));
            ws_ClienteProperty().getValue().getMyWebSocket().send(jsonBuyContrato);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    public void atualizaStakesNegociacao(Integer symbolId, boolean repeteMesmaStakeVitoria) {
        if (!getCompraAutorizada()[symbolId].getValue()) return;
        BigDecimal lucroTemp = getLucroPerdaUltimaTransacao(symbolId);
        if (lucroTemp.compareTo(BigDecimal.ZERO) > 0) {
            getRenovarTodosContratos()[symbolId].setValue(getQtdDerrotas()[symbolId].getValue().compareTo(0) > 0);
            getQtdDerrotas()[symbolId].setValue(0);
            if (!repeteMesmaStakeVitoria) {
                getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
            }
        } else {
            getRenovarTodosContratos()[symbolId].setValue(true);
            getQtdDerrotas()[symbolId].setValue(getQtdDerrotas()[symbolId].getValue() + 1);
            getStakeContrato()[symbolId].setValue(
                    getStakeContrato()[symbolId].getValue()
                            .add(getStakeContrato()[symbolId].getValue()
                                    .multiply(getFatorMartingale()[symbolId].getValue()))
                            .setScale(2, RoundingMode.HALF_UP));
        }
    }

    public boolean limitePraCompra(Integer symbolId) {
        if (vlrLucroAcumuladoProperty().getValue().compareTo(vlrStopGainProperty().getValue()) >= 0
                || vlrLucroAcumuladoProperty().getValue().add(getStakeContrato()[symbolId].getValue().negate())
                .compareTo(vlrStopLossProperty().getValue().negate()) < 0
                || getQtdDerrotas()[symbolId].getValue().compareTo(getQtdStopLoss()[symbolId].getValue()) >= 0) {
            BigDecimal novaStake = getStakeContrato()[symbolId].getValue().negate();
//            if (volatilidadeEmNegociacaoGeralProperty().getValue()) {
            getBtnStop().fire();
//            } else {
//                if (symbolId == VOL_10) getBtnStop_R10();
//                if (symbolId == VOL_25) getBtnStop_R25();
//                if (symbolId == VOL_50) getBtnStop_R50();
//                if (symbolId == VOL_75) getBtnStop_R75();
//                if (symbolId == VOL_100) getBtnStop_R100();
//            }
            ServiceAlertMensagem alertMensagem = new ServiceAlertMensagem();
            if (vlrLucroAcumuladoProperty().getValue().compareTo(vlrStopGainProperty().getValue()) >= 0) {
                alertMensagem.setCabecalho(String.format("Meta batida! Alcançou:R$%s.",
                        vlrLucroAcumuladoProperty().getValue().setScale(2, RoundingMode.HALF_UP)));
                alertMensagem.setContentText(String.format("sua meta foi batida com sucesso!!!" +
                                "\nParabéns o valor da meta de R$%s foi alcançado o valor de R$%s, no tempo total de %s",
                        vlrStopGainProperty().getValue().setScale(2, RoundingMode.HALF_UP),
                        vlrLucroAcumuladoProperty().getValue().setScale(2, RoundingMode.HALF_UP),
                        getLblTempoUso().getText()));
            } else if (vlrLucroAcumuladoProperty().getValue().add(novaStake)
                    .compareTo(vlrStopLossProperty().getValue().negate()) < 0) {
                alertMensagem.setCabecalho(String.format("Límite de perda máximo! perdeu R$%s.",
                        vlrLucroAcumuladoProperty().getValue().setScale(2, RoundingMode.HALF_UP)));
                alertMensagem.setContentText(String.format("o límite máximo para perda é R$%s, e você perdeu R$%s" +
                                " no tempo total de %s.\nVá dar uma volta e retorne amanhã para um novo dia!",
                        vlrStopLossProperty().getValue().setScale(2, RoundingMode.HALF_UP),
                        vlrLucroAcumuladoProperty().getValue().setScale(2, RoundingMode.HALF_UP),
                        getLblTempoUso().getText()));
            } else if (getQtdDerrotas()[symbolId].getValue().compareTo(getQtdStopLoss()[symbolId].getValue()) >= 0) {
                alertMensagem.setCabecalho("Límite de derrotas seguidas!");
                alertMensagem.setContentText(String.format("seu límite de [%s] derrotas seguidas foi atingida" +
                                "no tempo total de %s...\nVá dar uma volta e retorne amanhã para um novo dia!",
                        getTxtQtdStopLoss().getText(), getLblTempoUso().getText()));
            }
            alertMensagem.alertOk();
            return false;
        }
        return true;
    }


    private boolean carregarAnalises() throws Exception {
        monitorarVariaveis();
        return true;
    }

    private void graficoBarras(Integer symbolId) {
        getGrafBarListDataDigitos_R()[symbolId] = FXCollections.observableArrayList();
        getGrafBarListValorDigito_R()[symbolId] = FXCollections.observableArrayList();

        for (int i = 0; i < 10; i++) {
            getGrafBarTxtDigito_R()[symbolId][i] = new Text("");
            getGrafBarTxtDigito_R()[symbolId][i].setFont(Font.font("Arial", 10));
            getGrafBarTxtDigito_R()[symbolId][i].setStyle("-fx-text-fill: white;");
            getGrafBarListValorDigito_R()[symbolId].add(i, 0L);
            getGrafBarListDataDigitos_R()[symbolId].add(i, new Data<>(String.valueOf(i), 0));
        }

        getGrafBarVolatilidade_R()[symbolId] = new XYChart.Series<>();

        switch (symbolId) {
            case 0 -> {
                getyAxisBar_R10().setUpperBound(25);
                getGrafBar_R10().getData().add(getGrafBarVolatilidade_R()[VOL_10]);
                getGrafBar_R10().setVisible(true);
            }
            case 1 -> {
                getyAxisBar_R25().setUpperBound(25);
                getGrafBar_R25().getData().add(getGrafBarVolatilidade_R()[VOL_25]);
                getGrafBar_R25().setVisible(true);
            }
            case 2 -> {
                getyAxisBar_R50().setUpperBound(25);
                getGrafBar_R50().getData().add(getGrafBarVolatilidade_R()[VOL_50]);
                getGrafBar_R50().setVisible(true);
            }
            case 3 -> {
                getyAxisBar_R75().setUpperBound(25);
                getGrafBar_R75().getData().add(getGrafBarVolatilidade_R()[VOL_75]);
                getGrafBar_R75().setVisible(true);
            }
            case 4 -> {
                getyAxisBar_R100().setUpperBound(25);
                getGrafBar_R100().getData().add(getGrafBarVolatilidade_R()[VOL_100]);
                getGrafBar_R100().setVisible(true);
            }
        }
    }

    private void graficoLinha(Integer symbolId) {
        getGrafLineListDataDigitos_R()[symbolId] = FXCollections.observableArrayList();
        getGrafLineListValorDigito_R()[symbolId] = FXCollections.observableArrayList();

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
                    getGrafLineListDataDigitos_R()[symbolId].add(
                            new Data<>(hora, tick.getPrice().doubleValue()));
                }
            }
        });

        getGrafLineListDataDigitos_R()[symbolId].addListener((ListChangeListener<? super Data<String, Number>>) c -> {
            while (c.next()) {
                for (Data data : c.getRemoved())
                    getGrafLineVolatilidade_R()[symbolId].getData().remove(0);

                for (Data data : c.getAddedSubList())
                    getGrafLineVolatilidade_R()[symbolId].getData().add(data);

            }
        });
        getGrafLineVolatilidade_R()[symbolId] = new XYChart.Series<>();

        switch (symbolId) {
            case 0 -> {
                getGrafLine_R10().setVisible(true);
                getGrafLine_R10().getData().add(getGrafLineVolatilidade_R()[VOL_10]);
            }
            case 1 -> {
                getGrafLine_R25().setVisible(true);
                getGrafLine_R25().getData().add(getGrafLineVolatilidade_R()[VOL_25]);
            }
            case 2 -> {
                getGrafLine_R50().setVisible(true);
                getGrafLine_R50().getData().add(getGrafLineVolatilidade_R()[VOL_50]);
            }
            case 3 -> {
                getGrafLine_R75().setVisible(true);
                getGrafLine_R75().getData().add(getGrafLineVolatilidade_R()[VOL_75]);
            }
            case 4 -> {
                getGrafLine_R100().setVisible(true);
                getGrafLine_R100().getData().add(getGrafLineVolatilidade_R()[VOL_100]);
            }
        }
    }

    private void graficoMACD(Integer symbolId) {
        getGrafLineListDataMACD_R()[symbolId] = FXCollections.observableArrayList();
        getGrafLineListValorMACD_R()[symbolId] = FXCollections.observableArrayList();

        getGrafLineListValorMACD_R()[symbolId].addListener((ListChangeListener<? super HistoricoDeTicks>) c -> {
            while (c.next()) {
                for (HistoricoDeTicks tick : c.getRemoved()) {
                    getGrafLineListDataMACD_R()[symbolId].remove(0);
                }
                for (HistoricoDeTicks tick : c.getAddedSubList()) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss");
                    String hora = dtf.format(LocalDateTime.ofInstant(Instant.ofEpochSecond(tick.getTime()),
                            TimeZone.getDefault().toZoneId())
                            .toLocalTime());
                    Double MME12 = getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .limit(12).mapToDouble(value -> value.getPrice().doubleValue()).average().getAsDouble();
                    Double MME26 = getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .limit(26).mapToDouble(value -> value.priceProperty().getValue().doubleValue()).average().getAsDouble();
                    Double MACD = (MME26 - MME12);
                    if (symbolId == 0)
                        System.out.printf("MME26:%s\tMME12:%s\t\tMACD:%s\n",
                                MME26, MME12, MACD);
                    getGrafLineListDataMACD_R()[symbolId].add(
                            new Data<>(hora, MACD)
                    );
                }
            }
        });

        getGrafLineListDataMACD_R()[symbolId].addListener((ListChangeListener<? super Data<String, Number>>) c -> {
            while (c.next()) {
                for (Data data : c.getRemoved())
                    getGrafLineMACD_R()[symbolId].getData().remove(0);
                for (Data data : c.getAddedSubList())
                    getGrafLineMACD_R()[symbolId].getData().add(data);
            }
        });
        getGrafLineMACD_R()[symbolId] = new XYChart.Series<>();
        switch (symbolId) {
            case 0 -> {
                getGrafLine_R10().setVisible(true);
                getGrafLine_R10().getData().add(getGrafLineMACD_R()[VOL_10]);
            }
            case 1 -> {
                getGrafLine_R25().setVisible(true);
                getGrafLine_R25().getData().add(getGrafLineMACD_R()[VOL_25]);
            }
            case 2 -> {
                getGrafLine_R50().setVisible(true);
                getGrafLine_R50().getData().add(getGrafLineMACD_R()[VOL_50]);
            }
            case 3 -> {
                getGrafLine_R75().setVisible(true);
                getGrafLine_R75().getData().add(getGrafLineMACD_R()[VOL_75]);
            }
            case 4 -> {
                getGrafLine_R100().setVisible(true);
                getGrafLine_R100().getData().add(getGrafLineMACD_R()[VOL_100]);
            }
        }
    }

    private boolean atualizarGrafico() {
        Platform.runLater(() -> {
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                for (int j = 0; j < 10; j++) {
                    getGrafBarVolatilidade_R()[symbolId].getData()
                            .add(getGrafBarListDataDigitos_R()[symbolId].get(j));
                    getGrafBarVolatilidade_R()[symbolId].setName(String.valueOf(j));
                    displayLabelForData(
                            getGrafBarListDataDigitos_R()[symbolId].get(j),
                            getGrafBarTxtDigito_R()[symbolId][j]);
                }
            }
        });
        return true;
    }

    private void monitorarVariaveis() {
        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            int symbolId = i;
            getUltimoTick()[symbolId].addListener((ov, o, n) -> {
                Platform.runLater(() -> {
                    getUltimoDigito()[symbolId].setValue(n.getUltimoDigito());
                    Map<Integer, Long> valores = getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .map(HistoricoDeTicks::getUltimoDigito)
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

                    for (int j = 0; j < 10; j++) {
                        getGrafBarListValorDigito_R()[symbolId].set(j, valores.containsKey(j) ? valores.get(j) : 0L);
                        Double porcentagem = 0.;
                        if (getGrafBarListValorDigito_R()[symbolId].get(j) != 0)
                            porcentagem = getGrafBarListValorDigito_R()[symbolId].get(j).doubleValue()
                                    / (qtdTicksGraficoProperty().getValue().doubleValue() / 100.);
                        getGrafBarListDataDigitos_R()[symbolId].get(j).setYValue(porcentagem.intValue());
                        getGrafBarTxtDigito_R()[symbolId][j].setText(String.format("%s%%", porcentagem.intValue()));
                    }
//                        if (getGrafLineListValorDigito_R()[symbolId] != null)
//                            getGrafLineListValorDigito_R()[symbolId].remove(0);
//                        if (getGrafLineListValorMACD_R()[symbolId] != null)
//                            getGrafLineListValorMACD_R()[symbolId].remove(0);
                    if (getHistoricoDeTicksGraficoObservableList()[symbolId].size() > 1)
                        getTickSubindo()[symbolId]
                                .setValue(getHistoricoDeTicksGraficoObservableList()[symbolId].get(0).getPrice()
                                        .compareTo(getHistoricoDeTicksGraficoObservableList()[symbolId].get(1).getPrice()) > 0);

                    Transacoes transacoesTemp;
                    if (getTransacoesObservableList().size() > 0
                            && (transacoesTemp = getTransacoesObservableList().stream()
                            .filter(transacoes -> transacoes.symbolProperty().getValue().toString().equals(VOL_NAME[symbolId])
                                    && (transacoes.tickCompraProperty().getValue() == null
                                    || transacoes.tickVendaProperty().getValue() == null))
                            .findFirst().orElse(null)) != null) {
                        int index = getTransacoesObservableList().indexOf(transacoesTemp);
                        try {
                            if (transacoesTemp.tickCompraProperty().getValue() == null) {
                                transacoesTemp.tickCompraProperty().setValue(
                                        getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                                                .filter(historicoDeTicks -> historicoDeTicks.getTime()
                                                        > transacoesTemp.dataHoraCompraProperty().getValue())
                                                .findFirst().get().getPrice());
                            } else {//if (transacoesTemp.tickVendaProperty().getValue()==null){
                                if (transacoesTemp.getContract_type().toLowerCase().contains("call")
                                        || transacoesTemp.getContract_type().toLowerCase().contains("put")) {
                                    transacoesTemp.tickVendaProperty().setValue(
                                            getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                                                    .filter(historicoDeTicks -> historicoDeTicks.getTime()
                                                            > transacoesTemp.dataHoraExpiryProperty().getValue())
                                                    .sorted(Comparator.comparingInt(HistoricoDeTicks::getTime))
                                                    .findFirst().get().getPrice());
                                } else {
                                    transacoesTemp.tickVendaProperty().setValue(
                                            getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                                                    .filter(historicoDeTicks -> historicoDeTicks.getTime()
                                                            >= transacoesTemp.dataHoraExpiryProperty().getValue())
                                                    .sorted(Comparator.comparingInt(HistoricoDeTicks::getTime))
                                                    .findFirst().get().getPrice());
                                }
                            }
                        } catch (Exception ex) {
                            if (!(ex instanceof NullPointerException) && !(ex instanceof NoSuchElementException))
                                ex.printStackTrace();
                        } finally {
                            getTransacoesObservableList().set(index, transacoesTemp);
                            index = getTransacoesObservableList().indexOf(transacoesTemp);
                        }
                    }

                    Platform.runLater(() -> atualizaCoresGrafico(symbolId));

                    Double margem = (100. / qtdTicksGraficoProperty().getValue());

                    getQtdPares()[symbolId].setValue(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .map(HistoricoDeTicks::getUltimoDigito)
                            .filter(integer -> integer % 2 == 0)
                            .count());

                    getParesPorcentagem()[symbolId].setValue(new BigDecimal(margem * getQtdPares()[symbolId].getValue()));

                    getQtdImpares()[symbolId].setValue(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                            .mapToInt(HistoricoDeTicks::getUltimoDigito)
                            .filter(integer -> integer % 2 != 0)
                            .count());

                    getImparesPorcentagem()[symbolId].setValue(new BigDecimal(margem * getQtdImpares()[symbolId].getValue()));

                    int min = getGrafBarListDataDigitos_R()[symbolId].stream().map(Data::getYValue)
                            .min(Comparator.comparing(Number::intValue)).get().intValue();
                    int max = getGrafBarListDataDigitos_R()[symbolId].stream().map(Data::getYValue)
                            .max(Comparator.comparing(Number::intValue)).get().intValue();

                    int finalMin = min;
                    getListMenorQtdDigito()[symbolId].setAll(
                            getGrafBarListDataDigitos_R()[symbolId].stream()
                                    .filter(stringNumberData -> stringNumberData.getYValue().intValue() == finalMin)
                                    .map(stringNumberData -> Integer.valueOf(stringNumberData.getXValue()))
                                    .collect(Collectors.toList())
                    );
                    getListMaiorQtdDigito()[symbolId].setAll(
                            getGrafBarListDataDigitos_R()[symbolId].stream()
                                    .filter(stringNumberData -> stringNumberData.getYValue().intValue() == max)
                                    .map(stringNumberData -> Integer.valueOf(stringNumberData.getXValue()))
                                    .collect(Collectors.toList())
                    );

                    getMediaAbaixo()[symbolId].setValue(getGrafBarListDataDigitos_R()[symbolId].stream()
                            .filter(stringNumberData -> Integer.valueOf(stringNumberData.getXValue()) <= 2)
                            .mapToDouble(value -> value.getYValue().doubleValue())
                            .average().orElse(0.));
                    getMediaAcima()[symbolId].setValue(getGrafBarListDataDigitos_R()[symbolId].stream()
                            .filter(stringNumberData -> Integer.valueOf(stringNumberData.getXValue()) > 2)
                            .mapToDouble(value -> value.getYValue().doubleValue())
                            .average().orElse(0.));
                });
            });
        }
    }

    private void vincularTabelasTransacoes(Integer symbolId) {
        getTmodelTransacoes()[symbolId] = new TmodelTransacoes(symbolId);
        getTmodelTransacoes()[symbolId].criarTabela();

        getTransacoesFilteredList()[symbolId] = new FilteredList<>(getTransacoesObservableList());
        getTmodelTransacoes()[symbolId].setTransacoesFilteredList(getTransacoesFilteredList()[symbolId]);
        switch (symbolId) {
            case 0 -> {
                getTmodelTransacoes()[VOL_10].setTbvTransacoes(getTbvTransacoes_R10());

                getTpnLblExecucoes_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].qtdNExecucaoProperty().asString());
                getTpnLblVitorias_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].qtdNVitoriaProperty().asString());
                getTpnLblDerrotas_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].qtdNDerrotaProperty().asString());

                getLblInvestido_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].totalInvestidoProperty().asString());
                getLblInvestidoPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_10]
                                        .totalInvestidoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_10].totalInvestidoProperty()));

                getLblPremiacao_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_10]
                                        .totalPremiacaoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_10].totalPremiacaoProperty()));

                getTpnLblLucro_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].totalLucroProperty().asString());
                getLblLucro_R10().textProperty()
                        .bind(getTmodelTransacoes()[VOL_10].totalLucroProperty().asString());
                getLblLucroPorc_R10().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_10]
                                        .totalLucroProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_10].totalLucroProperty()));
            }
            case 1 -> {
                getTmodelTransacoes()[VOL_25].setTbvTransacoes(getTbvTransacoes_R25());

                getTpnLblExecucoes_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].qtdNExecucaoProperty().asString());
                getTpnLblVitorias_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].qtdNVitoriaProperty().asString());
                getTpnLblDerrotas_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].qtdNDerrotaProperty().asString());

                getLblInvestido_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].totalInvestidoProperty().asString());
                getLblInvestidoPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_25]
                                        .totalInvestidoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_25].totalInvestidoProperty()));

                getLblPremiacao_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_25]
                                        .totalPremiacaoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_25].totalPremiacaoProperty()));

                getTpnLblLucro_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].totalLucroProperty().asString());
                getLblLucro_R25().textProperty()
                        .bind(getTmodelTransacoes()[VOL_25].totalLucroProperty().asString());
                getLblLucroPorc_R25().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_25]
                                        .totalLucroProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_25].totalLucroProperty()));
            }
            case 2 -> {
                getTmodelTransacoes()[VOL_50].setTbvTransacoes(getTbvTransacoes_R50());

                getTpnLblExecucoes_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].qtdNExecucaoProperty().asString());
                getTpnLblVitorias_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].qtdNVitoriaProperty().asString());
                getTpnLblDerrotas_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].qtdNDerrotaProperty().asString());

                getLblInvestido_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].totalInvestidoProperty().asString());
                getLblInvestidoPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_50]
                                        .totalInvestidoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_50].totalInvestidoProperty()));

                getLblPremiacao_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_50]
                                        .totalPremiacaoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_50].totalPremiacaoProperty()));

                getTpnLblLucro_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].totalLucroProperty().asString());
                getLblLucro_R50().textProperty()
                        .bind(getTmodelTransacoes()[VOL_50].totalLucroProperty().asString());
                getLblLucroPorc_R50().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_50]
                                        .totalLucroProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_50].totalLucroProperty()));
            }
            case 3 -> {
                getTmodelTransacoes()[VOL_75].setTbvTransacoes(getTbvTransacoes_R75());

                getTpnLblExecucoes_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].qtdNExecucaoProperty().asString());
                getTpnLblVitorias_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].qtdNVitoriaProperty().asString());
                getTpnLblDerrotas_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].qtdNDerrotaProperty().asString());

                getLblInvestido_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].totalInvestidoProperty().asString());
                getLblInvestidoPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_75]
                                        .totalInvestidoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_75].totalInvestidoProperty()));

                getLblPremiacao_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_75]
                                        .totalPremiacaoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_75].totalPremiacaoProperty()));

                getTpnLblLucro_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].totalLucroProperty().asString());
                getLblLucro_R75().textProperty()
                        .bind(getTmodelTransacoes()[VOL_75].totalLucroProperty().asString());
                getLblLucroPorc_R75().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_75]
                                        .totalLucroProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_75].totalLucroProperty()));
            }
            case 4 -> {
                getTmodelTransacoes()[VOL_100].setTbvTransacoes(getTbvTransacoes_R100());

                getTpnLblExecucoes_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].qtdNExecucaoProperty().asString());
                getTpnLblVitorias_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].qtdNVitoriaProperty().asString());
                getTpnLblDerrotas_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].qtdNDerrotaProperty().asString());

                getLblInvestido_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].totalInvestidoProperty().asString());
                getLblInvestidoPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_100]
                                        .totalInvestidoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_100].totalInvestidoProperty()));

                getLblPremiacao_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].totalPremiacaoProperty().asString());
                getLblPremiacaoPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_100]
                                        .totalPremiacaoProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_100].totalPremiacaoProperty()));

                getTpnLblLucro_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].totalLucroProperty().asString());
                getLblLucro_R100().textProperty()
                        .bind(getTmodelTransacoes()[VOL_100].totalLucroProperty().asString());
                getLblLucroPorc_R100().textProperty().bind(Bindings.createStringBinding(() ->
                                ServiceMascara.getValorMoeda(getMetaPorcIdeal(getTmodelTransacoes()[VOL_100]
                                        .totalLucroProperty().getValue())) + "%",
                        getTmodelTransacoes()[VOL_100].totalLucroProperty()));
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
            BigDecimal lucroTotTemp = c.getList().stream().filter(transacoes -> transacoes.isConsolidado())
                    .map(transacoes -> transacoes.stakeVendaProperty().getValue().add(transacoes.stakeCompraProperty().getValue()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
            vlrLucroAcumuladoProperty().setValue(lucroTotTemp);
            getLblTotalLucro().setText(lucroTotTemp.toString());
            getLblTotalLucroPorc().setText(getMetaPorcResultado().toString() + "%");
        });

        getTmodelTransacoes()[symbolId].setTransacoesObservableList(getTransacoesObservableList());
        getTmodelTransacoes()[symbolId].escutarTransacoesTabela();
        getTmodelTransacoes()[symbolId].tabela_preencher();
    }

    private void monitorarTransacoes() {
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            int finalSymbolId = symbolId;
//            Platform.runLater(() -> {
            getTransactionObservableList()[finalSymbolId].addListener((ListChangeListener<? super Transaction>) c -> {
                while (c.next()) {
                    for (Transaction transaction : c.getAddedSubList()) {
                        try {
                            ACTION action = ACTION.valueOf(transaction.getAction().toUpperCase());
                            if (action != null) {
                                Platform.runLater(() -> {
//                                    getTransactionDAO().merger(transaction);
                                    getLblSaldoFinal()
                                            .setText(ServiceMascara.getValorMoeda(transaction.getBalance().setScale(2, RoundingMode.HALF_UP)));
                                    Transacoes transacao = null;
                                    switch (action) {
                                        case BUY -> {
                                            transacao = new Transacoes(transaction);
                                            getTransacoesObservableList().add(0, transacao);
                                        }
                                        case SELL -> {
                                            transacao = new Transacoes(transaction);
                                            getVolatilidadeEmNegociacao()[finalSymbolId].setValue(false);
                                        }
                                    }
//                                    if (transacao != null)
//                                        getTransacoesDAO().merger(transacao);
                                });
                            }
                        } catch (Exception ex) {
                            if (!(ex instanceof NullPointerException) && !(ex instanceof IllegalStateException))
                                ex.printStackTrace();
                            getVolatilidadeEmNegociacao()[finalSymbolId].setValue(false);
                        }
                    }
                }
            });
//            });
        }
    }

    public boolean minimaVolatilidadeAtiva() {
        final boolean[] valido = {false};
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            valido[0] = getVolatilidadeAtivada()[symbolId].getValue();
        }
        return valido[0];
    }

    public EventHandler getActionEventIniciarPausarVolatilidade(Integer symbolId) {
        if (!getVolatilidadeAtivada()[symbolId].getValue()) return null;
        EventHandler<ActionEvent> actionEventIniciarPausarVolatilidade = event -> {
            switch (symbolId) {
                case 0 -> getCompraAutorizada()[VOL_10].setValue(!getCompraAutorizada()[VOL_10].getValue());
                case 1 -> getCompraAutorizada()[VOL_25].setValue(!getCompraAutorizada()[VOL_25].getValue());
                case 2 -> getCompraAutorizada()[VOL_50].setValue(!getCompraAutorizada()[VOL_50].getValue());
                case 3 -> getCompraAutorizada()[VOL_75].setValue(!getCompraAutorizada()[VOL_75].getValue());
                case 4 -> getCompraAutorizada()[VOL_100].setValue(!getCompraAutorizada()[VOL_100].getValue());
            }

        };
        return actionEventIniciarPausarVolatilidade;
    }

    public void ativarControlesContaNegociacao(boolean ativar) {
        if (!ativar) {
            if (horaInicialProperty().getValue() <= 0)
                horaInicialProperty().setValue(System.currentTimeMillis());
        } else {
            horaInicialProperty().setValue(0);
        }

        getCboConta().setDisable(!ativar);
        getTxtDuracaoTicks().setDisable(!ativar);
        getCboDuracaoTipo().setDisable(!ativar);
        getTxtValorStake().setDisable(!ativar);
        getCboEstrategia().setDisable(!ativar);
        getTxtQtdRepete().setDisable(!ativar);
        if (!ativar) {
            getLblSaldoInicial().setText(getLblSaldoConta().getText());
        } else {
            if (getLblSaldoFinal().getText() != "0.00")
                getLblSaldoConta().setText(getLblSaldoFinal().getText());
        }
    }

    public void ativarBotoesVolatilidade(Integer symbolId, boolean ativar) {
        if (ativar) {
            getBtnIniciar().setDisable(!ativar);
            getBtnPausar().setDisable(!ativar);
            getBtnStop().setDisable(!ativar);
        }
        switch (symbolId) {
            case 0 -> {
                getBtnComprar_R10().setDisable(!ativar);
                getBtnPausar_R10().setDisable(!ativar);
                getBtnStop_R10().setDisable(!ativar);
            }
            case 1 -> {
                getBtnComprar_R25().setDisable(!ativar);
                getBtnPausar_R25().setDisable(!ativar);
                getBtnStop_R25().setDisable(!ativar);
            }
            case 2 -> {
                getBtnComprar_R50().setDisable(!ativar);
                getBtnPausar_R50().setDisable(!ativar);
                getBtnStop_R50().setDisable(!ativar);
            }
            case 3 -> {
                getBtnComprar_R75().setDisable(!ativar);
                getBtnPausar_R75().setDisable(!ativar);
                getBtnStop_R75().setDisable(!ativar);
            }
            case 4 -> {
                getBtnComprar_R100().setDisable(!ativar);
                getBtnPausar_R100().setDisable(!ativar);
                getBtnStop_R100().setDisable(!ativar);
            }
        }
    }

    public void novaOperação() {
        //setEstrategia(null);
        vlrSaldoInicialProperty().setValue(BigDecimal.ZERO);
        vlrLucroAcumuladoProperty().setValue(BigDecimal.ZERO);
        getTransacoesObservableList().clear();
        for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
            getQtdDerrotas()[symbolId].setValue(0);
            getStakePadrao()[symbolId].setValue(null);
            getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
        }
        horaInicialProperty().setValue(0);
        tempoCorridoProperty().setValue(0);
        getLblHoraInicio().setText("");
        getLblTempoUso().setText("");
//        vlrStopGainProperty().setValue(BigDecimal.ZERO);
//        getLblSaldoInicial().setText("0.00");
//        getLblSaldoFinal().setText("0.00");
        stopGainPorcentagemProperty().setValue(new BigDecimal(5.));
        vlrStopGainProperty().setValue(getMetaVlrIdeal(stopGainPorcentagemProperty().getValue()));

        stopLossPorcentagemProperty().setValue(new BigDecimal(20.));
        vlrStopLossProperty().setValue(getMetaVlrIdeal(stopLossPorcentagemProperty().getValue()));
    }

    private void atualizaStopGainStopLoss_Padrao() {
        getTxtVlrStopGain().textProperty().addListener((ov1, o1, n1) -> {
            if (!getTxtVlrStopGain().isFocused()) return;
            vlrStopGainProperty().setValue(new BigDecimal(n1.replace(",", "")).setScale(2));
            stopGainPorcentagemProperty().setValue(getMetaPorcIdeal(n1));
        });
        getTxtStopGainPorcentagem().textProperty().addListener((ov1, o1, n1) -> {
            if (!getTxtStopGainPorcentagem().isFocused()) return;
            stopGainPorcentagemProperty().setValue(new BigDecimal(n1.replaceAll("[,%]", "")).setScale(2));
            vlrStopGainProperty().setValue(getMetaVlrIdeal(n1));
        });
        vlrStopGainProperty().addListener((ov1, o1, n1) -> {
            if (n1 == null || getTxtVlrStopGain().isFocused()) return;
            getTxtVlrStopGain().setText(n1.toString());
        });
        stopGainPorcentagemProperty().addListener((ov1, o1, n1) -> {
            if (n1 == null || getTxtStopGainPorcentagem().isFocused()) return;
            getTxtStopGainPorcentagem().setText(n1.toString());
        });

        getTxtVlrStopLoss().textProperty().addListener((ov1, o1, n1) -> {
            if (!getTxtVlrStopLoss().isFocused()) return;
            vlrStopLossProperty().setValue(new BigDecimal(n1.replace(",", "")).setScale(2));
            stopLossPorcentagemProperty().setValue(getMetaPorcIdeal(n1));
        });
        getTxtStopLossPorcentagem().textProperty().addListener((ov1, o1, n1) -> {
            if (!getTxtStopLossPorcentagem().isFocused()) return;
            stopLossPorcentagemProperty().setValue(new BigDecimal(n1.replaceAll("[,%]", "")).setScale(2));
            vlrStopLossProperty().setValue(getMetaVlrIdeal(n1));
        });
        vlrStopLossProperty().addListener((ov1, o1, n1) -> {
            if (n1 == null || getTxtVlrStopLoss().isFocused()) return;
            getTxtVlrStopLoss().setText(n1.toString());
        });
        stopLossPorcentagemProperty().addListener((ov1, o1, n1) -> {
            if (n1 == null || getTxtStopLossPorcentagem().isFocused()) return;
            getTxtStopLossPorcentagem().setText(n1.toString());
        });

        stopGainPorcentagemProperty().setValue(new BigDecimal(5.));
        vlrStopGainProperty().setValue(getMetaVlrIdeal(stopGainPorcentagemProperty().getValue().toString()));

        stopLossPorcentagemProperty().setValue(new BigDecimal(20.));
        vlrStopLossProperty().setValue(getMetaVlrIdeal(stopLossPorcentagemProperty().getValue().toString()));
    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public EventHandler getActionEventContrato() {
        EventHandler<ActionEvent> actionEventContrato = event -> {
            for (int symbolId = 0; symbolId < getSymbolObservableList().size(); symbolId++) {
                getStakePadrao()[symbolId].setValue(new BigDecimal(getTxtValorStake().getText()));
                getStakeContrato()[symbolId].setValue(getStakePadrao()[symbolId].getValue());
                getQtdRepeticoes()[symbolId].setValue(Integer.valueOf(getTxtQtdRepete().getText().replace("\\D", "")));
                getQtdStopLoss()[symbolId].setValue(Integer.valueOf(getTxtQtdStopLoss().getText().replace("\\D", "")));
            }
            getBtnIniciar().setDisable(false);
            getBtnPausar().setDisable(false);
            getBtnStop().setDisable(false);
            if (getVolatilidadeAtivada()[VOL_10].getValue())
                getBtnContratos_R10().fire();
            if (getVolatilidadeAtivada()[VOL_25].getValue())
                getBtnContratos_R25().fire();
            if (getVolatilidadeAtivada()[VOL_50].getValue())
                getBtnContratos_R50().fire();
            if (getVolatilidadeAtivada()[VOL_75].getValue())
                getBtnContratos_R75().fire();
            if (getVolatilidadeAtivada()[VOL_100].getValue())
                getBtnContratos_R100().fire();
        };
        return actionEventContrato;
    }

    private BigDecimal getMetaPorcIdeal(String strVlr) {
        BigDecimal vlr = BigDecimal.ZERO;
        try {
            vlr = new BigDecimal(strVlr.replace(",", "")).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            if (!(ex instanceof NumberFormatException))
                ex.printStackTrace();
        }
        return getMetaPorcIdeal(vlr);
    }

    private BigDecimal getMetaPorcIdeal(BigDecimal vlr) {
        if (vlr.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return vlr.divide(vlrSaldoInicialProperty().getValue(), 5, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getMetaVlrIdeal(String strPorcLucro) {
        BigDecimal porc = BigDecimal.ZERO;
        try {
            porc = new BigDecimal(strPorcLucro.replace("%", "")).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception ex) {
            if (!(ex instanceof NumberFormatException))
                ex.printStackTrace();
        }
        return getMetaVlrIdeal(porc);
    }

    private BigDecimal getMetaVlrIdeal(BigDecimal porc) {
        if (porc.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        return (porc.multiply(vlrSaldoInicialProperty().getValue()))
                .divide(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getMetaPorcResultado() {
        if (vlrLucroAcumuladoProperty().getValue().compareTo(BigDecimal.ZERO) > 0) {
            return (vlrLucroAcumuladoProperty().getValue()
                    .divide(vlrStopGainProperty().getValue(), 5, RoundingMode.HALF_UP))
                    .multiply(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);
        } else if (vlrLucroAcumuladoProperty().getValue().compareTo(BigDecimal.ZERO) < 0) {
            return (vlrLucroAcumuladoProperty().getValue()
                    .divide(vlrStopLossProperty().getValue(), 5, RoundingMode.HALF_UP))
                    .multiply(new BigDecimal(100.)).setScale(2, RoundingMode.HALF_UP);
        } else {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
    }


    public static Integer getSymbolId(String symbol) {
        for (int i = 0; i < Operacoes.getSymbolObservableList().size(); i++)
            if (symbol.equals(Operacoes.VOL_NAME[i]))
                return i;
        return null;
    }

    public BigDecimal getLucroPerdaUltimaTransacao(Integer symbolId) {
        return Operacoes.getTransacoesObservableList().stream()
                .filter(transacoes -> transacoes.getSymbol().idProperty().intValue() == symbolId)
                .sorted(Comparator.comparing(Transacoes::getDataHoraCompra).reversed())
                .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                .findFirst().get();
    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    //EventHandler<ActionEvent> actionEventContratoVolatilidade


    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void atualizaCoresGrafico(Integer symbolId) {
        Platform.runLater(() -> {
            Integer menorQtd = getGrafBarListValorDigito_R()[symbolId].stream()
                    .sorted().findFirst().get().intValue();
            Integer maiorQtd = getGrafBarListValorDigito_R()[symbolId].stream()
                    .sorted().collect(Collectors.toList()).get(9).intValue();

            getGrafBarListDataDigitos_R()[symbolId].stream()
                    .forEach(stringNumberData -> {
                        try {
                            if (stringNumberData.getYValue().intValue() >= (maiorQtd / (qtdTicksGraficoProperty().getValue() / 100.))) {
                                stringNumberData.getNode().setStyle("-fx-bar-fill: #147e35; -fx-border-color: #1f1e1e;");
                            } else if (stringNumberData.getYValue().intValue() <= (menorQtd / (qtdTicksGraficoProperty().getValue() / 100.))) {
                                stringNumberData.getNode().setStyle("-fx-bar-fill: #cd060f; -fx-border-color: #1f1e1e;");
                            } else {
                                stringNumberData.getNode().setStyle("-fx-bar-fill: #dcedfa; -fx-border-color: #1f1e1e;");
                            }
                        } catch (Exception ex) {
                            if (!(ex instanceof NullPointerException))
                                ex.printStackTrace();
                        }
                    });
        });
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

//    public void atualizarBotoes(EventHandler<ActionEvent> eventEventHandler){
//        ((Operacoes)this).getBtnContratos().setOnAction(eventEventHandler);
////        (getBtnContratos()).setOnAction(eventEventHandler);
////        getBtnContratos_R10().setOnAction(eventEventHandler);
//    }

    /**
     * @return
     */


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

    public ComboBox<Token> getCboConta() {
        return cboConta;
    }

    public void setCboConta(ComboBox<Token> cboConta) {
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

    public TextField getTxtDuracaoTicks() {
        return txtDuracaoTicks;
    }

    public void setTxtDuracaoTicks(TextField txtDuracaoTicks) {
        this.txtDuracaoTicks = txtDuracaoTicks;
    }

    public ComboBox<DURATION_UNIT> getCboDuracaoTipo() {
        return cboDuracaoTipo;
    }

    public void setCboDuracaoTipo(ComboBox<DURATION_UNIT> cboDuracaoTipo) {
        this.cboDuracaoTipo = cboDuracaoTipo;
    }

    public TextField getTxtValorStake() {
        return txtValorStake;
    }

    public void setTxtValorStake(TextField txtValorStake) {
        this.txtValorStake = txtValorStake;
    }

    public ComboBox<ESTRATEGIAS> getCboEstrategia() {
        return cboEstrategia;
    }

    public void setCboEstrategia(ComboBox<ESTRATEGIAS> cboEstrategia) {
        this.cboEstrategia = cboEstrategia;
    }

    public TextField getTxtQtdRepete() {
        return txtQtdRepete;
    }

    public void setTxtQtdRepete(TextField txtQtdRepete) {
        this.txtQtdRepete = txtQtdRepete;
    }

    public TextField getTxtVlrStopGain() {
        return txtVlrStopGain;
    }

    public void setTxtVlrStopGain(TextField txtVlrStopGain) {
        this.txtVlrStopGain = txtVlrStopGain;
    }

    public TextField getTxtStopGainPorcentagem() {
        return txtStopGainPorcentagem;
    }

    public void setTxtStopGainPorcentagem(TextField txtStopGainPorcentagem) {
        this.txtStopGainPorcentagem = txtStopGainPorcentagem;
    }

    public TextField getTxtVlrStopLoss() {
        return txtVlrStopLoss;
    }

    public void setTxtVlrStopLoss(TextField txtVlrStopLoss) {
        this.txtVlrStopLoss = txtVlrStopLoss;
    }

    public TextField getTxtStopLossPorcentagem() {
        return txtStopLossPorcentagem;
    }

    public void setTxtStopLossPorcentagem(TextField txtStopLossPorcentagem) {
        this.txtStopLossPorcentagem = txtStopLossPorcentagem;
    }

    public TextField getTxtQtdStopLoss() {
        return txtQtdStopLoss;
    }

    public void setTxtQtdStopLoss(TextField txtQtdStopLoss) {
        this.txtQtdStopLoss = txtQtdStopLoss;
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

    public Label getLblHoraInicio() {
        return lblHoraInicio;
    }

    public void setLblHoraInicio(Label lblHoraInicio) {
        this.lblHoraInicio = lblHoraInicio;
    }

    public Label getLblHoraAtual() {
        return lblHoraAtual;
    }

    public void setLblHoraAtual(Label lblHoraAtual) {
        this.lblHoraAtual = lblHoraAtual;
    }

    public Label getLblTempoUso() {
        return lblTempoUso;
    }

    public void setLblTempoUso(Label lblTempoUso) {
        this.lblTempoUso = lblTempoUso;
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

    public Label getLblPares_R10() {
        return lblPares_R10;
    }

    public void setLblPares_R10(Label lblPares_R10) {
        this.lblPares_R10 = lblPares_R10;
    }

    public Label getLblParesPorc_R10() {
        return lblParesPorc_R10;
    }

    public void setLblParesPorc_R10(Label lblParesPorc_R10) {
        this.lblParesPorc_R10 = lblParesPorc_R10;
    }

    public Label getLblImpares_R10() {
        return lblImpares_R10;
    }

    public void setLblImpares_R10(Label lblImpares_R10) {
        this.lblImpares_R10 = lblImpares_R10;
    }

    public Label getLblImparesPorc_R10() {
        return lblImparesPorc_R10;
    }

    public void setLblImparesPorc_R10(Label lblImparesPorc_R10) {
        this.lblImparesPorc_R10 = lblImparesPorc_R10;
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

    public TableView<Transacoes> getTbvTransacoes_R10() {
        return tbvTransacoes_R10;
    }

    public void setTbvTransacoes_R10(TableView<Transacoes> tbvTransacoes_R10) {
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

    public Label getLblPares_R25() {
        return lblPares_R25;
    }

    public void setLblPares_R25(Label lblPares_R25) {
        this.lblPares_R25 = lblPares_R25;
    }

    public Label getLblParesPorc_R25() {
        return lblParesPorc_R25;
    }

    public void setLblParesPorc_R25(Label lblParesPorc_R25) {
        this.lblParesPorc_R25 = lblParesPorc_R25;
    }

    public Label getLblImpares_R25() {
        return lblImpares_R25;
    }

    public void setLblImpares_R25(Label lblImpares_R25) {
        this.lblImpares_R25 = lblImpares_R25;
    }

    public Label getLblImparesPorc_R25() {
        return lblImparesPorc_R25;
    }

    public void setLblImparesPorc_R25(Label lblImparesPorc_R25) {
        this.lblImparesPorc_R25 = lblImparesPorc_R25;
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

    public TableView<Transacoes> getTbvTransacoes_R25() {
        return tbvTransacoes_R25;
    }

    public void setTbvTransacoes_R25(TableView<Transacoes> tbvTransacoes_R25) {
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

    public Label getLblPares_R50() {
        return lblPares_R50;
    }

    public void setLblPares_R50(Label lblPares_R50) {
        this.lblPares_R50 = lblPares_R50;
    }

    public Label getLblParesPorc_R50() {
        return lblParesPorc_R50;
    }

    public void setLblParesPorc_R50(Label lblParesPorc_R50) {
        this.lblParesPorc_R50 = lblParesPorc_R50;
    }

    public Label getLblImpares_R50() {
        return lblImpares_R50;
    }

    public void setLblImpares_R50(Label lblImpares_R50) {
        this.lblImpares_R50 = lblImpares_R50;
    }

    public Label getLblImparesPorc_R50() {
        return lblImparesPorc_R50;
    }

    public void setLblImparesPorc_R50(Label lblImparesPorc_R50) {
        this.lblImparesPorc_R50 = lblImparesPorc_R50;
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

    public TableView<Transacoes> getTbvTransacoes_R50() {
        return tbvTransacoes_R50;
    }

    public void setTbvTransacoes_R50(TableView<Transacoes> tbvTransacoes_R50) {
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

    public Label getLblPares_R75() {
        return lblPares_R75;
    }

    public void setLblPares_R75(Label lblPares_R75) {
        this.lblPares_R75 = lblPares_R75;
    }

    public Label getLblParesPorc_R75() {
        return lblParesPorc_R75;
    }

    public void setLblParesPorc_R75(Label lblParesPorc_R75) {
        this.lblParesPorc_R75 = lblParesPorc_R75;
    }

    public Label getLblImpares_R75() {
        return lblImpares_R75;
    }

    public void setLblImpares_R75(Label lblImpares_R75) {
        this.lblImpares_R75 = lblImpares_R75;
    }

    public Label getLblImparesPorc_R75() {
        return lblImparesPorc_R75;
    }

    public void setLblImparesPorc_R75(Label lblImparesPorc_R75) {
        this.lblImparesPorc_R75 = lblImparesPorc_R75;
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

    public TableView<Transacoes> getTbvTransacoes_R75() {
        return tbvTransacoes_R75;
    }

    public void setTbvTransacoes_R75(TableView<Transacoes> tbvTransacoes_R75) {
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

    public Label getLblPares_R100() {
        return lblPares_R100;
    }

    public void setLblPares_R100(Label lblPares_R100) {
        this.lblPares_R100 = lblPares_R100;
    }

    public Label getLblParesPorc_R100() {
        return lblParesPorc_R100;
    }

    public void setLblParesPorc_R100(Label lblParesPorc_R100) {
        this.lblParesPorc_R100 = lblParesPorc_R100;
    }

    public Label getLblImpares_R100() {
        return lblImpares_R100;
    }

    public void setLblImpares_R100(Label lblImpares_R100) {
        this.lblImpares_R100 = lblImpares_R100;
    }

    public Label getLblImparesPorc_R100() {
        return lblImparesPorc_R100;
    }

    public void setLblImparesPorc_R100(Label lblImparesPorc_R100) {
        this.lblImparesPorc_R100 = lblImparesPorc_R100;
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

    public TableView<Transacoes> getTbvTransacoes_R100() {
        return tbvTransacoes_R100;
    }

    public void setTbvTransacoes_R100(TableView<Transacoes> tbvTransacoes_R100) {
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

    public static ObservableList<Symbol> getSymbolObservableList() {
        return symbolObservableList;
    }

    public TokenDAO getTokenDAO() {
        return tokenDAO;
    }

    public void setTokenDAO(TokenDAO tokenDAO) {
        this.tokenDAO = tokenDAO;
    }

    public static Timeline getRelogio() {
        return relogio;
    }

    public static void setRelogio(Timeline relogio) {
        Operacoes.relogio = relogio;
    }

    public static boolean isWs_Conectato() {
        return ws_Conectato.get();
    }

    public static BooleanProperty ws_ConectatoProperty() {
        return ws_Conectato;
    }

    public static void setWs_Conectato(boolean ws_Conectato) {
        Operacoes.ws_Conectato.set(ws_Conectato);
    }

    public WSClient getWs_Cliente() {
        return ws_Cliente.get();
    }

    public ObjectProperty<WSClient> ws_ClienteProperty() {
        return ws_Cliente;
    }

    public void setWs_Cliente(WSClient ws_Cliente) {
        this.ws_Cliente.set(ws_Cliente);
    }

    public static int getQtdTicksGrafico() {
        return qtdTicksGrafico.get();
    }

    public static IntegerProperty qtdTicksGraficoProperty() {
        return qtdTicksGrafico;
    }

    public static void setQtdTicksGrafico(int qtdTicksGrafico) {
        Operacoes.qtdTicksGrafico.set(qtdTicksGrafico);
    }

    public static int getQtdTicksAnalisar() {
        return qtdTicksAnalisar.get();
    }

    public static IntegerProperty qtdTicksAnalisarProperty() {
        return qtdTicksAnalisar;
    }

    public static void setQtdTicksAnalisar(int qtdTicksAnalisar) {
        Operacoes.qtdTicksAnalisar.set(qtdTicksAnalisar);
    }

    public boolean isGerarContratosDisponivel() {
        return gerarContratosDisponivel.get();
    }

    public BooleanProperty gerarContratosDisponivelProperty() {
        return gerarContratosDisponivel;
    }

    public void setGerarContratosDisponivel(boolean gerarContratosDisponivel) {
        this.gerarContratosDisponivel.set(gerarContratosDisponivel);
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

    public static ObservableList<Transacoes> getTransacoesObservableList() {
        return transacoesObservableList;
    }

    public static void setTransacoesObservableList(ObservableList<Transacoes> transacoesObservableList) {
        Operacoes.transacoesObservableList = transacoesObservableList;
    }

    public static String getStyleTickSubindo() {
        return styleTickSubindo.get();
    }

    public static StringProperty styleTickSubindoProperty() {
        return styleTickSubindo;
    }

    public static void setStyleTickSubindo(String styleTickSubindo) {
        Operacoes.styleTickSubindo.set(styleTickSubindo);
    }

    public static String getStyleTickDescendo() {
        return styleTickDescendo.get();
    }

    public static StringProperty styleTickDescendoProperty() {
        return styleTickDescendo;
    }

    public static void setStyleTickDescendo(String styleTickDescendo) {
        Operacoes.styleTickDescendo.set(styleTickDescendo);
    }

    public static String getStyleTickNegociando() {
        return styleTickNegociando.get();
    }

    public static StringProperty styleTickNegociandoProperty() {
        return styleTickNegociando;
    }

    public static void setStyleTickNegociando(String styleTickNegociando) {
        Operacoes.styleTickNegociando.set(styleTickNegociando);
    }

    public static String getStyleTickNegociandoFalse() {
        return styleTickNegociandoFalse.get();
    }

    public static StringProperty styleTickNegociandoFalseProperty() {
        return styleTickNegociandoFalse;
    }

    public static void setStyleTickNegociandoFalse(String styleTickNegociandoFalse) {
        Operacoes.styleTickNegociandoFalse.set(styleTickNegociandoFalse);
    }

    public static String getStyleLabelNExecucaoNegociando() {
        return styleLabelNExecucaoNegociando.get();
    }

    public static StringProperty styleLabelNExecucaoNegociandoProperty() {
        return styleLabelNExecucaoNegociando;
    }

    public static void setStyleLabelNExecucaoNegociando(String styleLabelNExecucaoNegociando) {
        Operacoes.styleLabelNExecucaoNegociando.set(styleLabelNExecucaoNegociando);
    }

    public static String getStyleLabelNExecucaoNegociandoFalse() {
        return styleLabelNExecucaoNegociandoFalse.get();
    }

    public static StringProperty styleLabelNExecucaoNegociandoFalseProperty() {
        return styleLabelNExecucaoNegociandoFalse;
    }

    public static void setStyleLabelNExecucaoNegociandoFalse(String styleLabelNExecucaoNegociandoFalse) {
        Operacoes.styleLabelNExecucaoNegociandoFalse.set(styleLabelNExecucaoNegociandoFalse);
    }

    public static String getStyleTotalNExecucaoNegociando() {
        return styleTotalNExecucaoNegociando.get();
    }

    public static StringProperty styleTotalNExecucaoNegociandoProperty() {
        return styleTotalNExecucaoNegociando;
    }

    public static void setStyleTotalNExecucaoNegociando(String styleTotalNExecucaoNegociando) {
        Operacoes.styleTotalNExecucaoNegociando.set(styleTotalNExecucaoNegociando);
    }

    public static String getStyleTotalNExecucaoNegociandoFalse() {
        return styleTotalNExecucaoNegociandoFalse.get();
    }

    public static StringProperty styleTotalNExecucaoNegociandoFalseProperty() {
        return styleTotalNExecucaoNegociandoFalse;
    }

    public static void setStyleTotalNExecucaoNegociandoFalse(String styleTotalNExecucaoNegociandoFalse) {
        Operacoes.styleTotalNExecucaoNegociandoFalse.set(styleTotalNExecucaoNegociandoFalse);
    }

    public BooleanProperty[] getVolatilidadeAtivada() {
        return volatilidadeAtivada;
    }

    public void setVolatilidadeAtivada(BooleanProperty[] volatilidadeAtivada) {
        this.volatilidadeAtivada = volatilidadeAtivada;
    }

    public BooleanProperty[] getCompraAutorizada() {
        return compraAutorizada;
    }

    public void setCompraAutorizada(BooleanProperty[] compraAutorizada) {
        this.compraAutorizada = compraAutorizada;
    }

    public static boolean isCompraAutorizadaGeral() {
        return compraAutorizadaGeral.get();
    }

    public static BooleanProperty compraAutorizadaGeralProperty() {
        return compraAutorizadaGeral;
    }

    public static void setCompraAutorizadaGeral(boolean compraAutorizadaGeral) {
        Operacoes.compraAutorizadaGeral.set(compraAutorizadaGeral);
    }

    public static boolean isVolatilidadeEmNegociacaoGeral() {
        return volatilidadeEmNegociacaoGeral.get();
    }

    public static BooleanProperty volatilidadeEmNegociacaoGeralProperty() {
        return volatilidadeEmNegociacaoGeral;
    }

    public static void setVolatilidadeEmNegociacaoGeral(boolean volatilidadeEmNegociacaoGeral) {
        Operacoes.volatilidadeEmNegociacaoGeral.set(volatilidadeEmNegociacaoGeral);
    }

    public BooleanProperty[] getVolatilidadeEmNegociacao() {
        return volatilidadeEmNegociacao;
    }

    public void setVolatilidadeEmNegociacao(BooleanProperty[] volatilidadeEmNegociacao) {
        this.volatilidadeEmNegociacao = volatilidadeEmNegociacao;
    }

    public static ObjectProperty<PriceProposal>[] getLastPriceProposal() {
        return lastPriceProposal;
    }

    public static void setLastPriceProposal(ObjectProperty<PriceProposal>[] lastPriceProposal) {
        Operacoes.lastPriceProposal = lastPriceProposal;
    }

    public static ObservableList<Transaction>[] getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction>[] transactionObservableList) {
        Operacoes.transactionObservableList = transactionObservableList;
    }

    public static IntegerProperty[] getQtdRepeticoes() {
        return qtdRepeticoes;
    }

    public static void setQtdRepeticoes(IntegerProperty[] qtdRepeticoes) {
        Operacoes.qtdRepeticoes = qtdRepeticoes;
    }

    public static IntegerProperty[] getQtdStopLoss() {
        return qtdStopLoss;
    }

    public static void setQtdStopLoss(IntegerProperty[] qtdStopLoss) {
        Operacoes.qtdStopLoss = qtdStopLoss;
    }

    public static BigDecimal getVlrSaldoInicial() {
        return vlrSaldoInicial.get();
    }

    public static ObjectProperty<BigDecimal> vlrSaldoInicialProperty() {
        return vlrSaldoInicial;
    }

    public static void setVlrSaldoInicial(BigDecimal vlrSaldoInicial) {
        Operacoes.vlrSaldoInicial.set(vlrSaldoInicial);
    }

    public static BigDecimal getVlrLucroAcumulado() {
        return vlrLucroAcumulado.get();
    }

    public static ObjectProperty<BigDecimal> vlrLucroAcumuladoProperty() {
        return vlrLucroAcumulado;
    }

    public static void setVlrLucroAcumulado(BigDecimal vlrLucroAcumulado) {
        Operacoes.vlrLucroAcumulado.set(vlrLucroAcumulado);
    }

    public static BigDecimal getVlrStopGain() {
        return vlrStopGain.get();
    }

    public static ObjectProperty<BigDecimal> vlrStopGainProperty() {
        return vlrStopGain;
    }

    public static void setVlrStopGain(BigDecimal vlrStopGain) {
        Operacoes.vlrStopGain.set(vlrStopGain);
    }

    public static BigDecimal getStopGainPorcentagem() {
        return stopGainPorcentagem.get();
    }

    public static ObjectProperty<BigDecimal> stopGainPorcentagemProperty() {
        return stopGainPorcentagem;
    }

    public static void setStopGainPorcentagem(BigDecimal stopGainPorcentagem) {
        Operacoes.stopGainPorcentagem.set(stopGainPorcentagem);
    }

    public static BigDecimal getVlrStopLoss() {
        return vlrStopLoss.get();
    }

    public static ObjectProperty<BigDecimal> vlrStopLossProperty() {
        return vlrStopLoss;
    }

    public static void setVlrStopLoss(BigDecimal vlrStopLoss) {
        Operacoes.vlrStopLoss.set(vlrStopLoss);
    }

    public static BigDecimal getStopLossPorcentagem() {
        return stopLossPorcentagem.get();
    }

    public static ObjectProperty<BigDecimal> stopLossPorcentagemProperty() {
        return stopLossPorcentagem;
    }

    public static void setStopLossPorcentagem(BigDecimal stopLossPorcentagem) {
        Operacoes.stopLossPorcentagem.set(stopLossPorcentagem);
    }

    public static ObjectProperty<BigDecimal>[] getStakePadrao() {
        return stakePadrao;
    }

    public static void setStakePadrao(ObjectProperty<BigDecimal>[] stakePadrao) {
        Operacoes.stakePadrao = stakePadrao;
    }

    public static ObjectProperty<BigDecimal>[] getStakeContrato() {
        return stakeContrato;
    }

    public static void setStakeContrato(ObjectProperty<BigDecimal>[] stakeContrato) {
        Operacoes.stakeContrato = stakeContrato;
    }

    public static ObjectProperty<BigDecimal>[] getFatorMartingale() {
        return fatorMartingale;
    }

    public static void setFatorMartingale(ObjectProperty<BigDecimal>[] fatorMartingale) {
        Operacoes.fatorMartingale = fatorMartingale;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksGraficoObservableList() {
        return historicoDeTicksGraficoObservableList;
    }

    public static void setHistoricoDeTicksGraficoObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksGraficoObservableList) {
        Operacoes.historicoDeTicksGraficoObservableList = historicoDeTicksGraficoObservableList;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksAnaliseObservableList() {
        return historicoDeTicksAnaliseObservableList;
    }

    public static void setHistoricoDeTicksAnaliseObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksAnaliseObservableList) {
        Operacoes.historicoDeTicksAnaliseObservableList = historicoDeTicksAnaliseObservableList;
    }

    public static ObservableList<Integer>[] getListMaiorQtdDigito() {
        return listMaiorQtdDigito;
    }

    public static void setListMaiorQtdDigito(ObservableList<Integer>[] listMaiorQtdDigito) {
        Operacoes.listMaiorQtdDigito = listMaiorQtdDigito;
    }

    public static ObservableList<Integer>[] getListMenorQtdDigito() {
        return listMenorQtdDigito;
    }

    public static void setListMenorQtdDigito(ObservableList<Integer>[] listMenorQtdDigito) {
        Operacoes.listMenorQtdDigito = listMenorQtdDigito;
    }

    public static ObjectProperty<Tick>[] getUltimoTick() {
        return ultimoTick;
    }

    public static void setUltimoTick(ObjectProperty<Tick>[] ultimoTick) {
        Operacoes.ultimoTick = ultimoTick;
    }

    public static IntegerProperty[] getUltimoDigito() {
        return ultimoDigito;
    }

    public static void setUltimoDigito(IntegerProperty[] ultimoDigito) {
        Operacoes.ultimoDigito = ultimoDigito;
    }

    public static BooleanProperty[] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[] tickSubindo) {
        Operacoes.tickSubindo = tickSubindo;
    }

    public TmodelTransacoes[] getTmodelTransacoes() {
        return tmodelTransacoes;
    }

    public void setTmodelTransacoes(TmodelTransacoes[] tmodelTransacoes) {
        this.tmodelTransacoes = tmodelTransacoes;
    }

    public FilteredList<Transacoes>[] getTransacoesFilteredList() {
        return transacoesFilteredList;
    }

    public void setTransacoesFilteredList(FilteredList<Transacoes>[] transacoesFilteredList) {
        this.transacoesFilteredList = transacoesFilteredList;
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

    public static ObservableList<Long>[] getGrafBarListValorDigito_R() {
        return grafBarListValorDigito_R;
    }

    public static void setGrafBarListValorDigito_R(ObservableList<Long>[] grafBarListValorDigito_R) {
        Operacoes.grafBarListValorDigito_R = grafBarListValorDigito_R;
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
        Operacoes.grafLineListValorDigito_R = grafLineListValorDigito_R;
    }

    public XYChart.Series<String, Number>[] getGrafLineMACD_R() {
        return grafLineMACD_R;
    }

    public void setGrafLineMACD_R(XYChart.Series<String, Number>[] grafLineMACD_R) {
        this.grafLineMACD_R = grafLineMACD_R;
    }

    public ObservableList<Data<String, Number>>[] getGrafLineListDataMACD_R() {
        return grafLineListDataMACD_R;
    }

    public void setGrafLineListDataMACD_R(ObservableList<Data<String, Number>>[] grafLineListDataMACD_R) {
        this.grafLineListDataMACD_R = grafLineListDataMACD_R;
    }

    public static ObservableList<HistoricoDeTicks>[] getGrafLineListValorMACD_R() {
        return grafLineListValorMACD_R;
    }

    public static void setGrafLineListValorMACD_R(ObservableList<HistoricoDeTicks>[] grafLineListValorMACD_R) {
        Operacoes.grafLineListValorMACD_R = grafLineListValorMACD_R;
    }

    public static long getHoraInicial() {
        return horaInicial.get();
    }

    public static LongProperty horaInicialProperty() {
        return horaInicial;
    }

    public static void setHoraInicial(long horaInicial) {
        Operacoes.horaInicial.set(horaInicial);
    }

    public static ObjectProperty<Error>[] getError() {
        return error;
    }

    public static void setError(ObjectProperty<Error>[] error) {
        Operacoes.error = error;
    }

    public static boolean isTransacoesAtutorizadas() {
        return transacoesAtutorizadas.get();
    }

    public static BooleanProperty transacoesAtutorizadasProperty() {
        return transacoesAtutorizadas;
    }

    public static void setTransacoesAtutorizadas(boolean transacoesAtutorizadas) {
        Operacoes.transacoesAtutorizadas.set(transacoesAtutorizadas);
    }

    public static boolean isContTempo() {
        return contTempo.get();
    }

    public static BooleanProperty contTempoProperty() {
        return contTempo;
    }

    public static void setContTempo(boolean contTempo) {
        Operacoes.contTempo.set(contTempo);
    }

    public static long getTempoCorrido() {
        return tempoCorrido.get();
    }

    public static LongProperty tempoCorridoProperty() {
        return tempoCorrido;
    }

    public static void setTempoCorrido(long tempoCorrido) {
        Operacoes.tempoCorrido.set(tempoCorrido);
    }

    public DoubleProperty[] getMediaAcima() {
        return mediaAcima;
    }

    public void setMediaAcima(DoubleProperty[] mediaAcima) {
        this.mediaAcima = mediaAcima;
    }

    public DoubleProperty[] getMediaAbaixo() {
        return mediaAbaixo;
    }

    public void setMediaAbaixo(DoubleProperty[] mediaAbaixo) {
        this.mediaAbaixo = mediaAbaixo;
    }

    public static IntegerProperty[] getParesPorcentagem() {
        return paresPorcentagem;
    }

    public static void setParesPorcentagem(IntegerProperty[] paresPorcentagem) {
        Operacoes.paresPorcentagem = paresPorcentagem;
    }

    public static IntegerProperty[] getImparesPorcentagem() {
        return imparesPorcentagem;
    }

    public static void setImparesPorcentagem(IntegerProperty[] imparesPorcentagem) {
        Operacoes.imparesPorcentagem = imparesPorcentagem;
    }

    public static IntegerProperty[] getQtdPares() {
        return qtdPares;
    }

    public static void setQtdPares(IntegerProperty[] qtdPares) {
        Operacoes.qtdPares = qtdPares;
    }

    public static IntegerProperty[] getQtdImpares() {
        return qtdImpares;
    }

    public static void setQtdImpares(IntegerProperty[] qtdImpares) {
        Operacoes.qtdImpares = qtdImpares;
    }

    public static Estrategia getEstrategia() {
        return estrategia.get();
    }

    public static ObjectProperty<Estrategia> estrategiaProperty() {
        return estrategia;
    }

    public static void setEstrategia(Estrategia estrategia) {
        Operacoes.estrategia.set(estrategia);
    }

    public static IntegerProperty[] getQtdDerrotas() {
        return qtdDerrotas;
    }

    public static void setQtdDerrotas(IntegerProperty[] qtdDerrotas) {
        Operacoes.qtdDerrotas = qtdDerrotas;
    }

    public static BooleanProperty[] getRenovarTodosContratos() {
        return renovarTodosContratos;
    }

    public static void setRenovarTodosContratos(BooleanProperty[] renovarTodosContratos) {
        Operacoes.renovarTodosContratos = renovarTodosContratos;
    }

    public static TransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public static void setTransactionDAO(TransactionDAO transactionDAO) {
        Operacoes.transactionDAO = transactionDAO;
    }

    public static TransacoesDAO getTransacoesDAO() {
        return transacoesDAO;
    }

    public static void setTransacoesDAO(TransacoesDAO transacoesDAO) {
        Operacoes.transacoesDAO = transacoesDAO;
    }

    public static Map<Integer, Long>[] getListDigitosAnalise_R() {
        return listDigitosAnalise_R;
    }

    public static void setListDigitosAnalise_R(Map<Integer, Long>[] listDigitosAnalise_R) {
        Operacoes.listDigitosAnalise_R = listDigitosAnalise_R;
    }

    public static ObservableList<Integer>[] getListAnalise100MenorQtdDigito() {
        return listAnalise100MenorQtdDigito;
    }

    public static void setListAnalise100MenorQtdDigito(ObservableList<Integer>[] listAnalise100MenorQtdDigito) {
        Operacoes.listAnalise100MenorQtdDigito = listAnalise100MenorQtdDigito;
    }

    public static ObservableList<Integer>[] getListAnalise200MenorQtdDigito() {
        return listAnalise200MenorQtdDigito;
    }

    public static void setListAnalise200MenorQtdDigito(ObservableList<Integer>[] listAnalise200MenorQtdDigito) {
        Operacoes.listAnalise200MenorQtdDigito = listAnalise200MenorQtdDigito;
    }

    public static ObservableList<Integer>[] getListAnalise1000MenorQtdDigito() {
        return listAnalise1000MenorQtdDigito;
    }

    public static void setListAnalise1000MenorQtdDigito(ObservableList<Integer>[] listAnalise1000MenorQtdDigito) {
        Operacoes.listAnalise1000MenorQtdDigito = listAnalise1000MenorQtdDigito;
    }

    public static Token getToken() {
        return token.get();
    }

    public static ObjectProperty<Token> tokenProperty() {
        return token;
    }

    public static void setToken(Token token) {
        Operacoes.token.set(token);
    }
}
