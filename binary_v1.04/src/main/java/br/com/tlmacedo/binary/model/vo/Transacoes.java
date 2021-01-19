package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "Transacoes")
@Table(name = "transacoes")
public class Transacoes implements Serializable {
    public static final long serialVersionUID = 1L;

    ObjectProperty<ContaToken> contaToken = new SimpleObjectProperty<>();
    ObjectProperty<Symbol> activeSymbol = new SimpleObjectProperty<>();
    LongProperty transaction_id = new SimpleLongProperty();
    LongProperty contract_id = new SimpleLongProperty();
    IntegerProperty dataHoraCompra = new SimpleIntegerProperty();
    IntegerProperty dataHoraVenda = new SimpleIntegerProperty();
    IntegerProperty dataHoraExpiry = new SimpleIntegerProperty();
    StringProperty contract_type = new SimpleStringProperty();
    StringProperty longcode = new SimpleStringProperty();
    ObjectProperty<BigDecimal> tickCompra = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> tickVenda = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> stakeCompra = new SimpleObjectProperty<>();
    ObjectProperty<BigDecimal> stakeVenda = new SimpleObjectProperty<>();
    BooleanProperty consolidado = new SimpleBooleanProperty(false);

    Integer activeSymbolId;
    CONTRACT_TYPE contractTypeLastPriceProposal;
    String lastBarrier;
    StringBuilder stbContract_Type = new StringBuilder();

    public Transacoes() {

    }

    public Transacoes(Symbol symbol) {

//        setSymbol(symbol);
//        setSymbolId((int) (getSymbol().getId() - 1));
//
//        setContractTypeLastPriceProposal(CONTRACT_TYPE.valueOf(Operacoes.getLastPriceProposal()[getSymbolId()].getValue().getContract_type().getDescricao().toUpperCase()));
//        setLastBarrier(Operacoes.getLastPriceProposal()[getSymbolId()].getValue().getBarrier());

    }

    public void newTransacao_BUY(Transaction transaction) {

//        this.contaToken = new SimpleObjectProperty<>(transaction.getContaToken());
////                this.symbol = new SimpleObjectProperty<>(transaction.getSymbol());
//        setSymbol(getSymbol());
//        this.transaction_id = new SimpleLongProperty(transaction.getTransaction_id());
//        this.contract_id = new SimpleLongProperty(transaction.getContract_id());
//        this.dataHoraCompra = new SimpleIntegerProperty(transaction.getTransaction_time());
//        try {
//            getStbContract_Type().append(getContractTypeLastPriceProposal().getDescricao());
//            if (getLastBarrier() != null)
//                getStbContract_Type().append(String.format("_%s", getLastBarrier()));
//        } catch (Exception ex) {
//            if (transaction.getBarrier().equals("S0P")) {
//                if (transaction.getLongcode().contains("higher"))
//                    getStbContract_Type().append(CONTRACT_TYPE.CALL);
//                else if (transaction.getLongcode().contains("lower"))
//                    getStbContract_Type().append(CONTRACT_TYPE.PUT);
//            }
//            if (!(ex instanceof NullPointerException))
//                ex.printStackTrace();
//        }
//        this.contract_type = new SimpleStringProperty(getStbContract_Type().toString());
//        this.longcode = new SimpleStringProperty(transaction.getLongcode());
//        this.stakeCompra = new SimpleObjectProperty<>(transaction.getAmount().setScale(2, RoundingMode.HALF_UP));
//        this.dataHoraExpiry = new SimpleIntegerProperty(transaction.getDate_expiry());
//        this.stakeVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
//
//        Operacoes.getTransacoesObservableList().add(0, this);

    }

    public void newTransacao_SELL(Transaction transaction) {

////        Transacoes transacao = Operacoes.getTransacoesObservableList().stream()
////                .filter(transacoes -> transacoes.getContract_id() == transaction.getContract_id())
////                .findFirst().get();
//
//        int index = Operacoes.getTransacoesObservableList().indexOf(this);
//
////        this.contaToken = new SimpleObjectProperty<>(transacao.getContaToken());
//////                this.symbol = new SimpleObjectProperty<>(transacao.getSymbol());
////        setSymbol(getSymbol());
////        this.transaction_id = new SimpleLongProperty(transacao.getTransaction_id());
////        this.contract_id = new SimpleLongProperty(transacao.getContract_id());
////        this.dataHoraCompra = new SimpleIntegerProperty(transacao.getDataHoraCompra());
////        this.contract_type = new SimpleStringProperty(transacao.getContract_type());
////        this.longcode = new SimpleStringProperty(transacao.getLongcode());
////        this.stakeCompra = new SimpleObjectProperty<>(transacao.getStakeCompra());
////        this.tickCompra = new SimpleObjectProperty<>(transacao.getTickCompra());
//        this.dataHoraExpiry = new SimpleIntegerProperty(transaction.getDate_expiry());
//        this.consolidado = new SimpleBooleanProperty(true);
//
//        this.stakeVenda = new SimpleObjectProperty<>(transaction.getAmount().setScale(2, RoundingMode.HALF_UP));
//        this.dataHoraVenda = new SimpleIntegerProperty(transaction.getTransaction_time());
//
////        if (transacao.tickVenda != null)
////            this.tickVenda = new SimpleObjectProperty<>(transacao.getTickVenda());
//
//        Operacoes.getTransacoesObservableList().set(index, this);
//
//        Operacoes.getTransacoesDAO().merger(this);

    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_token_id", foreignKey = @ForeignKey(name = "fk_transacoes_conta"), nullable = false)
    public ContaToken getContaToken() {
        return contaToken.get();
    }

    public ObjectProperty<ContaToken> contaTokenProperty() {
        return contaToken;
    }

    public void setContaToken(ContaToken contaToken) {
        this.contaToken.set(contaToken);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activeSymbol_id", foreignKey = @ForeignKey(name = "fk_transacoes_activeSymbol"), nullable = false)
    public Symbol getActiveSymbol() {
        return activeSymbol.get();
    }

    public ObjectProperty<Symbol> activeSymbolProperty() {
        return activeSymbol;
    }

    public void setActiveSymbol(Symbol symbol) {
        this.activeSymbol.set(symbol);
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

    @Column
    public int getDataHoraVenda() {
        return dataHoraVenda.get();
    }

    public IntegerProperty dataHoraVendaProperty() {
        return dataHoraVenda;
    }

    public void setDataHoraVenda(int dataHoraVenda) {
        this.dataHoraVenda.set(dataHoraVenda);
    }

    @Column
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

    @Column(length = 19, scale = 4, nullable = false)
    public BigDecimal getTickCompra() {
        return tickCompra.get();
    }

    public ObjectProperty<BigDecimal> tickCompraProperty() {
        return tickCompra;
    }

    public void setTickCompra(BigDecimal tickCompra) {
        this.tickCompra.set(tickCompra);
    }

    @Column(length = 19, scale = 4)
    public BigDecimal getTickVenda() {
        return tickVenda.get();
    }

    public ObjectProperty<BigDecimal> tickVendaProperty() {
        return tickVenda;
    }

    public void setTickVenda(BigDecimal tickVenda) {
        this.tickVenda.set(tickVenda);
    }

    @Column(length = 19, scale = 2, nullable = false)
    public BigDecimal getStakeCompra() {
        return stakeCompra.get();
    }

    public ObjectProperty<BigDecimal> stakeCompraProperty() {
        return stakeCompra;
    }

    public void setStakeCompra(BigDecimal stakeCompra) {
        this.stakeCompra.set(stakeCompra);
    }

    @Column(length = 19, scale = 2, nullable = false)
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

    @Transient
    public Integer getActiveSymbolId() {
        return activeSymbolId;
    }

    public void setActiveSymbolId(Integer activeSymbolId) {
        this.activeSymbolId = activeSymbolId;
    }

    @Transient
    public CONTRACT_TYPE getContractTypeLastPriceProposal() {
        return contractTypeLastPriceProposal;
    }

    public void setContractTypeLastPriceProposal(CONTRACT_TYPE contractTypeLastPriceProposal) {
        this.contractTypeLastPriceProposal = contractTypeLastPriceProposal;
    }

    @Transient
    public String getLastBarrier() {
        return lastBarrier;
    }

    public void setLastBarrier(String lastBarrier) {
        this.lastBarrier = lastBarrier;
    }

    @Transient
    public StringBuilder getStbContract_Type() {
        return stbContract_Type;
    }

    public void setStbContract_Type(StringBuilder stbContract_Type) {
        this.stbContract_Type = stbContract_Type;
    }

    @Override
    public String toString() {
        return "Transacoes{" +
                "contaToken=" + contaToken +
                ", activeSymbol=" + activeSymbol +
                ", transaction_id=" + transaction_id +
                ", contract_id=" + contract_id +
                ", dataHoraCompra=" + dataHoraCompra +
                ", dataHoraVenda=" + dataHoraVenda +
                ", dataHoraExpiry=" + dataHoraExpiry +
                ", contract_type=" + contract_type +
                ", longcode=" + longcode +
                ", tickCompra=" + tickCompra +
                ", tickVenda=" + tickVenda +
                ", stakeCompra=" + stakeCompra +
                ", stakeVenda=" + stakeVenda +
                ", consolidado=" + consolidado +
                ", activeSymbolId=" + activeSymbolId +
                ", contractTypeLastPriceProposal=" + contractTypeLastPriceProposal +
                ", lastBarrier='" + lastBarrier + '\'' +
                ", stbContract_Type=" + stbContract_Type +
                '}';
    }
}
