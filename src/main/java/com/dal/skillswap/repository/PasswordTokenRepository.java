package com.dal.skillswap.repository;

import com.dal.skillswap.entities.PasswordResetToken;
import com.dal.skillswap.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    @Query("SELECT p.user from PasswordResetToken p inner join p.user u where p.token=?1")
    Optional<User> findUserByToken(String token);
}
