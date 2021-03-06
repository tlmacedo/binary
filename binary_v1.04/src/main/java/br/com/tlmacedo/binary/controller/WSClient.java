package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.Abr;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.MSG_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.vo.Error;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Service_Alert;
import br.com.tlmacedo.binary.services.Util_Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private Error error;

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

        if (text.contains("\"error\":")) {
            JSONObject obj = new JSONObject(text);
            setError((Error) Util_Json.getObject_from_String(obj.get("error").toString(), Error.class));
            refreshError(getError(), text);
        } else {
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

    private void refreshError(Error error, String text) {

        System.out.printf("err:%s\n", text);
        System.out.printf("*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*ERROR*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*ERROR*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*ERROR*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*ERROR*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");
        System.out.printf("*-*-*-*-*-*-*-*-*-*-*-*-*-*\n");

        Service_Alert alert = new Service_Alert();
        alert.setCabecalho("Error servidor");
        alert.setContentText(String.format("code: %s\nmessage: %s", error.getCode(), error.getMessage()));
        alert.alertOk();

    }

    private void refreshAuthorize(Authorize authorize) {

        Platform.runLater(() -> Operacoes.setAuthorize(authorize));

    }

    private void refreshCandles(Passthrough passthrough, String candles) {

        Platform.runLater(() -> {
            try {
                Util_Json.addCandlesToHistorico(candles, passthrough.getT_id(), passthrough.getS_id());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

    }

    private void refreshHistoryTick(Passthrough passthrough, History history) {

        Platform.runLater(() -> {

        });

    }


    private void refreshTick(Passthrough passthrough, Tick tick) {

    }

    private void refreshOhlc(Passthrough passthrough, Ohlc ohlc) {

        Platform.runLater(() -> {

            int t_id = passthrough.getT_id(),
                    s_id = passthrough.getS_id();
            TimeFrame tFrame = Operacoes.getTimeFrameObservableList().get(t_id);
            Symbol symbol = Operacoes.getSymbolObservableList().get(s_id);

            if (t_id == 0) {
                HistoricoDeTicks hTicks = new HistoricoDeTicks(ohlc);
                if (Operacoes.isRoboMonitorando() && !Operacoes.isRoboMonitorandoPausado()) {
                    hTicks = Operacoes.getHistoricoDeTicksDAO().merger(hTicks);
                }
                Operacoes.getHistoricoDeTicksObservableList().add(hTicks);
                Operacoes.getUltimoOhlcStr()[s_id].setValue(ohlc);
            }

            try {
                List<HistoricoDeCandles> listCandles = Operacoes.getHistoricoDeCandlesObservableList().stream()
                        .filter(candles -> candles.getTimeFrame().getId() == tFrame.getId()
                                && candles.getSymbol().getId() == symbol.getId())
                        .collect(Collectors.toList());
                if (listCandles.size() <= Operacoes.getQtdCandlesAnalise()
                        && listCandles.size() > 0) {
                    HistoricoDeCandles hCandle = listCandles.stream()
                            .sorted(Comparator.comparing(HistoricoDeCandles::getEpoch).reversed())
                            .findFirst().get();
                    if (hCandle.getEpoch() == ohlc.getOpen_time())
                        Operacoes.getHistoricoDeCandlesObservableList().remove(hCandle);
                }

                Operacoes.getTimeCandleToClose()[t_id].setValue(ohlc.getGranularity() - (ohlc.getEpoch() - ohlc.getOpen_time()));

                if (Operacoes.getTimeCandleToClose()[t_id].getValue().compareTo(symbol.getTickTime()) == 0)
                    Operacoes.getHistoricoDeCandlesObservableList().add(new HistoricoDeCandles(ohlc));

            } catch (Exception ex) {
                if (!(ex instanceof IndexOutOfBoundsException))
                    ex.printStackTrace();
            }

        });

    }

    private void refreshProposal(Passthrough passthrough, Proposal proposal) {

        Platform.runLater(() -> {
            if (proposal == null) return;

            int t_id = passthrough.getT_id(),
                    s_id = passthrough.getS_id();

            boolean priceLoss = passthrough.isPriceLoss();

            switch (ROBOS.valueOf(Operacoes.getRobo().getClass().getSimpleName().toUpperCase())) {
                case ABR -> {
                    Abr.getProposal()
                            [t_id]
                            [s_id]
                            [passthrough.getTypeContract_id()]
                            [priceLoss ? 1 : 0]
                            = proposal;
                }
            }
        });

    }

    private void refreshTransaction(Transaction transaction) {

        Platform.runLater(() -> {

            if (transaction.getAction() != null)
                Operacoes.newTransaction(Operacoes.getTransactionDAO().merger(transaction));

        });

    }


    private void imprime(String text, MSG_TYPE msgType) {

        if (CONSOLE_BINARY_ALL || CONSOLE_BINARY_ALL_SEM_TICKS) {
            if (CONSOLE_BINARY_ALL_SEM_TICKS) {
                if (msgType.equals(MSG_TYPE.TICK)
                        || msgType.equals(MSG_TYPE.CANDLES)
                        || msgType.equals(MSG_TYPE.HISTORY)
                        || msgType.equals(MSG_TYPE.OHLC))
                    return;
            }
            System.out.printf("..0..%s\n", text);
            return;
        }
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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
