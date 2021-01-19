package br.com.tlmacedo.binary.services;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Service_Mascara {

    private static String REGEX_PONTUACAO = "[ !\"$%&'()*+,-./:;_`{|}]";

    public static int getNumberOfDecimal(BigDecimal bigDecimal) {
        String string = bigDecimal.stripTrailingZeros().toPlainString();
        int index = string.indexOf(".");
        return index < 0 ? 0 : string.length() - index - 1;
    }

    public static String getValorFormatado(Integer qtdDecimal, BigDecimal valor) {
        String mascara = String.format("%s.%0" + qtdDecimal.intValue() + "d", "###,###", 0);
        return new DecimalFormat(mascara).format(valor);//.replace(",", ";").replace(".", ",").replace(";", ".");
    }

    public static String getValorMoeda(BigDecimal vlr) {
        return new DecimalFormat("#,##0.00").format(vlr).replace(".", ";").replace(",", ".").replace(";", ",");
    }

    public static BigDecimal getValorMoeda(String strVlr, Integer qtdDecimal) {
        return new BigDecimal(strVlr.replace(",", "")).setScale(qtdDecimal, RoundingMode.HALF_UP);
    }

    public static void fieldMask(TextField textField, String tipMascara) {
        boolean porcentagem = tipMascara.contains("%");
        String mascara = tipMascara;
        if (porcentagem)
            mascara = mascara.replace("%", "");
        String finalMascara = mascara;
        textField.textProperty().addListener((ov, o, n) -> {
//            Platform.runLater(() -> {
            StringBuilder resultado = new StringBuilder("");
            int posicao = 0;
//            if (n != null && !n.equals("")) {
            try {
                posicao = textField.getCaretPosition() + ((n.length() > o.length()) ? 1 : 0);
            } catch (Exception ex) {
                posicao = 0;
            }
            String strValue = n != null ? n : "",
                    value = n != null ? n : "",
                    maskDigit = "";
            if (finalMascara.contains("##0.")) {
                textField.setAlignment(Pos.CENTER_RIGHT);
                if (strValue.equals("")) {
                    strValue = "0";
                    if (finalMascara.contains("*"))
                        strValue = finalMascara.substring(finalMascara.indexOf("*")).replaceAll("\\D", "");
                }
                String maskTemp = (finalMascara.contains("*") ? finalMascara.substring(0, finalMascara.indexOf("*")) : finalMascara);
                int qtdMax = maskTemp.replaceAll(".-/]", "").length();
                if (porcentagem) qtdMax++;
                int qtdDecimal = (maskTemp.replaceAll("\\D", "").length() - 1);
                if (strValue.length() > qtdMax)
                    strValue = strValue.substring(0, qtdMax);
                resultado.append(formataNumeroDecimal(strValue, qtdDecimal));
            } else if (finalMascara.contains("(##) ")) {
                textField.setAlignment(Pos.CENTER_RIGHT);
                if (value.length() > 2) {
                    //resultado.append(getTelefone(value));
                } else {
                    resultado.append(value);
                }
//                } else if (finalMascara.equals("##############") || finalMascara.equals("##############")) {
//                    if (value.length() >= 13 && Integer.valueOf(value.substring(1, 2)) <= 6)
//                        setMascara("##############");
//                    else
//                        setMascara("#############");
//                    resultado.append(value);
            } else {
                if (strValue.length() > 0) {
                    int digitado = 0;
                    Pattern p = Pattern.compile(REGEX_PONTUACAO);
                    Matcher m = p.matcher(finalMascara);
                    if (m.find())
                        value = strValue.replaceAll("\\W", "");
                    for (int i = 0; i < finalMascara.length(); i++) {
                        if (digitado < value.length()) {
                            switch ((maskDigit = finalMascara.substring(i, i + 1))) {
                                case "#":
                                case "0":
                                    if (Character.isDigit(value.charAt(digitado))) {
                                        resultado.append(value.substring(digitado, digitado + 1));
                                        digitado++;
                                    }
                                    break;
                                case "U":
                                case "A":
                                case "L":
                                    if ((Character.isLetterOrDigit(value.charAt(digitado))
                                            || Character.isSpaceChar(value.charAt(digitado))
                                            || Character.isDefined(value.charAt(digitado)))) {
                                        if (maskDigit.equals("L"))
                                            resultado.append(value.substring(digitado, digitado + 1).toLowerCase());
                                        else
                                            resultado.append(value.substring(digitado, digitado + 1).toUpperCase());
                                        digitado++;
                                    }
                                    break;
                                case "?":
                                case "*":
                                    resultado.append(value.substring(digitado, digitado + 1));
                                    digitado++;
                                    break;
                                default:
                                    resultado.append(finalMascara.substring(i, i + 1));
                                    break;
                            }
                        }
                    }
                }
            }
//            }
            if (porcentagem) {
                resultado.append("%");
                posicao++;
            }

            int finalPosicao = posicao;
//            Platform.runLater(() -> {
            textField.setText(resultado.toString());
//            Platform.runLater(() -> {
            if (finalMascara.contains(".0"))
                textField.positionCaret(resultado.length());
            else
                textField.positionCaret(finalPosicao);

//            });

        });
//        });
    }

    private static String formataNumeroDecimal(String value, int decimal) {
        String sinal = "";
        if (value.substring(0, 1).equals("-"))
            sinal = "-";
        value = Long.valueOf(value.replaceAll("\\D", "")).toString();
        int addZeros = ((decimal + 1) - value.length());
        if (addZeros > 0)
            value = String.format("%0" + addZeros + "d", 0) + value;

        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 18) + "})$", "$1,$2");
        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 15) + "})$", "$1,$2");
        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 12) + "})$", "$1,$2");
        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 9) + "})$", "$1,$2");
        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 6) + "})$", "$1,$2");
        value = value.replaceAll("(\\d{1})(\\d{" + (decimal + 3) + "})$", "$1,$2");
        if (decimal > 0)
            value = value.replaceAll("(\\d{1})(\\d{" + decimal + "})$", "$1.$2");
        return sinal + value;
    }

    public static String getRegexPontuacao() {
        return REGEX_PONTUACAO;
    }

    public static void setRegexPontuacao(String regexPontuacao) {
        REGEX_PONTUACAO = regexPontuacao;
    }

//    public static String getMascara() {
//        return Mascara;
//    }
//
//    public static void setMascara(String mascara) {
//        Mascara = mascara;
//    }
//
//    public static String getNames() {
//        return Names;
//    }
//
//    public static void setNames(String names) {
//        Names = names;
//    }
}
