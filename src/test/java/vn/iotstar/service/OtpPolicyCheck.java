package vn.iotstar.service;

import java.time.LocalDateTime;
import vn.iotstar.entity.AccountSecurity;
import vn.iotstar.util.Passwords;

public class OtpPolicyCheck {
    private static void check(boolean condition, String name) {
        if (!condition) throw new AssertionError(name);
    }
    public static void main(String[] args) {
        AccountSecurity s = new AccountSecurity();
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 0, 0);
        String hash = Passwords.hash("01234567");
        check(!OtpPolicy.issue(s, "RESET", hash, now), "Inactive cannot reset");
        check(OtpPolicy.issue(s, "ACTIVATE", hash, now), "Issue activation");
        check(!OtpPolicy.issue(s, "ACTIVATE", hash, now.plusSeconds(59)), "Cooldown");
        check(!OtpPolicy.consume(s, "RESET", "01234567", now), "Purpose mismatch");
        check(!OtpPolicy.consume(s, "ACTIVATE", "bad", now), "Invalid format");
        check(s.attempts == 1, "Count failed attempt");
        check(OtpPolicy.issue(s, "ACTIVATE", hash, now.plusSeconds(60)), "Resend");
        check(s.attempts == 1, "Resend preserves attempts");
        check(OtpPolicy.consume(s, "ACTIVATE", "01234567", now.plusSeconds(61)), "Consume OTP");
        check(!OtpPolicy.consume(s, "ACTIVATE", "01234567", now.plusSeconds(62)), "Replay blocked");
        s.activated = true;
        now = now.plusMinutes(2);
        check(OtpPolicy.issue(s, "RESET", hash, now), "Issue reset");
        for (int i = 0; i < 5; i++) check(!OtpPolicy.consume(s, "RESET", "bad", now), "Failed attempt");
        check(!OtpPolicy.consume(s, "RESET", "01234567", now), "Attempt cap");
        check(!OtpPolicy.issue(s, "RESET", hash, now.plusMinutes(1)), "Cannot bypass cap by resend");
        check(OtpPolicy.issue(s, "RESET", hash, now.plusMinutes(10)), "New window");
        check(s.attempts == 0, "Reset expired window attempts");
        check(!OtpPolicy.consume(s, "RESET", "01234567", now.plusMinutes(20)), "Exact expiry boundary");
        System.out.println("PASS: OTP purpose, cooldown, resend, attempt cap, expiry, one-time consumption");
    }
}
