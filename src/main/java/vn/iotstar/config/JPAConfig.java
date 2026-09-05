package vn.iotstar.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAConfig {

	private static EntityManagerFactory entityManagerFactory;

	private JPAConfig() {

	}

	public static synchronized EntityManager getEntityManager() {

		if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {

			Map<String, Object> properties = new HashMap<>();

			properties.put("jakarta.persistence.jdbc.user", System.getenv("DB_USER"));

			properties.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));

			entityManagerFactory = Persistence.createEntityManagerFactory("jpa-hibernate-sqlserver", properties);
		}

		return entityManagerFactory.createEntityManager();

	}

	public static synchronized void close() {

		if (entityManagerFactory != null && entityManagerFactory.isOpen()) {

			entityManagerFactory.close();

		}

	}

}