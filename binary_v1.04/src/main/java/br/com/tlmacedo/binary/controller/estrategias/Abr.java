package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.services.Service_Alert;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

public class Abr extends Operacoes implements Robo {


    @Override
    public void definicaoDeContrato() {

        System.out.printf("inicioDefinicaoDeContrato\n");
        try {
            Service_Alert alert = new Service_Alert();
            alert.setCabecalho("Stake");
            alert.setContentText("Qual o valor da stake padrão para operações?");
            for (TICK_TIME tick_time : TICK_TIME.values())
                getVlrStake()[tick_time.getCod()].setValue(
                        new BigDecimal(alert.alertTextField("#,##0.00", "0.35", "").get()));

            alert = new Service_Alert();
            alert.setContentText("Espera quantas candles seguidas em pull ou call?");
            for (TICK_TIME tick_time : TICK_TIME.values())
                getQtdCandles()[tick_time.getCod()].setValue(Integer.valueOf(alert.alertTextField("#,##0.*0", "5", "").get()
                        .replaceAll("\\D", "")));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Abr() {
        for (TICK_TIME tick_time : TICK_TIME.values()) {
            System.out.printf("001-tick_time00%s: %s[%s]\n", tick_time.getCod(), tick_time, getTimeAtivo()[tick_time.getCod()]);

            if (getTimeAtivo()[tick_time.getCod()].getValue()) {
                getVlrStake()[tick_time.getCod()] = new SimpleObjectProperty<>();
                getQtdCandles()[tick_time.getCod()] = new SimpleIntegerProperty();
            }
            System.out.printf("002-tick_time00%s: %s[%s]\n", tick_time.getCod(), tick_time, getTimeAtivo()[tick_time.getCod()]);
        }
    }

    /**
     * Getters and Setters!!!
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     */


}
