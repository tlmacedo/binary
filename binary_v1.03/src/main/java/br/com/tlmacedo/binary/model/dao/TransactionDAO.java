package br.com.tlmacedo.binary.model.dao;

import br.com.tlmacedo.binary.interfaces.DAO;
import br.com.tlmacedo.binary.interfaces.DAOImpl;
import br.com.tlmacedo.binary.model.vo.Transaction;

public class TransactionDAO extends DAOImpl<Transaction, Long> implements DAO<Transaction, Long> {
}
