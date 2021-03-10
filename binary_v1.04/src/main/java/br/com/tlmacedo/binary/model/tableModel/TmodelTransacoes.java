package br.com.tlmacedo.binary.model.tableModel;

import br.com.tlmacedo.binary.interfaces.Constants;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Transacoes;
import br.com.tlmacedo.binary.model.vo.Transaction;
import br.com.tlmacedo.binary.services.Service_DataTime;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TmodelTransacoes {

    private TableColumn<Transacoes, String> colId;
    private TableColumn<Transacoes, String> colTransaction_id;
    private TableColumn<Transacoes, String> colDataHoraCompra;
    private TableColumn<Transacoes, String> colNegociacao;
    private TableColumn<Transacoes, String> colTickCompra;
    private TableColumn<Transacoes, String> colTickVenda;
    private TableColumn<Transacoes, String> colStakeCompra;
    private TableColumn<Transacoes, String> colStakeVenda;
    private TableColumn<Transacoes, Boolean> colConsolidado;

    private TableView<Transacoes> tbvTransacoes;
    private FilteredList<Transacoes> transacoesFilteredList;

    private IntegerProperty n_Stakes = new SimpleIntegerProperty(0);
    private IntegerProperty n_Wins = new SimpleIntegerProperty(0);
    private IntegerProperty n_Loss = new SimpleIntegerProperty(0);

    private ObjectProperty<BigDecimal> vlr_In = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> vlr_Out = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> vlr_Diff = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public TmodelTransacoes() {
    }

    public TmodelTransacoes(FilteredList transacoesFilteredList) {
        this.transacoesFilteredList = transacoesFilteredList;
    }

//    public TmodelTransacoes(ObservableList<Transacoes> transacoesObservableList) {
//        this.transacoesObservableList = transacoesObservableList;
//    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public void criar_tabela() {

        setColTransaction_id(new TableColumn<>("referência"));
        getColTransaction_id().setPrefWidth(105);
        getColTransaction_id().setStyle("-fx-alignment: center-right;");
        getColTransaction_id().setCellValueFactory(param -> param.getValue().transaction_idProperty().asString());

        setColDataHoraCompra(new TableColumn<>("data hora"));
        getColDataHoraCompra().setPrefWidth(140);
        getColDataHoraCompra().setStyle("-fx-alignment: center-right;");
        getColDataHoraCompra().setCellValueFactory(param -> Service_DataTime
                .getCarimboStrProperty(param.getValue().dataHoraCompraProperty().getValue(), Constants.DTF_TMODEL_DATA_TRANSACTION));

        setColNegociacao(new TableColumn<>("Type"));
        getColNegociacao().setPrefWidth(50);
        getColNegociacao().setStyle("-fx-alignment: center-left;");
        getColNegociacao().setCellValueFactory(param -> {
            CONTRACT_TYPE op = null;
            if (param.getValue().getLongcode().contains("lower"))
                op = CONTRACT_TYPE.PUT;
            else if (param.getValue().getLongcode().contains("higher"))
                op = CONTRACT_TYPE.CALL;
            return new SimpleStringProperty(op.toString());
        });

        setColTickCompra(new TableColumn<>("tick_buy"));
        getColTickCompra().setPrefWidth(80);
        getColTickCompra().setStyle("-fx-alignment: center-right;");
        getColTickCompra().setCellValueFactory(param ->
                param.getValue().tickNegociacaoInicioProperty().asString());

        setColTickVenda(new TableColumn<>("tick_sell"));
        getColTickVenda().setPrefWidth(80);
        getColTickVenda().setStyle("-fx-alignment: center-right;");
        getColTickVenda().setCellValueFactory(param ->
                param.getValue().tickVendaProperty().asString());

        setColStakeCompra(new TableColumn<>("stake"));
        getColStakeCompra().setPrefWidth(60);
        getColStakeCompra().setStyle("-fx-alignment: center-right;");
        getColStakeCompra().setCellValueFactory(param -> {
            if (param.getValue().getStakeCompra() != null)
                return new SimpleStringProperty(Service_Mascara.getValorMoeda(
                        param.getValue().stakeCompraProperty().getValue().negate()));
            return new SimpleStringProperty("0.00");
        });

        setColConsolidado(new TableColumn<>("C"));
        getColConsolidado().setPrefWidth(30);
        getColConsolidado().setStyle("-fx-alignment: center");
        getColConsolidado().setCellValueFactory(param -> param.getValue().consolidadoProperty());

        setColStakeVenda(new TableColumn<>("result"));
        getColStakeVenda().setPrefWidth(60);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
        getColStakeVenda().setCellValueFactory(param -> {
            if (param.getValue().isConsolidado())
                return new SimpleStringProperty(Service_Mascara.getValorMoeda(
                        param.getValue().stakeVendaProperty().getValue()
                                .add(param.getValue().stakeCompraProperty().getValue())));
            return param.getValue().stakeVendaProperty().asString();
        });
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
                            if (linha.consolidadoProperty().getValue()) {
                                if (linha.stakeVendaProperty().getValue().compareTo(BigDecimal.ZERO) > 0)
                                    this.setStyle("-fx-text-fill: #59A35B;-fx-alignment: center-right;");
                                else
                                    this.setStyle("-fx-text-fill: #f75600;-fx-alignment: center-right;");
                            }
                        }
                    }
                }
        );

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    public void tabela_preencher() {

        getTbvTransacoes().getColumns().setAll(
                getColNegociacao(), getColTickCompra(), getColTickVenda(), getColStakeCompra(),
                getColStakeVenda(), getColConsolidado(), getColDataHoraCompra(), getColTransaction_id()
        );
        getTbvTransacoes().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getTbvTransacoes().getSelectionModel().setCellSelectionEnabled(true);
        getTbvTransacoes().setEditable(true);

        getTbvTransacoes().setItems(getTransacoesFilteredList()
                .sorted(Comparator.comparing(Transacoes::getDataHoraCompra).reversed()));

        escutarTransacoesTabela();
//        refreshTabela();

    }

    public void escutarTransacoesTabela() {
        getTransacoesFilteredList().addListener((ListChangeListener<? super Transacoes>) c -> {
            totalizaTabelas();
        });
    }

//    public void refreshTabela() {
//        getTbvTransacoes().refresh();
//        totalizaTabelas();
//    }

    public void totalizaTabelas() {

        n_StakesProperty().setValue(getTransacoesFilteredList().size());

        n_WinsProperty().setValue(getTransacoesFilteredList().stream()
                .filter(transacoes -> transacoes.isConsolidado())
                .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                .count());

        n_LossProperty().setValue(getTransacoesFilteredList().stream()
                .filter(transacoes -> transacoes.isConsolidado())
                .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                .count());

        vlr_InProperty().setValue(
                getTransacoesFilteredList().stream()
                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .negate().setScale(2, RoundingMode.HALF_UP));

        vlr_OutProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));

        vlr_DiffProperty().setValue(
                getTransacoesFilteredList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(transacoes -> transacoes.stakeVendaProperty().getValue()
                                .add(transacoes.stakeCompraProperty().getValue()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));

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


    public TableColumn<Transacoes, String> getColId() {
        return colId;
    }

    public void setColId(TableColumn<Transacoes, String> colId) {
        this.colId = colId;
    }

    public TableColumn<Transacoes, String> getColTransaction_id() {
        return colTransaction_id;
    }

    public void setColTransaction_id(TableColumn<Transacoes, String> colTransaction_id) {
        this.colTransaction_id = colTransaction_id;
    }

    public TableColumn<Transacoes, String> getColDataHoraCompra() {
        return colDataHoraCompra;
    }

    public void setColDataHoraCompra(TableColumn<Transacoes, String> colDataHoraCompra) {
        this.colDataHoraCompra = colDataHoraCompra;
    }

    public TableColumn<Transacoes, String> getColNegociacao() {
        return colNegociacao;
    }

    public void setColNegociacao(TableColumn<Transacoes, String> colNegociacao) {
        this.colNegociacao = colNegociacao;
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

    public TableColumn<Transacoes, Boolean> getColConsolidado() {
        return colConsolidado;
    }

    public void setColConsolidado(TableColumn<Transacoes, Boolean> colConsolidado) {
        this.colConsolidado = colConsolidado;
    }

    public TableView<Transacoes> getTbvTransacoes() {
        return tbvTransacoes;
    }

    public void setTbvTransacoes(TableView<Transacoes> tbvTransacoes) {
        this.tbvTransacoes = tbvTransacoes;
    }

    public FilteredList<Transacoes> getTransacoesFilteredList() {
        return transacoesFilteredList;
    }

    public void setTransacoesFilteredList(FilteredList<Transacoes> transacoesFilteredList) {
        this.transacoesFilteredList = transacoesFilteredList;
    }

    public int getN_Stakes() {
        return n_Stakes.get();
    }

    public IntegerProperty n_StakesProperty() {
        return n_Stakes;
    }

    public void setN_Stakes(int n_Stakes) {
        this.n_Stakes.set(n_Stakes);
    }

    public int getN_Wins() {
        return n_Wins.get();
    }

    public IntegerProperty n_WinsProperty() {
        return n_Wins;
    }

    public void setN_Wins(int n_Wins) {
        this.n_Wins.set(n_Wins);
    }

    public int getN_Loss() {
        return n_Loss.get();
    }

    public IntegerProperty n_LossProperty() {
        return n_Loss;
    }

    public void setN_Loss(int n_Loss) {
        this.n_Loss.set(n_Loss);
    }

    public BigDecimal getVlr_In() {
        return vlr_In.get();
    }

    public ObjectProperty<BigDecimal> vlr_InProperty() {
        return vlr_In;
    }

    public void setVlr_In(BigDecimal vlr_In) {
        this.vlr_In.set(vlr_In);
    }

    public BigDecimal getVlr_Out() {
        return vlr_Out.get();
    }

    public ObjectProperty<BigDecimal> vlr_OutProperty() {
        return vlr_Out;
    }

    public void setVlr_Out(BigDecimal vlr_Out) {
        this.vlr_Out.set(vlr_Out);
    }

    public BigDecimal getVlr_Diff() {
        return vlr_Diff.get();
    }

    public ObjectProperty<BigDecimal> vlr_DiffProperty() {
        return vlr_Diff;
    }

    public void setVlr_Diff(BigDecimal vlr_Diff) {
        this.vlr_Diff.set(vlr_Diff);
    }
}
