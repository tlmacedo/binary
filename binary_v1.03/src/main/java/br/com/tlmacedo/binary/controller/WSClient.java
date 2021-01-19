package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategia.EvenOdd_01_Porcentagem;
import br.com.tlmacedo.binary.controller.estrategia.Even_01_Porcentagem;
import br.com.tlmacedo.binary.controller.estrategia.Odd_01_Porcentagem;
import br.com.tlmacedo.binary.controller.estrategia.Over_01;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.MSG_TYPE;
import br.com.tlmacedo.binary.model.vo.Error;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.Util_Json;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

import static br.com.tlmacedo.binary.interfaces.Constants.*;

public class WSClient extends WebSocketListener {

    WebSocket webSocket;
    Msg_type msgType;
    Error error;
    Authorize authorize;
    Tick tick;
    History history;
    Transaction transaction;
    Buy buy;
    Proposal proposal;


    public WSClient() {
    }

    public void connect() {
//        OkHttpClient client = new OkHttpClient.Builder().build();
//        Request request = new Request.Builder().url(CONECT_URL_BINARY).build();
//        setWebSocket(client.newWebSocket(request, this));
        System.out.printf("conectando.\n");
//            setClient(new OkHttpClient.Builder().build());
        OkHttpClient client = new OkHttpClient.Builder().build();
        System.out.printf("conectando..\n");
//            setRequest(new Request.Builder().url(CONECT_URL_BINARY).build());
        Request request = new Request.Builder().url(CONECT_URL_BINARY).build();
        System.out.printf("conectando..\n");
        setWebSocket(client.newWebSocket(request, this));
        System.out.printf("conectado!!!\n");

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

        Object obj = null;
        try {
            obj = Util_Json.getObject_from_String(text, Error.class);
            setError((Error) Util_Json.getObject_from_String(text, Error.class));
            refreshError(getError());
        } catch (Exception exception) {
            switch (getMsgType().getMsgType()) {
//                case ERROR -> {
//                }
                case AUTHORIZE -> {
                    setAuthorize((Authorize) Util_Json.getObject_from_String(text, Authorize.class));
                    refreshAuthorize(getAuthorize());
                }
                case TICK -> {
                    setTick((Tick) Util_Json.getObject_from_String(text, Tick.class));
                    refreshTick(getTick());
                }
                case PROPOSAL -> {
                    setProposal((Proposal) Util_Json.getObject_from_String(text, Proposal.class));
                    Integer symbolId = Operacao.getSymbolId(Util_Json.getValue_from_EchoReq(text, "symbol"));
                    CONTRACT_TYPE contractType = CONTRACT_TYPE.valueOf(Util_Json.getValue_from_EchoReq(text, "contract_type"));
                    refreshProposal(symbolId, getProposal(), contractType);
                }
                case BUY -> {
                    setBuy((Buy) Util_Json.getObject_from_String(text, Buy.class));
                }
                case TRANSACTION -> {
                    setTransaction((Transaction) Util_Json.getObject_from_String(text, Transaction.class));
                    refreshTransaction(getTransaction());
                }
                case HISTORY -> {
                    Integer symbolId = Operacao.getSymbolId(Util_Json.getValue_from_EchoReq(text, "ticks_history"));
                    if (symbolId == null) return;
                    setHistory((History) Util_Json.getObject_from_String(text, History.class));
                    refreshHistoryTick(symbolId, getHistory());
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

    private void imprime(String text, MSG_TYPE msgType) {
        if (CONSOLE_BINARY_ALL || CONSOLE_BINARY_ALL_SEM_TICKS) {
            if (CONSOLE_BINARY_ALL_SEM_TICKS) {
                if (msgType.equals(MSG_TYPE.TICK))
                    return;
            }
            System.out.printf("...%s\n", text);
        } else {
            boolean print = false;
            switch (msgType) {
                case AUTHORIZE -> print = CONSOLE_BINARY_AUTHORIZE;
                case ERROR -> print = CONSOLE_BINARY_ERROR;
                case TICK -> print = CONSOLE_BINARY_TICK;
                case PROPOSAL -> print = CONSOLE_BINARY_PROPOSAL;
                case BUY -> print = CONSOLE_BINARY_BUY;
                case TRANSACTION -> print = CONSOLE_BINARY_TRANSACTION;
                case HISTORY -> print = CONSOLE_BINARY_HISTORY;
            }
            if (print)
                System.out.printf("...%s\n", text);
        }
    }

    private void openOrClosedSocket(boolean conectado) {
        Operacao.setWsConectado(conectado);
        if (CONSOLE_BINARY_CONECTADO)
            System.out.printf("servidorConectado: [%s]\n", conectado);
    }

    private void refreshError(Error error) {
        Operacao.getError()[Operacao.getSymbolObservableList().size()].setValue(error);
    }

    private void refreshAuthorize(Authorize authorize) {
        Platform.runLater(() -> Operacao.setAuthorize(authorize));
    }

    private void refreshTick(Tick tick) {
        Platform.runLater(() -> {
            Integer symbolId = Operacao.getSymbolId(tick.getSymbol());
//            if ((Operacao.isVol1s() && symbolId < 5) || (!Operacao.isVol1s() && symbolId >= 5)) return;
            HistoricoDeTicks ticks = new HistoricoDeTicks(symbolId, tick.getQuote(), tick.getEpoch());

            while (Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].size()
                    >= Operacao.getQtdTicksGrafico())
                Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId]
                        .remove(Operacao.getQtdTicksGrafico() - 1);
            while (Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].size()
                    >= Operacao.getQtdTicksAnalisar())
                Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId]
                        .remove(Operacao.getQtdTicksAnalisar() - 1);

            if (Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].stream()
                    .noneMatch(historicoDeTicks -> historicoDeTicks.getTime() == ticks.getTime())) {
                Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].add(0, ticks);
                Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].add(0, ticks);
            }

            Operacao.getUltimoTick()[symbolId].setValue(tick);

//            Util_Json.printJson_from_Object(tick,String.format("tick:%s", tick));
//            Operacao.getUltimoTick()[symbolId].getValue().setUltimoDigito(tick.getUltimoDigito());
//            Operacao.getUltimoDigito()[symbolId].setValue(tick.getUltimoDigito());
        });
    }

    private void refreshProposal(Integer symbolId, Proposal proposal, CONTRACT_TYPE contractType) {

//        Platform.runLater(() -> {
        if (symbolId == null) return;
        switch (Operacao.getRoboSelecionado()) {
            case EVEN_ODD_01_PORCENTAGEM -> {
                switch (contractType) {
                    case DIGITEVEN -> EvenOdd_01_Porcentagem.getProposal()[symbolId][0].setValue(proposal);
                    case DIGITODD -> EvenOdd_01_Porcentagem.getProposal()[symbolId][1].setValue(proposal);
                }
            }

            case EVEN_01_PORCENTAGEM -> Even_01_Porcentagem.getProposal()[symbolId].setValue(proposal);

            case ODD_01_PORCENTAGEM -> Odd_01_Porcentagem.getProposal()[symbolId].setValue(proposal);

            case OVER_01 -> Over_01.getProposal()[symbolId].setValue(proposal);
        }

//        });

    }

    private void refreshTransactionAutorizacao(Transaction transaction) {
        System.out.printf("transaction: %s\n", transaction);
//        transaction.setContaToken(Operacao.getContaToken());
//        Operacao.getTransactionObservableList()[symbolId].add(0, transaction);

    }

    private void refreshTransaction(Transaction transaction) {
        if (transaction.getSymbol() != null) {
            Integer symbolId = transaction.getSymbol().idProperty().intValue() - 1;
            if (symbolId == null || Operacao.getContaToken() == null) return;
            transaction.setContaToken(Operacao.getContaToken());
            Operacao.getTransactionObservableList()[symbolId].add(0, transaction);
            Operacao.getTransactionDAO().merger(transaction);
        }

    }

    private void refreshHistoryTick(Integer symbolId, History history) {
        Platform.runLater(() -> {
            //Operacao.getGrafBarListValorDigito_R()[symbolId].clear();
            for (int digito = 0; digito < 10; digito++) {
                Operacao.getGrafBarListValorDigito_R()[symbolId].get(digito).setValue(0);
            }

            Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].clear();
            Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].clear();

            HistoricoDeTicks ticks;
            for (int i = 0; i < history.getTimes().size(); i++) {
                int finalI = i;
                if (Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].stream()
                        .anyMatch(historicoDeTicks -> historicoDeTicks.getTime() == history.getTimes().get(finalI)))
                    continue;
                ticks = new HistoricoDeTicks(symbolId,
                        history.getPrices().get(i), history.getTimes().get(i));
                Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].add(0, ticks);
                Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId].sort(Comparator.comparing(HistoricoDeTicks::getTime).reversed());
            }
            for (HistoricoDeTicks tick : Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId])
                if (Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].size() < Operacao.getQtdTicksGrafico())
                    Operacao.getHistoricoDeTicksGraficoObservableList()[symbolId].add(tick);
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

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public Msg_type getMsgType() {
        return msgType;
    }

    public void setMsgType(Msg_type msgType) {
        this.msgType = msgType;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public Tick getTick() {
        return tick;
    }

    public void setTick(Tick tick) {
        this.tick = tick;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Buy getBuy() {
        return buy;
    }

    public void setBuy(Buy buy) {
        this.buy = buy;
    }

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }
}
