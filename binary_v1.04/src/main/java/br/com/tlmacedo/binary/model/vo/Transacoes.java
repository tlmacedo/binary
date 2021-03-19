package br.com.tlmacedo.binary.model.vo;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.services.Service_DataTime;
import br.com.tlmacedo.binary.services.Service_TelegramNotifier;
import br.com.tlmacedo.binary.services.Util_Json;
import javafx.beans.property.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity(name = "transacoes")
@Table(name = "transacoes")
public class Transacoes implements Serializable {
    public static final long serialVersionUID = 1L;

    ObjectProperty<ContaToken> contaToken = new SimpleObjectProperty<>();
    IntegerProperty t_id = new SimpleIntegerProperty();
    ObjectProperty<TimeFrame> tFrame = new SimpleObjectProperty<>();
    IntegerProperty s_id = new SimpleIntegerProperty();
    ObjectProperty<Symbol> symbol = new SimpleObjectProperty<>();
    LongProperty transaction_id = new SimpleLongProperty();
    LongProperty contract_id = new SimpleLongProperty();
    IntegerProperty dataHoraCompra = new SimpleIntegerProperty(0);
    IntegerProperty dataHoraVenda = new SimpleIntegerProperty(0);
    IntegerProperty dataHoraExpiry = new SimpleIntegerProperty(0);
    StringProperty contract_type = new SimpleStringProperty();
    StringProperty longcode = new SimpleStringProperty();
    ObjectProperty<BigDecimal> tickCompra = new SimpleObjectProperty<>(BigDecimal.ZERO);
    ObjectProperty<BigDecimal> tickVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
    ObjectProperty<BigDecimal> tickNegociacaoInicio = new SimpleObjectProperty<>(BigDecimal.ZERO);
    ObjectProperty<BigDecimal> stakeCompra = new SimpleObjectProperty<>(BigDecimal.ZERO);
    ObjectProperty<BigDecimal> stakeVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
    ObjectProperty<BigDecimal> stakeResult = new SimpleObjectProperty<>(BigDecimal.ZERO);
    BooleanProperty consolidado = new SimpleBooleanProperty(false);

    public Transacoes() {
    }

    public void isBUY(Transaction transaction) {

        this.t_id = new SimpleIntegerProperty(transaction.getT_id());
        this.tFrame = new SimpleObjectProperty<>(
                Operacoes.getTimeFrameObservableList().get(getT_id()));
        this.s_id = new SimpleIntegerProperty(transaction.getS_id());
        this.symbol = new SimpleObjectProperty<>(
                Operacoes.getSymbolObservableList().get(getS_id()));
        this.contaToken = new SimpleObjectProperty<>(Operacoes.getContaToken());
        this.transaction_id = new SimpleLongProperty(transaction.getTransaction_id());
        this.contract_id = new SimpleLongProperty(transaction.getContract_id());
        this.dataHoraCompra = new SimpleIntegerProperty(transaction.getTransaction_time());
        //this.dataHoraVenda = dataHoraCompra;
        this.dataHoraExpiry = new SimpleIntegerProperty(transaction.getDate_expiry());
        CONTRACT_TYPE contract = null;
        if (transaction.getBarrier().equals("S0P")) {
            if (transaction.getLongcode().toLowerCase().contains(" higher "))
                contract = CONTRACT_TYPE.CALL;
            else
                contract = CONTRACT_TYPE.PUT;
            this.contract_type = new SimpleStringProperty(contract.getDescricao());
        }
        this.longcode = new SimpleStringProperty(transaction.getLongcode());
//        this.tickCompra = new SimpleObjectProperty<>(BigDecimal.ZERO);
//        this.tickVenda = new SimpleObjectProperty<>(BigDecimal.ZERO);
//        this.tickNegociacaoInicio = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.stakeCompra = new SimpleObjectProperty<>(transaction.getAmount().setScale(2, RoundingMode.HALF_UP));
        this.stakeVenda = new SimpleObjectProperty<>(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
//        this.stakeResult = new SimpleObjectProperty<>(BigDecimal.ZERO);
        this.consolidado = new SimpleBooleanProperty(false);

//        Operacoes.getTransacoesObservableList().add(Operacoes.getTransacoesDAO().merger(this));

        Operacoes.getTransacoesObservableList().add(this);

        Operacoes.getRobo().gerarNovosContratos(getT_id(), getS_id(), contract.getCod(), 2);

        Service_TelegramNotifier.sendMsgTransacoesAction(this, transaction.getBalance(), ACTION.BUY);

    }

    public void isSELL(Transaction transaction) throws Exception {

        int indexTransacoes = Operacoes.getTransacoesObservableList().indexOf(this);

        this.dataHoraVenda = new SimpleIntegerProperty(transaction.getTransaction_time());
        this.stakeVenda = new SimpleObjectProperty<>(transaction.getAmount().setScale(2, RoundingMode.HALF_UP));
        this.stakeResult = new SimpleObjectProperty<>(getStakeVenda().add(getStakeCompra()).setScale(2, RoundingMode.HALF_UP));

        if (Operacoes.getContaToken().iscReal())
            Operacoes.getTransacoesObservableList().set(indexTransacoes, Operacoes.getTransacoesDAO().merger(this));
        else
            Operacoes.getTransacoesObservableList().set(indexTransacoes, this);

        Operacoes.getQtdLossSymbol()[getT_id()][getS_id()].setValue(getStakeVenda().compareTo(BigDecimal.ZERO) > 0
                ? 0 : Operacoes.getQtdLossSymbol()[getT_id()][getS_id()].getValue() + 1);

        if (Operacoes.isRoboMonitorando())
            if (getStakeVenda().compareTo(BigDecimal.ZERO) > 0)
                Operacoes.getRobo().gerarNovosContratos(getT_id(), getS_id(),
                        null, getStakeVenda().compareTo(BigDecimal.ZERO) > 0 ? 0 : 2);

        Service_TelegramNotifier.sendMsgTransacoesAction(this, transaction.getBalance(), ACTION.SELL);

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

    @Column(length = 2, nullable = false)
    public int getT_id() {
        return t_id.get();
    }

    public IntegerProperty t_idProperty() {
        return t_id;
    }

    public void setT_id(int t_id) {
        this.t_id.set(t_id);
    }

    @ManyToOne(fetch = FetchType.LAZY)
    public TimeFrame gettFrame() {
        return tFrame.get();
    }

    public ObjectProperty<TimeFrame> tFrameProperty() {
        return tFrame;
    }

    public void settFrame(TimeFrame tFrame) {
        this.tFrame.set(tFrame);
    }

    @Column(length = 2, nullable = false)
    public int getS_id() {
        return s_id.get();
    }

    public IntegerProperty s_idProperty() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id.set(s_id);
    }

    @ManyToOne(fetch = FetchType.LAZY)
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

    @Column(length = 19, scale = 4)
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

    @Column(length = 19, scale = 4)
    public BigDecimal getTickNegociacaoInicio() {
        return tickNegociacaoInicio.get();
    }

    public ObjectProperty<BigDecimal> tickNegociacaoInicioProperty() {
        return tickNegociacaoInicio;
    }

    public void setTickNegociacaoInicio(BigDecimal tickNegociacaoInicio) {
        this.tickNegociacaoInicio.set(tickNegociacaoInicio);
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

    @Column(length = 19, scale = 2)
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

    @Column(length = 19, scale = 2)
    public BigDecimal getStakeResult() {
        return stakeResult.get();
    }

    public ObjectProperty<BigDecimal> stakeResultProperty() {
        return stakeResult;
    }

    public void setStakeResult(BigDecimal stakeResult) {
        this.stakeResult.set(stakeResult);
    }

    @Override
    public String toString() {
        return "Transacoes{" +
                "contaToken=" + contaToken +
                ", t_id=" + t_id +
                ", tFrame=" + tFrame +
                ", s_id=" + s_id +
                ", symbol=" + symbol +
                ", transaction_id=" + transaction_id +
                ", contract_id=" + contract_id +
                ", dataHoraCompra=" + dataHoraCompra +
                ", dataHoraVenda=" + dataHoraVenda +
                ", dataHoraExpiry=" + dataHoraExpiry +
                ", contract_type=" + contract_type +
                ", longcode=" + longcode +
                ", tickCompra=" + tickCompra +
                ", tickVenda=" + tickVenda +
                ", tickNegociacaoInicio=" + tickNegociacaoInicio +
                ", stakeCompra=" + stakeCompra +
                ", stakeVenda=" + stakeVenda +
                ", consolidado=" + consolidado +
                ", stakeResult=" + stakeResult +
                '}';
    }
}
