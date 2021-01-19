package br.com.tlmacedo.binary.controller;

import br.com.tlmacedo.binary.controller.estrategias.*;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Error;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.services.UtilJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class WSClient extends WebSocketListener {

    private static ObjectMapper mapper = new ObjectMapper();
    private WebSocket myWebSocket;
    private Msg_type msg_type;
    private Error error;
    private History history;
    private Tick tick;
    private Proposal proposal;
    private Authorize authorize;
    private Buy buy;
    private Transaction transaction;
    private Integer qtdTicksGrafico = 0, qtdTicksAnalisar = 0, contador = 1;

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
        setMsg_type((Msg_type) UtilJson.getMsg_Type(text));
        if (!getMsg_type().getMsg_type().getDescricao().toLowerCase().equals("tick"))
            System.out.printf("...%s\n", text);

        switch (getMsg_type().getMsg_type()) {
            case ERROR -> {
                setError((Error) UtilJson.getObject_From_String(getMapper(), text, Error.class));
                refreshError(getError());
            }
            case AUTHORIZE -> {
                setAuthorize((Authorize) UtilJson.getObject_From_String(getMapper(), text, Authorize.class));
                refreshAuthorize(getAuthorize());
            }
            case TICK -> {
                setTick((Tick) UtilJson.getObject_From_String(getMapper(), text, Tick.class));
                refreshTick(getTick());
            }
            case PROPOSAL -> {
                setProposal((Proposal) UtilJson.getObject_From_String(getMapper(), text, Proposal.class));
                CONTRACT_TYPE contractType = CONTRACT_TYPE.valueOf(UtilJson.getValue_From_EchoReq(text, "contract_type"));
                Integer symbolId = Operacoes.getSymbolId(UtilJson.getValue_From_EchoReq(text, "symbol"));
                if (symbolId != null)
                    switch (contractType) {
                        case DIGITDIFF -> {
                            Integer digito = Integer.valueOf(UtilJson.getValue_From_EchoReq(text, "barrier"));
                            switch (Operacoes.getEstrategia().getClass().getSimpleName().toLowerCase()) {
                                case "estrategiadiff0" -> {
                                    EstrategiaDiff0.getProposal()[symbolId][digito].setValue(new Proposal());
                                    EstrategiaDiff0.getProposal()[symbolId][digito].setValue(getProposal());
                                }
                            }
                        }
                        case CALL -> {
                            switch (Operacoes.getEstrategia().getClass().getSimpleName()) {
                                case "CallPut_01_Reversed" -> {
                                    CallPut_01_Reversed.getProposal()[symbolId][0].setValue(new Proposal());
                                    CallPut_01_Reversed.getProposal()[symbolId][0].setValue(getProposal());
                                }
                            }
                        }
                        case PUT -> {
                            switch (Operacoes.getEstrategia().getClass().getSimpleName()) {
                                case "CallPut_01_Reversed" -> {
                                    CallPut_01_Reversed.getProposal()[symbolId][1].setValue(new Proposal());
                                    CallPut_01_Reversed.getProposal()[symbolId][1].setValue(getProposal());
                                }
                            }
                        }
                        case DIGITOVER -> {
                            EstrategiaOver.getProposal()[symbolId].setValue(new Proposal());
                            EstrategiaOver.getProposal()[symbolId].setValue(getProposal());
                        }
                        case DIGITODD -> {
                            switch (Operacoes.getEstrategia().getClass().getSimpleName().toLowerCase()) {
                                case "estrategiaevenodd0" -> {
                                    EstrategiaEvenOdd0.getProposal()[symbolId][1].setValue(new Proposal());
                                    EstrategiaEvenOdd0.getProposal()[symbolId][1].setValue(getProposal());
                                }
                                case "estrategiaevenodd1" -> {
                                    EstrategiaEvenOdd1.getProposal()[symbolId][1].setValue(new Proposal());
                                    EstrategiaEvenOdd1.getProposal()[symbolId][1].setValue(getProposal());
                                }
                            }
                        }
                        case DIGITEVEN -> {
                            switch (Operacoes.getEstrategia().getClass().getSimpleName().toLowerCase()) {
                                case "estrategiaevenodd0" -> {
                                    EstrategiaEvenOdd0.getProposal()[symbolId][0].setValue(new Proposal());
                                    EstrategiaEvenOdd0.getProposal()[symbolId][0].setValue(getProposal());
                                }
                                case "estrategiaevenodd1" -> {
                                    EstrategiaEvenOdd1.getProposal()[symbolId][0].setValue(new Proposal());
                                    EstrategiaEvenOdd1.getProposal()[symbolId][0].setValue(getProposal());
                                }
                            }
                        }
                    }
            }
            case BUY -> {
                setBuy((Buy) UtilJson.getObject_From_String(getMapper(), text, Buy.class));
            }
            case TRANSACTION -> {
                setTransaction((Transaction) UtilJson.getObject_From_String(getMapper(), text, Transaction.class));
                refreshTransaction(getTransaction());
            }
            case HISTORY -> {
                Integer symbolId = Operacoes.getSymbolId(UtilJson.getValue_From_EchoReq(text, "ticks_history"));
                if (symbolId == null) return;
                setHistory((History) UtilJson.getObject_From_String(getMapper(), text, History.class));
                refreshHistoryTick(symbolId, getHistory());
            }
        }
    }


    private void openOrClosedSocket(boolean value) {
        Operacoes.ws_ConectatoProperty().setValue(value);
        System.out.printf("servidorConectado:[%s]\n", value);
    }

    private void refreshAuthorize(Authorize authorize) {
        Platform.runLater(() -> Operacoes.authorizeProperty().setValue(authorize));
    }

    private void refreshTick(Tick newTick) {
        Platform.runLater(() -> {

            Integer symbolId = Operacoes.getSymbolId(newTick.getSymbol());
            if (symbolId == null) return;
            HistoricoDeTicks ticks = new HistoricoDeTicks(symbolId, newTick.getQuote(), newTick.getEpoch());

//        Platform.runLater(() -> Operacoes.getUltimoTick()[symbolId].setValue(ticks.getQuoteCompleto()));
            while (Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].size()
                    >= Operacoes.qtdTicksGraficoProperty().getValue())
                Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId]
                        .remove(Operacoes.qtdTicksGraficoProperty().getValue() - 1);
            while (Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId].size()
                    >= Operacoes.qtdTicksAnalisarProperty().getValue())
                Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId]
                        .remove(Operacoes.qtdTicksAnalisarProperty().getValue() - 1);

            if (Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].stream()
                    .noneMatch(historicoDeTicks -> historicoDeTicks.getTime() == ticks.getTime()))
                Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].add(0, ticks);

            if (Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId].stream()
                    .noneMatch(analiseDeTicks -> analiseDeTicks.getTime() == ticks.getTime()))
                Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId].add(0, ticks);

            Operacoes.getUltimoTick()[symbolId].setValue(newTick);
            Operacoes.getUltimoDigito()[symbolId].setValue(newTick.getUltimoDigito());

        });
    }

    private void refreshHistoryTick(Integer symbolId, History history) {
        Platform.runLater(() -> {
            Operacoes.getGrafBarListValorDigito_R()[symbolId].clear();
            for (int i = 0; i < 10; i++) {
                Operacoes.getGrafBarListValorDigito_R()[symbolId].add(0L);
            }
            Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].clear();
            Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId].clear();

            HistoricoDeTicks ticks;
            for (int i = 0; i < history.getTimes().size(); i++) {
                ticks = new HistoricoDeTicks(symbolId,
                        new BigDecimal(history.getPrices().get(i).doubleValue()),
                        history.getTimes().get(i));
                Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId]
                        .add(0, ticks);
            }
            for (HistoricoDeTicks tick : Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId])
                if (Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].size() < Operacoes.qtdTicksGraficoProperty().getValue())
                    Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId].add(tick);
        });
    }

    public void refreshTransaction(Transaction transaction) {
//        int symbolId = 0;
//        for (int i = 0; i < Operacoes.getSymbolObservableList().size(); i++)
//            if (Operacoes.VOL_NAME[i].equals(transaction.getSymbol())) {
//                symbolId = i;
//                break;
//            }
        if (transaction.getSymbol() != null) {
            transaction.setToken(Operacoes.tokenProperty().getValue());
            Operacoes.getTransactionObservableList()[transaction.getSymbol().idProperty().intValue()].add(0, transaction);
//            Operacoes.getTransactionDAO().merger(transaction);

//        }else {
//            if (transaction.getId()!=null)
//                Operacoes.tra
        }
    }

    public void refreshError(Error error) {
        Operacoes.getError()[Operacoes.getSymbolObservableList().size()].setValue(error);
    }

    /**
     * @return
     */

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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
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

    public Proposal getProposal() {
        return proposal;
    }

    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public Integer getQtdTicksGrafico() {
        return qtdTicksGrafico;
    }

    public void setQtdTicksGrafico(Integer qtdTicksGrafico) {
        this.qtdTicksGrafico = qtdTicksGrafico;
    }

    public Integer getQtdTicksAnalisar() {
        return qtdTicksAnalisar;
    }

    public void setQtdTicksAnalisar(Integer qtdTicksAnalisar) {
        this.qtdTicksAnalisar = qtdTicksAnalisar;
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

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        WSClient.mapper = mapper;
    }
}
