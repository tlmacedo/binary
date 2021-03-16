package br.com.tlmacedo.binary.services;


import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.ACTION;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.vo.Transacoes;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

import static br.com.tlmacedo.binary.interfaces.Constants.*;

public class Service_TelegramNotifier extends Operacoes {
    static URL url;
    static URLConnection conn;
    static String retorno;

    public static void sendMenssageAndPrintResult(String msg) {

        try {
            sendMenssage(msg);
            printRetorno();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMenssage(String msg) {

        String urlString = TELEGRAM_URL + msg;

        try {
            urlString = urlString.replace("\n", "%0a")
                    .replace("\t", "    ");
            setUrl(new URL(urlString));
            setConn(getUrl().openConnection());
            setRetorno(getStrRetorno(getConn().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void printRetorno() throws IOException {
        System.out.printf("Telegram_sendMenssage: %s\n", getRetorno());
    }

    public static String getStrRetorno(InputStream result) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(result);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        return sb.toString();
    }

    public static void sendMsgInicioRobo() {
        String msgModelo = "Iniciando: %s\n%s\n%s";
        String msgDtHora = LocalDateTime.now().format(DTF_HORA_MINUTOS_SEGUNDOS);
        String msgConta = String.format("conta: %s\tsaldoInicial: %s%s", getContaToken(),
                Service_Mascara.getValorMoeda(getSaldoInicial()), getAuthorize().getCurrency());
        String msgParam = getParametrosUtilizadosRobo();
        String msgTelegram = String.format(msgModelo, msgDtHora, msgConta, msgParam);
        sendMenssage(msgTelegram);
    }


    public static void sendMsgTransacoesAction(Transacoes transacao, BigDecimal balance, ACTION action) {
        String msg = "Tf_%s Symbol_%s Action_%s cType_%s Stake_%s balance_%s";
        String msgTelegram = String.format(msg, transacao.gettFrame(), transacao.getSymbol(),
                action, CONTRACT_TYPE.valueOf(transacao.getContract_type().toUpperCase()),
                transacao.getStakeCompra(), balance.setScale(2, RoundingMode.HALF_UP));
        sendMenssage(msgTelegram);
    }

    public static URL getUrl() {
        return url;
    }

    public static void setUrl(URL url) {
        Service_TelegramNotifier.url = url;
    }

    public static URLConnection getConn() {
        return conn;
    }

    public static void setConn(URLConnection conn) {
        Service_TelegramNotifier.conn = conn;
    }

    public static String getRetorno() {
        return retorno;
    }

    public static void setRetorno(String retorno) {
        Service_TelegramNotifier.retorno = retorno;
    }
}