package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacao;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.model.vo.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Util_Json {

    private static ObjectMapper mapper = new ObjectMapper();

    public static Object getMsg_Type(String strJson) {
        JSONObject obj = new JSONObject(strJson);
        return new Msg_type(obj.getString("msg_type"));
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
            if (aClass == Buy.class || aClass == Error.class)
                return getMapper().readValue(obj.getJSONObject(aClass.getSimpleName().toLowerCase()).toString(), aClass);
            else
                return getMapper().readValue(obj.getJSONObject(aClass.getSimpleName().toLowerCase()).toString(), aClass);
        } catch (Exception ex) {
            if (!(ex instanceof JSONException))
                ex.printStackTrace();
            else {
                try {
                    return getMapper().readValue(obj.getJSONObject("error").toString(), Error.class);
                } catch (JsonProcessingException ex1) {
                    ex1.printStackTrace();
                }
            }
            return null;
        }
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
        JSONObject obj = new JSONObject(strJson).getJSONObject("history");
        List<BigDecimal> listPrices = new ArrayList((Collection) obj.getJSONArray("prices"));
        List<Integer> listTimes = new ArrayList((Collection) obj.getJSONArray("times"));
        ObservableList<HistoricoDeTicks> historicoDeTicksList = FXCollections.observableArrayList();
        for (int i = 0; i < Operacao.getQtdTicksAnalisar(); i++) {
            historicoDeTicksList.add(0, new HistoricoDeTicks(symbolId,
                    listPrices.get(i), listTimes.get(i)));
        }
        Operacao.getHistoricoDeTicksAnaliseObservableList()[symbolId]
                .setAll(historicoDeTicksList.sorted(Comparator.comparing(HistoricoDeTicks::getTime)));
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Util_Json.mapper = mapper;
    }
}
