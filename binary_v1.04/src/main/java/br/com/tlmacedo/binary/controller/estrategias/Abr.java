package br.com.tlmacedo.binary.controller.estrategias;

import br.com.tlmacedo.binary.controller.Operacoes;
import br.com.tlmacedo.binary.interfaces.Robo;
import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;
import br.com.tlmacedo.binary.model.enums.TICK_TIME;
import br.com.tlmacedo.binary.model.vo.Proposal;
import br.com.tlmacedo.binary.services.Service_Alert;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

public class Abr extends Operacoes implements Robo {

    static Proposal[][][] proposal = new Proposal[TICK_TIME.values().length][getSymbolObservableList().size()][2];

    @Override
    public void setNameEstrategiaRobo(ROBOS nameRobo) {
        setROBO_Selecionado(nameRobo);
    }

    @Override
    public void definicaoDeContrato() {

        System.out.printf("inicioDefinicaoDeContrato\n");
        try {
            BigDecimal vlr;
            Integer qtd;
            Service_Alert alert = new Service_Alert();
            alert.setCabecalho("Stake");
            alert.setContentText("Qual o valor da stake padrão para operações?");
            vlr = new BigDecimal(alert.alertTextField("#,##0.00", "0.35", "").get());

            alert = new Service_Alert();
            alert.setContentText("Espera quantas candles seguidas em pull ou call?");
            qtd = Integer.valueOf(alert.alertTextField("#,##0.*0", "5", "").get()
                    .replaceAll("\\D", ""));

            for (TICK_TIME tick_time : TICK_TIME.values()) {
                if (!getTimeAtivo()[tick_time.getCod()].getValue()) continue;
                getVlrStkPadrao()[tick_time.getCod()].setValue(vlr);
                getQtdCandles()[tick_time.getCod()].setValue(qtd);
                for (int i = 0; i < getSymbolObservableList().size(); i++) {
                    getVlrStkContrato()[tick_time.getCod()][i]
                            .setValue(getVlrStkPadrao()[tick_time.getCod()].getValue());
                    for (int j = 0; j < 2; j++) {
                        getProposal()[tick_time.getCod()][i][j] = new Proposal();
                        gerarContrato(tick_time, getSymbolObservableList().get(i), CONTRACT_TYPE.toEnum(j));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public Abr() {

        for (TICK_TIME tick_time : TICK_TIME.values()) {
            if (getTimeAtivo()[tick_time.getCod()].getValue()) {
                getVlrStkPadrao()[tick_time.getCod()] = new SimpleObjectProperty<>();
                getQtdCandles()[tick_time.getCod()] = new SimpleIntegerProperty();
                for (int i = 0; i < getSymbolObservableList().size(); i++)
                    getVlrStkContrato()[tick_time.getCod()][i] = new SimpleObjectProperty<>();
            }
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

    public static Proposal[][][] getProposal() {
        return proposal;
    }

    public static void setProposal(Proposal[][][] proposal) {
        Abr.proposal = proposal;
    }
}
