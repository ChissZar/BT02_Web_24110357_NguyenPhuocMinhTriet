package vn.iotstar.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "account_security")
public class AccountSecurity {
    @Id
    public int userId;
    public boolean activated;
    @Column(length = 100)
    public String otpHash;
    @Column(length = 12)
    public String purpose;
    public LocalDateTime expiresAt;
    public LocalDateTime sentAt;
    public int attempts;
}
