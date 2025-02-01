package com.dal.skillswap.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "password_token")
public class PasswordResetToken {
    private static final int EXPIRATION_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    private LocalDateTime tokenExpiryDate;

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.tokenExpiryDate = LocalDateTime.now().plusDays(EXPIRATION_DAYS);
    }

    public PasswordResetToken() {

    }

    public void setTokenExpiryDate() {
        this.tokenExpiryDate = LocalDateTime.now().plusDays(EXPIRATION_DAYS);
    }

}
