package br.com.tlmacedo.binary.controller.estrategia;

public interface Estrategia {

    void definicaoDeContrato(Integer symbolId);

    void acompanharMovimentoVolatilidade(Integer symbolId);

    void acompanharVendaDeContrato(Integer symbolId);

    void negociarNovamente(Integer symbolId);

    void solicitaCompraContrato(Integer symbolId);

    void pausarContinuarRobo(Integer symbolId);

    void stopEstrategia(Integer symbolId);

    void atualizaNovosParametros(Integer symbolId);

}
