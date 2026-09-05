package vn.iotstar.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import jakarta.persistence.*;
import vn.iotstar.config.JPAConfig;
import vn.iotstar.entity.*;
import vn.iotstar.util.Passwords;

public class AccountService {
    private final OtpMailer mailer;
    private final Supplier<EntityManager> managers;
    private static final SecureRandom RANDOM = new SecureRandom();

    public AccountService() { this(new OtpMailer(), JPAConfig::getEntityManager); }

    AccountService(OtpMailer mailer, Supplier<EntityManager> managers) {
        this.mailer = mailer;
        this.managers = managers;
    }

    private <T> T transaction(Function<EntityManager, T> work) {
        EntityManager em = managers.get();
        try {
            em.getTransaction().begin();
            T result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException exception) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw exception;
        } finally { em.close(); }
    }

    public static String email(String value) {
        String result = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (result.length() > 100 || !result.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+"))
            throw new IllegalArgumentException("Email không hợp lệ.");
        return result;
    }

    public void register(String username, String name, String email, String password) {
        String address = email(email);
        String login = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
        String fullName = name == null ? "" : name.trim();
        if (!login.matches("[a-z0-9_.]{3,50}"))
            throw new IllegalArgumentException("Tên đăng nhập gồm 3–50 ký tự: chữ không dấu, số, dấu chấm hoặc gạch dưới.");
        if (fullName.isEmpty() || fullName.length() > 100) throw new IllegalArgumentException("Họ tên phải có 1–100 ký tự.");
        Passwords.validate(password);
        mailer.checkConfiguration();
        String hash = Passwords.hash(password);
        transaction(em -> {
            long count = em.createQuery("SELECT COUNT(u) FROM User u WHERE LOWER(u.userName)=:name OR LOWER(u.email)=:email", Long.class)
                    .setParameter("name", login).setParameter("email", address).getSingleResult();
            if (count > 0) throw new IllegalArgumentException("Tên đăng nhập hoặc email đã được sử dụng. Nếu chưa kích hoạt, chọn gửi lại OTP.");
            em.persist(new RegistrationKey("name:" + login));
            em.persist(new RegistrationKey("email:" + address));
            em.flush();
            User user = new User();
            user.setUserName(login); user.setEmail(address); user.setFullName(fullName);
            user.setPassWord(hash); user.setRoleid(1); user.setCreatedDate(LocalDateTime.now());
            em.persist(user); em.flush();
            AccountSecurity security = new AccountSecurity();
            security.userId = user.getId(); security.activated = false;
            em.persist(security);
            return null;
        });
        requestOtp(address, "ACTIVATE");
    }

    private User byEmail(EntityManager em, String email) {
        var users = em.createQuery("SELECT u FROM User u WHERE LOWER(u.email)=:email", User.class)
                .setParameter("email", email).setMaxResults(2).getResultList();
        if (users.size() != 1) return null;
        return em.find(User.class, users.get(0).getId(), LockModeType.PESSIMISTIC_WRITE);
    }

    private AccountSecurity security(EntityManager em, User user) {
        AccountSecurity result = em.find(AccountSecurity.class, user.getId());
        if (result == null) {
            result = new AccountSecurity(); result.userId = user.getId(); result.activated = true;
            em.persist(result);
        }
        return result;
    }

    public void requestOtp(String email, String purpose) {
        String address = email(email);
        if (!purpose.equals("ACTIVATE") && !purpose.equals("RESET")) throw new IllegalArgumentException("Invalid purpose");
        mailer.checkConfiguration();
        String code = String.format(Locale.ROOT, "%08d", RANDOM.nextInt(100000000));
        String hash = Passwords.hash(code);
        boolean send = transaction(em -> {
            User user = byEmail(em, address);
            if (user == null) return false;
            AccountSecurity s = security(em, user);
            return OtpPolicy.issue(s, purpose, hash, LocalDateTime.now());
        });
        if (send) mailer.send(address, code, purpose);
    }

    public boolean confirm(String email, String purpose, String code, String password) {
        String address = email(email);
        if (!purpose.equals("ACTIVATE") && !purpose.equals("RESET")) return false;
        if (purpose.equals("RESET")) Passwords.validate(password);
        String newHash = purpose.equals("RESET") ? Passwords.hash(password) : null;
        return transaction(em -> {
            User user = byEmail(em, address);
            if (user == null) return false;
            AccountSecurity s = security(em, user);
            if (!OtpPolicy.consume(s, purpose, code, LocalDateTime.now())) return false;
            if (purpose.equals("ACTIVATE")) s.activated = true;
            else user.setPassWord(newHash);
            return true;
        });
    }

    public User login(String username, String password) {
        return transaction(em -> {
            var matches = em.createQuery("SELECT u FROM User u WHERE u.userName=:name", User.class)
                    .setParameter("name", username).getResultList();
            if (matches.size() != 1) return null;
            User user = em.find(User.class, matches.get(0).getId(), LockModeType.PESSIMISTIC_WRITE);
            if (!Passwords.matches(password, user.getPassWord())) return null;
            AccountSecurity s = security(em, user);
            if (!s.activated) return null;
            if (!user.getPassWord().startsWith("pbkdf2$")) user.setPassWord(Passwords.hash(password));
            return user;
        });
    }
}
