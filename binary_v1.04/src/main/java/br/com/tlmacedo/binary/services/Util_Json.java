package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.model.vo.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.stream.Collector;

public class Util_Json {

    private static ObjectMapper mapper = new ObjectMapper();

    public static Object getMsg_Type(String strJson) {

        JSONObject obj = new JSONObject(strJson);
        try {
            return new Msg_type(obj.getString("msg_type"));
        } catch (Exception ex) {
            return new Msg_type();
        }

    }

    public static String getJson_from_Object(Object object) {

        try {
            return getMapper().writeValueAsString(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static Object getObject_from_String(String strJson, Class aClass) {

        JSONObject obj = new JSONObject(strJson);
        try {
            return getMapper().readValue(obj.getJSONObject(aClass.getSimpleName().toLowerCase()).toString(), aClass);
        } catch (Exception ex) {
            if (!(ex instanceof JSONException)) {
                ex.printStackTrace();
            } else {
                try {
                    if (aClass.equals(Symbols.class)) {
                        String str = String.format("{\"active_symbols\": %s}",
                                obj.getJSONArray("active_symbols").toString());
                        return getMapper().readValue(str, Symbols.class);
                    } else {
                        return getMapper().readValue(obj.getJSONObject("error").toString(), Error.class);
                    }
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public static void printJson_from_Object(Object object, String label) {

        try {
            if (label != null)
                System.out.printf("%s:\n", label);
            System.out.printf("%s\n", getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static String getValue_from_EchoReq(String echo_req, String field) {

        JSONObject obj = new JSONObject(echo_req);
        return obj.getJSONObject("echo_req").getString(field);

    }

    public static void getHistory_from_String(Integer symbolId, String strJson) {

//        JSONObject obj = new JSONObject(strJson).getJSONObject("history");
//        List<BigDecimal> listPrices = new ArrayList((Collection) obj.getJSONArray("prices"));
//        List<Integer> listTimes = new ArrayList((Collection) obj.getJSONArray("times"));
//        ObservableList<HistoricoDeTicks> historicoDeTicksList = FXCollections.observableArrayList();
//        for (int i = 0; i < Operacoes.getQtdTicksAnalisar(); i++) {
//            historicoDeTicksList.add(0, new HistoricoDeTicks(symbolId,
//                    listPrices.get(i), listTimes.get(i)));
//        }
//        Operacoes.getHistoricoDeTicksAnaliseObservableList()[symbolId]
//                .setAll(historicoDeTicksList.sorted(Comparator.comparing(HistoricoDeTicks::getTime)));

    }

    public static void getCandles_from_String(Passthrough passthrough, String strJson) {
        try {
            JSONArray array = new JSONObject(strJson).getJSONArray("candles");
            ObservableList<HistoricoDeCandles> historicoDeOhlcList = FXCollections.observableArrayList();
            int timeCandle = Integer.parseInt(passthrough.getTickTime().getDescricao().replaceAll("\\D", ""));
            for (int i = 0; i < Operacoes.getQtdCandlesAnalise(); i++) {
                JSONObject obj = array.getJSONObject(i);
                int granularity = timeCandle * 60;
                historicoDeOhlcList.add(0, new HistoricoDeCandles(obj, passthrough.getSymbol(), granularity));
                if (Operacoes.getTimeCandleStart()[timeCandle - 1].getValue().compareTo(0) == 0)
                    Operacoes.getTimeCandleStart()[timeCandle - 1].setValue(obj.getInt("epoch"));
            }
            Operacoes.getHistoricoDeCandlesObservableList().addAll(historicoDeOhlcList.sorted(Comparator.comparing(HistoricoDeCandles::getEpoch)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Util_Json.mapper = mapper;
    }
}
