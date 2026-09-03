package vn.iotstar.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Constant {
    public static final String SESSION_ACCOUNT = "account";
    public static final String COOKIE_REMEMBER = "username";

    private static final String DEFAULT_UPLOAD_DIR =
            Paths.get(System.getProperty("user.home"), "uploads").toString();

    public static final String DIR = System.getProperty(
            "upload.dir",
            System.getenv().getOrDefault("UPLOAD_DIR", DEFAULT_UPLOAD_DIR));

    private Constant() {
    }

    public static Path uploadDirectory() {
        return Paths.get(DIR).toAbsolutePath().normalize();
    }
}
