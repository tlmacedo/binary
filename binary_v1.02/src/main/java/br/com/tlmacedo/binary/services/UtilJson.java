package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.HistoricoDeTicks;
import br.com.tlmacedo.binary.model.vo.Msg_type;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class UtilJson {

    public static Object getMsg_Type(String strJson) {
        JSONObject obj = new JSONObject(strJson);
        return new Msg_type(obj.getString("msg_type"));
    }

    public static String getJson_From_Object(ObjectMapper mapper, Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public static Object getObject_From_String(ObjectMapper mapper, String strJson, Class aClass) {
        try {
            JSONObject obj = new JSONObject(strJson);
            return mapper.readValue(obj.getJSONObject(aClass.getSimpleName().toLowerCase()).toString(), aClass);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void printJsonFromObject(ObjectMapper mapper, Object object, String label) {
        try {
            if (label != null)
                System.out.printf(label + "\n");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue_From_EchoReq(String echo_req, String valueFind) {
        JSONObject obj = new JSONObject(echo_req);
        return obj.getJSONObject("echo_req").getString(valueFind);
    }

    public static void getHistory_From_String(Integer symbolId, String strJson) {
        JSONObject obj = new JSONObject(strJson).getJSONObject("history");
        List<BigDecimal> listPrices = new ArrayList((Collection) obj.getJSONArray("prices"));
        List<Integer> listTimes = new ArrayList((Collection) obj.getJSONArray("times"));
        ObservableList<HistoricoDeTicks> ticksObservableList = FXCollections.observableArrayList();
        for (int i = 0; i < Operacoes.qtdTicksAnalisarProperty().getValue(); i++) {
            ticksObservableList.add(0, new HistoricoDeTicks(symbolId, listPrices.get(i),
                    listTimes.get(i)));
        }
        Operacoes.getHistoricoDeTicksGraficoObservableList()[symbolId]
                .setAll(ticksObservableList.sorted(Comparator.comparing(HistoricoDeTicks::getTime)));
    }

}
