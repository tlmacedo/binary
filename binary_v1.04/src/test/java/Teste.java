import br.com.tlmacedo.binary.services.Service_TelegramNotifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Teste {

    private static ObjectMapper mapper = new ObjectMapper();

//    public static void main(String[] args) {
//
//        Robo robo = new Abr(true);
//        BigDecimal vlr = new BigDecimal("0.35"), martingale = new BigDecimal("100.00"),
//                saldoInicial = new BigDecimal("7456.33");
//        Integer qtd = 10;
//
//
//        String parametrosUtilizadosRobo = "Robo: Abr vlr_Stake: 0.35 USD qtd_Candles: 10 mart: 100.00";
//
////        String parametrosUtilizadosRobo = String.format("Robo: %s vlr_Stake: %s %s qtd_Candles: %s mart: %s",
////                robo.getClass().getSimpleName(), vlr, "USD", qtd, martingale);
//
//        sendMsgInicioRobo(robo, parametrosUtilizadosRobo, saldoInicial);
//
//    }
//
//    public static void sendMsgInicioRobo(Robo robo, String parametros, BigDecimal saldoInicial) {
//        System.out.printf("parametros: %s\n", parametros);
//        String msg = "Robo_%s saldo_%s iniciou uso com os parametros: %s";
//        String msgTelegram = "Robo_Abrsaldo_7456.33 iniciou uso com os parametros: Robo: Abr vlr_Stake: 0.35 USD qtd_Candles: 10 mart: 100.00";
////        String msgTelegram = String.format(msg, robo.getClass().getSimpleName(),
////                saldoInicial.setScale(2, RoundingMode.HALF_UP), parametros);
//        System.out.printf("msgTelegram: %s\n", msgTelegram);
//        Service_TelegramNotifier.sendMenssage(msgTelegram);
//
//
////        String longcode0 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 58 seconds after contract start time.";
////        String longcode1 = "Win payout if Volatility 10 Index is strictly lower than entry spot at 1 minute 58 seconds after contract start time.";
////        String text = longcode0;
////        int indexMin = text.indexOf("minute"), indexSecond = text.indexOf("seconds");
////        System.out.printf("indexMinute: %s\n", indexMin);
////        System.out.printf("indexSeconds: %s\n", indexSecond);
////        System.out.printf("%s\n", indexMin >= 0 ? text.substring(indexMin - 3, indexMin + 6) : "");
////        System.out.printf("%s\n", indexSecond >= 0 ? text.substring(indexSecond - 4, indexSecond + 7) : "");
//
//
//    }


    public static final String CHAT_ID = "1025551558";
    public static final String TOKEN = "1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc";

    public static void main(String[] args) throws IOException {

        String message = "Hello World!\nfrom Java 11\t\t12";

        String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

        String apiToken = "1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc";
        String chatId = "@BinarySoftBot";
        String text = "Hello world?";

        urlString = String.format(urlString, TOKEN, CHAT_ID, text);

        System.out.printf("urlString: %s\n", urlString);

        //urlString: https://api.telegram.org/bot1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc/sendMessage?chat_id=1025551558&text=Hello world!
        //urlString: https://api.telegram.org/bot1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc/sendMessage?chat_id=1025551558&text=Hello world!
        //urlString: https://api.telegram.org/bot1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc/sendMessage?chat_id=1559825573&text=Hello world!

        Service_TelegramNotifier.sendMenssage(message);

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        String response = sb.toString();
        System.out.printf("response: %s\n", response);

    }


}
