package vn.iotstar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "registration_keys")
public class RegistrationKey {
    @Id
    @Column(length = 110)
    public String identityKey;
    public RegistrationKey() { }
    public RegistrationKey(String key) { identityKey = key; }
}
