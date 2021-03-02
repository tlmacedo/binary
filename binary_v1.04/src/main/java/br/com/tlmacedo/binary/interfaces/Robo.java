package br.com.tlmacedo.binary.interfaces;

import br.com.tlmacedo.binary.model.enums.ROBOS;

public interface Robo {

    void definicaoDeContrato() throws Exception;

    void monitorarCondicoesParaComprar() throws Exception;

}
