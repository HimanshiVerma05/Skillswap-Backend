package com.dal.skillswap.mapper;

import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.mapper.SkillMapperImpl;
import com.dal.skillswap.mapper.UserSkillMapper;
import com.dal.skillswap.mapper.UserSkillMapperImpl;
import com.dal.skillswap.models.response.Skill;
import com.dal.skillswap.models.response.User;
import com.dal.skillswap.models.response.UserSkill;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserSkillMapperImpl.class})
public class UserSkillMapperTest {

    @Autowired
    private UserSkillMapper userSkillMapper;

    @Test
    public void testMapToSkillModel() {
        com.dal.skillswap.entities.Skill skillEntity = getSampleSkillEntity();

        Skill skillModel = userSkillMapper.toSkillModel(skillEntity);

        assertEquals(skillModel.getSkillName(), skillEntity.getSkillName());
    }

    @Test
    public void testMapToUserModel() {
        com.dal.skillswap.entities.User userEntity = getSampleUserEntity();

        User userModel = userSkillMapper.toUserModel(userEntity);

        assertEquals(userModel.getEmail(), userEntity.getEmail());
    }

    @Test
    public void testMapToUserSkillModel() {
        com.dal.skillswap.entities.UserSkill userSkillEntity = getSampleUserSkillEntity();

        UserSkill userSkillModel = userSkillMapper.toUserSkillModel(userSkillEntity);

        assertEquals(userSkillModel.getSkill().getSkillName(), userSkillEntity.getSkill().getSkillName());
    }


    private com.dal.skillswap.entities.Skill getSampleSkillEntity() {
        return new com.dal.skillswap.entities.Skill("Java");
    }

    private com.dal.skillswap.entities.User getSampleUserEntity() {
        return new com.dal.skillswap.entities.User("test", "test", "test@test.com", "", UserRole.USER, "B3H1B9",
                "test123", 0.0, 0.0);
    }
    private com.dal.skillswap.entities.UserSkill getSampleUserSkillEntity() {
        com.dal.skillswap.entities.User userEntity = getSampleUserEntity();
        com.dal.skillswap.entities.Skill skillEntity = getSampleSkillEntity();
        return new com.dal.skillswap.entities.UserSkill(getSampleUserEntity(), getSampleSkillEntity());
    }
}
