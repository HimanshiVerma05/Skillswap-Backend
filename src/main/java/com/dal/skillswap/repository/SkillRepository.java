package com.dal.skillswap.repository;

import com.dal.skillswap.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findBySkillName(String skillName);

    boolean existsBySkillName(String skillName);

    long deleteBySkillNameIn(List<String> skillNames);
}
