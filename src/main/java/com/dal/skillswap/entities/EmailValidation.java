package com.dal.skillswap.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "email_validation")
@NoArgsConstructor
public class EmailValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private String code;

    public EmailValidation(User user, String code) {
        this.code = code;
        this.user = user;
    }

}
