package br.com.tlmacedo.binary.model.tableModel;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.Symbol;
import br.com.tlmacedo.binary.model.vo.Transacoes;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static br.com.tlmacedo.binary.interfaces.Constants.DTF_TMODEL_DATA_TRANSACTION;

public class TmodelTransacoes {

    private Symbol symbol;
    private TablePosition tp;
    private TableView<Transacoes> tbvTransacoes;
    private ObservableList<Transacoes> transacoesObservableList;
    private FilteredList<Transacoes> transacoesFilteredList;
    public TextField txtNExecucoes;
    public TextField txtNVitorias;
    public TextField txtNDerrotas;
    public TextField txtLucro;


    private TableColumn<Transacoes, String> colId;
    private TableColumn<Transacoes, String> colSymbol;
    private TableColumn<Transacoes, String> colContract;
    private TableColumn<Transacoes, String> colDataHoraCompra;
    private TableColumn<Transacoes, String> colTipoNegociacao;
    private TableColumn<Transacoes, String> colTickCompra;
    private TableColumn<Transacoes, String> colTickVenda;
    private TableColumn<Transacoes, String> colStakeCompra;
    private TableColumn<Transacoes, String> colStakeVenda;

    private IntegerProperty qtdNExecucao = new SimpleIntegerProperty(0);
    private IntegerProperty qtdNVitoria = new SimpleIntegerProperty(0);
    private IntegerProperty qtdNDerrota = new SimpleIntegerProperty(0);
    private ObjectProperty<BigDecimal> totalInvestido = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalPremiacao = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalLucro = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public TmodelTransacoes(Integer activeSymbolId) {
        this.symbol = Operacoes.getActiveSymbolObservableList().stream()
                .filter(activeSymbol1 -> activeSymbol1.getId().intValue() == activeSymbolId)
                .findFirst().orElse(null);
    }

    public void criarTabela() {
//        setColId(new TableColumn<>("id"));
//        getColId().setPrefWidth(0);
//        getColId().setStyle("-fx-alignment: center-right;");
//        getColId().setCellValueFactory(param -> param.getValue());

        setColSymbol(new TableColumn<>("Symbol"));
        getColSymbol().setPrefWidth(60);
        getColSymbol().setStyle("-fx-alignment: center;");
        getColSymbol().setCellValueFactory(param -> param.getValue().activeSymbolProperty().asString());

        setColDataHoraCompra(new TableColumn<>("carimbo hora"));
        getColDataHoraCompra().setPrefWidth(140);
        getColDataHoraCompra().setStyle("-fx-alignment: center-right;");
        getColDataHoraCompra().setCellValueFactory(cellData ->
                new SimpleStringProperty(LocalDateTime.ofInstant(Instant.ofEpochSecond(cellData.getValue().dataHoraCompraProperty().getValue()),
                        TimeZone.getDefault().toZoneId()).format(DTF_TMODEL_DATA_TRANSACTION)));

        setColTickCompra(new TableColumn<>("tick_buy"));
        getColTickCompra().setPrefWidth(94);
        getColTickCompra().setStyle("-fx-alignment: center-right;");
        getColTickCompra().setCellValueFactory(cellData -> {
            if (cellData.getValue().tickCompraProperty().getValue() != null)
                return new SimpleStringProperty(Service_Mascara.getValorFormatado(
                        getActiveSymbol().getPip().intValue(),
                        cellData.getValue().tickCompraProperty().getValue()));
            return new SimpleStringProperty("");
        });

        setColTickVenda(new TableColumn<>("tick_sell"));
        getColTickVenda().setPrefWidth(94);
        getColTickVenda().setStyle("-fx-alignment: center-right;");
        getColTickVenda().setCellValueFactory(cellData -> {
            if (cellData.getValue().tickVendaProperty().getValue() != null)
                return new SimpleStringProperty(Service_Mascara.getValorFormatado(
                        getActiveSymbol().getPip().intValue(),
                        cellData.getValue().tickVendaProperty().getValue()));
            return new SimpleStringProperty("");
        });

        setColStakeCompra(new TableColumn<>("stake"));
        getColStakeCompra().setPrefWidth(60);
        getColStakeCompra().setStyle("-fx-alignment: center-right;");
        getColStakeCompra().setCellValueFactory(cellData -> {
            if (cellData.getValue().stakeCompraProperty().getValue() != null)
                return new SimpleStringProperty(Service_Mascara.getValorMoeda(cellData.getValue().stakeCompraProperty()
                        .getValue().negate()));
            return new SimpleStringProperty("0.00");
        });

        setColStakeVenda(new TableColumn<>("result"));
        getColStakeVenda().setPrefWidth(60);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
        getColStakeVenda().setCellValueFactory(cellData -> new SimpleStringProperty(Service_Mascara
                .getValorMoeda(cellData.getValue().stakeVendaProperty().getValue()
                        .add(cellData.getValue().stakeCompraProperty().getValue()))));
        getColStakeVenda().setCellFactory(param ->
                new TableCell<Transacoes, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        getStyleClass().removeAll(getStyleClass().stream().filter(s -> s.contains("operacao-")).collect(Collectors.toList()));
                        if (empty || item == null) {
                            this.setText(null);
                            this.setStyle("-fx-alignment: center-right;");
                        } else {
                            this.setText(item);
                            Transacoes linha = param.getTableView().getItems().get(getIndex());
                            if (linha.isConsolidado()) {
                                if (linha.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                                    this.setStyle("-fx-text-fill: #59A35B;-fx-alignment: center-right;");
                                else
                                    this.setStyle("-fx-text-fill: #f75600;-fx-alignment: center-right;");
                            }
                        }
                    }
                }
        );

        setColTipoNegociacao(new TableColumn<>("Negociação"));
        getColTipoNegociacao().setPrefWidth(92);
        getColTipoNegociacao().setStyle("-fx-alignment: center;");
        getColTipoNegociacao().setCellValueFactory(param -> param.getValue().contract_typeProperty());

        setColContract(new TableColumn<>("referência"));
        getColContract().setPrefWidth(105);
        getColSymbol().setStyle("-fx-alignment: center-right;");
        getColContract().setCellValueFactory(param -> param.getValue().transaction_idProperty().asString());

    }

    public void escutarTransacoesTabela() {
        getTransacoesFilteredList().setPredicate(transacoes -> transacoes.getActiveSymbol().getSymbol().equals(getActiveSymbol().getSymbol()));
        getTransacoesObservableList().addListener((ListChangeListener<? super Transacoes>) change -> {
            totalizaTabela();
        });

//        getTbvTransacoes().setRowFactory(param -> new TableRow<>() {
//            @Override
//            protected void updateItem(Transacoes item, boolean empty) {
//                super.updateItem(item, empty);
//                getStyleClass().removeAll(getStyleClass().stream().filter(s -> s.contains("operacao-")).collect(Collectors.toList()));
////                TableColumn column = getTableView().getColumns().get(4);
////                column.getStyleClass().removeAll(getStyleClass().stream().filter(s -> s.contains("operacao-")).collect(Collectors.toList()));
//                if (!empty)
//                    if (item.isConsolidado()) {
//                        System.out.printf("%s\n", getTableView().getColumns().get(4).getStyleClass());
//                        if (item.getStakeVenda().compareTo(BigDecimal.ZERO) > 0) {
//                            getTableView().getColumns().ge.getStyleClass().add("operacao-ganho");
//                        } else {
//                            getTableView().getColumns().get(4).getStyleClass().add("operacao-perda");
//                        }
//                    }
//            }
//        });

    }


    public void tabela_preencher() {
        getTbvTransacoes().getColumns().setAll(
                getColTipoNegociacao(), getColTickCompra(), getColTickVenda(), getColStakeCompra(),
                getColStakeVenda(), getColDataHoraCompra(), getColContract()
        );
        getTbvTransacoes().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getTbvTransacoes().getSelectionModel().setCellSelectionEnabled(true);
        getTbvTransacoes().setEditable(true);
//        getTbvTransacoes().setItems(getTransacoesObservableList().stream()
//                .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
//                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        getTbvTransacoes().setItems(getTransacoesFilteredList());

        totalizaTabela();
    }

    public void totalizaTabela() {
        qtdNExecucaoProperty().setValue(
                getTransacoesFilteredList().stream()
                        .count());

        qtdNVitoriaProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                        .count());

        qtdNDerrotaProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                        .count());

        totalInvestidoProperty().setValue(
                getTransacoesFilteredList().stream()
                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .negate().setScale(2, RoundingMode.HALF_UP));

        totalPremiacaoProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));

        totalLucroProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
    }

    public Symbol getActiveSymbol() {
        return symbol;
    }

    public void setActiveSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    public TablePosition getTp() {
        return tp;
    }

    public void setTp(TablePosition tp) {
        this.tp = tp;
    }

    public TableView<Transacoes> getTbvTransacoes() {
        return tbvTransacoes;
    }

    public void setTbvTransacoes(TableView<Transacoes> tbvTransacoes) {
        this.tbvTransacoes = tbvTransacoes;
    }

    public ObservableList<Transacoes> getTransacoesObservableList() {
        return transacoesObservableList;
    }

    public void setTransacoesObservableList(ObservableList<Transacoes> transacoesObservableList) {
        this.transacoesObservableList = transacoesObservableList;
    }

    public FilteredList<Transacoes> getTransacoesFilteredList() {
        return transacoesFilteredList;
    }

    public void setTransacoesFilteredList(FilteredList<Transacoes> transacoesFilteredList) {
        this.transacoesFilteredList = transacoesFilteredList;
    }

    public TextField getTxtNExecucoes() {
        return txtNExecucoes;
    }

    public void setTxtNExecucoes(TextField txtNExecucoes) {
        this.txtNExecucoes = txtNExecucoes;
    }

    public TextField getTxtNVitorias() {
        return txtNVitorias;
    }

    public void setTxtNVitorias(TextField txtNVitorias) {
        this.txtNVitorias = txtNVitorias;
    }

    public TextField getTxtNDerrotas() {
        return txtNDerrotas;
    }

    public void setTxtNDerrotas(TextField txtNDerrotas) {
        this.txtNDerrotas = txtNDerrotas;
    }

    public TextField getTxtLucro() {
        return txtLucro;
    }

    public void setTxtLucro(TextField txtLucro) {
        this.txtLucro = txtLucro;
    }

    public TableColumn<Transacoes, String> getColId() {
        return colId;
    }

    public void setColId(TableColumn<Transacoes, String> colId) {
        this.colId = colId;
    }

    public TableColumn<Transacoes, String> getColSymbol() {
        return colSymbol;
    }

    public void setColSymbol(TableColumn<Transacoes, String> colSymbol) {
        this.colSymbol = colSymbol;
    }

    public TableColumn<Transacoes, String> getColContract() {
        return colContract;
    }

    public void setColContract(TableColumn<Transacoes, String> colContract) {
        this.colContract = colContract;
    }

    public TableColumn<Transacoes, String> getColDataHoraCompra() {
        return colDataHoraCompra;
    }

    public void setColDataHoraCompra(TableColumn<Transacoes, String> colDataHoraCompra) {
        this.colDataHoraCompra = colDataHoraCompra;
    }

    public TableColumn<Transacoes, String> getColTipoNegociacao() {
        return colTipoNegociacao;
    }

    public void setColTipoNegociacao(TableColumn<Transacoes, String> colTipoNegociacao) {
        this.colTipoNegociacao = colTipoNegociacao;
    }

    public TableColumn<Transacoes, String> getColTickCompra() {
        return colTickCompra;
    }

    public void setColTickCompra(TableColumn<Transacoes, String> colTickCompra) {
        this.colTickCompra = colTickCompra;
    }

    public TableColumn<Transacoes, String> getColTickVenda() {
        return colTickVenda;
    }

    public void setColTickVenda(TableColumn<Transacoes, String> colTickVenda) {
        this.colTickVenda = colTickVenda;
    }

    public TableColumn<Transacoes, String> getColStakeCompra() {
        return colStakeCompra;
    }

    public void setColStakeCompra(TableColumn<Transacoes, String> colStakeCompra) {
        this.colStakeCompra = colStakeCompra;
    }

    public TableColumn<Transacoes, String> getColStakeVenda() {
        return colStakeVenda;
    }

    public void setColStakeVenda(TableColumn<Transacoes, String> colStakeVenda) {
        this.colStakeVenda = colStakeVenda;
    }

    public int getQtdNExecucao() {
        return qtdNExecucao.get();
    }

    public IntegerProperty qtdNExecucaoProperty() {
        return qtdNExecucao;
    }

    public void setQtdNExecucao(int qtdNExecucao) {
        this.qtdNExecucao.set(qtdNExecucao);
    }

    public int getQtdNVitoria() {
        return qtdNVitoria.get();
    }

    public IntegerProperty qtdNVitoriaProperty() {
        return qtdNVitoria;
    }

    public void setQtdNVitoria(int qtdNVitoria) {
        this.qtdNVitoria.set(qtdNVitoria);
    }

    public int getQtdNDerrota() {
        return qtdNDerrota.get();
    }

    public IntegerProperty qtdNDerrotaProperty() {
        return qtdNDerrota;
    }

    public void setQtdNDerrota(int qtdNDerrota) {
        this.qtdNDerrota.set(qtdNDerrota);
    }

    public BigDecimal getTotalInvestido() {
        return totalInvestido.get();
    }

    public ObjectProperty<BigDecimal> totalInvestidoProperty() {
        return totalInvestido;
    }

    public void setTotalInvestido(BigDecimal totalInvestido) {
        this.totalInvestido.set(totalInvestido);
    }

    public BigDecimal getTotalPremiacao() {
        return totalPremiacao.get();
    }

    public ObjectProperty<BigDecimal> totalPremiacaoProperty() {
        return totalPremiacao;
    }

    public void setTotalPremiacao(BigDecimal totalPremiacao) {
        this.totalPremiacao.set(totalPremiacao);
    }

    public BigDecimal getTotalLucro() {
        return totalLucro.get();
    }

    public ObjectProperty<BigDecimal> totalLucroProperty() {
        return totalLucro;
    }

    public void setTotalLucro(BigDecimal totalLucro) {
        this.totalLucro.set(totalLucro);
    }
}
