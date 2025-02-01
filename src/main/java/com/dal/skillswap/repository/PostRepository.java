package com.dal.skillswap.repository;

import com.dal.skillswap.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findBySkill_SkillId(Long skillId);

    List<Post> findByUser_Id(Long userId);

    List<Post> findBySkill_SkillIdAndUser_Id(Long skillId, Long userId);
}
