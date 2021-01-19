package br.com.tlmacedo.binary.model;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.Enums.ACTION;
import br.com.tlmacedo.binary.model.Enums.SYMBOL;
import javafx.beans.property.*;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class Transacoes {

    private Integer id;
    private ObjectProperty<SYMBOL> symbol = new SimpleObjectProperty<>();
    private LongProperty transaction_id = new SimpleLongProperty();
    private LongProperty contract_id = new SimpleLongProperty();
    private IntegerProperty dataHoraCompra = new SimpleIntegerProperty();
    private IntegerProperty dataHoraVenda = new SimpleIntegerProperty();
    private StringProperty contrac_type = new SimpleStringProperty();
    private StringProperty longcode = new SimpleStringProperty();
    private ObjectProperty<Number> tickCompra = new SimpleObjectProperty<>();
    private ObjectProperty<Number> tickVenda = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> stakeCompra = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> stakeVenda = new SimpleObjectProperty<>();
    private ObjectProperty<Number> proximaStake = new SimpleObjectProperty<>();
    private BooleanProperty consolidado = new SimpleBooleanProperty(false);


    public Transacoes(Transaction transaction) {
        switch (ACTION.valueOf(transaction.getAction().toUpperCase())) {
            case BUY -> {
                this.symbol.setValue(transaction.getSymbol());
                this.transaction_id = new SimpleLongProperty(transaction.getTransaction_id());
                this.contract_id = new SimpleLongProperty(transaction.getContract_id());
                this.dataHoraCompra = new SimpleIntegerProperty(transaction.getTransaction_time());
                this.contrac_type = new SimpleStringProperty(
                        String.format("%s_%s",
                                Operacoes.getLastPriceProposal()[getSymbol().getCod()].getValue().getContract_type(),
                                Operacoes.getLastPriceProposal()[getSymbol().getCod()].getValue().getBarrier())
                );
                this.longcode = new SimpleStringProperty(transaction.getLongcode());
                this.stakeCompra = new SimpleObjectProperty<>(new BigDecimal(transaction.getAmount().doubleValue()));
                this.stakeVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
                this.consolidado = new SimpleBooleanProperty(false);
            }
            case SELL -> {
                try {
                    Transacoes transacao = Operacoes.getTransacoesEfetuadasObservableList().stream()
                            .filter(transacoes -> transacoes.getContract_id() == transaction.getContract_id())
                            .findAny().get();
                    this.symbol.setValue(transacao.getSymbol());
                    this.transaction_id = new SimpleLongProperty(transacao.getTransaction_id());
                    this.contract_id = new SimpleLongProperty(transacao.getContract_id());
                    this.dataHoraCompra = new SimpleIntegerProperty(transacao.getDataHoraCompra());
                    this.contrac_type = new SimpleStringProperty(transacao.getContrac_type());
                    this.longcode = new SimpleStringProperty(transacao.getLongcode());
                    this.tickCompra = new SimpleObjectProperty<>(transacao.getTickCompra());
                    this.stakeCompra = new SimpleObjectProperty<>(transacao.getStakeCompra());
                    this.consolidado = new SimpleBooleanProperty(true);

                    int index = Operacoes.getTransacoesEfetuadasObservableList().indexOf(transacao);

                    this.dataHoraVenda = new SimpleIntegerProperty(transaction.getTransaction_time());
                    this.tickVenda = new SimpleObjectProperty<>(Operacoes.getHistoricoTicksObservableList()[transaction.getSymbol().getCod()]
                            .stream()
                            .filter(historicoDeTicks -> historicoDeTicks.getTime().equals(transaction.getDate_expiry() % 2 == 0
                                    ? transaction.getDate_expiry()
                                    : transaction.getDate_expiry() - 1
                            ))
                            .findFirst().get().getPrice());
                    this.stakeVenda = new SimpleObjectProperty<>(new BigDecimal(transaction.getAmount().doubleValue()));
                    if (getStakeVenda().compareTo(BigDecimal.ZERO) == 0)
                        Operacoes.getQtdDerrotas()[getSymbol().getCod()].setValue(Operacoes.getQtdDerrotas()[getSymbol().getCod()].getValue() + 1);
                    else
                        Operacoes.getQtdDerrotas()[getSymbol().getCod()].setValue(0);

                    Operacoes.getTransacoesEfetuadasObservableList().set(index, this);
                } catch (Exception ex) {
                    if (!(ex instanceof NoSuchElementException))
                        ex.printStackTrace();
                }
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SYMBOL getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<SYMBOL> symbolProperty() {
        return symbol;
    }

    public void setSymbol(SYMBOL symbol) {
        this.symbol.set(symbol);
    }

    public long getTransaction_id() {
        return transaction_id.get();
    }

    public LongProperty transaction_idProperty() {
        return transaction_id;
    }

    public void setTransaction_id(long transaction_id) {
        this.transaction_id.set(transaction_id);
    }

    public long getContract_id() {
        return contract_id.get();
    }

    public LongProperty contract_idProperty() {
        return contract_id;
    }

    public void setContract_id(long contract_id) {
        this.contract_id.set(contract_id);
    }

    public int getDataHoraCompra() {
        return dataHoraCompra.get();
    }

    public IntegerProperty dataHoraCompraProperty() {
        return dataHoraCompra;
    }

    public void setDataHoraCompra(int dataHoraCompra) {
        this.dataHoraCompra.set(dataHoraCompra);
    }

    public int getDataHoraVenda() {
        return dataHoraVenda.get();
    }

    public IntegerProperty dataHoraVendaProperty() {
        return dataHoraVenda;
    }

    public void setDataHoraVenda(int dataHoraVenda) {
        this.dataHoraVenda.set(dataHoraVenda);
    }

    public String getContrac_type() {
        return contrac_type.get();
    }

    public StringProperty contrac_typeProperty() {
        return contrac_type;
    }

    public void setContrac_type(String contrac_type) {
        this.contrac_type.set(contrac_type);
    }

    public String getLongcode() {
        return longcode.get();
    }

    public StringProperty longcodeProperty() {
        return longcode;
    }

    public void setLongcode(String longcode) {
        this.longcode.set(longcode);
    }

    public Number getTickCompra() {
        return tickCompra.get();
    }

    public ObjectProperty<Number> tickCompraProperty() {
        return tickCompra;
    }

    public void setTickCompra(Number tickCompra) {
        this.tickCompra.set(tickCompra);
    }

    public Number getTickVenda() {
        return tickVenda.get();
    }

    public ObjectProperty<Number> tickVendaProperty() {
        return tickVenda;
    }

    public void setTickVenda(Number tickVenda) {
        this.tickVenda.set(tickVenda);
    }

    public BigDecimal getStakeCompra() {
        return stakeCompra.get();
    }

    public ObjectProperty<BigDecimal> stakeCompraProperty() {
        return stakeCompra;
    }

    public void setStakeCompra(BigDecimal stakeCompra) {
        this.stakeCompra.set(stakeCompra);
    }

    public BigDecimal getStakeVenda() {
        return stakeVenda.get();
    }

    public ObjectProperty<BigDecimal> stakeVendaProperty() {
        return stakeVenda;
    }

    public void setStakeVenda(BigDecimal stakeVenda) {
        this.stakeVenda.set(stakeVenda);
    }

    public Number getProximaStake() {
        return proximaStake.get();
    }

    public ObjectProperty<Number> proximaStakeProperty() {
        return proximaStake;
    }

    public void setProximaStake(Number proximaStake) {
        this.proximaStake.set(proximaStake);
    }

    public boolean isConsolidado() {
        return consolidado.get();
    }

    public BooleanProperty consolidadoProperty() {
        return consolidado;
    }

    public void setConsolidado(boolean consolidado) {
        this.consolidado.set(consolidado);
    }

    @Override
    public String toString() {
        return "Transacoes{" +
                "id=" + id +
                ", symbol=" + symbol +
                ", transaction_id=" + transaction_id +
                ", contract_id=" + contract_id +
                ", dataHoraCompra=" + dataHoraCompra +
                ", dataHoraVenda=" + dataHoraVenda +
                ", contrac_type=" + contrac_type +
                ", longcode=" + longcode +
                ", tickCompra=" + tickCompra +
                ", tickVenda=" + tickVenda +
                ", stakeCompra=" + stakeCompra +
                ", stakeVenda=" + stakeVenda +
                ", proximaStake=" + proximaStake +
                ", consolidado=" + consolidado +
                '}';
    }
}
