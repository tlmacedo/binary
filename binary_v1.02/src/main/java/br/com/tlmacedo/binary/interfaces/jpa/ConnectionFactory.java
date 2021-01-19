package br.com.tlmacedo.binary.interfaces.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {

    public static final String UNIT_NAME = "binary_PU";

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    public ConnectionFactory() {
        Map<String, String> properties = new HashMap<String, String>();

        if (getEntityManagerFactory() == null)
            setEntityManagerFactory(Persistence.createEntityManagerFactory(getUnitName(), properties));

        if (getEntityManager() == null)
            setEntityManager(getEntityManagerFactory().createEntityManager());

    }

    public void closeEntityManager() {
        if (getEntityManager() != null) {
            getEntityManager().close();
            if (getEntityManagerFactory() != null)
                getEntityManagerFactory().close();
        }
    }

    public static String getUnitName() {
        return UNIT_NAME;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
