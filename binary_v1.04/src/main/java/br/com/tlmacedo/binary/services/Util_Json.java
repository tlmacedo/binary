package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.*;
import br.com.tlmacedo.binary.model.vo.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
            ex.printStackTrace();
        }
        return null;

    }

    public static void addCandlesToHistorico(String strJson, int symbol_id, int granularity) {

        try {
            JSONArray array = new JSONObject(strJson).getJSONArray("candles");
            for (Object o : array)
                Operacoes.getHistoricoDeCandlesObservableList().add(0, new HistoricoDeCandles((JSONObject) o, symbol_id, granularity));
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Util_Json.mapper = mapper;
    }
}
