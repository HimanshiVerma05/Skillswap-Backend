package com.dal.skillswap.repository;

import com.dal.skillswap.entities.UserSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    long deleteBySkill_SkillNameIn(List<String> skillNames);
}
