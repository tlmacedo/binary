package br.com.tlmacedo.binary.services;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class Service_DataHoraCarimbo {

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


}
