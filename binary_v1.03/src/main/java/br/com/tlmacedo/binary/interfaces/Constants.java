package br.com.tlmacedo.binary.interfaces;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public interface Constants {

    String REGEX_PONTUACAO = "[ !\"$%&'()*+,-./:;_`{|}]";
    String VERSAO_APP = "v1.03";
    String URL_BINARY = "wss://ws.binaryws.com/websockets/v3?app_id=";
    String APP_BINARY = "23487";
    String CONECT_URL_BINARY = URL_BINARY + APP_BINARY;

    /**
     * Detalhes de Contrato Padrao
     */
    String PRICE_PROPOSAL_BASIS = "stake";
    String PRICE_PROPOSAL_CURRENCY = "USD";

    /**
     * Style Geral
     */
    String STYLE_TICK_SUBINDO = "-fx-background-color: #4BB4B3; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;";
    String STYLE_TICK_DESCENDO = "-fx-background-color: #EC3F3F; -fx-text-fill: #ffffff; -fx-background-radius: 8 0 0 8; -fx-background-insets: 0;";
    String STYLE_TICK_NEGOCIANDO = "-fx-background-color: #fffd03; -fx-text-fill: #000000;";
    String STYLE_TICK_NEGOCIANDO_FALSE = ".lbl_informacao.right";
    String STYLE_GRAF_BARRAS_DEFAULT = "-fx-bar-fill: #F2F3F4; -fx-border-color: #1f1e1e;";
    String STYLE_GRAF_BARRAS_DIGITO_MAIOR = "-fx-bar-fill: #4BB4B3; -fx-border-color: #1f1e1e;";
    String STYLE_GRAF_BARRAS_DIGITO_MENOR = "-fx-bar-fill: #EC3F3F; -fx-border-color: #1f1e1e;";

    DateTimeFormatter DTF_DATA_HORA_SEGUNDOS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", new Locale("pt", "br"));
    DateTimeFormatter DTF_TMODEL_DATA_TRANSACTION = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy", new Locale("pt", "br"));
    DateTimeFormatter DTF_HORA_MINUTOS_SEGUNDOS = DateTimeFormatter.ofPattern("HH:mm:ss", new Locale("pt", "br"));
    DateTimeFormatter DTF_MINUTOS_SEGUNDOS = DateTimeFormatter.ofPattern("mm:ss", new Locale("pt", "br"));

    /**
     * Print Console return messages Ws Binary.
     */
    Boolean CONSOLE_BINARY_ALL = false;
    Boolean CONSOLE_BINARY_ALL_SEM_TICKS = true;

    Boolean CONSOLE_BINARY_CONECTADO = false;
    Boolean CONSOLE_BINARY_AUTHORIZE = false;
    Boolean CONSOLE_BINARY_ERROR = false;
    Boolean CONSOLE_BINARY_TICK = false;
    Boolean CONSOLE_BINARY_PROPOSAL = false;
    Boolean CONSOLE_BINARY_BUY = false;
    Boolean CONSOLE_BINARY_TRANSACTION = false;
    Boolean CONSOLE_BINARY_HISTORY = false;

}
