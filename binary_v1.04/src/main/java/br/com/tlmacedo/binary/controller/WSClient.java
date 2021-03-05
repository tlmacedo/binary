package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.MSG_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Util_Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

import static br.com.tlmacedo.binary.interfaces.Constants.*;

public class WSClient extends WebSocketListener {

    private static ObjectMapper mapper = new ObjectMapper();
    private WebSocket myWebSocket;
    private OkHttpClient client;
    private Request request;

    private Msg_type msgType;
    private Passthrough passthrough;
    private History history;
    private Ohlc ohlc;
    private Tick tick;
    private Authorize authorize;

    private Proposal proposal;
    private Buy buy;
    private Transaction transaction;

    public WSClient() {
    }

    public void connect() {

        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(CONECT_URL_BINARY).build();
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

        setMsgType((Msg_type) Util_Json.getMsg_Type(text));
        imprime(text, getMsgType().getMsgType());

        if (getMsgType().getMsgType() != null) {
            if (text.toLowerCase().contains("passthrough"))
                setPassthrough((Passthrough) Util_Json.getObject_from_String(text, Passthrough.class));
            switch (getMsgType().getMsgType()) {
                case AUTHORIZE -> {
                    setAuthorize((Authorize) Util_Json.getObject_from_String(text, Authorize.class));
                    refreshAuthorize(getAuthorize());
                }
                case CANDLES -> {
                    refreshCandles(getPassthrough(), text);
                }
                case HISTORY -> {
                    setHistory((History) Util_Json.getObject_from_String(text, History.class));
                    refreshHistoryTick(getPassthrough(), getHistory());
                }
                case TICK -> {
                    setTick((Tick) Util_Json.getObject_from_String(text, Tick.class));
                    refreshTick(getPassthrough(), getTick());
                }
                case OHLC -> {
                    setOhlc((Ohlc) Util_Json.getObject_from_String(text, Ohlc.class));
                    refreshOhlc(getPassthrough(), getOhlc());
                }
                case PROPOSAL -> {
                    setProposal((Proposal) Util_Json.getObject_from_String(text, Proposal.class));
                    refreshProposal(getPassthrough(), getProposal());
                }
                case BUY -> {
                    setBuy((Buy) Util_Json.getObject_from_String(text, Buy.class));
                    //refreshBuy();
                }
                case TRANSACTION -> {
                    setTransaction((Transaction) Util_Json.getObject_from_String(text, Transaction.class));
                    refreshTransaction(getTransaction());
                }
            }
        }

    }

    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */

    private void openOrClosedSocket(boolean conectado) {

        if (CONSOLE_BINARY_CONECTADO)
            System.out.printf("servidorConectado: [%s]\n", conectado);
        Operacoes.setWsConectado(conectado);

    }

    private void refreshError() {

//        System.out.printf("deu erro!!!!!\n%s\n", getError().toString());

    }

    private void refreshAuthorize(Authorize authorize) {

        Platform.runLater(() -> Operacoes.setAuthorize(authorize));

    }

    private void refreshTick(Passthrough passthrough, Tick tick) {

    }

    private void refreshOhlc(Passthrough passthrough, Ohlc ohlc) {

        Platform.runLater(() -> {

            Symbol symbol = Operacoes.getSymbolList().stream()
                    .filter(symbol1 -> symbol1.getSymbol().equals(ohlc.getSymbol()))
                    .findFirst().orElse(null);
            int s_id = symbol.getId().intValue() - 1;
            int t_id = passthrough.getTickTime().getCod();

            if (t_id == 0)
                Operacoes.getUltimoOhlcStr()[s_id].setValue(ohlc);

            try {
                boolean symbolAtivo = Operacoes.getSymbolObservableList().get(s_id) != null;

                if (Operacoes.getTestaLastCandle()[t_id][s_id].getValue() == null
                        && Operacoes.getHistoricoDeCandlesFilteredList()[t_id][s_id].size()
                        <= Operacoes.getQtdCandlesAnalise()) {
                    Operacoes.getTestaLastCandle()[t_id][s_id].setValue(ohlc);

                    HistoricoDeCandles hCandle = Operacoes.getHistoricoDeCandlesFilteredList()[t_id][s_id]
                            .sorted(Comparator.comparing(HistoricoDeCandles::getEpoch).reversed()).get(0);
                    if (hCandle.getEpoch() == ohlc.getOpen_time())
                        Operacoes.getHistoricoDeCandlesObservableList().remove(hCandle);
                }

                Operacoes.getTimeCandleToClose()[t_id].setValue(ohlc.getGranularity() - (ohlc.getEpoch() - ohlc.getOpen_time()));

                if (Operacoes.getTimeCandleToClose()[t_id].getValue().compareTo(symbol.getTickTime()) == 0)
                    Operacoes.getHistoricoDeCandlesObservableList().add(0, new HistoricoDeCandles(ohlc));

            } catch (Exception ex) {
                if (!(ex instanceof IndexOutOfBoundsException))
                    ex.printStackTrace();
            }

        });

    }

    private void refreshProposal(Passthrough passthrough, Proposal proposal) {

        Platform.runLater(() -> {
            int s_id = passthrough.getSymbol().getId().intValue() - 1, t_id = passthrough.getTickTime().getCod();
            CONTRACT_TYPE cType = passthrough.getContractType();

            switch (ROBOS.valueOf(Operacoes.getRobo().getClass().getSimpleName().toUpperCase())) {
                case ABR -> {
                    if (proposal.getAsk_price().compareTo(Operacoes.getVlrStkPadrao()[t_id].getValue()) == 0)
                        Abr.getProposal()[t_id][s_id][cType.equals(CONTRACT_TYPE.CALL) ? 0 : 1][0] = proposal;
                    else
                        Abr.getProposal()[t_id][s_id][cType.equals(CONTRACT_TYPE.CALL) ? 0 : 1][1] = proposal;
                }
            }
        });

    }

    private void refreshTransaction(Transaction transaction) {

        Platform.runLater(() -> {

            if (transaction.getAction() != null)
                Operacoes.getTransactionObservableList().add(0, transaction);

        });

    }

    private void refreshCandles(Passthrough passthrough, String candles) {

        Platform.runLater(() -> {
//            try {
            int t_id = passthrough.getTickTime().getCod();
            int s_id = passthrough.getSymbol().getId().intValue() - 1;
            if (TICK_TIME.toEnum(t_id) != null && Operacoes.getSymbolObservableList().size() > s_id)
                Util_Json.addCandlesToHistorico(candles, s_id, TICK_TIME.getTimeSeconds(t_id));
//            } catch (Exception ex) {
//
//            }
        });

    }

    private void refreshHistoryTick(Passthrough passthrough, History history) {

        Platform.runLater(() -> {

        });

    }

    private void imprime(String text, MSG_TYPE msgType) {

        if (CONSOLE_BINARY_ALL || CONSOLE_BINARY_ALL_SEM_TICKS) {
            if (CONSOLE_BINARY_ALL_SEM_TICKS) {
                if (msgType.equals(MSG_TYPE.TICK)
                        || msgType.equals(MSG_TYPE.HISTORY)
                        || msgType.equals(MSG_TYPE.OHLC)
                        || msgType.equals(MSG_TYPE.CANDLES))
                    return;
            }
            System.out.printf("..0..%s\n", text);
        } else {
            boolean print = false;
            switch (msgType) {
//                case ACTIVE_SYMBOLS -> print = CONSOLE_BINARY_ACTIVE_SYMBOL;
                case AUTHORIZE -> print = CONSOLE_BINARY_AUTHORIZE;
                case ERROR -> print = CONSOLE_BINARY_ERROR;
                case TICK, OHLC -> print = CONSOLE_BINARY_TICK;
                case PROPOSAL -> print = CONSOLE_BINARY_PROPOSAL;
                case BUY -> print = CONSOLE_BINARY_BUY;
                case TRANSACTION -> print = CONSOLE_BINARY_TRANSACTION;
                case HISTORY, CANDLES -> print = CONSOLE_BINARY_HISTORY;
                default -> print = (CONSOLE_BINARY_ALL || CONSOLE_BINARY_ALL_SEM_TICKS);
            }
            if (print)
                System.out.printf("..1..%s\n", text);
        }

    }


    /**
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */


    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        WSClient.mapper = mapper;
    }

    public WebSocket getMyWebSocket() {
        return myWebSocket;
    }

    public void setMyWebSocket(WebSocket myWebSocket) {
        this.myWebSocket = myWebSocket;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Msg_type getMsgType() {
        return msgType;
    }

    public void setMsgType(Msg_type msgType) {
        this.msgType = msgType;
    }

    public Passthrough getPassthrough() {
        return passthrough;
    }

    public void setPassthrough(Passthrough passthrough) {
        this.passthrough = passthrough;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Ohlc getOhlc() {
        return ohlc;
    }

    public void setOhlc(Ohlc ohlc) {
        this.ohlc = ohlc;
    }

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {
        this.tick = tick;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public Buy getBuy() {
        return buy;
    }

    public void setBuy(Buy buy) {
        this.buy = buy;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

}
