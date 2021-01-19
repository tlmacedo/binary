package br.com.tlmacedo.binary.services;

import java.time.*;

public class UtilDatas {

    static final int MINUTES_PER_HOUR = 60;
    static final int SECONDS_PER_MINUTE = 60;
    static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;

    public static String getIntervaloData(LocalDate data) {
        return getIntervaloData(data, null);
    }

    public static String getIntervaloTempo(LocalDateTime ldt1, LocalDateTime ldt2) {
        Duration duration = Duration.between(ldt1, ldt2);
        return String.format("%s:%s:%s",
                duration.toHours(),
                duration.toMinutes(),
                duration.toSeconds());
    }

    public static long[] getTime(LocalDateTime dob, LocalDateTime now) {
        LocalDateTime today = LocalDateTime.of(now.getYear(),
                now.getMonthValue(), now.getDayOfMonth(), dob.getHour(), dob.getMinute(), dob.getSecond());
        Duration duration = Duration.between(today, now);

        long seconds = duration.getSeconds();

        long hours = seconds / SECONDS_PER_HOUR;
        long minutes = ((seconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE);
        long secs = (seconds % SECONDS_PER_MINUTE);

        return new long[]{hours, minutes, secs};
    }

    public static String getIntervaloData(LocalDate data1, LocalDate data2) {
        if (data1 == null) return null;
        if (data2 == null) data2 = LocalDate.now();
        Period period = Period.between(data1, data2);
        StringBuilder stbPeriodo = new StringBuilder("");
        if (period.getYears() >= 1) {
            stbPeriodo.append(String.format("%d %s",
                    period.getYears(),
                    period.getYears() > 1 ? "anos" : "ano"));
        }
        if (period.getMonths() >= 1) {
            if (!stbPeriodo.toString().equals("")) stbPeriodo.append(" ");
            stbPeriodo.append(String.format("%d %s",
                    period.getMonths(),
                    period.getMonths() > 1 ? "meses" : "mês"));
        }
        if (period.getDays() >= 1) {
            if (!stbPeriodo.toString().equals("")) stbPeriodo.append(" ");
            stbPeriodo.append(String.format("%d %s",
                    period.getDays(),
                    period.getDays() > 1 ? "dias" : "dia"));
        }
        if (period.isZero() || stbPeriodo.toString().equals(""))
            stbPeriodo.append("hoje");
        return stbPeriodo.toString();
    }

    public static LocalDate getDataVencimento(LocalDate data, Integer dias, boolean diaUtil) {
        if (dias == null) return LocalDate.now();
        if (data == null)
            data = LocalDate.now();
        if (diaUtil) {
            for (int i = 0; i < dias; i++) {
                if (data.plusDays(i).getDayOfWeek() == DayOfWeek.SATURDAY
                        || data.plusDays(i).getDayOfWeek() == DayOfWeek.SUNDAY)
                    dias = dias + 1;
            }
            if (data.plusDays(dias).getDayOfWeek() == DayOfWeek.SATURDAY)
                dias = dias + 2;
            if (data.plusDays(dias).getDayOfWeek() == DayOfWeek.SUNDAY)
                dias = dias + 1;
        }
        return data.plusDays(dias);
    }
}