package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.Enums.Error;
import br.com.tlmacedo.binary.model.Enums.PIP_SIZE;
import br.com.tlmacedo.binary.model.Enums.SYMBOL;
import br.com.tlmacedo.binary.model.HistoricoTicks;
import br.com.tlmacedo.binary.model.Msg_type;
import br.com.tlmacedo.binary.model.PriceProposal;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UtilJson {
    static ObjectMapper mapper = new ObjectMapper();

    public static Object getObjectFromJson(String json, Class classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJsonObjectFromObject(Object object) {
        try {
            return new JSONObject(mapper.writeValueAsString(object));
        } catch (JSONException | IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String getJsonFromObject(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            return null;
        }
    }

    public static String getJsonFromList(List list) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void printJsonFromObject(Object object, String label) {
        try {
            if (label != null)
                System.out.printf(label + "\n");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printJsonFromString(String string, String label) {
        try {
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(string);
            JsonNode actualObj = mapper.readTree(parser);
            if (label != null)
                System.out.printf(label + "\n");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(actualObj) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printJsonFromList(List list, String label) {
        try {
            if (label != null)
                System.out.printf(label + "\n");
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(list) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getMsg_Type(String strJson) {
        JSONObject obj = new JSONObject(strJson);
        try {
            return new Msg_type(obj.getString("msg_type"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Object getMsg_Error(String strJson) {
        JSONObject obj = new JSONObject(strJson);
        Gson gson = new Gson();
        try {
            return gson.fromJson(obj.getJSONObject("error").toString(), Error.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String getEcho_RegPartJson(String strJson, String getPart) {
        JSONObject obj = new JSONObject(strJson);
        return obj.getJSONObject("echo_req").getString(getPart);
    }

    public static Object getObjectFromString(String strJson, Class classe) {
        JSONObject obj = new JSONObject(strJson);
        Gson gson = new Gson();
        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
//            if (classe.getSimpleName().toLowerCase().equals("transaction"))
//                System.out.printf("strJson: %s\n", strJson);
            return gson.fromJson(obj.getJSONObject(classe.getSimpleName().toLowerCase()).toString(), classe);
        } catch (Exception ex) {
            if (ex instanceof JSONException) {
                try {
                    return gson.fromJson(obj.getString(classe.getSimpleName().toLowerCase()), classe);
                } catch (Exception exz) {
                    try {
                        return gson.fromJson(obj.getJSONObject("error").toString(), Error.class);
                    } catch (Exception exx) {
                        exx.printStackTrace();
                    }
                    exz.printStackTrace();
                }
            }
            ex.printStackTrace();
            return null;
        }
    }

    public static void getHistoryFromString(SYMBOL symbol, String strJson) {
        JSONObject obj = new JSONObject(strJson);
        try {
            int pipSize = Integer.valueOf(PIP_SIZE.toEnum(symbol.getCod()).getDescricao());
            List<Number> listPrices = new ArrayList<>();
            for (Object price : obj.getJSONObject("history").getJSONArray("prices"))
                listPrices.add(Double.valueOf(price.toString()));
            List<Integer> listTimes = new ArrayList<>();
            for (Object time : obj.getJSONObject("history").getJSONArray("times"))
                listTimes.add(Integer.valueOf(time.toString()));
            for (int i = 0; i < Operacoes.getQtdTicksAnalisar(); i++)
                Operacoes.getHistoricoTicksObservableList()[symbol.getCod()].add(0,
                        new HistoricoTicks(listPrices.get(i), pipSize, listTimes.get(i)));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
