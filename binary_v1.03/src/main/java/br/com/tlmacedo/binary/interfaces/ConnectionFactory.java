package br.com.tlmacedo.binary.interfaces;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {

    static final String UNIT_NAME = "binary_PU";
    static Map<String, String> properties = new HashMap<String, String>();

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public ConnectionFactory() {

        if (getEntityManagerFactory() == null)
            setEntityManagerFactory(Persistence.createEntityManagerFactory(getUnitName(), getProperties()));

        if (getEntityManager() == null)
            setEntityManager(getEntityManagerFactory().createEntityManager());

    }

    public void closeEntityManager() {
        if (getEntityManager() != null) {
            getEntityManager().close();
            if (getEntityManagerFactory() != null) {
                getEntityManagerFactory().close();
            }
        }
    }

    public static String getUnitName() {
        return UNIT_NAME;
    }

    public static Map<String, String> getProperties() {
        return properties;
    }

    public static void setProperties(Map<String, String> properties) {
        ConnectionFactory.properties = properties;
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
