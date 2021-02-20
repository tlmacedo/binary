package br.com.tlmacedo.binary.controller;

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
    static final ObservableList<ContaToken> TOKEN_OBSERVABLE_LIST =
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
    static BooleanProperty[] tickSubindo = new BooleanProperty[getSymbolObservableList().size()];
    static ObjectProperty<Tick>[] ultimoTick = new ObjectProperty[getSymbolObservableList().size()];
    static ObjectProperty<Ohlc>[] ultimoCandle = new ObjectProperty[getSymbolObservableList().size()];
    //    static IntegerProperty[] maiorQtdDigito = new IntegerProperty[5];
//    static IntegerProperty[] menorQtdDigito = new IntegerProperty[5];
//    static StringProperty[] informacaoDetalhe01 = new StringProperty[5];
//    static StringProperty[] informacaoValor01 = new StringProperty[5];
//    static StringProperty[] informacaoDetalhe02 = new StringProperty[5];
//    static StringProperty[] informacaoValor02 = new StringProperty[5];
    //** Listas **
//    static ObservableList<HistoricoDeTicks>[] historicoDeTicks_TempObservableList = new ObservableList[getSymbolObservableList().size()];
//    static ObservableList<HistoricoDeTicks>[] historicoDeTicksAnalise_TempObservableList = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeTicks>[] historicoDeTicksObservableList = new ObservableList[getSymbolObservableList().size()];
    static ObservableList<HistoricoDeCandles>[] historicoDeCandlesObservableList = new ObservableList[getSymbolObservableList().size()];
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


    // Operações 01
    public TitledPane tpn_Op01;
    public Label lblInf01_Op01;
    public Label lblVlrInf01_Op01;
    public Label lblPorcInf01_Op01;
    public Label lblInf02_Op01;
    public Label lblVlrInf02_Op01;
    public Label lblPorcInf02_Op01;
    public Label lblTickUltimo_Op01;
    public Label lblLegendaTickUltimo_Op01;
    public Button btnContrato_Op01;
    public Button btnPausar_Op01;
    public Button btnStop_Op01;
    public Label lblInvestido_Op01;
    public Label lblPremiacao_Op01;
    public Label lblLucro_Op01;
    public TableView<Transacoes> tbvTransacoes_Op01;
    public Label tpnLblLegendaExecucoes_Op01;
    public Label tpnLblExecucoes_Op01;
    public Label tpnLblVitorias_Op01;
    public Label tpnLblDerrotas_Op01;


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

        for (int i = 0; i < getSymbolObservableList().size(); i++) {
            getHistoricoDeTicksObservableList()[i] = FXCollections.observableArrayList();
            getHistoricoDeCandlesObservableList()[i] = FXCollections.observableArrayList();
            getUltimoTick()[i] = new SimpleObjectProperty<>();
            getUltimoCandle()[i] = new SimpleObjectProperty<>();
        }

//        for (int symbol_id = 0; symbol_id < getSymbolObservableList().size(); symbol_id++) {
//            getHistoricoDeTicks_TempObservableList()[symbol_id] = FXCollections.observableArrayList();
//            getHistoricoDeTicksAnalise_TempObservableList()[symbol_id] = FXCollections.observableArrayList();
//        }
//
//        for (int operadorId = 0; operadorId < 5; operadorId++) {
//            getOperador()[operadorId] = new SimpleObjectProperty<>();
//            getOperadorAtivo()[operadorId] = new SimpleBooleanProperty(false);
//            getOperadorCompraAutorizada()[operadorId] = new SimpleBooleanProperty(false);
//            getOperadorNegociando()[operadorId] = new SimpleBooleanProperty(false);
//            getTickSubindo()[operadorId] = new SimpleBooleanProperty();
//            getUltimoTick()[operadorId] = new SimpleObjectProperty<>();
//            getMaiorQtdDigito()[operadorId] = new SimpleIntegerProperty();
//            getMenorQtdDigito()[operadorId] = new SimpleIntegerProperty();
//            getInformacaoDetalhe01()[operadorId] = new SimpleStringProperty();
//            getInformacaoValor01()[operadorId] = new SimpleStringProperty();
//            getInformacaoValor01()[operadorId] = new SimpleStringProperty();
//            getInformacaoValor02()[operadorId] = new SimpleStringProperty();
//
//            getHistoricoDeTicksObservableList()[operadorId] = FXCollections.observableArrayList();
//            getHistoricoDeTicksAnaliseObservableList()[operadorId] = FXCollections.observableArrayList();
//            getTransactionObservableList()[operadorId] = FXCollections.observableArrayList();
//
//            graficoEmBarras(operadorId);
//        }
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

        for (Symbol symbol : getSymbolObservableList()) {
//            solicitarTicks(symbol);
        }

    }

    private void conectarObjetosEmVariaveis() {

        getLblTickUltimo_Op01().textProperty().unbind();
//        System.out.printf("tickStyle: %s\n", getTickStyle());
        if (getTickStyle().equals(TICK_STYLE.TICKS))
            getLblTickUltimo_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getUltimoTick()[0].getValue() == null)
                    return "";
                return getUltimoTick()[0].getValue().toString();
            }, getUltimoTick()[0]));
        else if (getTickStyle().equals(TICK_STYLE.CANDLES))
            getLblTickUltimo_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
                if (getUltimoCandle()[0].getValue() == null)
                    return "";
                return getUltimoCandle()[0].getValue().toString();
            }, getUltimoCandle()[0]));


//        getLblTickUltimo_Op01().textProperty().unbind();
//        getLblTickUltimo_Op01().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (getUltimoTick()[0].getValue() == null)
//                return "";
//            return getUltimoTick()[0].getValue().toString();
//        }, getUltimoTick()[0]));
//        getLblTickUltimo_Op01().styleProperty().unbind();
//        getLblTickUltimo_Op01().styleProperty().bind(Bindings.createStringBinding(() -> {
////            if (getTickSubindo()[0].getValue() == null)
////                return STYLE_TICK_NEUTRO;
//            return getTickSubindo()[0].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
//        }, getTickSubindo()[0]));
//        getLblLegendaTickUltimo_Op01().styleProperty().unbind();
//        getLblLegendaTickUltimo_Op01().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getOperadorNegociando()[0].getValue() == null)
//                return null;
//            return getOperadorNegociando()[0].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
//        }, getOperadorNegociando()[0]));
//
//        getLblTickUltimo_Op02().textProperty().unbind();
//        getLblTickUltimo_Op02().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (getUltimoTick()[1].getValue() == null)
//                return "";
//            return getUltimoTick()[1].getValue().toString();
//        }, getUltimoTick()[1]));
//        getLblTickUltimo_Op02().styleProperty().unbind();
//        getLblTickUltimo_Op02().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getTickSubindo()[1].getValue() == null)
//                return null;
//            return getTickSubindo()[1].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
//        }, getTickSubindo()[1]));
//        getLblLegendaTickUltimo_Op02().styleProperty().unbind();
//        getLblLegendaTickUltimo_Op02().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getOperadorNegociando()[1].getValue() == null)
//                return null;
//            return getOperadorNegociando()[1].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
//        }, getOperadorNegociando()[1]));
//
//
//        getLblTickUltimo_Op03().textProperty().unbind();
//        getLblTickUltimo_Op03().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (getUltimoTick()[2].getValue() == null)
//                return "";
//            return getUltimoTick()[2].getValue().toString();
//        }, getUltimoTick()[2]));
//        getLblTickUltimo_Op03().styleProperty().unbind();
//        getLblTickUltimo_Op03().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getTickSubindo()[2].getValue() == null)
//                return null;
//            return getTickSubindo()[2].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
//        }, getTickSubindo()[2]));
//        getLblLegendaTickUltimo_Op03().styleProperty().unbind();
//        getLblLegendaTickUltimo_Op03().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getOperadorNegociando()[2].getValue() == null)
//                return null;
//            return getOperadorNegociando()[2].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
//        }, getOperadorNegociando()[2]));
//
//
//        getLblTickUltimo_Op04().textProperty().unbind();
//        getLblTickUltimo_Op04().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (getUltimoTick()[3].getValue() == null)
//                return "";
//            return getUltimoTick()[3].getValue().toString();
//        }, getUltimoTick()[3]));
//        getLblTickUltimo_Op04().styleProperty().unbind();
//        getLblTickUltimo_Op04().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getTickSubindo()[3].getValue() == null)
//                return null;
//            return getTickSubindo()[3].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
//        }, getTickSubindo()[3]));
//        getLblLegendaTickUltimo_Op04().styleProperty().unbind();
//        getLblLegendaTickUltimo_Op04().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getOperadorNegociando()[3].getValue() == null)
//                return null;
//            return getOperadorNegociando()[3].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
//        }, getOperadorNegociando()[3]));
//
//
//        getLblTickUltimo_Op05().textProperty().unbind();
//        getLblTickUltimo_Op05().textProperty().bind(Bindings.createStringBinding(() -> {
//            if (getUltimoTick()[4].getValue() == null)
//                return "";
//            return getUltimoTick()[4].getValue().toString();
//        }, getUltimoTick()[4]));
//        getLblTickUltimo_Op05().styleProperty().unbind();
//        getLblTickUltimo_Op05().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getTickSubindo()[4].getValue() == null)
//                return null;
//            return getTickSubindo()[4].getValue() ? STYLE_TICK_SUBINDO : STYLE_TICK_DESCENDO;
//        }, getTickSubindo()[4]));
//        getLblLegendaTickUltimo_Op05().styleProperty().unbind();
//        getLblLegendaTickUltimo_Op05().styleProperty().bind(Bindings.createStringBinding(() -> {
//            if (getOperadorNegociando()[4].getValue() == null)
//                return null;
//            return getOperadorNegociando()[4].getValue() ? STYLE_TICK_NEGOCIANDO : STYLE_TICK_NEGOCIANDO_FALSE;
//        }, getOperadorNegociando()[4]));

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
        getWsClientObjectProperty().getMyWebSocket().send(jsonActiveSymbols);

    }

    private void solicitarTicks() {

//        for (Symbol symbol : getSymbolObservableList()) {
        Symbol symbol;
        for (int z = 0; z < 1; z++) {
            symbol = getSymbolObservableList().get(z);

            Integer tempoVela = null;
            for (int i = 1; i <= 1; i++) {
                tempoVela = i * 60;
                String jsonHistory = Util_Json.getJson_from_Object(new TicksHistory(symbol.getSymbol(),
                        getCboQtdTicksGrafico().getValue(), getTickStyle(), tempoVela, null));
                if (tempoVela == null) jsonHistory = jsonHistory.replace(",\"granularity\":null", "");
                jsonHistory = jsonHistory.replace(",\"passthrough\":null", "");
                System.out.printf("jsonHistory: %s\n", jsonHistory);
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
     * Atualizações carregamentos e manipulações com graficos.
     * <p>
     * <p>
     * <p>
     * <p>
     */

//    private void graficoEmBarras(Integer operadorId) {
//
//        getGraficoBarrasListValoresDigitos_R()[operadorId] = FXCollections.observableArrayList();
//        getGraficoBarrasListQtdDigito_R()[operadorId] = FXCollections.observableArrayList();
//        getGraficoBarrasVolatilidade_R()[operadorId] = new XYChart.Series<>();
//
//        for (int digito = 0; digito < 10; digito++) {
//            getGraficoTxtLegendaDigito_R()[operadorId][digito] = new Text();
//            getGraficoTxtLegendaDigito_R()[operadorId][digito].setFont(Font.font("Arial", 10));
//            getGraficoTxtLegendaDigito_R()[operadorId][digito].setStyle("-fx-text-fill: #fff;");
//
//            getGraficoBarrasListQtdDigito_R()[operadorId].add(digito, new SimpleLongProperty(0));
//            getGraficoBarrasListValoresDigitos_R()[operadorId].add(new Data<>(String.valueOf(digito), 0));
//
//            int finalDigito = digito;
//            getGraficoBarrasListQtdDigito_R()[operadorId].get(digito).addListener((ov, o, n) -> {
//                if (n == null) return;
//                Double porcento = n.intValue() != 0
//                        ? (n.intValue() / (getGraficoQtdTicks() / 100.)) : 0.;
//                getGraficoBarrasListValoresDigitos_R()[operadorId].get(finalDigito).setYValue(porcento.intValue());
//                getGraficoTxtLegendaDigito_R()[operadorId][finalDigito].setText(String.format("%s%%", porcento.intValue()));
//            });
//        }
//
////        if (operadorId == 0) {
////            getyAxisBarras_Op01().setUpperBound(25);
////            getGraficoBarras_Op01().getData().add(getGraficoBarrasVolatilidade_R()[operadorId]);
////            getGraficoBarras_Op01().setVisible(true);
////        }
////        if (operadorId == 1) {
////            getyAxisBarras_Op02().setUpperBound(25);
////            getGraficoBarras_Op02().getData().add(getGraficoBarrasVolatilidade_R()[operadorId]);
////            getGraficoBarras_Op02().setVisible(true);
////        }
////        if (operadorId == 2) {
////            getyAxisBarras_Op03().setUpperBound(25);
////            getGraficoBarras_Op03().getData().add(getGraficoBarrasVolatilidade_R()[operadorId]);
////            getGraficoBarras_Op03().setVisible(true);
////        }
////        if (operadorId == 3) {
////            getyAxisBarras_Op04().setUpperBound(25);
////            getGraficoBarras_Op04().getData().add(getGraficoBarrasVolatilidade_R()[operadorId]);
////            getGraficoBarras_Op04().setVisible(true);
////        }
////        if (operadorId == 4) {
////            getyAxisBarras_Op05().setUpperBound(25);
////            getGraficoBarras_Op05().getData().add(getGraficoBarrasVolatilidade_R()[operadorId]);
////            getGraficoBarras_Op05().setVisible(true);
////        }
//
//    }
//    private void displayLabelForData(XYChart.Data<String, Number> data, Text text) {
//
//        Platform.runLater(() -> {
//            if (data == null || text == null) return;
//            final Node node = data.getNode();
//            if (node == null) return;
//            ((Group) node.getParent()).getChildren().add(text);
//            node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
//                text.setLayoutX(Math.round(bounds.getMinX() + bounds.getWidth() / 2 - 6.));
//                text.setLayoutY(Math.round(bounds.getMinY() - 12. * 0.5));
//                text.setFill(Color.WHITE);
//            });
//        });
//
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


//        for (int operadorId = 0; operadorId < 5; operadorId++) {
//            int finalOperadorId = operadorId;
//            getUltimoTick()[operadorId].addListener((ov, o, n) -> {
//                Platform.runLater(() -> {
//                    Map<Integer, Long> vlrDigitos = getHistoricoDeTicksObservableList()[finalOperadorId].stream()
//                            .map(HistoricoDeTicks::getUltimoDigito)
//                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
//                    getMenorQtdDigito()[finalOperadorId].setValue(Collections.min(vlrDigitos.values()));
//                    getMaiorQtdDigito()[finalOperadorId].setValue(Collections.max(vlrDigitos.values()));
//
//                    for (int digito = 0; digito < 10; digito++) {
//                        getGraficoBarrasListQtdDigito_R()[finalOperadorId].get(digito).setValue(
//                                vlrDigitos.containsKey(digito) ? vlrDigitos.get(digito) : 0L);
//                        if (!vlrDigitos.containsKey(digito))
//                            getMenorQtdDigito()[finalOperadorId].setValue(0);
//                    }
//
//                    if (getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].size() > 1)
//                        getTickSubindo()[finalOperadorId].setValue(
//                                getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].get(0).getPrice()
//                                        .compareTo(getHistoricoDeTicksAnaliseObservableList()[finalOperadorId].get(1).getPrice()) >= 0);
//
//                    //Transacoes transacoesTemp;
//                    //                    if (getTransacoesObservableList().size() > 0
//                    //                            && (transacoesTemp = getTransacoesObservableList().stream()
//                    //                            .filter(transacoes -> transacoes.getSymbol().getId() == symbolId + 1
//                    //                                    && (transacoes.getTickCompra() == null
//                    //                                    || transacoes.getTickVenda() == null))
//                    //                            .findFirst().orElse(null)) != null) {
//                    //                        int index = getTransacoesObservableList().indexOf(transacoesTemp);
//                    //                        try {
//                    //                            if (transacoesTemp.getTickCompra() == null) {
//                    //                                transacoesTemp.setTickCompra(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
//                    //                                        .filter(historicoDeTicks -> historicoDeTicks.getTime() >= transacoesTemp.getDataHoraCompra())
//                    //                                        .findFirst().get().getPrice());
//                    //                            } else {
//                    //                                String contrato = transacoesTemp.getContract_type().toLowerCase();
//                    //                                transacoesTemp.setTickVenda(getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
//                    //                                        .filter(historicoDeTicks -> {
//                    //                                            if (contrato.contains("call") || contrato.contains("put"))
//                    //                                                return historicoDeTicks.getTime() > transacoesTemp.getDataHoraExpiry();
//                    //                                            return historicoDeTicks.getTime() >= transacoesTemp.getDataHoraExpiry();
//                    //                                        }).sorted(Comparator.comparing(HistoricoDeTicks::getTime))
//                    //                                        .findFirst().get().getPrice());
//                    //                                if (transacoesTemp.getTickCompra() != null && transacoesTemp.getTickVenda() != null) {
//                    //                                    getTransacoesDAO().merger(transacoesTemp);
//                    //                                }
//                    //                            }
//                    //                        } catch (Exception ex) {
//                    //                            if (!(ex instanceof NullPointerException) && !(ex instanceof NoSuchElementException))
//                    //                                ex.printStackTrace();
//                    //                        } finally {
//                    //                            getTransacoesObservableList().set(index, transacoesTemp);
//                    //                        }
//                    //                    }
//
//                    atualizaCoresGrafico(finalOperadorId);
//                });
//            });
//        }

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

    public static ObservableList<ContaToken> getTokenObservableList() {
        return TOKEN_OBSERVABLE_LIST;
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

    public Button getBtnContrato_Op01() {
        return btnContrato_Op01;
    }

    public void setBtnContrato_Op01(Button btnContrato_Op01) {
        this.btnContrato_Op01 = btnContrato_Op01;
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

    public Label getLblPremiacao_Op01() {
        return lblPremiacao_Op01;
    }

    public void setLblPremiacao_Op01(Label lblPremiacao_Op01) {
        this.lblPremiacao_Op01 = lblPremiacao_Op01;
    }

    public Label getLblLucro_Op01() {
        return lblLucro_Op01;
    }

    public void setLblLucro_Op01(Label lblLucro_Op01) {
        this.lblLucro_Op01 = lblLucro_Op01;
    }

    public TableView<Transacoes> getTbvTransacoes_Op01() {
        return tbvTransacoes_Op01;
    }

    public void setTbvTransacoes_Op01(TableView<Transacoes> tbvTransacoes_Op01) {
        this.tbvTransacoes_Op01 = tbvTransacoes_Op01;
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

    public static TICK_STYLE getTickStyle() {
        return TICK_STYLE;
    }

    public static ObservableList<HistoricoDeTicks>[] getHistoricoDeTicksObservableList() {
        return historicoDeTicksObservableList;
    }

    public static void setHistoricoDeTicksObservableList(ObservableList<HistoricoDeTicks>[] historicoDeTicksObservableList) {
        Operacoes.historicoDeTicksObservableList = historicoDeTicksObservableList;
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

    public static ObservableList<HistoricoDeCandles>[] getHistoricoDeCandlesObservableList() {
        return historicoDeCandlesObservableList;
    }

    public static void setHistoricoDeCandlesObservableList(ObservableList<HistoricoDeCandles>[] historicoDeCandlesObservableList) {
        Operacoes.historicoDeCandlesObservableList = historicoDeCandlesObservableList;
    }

    public static ObjectProperty<Ohlc>[] getUltimoCandle() {
        return ultimoCandle;
    }

    public static void setUltimoCandle(ObjectProperty<Ohlc>[] ultimoCandle) {
        Operacoes.ultimoCandle = ultimoCandle;
    }
}
