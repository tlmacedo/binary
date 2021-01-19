package br.com.tlmacedo.binary.controller;


import br.com.tlmacedo.binary.model.*;
import br.com.tlmacedo.binary.model.Enums.CONTRAC_TYPE;
import br.com.tlmacedo.binary.model.Enums.Error;
import br.com.tlmacedo.binary.model.Enums.MSG_TYPE;
import br.com.tlmacedo.binary.model.Enums.SYMBOL;
import br.com.tlmacedo.binary.services.ServiceMascara;
import br.com.tlmacedo.binary.services.UtilJson;
import javafx.application.Platform;
import javafx.beans.property.*;
import jdk.jshell.execution.Util;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

public class WSClient extends WebSocketListener {

    private WebSocket myWebSocket;
    //    private SYMBOL symbol;
    private Msg_type msg_type;
    private Error error;
    private History history;
    private Authorize authorize;
    private Tick tick;
    private Proposal proposal;
    private PriceProposal priceProposal;
    private Buy buy;
    private Transaction transaction;
    private BooleanProperty tickSubindo = new SimpleBooleanProperty();
    private StringProperty ultimoTick = new SimpleStringProperty();
    private IntegerProperty ultimoDigito = new SimpleIntegerProperty();

    public WSClient() {
    }

    public void connect() {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("wss://ws.binaryws.com/websockets/v3?app_id=23487").build();
        setMyWebSocket(client.newWebSocket(request, this));
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        openOrClosedSocket(true);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        openOrClosedSocket(false);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        boolean msgTerminalGeral = true;
        setMsg_type((Msg_type) UtilJson.getMsg_Type(text));

//        if (msgTerminalGeral)
//            if (!getMsg_type().getMsg_type().equals(MSG_TYPE.TICK))
//                System.out.printf("...%s\n", text);

        switch (getMsg_type().getMsg_type()) {
            case AUTHORIZE -> {
                setAuthorize((Authorize) UtilJson.getObjectFromString(text, Authorize.class));
                refreshAuthorize(getAuthorize());
            }
            case TICK -> {
                setTick((Tick) UtilJson.getObjectFromString(text, Tick.class));
                refreshTick(getTick());
            }
            case PROPOSAL -> {
                if (!msgTerminalGeral)
                    System.out.printf("**-*-*%s*-*-**\t%s\n", getMsg_type().getMsg_type(), text);
                Object object;
                if ((object = UtilJson.getObjectFromString(text, Proposal.class)).getClass().getSimpleName().toLowerCase().equals("proposal"))
                    setProposal((Proposal) object);
                else
                    setError((Error) object);
                SYMBOL symbol = SYMBOL.valueOf(UtilJson.getEcho_RegPartJson(text, "symbol"));
                String type = UtilJson.getEcho_RegPartJson(text, "contract_type");
                Integer barrier = Integer.valueOf(UtilJson.getEcho_RegPartJson(text, "barrier"));
                if (getError() != null) {
                    getProposal().setError(getError());
                    getProposal().getError().setContrac_type(CONTRAC_TYPE.valueOf(type));
                    getProposal().getError().setBarrier(Integer.valueOf(barrier));
                    Operacoes.getProposal()[symbol.getCod()][barrier].setValue(getProposal());
                } else {
                    switch (type.toLowerCase()) {
                        case "digitodd" -> Operacoes.getProposal()[symbol.getCod()][1].setValue(getProposal());
                        case "digiteven" -> Operacoes.getProposal()[symbol.getCod()][0].setValue(getProposal());
                        case "put" -> {
                        }
                        case "call" -> {
                        }
                        default -> {
                            Operacoes.getProposal()[symbol.getCod()][barrier].setValue(getProposal());
                        }
                    }
                }
            }
            case BUY -> {
                if (!msgTerminalGeral)
                    System.out.printf("**-*-*%s*-*-**\t%s\n", getMsg_type().getMsg_type(), text);
                Object object;
                if ((object = UtilJson.getObjectFromString(text, Buy.class)).getClass().getSimpleName().toLowerCase().equals("buy"))
                    setBuy((Buy) object);
                else
                    setError((Error) object);
                String buyId = UtilJson.getEcho_RegPartJson(text, "buy");
                if (getError() != null) {
                    getBuy().setError(getError());
                    getBuy().getError().setBuyId(buyId);
                }
            }
            case HISTORY -> {
                if (!msgTerminalGeral)
                    System.out.printf("**-*-*%s*-*-**\t%s\n", getMsg_type().getMsg_type(), text);
                SYMBOL symbol = SYMBOL.valueOf(UtilJson.getEcho_RegPartJson(text, "ticks_history"));
                UtilJson.getHistoryFromString(symbol, text);
            }
            case TRANSACTION -> {
                if (!msgTerminalGeral)
                    System.out.printf("**-*-*%s*-*-**\t%s\n", getMsg_type().getMsg_type(), text);
                setTransaction((Transaction) UtilJson.getObjectFromString(text, Transaction.class));
                refreshTransactions(getTransaction());
            }
            default -> {
//                if (!msgTerminalGeral)
//                    System.out.printf("**-default-**%s\n", text);
            }
        }
    }

    private void openOrClosedSocket(boolean value) {
        Operacoes.ws_ConectadoProperty().setValue(value);
    }

    private void refreshAuthorize(Authorize authorize) {
        Platform.runLater(() -> Operacoes.authorizeObjectProperty().setValue(authorize));
    }

    private void refreshTick(Tick tick) {
        SYMBOL symbol = SYMBOL.valueOf(tick.getSymbol());
        if (Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].size() > 0) {
            tickSubindoProperty().setValue((tick.getQuote().doubleValue() >
                    Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].get(0).getPrice().doubleValue()));
        }
        Platform.runLater(() -> {
            //            ultimoTickProperty().setValue(tick.toString());
//            ultimoDigitoProperty().setValue(tick.getUltimoDigt());
//            if (tick.getSymbol().equals(SYMBOL.R_10.getDescricao()))
//            System.out.printf("wsClient: %s\tdigit: %s\n", tick.toString(), tick.getUltimoDigt());

//            Operacoes.getUltimoTick()[symbol.getCod()].setValue(tick.toString());
//            Operacoes.getTickSubindo()[symbol.getCod()].setValue(tickSubindoProperty().getValue());
        });
//        });
        Platform.runLater(() -> {
            if (Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].stream()
                    .noneMatch(historicoTicks -> historicoTicks.getTime().equals(tick.getEpoch())))
                Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].add(0,
                        new HistoricoTicks(tick.getQuote(), tick.getPip_size(), tick.getEpoch()));
            while (Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].size() > Operacoes.getQtdTicksAnalisar())
                Operacoes.getHistoricoTicksObservableList()[symbol.getCod()]
                        .remove(Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].get(Operacoes.getQtdTicksAnalisar()));
            Operacoes.getUltimoTick()[symbol.getCod()].setValue(tick.toString());
            Operacoes.getTickSubindo()[symbol.getCod()].setValue(tickSubindoProperty().getValue());
        });
    }

    public void refreshTransactions(Transaction transaction) {
        if (transaction.getSymbol() != null) {
            Operacoes.getTransactionObservableList()[transaction.getSymbol().getCod()].add(0, transaction);
        } else {
            if (transaction.getId() != null) {
                Operacoes.transactionsAuthorizedsProperty().setValue(transaction.getId());
            }
        }

    }

    public WebSocket getMyWebSocket() {
        return myWebSocket;
    }

    public void setMyWebSocket(WebSocket myWebSocket) {
        this.myWebSocket = myWebSocket;
    }

    public Msg_type getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(Msg_type msg_type) {
        this.msg_type = msg_type;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {
        this.tick = tick;
    }

    public String getUltimoTick() {
        return ultimoTick.get();
    }

    public StringProperty ultimoTickProperty() {
        return ultimoTick;
    }

    public void setUltimoTick(String ultimoTick) {
        this.ultimoTick.set(ultimoTick);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public boolean isTickSubindo() {
        return tickSubindo.get();
    }

    public BooleanProperty tickSubindoProperty() {
        return tickSubindo;
    }

    public void setTickSubindo(boolean tickSubindo) {
        this.tickSubindo.set(tickSubindo);
    }

    public int getUltimoDigito() {
        return ultimoDigito.get();
    }

    public IntegerProperty ultimoDigitoProperty() {
        return ultimoDigito;
    }

    public void setUltimoDigito(int ultimoDigito) {
        this.ultimoDigito.set(ultimoDigito);
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public PriceProposal getPriceProposal() {
        return priceProposal;
    }

    public void setPriceProposal(PriceProposal priceProposal) {
        this.priceProposal = priceProposal;
    }

    public Buy getBuy() {
        return buy;
    }

    public void setBuy(Buy buy) {
        this.buy = buy;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}