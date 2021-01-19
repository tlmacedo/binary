package br.com.tlmacedo.binary.model.dao;

import br.com.tlmacedo.binary.interfaces.jpa.DAO;
import br.com.tlmacedo.binary.interfaces.jpa.DAOImpl;
import br.com.tlmacedo.binary.model.vo.Transacoes;

public class TransacoesDAO extends DAOImpl<Transacoes, Long> implements DAO<Transacoes, Long> {
    public TransacoesDAO() {
        System.out.printf("DAO:[%s]\n", this.getClass().getSimpleName());
    }
}
