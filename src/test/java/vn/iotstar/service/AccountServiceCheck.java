package vn.iotstar.service;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import vn.iotstar.entity.*;
import vn.iotstar.util.Passwords;

public class AccountServiceCheck {
    static class Mailbox extends OtpMailer {
        String code;
        boolean fail;
        public void checkConfiguration() { }
        public void send(String email, String otp, String purpose) {
            if (fail) throw new IllegalStateException("Simulated SMTP failure");
            code = otp;
        }
    }
    static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
    }
    static void edit(EntityManagerFactory factory, int id, java.util.function.Consumer<AccountSecurity> change) {
        var em = factory.createEntityManager();
        try {
            em.getTransaction().begin();
            change.accept(em.find(AccountSecurity.class, id));
            em.getTransaction().commit();
        } finally { em.close(); }
    }
    static int id(EntityManagerFactory factory, String name) {
        var em = factory.createEntityManager();
        try { return em.createQuery("SELECT u.id FROM User u WHERE u.userName=:name", Integer.class)
                .setParameter("name", name).getSingleResult(); }
        finally { em.close(); }
    }
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("accounts-test");
        try {
            Mailbox mail = new Mailbox();
            AccountService service = new AccountService(mail, factory::createEntityManager);
            service.register("tester", "Nguyễn Văn An", "Tester@example.com", "Password123!");
            String activation = mail.code;
            int id = id(factory, "tester");
            check(activation.matches("[0-9]{8}"), "OTP format");
            check(service.login("tester", "Password123!") == null, "Inactive cannot log in");
            check(!service.confirm("tester@example.com", "RESET", activation, "OtherPassword!"), "Purpose isolation");
            check(!service.confirm("tester@example.com", "ACTIVATE", "bad", null), "Reject wrong OTP");
            check(service.confirm("tester@example.com", "ACTIVATE", activation, null), "Activate");
            check(!service.confirm("tester@example.com", "ACTIVATE", activation, null), "Single use");
            User loggedIn = service.login("tester", "Password123!");
            check(loggedIn != null && loggedIn.getPassWord().startsWith("pbkdf2$"), "Hashed login");
            check(loggedIn.getPassWord().length() <= 100, "Hash fits schema");
            edit(factory, id, s -> s.sentAt = LocalDateTime.now().minusMinutes(2));
            service.requestOtp("tester@example.com", "RESET");
            String reset = mail.code;
            service.requestOtp("tester@example.com", "RESET");
            check(reset.equals(mail.code), "Resend cooldown");
            for (int i = 0; i < 5; i++) check(!service.confirm("tester@example.com", "RESET", "bad", "NewPassword!"), "Wrong attempt");
            check(!service.confirm("tester@example.com", "RESET", reset, "NewPassword!"), "Attempt limit");
            edit(factory, id, s -> { s.sentAt = LocalDateTime.now().minusMinutes(11); s.expiresAt = LocalDateTime.now().minusSeconds(1); });
            check(!service.confirm("tester@example.com", "RESET", reset, "NewPassword!"), "Expired OTP");
            service.requestOtp("tester@example.com", "RESET");
            check(service.confirm("tester@example.com", "RESET", mail.code, "NewPassword!"), "Password reset");
            check(!service.confirm("tester@example.com", "RESET", mail.code, "AnotherPassword!"), "Reset replay");
            check(service.login("tester", "Password123!") == null, "Old password rejected");
            check(service.login("tester", "NewPassword!") != null, "New password works");
            try { service.register("another", "An", "TESTER@example.com", "Password123!"); throw new AssertionError("Duplicate email"); }
            catch (IllegalArgumentException expected) { }
            String previous = mail.code;
            service.requestOtp("absent@example.com", "RESET");
            check(previous.equals(mail.code), "Unknown email no mail");
            mail.fail = true;
            try { service.register("mailfail", "An", "fail@example.com", "Password123!"); throw new AssertionError("SMTP failure"); }
            catch (IllegalStateException expected) { }
            check(service.login("mailfail", "Password123!") == null, "SMTP failure leaves inactive");
            check(Passwords.matches("123", "123"), "Legacy password compatibility");
            System.out.println("PASS: registration, activation, login, password hashing, OTP purpose/expiry/replay/attempt limits/cooldown, reset, duplicate email, SMTP failure");
        } finally { factory.close(); }
    }
}
