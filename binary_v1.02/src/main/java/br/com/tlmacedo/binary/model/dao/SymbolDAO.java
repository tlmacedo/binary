package br.com.tlmacedo.binary.model.dao;

import br.com.tlmacedo.binary.interfaces.jpa.DAO;
import br.com.tlmacedo.binary.interfaces.jpa.DAOImpl;
import br.com.tlmacedo.binary.model.vo.Symbol;

public class SymbolDAO extends DAOImpl<Symbol, Long> implements DAO<Symbol, Long> {
    public SymbolDAO() {
        System.out.printf("DAO:[%s]\n", this.getClass().getSimpleName());
    }
}
