package br.com.tlmacedo.binary.services;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.model.vo.TimeFrame;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Service_DataTime {

    public static Long getIntegerDateNow() {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static StringProperty getCarimboStrProperty(LocalDateTime localDateTime, DateTimeFormatter dtf) {
        return new SimpleStringProperty(getCarimboStr(localDateTime, dtf));
    }

    public static StringProperty getCarimboStrProperty(Integer intDateTime, DateTimeFormatter dtf) {
        return new SimpleStringProperty(getCarimboStr(intDateTime, dtf));
    }

    public static String getCarimboStr(Integer intDateTime, DateTimeFormatter dtf) {
        return getCarimboStr(LocalDateTime.ofInstant(Instant.ofEpochSecond(intDateTime), TimeZone.getDefault().toZoneId()), dtf);
    }

    public static String getCarimboStr(LocalDateTime localDateTime, DateTimeFormatter dtf) {
        return localDateTime.format(dtf);
    }

    public static Integer getTimeMinutosCandle(String longcode) {
        int indexMin = longcode.indexOf("minute");
        int minutes = indexMin < 0 ? 0 : Integer.parseInt(longcode.substring(indexMin - 3, indexMin + 6).replaceAll("\\D", ""));
        return minutes;
    }

    public static Integer getGranularityCandle(String longcode) {
        return getTimeMinutosCandle(longcode) * 60;
    }

    public static Integer getTimeFrame_t_id(String longcode) {
        int granularity = getGranularityCandle(longcode);
        for (int t = 0; t < Operacoes.getTimeFrameObservableList().size(); t++)
            if (Operacoes.getTimeFrameObservableList().get(t).getGranularity() == granularity)
                return t;
        return -1;
    }

    public static TimeFrame getTimeFrameCandle(String longcode) {
        int granularity = getGranularityCandle(longcode);
        for (int t = 0; t < Operacoes.getTimeFrameObservableList().size(); t++)
            if (Operacoes.getTimeFrameObservableList().get(t).getGranularity() == granularity)
                return Operacoes.getTimeFrameObservableList().get(t);
        return null;
    }


}
