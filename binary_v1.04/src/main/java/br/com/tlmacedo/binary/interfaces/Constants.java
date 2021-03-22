package br.com.tlmacedo.binary.interfaces;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

    String REGEX_PONTUACAO = "[ !\"$%&'()*+,-./:;_`{|}]";
    String VERSAO_APP = "v1.04";
    String URL_BINARY = "wss://ws.binaryws.com/websockets/v3?app_id=";
    String APP_BINARY = "23487";
    String CONECT_URL_BINARY = URL_BINARY + APP_BINARY;


    String TELEGRAM_TOKEN = "1559825573:AAEgXlAZv7lDL-9S6C4XskI10-RmVYGPrHc";
    String TELEGRAM_CHAT_ID = "1025551558";
    String TELEGRAM_URL = String.format("https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=",
            TELEGRAM_TOKEN, TELEGRAM_CHAT_ID);


//                    .fromUri("https://api.telegram.org")
//                .path("/{token}/sendMessage")
//                .queryParam("chat_id", CHAT_ID)
//                .queryParam("text", message);


    /**
     * Detalhes de Contrato Padrao
     */
    String PRICE_PROPOSAL_BASIS = "stake";
    String PRICE_PROPOSAL_CURRENCY = "USD";

    /**
     * Style Geral
     */
    String STYLE_TICK_NEUTRO = "-fx-background-color: transparent;";
    String STYLE_TICK_SUBINDO = "-fx-background-color: #4BB4B3;";
    String STYLE_TICK_DESCENDO = "-fx-background-color: #EC3F3F;";
    String STYLE_TIME_NEGOCIANDO = "-fx-text-fill: #3dd936; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #86865a; -fx-background-insets: 1 1 1 -4; -fx-background-radius: 4;";
    String STYLE_TIME_NEGOCIANDO_FALSE = "-fx-background-color: -fx-border-shadow, -fx-background;";
    String STYLE_TICK_NEGOCIANDO_1 = "-fx-text-fill: #038043; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #f3e365; -fx-background-radius: 4;";
    String STYLE_TICK_NEGOCIANDO_2 = "-fx-text-fill: #038043; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #f3e365; -fx-background-radius: 4;";
    String STYLE_TICK_NEGOCIANDO_3 = "-fx-text-fill: #038043; -fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #f3e365; -fx-background-radius: 4;";
    String STYLE_TICK_NEGOCIANDO_FALSE = "-fx-background-color: -fx-border-shadow, -fx-background;";
    String STYLE_TICK_WAIT_BUY = "-fx-font-family: \"Arial\"; -fx-font-weight: bold; -fx-background-color: #86865a; -fx-background-radius: 4;";
    String STYLE_TICK_WAIT_BUY_FALSE = "-fx-background-color: -fx-border-shadow, -fx-background;";
    String STYLE_GRAF_BARRAS_DEFAULT = "-fx-bar-fill: #F2F3F4; -fx-border-color: #1f1e1e;";
    String STYLE_GRAF_BARRAS_DIGITO_MAIOR = "-fx-bar-fill: #4BB4B3; -fx-border-color: #1f1e1e;";
    String STYLE_GRAF_BARRAS_DIGITO_MENOR = "-fx-bar-fill: #EC3F3F; -fx-border-color: #1f1e1e;";

    DateTimeFormatter DTF_DATA_HORA_SEGUNDOS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("pt", "br"));
    DateTimeFormatter DTF_TMODEL_DATA_TRANSACTION = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy", new Locale("pt", "br"));
    DateTimeFormatter DTF_HORA_MINUTOS_SEGUNDOS = DateTimeFormatter.ofPattern("HH:mm:ss", new Locale("pt", "br"));
    DateTimeFormatter DTF_HORA_MINUTOS = DateTimeFormatter.ofPattern("HH:mm", new Locale("pt", "br"));
    DateTimeFormatter DTF_MINUTOS_SEGUNDOS = DateTimeFormatter.ofPattern("mm:ss", new Locale("pt", "br"));

    /**
     * Print Console return messages Ws Binary.
     */
    Boolean CONSOLE_BINARY_ALL = false;
    Boolean CONSOLE_BINARY_ALL_SEM_TICKS = false;

    Boolean CONSOLE_BINARY_CONECTADO = true;
    //    Boolean CONSOLE_BINARY_ACTIVE_SYMBOL = true;
    Boolean CONSOLE_BINARY_AUTHORIZE = false;
    Boolean CONSOLE_BINARY_ERROR = true;
    Boolean CONSOLE_BINARY_TICK = false;
    Boolean CONSOLE_BINARY_PROPOSAL = true;
    Boolean CONSOLE_BINARY_BUY = false;
    Boolean CONSOLE_BINARY_TRANSACTION = true;
    Boolean CONSOLE_BINARY_HISTORY = false;

}
