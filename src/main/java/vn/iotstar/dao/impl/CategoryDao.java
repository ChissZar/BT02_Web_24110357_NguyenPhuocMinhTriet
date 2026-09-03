package vn.iotstar.dao.impl;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.dao.ICategoryDao;
import vn.iotstar.entity.Category;

public class CategoryDao implements ICategoryDao {
    @Override
    public void insert(Category category) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(category);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void update(Category category) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(category);
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void delete(int cateid) throws Exception {
        EntityManager entityManager = JPAConfig.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Category category = entityManager.find(Category.class, cateid);
            if (category == null) {
                throw new Exception("Không tìm thấy Category có ID " + cateid);
            }
            entityManager.remove(category);
            transaction.commit();
        } catch (Exception exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Category findById(int cateid) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        try {
            return entityManager.find(Category.class, cateid);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Category findByCategoryname(String name) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        String jpql = "SELECT c FROM Category c WHERE c.categoryname = :catename";
        try {
            TypedQuery<Category> query = entityManager.createQuery(jpql, Category.class);
            query.setParameter("catename", name);
            return query.getResultStream().findFirst().orElse(null);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category> findAll() {
        EntityManager entityManager = JPAConfig.getEntityManager();
        try {
            TypedQuery<Category> query = entityManager.createNamedQuery("Category.findAll", Category.class);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category> searchByName(String catname) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        String jpql = "SELECT c FROM Category c WHERE c.categoryname LIKE :catename ORDER BY c.categoryid";
        try {
            TypedQuery<Category> query = entityManager.createQuery(jpql, Category.class);
            query.setParameter("catename", "%" + catname + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        try {
            TypedQuery<Category> query = entityManager.createNamedQuery("Category.findAll", Category.class);
            query.setFirstResult(Math.max(page, 0) * pagesize);
            query.setMaxResults(pagesize);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int count() {
        EntityManager entityManager = JPAConfig.getEntityManager();
        try {
            Query query = entityManager.createQuery("SELECT COUNT(c) FROM Category c");
            return ((Long) query.getSingleResult()).intValue();
        } finally {
            entityManager.close();
        }
    }
}
