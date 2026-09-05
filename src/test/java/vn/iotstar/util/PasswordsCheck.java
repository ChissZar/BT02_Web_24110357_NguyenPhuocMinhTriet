package vn.iotstar.util;

public class PasswordsCheck {
    private static void check(boolean value) {
        if (!value) throw new AssertionError("Password check failed");
    }
    public static void main(String[] args) {
        String hash = Passwords.hash("Mật khẩu 123!");
        check(hash.length() <= 100);
        check(Passwords.matches("Mật khẩu 123!", hash));
        check(!Passwords.matches("Mật khẩu 123", hash));
        check(!hash.equals(Passwords.hash("Mật khẩu 123!")));
        check(!Passwords.matches(null, hash));
        check(!Passwords.matches("test", "pbkdf2$broken"));
        check(Passwords.matches("123", "123"));
        check(!Passwords.matches("123 ", "123"));
        check(!Passwords.matches("x".repeat(129), hash));
        Passwords.validate("123");
        Passwords.validate("a");
        Passwords.validate("x".repeat(128));
        for (String invalid : new String[]{null, "", "x".repeat(129)}) {
            try { Passwords.validate(invalid); throw new AssertionError("Invalid password accepted"); }
            catch (IllegalArgumentException expected) { }
        }
        System.out.println("PASS: password hashing, random salt, Unicode, wrong password, malformed hash, legacy compatibility, length validation");
    }
}
