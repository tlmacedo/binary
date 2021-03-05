package br.com.tlmacedo.binary.model.tableModel;

import br.com.tlmacedo.binary.interfaces.Constants;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Transacoes;
import br.com.tlmacedo.binary.model.vo.Transaction;
import br.com.tlmacedo.binary.services.Service_DataTime;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;

import java.math.BigDecimal;
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

    private TableView<Transacoes> tbvTransacoes;
    private ObservableList<Transacoes> transacoesObservableList;
    private FilteredList<Transacoes> transacoesFilteredList;


    public TmodelTransacoes() {
    }

    public TmodelTransacoes(FilteredList transacoesFilteredList) {
        this.transacoesFilteredList = transacoesFilteredList;
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
        getColDataHoraCompra().setStyle("-fx-alignment: center-right;");
        getColDataHoraCompra().setCellValueFactory(param -> Service_DataTime
                .getCarimboStrProperty(param.getValue().getDataHoraCompra(), Constants.DTF_TMODEL_DATA_TRANSACTION));

        setColNegociacao(new TableColumn<>("Negociação"));
        getColNegociacao().setPrefWidth(92);
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
        getColTickCompra().setPrefWidth(94);
        getColTickCompra().setStyle("-fx-alignment: center-right;");
        getColTickCompra().setCellValueFactory(param -> new SimpleStringProperty(""));

        setColTickVenda(new TableColumn<>("tick_sell"));
        getColTickVenda().setPrefWidth(94);
        getColTickVenda().setStyle("-fx-alignment: center-right;");
        getColTickVenda().setCellValueFactory(param -> new SimpleStringProperty(""));

        setColStakeCompra(new TableColumn<>("stake"));
        getColStakeCompra().setPrefWidth(60);
        getColStakeCompra().setStyle("-fx-alignment: center-right;");
        getColStakeCompra().setCellValueFactory(param -> {
            if (param.getValue().getStakeCompra() != null)
                return new SimpleStringProperty(Service_Mascara.getValorMoeda(
                        param.getValue().getStakeCompra().negate()));
            return new SimpleStringProperty("0.00");
        });

        setColStakeVenda(new TableColumn<>("result"));
        getColStakeCompra().setPrefWidth(60);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
        getColStakeVenda().setCellValueFactory(param -> new SimpleStringProperty(Service_Mascara.getValorMoeda(
                param.getValue().getStakeVenda().add(param.getValue().getStakeCompra()))));
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
                getColStakeVenda(), getColDataHoraCompra(), getColTransaction_id()
        );
        getTbvTransacoes().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getTbvTransacoes().getSelectionModel().setCellSelectionEnabled(true);
        getTbvTransacoes().setEditable(true);

//        getTransactionFilteredList().addListener((ListChangeListener<? super Transaction>) c -> {
//            while (c.next()) {
//                for (Transaction transaction : c.getAddedSubList())
//                    System.out.printf("mudou na Transação ===>>>   %s\n", transaction);
//            }
//        });
        getTbvTransacoes().setItems(getTransacoesFilteredList());

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
}
