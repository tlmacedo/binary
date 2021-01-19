package br.com.tlmacedo.binary.model.TableModel;

import br.com.tlmacedo.binary.model.Enums.PIP_SIZE;
import br.com.tlmacedo.binary.model.Enums.SYMBOL;
import br.com.tlmacedo.binary.model.Transacoes;
import br.com.tlmacedo.binary.services.ServiceMascara;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class TmodelTransacoes {

    private SYMBOL symbol;
    private TablePosition tp;
    private TableView<Transacoes> tbvTransacoes;
    private ObservableList<Transacoes> transacoesObservableList;
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
    private ObjectProperty<BigDecimal> totalAposta = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalPremio = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalLucro = new SimpleObjectProperty<>(BigDecimal.ZERO);

    private IntegerProperty qtdNExecucaoAcumulado = new SimpleIntegerProperty(0);
    private IntegerProperty qtdNVitoriaAcumulado = new SimpleIntegerProperty(0);
    private IntegerProperty qtdNDerrotaAcumulado = new SimpleIntegerProperty(0);
    private ObjectProperty<BigDecimal> totalApostaAcumulado = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalPremioAcumulado = new SimpleObjectProperty<>(BigDecimal.ZERO);
    private ObjectProperty<BigDecimal> totalLucroAcumulado = new SimpleObjectProperty<>(BigDecimal.ZERO);

    public TmodelTransacoes(SYMBOL symbol) {
        this.symbol = symbol;
    }

    public void criarTabela() {
//        setColId(new TableColumn<>("id"));
//        getColId().setPrefWidth(0);
//        getColId().setStyle("-fx-alignment: center-right;");
//        getColId().setCellValueFactory(param -> param.getValue());


        setColSymbol(new TableColumn<>("Symbol"));
        getColSymbol().setPrefWidth(60);
        getColSymbol().setStyle("-fx-alignment: center;");
        getColSymbol().setCellValueFactory(param -> param.getValue().symbolProperty().asString());

        setColDataHoraCompra(new TableColumn<>("carimbo hora"));
        getColDataHoraCompra().setPrefWidth(150);
        getColDataHoraCompra().setStyle("-fx-alignment: center-right;");
        getColDataHoraCompra().setCellValueFactory(cellData ->
                new SimpleStringProperty(LocalDateTime.ofInstant(Instant.ofEpochSecond(cellData.getValue().dataHoraCompraProperty().getValue()),
                        TimeZone.getDefault().toZoneId()).toString()));

        setColContract(new TableColumn<>("referência"));
        getColContract().setPrefWidth(100);
        getColSymbol().setStyle("-fx-alignment: center-right;");
        getColContract().setCellValueFactory(param -> param.getValue().transaction_idProperty().asString());


        setColTipoNegociacao(new TableColumn<>("Negociação"));
        getColTipoNegociacao().setPrefWidth(92);
        getColTipoNegociacao().setStyle("-fx-alignment: center;");
        getColTipoNegociacao().setCellValueFactory(param -> param.getValue().contrac_typeProperty());

        setColTickCompra(new TableColumn<>("preço entrada"));
        getColTickCompra().setPrefWidth(94);
        getColTickCompra().setStyle("-fx-alignment: center-right;");
        getColTickCompra().setCellValueFactory(cellData -> {
            if (cellData.getValue().tickCompraProperty().getValue() != null)
                return new SimpleStringProperty(ServiceMascara.getValorFormatado(Integer.parseInt(PIP_SIZE.toEnum(getSymbol().getCod()).getDescricao()), cellData.getValue().tickCompraProperty().getValue().doubleValue()));
            return new SimpleStringProperty("");
        });

        setColTickVenda(new TableColumn<>("preço saida"));
        getColTickVenda().setPrefWidth(94);
        getColTickVenda().setStyle("-fx-alignment: center-right;");
        getColTickVenda().setCellValueFactory(cellData -> {
            if (cellData.getValue().tickVendaProperty().getValue() != null)
                return new SimpleStringProperty(ServiceMascara.getValorFormatado(Integer.parseInt(PIP_SIZE.toEnum(getSymbol().getCod()).getDescricao()), cellData.getValue().tickVendaProperty().getValue().doubleValue()));
            return new SimpleStringProperty("");
        });

        setColStakeCompra(new TableColumn<>("preço compra"));
        getColStakeCompra().setPrefWidth(90);
        getColStakeCompra().setStyle("-fx-alignment: center-right;");
        getColStakeCompra().setCellValueFactory(cellData -> {
            if (cellData.getValue().stakeCompraProperty().getValue() != null)
                return new SimpleStringProperty(ServiceMascara.getValorMoeda(cellData.getValue().stakeCompraProperty().getValue().doubleValue() * -1));
            return new SimpleStringProperty("0,00");
        });

        setColStakeVenda(new TableColumn<>("lucro / perda"));
        getColStakeVenda().setPrefWidth(90);
        getColStakeVenda().setStyle("-fx-alignment: center-right;");
        getColStakeVenda().setCellValueFactory(cellData -> {
            if (cellData.getValue().stakeVendaProperty().getValue() != null)
                return new SimpleStringProperty(ServiceMascara
                        .getValorMoeda(
                                cellData.getValue().stakeVendaProperty().getValue().doubleValue()
                                        + cellData.getValue().stakeCompraProperty().getValue().doubleValue()
                        ));
            return new SimpleStringProperty("0,00");
        });

    }

    public void escutarTransacoesTabela() {
        getTransacoesObservableList().addListener((ListChangeListener<? super Transacoes>) change -> {
            Platform.runLater(() -> {
                //tabela_preencher();
                getTbvTransacoes().setItems(getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
                getTbvTransacoes().refresh();
                totalizaTabela();
            });
        });
    }


    public void tabela_preencher() {
        getTbvTransacoes().getColumns().setAll(
                getColDataHoraCompra(), getColContract(), getColTipoNegociacao(),
                getColTickCompra(), getColTickVenda(), getColStakeCompra(), getColStakeVenda()
        );
        getTbvTransacoes().getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        getTbvTransacoes().getSelectionModel().setCellSelectionEnabled(true);
        getTbvTransacoes().setEditable(true);
        getTbvTransacoes().setItems(getTransacoesObservableList().stream()
                .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        totalizaTabela();
    }

    public void totalizaTabela() {
        qtdNExecucaoProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .count());
        qtdNExecucaoAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .count());


        qtdNVitoriaProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                        .count());
        qtdNVitoriaAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                        .count());

        qtdNDerrotaProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                        .count());
        qtdNDerrotaAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                        .count());

        totalApostaProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));
        totalApostaAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .map(Transacoes::getStakeCompra).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .multiply(new BigDecimal(-1)).setScale(2, RoundingMode.HALF_UP));

        totalPremioProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));
        totalPremioAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.isConsolidado())
                        .map(Transacoes::getStakeVenda).reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.HALF_UP));

        totalLucroProperty().setValue(
                getTransacoesObservableList().stream()
                        .filter(transacoes -> transacoes.getSymbol().equals(getSymbol()))
                        .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
        totalLucroAcumuladoProperty().setValue(
                getTransacoesObservableList().stream()
                        .map(transacoes -> transacoes.getStakeVenda().add(transacoes.getStakeCompra()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP));
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

    public TableColumn<Transacoes, String> getColTipoNegociacao() {
        return colTipoNegociacao;
    }

    public void setColTipoNegociacao(TableColumn<Transacoes, String> colTipoNegociacao) {
        this.colTipoNegociacao = colTipoNegociacao;
    }

    public SYMBOL getSymbol() {
        return symbol;
    }

    public void setSymbol(SYMBOL symbol) {
        this.symbol = symbol;
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

    public BigDecimal getTotalLucro() {
        return totalLucro.get();
    }

    public ObjectProperty<BigDecimal> totalLucroProperty() {
        return totalLucro;
    }

    public void setTotalLucro(BigDecimal totalLucro) {
        this.totalLucro.set(totalLucro);
    }

    public BigDecimal getTotalAposta() {
        return totalAposta.get();
    }

    public ObjectProperty<BigDecimal> totalApostaProperty() {
        return totalAposta;
    }

    public void setTotalAposta(BigDecimal totalAposta) {
        this.totalAposta.set(totalAposta);
    }

    public BigDecimal getTotalPremio() {
        return totalPremio.get();
    }

    public ObjectProperty<BigDecimal> totalPremioProperty() {
        return totalPremio;
    }

    public void setTotalPremio(BigDecimal totalPremio) {
        this.totalPremio.set(totalPremio);
    }

    public int getQtdNExecucaoAcumulado() {
        return qtdNExecucaoAcumulado.get();
    }

    public IntegerProperty qtdNExecucaoAcumuladoProperty() {
        return qtdNExecucaoAcumulado;
    }

    public void setQtdNExecucaoAcumulado(int qtdNExecucaoAcumulado) {
        this.qtdNExecucaoAcumulado.set(qtdNExecucaoAcumulado);
    }

    public int getQtdNVitoriaAcumulado() {
        return qtdNVitoriaAcumulado.get();
    }

    public IntegerProperty qtdNVitoriaAcumuladoProperty() {
        return qtdNVitoriaAcumulado;
    }

    public void setQtdNVitoriaAcumulado(int qtdNVitoriaAcumulado) {
        this.qtdNVitoriaAcumulado.set(qtdNVitoriaAcumulado);
    }

    public int getQtdNDerrotaAcumulado() {
        return qtdNDerrotaAcumulado.get();
    }

    public IntegerProperty qtdNDerrotaAcumuladoProperty() {
        return qtdNDerrotaAcumulado;
    }

    public void setQtdNDerrotaAcumulado(int qtdNDerrotaAcumulado) {
        this.qtdNDerrotaAcumulado.set(qtdNDerrotaAcumulado);
    }

    public BigDecimal getTotalApostaAcumulado() {
        return totalApostaAcumulado.get();
    }

    public ObjectProperty<BigDecimal> totalApostaAcumuladoProperty() {
        return totalApostaAcumulado;
    }

    public void setTotalApostaAcumulado(BigDecimal totalApostaAcumulado) {
        this.totalApostaAcumulado.set(totalApostaAcumulado);
    }

    public BigDecimal getTotalPremioAcumulado() {
        return totalPremioAcumulado.get();
    }

    public ObjectProperty<BigDecimal> totalPremioAcumuladoProperty() {
        return totalPremioAcumulado;
    }

    public void setTotalPremioAcumulado(BigDecimal totalPremioAcumulado) {
        this.totalPremioAcumulado.set(totalPremioAcumulado);
    }

    public BigDecimal getTotalLucroAcumulado() {
        return totalLucroAcumulado.get();
    }

    public ObjectProperty<BigDecimal> totalLucroAcumuladoProperty() {
        return totalLucroAcumulado;
    }

    public void setTotalLucroAcumulado(BigDecimal totalLucroAcumulado) {
        this.totalLucroAcumulado.set(totalLucroAcumulado);
    }
}
