package br.com.tlmacedo.binary.model.dao;

import br.com.tlmacedo.binary.interfaces.jpa.DAO;
import br.com.tlmacedo.binary.interfaces.jpa.DAOImpl;
import br.com.tlmacedo.binary.model.vo.Transaction;

public class TransactionDAO extends DAOImpl<Transaction, Long> implements DAO<Transaction, Long> {
    public TransactionDAO() {
        System.out.printf("DAO:[%s]\n", this.getClass().getSimpleName());
    }
}
