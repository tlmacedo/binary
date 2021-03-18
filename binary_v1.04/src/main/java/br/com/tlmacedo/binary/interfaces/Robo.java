package br.com.tlmacedo.binary.interfaces;

import br.com.tlmacedo.binary.model.enums.CONTRACT_TYPE;
import br.com.tlmacedo.binary.model.enums.ROBOS;

import java.math.BigDecimal;

public interface Robo {

    boolean variaveisIniciais();

    boolean definicaoDeContrato() throws Exception;

    void gerarContratosPendentes(Integer ft_id, Integer fs_id);

    void monitorarCondicoesParaComprar() throws Exception;

    void gerarNovosContratos(int t_id, int s_id, Integer typeContract, Integer proposal_id);

    void cancelarContratosNaoUsados(int t_id, int s_id);

}
