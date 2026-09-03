package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.entity.User;

public class UserDao implements IUserDao {
    @Override
    public User get(String username) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        String jpql = "SELECT u FROM User u WHERE u.userName = :username";
        try {
            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            query.setParameter("username", username);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            entityManager.close();
        }
    }
}
