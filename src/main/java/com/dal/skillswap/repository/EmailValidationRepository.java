package com.dal.skillswap.repository;

import com.dal.skillswap.entities.EmailValidation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailValidationRepository extends JpaRepository<EmailValidation, Long> {

    List<EmailValidation> getEmailValidationsByCode(String code);
}
