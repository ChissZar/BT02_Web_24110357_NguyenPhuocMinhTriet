package vn.iotstar.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class Passwords {
    private static final SecureRandom RANDOM = new SecureRandom();
    private Passwords() { }

    public static void validate(String password) {
        if (password == null || password.isEmpty() || password.length() > 128)
            throw new IllegalArgumentException("Mật khẩu không được rỗng và tối đa 128 ký tự.");
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return "pbkdf2$600000$" + Base64.getEncoder().withoutPadding().encodeToString(salt)
                + "$" + Base64.getEncoder().withoutPadding().encodeToString(derive(password, salt, 600000));
    }

    public static boolean matches(String password, String encoded) {
        if (password == null || encoded == null || password.length() > 128) return false;
        if (!encoded.startsWith("pbkdf2$"))
            return MessageDigest.isEqual(password.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    encoded.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try {
            String[] parts = encoded.split("\\$");
            int iterations = Integer.parseInt(parts[1]);
            if (parts.length != 4 || iterations != 600000) return false;
            return MessageDigest.isEqual(Base64.getDecoder().decode(parts[3]),
                    derive(password, Base64.getDecoder().decode(parts[2]), iterations));
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (java.security.GeneralSecurityException exception) {
            throw new IllegalStateException("Password hashing unavailable", exception);
        } finally { spec.clearPassword(); }
    }
}
