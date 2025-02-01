package com.dal.skillswap.mapper;

import com.dal.skillswap.entities.User;
import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.models.response.Skill;
import com.dal.skillswap.models.response.UserSkill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SkillMapperImpl.class})
public class SkillMapperTest {
    @Autowired
    private SkillMapper skillMapper;

    @Test
    public void testMapToUserModel() {
        User user = getSampleUserEntity();

        com.dal.skillswap.models.response.User responseUser = skillMapper.toUserModel(user);

        assertEquals(responseUser.getEmail(), user.getEmail());

    }

    @Test
    public void testMapToUserSkillModel() {
        com.dal.skillswap.entities.User user = getSampleUserEntity();

        com.dal.skillswap.models.response.UserSkill responseUserSkill = skillMapper.toUserSkillModel(user);

        assertNull(responseUserSkill.getSkill());
    }

    @Test
    public void testMapToSkillModel() {
        com.dal.skillswap.entities.Skill skill = getSampleSkillEntity();

        Skill responseSkill = skillMapper.toSkillModel(skill);

        assertEquals(responseSkill.getSkillName(), skill.getSkillName());
    }

    @Test
    public void testMapToSkillEntity() {
        Skill skill = getSampleSkillModel();

        com.dal.skillswap.entities.Skill skillEntity = skillMapper.toSkillEntity(skill);

        assertEquals(skillEntity.getSkillName(), skill.getSkillName());
    }


    private UserSkill getUserSkillEntity() {
        return new UserSkill(new Skill());
    }

    private com.dal.skillswap.entities.Skill getSampleSkillEntity() {
        return new com.dal.skillswap.entities.Skill("Test Skill");
    }

    private Skill getSampleSkillModel() {
        return new Skill(1L, "Test Skill");
    }

    private User getSampleUserEntity() {
        return new User("test", "test", "test@test.com", "", UserRole.USER, "B3H1B9",
                "test123", 0.0, 0.0);
    }
}
