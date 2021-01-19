package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Entity(name = "Transacoes")
@Table(name = "transacoes")
public class Transacoes implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectProperty<Token> token = new SimpleObjectProperty<>();
    private StringProperty id = new SimpleStringProperty();
    private ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    private LongProperty transaction_id = new SimpleLongProperty();
    private LongProperty contract_id = new SimpleLongProperty();
    private IntegerProperty dataHoraCompra = new SimpleIntegerProperty();
    private IntegerProperty dataHoraVenda = new SimpleIntegerProperty();
    private IntegerProperty dataHoraExpiry = new SimpleIntegerProperty();
    private StringProperty contract_type = new SimpleStringProperty();
    private StringProperty longcode = new SimpleStringProperty();
    private ObjectProperty<BigDecimal> tickCompra = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> tickVenda = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> stakeCompra = new SimpleObjectProperty<>();
    private ObjectProperty<BigDecimal> stakeVenda = new SimpleObjectProperty<>();
    private BooleanProperty consolidado = new SimpleBooleanProperty(false);

    public Transacoes() {
    }

    public Transacoes(Transaction transaction) {
        int symbolId = transaction.getSymbol().idProperty().intValue();// - 1;
        switch (ACTION.valueOf(transaction.getAction().toUpperCase())) {
            case BUY -> {
                this.token = new SimpleObjectProperty<>(transaction.getToken());
                this.symbol = new SimpleObjectProperty<>(transaction.getSymbol());
                this.transaction_id = new SimpleLongProperty(transaction.getTransaction_id());
                this.contract_id = new SimpleLongProperty(transaction.getContract_id());
                this.dataHoraCompra = new SimpleIntegerProperty(transaction.getTransaction_time());
                StringBuilder contractType = new StringBuilder();
                try {
                    contractType.append(Operacoes.getLastPriceProposal()[symbolId].getValue().getContract_type().getDescricao());
                    switch (Operacoes.getLastPriceProposal()[symbolId].getValue().getContract_type()) {
                        case DIGITOVER -> contractType.append(String.format("_%s,", Operacoes.getLastPriceProposal()[symbolId]
                                .getValue().getBarrier()));
                        case DIGITDIFF -> contractType.append(String.format("_%s", Operacoes.getLastPriceProposal()[symbolId]
                                .getValue().getBarrier()));
                    }
                } catch (Exception ex) {
                    if (transaction.getBarrier().equals("S0P")) {
                        if (transaction.getLongcode().contains("higher")) {
                            contractType.append(CONTRACT_TYPE.CALL);
                        } else if (transaction.getLongcode().contains("lower")) {
                            contractType.append(CONTRACT_TYPE.PUT);
                        }
                    }
                    if (!(ex instanceof NullPointerException))
                        ex.printStackTrace();
                }
                this.contract_type = new SimpleStringProperty(contractType.toString());
                this.longcode = new SimpleStringProperty(transaction.getLongcode());
                this.stakeCompra = new SimpleObjectProperty<>(new BigDecimal(transaction.getAmount().doubleValue()));
                this.stakeVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
                this.consolidado = new SimpleBooleanProperty(false);
                this.dataHoraExpiry = new SimpleIntegerProperty(transaction.getDate_expiry());
//                Operacoes.getTransacoesDAO().merger(this);
            }
            case SELL -> {
                Transacoes transacao = Operacoes.getTransacoesObservableList().stream()
                        .filter(transacoes1 -> transacoes1.getContract_id() == transaction.getContract_id())
                        .findAny().get();

                int index = Operacoes.getTransacoesObservableList().indexOf(transacao);

                this.token = new SimpleObjectProperty<>(transacao.getToken());
                this.symbol = new SimpleObjectProperty<>(transacao.getSymbol());
                this.transaction_id = new SimpleLongProperty(transacao.getTransaction_id());
                this.contract_id = new SimpleLongProperty(transacao.getContract_id());
                this.dataHoraCompra = new SimpleIntegerProperty(transacao.getDataHoraCompra());
                this.contract_type = new SimpleStringProperty(transacao.getContract_type());
                this.longcode = new SimpleStringProperty(transacao.getLongcode());
                this.stakeCompra = new SimpleObjectProperty<>(transacao.getStakeCompra());
                this.tickCompra = new SimpleObjectProperty<>(transacao.getTickCompra());
                this.consolidado = new SimpleBooleanProperty(true);
                this.dataHoraExpiry = new SimpleIntegerProperty(transacao.getDataHoraExpiry());

                this.dataHoraVenda = new SimpleIntegerProperty(transaction.getTransaction_time());

                if (transacao.tickVendaProperty().getValue() != null)
                    this.tickVenda = new SimpleObjectProperty<>(transacao.getTickVenda());
                this.stakeVenda = new SimpleObjectProperty<>(transaction.getAmount());

                Operacoes.getTransacoesObservableList().set(index, this);
//                Operacoes.getTransacoesDAO().merger(this);
            }
        }
    }

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    public Token getToken() {
        return token.get();
    }

    public ObjectProperty<Token> tokenProperty() {
        return token;
    }

    public void setToken(Token token) {
        this.token.set(token);
    }

    @Column(length = 90, nullable = true)
    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    @ManyToOne()
    @JoinColumn(name = "symbol_id", foreignKey = @ForeignKey(name = "fk_transacoes_symbol"), nullable = false)
    public Symbol getSymbol() {
        return symbol.get();
    }

    public ObjectProperty<Symbol> symbolProperty() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol.set(symbol);
    }

    @Column(nullable = false)
    public long getTransaction_id() {
        return transaction_id.get();
    }

    public LongProperty transaction_idProperty() {
        return transaction_id;
    }

    public void setTransaction_id(long transaction_id) {
        this.transaction_id.set(transaction_id);
    }

    @Id
    @Column(nullable = false, unique = true)
    public long getContract_id() {
        return contract_id.get();
    }

    public LongProperty contract_idProperty() {
        return contract_id;
    }

    public void setContract_id(long contract_id) {
        this.contract_id.set(contract_id);
    }

    @Column(nullable = false)
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

    public int getDataHoraExpiry() {
        return dataHoraExpiry.get();
    }

    public IntegerProperty dataHoraExpiryProperty() {
        return dataHoraExpiry;
    }

    public void setDataHoraExpiry(int dataHoraExpiry) {
        this.dataHoraExpiry.set(dataHoraExpiry);
    }

    @Column(length = 30, nullable = false)
    public String getContract_type() {
        return contract_type.get();
    }

    public StringProperty contract_typeProperty() {
        return contract_type;
    }

    public void setContract_type(String contract_type) {
        this.contract_type.set(contract_type);
    }

    @Column(length = 200, nullable = false)
    public String getLongcode() {
        return longcode.get();
    }

    public StringProperty longcodeProperty() {
        return longcode;
    }

    public void setLongcode(String longcode) {
        this.longcode.set(longcode);
    }

    @Column(length = 19)
    public BigDecimal getTickCompra() {
        return tickCompra.get();
    }

    public ObjectProperty<BigDecimal> tickCompraProperty() {
        return tickCompra;
    }

    public void setTickCompra(BigDecimal tickCompra) {
        this.tickCompra.set(tickCompra);
    }

    @Column(length = 19)
    public BigDecimal getTickVenda() {
        return tickVenda.get();
    }

    public ObjectProperty<BigDecimal> tickVendaProperty() {
        return tickVenda;
    }

    public void setTickVenda(BigDecimal tickVenda) {
        this.tickVenda.set(tickVenda);
    }

    @Column(length = 19, nullable = false)
    public BigDecimal getStakeCompra() {
        return stakeCompra.get();
    }

    public ObjectProperty<BigDecimal> stakeCompraProperty() {
        return stakeCompra;
    }

    public void setStakeCompra(BigDecimal stakeCompra) {
        this.stakeCompra.set(stakeCompra);
    }

    @Column(length = 19, nullable = false)
    public BigDecimal getStakeVenda() {
        return stakeVenda.get();
    }

    public ObjectProperty<BigDecimal> stakeVendaProperty() {
        return stakeVenda;
    }

    public void setStakeVenda(BigDecimal stakeVenda) {
        this.stakeVenda.set(stakeVenda);
    }

    @Column(length = 1, nullable = false)
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
                ", contract_id=" + contract_id +
                ", dataHoraCompra=" + dataHoraCompra +
                ", dataHoraVenda=" + dataHoraVenda +
                ", contract_type=" + contract_type +
                ", longcode=" + longcode +
                ", tickCompra=" + tickCompra +
                ", tickVenda=" + tickVenda +
                ", stakeCompra=" + stakeCompra +
                ", stakeVenda=" + stakeVenda +
                ", consolidado=" + consolidado +
                '}';
    }

}
