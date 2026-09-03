package vn.iotstar.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;

public class TestJpa {
    public static void main(String[] args) {
        EntityManager entityManager = JPAConfig.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        Category category = new Category();
        category.setCategoryname("Iphone");
        category.setImages("abc.jpg");
        category.setStatus(1);

        Video video = new Video();
        video.setVideoId("v" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        video.setTitle("Test JPA");
        video.setActive(1);
        video.setDescription("Kiểm tra cấu hình JPA");
        video.setPoster("abc.jpg");
        video.setViews(0);
        video.setCategory(category);

        try {
            transaction.begin();

            User user = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.userName = :username", User.class)
                    .setParameter("username", "trungnh")
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                user = new User();
                user.setEmail("trungnh@hcmute.edu.vn");
                user.setUserName("trungnh");
                user.setFullName("Nguyễn Hữu Trung");
                user.setPassWord("123");
                user.setAvatar("avatar.png");
                user.setRoleid(1);
                user.setPhone("0908617108");
                user.setCreatedDate(LocalDateTime.now());
                entityManager.persist(user);
            }

            entityManager.persist(category);
            entityManager.persist(video);
            transaction.commit();

            Long total = entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class)
                    .getSingleResult();
            System.out.println("Cấu hình JPA thành công.");
            System.out.println("Category vừa tạo có ID: " + category.getCategoryid());
            System.out.println("Tổng số Category: " + total);
            System.out.println("Tài khoản đăng nhập: trungnh / 123");
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
            JPAConfig.close();
        }
    }
}
