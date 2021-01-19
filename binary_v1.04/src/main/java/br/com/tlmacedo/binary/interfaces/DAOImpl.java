package br.com.tlmacedo.binary.interfaces;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

public class DAOImpl<T, I extends Serializable> implements DAO<T, I> {

    ConnectionFactory conexao;

    public DAOImpl() {
        if (getConexao() == null)
            setConexao(new ConnectionFactory());
    }

    @Override
    public T merger(T entity) {
        try {
            if (!getConexao().getEntityManager().getTransaction().isActive())
                getConexao().getEntityManager().getTransaction().begin();
            T saved = getConexao().getEntityManager().merge(entity);
            getConexao().getEntityManager().getTransaction().commit();
            return saved;
        } catch (Exception ex) {
            ex.printStackTrace();
            //transactionRollback();
            return null;
        }
    }

    @Override
    public void remove(T entity) {
        getConexao().getEntityManager().getTransaction().begin();
        getConexao().getEntityManager().remove(entity);
        getConexao().getEntityManager().getTransaction().commit();
    }

//    @Override
//    public void transactionBegin() {
//        getTransaction().begin();
//    }
//
//    @Override
//    public void setTransactionPersist(T entity) {
//        getConexao().getEntityManager().persist(entity);
//    }
//
//    @Override
//    public void transactionCommit() {
//        try {
//            getTransaction().commit();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void transactionRollback() {
        try {
            if (getTransaction() == null)
                getTransaction().rollback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public T getById(Class<T> tClass, I pk) {
        return getConexao().getEntityManager().find(tClass, pk);
    }

    @Override
    public List<T> getAll(Class<T> tClass, String busca, String orderBy) {
        Query select;
        String sql = String.format("from %s%s%s",
                tClass.getSimpleName(),
                (busca != null)
                        ? String.format(" where %s", busca) : "",
                (orderBy != null)
                        ? String.format(" order by %s", orderBy) : ""
        );
        select = getConexao().getEntityManager().createQuery(sql);
        return select.getResultList();
    }

    @Override
    public EntityManager getEntityManager() {
        if (getConexao() == null)
            setConexao(new ConnectionFactory());
        return getConexao().getEntityManager();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getConexao().getEntityManager().getTransaction();
    }

    @Override
    public void closeTransaction() {
        getConexao().getEntityManager().close();
    }

    public ConnectionFactory getConexao() {
        return conexao;
    }

    public void setConexao(ConnectionFactory conexao) {
        this.conexao = conexao;
    }
}
