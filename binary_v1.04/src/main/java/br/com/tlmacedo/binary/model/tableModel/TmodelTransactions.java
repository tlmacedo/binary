package br.com.tlmacedo.binary.model.tableModel;

import br.com.tlmacedo.binary.interfaces.Constants;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Transaction;
import br.com.tlmacedo.binary.services.Service_DataHoraCarimbo;
import br.com.tlmacedo.binary.services.Service_Mascara;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TmodelTransactions {

    private TableColumn<Transaction, String> colTransaction_id;
    private TableColumn<Transaction, String> colDataHoraCompra;
    private TableColumn<Transaction, String> colNegociacao;
    private TableColumn<Transaction, String> colTickCompra;
    private TableColumn<Transaction, String> colTickVenda;
    private TableColumn<Transaction, String> colStakeCompra;
    private TableColumn<Transaction, String> colStakeVenda;

    private TableView<Transaction> tbvTransaction;
    private ObservableList<Transaction> transactionObservableList;
    private FilteredList<Transaction> transactionFilteredList;

    public TmodelTransactions() {
    }

    public TmodelTransactions(FilteredList transactionFilteredList) {
        this.transactionFilteredList = transactionFilteredList;
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
        getColTransaction_id().setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTransaction_id().toString()));

        setColDataHoraCompra(new TableColumn<>("data hora"));
        getColDataHoraCompra().setPrefWidth(140);
        getColDataHoraCompra().setStyle("-fx-alignment: center-right;");
        getColDataHoraCompra().setCellValueFactory(param -> Service_DataHoraCarimbo
                .getCarimboStrProperty(param.getValue().getPurchase_time(), Constants.DTF_TMODEL_DATA_TRANSACTION));

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
            switch (ACTION.valueOf(param.getValue().getAction().toUpperCase())) {
                case BUY -> {
                    return new SimpleStringProperty(Service_Mascara.getValorMoeda(param.getValue().getAmount().negate()));
                }
            }
            return new SimpleStringProperty("");
        });

        setColStakeVenda(new TableColumn<>("result"));
        getColStakeCompra().setPrefWidth(60);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
        getColStakeVenda().setCellValueFactory(param -> {
            switch (ACTION.valueOf(param.getValue().getAction().toUpperCase())) {
                case SELL -> {
                    return new SimpleStringProperty(Service_Mascara.getValorMoeda(param.getValue().getAmount()));
                }
            }
            return new SimpleStringProperty("");
        });


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

        getTbvTransaction().getColumns().setAll(
                getColNegociacao(), getColTickCompra(), getColTickVenda(), getColStakeCompra(),
                getColStakeVenda(), getColDataHoraCompra(), getColTransaction_id()
        );
        getTbvTransaction().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        getTbvTransactions().getSelectionModel().setCellSelectionEnabled(true);
        getTbvTransaction().setEditable(true);

        getTransactionFilteredList().addListener((ListChangeListener<? super Transaction>) c -> {
            while (c.next()) {
                for (Transaction transaction : c.getAddedSubList())
                    System.out.printf("mudou na Transação ===>>>   %s\n", transaction);
            }
        });
        // getTbvTransactions().setItems(getTransactionFilteredList());

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


    public TableColumn<Transaction, String> getColTransaction_id() {
        return colTransaction_id;
    }

    public void setColTransaction_id(TableColumn<Transaction, String> colTransaction_id) {
        this.colTransaction_id = colTransaction_id;
    }

    public TableColumn<Transaction, String> getColDataHoraCompra() {
        return colDataHoraCompra;
    }

    public void setColDataHoraCompra(TableColumn<Transaction, String> colDataHoraCompra) {
        this.colDataHoraCompra = colDataHoraCompra;
    }

    public TableColumn<Transaction, String> getColNegociacao() {
        return colNegociacao;
    }

    public void setColNegociacao(TableColumn<Transaction, String> colNegociacao) {
        this.colNegociacao = colNegociacao;
    }

    public TableColumn<Transaction, String> getColTickCompra() {
        return colTickCompra;
    }

    public void setColTickCompra(TableColumn<Transaction, String> colTickCompra) {
        this.colTickCompra = colTickCompra;
    }

    public TableColumn<Transaction, String> getColTickVenda() {
        return colTickVenda;
    }

    public void setColTickVenda(TableColumn<Transaction, String> colTickVenda) {
        this.colTickVenda = colTickVenda;
    }

    public TableColumn<Transaction, String> getColStakeCompra() {
        return colStakeCompra;
    }

    public void setColStakeCompra(TableColumn<Transaction, String> colStakeCompra) {
        this.colStakeCompra = colStakeCompra;
    }

    public TableColumn<Transaction, String> getColStakeVenda() {
        return colStakeVenda;
    }

    public void setColStakeVenda(TableColumn<Transaction, String> colStakeVenda) {
        this.colStakeVenda = colStakeVenda;
    }

    public TableView<Transaction> getTbvTransaction() {
        return tbvTransaction;
    }

    public void setTbvTransaction(TableView<Transaction> tbvTransaction) {
        this.tbvTransaction = tbvTransaction;
    }

    public ObservableList<Transaction> getTransactionObservableList() {
        return transactionObservableList;
    }

    public void setTransactionObservableList(ObservableList<Transaction> transactionObservableList) {
        this.transactionObservableList = transactionObservableList;
    }

    public FilteredList<Transaction> getTransactionFilteredList() {
        return transactionFilteredList;
    }

    public void setTransactionFilteredList(FilteredList<Transaction> transactionFilteredList) {
        this.transactionFilteredList = transactionFilteredList;
    }
}
