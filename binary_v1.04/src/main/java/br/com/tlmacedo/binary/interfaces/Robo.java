package br.com.tlmacedo.binary.interfaces;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;

import java.math.BigDecimal;

public interface Robo {

    void cancelarContratos();

    void definicaoDeContrato() throws Exception;

    void monitorarCondicoesParaComprar() throws Exception;

    void gerarNovosContratos(int t_id, int s_id, CONTRACT_TYPE typeContract, Boolean winLoss);

}
