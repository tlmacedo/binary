package br.com.tlmacedo.binary.interfaces;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;
import java.util.List;

public interface DAO<T, I extends Serializable> {

    T merger(T entity);

    void remove(T entity);

    void transactionRollback();

    T getById(Class<T> tClass, I pk);

    List<T> getAll(Class<T> tClass, String busca, String orderBy);

    EntityManager getEntityManager();

    EntityTransaction getTransaction();

    void closeTransaction();

//    void transactionBegin();
//
//    void setTransactionPersist(T entity);
//
//    void transactionCommit();

}
