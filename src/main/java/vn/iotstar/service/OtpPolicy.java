package vn.iotstar.service;

import java.time.LocalDateTime;
import vn.iotstar.entity.AccountSecurity;
import vn.iotstar.util.Passwords;

public final class OtpPolicy {
    private OtpPolicy() { }

    public static boolean issue(AccountSecurity state, String purpose, String hash, LocalDateTime now) {
        if (state.activated != purpose.equals("RESET")) return false;
        if (state.sentAt != null && now.isBefore(state.sentAt.plusSeconds(60))) return false;
        if (state.attempts >= 5 && state.expiresAt != null && now.isBefore(state.expiresAt)) return false;
        if (state.expiresAt == null || !now.isBefore(state.expiresAt)) state.attempts = 0;
        state.otpHash = hash;
        state.purpose = purpose;
        state.sentAt = now;
        state.expiresAt = now.plusMinutes(10);
        return true;
    }

    public static boolean consume(AccountSecurity state, String purpose, String code, LocalDateTime now) {
        if (state.otpHash == null || !purpose.equals(state.purpose) || state.expiresAt == null
                || !now.isBefore(state.expiresAt) || state.attempts >= 5) return false;
        state.attempts++;
        if (code == null || !code.matches("[0-9]{8}") || !Passwords.matches(code, state.otpHash)) return false;
        state.otpHash = null;
        state.purpose = null;
        state.expiresAt = null;
        return true;
    }
}
