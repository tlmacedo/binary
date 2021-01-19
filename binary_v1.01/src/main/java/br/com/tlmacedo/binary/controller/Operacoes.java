package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.model.*;
import br.com.tlmacedo.binary.model.Enums.*;
import br.com.tlmacedo.binary.model.TableModel.TmodelTransacoes;
import br.com.tlmacedo.binary.model.dao.TokensDAO;
import br.com.tlmacedo.binary.services.UtilJson;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Operacoes implements Initializable {

    /**
     * Objetos do formulario
     */

    public AnchorPane painelViewBinary;
    public ComboBox<Tokens> cboConta;
    public TextField txtDuracaoTicks;
    public ComboBox<DURATION_UNIT> cboDuracaoTipo;
    public TextField txtQtdRepeticoes;
    public TextField txtValorStake;
    public Label lblEstrategia;
    public ComboBox<ESTRATEGIAS> cboEstrategia;
    public CheckBox chkAgressiva;
    public ComboBox<Integer> cboQtdTicksAnalisar;
    public Button btnContratos;
    public Button btnPausar;
    public Button btnIniciar;
    public Button btnStop;

    // Informações da conta
    public Label lblProprietarioConta;
    public Label lblEmailConta;
    public Label lblSaldoConta;
    public Label lblMoedaSaldo;
    public Label lblIdConta;
    public Label lblMaxLucroMoeda;
    public Label lblMaxLucroPorcentagem;
    public Label lblMaxPrejuizoMoeda;
    public Label lblMaxPrejuizoPorcentagem;
    public Label lblHoraInicio;
    public Label lblHoraAtual;
    public Label lblTempoUso;
    public Label lblLegendaNExecucoes;
    public Label lblTotalExecucoes;
    public Label lblTotalVitorias;
    public Label lblTotalDerrotas;
    public Label lblSaldoInicial;
    public Label lblSaldoFinal;
    public Label lblTotalApostado;
    public Label lblTotalPremio;
    public Label lblTotalLucro;

    // Volatilidade R10
    public TitledPane tpn_R10;
    public CheckBox chkAtivo_R10;
    public CategoryAxis xAxis_R10;
    public NumberAxis yAxis_R10;
    public BarChart<String, Number> graf_R10;
    public Label lblLastTick_R10;
    public Label lblLegendaTick_R10;
    public Button btnPausar_R10;
    public Button btnComprar_R10;
    public Button btnStop_R10;
    public Label lblNExecucoes_R10;
    public Label lblNVitorias_R10;
    public Label lblNDerrotas_R10;
    public TableView<Transacoes> tbvTransacoes_R10;
    public Label lblApostaTotal_R10;
    public Label lblPremioTotal_R10;
    public Label lblLucro_R10;

    // Volatilidade R25
    public TitledPane tpn_R25;
    public CheckBox chkAtivo_R25;
    public CategoryAxis xAxis_R25;
    public NumberAxis yAxis_R25;
    public BarChart<String, Number> graf_R25;
    public Label lblLastTick_R25;
    public Label lblLegendaTick_R25;
    public Button btnPausar_R25;
    public Button btnComprar_R25;
    public Button btnStop_R25;
    public Label lblNExecucoes_R25;
    public Label lblNVitorias_R25;
    public Label lblNDerrotas_R25;
    public TableView<Transacoes> tbvTransacoes_R25;
    public Label lblApostaTotal_R25;
    public Label lblPremioTotal_R25;
    public Label lblLucro_R25;

    // Volatilidade R50
    public TitledPane tpn_R50;
    public CheckBox chkAtivo_R50;
    public CategoryAxis xAxis_R50;
    public NumberAxis yAxis_R50;
    public BarChart<String, Number> graf_R50;
    public Label lblLastTick_R50;
    public Label lblLegendaTick_R50;
    public Button btnPausar_R50;
    public Button btnComprar_R50;
    public Button btnStop_R50;
    public Label lblNExecucoes_R50;
    public Label lblNVitorias_R50;
    public Label lblNDerrotas_R50;
    public TableView<Transacoes> tbvTransacoes_R50;
    public Label lblApostaTotal_R50;
    public Label lblPremioTotal_R50;
    public Label lblLucro_R50;

    // Volatilidade R75
    public TitledPane tpn_R75;
    public CheckBox chkAtivo_R75;
    public CategoryAxis xAxis_R75;
    public NumberAxis yAxis_R75;
    public BarChart<String, Number> graf_R75;
    public Label lblLastTick_R75;
    public Label lblLegendaTick_R75;
    public Button btnPausar_R75;
    public Button btnComprar_R75;
    public Button btnStop_R75;
    public Label lblNExecucoes_R75;
    public Label lblNVitorias_R75;
    public Label lblNDerrotas_R75;
    public TableView<Transacoes> tbvTransacoes_R75;
    public Label lblApostaTotal_R75;
    public Label lblPremioTotal_R75;
    public Label lblLucro_R75;

    // Volatilidade R100
    public TitledPane tpn_R100;
    public CheckBox chkAtivo_R100;
    public CategoryAxis xAxis_R100;
    public NumberAxis yAxis_R100;
    public BarChart<String, Number> graf_R100;
    public Label lblLastTick_R100;
    public Label lblLegendaTick_R100;
    public Button btnPausar_R100;
    public Button btnComprar_R100;
    public Button btnStop_R100;
    public Label lblNExecucoes_R100;
    public Label lblNVitorias_R100;
    public Label lblNDerrotas_R100;
    public TableView<Transacoes> tbvTransacoes_R100;
    public Label lblApostaTotal_R100;
    public Label lblPremioTotal_R100;
    public Label lblLucro_R100;


    private static ObjectProperty<LocalDateTime> ldtHoraInicio = new SimpleObjectProperty<>();
    private static ObjectProperty<LocalDateTime> ldtHoraAtual = new SimpleObjectProperty<>(LocalDateTime.now());
    private static ObjectProperty<LocalTime> ldtTempUso = new SimpleObjectProperty<>();

    private static IntegerProperty qtdTicksAnalisar = new SimpleIntegerProperty(100);
    private static ObjectProperty<BigDecimal> fatorMartingale = new SimpleObjectProperty<>();
    private static ObjectProperty<Authorize> authorizeObject = new SimpleObjectProperty<>();
    private static final StringProperty tokenDeAutorizacao = new SimpleStringProperty(TOKENS.BOT_CAFEPERFEITO.getDescricao());
    public static final StringProperty styleTickSubindo =
            new SimpleStringProperty("-fx-background-color: #2AABE2; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;");
    public static final StringProperty styleTickDescendo
            = new SimpleStringProperty("-fx-background-color: #CD012F; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;");
    public static final StringProperty styleTickNegociando = new SimpleStringProperty("-fx-background-color: #fffd03; -fx-text-fill: #000000; -fx-background-radius: 8; -fx-background-insets: 0;");
    public static final StringProperty styleTickNegociandoFalse = new SimpleStringProperty("-fx-font-size: 15px; -fx-background-color: #ff6600; -fx-background-radius: 0 8 8 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");

    public static final StringProperty styleLabelNExecucaoNegociando = new SimpleStringProperty("-fx-border-base: black; -fx-border-shadow: #323232; -fx-light-border: derive(-fx-border-base, 25%); -fx-border-color: -fx-light-border -fx-border-base -fx-border-base -fx-light-border; -fx-border-insets: 0 1 1 0; -fx-background-color: #fffd03, -fx-border-shadow, -fx-background; -fx-background-insets: 1 0 0 1, 2; -fx-background-radius: 0 0 4 4; -fx-border-radius: 0 0 4 4; -fx-padding: 2;");
    public static final StringProperty styleLabelNExecucaoNegociandoFalse = new SimpleStringProperty("-fx-border-base: black; -fx-border-shadow: #323232; -fx-light-border: derive(-fx-border-base, 25%); -fx-border-color: -fx-light-border -fx-border-base -fx-border-base -fx-light-border; -fx-border-insets: 0 1 1 0; -fx-background-color: -fx-border-shadow, -fx-background; -fx-background-insets: 1 0 0 1, 2; -fx-background-radius: 0 0 4 4; -fx-border-radius: 0 0 4 4; -fx-padding: 2;");
    public static final StringProperty styleTotalNExecucaoNegociando = new SimpleStringProperty("-fx-font-size: 10px; -fx-background-color: #fffd03; -fx-background-radius: 8 8 0 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");
    public static final StringProperty styleTotalNExecucaoNegociandoFalse = new SimpleStringProperty("-fx-font-size: 10px; -fx-background-color: #ff6600; -fx-background-radius: 8 8 0 0; -fx-background-insets: 0 0 0 0; -fx-text-fill: #ffffff;");

    public static StringProperty transactionsAuthorizeds = new SimpleStringProperty();
    public static ObservableList<Transacoes> transacoesEfetuadasObservableList = FXCollections.observableArrayList();

    public static ObjectProperty<BigDecimal>[] stakePadrao = new ObjectProperty[SYMBOL.values().length];
    public static ObjectProperty<BigDecimal>[] stakeContrato = new ObjectProperty[SYMBOL.values().length];
    public static IntegerProperty[] qtdDerrotas = new IntegerProperty[SYMBOL.values().length];
    public static ObjectProperty<BigDecimal>[] vlrPerdas = new ObjectProperty[SYMBOL.values().length];

    /**
     * Variaveis da classe para volatilidades
     */

    public static BooleanProperty[] autorizado = new BooleanProperty[SYMBOL.values().length];
    public static BooleanProperty[] volatilidadeAtivada = new BooleanProperty[SYMBOL.values().length];
    public static BooleanProperty[] compraAutorizada = new BooleanProperty[SYMBOL.values().length];
    public static BooleanProperty[] negociandoVol = new BooleanProperty[SYMBOL.values().length];
    public static BooleanProperty ws_Conectado = new SimpleBooleanProperty(false);
    public static ObjectProperty<WSClient> ws_Client = new SimpleObjectProperty<>();

    public static ObjectProperty<PriceProposal>[][] priceProposal = new ObjectProperty[SYMBOL.values().length][10];
    public static ObjectProperty<PriceProposal>[] lastPriceProposal = new ObjectProperty[SYMBOL.values().length];
    public static ObjectProperty<Proposal>[][] proposal = new ObjectProperty[SYMBOL.values().length][10];
    public static ObjectProperty<Buy>[][] buy = new ObjectProperty[SYMBOL.values().length][10];
    public static ObservableList<HistoricoTicks>[] historicoTicksObservableList = new ObservableList[SYMBOL.values().length];
    public static BooleanProperty[] tickSubindo = new BooleanProperty[SYMBOL.values().length];

    public static ObservableList<Transaction>[] transactionObservableList = new ObservableList[SYMBOL.values().length];


    public static StringProperty[] ultimoTick = new StringProperty[SYMBOL.values().length];
    public static IntegerProperty[] ultimoDigito = new IntegerProperty[SYMBOL.values().length];
    public static IntegerProperty[] digitoMaiorQuantidade = new IntegerProperty[SYMBOL.values().length];
    public static IntegerProperty[] digitoMenorQuantidade = new IntegerProperty[SYMBOL.values().length];

    private XYChart.Series<String, Number>[] grafVolatilidade_R = new XYChart.Series[SYMBOL.values().length];
    private ObservableList<Data<String, Number>>[] grafListDataDigitos_R = new ObservableList[SYMBOL.values().length];
    private ObservableList<IntegerProperty>[] grafListValorDigito_R = new ObservableList[SYMBOL.values().length];
    private Text[][] grafTxtDigito_R = new Text[SYMBOL.values().length][10];

    private TmodelTransacoes[] tmodelTransacoes = new TmodelTransacoes[SYMBOL.values().length];

    //private TmodelTransacoes[] tmodelTransacoes = new TmodelTransacoes[SYMBOL.values().length + 1];

    /**
     * Variaveis utilizadas nos graficos
     */

    TokensDAO tokensDAO = new TokensDAO();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            inicializarObjetosGeral();
            inicializarVolatilidades();
            if (carregarAnalises()) {
                Platform.runLater(() -> {
                    try {
                        carregarInformacoesGraficos();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            getBtnContratos().setDisable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void gerandoSegurancas() {
        for (SYMBOL symbol : SYMBOL.values())
            gerandoSegurancas(symbol);
    }

    private void restaurandoSegurancas() {
        for (SYMBOL symbol : SYMBOL.values()) {
            //getVolatilidadeAtivada()[symbol.getCod()].setValue(true);
            getNegociandoVol()[symbol.getCod()].setValue(false);
            getCompraAutorizada()[symbol.getCod()].setValue(false);

            getQtdDerrotas()[symbol.getCod()].setValue(0);
            getLastPriceProposal()[symbol.getCod()].setValue(null);

            switch (symbol) {
                case R_10 -> getChkAtivo_R10().setSelected(true);
                case R_25 -> getChkAtivo_R25().setSelected(true);
                case R_50 -> getChkAtivo_R50().setSelected(true);
                case R_75 -> getChkAtivo_R75().setSelected(true);
                case R_100 -> getChkAtivo_R100().setSelected(true);
            }
            controlesNegociacaoAtivados(true);
        }
    }

    private void gerandoSegurancas(SYMBOL symbol) {
//        authorizeObjectProperty().setValue(new Authorize());
//        ws_ConectadoProperty().setValue(false);

        getVolatilidadeAtivada()[symbol.getCod()] = new SimpleBooleanProperty(true);
        getNegociandoVol()[symbol.getCod()] = new SimpleBooleanProperty(false);
        getCompraAutorizada()[symbol.getCod()] = new SimpleBooleanProperty(false);

        getQtdDerrotas()[symbol.getCod()] = new SimpleIntegerProperty(0);
        getLastPriceProposal()[symbol.getCod()] = new SimpleObjectProperty<>();

        switch (symbol) {
            case R_10 -> {
                getChkAtivo_R10().setSelected(true);
                getChkAtivo_R10().selectedProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    getVolatilidadeAtivada()[symbol.getCod()].setValue(n);
                });
            }
            case R_25 -> {
                getChkAtivo_R25().setSelected(true);
                getChkAtivo_R25().selectedProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    getVolatilidadeAtivada()[symbol.getCod()].setValue(n);
                });
            }
            case R_50 -> {
                getChkAtivo_R50().setSelected(true);
                getChkAtivo_R50().selectedProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    getVolatilidadeAtivada()[symbol.getCod()].setValue(n);
                });
            }
            case R_75 -> {
                getChkAtivo_R75().setSelected(true);
                getChkAtivo_R75().selectedProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    getVolatilidadeAtivada()[symbol.getCod()].setValue(n);
                });
            }
            case R_100 -> {
                getChkAtivo_R100().setSelected(true);
                getChkAtivo_R100().selectedProperty().addListener((ov, o, n) -> {
                    if (n == null) return;
                    getVolatilidadeAtivada()[symbol.getCod()].setValue(n);
                });
            }
        }
        controlesNegociacaoAtivados(true);
    }

    private void criarGavetasContratos() {
        for (SYMBOL symbol : SYMBOL.values())
            criarGavetasContratos(symbol);
    }

    private void limparGavetasContratos() {
        getTransacoesEfetuadasObservableList().clear();
        for (SYMBOL symbol : SYMBOL.values()) {
            getTransactionObservableList()[symbol.getCod()].clear();
            for (int i = 0; i < 10; i++) {
                getPriceProposal()[symbol.getCod()][i].setValue(null);
                getProposal()[symbol.getCod()][i].setValue(null);
            }
            getStakePadrao()[symbol.getCod()] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getStakeContrato()[symbol.getCod()] = new SimpleObjectProperty<>(BigDecimal.ZERO);
            getVlrPerdas()[symbol.getCod()] = new SimpleObjectProperty<>(BigDecimal.ZERO);
        }
    }

    private void criarGavetasContratos(SYMBOL symbol) {
        getTransactionObservableList()[symbol.getCod()] = FXCollections.observableArrayList();
        for (int i = 0; i < 10; i++) {
            getPriceProposal()[symbol.getCod()][i] = new SimpleObjectProperty<>();
            getProposal()[symbol.getCod()][i] = new SimpleObjectProperty<>();
            getBuy()[symbol.getCod()][i] = new SimpleObjectProperty<>();
        }
        getStakePadrao()[symbol.getCod()] = new SimpleObjectProperty<>(BigDecimal.ZERO);
        getStakeContrato()[symbol.getCod()] = new SimpleObjectProperty<>(BigDecimal.ZERO);
    }

    private void inicializarObjetosGeral() {
        gerandoSegurancas();

        //getCboConta().setItems(Arrays.stream(TOKENS.values()).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboConta().setItems(tokensDAO.getAll(Tokens.class, null, null)
                .stream().collect(Collectors.toCollection(FXCollections::observableArrayList)));
//        getCboConta().getSelectionModel().selectedItemProperty().addListener((ov, o, n) -> {
//            tokenDeAutorizacaoProperty().setValue(n.getDescricao());
//        });
        getCboConta().valueProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            try {
                tokenDeAutorizacaoProperty().setValue(n.getDescricao());
                autorizarAplicacaoParaComprar();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        authorizeObjectProperty().addListener((ov, o, n) -> {
//            if (n == null) return;
            System.out.printf("autho: %s\n", n);
            getLblProprietarioConta().setText(n.getFullname());
            getLblEmailConta().setText(n.getEmail());
            String balance = "0.00";
            if (n.getBalance() != null)
                balance = n.getBalance().toString();
            getLblSaldoConta().setText(balance);
            getLblMoedaSaldo().setText(n.getCurrency());
            getLblIdConta().setText(n.getLoginid());
        });

        getLblHoraAtual().textProperty().bind(ldtHoraAtualProperty().asString());
//        getLblHoraInicio().textProperty().bind(ldtHoraInicioProperty().asString());
        getLblTempoUso().textProperty().bind(ldtTempUsoProperty().asString());
        ldtHoraAtualProperty().addListener((observableValue, localDateTime, t1) -> {
//            if (t1 == null || getLdtHoraInicio() == null) return;
//            System.out.printf("%s\n\n", getLdtHoraInicio().HoraInicio().. .minus(getLdtHoraInicio().toLocalTime()));
//            long[] tempo = UtilDatas.getTime(getLdtHoraInicio(), getLdtHoraAtual());
//            getLdtTempUso().of
//            setLdtTempUso(Period.parse(UtilDatas.getIntervaloTempo(getLdtHoraInicio(), getLdtHoraAtual())));
//            setLdtTempUso(Period.from(Duration.between(getLdtHoraAtual(), getLdtHoraInicio())));
        });
        getTxtDuracaoTicks().setText("1");
        getCboDuracaoTipo().setItems(Arrays.stream(DURATION_UNIT.values()).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboDuracaoTipo().getSelectionModel().select(4);

        getTxtValorStake().setText("0.35");

        getCboEstrategia().setItems(Arrays.stream(ESTRATEGIAS.values()).collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getCboEstrategia().getSelectionModel().select(0);

        getCboQtdTicksAnalisar().getItems().setAll(100, 75, 50, 25, 10);
        getCboQtdTicksAnalisar().getSelectionModel().select(0);
        getCboQtdTicksAnalisar().valueProperty().addListener((ov, o, n) -> {
            if (n == null) return;
            qtdTicksAnalisarProperty().setValue(n);
            qtdTicksAnalisarProperty().addListener((ov1, o1, n1) -> {
                if (n1 == null) return;
                try {
                    for (SYMBOL symbol : SYMBOL.values())
                        solicitarHistoricoTicks();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });
        getBtnContratos().setOnAction(actionEvent -> {
            if (!validarParaGerarContratos()) return;
            try {
//                if (autorizarAplicacaoParaComprar()) {
                if (transactionsAuthorizedsProperty().getValue() == null)
                    solicitarTransacoes();
                gerarContratos();
                getBtnContratos().setDisable(true);
                ativarEstrategias();
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        getBtnIniciar().setOnAction(actionEvent -> {
            getBtnIniciar().setDisable(true);
            for (SYMBOL symbol : SYMBOL.values())
                if (getVolatilidadeAtivada()[symbol.getCod()].getValue())
                    getCompraAutorizada()[symbol.getCod()].setValue(true);
            setLdtHoraInicio(LocalDateTime.now());
            getLblSaldoInicial().setText(getLblSaldoConta().getText());
            getLblHoraInicio().setText(getLdtHoraInicio().toString());
        });

        getBtnStop().setOnAction(actionEvent -> {
            for (SYMBOL symbol : SYMBOL.values())
                switch (symbol) {
                    case R_10 -> getBtnPausar_R10().fire();
                    case R_25 -> getBtnPausar_R25().fire();
                    case R_50 -> getBtnPausar_R50().fire();
                    case R_75 -> getBtnPausar_R75().fire();
                    case R_100 -> getBtnPausar_R100().fire();
                }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            restaurandoSegurancas();
            limparGavetasContratos();
            getCboConta().getSelectionModel().select(-1);
            getBtnContratos().setDisable(false);
            getBtnIniciar().setText("Iniciar");
        });

        getBtnPausar().setOnAction(actionEvent -> {
            getBtnIniciar().setText("Continuar");
            getBtnIniciar().setDisable(false);
            for (SYMBOL symbol : SYMBOL.values())
                getCompraAutorizada()[symbol.getCod()].setValue(false);
        });

        getBtnPausar_R10().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_10.getCod()].setValue(false));
        getBtnPausar_R25().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_25.getCod()].setValue(false));
        getBtnPausar_R50().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_50.getCod()].setValue(false));
        getBtnPausar_R75().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_75.getCod()].setValue(false));
        getBtnPausar_R100().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_100.getCod()].setValue(false));

        getBtnComprar_R10().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_10.getCod()].setValue(true));
        getBtnComprar_R25().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_25.getCod()].setValue(true));
        getBtnComprar_R50().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_50.getCod()].setValue(true));
        getBtnComprar_R75().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_75.getCod()].setValue(true));
        getBtnComprar_R100().setOnAction(actionEvent -> getVolatilidadeAtivada()[SYMBOL.R_100.getCod()].setValue(true));
    }

    private boolean validarParaGerarContratos() {
        if (!minimaQtdVolatilidadeAtiva()) return false;
        if (getCboConta().getSelectionModel().getSelectedItem() == null) return false;
        if (Double.valueOf(getTxtValorStake().getText()).isNaN()) return false;
        if (getCboEstrategia().getSelectionModel().getSelectedItem() == null) return false;
        controlesNegociacaoAtivados(false);
        return true;
    }

    private void controlesNegociacaoAtivados(boolean ativar) {
        getCboConta().setDisable(!ativar);
        getTxtDuracaoTicks().setDisable(!ativar);
        getCboDuracaoTipo().setDisable(!ativar);
        getTxtValorStake().setDisable(!ativar);
        getTxtQtdRepeticoes().setDisable(!ativar);
        getCboEstrategia().setDisable(!ativar);
        getLblEstrategia().setText(
                !ativar
                        ? getLblEstrategia().getText() + ": " + getCboEstrategia().getSelectionModel().getSelectedItem().toString()
                        : "Estratégia"
        );
        getChkAgressiva().setDisable(!ativar);
        getBtnContratos().setDisable(!ativar);

        getBtnContratos().setDisable(ativar);
        getBtnIniciar().setDisable(ativar);
        getBtnPausar().setDisable(ativar);
        getBtnStop().setDisable(ativar);

        for (SYMBOL symbol : SYMBOL.values())
            controlesNegociacaoAtivados(symbol, ativar);
    }

    private void controlesNegociacaoAtivados(SYMBOL symbol, boolean ativar) {
        switch (symbol) {
            case R_10 -> {
                getBtnComprar_R10().setDisable(ativar);
                getBtnPausar_R10().setDisable(ativar);
                getBtnStop_R10().setDisable(ativar);
            }
            case R_25 -> {
                getBtnComprar_R25().setDisable(ativar);
                getBtnPausar_R25().setDisable(ativar);
                getBtnStop_R25().setDisable(ativar);
            }
            case R_50 -> {
                getBtnComprar_R50().setDisable(ativar);
                getBtnPausar_R50().setDisable(ativar);
                getBtnStop_R50().setDisable(ativar);
            }
            case R_75 -> {
                getBtnComprar_R75().setDisable(ativar);
                getBtnPausar_R75().setDisable(ativar);
                getBtnStop_R75().setDisable(ativar);
            }
            case R_100 -> {
                getBtnComprar_R100().setDisable(ativar);
                getBtnPausar_R100().setDisable(ativar);
                getBtnStop_R100().setDisable(ativar);
            }
        }
    }

    private void inicializarVolatilidades() {
        for (SYMBOL symbol : SYMBOL.values()) {

            criarGavetasContratos();

            getGrafListDataDigitos_R()[symbol.getCod()] = FXCollections.observableArrayList();
            getGrafListValorDigito_R()[symbol.getCod()] = FXCollections.observableArrayList();

            for (int i = 0; i < 10; i++) {
                getGrafTxtDigito_R()[symbol.getCod()][i] = new Text("");
                getGrafTxtDigito_R()[symbol.getCod()][i].setFont(Font.font("Arial", 10));
                getGrafListValorDigito_R()[symbol.getCod()].add(i, new SimpleIntegerProperty(0));
                getGrafListDataDigitos_R()[symbol.getCod()].add(i, new XYChart.Data<>(String.valueOf(i), 0));
            }
            while (getGrafListDataDigitos_R()[symbol.getCod()].size() > 10)
                getGrafListDataDigitos_R()[symbol.getCod()]
                        .remove(getGrafListDataDigitos_R()[symbol.getCod()].get(10));
            while (getGrafListValorDigito_R()[symbol.getCod()].size() > 10)
                getGrafListValorDigito_R()[symbol.getCod()]
                        .remove(getGrafListValorDigito_R()[symbol.getCod()].get(10));

            Platform.runLater(() -> {
                for (int i = 0; i < 10; i++) {
                    int finalI = i;
                    int finalI1 = i;
                    getGrafListValorDigito_R()[symbol.getCod()].get(i).addListener((ov, o, n) -> {
                        Integer value = n == null ? 0 : n.intValue();
                        Double porcentagem = 0.;
                        if (value != 0)
                            porcentagem = (value
                                    / (getQtdTicksAnalisar()
                                    / 100.));
                        getGrafListDataDigitos_R()[symbol.getCod()].get(finalI).setYValue(porcentagem);
                        getGrafTxtDigito_R()[symbol.getCod()][finalI1].setText(String.format("%d%%", porcentagem.intValue()));
                    });
                }
            });

            ws_ClientProperty().setValue(new WSClient());
            ws_ClientProperty().getValue().connect();


            getTickSubindo()[symbol.getCod()] = new SimpleBooleanProperty();
            getUltimoTick()[symbol.getCod()] = new SimpleStringProperty();
            getUltimoDigito()[symbol.getCod()] = new SimpleIntegerProperty();
            getUltimoTick()[symbol.getCod()].addListener((ov, o, n) -> {
                if (n == null) return;
                getUltimoDigito()[symbol.getCod()].setValue(Integer.parseInt(n.substring(n.length() - 1)));
            });

            getDigitoMaiorQuantidade()[symbol.getCod()] = new SimpleIntegerProperty(100);
            getDigitoMenorQuantidade()[symbol.getCod()] = new SimpleIntegerProperty(0);
            getHistoricoTicksObservableList()[symbol.getCod()] = FXCollections.observableArrayList();

            getGrafVolatilidade_R()[symbol.getCod()] = new XYChart.Series<>();

            getTmodelTransacoes()[symbol.getCod()] = new TmodelTransacoes(symbol);
            getTmodelTransacoes()[symbol.getCod()].criarTabela();


            switch (symbol) {
                case R_10 -> {
                    getLblLastTick_R10().textProperty().bind(getUltimoTick()[symbol.getCod()]);
                    //getVolatilidadeAtivada()[symbol.getCod()].bind(getChkAtivo_R10().selectedProperty());
                    getTickSubindo()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblLastTick_R10().setStyle(getStyleTickSubindo());
                        else
                            getLblLastTick_R10().setStyle(getStyleTickDescendo());
                    });
                    getNegociandoVol()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null || !n)
                            getLblLegendaTick_R10().setStyle(getStyleTickNegociandoFalse());
                        else
                            getLblLegendaTick_R10().setStyle(getStyleTickNegociando());
                        pintarNExecucao(symbol);
                    });
                    getyAxis_R10().setUpperBound(30);
                    getGraf_R10().getData().add(getGrafVolatilidade_R()[symbol.getCod()]);

                    getTmodelTransacoes()[symbol.getCod()].setTbvTransacoes(getTbvTransacoes_R10());

                    getLblNExecucoes_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoProperty().asString());
                    getLblNVitorias_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaProperty().asString());
                    getLblNDerrotas_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaProperty().asString());

                    getLblApostaTotal_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaProperty().asString());
                    getLblPremioTotal_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioProperty().asString());
                    getLblLucro_R10().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroProperty().asString());

                    getBtnComprar_R10().setOnAction(actionEvent -> {
                        try {
                            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                                case DIGITDIFF -> {
                                    comprarContrato(symbol, getUltimoDigito()[symbol.getCod()].getValue());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
                case R_25 -> {
                    getLblLastTick_R25().textProperty().bind(getUltimoTick()[symbol.getCod()]);
                    getTickSubindo()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblLastTick_R25().setStyle(getStyleTickSubindo());
                        else
                            getLblLastTick_R25().setStyle(getStyleTickDescendo());
                    });
                    getNegociandoVol()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null || !n)
                            getLblLegendaTick_R25().setStyle(getStyleTickNegociandoFalse());
                        else
                            getLblLegendaTick_R25().setStyle(getStyleTickNegociando());
                    });
                    getyAxis_R25().setUpperBound(30);
                    getGraf_R25().getData().add(getGrafVolatilidade_R()[symbol.getCod()]);

                    getTmodelTransacoes()[symbol.getCod()].setTbvTransacoes(getTbvTransacoes_R25());

                    getLblNExecucoes_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoProperty().asString());
                    getLblNVitorias_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaProperty().asString());
                    getLblNDerrotas_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaProperty().asString());

                    getLblApostaTotal_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaProperty().asString());
                    getLblPremioTotal_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioProperty().asString());
                    getLblLucro_R25().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroProperty().asString());

                    getBtnComprar_R25().setOnAction(actionEvent -> {
                        try {
                            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                                case DIGITDIFF -> {
                                    comprarContrato(symbol, getUltimoDigito()[symbol.getCod()].getValue());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
                case R_50 -> {
                    getLblLastTick_R50().textProperty().bind(getUltimoTick()[symbol.getCod()]);
                    getTickSubindo()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblLastTick_R50().setStyle(getStyleTickSubindo());
                        else
                            getLblLastTick_R50().setStyle(getStyleTickDescendo());
                    });
                    getNegociandoVol()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null || !n)
                            getLblLegendaTick_R50().setStyle(getStyleTickNegociandoFalse());
                        else
                            getLblLegendaTick_R50().setStyle(getStyleTickNegociando());
                    });
                    getyAxis_R50().setUpperBound(30);
                    getGraf_R50().getData().add(getGrafVolatilidade_R()[symbol.getCod()]);

                    getTmodelTransacoes()[symbol.getCod()].setTbvTransacoes(getTbvTransacoes_R50());

                    getLblNExecucoes_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoProperty().asString());
                    getLblNVitorias_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaProperty().asString());
                    getLblNDerrotas_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaProperty().asString());

                    getLblApostaTotal_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaProperty().asString());
                    getLblPremioTotal_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioProperty().asString());
                    getLblLucro_R50().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroProperty().asString());

                    getBtnComprar_R50().setOnAction(actionEvent -> {
                        try {
                            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                                case DIGITDIFF -> {
                                    comprarContrato(symbol, getUltimoDigito()[symbol.getCod()].getValue());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
                case R_75 -> {
                    getLblLastTick_R75().textProperty().bind(getUltimoTick()[symbol.getCod()]);
                    getTickSubindo()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblLastTick_R75().setStyle(getStyleTickSubindo());
                        else
                            getLblLastTick_R75().setStyle(getStyleTickDescendo());
                    });
                    getNegociandoVol()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null || !n)
                            getLblLegendaTick_R75().setStyle(getStyleTickNegociandoFalse());
                        else
                            getLblLegendaTick_R75().setStyle(getStyleTickNegociando());
                    });
                    getyAxis_R75().setUpperBound(30);
                    getGraf_R75().getData().add(getGrafVolatilidade_R()[symbol.getCod()]);

                    getTmodelTransacoes()[symbol.getCod()].setTbvTransacoes(getTbvTransacoes_R75());

                    getLblNExecucoes_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoProperty().asString());
                    getLblNVitorias_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaProperty().asString());
                    getLblNDerrotas_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaProperty().asString());

                    getLblApostaTotal_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaProperty().asString());
                    getLblPremioTotal_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioProperty().asString());
                    getLblLucro_R75().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroProperty().asString());

                    getBtnComprar_R75().setOnAction(actionEvent -> {
                        try {
                            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                                case DIGITDIFF -> {
                                    comprarContrato(symbol, getUltimoDigito()[symbol.getCod()].getValue());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
                case R_100 -> {
                    getLblLastTick_R100().textProperty().bind(getUltimoTick()[symbol.getCod()]);
                    getTickSubindo()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null) return;
                        if (n)
                            getLblLastTick_R100().setStyle(getStyleTickSubindo());
                        else
                            getLblLastTick_R100().setStyle(getStyleTickDescendo());
                    });
                    getNegociandoVol()[symbol.getCod()].addListener((ov, o, n) -> {
                        if (n == null || !n)
                            getLblLegendaTick_R100().setStyle(getStyleTickNegociandoFalse());
                        else
                            getLblLegendaTick_R100().setStyle(getStyleTickNegociando());
                    });
                    getyAxis_R100().setUpperBound(30);
                    getGraf_R100().getData().add(getGrafVolatilidade_R()[symbol.getCod()]);

                    getTmodelTransacoes()[symbol.getCod()].setTbvTransacoes(getTbvTransacoes_R100());

                    getLblNExecucoes_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoProperty().asString());
                    getLblNVitorias_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaProperty().asString());
                    getLblNDerrotas_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaProperty().asString());

                    getLblApostaTotal_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaProperty().asString());
                    getLblPremioTotal_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioProperty().asString());
                    getLblLucro_R100().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroProperty().asString());

                    getBtnComprar_R100().setOnAction(actionEvent -> {
                        try {
                            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                                case DIGITDIFF -> {
                                    comprarContrato(symbol, getUltimoDigito()[symbol.getCod()].getValue());
                                }
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    });
                }
            }

            getLblTotalExecucoes().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNExecucaoAcumuladoProperty().asString());
            getLblTotalVitorias().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNVitoriaAcumuladoProperty().asString());
            getLblTotalDerrotas().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].qtdNDerrotaAcumuladoProperty().asString());

            getLblTotalApostado().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalApostaAcumuladoProperty().asString());
            getLblTotalPremio().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalPremioAcumuladoProperty().asString());
            getLblTotalLucro().textProperty().bind(getTmodelTransacoes()[symbol.getCod()].totalLucroAcumuladoProperty().asString());

            getTmodelTransacoes()[symbol.getCod()].setTransacoesObservableList(getTransacoesEfetuadasObservableList());
            getTmodelTransacoes()[symbol.getCod()].escutarTransacoesTabela();
            getTmodelTransacoes()[symbol.getCod()].tabela_preencher();
        }
        getLblLegendaNExecucoes().styleProperty().bind(Bindings.createStringBinding(() -> {
                    boolean negociando;
                    for (SYMBOL symbol : SYMBOL.values()) {
                        negociando = (getNegociandoVol()[symbol.getCod()].getValue());
                        if (negociando)
                            return styleTotalNExecucaoNegociandoProperty().getValue();
                    }
                    return styleTotalNExecucaoNegociandoFalseProperty().getValue();
                }, getLblLastTick_R10().styleProperty(), getLblLastTick_R25().styleProperty(), getLblLastTick_R50().styleProperty(),
                getLblLastTick_R75().styleProperty(), getLblLastTick_R100().styleProperty()));
    }

    private boolean carregarAnalises() throws Exception {
        Platform.runLater(() -> {
            try {
                solicitarTicks();
                monitorarVariaveis();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return true;
    }

    private boolean solicitarTicks() throws Exception {
        for (SYMBOL symbol : SYMBOL.values()) {
            String jsonTicksStream = UtilJson.getJsonFromObject(new TicksStream(symbol.toString()));
            ws_ClientProperty().getValue().getMyWebSocket().send(jsonTicksStream);
        }
        return true;
    }

    private boolean solicitarHistoricoTicks() throws Exception {
        for (SYMBOL symbol : SYMBOL.values()) {
            String jsonTicksHistory = UtilJson.getJsonFromObject(new TicksHistory(symbol.toString(), getQtdTicksAnalisar()));
            ws_ClientProperty().getValue().getMyWebSocket().send(jsonTicksHistory);
            //Thread.sleep(1000);
        }
        return true;
    }

    private boolean solicitarTransacoes() throws Exception {
//        for (SYMBOL symbol : SYMBOL.values()) {
        String jsonTransacoes = UtilJson.getJsonFromObject(new TransactionsStream(1));
        ws_ClientProperty().getValue().getMyWebSocket().send(jsonTransacoes);
        monitorarTransacoes();
//        }
        return true;
    }

    private boolean autorizarAplicacaoParaComprar() throws Exception {
//        for (SYMBOL symbol : SYMBOL.values())
//            if (getVolatilidadeAtivada()[symbol.getCod()].getValue()) {
        String jsonAuthorize = String.format("{\"authorize\":\"%s\"}", getCboConta().getSelectionModel().getSelectedItem().getDescricao());
        ws_ClientProperty().getValue().getMyWebSocket().send(jsonAuthorize);
//            }
        return true;
    }

    private boolean monitorarVariaveis() throws Exception {
        for (SYMBOL symbol : SYMBOL.values()) {
            for (int i = 0; i < 10; i++) {
                getProposal()[symbol.getCod()][i].addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n.getError() != null) {
                            System.out.printf("\n\n\n\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "%s%s*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n", "Proposal", n.getError());
                            gerarContrato(symbol, n.getError().getContrac_type(), n.getError().getBarrier());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                getBuy()[symbol.getCod()][i].addListener((ov, o, n) -> {
                    if (n == null) return;
                    try {
                        if (n.getError() != null) {
                            System.out.printf("\n\n\n\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "%s%s*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n" +
                                    "*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-\n", "Buy", n.getError());
                            switch (n.getError().getCode()) {
                                case "InvalidContractProposal" -> {
                                    for (int j = 0; j < 10; j++) {
                                        if (getProposal()[symbol.getCod()][j].getValue().getId().equals(n.getError().getBuyId()))
                                            enviarContrato(symbol, getPriceProposal()[symbol.getCod()][j].getValue());
                                    }
                                }
                                case "RateLimit" -> {
                                    comprarContrato(symbol, n.getError().getBarrier());
                                }
                                default -> {
//                                    for (int j = 0; j < 10; j++) {
//                                        if (getProposal()[symbol.getCod()][j].getValue().getId().equals(n.getError().getBuyId()))
//                                            comprarContrato(symbol, j);
//                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
            getHistoricoTicksObservableList()[symbol.getCod()].addListener((ListChangeListener<? super HistoricoTicks>) c -> {
                IntegerProperty oldQtd, newQtd;
                while (c.next()) {
                    for (HistoricoTicks removidos : c.getRemoved()) {
                        newQtd = getGrafListValorDigito_R()[symbol.getCod()].get(removidos.getUltimoDigito());
                        oldQtd = newQtd;
                        newQtd.setValue(oldQtd.getValue() - 1);

                        getGrafListValorDigito_R()[symbol.getCod()].set(removidos.getUltimoDigito(), newQtd);

                    }
                    for (HistoricoTicks adicionados : c.getAddedSubList()) {
//                        if (symbol == SYMBOL.R_10)
//                            System.out.printf("%s__ListenerHistorico: [%s]-[%s]\n", symbol, adicionados.getQuoteCompleto(), adicionados.getUltimoDigito());
                        Platform.runLater(() -> setLdtHoraAtual(LocalDateTime
                                .ofInstant(Instant.ofEpochSecond(adicionados.getTime()),
                                        TimeZone.getDefault().toZoneId())));
                        Transacoes transacoesTemp;
                        if (getTransacoesEfetuadasObservableList().size() > 0) {
                            if ((transacoesTemp = getTransacoesEfetuadasObservableList().stream()
                                    .filter(transacoes -> transacoes.getTickCompra() == null
                                            && transacoes.getSymbol().equals(symbol))
                                    .findFirst().orElse(null)) != null)
                                transacoesTemp.tickCompraProperty().setValue(adicionados.getPrice());
                        }
                        newQtd = getGrafListValorDigito_R()[symbol.getCod()].get(adicionados.getUltimoDigito());
                        oldQtd = newQtd;
                        newQtd.setValue(oldQtd.getValue() + 1);

                        getGrafListValorDigito_R()[symbol.getCod()].set(adicionados.getUltimoDigito(), newQtd);
                    }

                    getDigitoMenorQuantidade()[symbol.getCod()].setValue(getGrafListValorDigito_R()[symbol.getCod()].stream()
                            .sorted(Comparator.comparing(integerProperty -> integerProperty.getValue()))
                            .findFirst().get().getValue());
                    getDigitoMaiorQuantidade()[symbol.getCod()].setValue(getGrafListValorDigito_R()[symbol.getCod()].stream()
                            .sorted(Comparator.comparing(integerProperty -> integerProperty.getValue()))
                            .collect(Collectors.toList()).get(getGrafListValorDigito_R()[symbol.getCod()].size() - 1).getValue());
                    Platform.runLater(() -> {
                        getGrafListDataDigitos_R()[symbol.getCod()].stream()
                                .forEach(stringNumberData -> {
                                    if (stringNumberData.getYValue().intValue() >= (double) ((getDigitoMaiorQuantidade()[symbol.getCod()].getValue())
                                            / (getQtdTicksAnalisar() / 100.))) {
                                        stringNumberData.getNode().setStyle("-fx-bar-fill: #147e35; -fx-border-color: #1f1e1e;");
                                    } else if (stringNumberData.getYValue().intValue() <= (double) ((getDigitoMenorQuantidade()[symbol.getCod()].getValue())
                                            / (getQtdTicksAnalisar() / 100.))) {
                                        stringNumberData.getNode().setStyle("-fx-bar-fill: #cd060f; -fx-border-color: #1f1e1e;");
                                    } else {
                                        stringNumberData.getNode().setStyle("-fx-bar-fill: #dcedfa; -fx-border-color: #1f1e1e;");
                                    }
                                });
                    });

                }
            });
        }
        return true;
    }

    private void ativarEstrategias() {
        for (SYMBOL symbol : SYMBOL.values()) {
            if (getVolatilidadeAtivada()[symbol.getCod()].getValue()) {
                getUltimoTick()[symbol.getCod()].addListener((ov, o, n) -> {
                    if (n == null || getNegociandoVol()[symbol.getCod()].getValue()) return;
                    int digito = getUltimoDigito()[symbol.getCod()].getValue();
                    try {
                        switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                            case DIGITOVER1, DIGITOVER2, DIGITOVER3, DIGITOVER6, DIGITOVER8 -> {
//                            comprarContrato(symbol, digito);
                            }
                            case DIGITUNDER1, DIGITUNDER3, DIGITUNDER6, DIGITUNDER7, DIGITUNDER8 -> {
//                            gerarContrato(symbol, CONTRAC_TYPE.DIGITOVER, digito);
                            }
                            case DIGITUNDER3OVER6, DIGITUNDER7OVER2 -> {
                                estrategiaDIGITUNDER3OVER6(symbol, digito);
//                            if (todas) {
//                                gerarContrato(symbol, CONTRAC_TYPE.DIGITUNDER, Integer.valueOf(digitos.substring(0, 1)));
//                                gerarContrato(symbol, CONTRAC_TYPE.DIGITOVER, Integer.valueOf(digitos.substring(1)));
//                            } else {
//                                gerarContrato(symbol, getLastPriceProposal()[symbol.getCod()].getValue().getContract_type(),
//                                        Integer.parseInt(getLastPriceProposal()[symbol.getCod()].getValue().getBarrier()));
//                            }
                            }
                            case DIGITODD -> {
//                            gerarContrato(symbol, CONTRAC_TYPE.DIGITODD, 1);
                            }
                            case DIGITEVEN -> {
//                            gerarContrato(symbol, CONTRAC_TYPE.DIGITEVEN, 0);
                            }
                            case DIGITODDEVEN -> {
//                            if (todas) {
//                                gerarContrato(symbol, CONTRAC_TYPE.DIGITODD, 1);
//                                gerarContrato(symbol, CONTRAC_TYPE.DIGITEVEN, 0);
//                            } else {
//                                gerarContrato(symbol, getLastPriceProposal()[symbol.getCod()].getValue().getContract_type(),
//                                        getLastPriceProposal()[symbol.getCod()].getValue().getContract_type() == CONTRAC_TYPE.DIGITODD
//                                                ? 1 : 0);
//                            }
                            }
                            case DIGITDIFF -> {
                                estrategiaDIFF(symbol);
                            }
                            case DIGITDIFF_MG_PARC -> {
                                estrategiaDIFF_MG_PARC(symbol);
                            }
//                    default -> {
//
//                    }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                });
            }
        }
    }

    private void monitorarTransacoes() throws Exception {
        for (SYMBOL symbol : SYMBOL.values())
            monitorarTransacoes(symbol);
    }

    private void monitorarTransacoes(SYMBOL symbol) throws Exception {
        getTransactionObservableList()[symbol.getCod()].addListener((ListChangeListener<? super Transaction>) change -> {
            while (change.next()) {
                for (Transaction transaction : change.getAddedSubList()) {
                    try {
                        ACTION action = ACTION.valueOf(transaction.getAction().toUpperCase());
                        if (action != null)
                            switch (action) {
                                case BUY -> Platform.runLater(() ->
                                        getTransacoesEfetuadasObservableList().add(0, new Transacoes(transaction)));
                                case SELL -> Platform.runLater(() -> {
                                    pintarLabelNVitoriasNDerrotas(symbol, getQtdDerrotas()[symbol.getCod()].getValue() == 0);
                                    getNegociandoVol()[symbol.getCod()].setValue(false);
                                    comprarNovamente(symbol, new Transacoes(transaction));
                                });
                            }
                    } catch (Exception ex) {
                        if (!(ex instanceof NullPointerException) && !(ex instanceof IllegalStateException))
                            ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void comprarNovamente(SYMBOL symbol, Transacoes ultimaTransacao) {
        int digito = Integer.parseInt(getLastPriceProposal()[symbol.getCod()].getValue().getBarrier());
        boolean mudarTodasStakes = false;
        try {
            if (getQtdDerrotas()[symbol.getCod()].getValue() > 0) {
                mudarTodasStakes = true;
                BigDecimal newStake = BigDecimal.ZERO, oldStake = BigDecimal.ZERO;
                oldStake = getStakeContrato()[symbol.getCod()].getValue();
                newStake = oldStake.add(oldStake.multiply(getFatorMartingale()));
                getStakeContrato()[symbol.getCod()].setValue(newStake);
//                getStakeContrato()[symbol.getCod()].setValue(
//                        getStakeContrato()[symbol.getCod()].getValue()
//                                .add(getStakeContrato()[symbol.getCod()].getValue().multiply(fatorMartingaleProperty().getValue())));
            } else {
                if (getStakeContrato()[symbol.getCod()].getValue().compareTo(getStakePadrao()[symbol.getCod()].getValue()) != 0) {
                    getStakeContrato()[symbol.getCod()].setValue(getStakePadrao()[symbol.getCod()].getValue());
                    mudarTodasStakes = true;
                }
            }
            gerarContratos(symbol, String.valueOf(digito), mudarTodasStakes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean carregarInformacoesGraficos() throws Exception {
        Platform.runLater(() -> {
            for (SYMBOL symbol : SYMBOL.values()) {
                try {
                    for (int i = 0; i < 10; i++) {
                        getGrafVolatilidade_R()[symbol.getCod()].getData().add(getGrafListDataDigitos_R()[symbol.getCod()].get(i));
                        getGrafVolatilidade_R()[symbol.getCod()].setName(String.valueOf(i));

                        displayLabelForData(
                                getGrafListDataDigitos_R()[symbol.getCod()].get(i),
                                getGrafTxtDigito_R()[symbol.getCod()][i]);
                    }
                } catch (Exception ex) {
                    if (!(ex instanceof ArrayIndexOutOfBoundsException))
                        ex.printStackTrace();
                }
            }
        });

        solicitarHistoricoTicks();
        return true;
    }

    private boolean minimaQtdVolatilidadeAtiva() {
        for (SYMBOL symbol : SYMBOL.values())
            if (getVolatilidadeAtivada()[symbol.getCod()].getValue())
                return true;
        return false;
    }

    private boolean gerarContratos() throws Exception {
        for (SYMBOL symbol : SYMBOL.values()) {
            getStakePadrao()[symbol.getCod()].setValue(new BigDecimal(getTxtValorStake().getText()));
            getStakeContrato()[symbol.getCod()].setValue(getStakePadrao()[symbol.getCod()].getValue());

            String digitos = getCboEstrategia().getSelectionModel().getSelectedItem().toString().replaceAll("\\D", "");
            gerarContratos(symbol, digitos, true);
        }
        return true;
    }

    private boolean gerarContratos(SYMBOL symbol, String digitos, boolean todas) throws Exception {
        if (getVolatilidadeAtivada()[symbol.getCod()].getValue()) {
            String numeroNaEstrategia = getCboEstrategia().getSelectionModel().getSelectedItem().toString().replaceAll("\\D", "");
            int digito = (!digitos.equals("")
                    ? (!numeroNaEstrategia.equals("") ? Integer.valueOf(numeroNaEstrategia) : -1)
                    : -1);
            switch (getCboEstrategia().getSelectionModel().getSelectedItem()) {
                case DIGITOVER1, DIGITOVER2, DIGITOVER3, DIGITOVER6, DIGITOVER8 -> {
                    gerarContrato(symbol, CONTRAC_TYPE.DIGITOVER, digito);
                }
                case DIGITUNDER1, DIGITUNDER3, DIGITUNDER6, DIGITUNDER7, DIGITUNDER8 -> {
                    gerarContrato(symbol, CONTRAC_TYPE.DIGITOVER, digito);
                }
                case DIGITUNDER3OVER6, DIGITUNDER7OVER2 -> {
                    if (getCboEstrategia().getSelectionModel().getSelectedItem() == ESTRATEGIAS.DIGITUNDER3OVER6)
                        fatorMartingaleProperty().setValue(new BigDecimal(0.5));
                    if (getCboEstrategia().getSelectionModel().getSelectedItem() == ESTRATEGIAS.DIGITUNDER7OVER2)
                        fatorMartingaleProperty().setValue(new BigDecimal(2));

                    if (todas) {
                        String estrategia = getCboEstrategia().getSelectionModel().getSelectedItem().name();
                        int quebra = estrategia.indexOf("OVER");
                        gerarContrato(symbol, CONTRAC_TYPE.DIGITUNDER,
                                Integer.valueOf(estrategia.substring(quebra - 1, estrategia.length() - 1).replaceAll("\\D", "")));

                        gerarContrato(symbol, CONTRAC_TYPE.DIGITOVER,
                                Integer.valueOf(estrategia.substring(quebra).replaceAll("\\D", "")));
                    } else {
                        gerarContrato(symbol, getLastPriceProposal()[symbol.getCod()].getValue().getContract_type(),
                                Integer.parseInt(getLastPriceProposal()[symbol.getCod()].getValue().getBarrier()));
                    }
                }
                case DIGITODD -> {
                    fatorMartingaleProperty().setValue(new BigDecimal(1));
                    gerarContrato(symbol, CONTRAC_TYPE.DIGITODD, 1);
                }
                case DIGITEVEN -> {
                    fatorMartingaleProperty().setValue(new BigDecimal(1));
                    gerarContrato(symbol, CONTRAC_TYPE.DIGITEVEN, 0);
                }
                case DIGITODDEVEN -> {
                    fatorMartingaleProperty().setValue(new BigDecimal(1));
                    if (todas) {
                        gerarContrato(symbol, CONTRAC_TYPE.DIGITODD, 1);
                        gerarContrato(symbol, CONTRAC_TYPE.DIGITEVEN, 0);
                    } else {
                        gerarContrato(symbol, getLastPriceProposal()[symbol.getCod()].getValue().getContract_type(),
                                getLastPriceProposal()[symbol.getCod()].getValue().getContract_type() == CONTRAC_TYPE.DIGITODD
                                        ? 1 : 0);
                    }
                }
                case DIGITDIFF -> {
                    fatorMartingaleProperty().setValue(new BigDecimal(11.15));
                    if (getQtdDerrotas()[symbol.getCod()].getValue() >= 2) {
                        getBtnPausar().fire();
                        switch (symbol) {
                            case R_10 -> {
                                getBtnPausar_R10().fire();
                                getLblNExecucoes_R10().setStyle("-fx-background-color: red");
                                getLblNVitorias_R10().setStyle("-fx-background-color: red");
                                getLblNDerrotas_R10().setStyle("-fx-background-color: red");
                            }
                            case R_25 -> {
                                getBtnPausar_R25().fire();
                                getLblNExecucoes_R25().setStyle("-fx-background-color: red");
                                getLblNVitorias_R25().setStyle("-fx-background-color: red");
                                getLblNDerrotas_R25().setStyle("-fx-background-color: red");
                            }
                            case R_50 -> {
                                getBtnPausar_R50().fire();
                                getLblNExecucoes_R50().setStyle("-fx-background-color: red");
                                getLblNVitorias_R50().setStyle("-fx-background-color: red");
                                getLblNDerrotas_R50().setStyle("-fx-background-color: red");
                            }
                            case R_75 -> {
                                getBtnPausar_R75().fire();
                                getLblNExecucoes_R75().setStyle("-fx-background-color: red");
                                getLblNVitorias_R75().setStyle("-fx-background-color: red");
                                getLblNDerrotas_R75().setStyle("-fx-background-color: red");
                            }
                            case R_100 -> {
                                getBtnPausar_R100().fire();
                                getLblNExecucoes_R100().setStyle("-fx-background-color: red");
                                getLblNVitorias_R100().setStyle("-fx-background-color: red");
                                getLblNDerrotas_R100().setStyle("-fx-background-color: red");
                            }
                        }
                        return false;
                    }
                    if (todas) {
                        for (int i = 0; i < 10; i++) {
                            gerarContrato(symbol, CONTRAC_TYPE.DIGITDIFF, i);
                        }
                    } else {
                        gerarContrato(symbol, getLastPriceProposal()[symbol.getCod()].getValue().getContract_type(),
                                Integer.parseInt(getLastPriceProposal()[symbol.getCod()].getValue().getBarrier()));
                    }
                }
            }
        }
        return true;
    }

    private boolean gerarContrato(SYMBOL symbol, CONTRAC_TYPE contrac_type, int digito) throws Exception {
        if (!getVolatilidadeAtivada()[symbol.getCod()].getValue()) return false;
        getPriceProposal()[symbol.getCod()][digito].setValue(new PriceProposal());
        getPriceProposal()[symbol.getCod()][digito].getValue().setProposal(1);
        getPriceProposal()[symbol.getCod()][digito].getValue().setAmount(getStakeContrato()[symbol.getCod()].getValue()
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        getPriceProposal()[symbol.getCod()][digito].getValue().setBarrier(String.valueOf(digito));
        getPriceProposal()[symbol.getCod()][digito].getValue().setBasis("stake");
        getPriceProposal()[symbol.getCod()][digito].getValue().setContract_type(contrac_type);
        getPriceProposal()[symbol.getCod()][digito].getValue().setCurrency("USD");
        getPriceProposal()[symbol.getCod()][digito].getValue().setDuration(Integer.valueOf(getTxtDuracaoTicks().getText().replaceAll("\\D", "")));
        getPriceProposal()[symbol.getCod()][digito].getValue().setDuration_unit(getCboDuracaoTipo().getSelectionModel().getSelectedItem());
        getPriceProposal()[symbol.getCod()][digito].getValue().setSymbol(symbol);
        enviarContrato(symbol, getPriceProposal()[symbol.getCod()][digito].getValue());
        return true;
    }

    private boolean enviarContrato(SYMBOL symbol, PriceProposal priceProposal) throws Exception {
        if (!getVolatilidadeAtivada()[symbol.getCod()].getValue()) return false;
        String jsonPriceProposal = UtilJson.getJsonFromObject(priceProposal);
        switch (priceProposal.getContract_type()) {
            case DIGITODD, DIGITEVEN, CALL, PUT -> jsonPriceProposal = jsonPriceProposal.replace("\"barrier\":\"1\",", "");
        }
        ws_ClientProperty().getValue().getMyWebSocket().send(jsonPriceProposal);
        return true;
    }

    private void comprarContrato(SYMBOL symbol, int digito) throws Exception {
        if (!getVolatilidadeAtivada()[symbol.getCod()].getValue()
                || !getCompraAutorizada()[symbol.getCod()].getValue()
                || getNegociandoVol()[symbol.getCod()].getValue()) return;

        getNegociandoVol()[symbol.getCod()].setValue(true);
        System.out.printf("%s__comprei diff de: %s\n", symbol.getDescricao(), digito);
        getLastPriceProposal()[symbol.getCod()].setValue(getPriceProposal()[symbol.getCod()][digito].getValue());
        String jsonBuyContract = UtilJson.getJsonFromObject(new BuyContract(getProposal()[symbol.getCod()][digito].getValue().getId()));
        ws_ClientProperty().getValue().getMyWebSocket().send(jsonBuyContract);

    }

    private void pintarLabelNVitoriasNDerrotas(SYMBOL symbol, boolean vitoria) {
        switch (symbol) {
            case R_10 -> {
                getLblNVitorias_R10().setStyle(vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
                getLblNDerrotas_R10().setStyle(!vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            }
            case R_25 -> {
                getLblNVitorias_R25().setStyle(vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
                getLblNDerrotas_R25().setStyle(!vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            }
            case R_50 -> {
                getLblNVitorias_R50().setStyle(vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
                getLblNDerrotas_R50().setStyle(!vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            }
            case R_75 -> {
                getLblNVitorias_R75().setStyle(vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
                getLblNDerrotas_R75().setStyle(!vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            }
            case R_100 -> {
                getLblNVitorias_R100().setStyle(vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
                getLblNDerrotas_R100().setStyle(!vitoria
                        ? styleLabelNExecucaoNegociandoProperty().getValue()
                        : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            }
        }
    }

    private void pintarNExecucao(SYMBOL symbol) {
        switch (symbol) {
            case R_10 -> getLblNExecucoes_R10().setStyle(getNegociandoVol()[symbol.getCod()].getValue()
                    ? styleLabelNExecucaoNegociandoProperty().getValue()
                    : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            case R_25 -> getLblNExecucoes_R25().setStyle(getNegociandoVol()[symbol.getCod()].getValue()
                    ? styleLabelNExecucaoNegociandoProperty().getValue()
                    : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            case R_50 -> getLblNExecucoes_R50().setStyle(getNegociandoVol()[symbol.getCod()].getValue()
                    ? styleLabelNExecucaoNegociandoProperty().getValue()
                    : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            case R_75 -> getLblNExecucoes_R75().setStyle(getNegociandoVol()[symbol.getCod()].getValue()
                    ? styleLabelNExecucaoNegociandoProperty().getValue()
                    : styleLabelNExecucaoNegociandoFalseProperty().getValue());
            case R_100 -> getLblNExecucoes_R100().setStyle(getNegociandoVol()[symbol.getCod()].getValue()
                    ? styleLabelNExecucaoNegociandoProperty().getValue()
                    : styleLabelNExecucaoNegociandoFalseProperty().getValue());
        }
    }

    /**
     *
     */

    private void displayLabelForData(XYChart.Data<String, Number> data, Text text) {
//        Platform.runLater(() -> {
        final Node node = data.getNode();
        ((Group) node.getParent()).getChildren().add(text);
        node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
            text.setLayoutX(
                    Math.round(bounds.getMinX() + bounds.getWidth() / 2 - text.prefWidth(-1) / 2));
            text.setLayoutY(Math.round(bounds.getMinY() - text.prefHeight(-2) * 0.5));
        });
//        });
    }

    /**
     * @return
     */


    /**
     * @return
     */


    private void estrategiaDIFF(SYMBOL symbol) {
        int qtdRepetido = Integer.valueOf(getTxtQtdRepeticoes().getText().replaceAll("\\D", ""));
        int digitoCompra = -1;
        boolean validado = false;
        int digito = getUltimoDigito()[symbol.getCod()].getValue();
        List<String> listDigitosBaixos =
                getGrafListDataDigitos_R()[symbol.getCod()].stream()
                        .filter(stringNumberData ->
                                stringNumberData.getYValue().intValue() == getGrafListDataDigitos_R()[symbol.getCod()].stream()
                                        .sorted(Comparator.comparingDouble(value -> value.getYValue().intValue()))
                                        .collect(Collectors.toList()).get(0).getYValue().intValue()
                        ).map(value -> value.getXValue())
                        .collect(Collectors.toList());
//        Random rand = new Random();

//        if (getChkAgressiva().isSelected()) {
//            if (qtdRepetido <= 4) {
//                for (int i = 1; i < qtdRepetido - 1; i++) {
//                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
//                    if (!validado)
//                        break;
//                }
//            } else {
//                for (int i = 1; i < qtdRepetido - 2; i++) {
//                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
//                    if (!validado)
//                        break;
//                }
//            }
//            if (validado && listDigitosBaixos.stream().filter(integer -> integer == digito).count() > 0) {
//                if (qtdRepetido > 2) {
//                    digitoCompra = digito;
//                } else {
//                    if (getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() < 6.)
//                        digitoCompra = digito;
//                    else
//                        validado = false;
//                }
//            } else {
//                validado = false;
//            }
//
//            if (getQtdDerrotas()[symbol.getCod()].getValue() == 0 && !validado) {
//                for (int i = 1; i < qtdRepetido; i++) {
//                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
//                    if (!validado)
//                        break;
//                }
//                if (qtdRepetido <= 3) {
//                    if (validado && getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() <= 16.)
//                        digitoCompra = digito;
//                } else {
//                    if (validado)
//                        digitoCompra = digito;
//                }
//            }
//        } else {
        Integer qtdRepeticoes = 0;
        if (getQtdDerrotas()[symbol.getCod()].getValue() == 0) {
            qtdRepeticoes = qtdRepetido;
        } else {
            qtdRepeticoes = qtdRepetido + 1;
        }
        String ultimosDigitosRepetidos = String.valueOf(digito);
        for (int i = 1; i < qtdRepeticoes; i++) {
            validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
            if (!validado)
                break;
            else
                ultimosDigitosRepetidos = ultimosDigitosRepetidos + ", " + getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito();
        }
        if (validado) {
            ultimosDigitosRepetidos = ultimosDigitosRepetidos + ";";
            System.out.printf("%s__validado:[%s]\tDigito:[%d]\tDigitos:[%s]" +
                            "\tporcDigito(%d):[%s]\tMenoresDigitos:%s%s\t\n",
                    symbol, validado, digito, ultimosDigitosRepetidos, digito,
                    getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue(),
                    listDigitosBaixos,
                    (listDigitosBaixos.stream().filter(s -> s.equals(String.valueOf(digito))).count() > 0)
                            ? "\tigual:[true]" : "");
        }
        if (validado && listDigitosBaixos.stream().filter(s -> s.equals(String.valueOf(digito))).count() > 0)
            if (getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() <= 7)
                digitoCompra = digito;

//        }
        if (digitoCompra >= 0) {
            try {
                comprarContrato(symbol, digitoCompra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void estrategiaDIFF_MG_PARC(SYMBOL symbol) {
        int qtdRepetido = Integer.valueOf(getTxtQtdRepeticoes().getText().replaceAll("\\D", ""));
        int digitoCompra = -1;
        boolean validado = true;
        int digito = getUltimoDigito()[symbol.getCod()].getValue();
        List<Integer> listDigitosBaixos =
                getGrafListDataDigitos_R()[symbol.getCod()].stream()
                        .filter(stringNumberData ->
                                stringNumberData.getYValue().intValue() == getGrafListDataDigitos_R()[symbol.getCod()].stream()
                                        .sorted(Comparator.comparingDouble(value -> value.getYValue().intValue()))
                                        .collect(Collectors.toList()).get(0).getYValue().intValue()
                        ).map(stringNumberData -> stringNumberData.getYValue().intValue())
                        .collect(Collectors.toList());
//        Random rand = new Random();

        if (getChkAgressiva().isSelected()) {
            if (qtdRepetido <= 4) {
                for (int i = 1; i < qtdRepetido - 1; i++) {
                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
                    if (!validado)
                        break;
                }
            } else {
                for (int i = 1; i < qtdRepetido - 2; i++) {
                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
                    if (!validado)
                        break;
                }
            }
            if (validado && listDigitosBaixos.stream().filter(integer -> integer == digito).count() > 0) {
                if (qtdRepetido > 2) {
                    digitoCompra = digito;
                } else {
                    if (getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() < 6.)
                        digitoCompra = digito;
                    else
                        validado = false;
                }
            } else {
                validado = false;
            }

            if (getQtdDerrotas()[symbol.getCod()].getValue() == 0 && !validado) {
                for (int i = 1; i < qtdRepetido; i++) {
                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
                    if (!validado)
                        break;
                }
                if (qtdRepetido <= 3) {
                    if (validado && getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() <= 16.)
                        digitoCompra = digito;
                } else {
                    if (validado)
                        digitoCompra = digito;
                }
            }
        } else {
            for (int i = 1; i < qtdRepetido; i++) {
                validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
                if (!validado)
                    break;
            }
//            if (validado && listDigitosBaixos.stream().filter(integer -> integer == digito).count() > 0) {
//                if (qtdRepetido > 2) {
//                    digitoCompra = digito;
//                } else {
//                    if (getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() < 6.)
//                        digitoCompra = digito;
//                    else
//                        validado = false;
//                }
//            } else {
//                validado = false;
//            }

            if (getQtdDerrotas()[symbol.getCod()].getValue() == 0 && !validado) {
                for (int i = 1; i < qtdRepetido - 1; i++) {
                    validado = (getHistoricoTicksObservableList()[symbol.getCod()].get(i).getUltimoDigito() == digito);
                    if (!validado)
                        break;
                }
//                if (qtdRepetido <= 3) {
//                    if (validado && getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() <= 16.)
//                        digitoCompra = digito;
//                } else {
//                    if (validado)
//                        digitoCompra = digito;
//                }
                if (validado && listDigitosBaixos.stream().filter(integer -> integer == digito).count() > 0) {
                    if (qtdRepetido > 2) {
                        digitoCompra = digito;
                    } else {
                        if (getGrafListDataDigitos_R()[symbol.getCod()].get(digito).getYValue().doubleValue() < 6.)
                            digitoCompra = digito;
                    }
                }
            }
        }
        if (digitoCompra >= 0) {
            try {
                comprarContrato(symbol, digitoCompra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void estrategiaDIGITUNDER3OVER6(SYMBOL symbol, int digito) {
        try {
//            getGrafListValorDigito_R()[symbol.getCod()].stream()
//                    .filter(integerProperty -> integerProperty.getValue() > 6)
//                    .mapToInt(value -> value.getValue()).average();
            double mediaOver = getGrafListDataDigitos_R()[symbol.getCod()].stream()
                    .filter(stringNumberData ->
                            Integer.valueOf(stringNumberData.getXValue())
                                    > 6)
                    .mapToDouble(value -> value.getYValue().doubleValue())
                    .average().orElse(0.);
            double mediaUnder = getGrafListDataDigitos_R()[symbol.getCod()].stream()
                    .filter(stringNumberData ->
                            Integer.valueOf(stringNumberData.getXValue())
                                    < 3)
                    .mapToDouble(value -> value.getYValue().doubleValue())
                    .average().orElse(0.);
            if (getChkAgressiva().isSelected()) {
                if (getQtdDerrotas()[symbol.getCod()].getValue() == 0) {
                    if (mediaOver > mediaUnder)
                        comprarContrato(symbol, 6);
                    else
                        comprarContrato(symbol, 3);
                } else {
                    if (mediaOver > 10)
                        comprarContrato(symbol, 6);
                    else if (mediaUnder > 10)
                        comprarContrato(symbol, 3);
                }
            } else {
                if (getQtdDerrotas()[symbol.getCod()].getValue() == 0) {
                    if (mediaOver >= 10 || mediaUnder >= 10) {
                        if (mediaOver > mediaUnder)
                            comprarContrato(symbol, 6);
                        else
                            comprarContrato(symbol, 3);
                    }
                } else {
                    if (mediaOver >= 12 || mediaUnder >= 12) {
                        if (mediaOver > mediaUnder)
                            comprarContrato(symbol, 6);
                        else
                            comprarContrato(symbol, 3);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * @return
     */


    public AnchorPane getPainelViewBinary() {
        return painelViewBinary;
    }

    public void setPainelViewBinary(AnchorPane painelViewBinary) {
        this.painelViewBinary = painelViewBinary;
    }

    public ComboBox<Tokens> getCboConta() {
        return cboConta;
    }

    public void setCboConta(ComboBox<Tokens> cboConta) {
        this.cboConta = cboConta;
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

    public TextField getTxtQtdRepeticoes() {
        return txtQtdRepeticoes;
    }

    public void setTxtQtdRepeticoes(TextField txtQtdRepeticoes) {
        this.txtQtdRepeticoes = txtQtdRepeticoes;
    }

    public TextField getTxtValorStake() {
        return txtValorStake;
    }

    public void setTxtValorStake(TextField txtValorStake) {
        this.txtValorStake = txtValorStake;
    }

    public Label getLblEstrategia() {
        return lblEstrategia;
    }

    public void setLblEstrategia(Label lblEstrategia) {
        this.lblEstrategia = lblEstrategia;
    }

    public ComboBox<ESTRATEGIAS> getCboEstrategia() {
        return cboEstrategia;
    }

    public void setCboEstrategia(ComboBox<ESTRATEGIAS> cboEstrategia) {
        this.cboEstrategia = cboEstrategia;
    }

    public CheckBox getChkAgressiva() {
        return chkAgressiva;
    }

    public void setChkAgressiva(CheckBox chkAgressiva) {
        this.chkAgressiva = chkAgressiva;
    }

    public ComboBox<Integer> getCboQtdTicksAnalisar() {
        return cboQtdTicksAnalisar;
    }

    public void setCboQtdTicksAnalisar(ComboBox<Integer> cboQtdTicksAnalisar) {
        this.cboQtdTicksAnalisar = cboQtdTicksAnalisar;
    }

    public Button getBtnContratos() {
        return btnContratos;
    }

    public void setBtnContratos(Button btnContratos) {
        this.btnContratos = btnContratos;
    }

    public Button getBtnPausar() {
        return btnPausar;
    }

    public void setBtnPausar(Button btnPausar) {
        this.btnPausar = btnPausar;
    }

    public Button getBtnIniciar() {
        return btnIniciar;
    }

    public void setBtnIniciar(Button btnIniciar) {
        this.btnIniciar = btnIniciar;
    }

    public Button getBtnStop() {
        return btnStop;
    }

    public void setBtnStop(Button btnStop) {
        this.btnStop = btnStop;
    }

    public Label getLblProprietarioConta() {
        return lblProprietarioConta;
    }

    public void setLblProprietarioConta(Label lblProprietarioConta) {
        this.lblProprietarioConta = lblProprietarioConta;
    }

    public Label getLblEmailConta() {
        return lblEmailConta;
    }

    public void setLblEmailConta(Label lblEmailConta) {
        this.lblEmailConta = lblEmailConta;
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

    public Label getLblIdConta() {
        return lblIdConta;
    }

    public void setLblIdConta(Label lblIdConta) {
        this.lblIdConta = lblIdConta;
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

    public Label getLblSaldoInicial() {
        return lblSaldoInicial;
    }

    public void setLblSaldoInicial(Label lblSaldoInicial) {
        this.lblSaldoInicial = lblSaldoInicial;
    }

    public Label getLblSaldoFinal() {
        return lblSaldoFinal;
    }

    public void setLblSaldoFinal(Label lblSaldoFinal) {
        this.lblSaldoFinal = lblSaldoFinal;
    }

    public Label getLblTotalApostado() {
        return lblTotalApostado;
    }

    public void setLblTotalApostado(Label lblTotalApostado) {
        this.lblTotalApostado = lblTotalApostado;
    }

    public Label getLblTotalPremio() {
        return lblTotalPremio;
    }

    public void setLblTotalPremio(Label lblTotalPremio) {
        this.lblTotalPremio = lblTotalPremio;
    }

    public Label getLblTotalLucro() {
        return lblTotalLucro;
    }

    public void setLblTotalLucro(Label lblTotalLucro) {
        this.lblTotalLucro = lblTotalLucro;
    }

    public TitledPane getTpn_R10() {
        return tpn_R10;
    }

    public void setTpn_R10(TitledPane tpn_R10) {
        this.tpn_R10 = tpn_R10;
    }

    public CheckBox getChkAtivo_R10() {
        return chkAtivo_R10;
    }

    public void setChkAtivo_R10(CheckBox chkAtivo_R10) {
        this.chkAtivo_R10 = chkAtivo_R10;
    }

    public CategoryAxis getxAxis_R10() {
        return xAxis_R10;
    }

    public void setxAxis_R10(CategoryAxis xAxis_R10) {
        this.xAxis_R10 = xAxis_R10;
    }

    public NumberAxis getyAxis_R10() {
        return yAxis_R10;
    }

    public void setyAxis_R10(NumberAxis yAxis_R10) {
        this.yAxis_R10 = yAxis_R10;
    }

    public BarChart<String, Number> getGraf_R10() {
        return graf_R10;
    }

    public void setGraf_R10(BarChart<String, Number> graf_R10) {
        this.graf_R10 = graf_R10;
    }

    public Label getLblLastTick_R10() {
        return lblLastTick_R10;
    }

    public void setLblLastTick_R10(Label lblLastTick_R10) {
        this.lblLastTick_R10 = lblLastTick_R10;
    }

    public Label getLblLegendaTick_R10() {
        return lblLegendaTick_R10;
    }

    public void setLblLegendaTick_R10(Label lblLegendaTick_R10) {
        this.lblLegendaTick_R10 = lblLegendaTick_R10;
    }

    public Button getBtnPausar_R10() {
        return btnPausar_R10;
    }

    public void setBtnPausar_R10(Button btnPausar_R10) {
        this.btnPausar_R10 = btnPausar_R10;
    }

    public Button getBtnComprar_R10() {
        return btnComprar_R10;
    }

    public void setBtnComprar_R10(Button btnComprar_R10) {
        this.btnComprar_R10 = btnComprar_R10;
    }

    public Button getBtnStop_R10() {
        return btnStop_R10;
    }

    public void setBtnStop_R10(Button btnStop_R10) {
        this.btnStop_R10 = btnStop_R10;
    }

    public Label getLblNExecucoes_R10() {
        return lblNExecucoes_R10;
    }

    public void setLblNExecucoes_R10(Label lblNExecucoes_R10) {
        this.lblNExecucoes_R10 = lblNExecucoes_R10;
    }

    public Label getLblNVitorias_R10() {
        return lblNVitorias_R10;
    }

    public void setLblNVitorias_R10(Label lblNVitorias_R10) {
        this.lblNVitorias_R10 = lblNVitorias_R10;
    }

    public Label getLblNDerrotas_R10() {
        return lblNDerrotas_R10;
    }

    public void setLblNDerrotas_R10(Label lblNDerrotas_R10) {
        this.lblNDerrotas_R10 = lblNDerrotas_R10;
    }

    public TableView<Transacoes> getTbvTransacoes_R10() {
        return tbvTransacoes_R10;
    }

    public void setTbvTransacoes_R10(TableView<Transacoes> tbvTransacoes_R10) {
        this.tbvTransacoes_R10 = tbvTransacoes_R10;
    }

    public Label getLblApostaTotal_R10() {
        return lblApostaTotal_R10;
    }

    public void setLblApostaTotal_R10(Label lblApostaTotal_R10) {
        this.lblApostaTotal_R10 = lblApostaTotal_R10;
    }

    public Label getLblPremioTotal_R10() {
        return lblPremioTotal_R10;
    }

    public void setLblPremioTotal_R10(Label lblPremioTotal_R10) {
        this.lblPremioTotal_R10 = lblPremioTotal_R10;
    }

    public Label getLblLucro_R10() {
        return lblLucro_R10;
    }

    public void setLblLucro_R10(Label lblLucro_R10) {
        this.lblLucro_R10 = lblLucro_R10;
    }

    public TitledPane getTpn_R25() {
        return tpn_R25;
    }

    public void setTpn_R25(TitledPane tpn_R25) {
        this.tpn_R25 = tpn_R25;
    }

    public CheckBox getChkAtivo_R25() {
        return chkAtivo_R25;
    }

    public void setChkAtivo_R25(CheckBox chkAtivo_R25) {
        this.chkAtivo_R25 = chkAtivo_R25;
    }

    public CategoryAxis getxAxis_R25() {
        return xAxis_R25;
    }

    public void setxAxis_R25(CategoryAxis xAxis_R25) {
        this.xAxis_R25 = xAxis_R25;
    }

    public NumberAxis getyAxis_R25() {
        return yAxis_R25;
    }

    public void setyAxis_R25(NumberAxis yAxis_R25) {
        this.yAxis_R25 = yAxis_R25;
    }

    public BarChart<String, Number> getGraf_R25() {
        return graf_R25;
    }

    public void setGraf_R25(BarChart<String, Number> graf_R25) {
        this.graf_R25 = graf_R25;
    }

    public Label getLblLastTick_R25() {
        return lblLastTick_R25;
    }

    public void setLblLastTick_R25(Label lblLastTick_R25) {
        this.lblLastTick_R25 = lblLastTick_R25;
    }

    public Label getLblLegendaTick_R25() {
        return lblLegendaTick_R25;
    }

    public void setLblLegendaTick_R25(Label lblLegendaTick_R25) {
        this.lblLegendaTick_R25 = lblLegendaTick_R25;
    }

    public Button getBtnPausar_R25() {
        return btnPausar_R25;
    }

    public void setBtnPausar_R25(Button btnPausar_R25) {
        this.btnPausar_R25 = btnPausar_R25;
    }

    public Button getBtnComprar_R25() {
        return btnComprar_R25;
    }

    public void setBtnComprar_R25(Button btnComprar_R25) {
        this.btnComprar_R25 = btnComprar_R25;
    }

    public Button getBtnStop_R25() {
        return btnStop_R25;
    }

    public void setBtnStop_R25(Button btnStop_R25) {
        this.btnStop_R25 = btnStop_R25;
    }

    public Label getLblNExecucoes_R25() {
        return lblNExecucoes_R25;
    }

    public void setLblNExecucoes_R25(Label lblNExecucoes_R25) {
        this.lblNExecucoes_R25 = lblNExecucoes_R25;
    }

    public Label getLblNVitorias_R25() {
        return lblNVitorias_R25;
    }

    public void setLblNVitorias_R25(Label lblNVitorias_R25) {
        this.lblNVitorias_R25 = lblNVitorias_R25;
    }

    public Label getLblNDerrotas_R25() {
        return lblNDerrotas_R25;
    }

    public void setLblNDerrotas_R25(Label lblNDerrotas_R25) {
        this.lblNDerrotas_R25 = lblNDerrotas_R25;
    }

    public TableView<Transacoes> getTbvTransacoes_R25() {
        return tbvTransacoes_R25;
    }

    public void setTbvTransacoes_R25(TableView<Transacoes> tbvTransacoes_R25) {
        this.tbvTransacoes_R25 = tbvTransacoes_R25;
    }

    public Label getLblApostaTotal_R25() {
        return lblApostaTotal_R25;
    }

    public void setLblApostaTotal_R25(Label lblApostaTotal_R25) {
        this.lblApostaTotal_R25 = lblApostaTotal_R25;
    }

    public Label getLblPremioTotal_R25() {
        return lblPremioTotal_R25;
    }

    public void setLblPremioTotal_R25(Label lblPremioTotal_R25) {
        this.lblPremioTotal_R25 = lblPremioTotal_R25;
    }

    public Label getLblLucro_R25() {
        return lblLucro_R25;
    }

    public void setLblLucro_R25(Label lblLucro_R25) {
        this.lblLucro_R25 = lblLucro_R25;
    }

    public TitledPane getTpn_R50() {
        return tpn_R50;
    }

    public void setTpn_R50(TitledPane tpn_R50) {
        this.tpn_R50 = tpn_R50;
    }

    public CheckBox getChkAtivo_R50() {
        return chkAtivo_R50;
    }

    public void setChkAtivo_R50(CheckBox chkAtivo_R50) {
        this.chkAtivo_R50 = chkAtivo_R50;
    }

    public CategoryAxis getxAxis_R50() {
        return xAxis_R50;
    }

    public void setxAxis_R50(CategoryAxis xAxis_R50) {
        this.xAxis_R50 = xAxis_R50;
    }

    public NumberAxis getyAxis_R50() {
        return yAxis_R50;
    }

    public void setyAxis_R50(NumberAxis yAxis_R50) {
        this.yAxis_R50 = yAxis_R50;
    }

    public BarChart<String, Number> getGraf_R50() {
        return graf_R50;
    }

    public void setGraf_R50(BarChart<String, Number> graf_R50) {
        this.graf_R50 = graf_R50;
    }

    public Label getLblLastTick_R50() {
        return lblLastTick_R50;
    }

    public void setLblLastTick_R50(Label lblLastTick_R50) {
        this.lblLastTick_R50 = lblLastTick_R50;
    }

    public Label getLblLegendaTick_R50() {
        return lblLegendaTick_R50;
    }

    public void setLblLegendaTick_R50(Label lblLegendaTick_R50) {
        this.lblLegendaTick_R50 = lblLegendaTick_R50;
    }

    public Button getBtnPausar_R50() {
        return btnPausar_R50;
    }

    public void setBtnPausar_R50(Button btnPausar_R50) {
        this.btnPausar_R50 = btnPausar_R50;
    }

    public Button getBtnComprar_R50() {
        return btnComprar_R50;
    }

    public void setBtnComprar_R50(Button btnComprar_R50) {
        this.btnComprar_R50 = btnComprar_R50;
    }

    public Button getBtnStop_R50() {
        return btnStop_R50;
    }

    public void setBtnStop_R50(Button btnStop_R50) {
        this.btnStop_R50 = btnStop_R50;
    }

    public Label getLblNExecucoes_R50() {
        return lblNExecucoes_R50;
    }

    public void setLblNExecucoes_R50(Label lblNExecucoes_R50) {
        this.lblNExecucoes_R50 = lblNExecucoes_R50;
    }

    public Label getLblNVitorias_R50() {
        return lblNVitorias_R50;
    }

    public void setLblNVitorias_R50(Label lblNVitorias_R50) {
        this.lblNVitorias_R50 = lblNVitorias_R50;
    }

    public Label getLblNDerrotas_R50() {
        return lblNDerrotas_R50;
    }

    public void setLblNDerrotas_R50(Label lblNDerrotas_R50) {
        this.lblNDerrotas_R50 = lblNDerrotas_R50;
    }

    public TableView<Transacoes> getTbvTransacoes_R50() {
        return tbvTransacoes_R50;
    }

    public void setTbvTransacoes_R50(TableView<Transacoes> tbvTransacoes_R50) {
        this.tbvTransacoes_R50 = tbvTransacoes_R50;
    }

    public Label getLblApostaTotal_R50() {
        return lblApostaTotal_R50;
    }

    public void setLblApostaTotal_R50(Label lblApostaTotal_R50) {
        this.lblApostaTotal_R50 = lblApostaTotal_R50;
    }

    public Label getLblPremioTotal_R50() {
        return lblPremioTotal_R50;
    }

    public void setLblPremioTotal_R50(Label lblPremioTotal_R50) {
        this.lblPremioTotal_R50 = lblPremioTotal_R50;
    }

    public Label getLblLucro_R50() {
        return lblLucro_R50;
    }

    public void setLblLucro_R50(Label lblLucro_R50) {
        this.lblLucro_R50 = lblLucro_R50;
    }

    public TitledPane getTpn_R75() {
        return tpn_R75;
    }

    public void setTpn_R75(TitledPane tpn_R75) {
        this.tpn_R75 = tpn_R75;
    }

    public CheckBox getChkAtivo_R75() {
        return chkAtivo_R75;
    }

    public void setChkAtivo_R75(CheckBox chkAtivo_R75) {
        this.chkAtivo_R75 = chkAtivo_R75;
    }

    public CategoryAxis getxAxis_R75() {
        return xAxis_R75;
    }

    public void setxAxis_R75(CategoryAxis xAxis_R75) {
        this.xAxis_R75 = xAxis_R75;
    }

    public NumberAxis getyAxis_R75() {
        return yAxis_R75;
    }

    public void setyAxis_R75(NumberAxis yAxis_R75) {
        this.yAxis_R75 = yAxis_R75;
    }

    public BarChart<String, Number> getGraf_R75() {
        return graf_R75;
    }

    public void setGraf_R75(BarChart<String, Number> graf_R75) {
        this.graf_R75 = graf_R75;
    }

    public Label getLblLastTick_R75() {
        return lblLastTick_R75;
    }

    public void setLblLastTick_R75(Label lblLastTick_R75) {
        this.lblLastTick_R75 = lblLastTick_R75;
    }

    public Label getLblLegendaTick_R75() {
        return lblLegendaTick_R75;
    }

    public void setLblLegendaTick_R75(Label lblLegendaTick_R75) {
        this.lblLegendaTick_R75 = lblLegendaTick_R75;
    }

    public Button getBtnPausar_R75() {
        return btnPausar_R75;
    }

    public void setBtnPausar_R75(Button btnPausar_R75) {
        this.btnPausar_R75 = btnPausar_R75;
    }

    public Button getBtnComprar_R75() {
        return btnComprar_R75;
    }

    public void setBtnComprar_R75(Button btnComprar_R75) {
        this.btnComprar_R75 = btnComprar_R75;
    }

    public Button getBtnStop_R75() {
        return btnStop_R75;
    }

    public void setBtnStop_R75(Button btnStop_R75) {
        this.btnStop_R75 = btnStop_R75;
    }

    public Label getLblNExecucoes_R75() {
        return lblNExecucoes_R75;
    }

    public void setLblNExecucoes_R75(Label lblNExecucoes_R75) {
        this.lblNExecucoes_R75 = lblNExecucoes_R75;
    }

    public Label getLblNVitorias_R75() {
        return lblNVitorias_R75;
    }

    public void setLblNVitorias_R75(Label lblNVitorias_R75) {
        this.lblNVitorias_R75 = lblNVitorias_R75;
    }

    public Label getLblNDerrotas_R75() {
        return lblNDerrotas_R75;
    }

    public void setLblNDerrotas_R75(Label lblNDerrotas_R75) {
        this.lblNDerrotas_R75 = lblNDerrotas_R75;
    }

    public TableView<Transacoes> getTbvTransacoes_R75() {
        return tbvTransacoes_R75;
    }

    public void setTbvTransacoes_R75(TableView<Transacoes> tbvTransacoes_R75) {
        this.tbvTransacoes_R75 = tbvTransacoes_R75;
    }

    public Label getLblApostaTotal_R75() {
        return lblApostaTotal_R75;
    }

    public void setLblApostaTotal_R75(Label lblApostaTotal_R75) {
        this.lblApostaTotal_R75 = lblApostaTotal_R75;
    }

    public Label getLblPremioTotal_R75() {
        return lblPremioTotal_R75;
    }

    public void setLblPremioTotal_R75(Label lblPremioTotal_R75) {
        this.lblPremioTotal_R75 = lblPremioTotal_R75;
    }

    public Label getLblLucro_R75() {
        return lblLucro_R75;
    }

    public void setLblLucro_R75(Label lblLucro_R75) {
        this.lblLucro_R75 = lblLucro_R75;
    }

    public TitledPane getTpn_R100() {
        return tpn_R100;
    }

    public void setTpn_R100(TitledPane tpn_R100) {
        this.tpn_R100 = tpn_R100;
    }

    public CheckBox getChkAtivo_R100() {
        return chkAtivo_R100;
    }

    public void setChkAtivo_R100(CheckBox chkAtivo_R100) {
        this.chkAtivo_R100 = chkAtivo_R100;
    }

    public CategoryAxis getxAxis_R100() {
        return xAxis_R100;
    }

    public void setxAxis_R100(CategoryAxis xAxis_R100) {
        this.xAxis_R100 = xAxis_R100;
    }

    public NumberAxis getyAxis_R100() {
        return yAxis_R100;
    }

    public void setyAxis_R100(NumberAxis yAxis_R100) {
        this.yAxis_R100 = yAxis_R100;
    }

    public BarChart<String, Number> getGraf_R100() {
        return graf_R100;
    }

    public void setGraf_R100(BarChart<String, Number> graf_R100) {
        this.graf_R100 = graf_R100;
    }

    public Label getLblLastTick_R100() {
        return lblLastTick_R100;
    }

    public void setLblLastTick_R100(Label lblLastTick_R100) {
        this.lblLastTick_R100 = lblLastTick_R100;
    }

    public Label getLblLegendaTick_R100() {
        return lblLegendaTick_R100;
    }

    public void setLblLegendaTick_R100(Label lblLegendaTick_R100) {
        this.lblLegendaTick_R100 = lblLegendaTick_R100;
    }

    public Button getBtnPausar_R100() {
        return btnPausar_R100;
    }

    public void setBtnPausar_R100(Button btnPausar_R100) {
        this.btnPausar_R100 = btnPausar_R100;
    }

    public Button getBtnComprar_R100() {
        return btnComprar_R100;
    }

    public void setBtnComprar_R100(Button btnComprar_R100) {
        this.btnComprar_R100 = btnComprar_R100;
    }

    public Button getBtnStop_R100() {
        return btnStop_R100;
    }

    public void setBtnStop_R100(Button btnStop_R100) {
        this.btnStop_R100 = btnStop_R100;
    }

    public Label getLblNExecucoes_R100() {
        return lblNExecucoes_R100;
    }

    public void setLblNExecucoes_R100(Label lblNExecucoes_R100) {
        this.lblNExecucoes_R100 = lblNExecucoes_R100;
    }

    public Label getLblNVitorias_R100() {
        return lblNVitorias_R100;
    }

    public void setLblNVitorias_R100(Label lblNVitorias_R100) {
        this.lblNVitorias_R100 = lblNVitorias_R100;
    }

    public Label getLblNDerrotas_R100() {
        return lblNDerrotas_R100;
    }

    public void setLblNDerrotas_R100(Label lblNDerrotas_R100) {
        this.lblNDerrotas_R100 = lblNDerrotas_R100;
    }

    public TableView<Transacoes> getTbvTransacoes_R100() {
        return tbvTransacoes_R100;
    }

    public void setTbvTransacoes_R100(TableView<Transacoes> tbvTransacoes_R100) {
        this.tbvTransacoes_R100 = tbvTransacoes_R100;
    }

    public Label getLblApostaTotal_R100() {
        return lblApostaTotal_R100;
    }

    public void setLblApostaTotal_R100(Label lblApostaTotal_R100) {
        this.lblApostaTotal_R100 = lblApostaTotal_R100;
    }

    public Label getLblPremioTotal_R100() {
        return lblPremioTotal_R100;
    }

    public void setLblPremioTotal_R100(Label lblPremioTotal_R100) {
        this.lblPremioTotal_R100 = lblPremioTotal_R100;
    }

    public Label getLblLucro_R100() {
        return lblLucro_R100;
    }

    public void setLblLucro_R100(Label lblLucro_R100) {
        this.lblLucro_R100 = lblLucro_R100;
    }

    public static LocalDateTime getLdtHoraInicio() {
        return ldtHoraInicio.get();
    }

    public static ObjectProperty<LocalDateTime> ldtHoraInicioProperty() {
        return ldtHoraInicio;
    }

    public static void setLdtHoraInicio(LocalDateTime ldtHoraInicio) {
        Operacoes.ldtHoraInicio.set(ldtHoraInicio);
    }

    public static LocalDateTime getLdtHoraAtual() {
        return ldtHoraAtual.get();
    }

    public static ObjectProperty<LocalDateTime> ldtHoraAtualProperty() {
        return ldtHoraAtual;
    }

    public static void setLdtHoraAtual(LocalDateTime ldtHoraAtual) {
        Operacoes.ldtHoraAtual.set(ldtHoraAtual);
    }

    public static LocalTime getLdtTempUso() {
        return ldtTempUso.get();
    }

    public static ObjectProperty<LocalTime> ldtTempUsoProperty() {
        return ldtTempUso;
    }

    public static void setLdtTempUso(LocalTime ldtTempUso) {
        Operacoes.ldtTempUso.set(ldtTempUso);
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

    public static BigDecimal getFatorMartingale() {
        return fatorMartingale.get();
    }

    public static ObjectProperty<BigDecimal> fatorMartingaleProperty() {
        return fatorMartingale;
    }

    public static void setFatorMartingale(BigDecimal fatorMartingale) {
        Operacoes.fatorMartingale.set(fatorMartingale);
    }

    public static Authorize getAuthorizeObject() {
        return authorizeObject.get();
    }

    public static ObjectProperty<Authorize> authorizeObjectProperty() {
        return authorizeObject;
    }

    public static void setAuthorizeObject(Authorize authorizeObject) {
        Operacoes.authorizeObject.set(authorizeObject);
    }

    public static String getTokenDeAutorizacao() {
        return tokenDeAutorizacao.get();
    }

    public static StringProperty tokenDeAutorizacaoProperty() {
        return tokenDeAutorizacao;
    }

    public static void setTokenDeAutorizacao(String tokenDeAutorizacao) {
        Operacoes.tokenDeAutorizacao.set(tokenDeAutorizacao);
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

    public static ObservableList<Transacoes> getTransacoesEfetuadasObservableList() {
        return transacoesEfetuadasObservableList;
    }

    public static void setTransacoesEfetuadasObservableList(ObservableList<Transacoes> transacoesEfetuadasObservableList) {
        Operacoes.transacoesEfetuadasObservableList = transacoesEfetuadasObservableList;
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

    public static IntegerProperty[] getQtdDerrotas() {
        return qtdDerrotas;
    }

    public static void setQtdDerrotas(IntegerProperty[] qtdDerrotas) {
        Operacoes.qtdDerrotas = qtdDerrotas;
    }

    public static ObjectProperty<BigDecimal>[] getVlrPerdas() {
        return vlrPerdas;
    }

    public static void setVlrPerdas(ObjectProperty<BigDecimal>[] vlrPerdas) {
        Operacoes.vlrPerdas = vlrPerdas;
    }

    public static BooleanProperty[] getAutorizado() {
        return autorizado;
    }

    public static void setAutorizado(BooleanProperty[] autorizado) {
        Operacoes.autorizado = autorizado;
    }

    public static BooleanProperty[] getVolatilidadeAtivada() {
        return volatilidadeAtivada;
    }

    public static void setVolatilidadeAtivada(BooleanProperty[] volatilidadeAtivada) {
        Operacoes.volatilidadeAtivada = volatilidadeAtivada;
    }

    public static BooleanProperty[] getCompraAutorizada() {
        return compraAutorizada;
    }

    public static void setCompraAutorizada(BooleanProperty[] compraAutorizada) {
        Operacoes.compraAutorizada = compraAutorizada;
    }

    public static BooleanProperty[] getNegociandoVol() {
        return negociandoVol;
    }

    public static void setNegociandoVol(BooleanProperty[] negociandoVol) {
        Operacoes.negociandoVol = negociandoVol;
    }

    public static boolean isWs_Conectado() {
        return ws_Conectado.get();
    }

    public static BooleanProperty ws_ConectadoProperty() {
        return ws_Conectado;
    }

    public static void setWs_Conectado(boolean ws_Conectado) {
        Operacoes.ws_Conectado.set(ws_Conectado);
    }

    public static WSClient getWs_Client() {
        return ws_Client.get();
    }

    public static ObjectProperty<WSClient> ws_ClientProperty() {
        return ws_Client;
    }

    public static void setWs_Client(WSClient ws_Client) {
        Operacoes.ws_Client.set(ws_Client);
    }

    public static ObjectProperty<PriceProposal>[][] getPriceProposal() {
        return priceProposal;
    }

    public static void setPriceProposal(ObjectProperty<PriceProposal>[][] priceProposal) {
        Operacoes.priceProposal = priceProposal;
    }

    public static ObjectProperty<PriceProposal>[] getLastPriceProposal() {
        return lastPriceProposal;
    }

    public static void setLastPriceProposal(ObjectProperty<PriceProposal>[] lastPriceProposal) {
        Operacoes.lastPriceProposal = lastPriceProposal;
    }

    public static ObjectProperty<Proposal>[][] getProposal() {
        return proposal;
    }

    public static void setProposal(ObjectProperty<Proposal>[][] proposal) {
        Operacoes.proposal = proposal;
    }

    public static ObservableList<HistoricoTicks>[] getHistoricoTicksObservableList() {
        return historicoTicksObservableList;
    }

    public static void setHistoricoTicksObservableList(ObservableList<HistoricoTicks>[] historicoTicksObservableList) {
        Operacoes.historicoTicksObservableList = historicoTicksObservableList;
    }

    public static BooleanProperty[] getTickSubindo() {
        return tickSubindo;
    }

    public static void setTickSubindo(BooleanProperty[] tickSubindo) {
        Operacoes.tickSubindo = tickSubindo;
    }

    public static ObservableList<Transaction>[] getTransactionObservableList() {
        return transactionObservableList;
    }

    public static void setTransactionObservableList(ObservableList<Transaction>[] transactionObservableList) {
        Operacoes.transactionObservableList = transactionObservableList;
    }

    public static StringProperty[] getUltimoTick() {
        return ultimoTick;
    }

    public static void setUltimoTick(StringProperty[] ultimoTick) {
        Operacoes.ultimoTick = ultimoTick;
    }

    public static IntegerProperty[] getUltimoDigito() {
        return ultimoDigito;
    }

    public static void setUltimoDigito(IntegerProperty[] ultimoDigito) {
        Operacoes.ultimoDigito = ultimoDigito;
    }

    public static IntegerProperty[] getDigitoMaiorQuantidade() {
        return digitoMaiorQuantidade;
    }

    public static void setDigitoMaiorQuantidade(IntegerProperty[] digitoMaiorQuantidade) {
        Operacoes.digitoMaiorQuantidade = digitoMaiorQuantidade;
    }

    public static IntegerProperty[] getDigitoMenorQuantidade() {
        return digitoMenorQuantidade;
    }

    public static void setDigitoMenorQuantidade(IntegerProperty[] digitoMenorQuantidade) {
        Operacoes.digitoMenorQuantidade = digitoMenorQuantidade;
    }

    public XYChart.Series<String, Number>[] getGrafVolatilidade_R() {
        return grafVolatilidade_R;
    }

    public void setGrafVolatilidade_R(XYChart.Series<String, Number>[] grafVolatilidade_R) {
        this.grafVolatilidade_R = grafVolatilidade_R;
    }

    public ObservableList<Data<String, Number>>[] getGrafListDataDigitos_R() {
        return grafListDataDigitos_R;
    }

    public void setGrafListDataDigitos_R(ObservableList<Data<String, Number>>[] grafListDataDigitos_R) {
        this.grafListDataDigitos_R = grafListDataDigitos_R;
    }

    public ObservableList<IntegerProperty>[] getGrafListValorDigito_R() {
        return grafListValorDigito_R;
    }

    public void setGrafListValorDigito_R(ObservableList<IntegerProperty>[] grafListValorDigito_R) {
        this.grafListValorDigito_R = grafListValorDigito_R;
    }

    public Text[][] getGrafTxtDigito_R() {
        return grafTxtDigito_R;
    }

    public void setGrafTxtDigito_R(Text[][] grafTxtDigito_R) {
        this.grafTxtDigito_R = grafTxtDigito_R;
    }

    public TmodelTransacoes[] getTmodelTransacoes() {
        return tmodelTransacoes;
    }

    public void setTmodelTransacoes(TmodelTransacoes[] tmodelTransacoes) {
        this.tmodelTransacoes = tmodelTransacoes;
    }

    public static String getTransactionsAuthorizeds() {
        return transactionsAuthorizeds.get();
    }

    public static StringProperty transactionsAuthorizedsProperty() {
        return transactionsAuthorizeds;
    }

    public static void setTransactionsAuthorizeds(String transactionsAuthorizeds) {
        Operacoes.transactionsAuthorizeds.set(transactionsAuthorizeds);
    }

    public Label getLblLegendaNExecucoes() {
        return lblLegendaNExecucoes;
    }

    public void setLblLegendaNExecucoes(Label lblLegendaNExecucoes) {
        this.lblLegendaNExecucoes = lblLegendaNExecucoes;
    }

    public static ObjectProperty<Buy>[][] getBuy() {
        return buy;
    }

    public static void setBuy(ObjectProperty<Buy>[][] buy) {
        Operacoes.buy = buy;
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

    public Label getLblMaxLucroMoeda() {
        return lblMaxLucroMoeda;
    }

    public void setLblMaxLucroMoeda(Label lblMaxLucroMoeda) {
        this.lblMaxLucroMoeda = lblMaxLucroMoeda;
    }

    public Label getLblMaxLucroPorcentagem() {
        return lblMaxLucroPorcentagem;
    }

    public void setLblMaxLucroPorcentagem(Label lblMaxLucroPorcentagem) {
        this.lblMaxLucroPorcentagem = lblMaxLucroPorcentagem;
    }

    public Label getLblMaxPrejuizoMoeda() {
        return lblMaxPrejuizoMoeda;
    }

    public void setLblMaxPrejuizoMoeda(Label lblMaxPrejuizoMoeda) {
        this.lblMaxPrejuizoMoeda = lblMaxPrejuizoMoeda;
    }

    public Label getLblMaxPrejuizoPorcentagem() {
        return lblMaxPrejuizoPorcentagem;
    }

    public void setLblMaxPrejuizoPorcentagem(Label lblMaxPrejuizoPorcentagem) {
        this.lblMaxPrejuizoPorcentagem = lblMaxPrejuizoPorcentagem;
    }
}
