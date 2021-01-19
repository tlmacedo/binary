package br.com.tlmacedo.binary.model.dao;

import br.com.tlmacedo.binary.interfaces.jpa.DAO;
import br.com.tlmacedo.binary.interfaces.jpa.DAOImpl;
import br.com.tlmacedo.binary.model.vo.Token;

public class TokenDAO extends DAOImpl<Token, Long> implements DAO<Token, Long> {
}
