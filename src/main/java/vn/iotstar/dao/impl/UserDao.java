package vn.iotstar.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.IUserDao;
import vn.iotstar.entity.User;

public class UserDao implements IUserDao {
    @Override
    public User findById(int id) {
        EntityManager em = JPAConfig.getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public User updateProfile(int id, String fullName, String phone, String avatar) {
        EntityManager em = JPAConfig.getEntityManager();
        var transaction = em.getTransaction();
        try {
            transaction.begin();
            User user = em.find(User.class, id);
            if (user == null) throw new IllegalArgumentException("Tài khoản không còn tồn tại");
            user.setFullName(fullName);
            user.setPhone(phone);
            if (avatar != null) user.setAvatar(avatar);
            transaction.commit();
            return user;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) transaction.rollback();
            throw exception;
        } finally {
            em.close();
        }
    }

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
