package br.com.tlmacedo.binary.model.tableModel;

import br.com.tlmacedo.binary.interfaces.Constants;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Symbol;
import br.com.tlmacedo.binary.model.vo.TimeFrame;
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
    private TableColumn<Transacoes, String> colTickNegociacaoInicio;
    private TableColumn<Transacoes, String> colTickVenda;
    private TableColumn<Transacoes, String> colStakeCompra;
    private TableColumn<Transacoes, String> colStakeVenda;
    private TableColumn<Transacoes, String> colStakeResult;
//    private TableColumn<Transacoes, Boolean> colConsolidado;

    private TableView<Transacoes> tbvTransacoes;
    //    private ObservableList<Transacoes> transacoesObservableList;
    private FilteredList<Transacoes> transacoesFilteredList;


    public TmodelTransacoes(ObservableList transacoesObservableList, int t_id, int s_id) {
//        this.transacoesObservableList = transacoesObservableList;
        this.transacoesFilteredList = new FilteredList<>(transacoesObservableList);
        getTransacoesFilteredList().setPredicate(transacoes -> transacoes.getT_id() == t_id
                && transacoes.getS_id() == s_id);
    }

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
        getColDataHoraCompra().setStyle("-fx-alignment: center-left;");
        getColDataHoraCompra().setCellValueFactory(param -> Service_DataTime
                .getCarimboStrProperty(param.getValue().dataHoraCompraProperty().getValue(), Constants.DTF_TMODEL_DATA_TRANSACTION));

        setColNegociacao(new TableColumn<>("Type"));
        getColNegociacao().setPrefWidth(45);
        getColNegociacao().setStyle("-fx-alignment: center-left;");
        getColNegociacao().setCellValueFactory(param -> {
            CONTRACT_TYPE op = null;
            if (param.getValue().getLongcode().contains("lower"))
                op = CONTRACT_TYPE.PUT;
            else if (param.getValue().getLongcode().contains("higher"))
                op = CONTRACT_TYPE.CALL;
            return new SimpleStringProperty(op.toString());
        });

        setColTickNegociacaoInicio(new TableColumn<>("tick_buy"));
        getColTickNegociacaoInicio().setPrefWidth(85);
        getColTickNegociacaoInicio().setStyle("-fx-alignment: center-right;");
        getColTickNegociacaoInicio().setCellValueFactory(param -> param.getValue().tickNegociacaoInicioProperty().asString());
//        getColTickNegociacaoInicio().setCellValueFactory(param -> {
//            System.out.printf("getTickNegociacaoInicio(): %s\n", param.getValue().getTickNegociacaoInicio());
//            if (param.getValue().tickNegociacaoInicioProperty().getValue() == null)
//                return new SimpleStringProperty("");
//            return param.getValue().tickNegociacaoInicioProperty().asString();
//        });

        setColTickVenda(new TableColumn<>("tick_sell"));
        getColTickVenda().setPrefWidth(85);
        getColTickVenda().setStyle("-fx-alignment: center-right;");
//        getColTickVenda().setCellValueFactory(param -> param.getValue().tickVendaProperty().asString());
        getColTickVenda().setCellValueFactory(param -> {
            if (param.getValue().isConsolidado())
                return param.getValue().tickVendaProperty().asString();
            return new SimpleStringProperty("");
        });

        setColStakeCompra(new TableColumn<>("pay_In"));
        getColStakeCompra().setPrefWidth(55);
        getColStakeCompra().setStyle("-fx-alignment: center-right;");
        getColStakeCompra().setCellValueFactory(param -> param.getValue().stakeCompraProperty().asString());
//        getColStakeCompra().setCellValueFactory(param -> {
//            if (param.getValue().getStakeVenda() != null)
//                return param.getValue().stakeCompraProperty().asString();
//            return new SimpleStringProperty("");
//        });

        setColStakeVenda(new TableColumn<>("pay_Out"));
        getColStakeVenda().setPrefWidth(60);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
//        getColStakeVenda().setCellValueFactory(param -> param.getValue().stakeVendaProperty().asString());
        getColStakeVenda().setCellValueFactory(param -> {
            if (param.getValue().isConsolidado())
                return param.getValue().stakeVendaProperty().asString();
            return new SimpleStringProperty("");
        });

        setColStakeResult(new TableColumn<>("result"));
        getColStakeResult().setPrefWidth(50);
        getColStakeResult().setStyle("-fx-alignment: center-right;");
        getColStakeResult().setCellValueFactory(param -> {
            if (param.getValue().isConsolidado())
                return param.getValue().stakeResultProperty().asString();
            return new SimpleStringProperty("");
        });
        getColStakeResult().setCellFactory(param ->
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
                getColNegociacao(), getColTickNegociacaoInicio(), getColTickVenda(), getColStakeCompra(),
                getColStakeVenda(), getColStakeResult(), getColDataHoraCompra(), getColTransaction_id()
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
            getTbvTransacoes().refresh();
//            totalizaTabelas();
        });
    }

//    public void refreshTabela() {
//        getTbvTransacoes().refresh();
//        totalizaTabelas();
//    }

    public void totalizaLinha(Transacoes transacao) {
        if (transacao.isConsolidado()) {
            System.out.printf("totalizando linha consolidado\n");
            transacao.setStakeResult(transacao.getStakeVenda().add(transacao.getStakeCompra()));
        } else {
            System.out.printf("totalizando ainda não foi consolidado\n");
            transacao.setStakeResult(BigDecimal.ZERO);
        }
    }

//    public void totalizaTabelas() {
//
//        n_StakesProperty().setValue(getTransacoesFilteredList().size());
//
//        n_WinsProperty().setValue(getTransacoesFilteredList().stream()
//                .filter(transacoes -> transacoes.isConsolidado())
//                .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
//                .count());
//
//        n_LossProperty().setValue(getTransacoesFilteredList().stream()
//                .filter(transacoes -> transacoes.isConsolidado())
//                .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
//                .count());
//
//        vlr_InProperty().setValue(
//                getTransacoesFilteredList().stream()
//                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
//                        .negate().setScale(2, RoundingMode.HALF_UP));
//
//        vlr_OutProperty().setValue(
//                getTransacoesFilteredList().stream()
//                        .filter(transacoes -> transacoes.isConsolidado())
//                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
//                        .setScale(2, RoundingMode.HALF_UP));
//
//        vlr_DiffProperty().setValue(
//                getTransacoesFilteredList().stream()
//                        .filter(transacoes -> transacoes.isConsolidado())
//                        .map(transacoes -> transacoes.stakeVendaProperty().getValue()
//                                .add(transacoes.stakeCompraProperty().getValue()))
//                        .reduce(BigDecimal.ZERO, BigDecimal::add)
//                        .setScale(2, RoundingMode.HALF_UP));
//
//    }


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

    public TableColumn<Transacoes, String> getColTickNegociacaoInicio() {
        return colTickNegociacaoInicio;
    }

    public void setColTickNegociacaoInicio(TableColumn<Transacoes, String> colTickNegociacaoInicio) {
        this.colTickNegociacaoInicio = colTickNegociacaoInicio;
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

    public TableColumn<Transacoes, String> getColStakeResult() {
        return colStakeResult;
    }

    public void setColStakeResult(TableColumn<Transacoes, String> colStakeResult) {
        this.colStakeResult = colStakeResult;
    }

//    public TableColumn<Transacoes, Boolean> getColConsolidado() {
//        return colConsolidado;
//    }
//
//    public void setColConsolidado(TableColumn<Transacoes, Boolean> colConsolidado) {
//        this.colConsolidado = colConsolidado;
//    }

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

    public TableColumn<Transacoes, String> getColStakeVenda() {
        return colStakeVenda;
    }

    public void setColStakeVenda(TableColumn<Transacoes, String> colStakeVenda) {
        this.colStakeVenda = colStakeVenda;
    }
}
