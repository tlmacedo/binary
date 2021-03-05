import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.Candle;
import br.com.tlmacedo.binary.model.vo.Candles;
import br.com.tlmacedo.binary.model.vo.HistoricoDeTicks;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Teste {

    private static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
//        String longcode0 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 58 seconds after contract start time.";
//        String longcode1 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 1 minute 58 seconds after contract start time.";
//        String text = longcode0;
//        int indexMin = text.indexOf("minute"), indexSecond = text.indexOf("seconds");
//        System.out.printf("indexMinute: %s\n", indexMin);
//        System.out.printf("indexSeconds: %s\n", indexSecond);
//        System.out.printf("%s\n", indexMin >= 0 ? text.substring(indexMin - 3, indexMin + 6) : "");
//        System.out.printf("%s\n", indexSecond >= 0 ? text.substring(indexSecond - 4, indexSecond + 7) : "");


    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Teste.mapper = mapper;
    }
}
