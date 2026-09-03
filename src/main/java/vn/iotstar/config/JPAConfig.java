package vn.iotstar.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAConfig {
    private static EntityManagerFactory entityManagerFactory;

    private JPAConfig() {
    }

    public static synchronized EntityManager getEntityManager() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            entityManagerFactory = Persistence.createEntityManagerFactory("jpa-hibernate-mysql");
        }
        return entityManagerFactory.createEntityManager();
    }

    public static synchronized void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
