package br.com.tlmacedo.binary.controller.estrategias;

import javafx.event.EventHandler;

public interface Estrategia {

    boolean definicaoDeContrato(Integer symbolId);

    boolean gerarContrato(Integer symbolId);

    void acompanhaCompraDeContrato(Integer symbolId);

    void acompanhaVendaDeContratoSeDisponivel(Integer symbolId);

    void resultadoNegociarNovamente(Integer symbolId);

    void botoesSetAction();

    void disparaOrdemCompra(Integer symbolId);

    EventHandler actionContrato(Integer symbolId);

    EventHandler actionCompra(Integer symbolId);

    EventHandler actionStop(Integer symbolId);


}
