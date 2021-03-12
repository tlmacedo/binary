package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

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
            return getMapper().writeValueAsString(object)
                    .replace(",\"granularity\":null", "")
                    .replace(",\"passthrough\":null", "")
                    .replace(",\"subscribe\":null", "")
                    .replace("\"barrier\":null,", "");
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
            System.out.printf("strJson: [%s]\n", strJson);
            ex.printStackTrace();
        }
        return null;

    }

    public static void addCandlesToHistorico(String strJson, int t_id, int s_id) {

        try {
            JSONArray array = new JSONObject(strJson).getJSONArray("candles");
            for (Object o : array)
                Operacoes.getHistoricoDeCandlesObservableList()
                        .add(new HistoricoDeCandles((JSONObject) o, t_id, s_id));
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
